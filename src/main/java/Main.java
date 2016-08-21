import models.Extension;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        String response = "{\"info\": {\"firstName\": \"Sean\", \"directNumbers\": [{\"number\": 1234567890, \"id\": 123}]}}";
        Converter converter = new Converter();
        Extension model = (Extension) converter.init(response, Extension.class);
        LOGGER.info(model);

        ConverterEx converterEx = new ConverterEx();
        model = (Extension) converterEx.init(response, Extension.class);
        LOGGER.info(model);
    }

}
