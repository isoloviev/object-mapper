import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.util.StringUtils;
import models.FieldMapper;
import models.FieldMapperEx;
import models.ObjectModel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.*;

public class ConverterEx {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private ObjectModel parse(JsonNode node, Class clazz) {
        try {

            Map<FieldMapperEx, Field> fields = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(FieldMapperEx.class)) {
                    FieldMapperEx mapperAnnotation = field.getAnnotation(FieldMapperEx.class);
                    fields.put(mapperAnnotation, field);
                }
            }

            ObjectModel model = (ObjectModel) clazz.newInstance();

            for (FieldMapperEx key : fields.keySet()) {
                JsonNode jsonNode = findNode(key.value(), node);
                if (jsonNode == null)
                    continue;
                LOGGER.info(jsonNode);

                if (jsonNode.isArray()) {
                    Iterator<JsonNode> elements = node.elements();
                    while (elements.hasNext()) {
                        JsonNode arrNodeItem = elements.next().findPath(key.field());
                        if (!arrNodeItem.isMissingNode())
                            setValueToField(fields.get(key), model, arrNodeItem);
                    }
                } else {
                    setValueToField(fields.get(key), model, jsonNode);
                }
            }

            return model;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(e);
            return null;
        }
    }

    private JsonNode findNode(String key, JsonNode node) {
        JsonNode findNode = node;
        for (String k : key.split("\\.")) {
            findNode = findNode.path(k);
        }
        return findNode.isMissingNode() ? null : findNode;
    }

    private void setValueToField(Field fld, ObjectModel inst, JsonNode node) {
        try {
            String methodFieldName = fld.getName().replaceFirst(fld.getName().substring(0, 1), fld.getName()
                    .substring(0, 1).toUpperCase());
            Method method = inst.getClass().getMethod("set" + methodFieldName, fld.getType());
            method.setAccessible(true);
            if (fld.getType() == Integer.class)
                method.invoke(inst, node.asInt());
            else if (fld.getType() == Double.class)
                method.invoke(inst, node.asInt());
            else if (fld.getType() == Long.class)
                method.invoke(inst, node.asLong());
            else if (fld.getType() == Boolean.class)
                method.invoke(inst, node.asBoolean());
            else if (fld.getType() == List.class) {

                // check that list is not instantiated
                Method getList = inst.getClass().getMethod("get" + methodFieldName);
                getList.setAccessible(true);
                Object list = getList.invoke(inst);
                if (list == null) {
                    list = ArrayList.class.newInstance();
                }
                Method add = List.class.getDeclaredMethod("add", Object.class);
                add.invoke(list, node.asText());

                // set list
                method.invoke(inst, list);

            } else if (fld.getType() == String.class)
                method.invoke(inst, node.asText());
            else
                LOGGER.warn("Setter is not defined for [" + fld.getType() + "]");
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            LOGGER.error(e);
        }
    }

    public ObjectModel init(String response, Class clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return parse(jsonNode, clazz);
    }
}
