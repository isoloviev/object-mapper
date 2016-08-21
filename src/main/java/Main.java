import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        String response = "{\"info\": {\"firstName\": \"Sean\", \"directPhones\": [{\"phone\": 1234567890}]}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        Extension model = (Extension) parse(jsonNode, Extension.class);

        LOGGER.info(model);

    }


    private static ObjectModel parse(JsonNode node, Class clazz) {

        return result;
    }

    public class ObjectModel {

    }

    public class Extension extends ObjectModel {

        @Field(name = "info.firstName")
        private String firstName;

        @Field(name = "info.lastName")
        private String lastName;

        @Field(name = "info.directNumbers.number")
        private List<String> numbers;

        public Extension() {
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public List<String> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<String> numbers) {
            this.numbers = numbers;
        }
    }
}
