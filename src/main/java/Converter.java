import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.util.StringUtils;
import models.FieldMapper;
import models.ObjectModel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Converter {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private ObjectModel parse(JsonNode node, Class clazz) {
        try {
            return parse(node, (ObjectModel) clazz.newInstance(), new ArrayList<String>());
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(e);
            return null;
        }
    }

    private ObjectModel parse(JsonNode node, ObjectModel inst, List<String> path) {
        LOGGER.info("Path: " + path);
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            LOGGER.info("JSON Field name: " + fieldName);
            if (node.get(fieldName).isArray()) {
                LOGGER.info("isArray");
                path.add(fieldName);
                Iterator<JsonNode> elements = node.get(fieldName).elements();
                while (elements.hasNext()) {
                    JsonNode arrNodeItem = elements.next();
                    LOGGER.info("Els: " + arrNodeItem);

                    inst = parse(arrNodeItem, inst, path);
                }
            } else if (node.get(fieldName).isObject()) {
                path.add(fieldName);
                inst = parse(node.get(fieldName), inst, path);
            } else {
                Field fld = findField(fieldName, inst, StringUtils.join(path, ".") + "." + fieldName);
                if (fld != null) {
                    try {
                        String methodFieldName = fld.getName().replaceFirst(fld.getName().substring(0, 1), fld.getName()
                                .substring(0, 1).toUpperCase());
                        Method method = inst.getClass().getMethod("set" + methodFieldName, fld.getType());
                        method.setAccessible(true);
                        if (fld.getType() == Integer.class)
                            method.invoke(inst, node.get(fieldName).asInt());
                        else if (fld.getType() == Double.class)
                            method.invoke(inst, node.get(fieldName).asInt());
                        else if (fld.getType() == Long.class)
                            method.invoke(inst, node.get(fieldName).asLong());
                        else if (fld.getType() == Boolean.class)
                            method.invoke(inst, node.get(fieldName).asBoolean());
                        else if (fld.getType() == List.class) {

                            // check that list is not instantiated
                            Method getList = inst.getClass().getMethod("get" + methodFieldName);
                            getList.setAccessible(true);
                            Object list = getList.invoke(inst);
                            if (list == null) {
                                list = ArrayList.class.newInstance();
                            }
                            Method add = List.class.getDeclaredMethod("add", Object.class);
                            add.invoke(list, node.get(fieldName).asText());

                            // set list
                            method.invoke(inst, list);

                        } else if (fld.getType() == String.class)
                            method.invoke(inst, node.get(fieldName).asText());
                        else
                            LOGGER.warn("Setter is not defined for [" + fld.getType() + "]");
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                }
            }
        }
        return inst;
    }

    private Field findField(String name, ObjectModel inst, String pathMapper) {
        for (Field field : inst.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldMapper.class)) {
                FieldMapper mapperAnnotation = field.getAnnotation(FieldMapper.class);
                if (mapperAnnotation.value().equals(pathMapper)) {
                    LOGGER.debug("Found field: " + field.getName() + ", " + field.getType() + ", " + mapperAnnotation.value());
                    return field;
                }

            }
        }
        LOGGER.warn("Field was not found");
        return null;
    }

    public ObjectModel init(String response, Class clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return parse(jsonNode, clazz);
    }
}
