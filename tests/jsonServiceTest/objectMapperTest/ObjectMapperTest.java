package tests.jsonServiceTest.objectMapperTest;

import org.services.jsonService.json.formatter.JsonFormat;
import org.services.jsonService.json.navigator.JsonNavigator;
import org.services.jsonService.json.objectMapper.ObjectMapper;
import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.properties.JsonValue;
import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonObject;
import org.services.jsonService.json.types.JsonType;
import org.services.jsonService.json.validator.JsonValidator;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectMapperTest {
    public static void main(String[] args) throws ReflectiveOperationException {
        String jsonDummy =
                """
                            {
                                "name": "Alex",
                                "age": 24,
                                "address": {
                                    "street": "Bioengineering's",
                                    "neighbors": [
                                        "Alex",
                                        "Marian"
                                    ]
                                },
                                "prefer": [
                                    "pp",
                                    1,
                                    2.23,
                                    false
                                ]
                            }
                        """;

        String jsonForInstance =
                """
                            {
                                "name": "Alex",
                                "age": 24,
                                "address": {
                                    "street": "Bioengineering's",
                                    "neighbors": [
                                        "Alex",
                                        "Marian"
                                    ]
                                },
                                "prefer": [
                                    1,
                                    2,
                                    3
                                ]
                            }
                        """;


        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance()
        );

        JsonParser jsonParser = JsonParser.getInstance();

        JsonType jsonType = jsonParser.parse(jsonForInstance);
        DummyClass testObject = jsonParser.mapObject((JsonObject) jsonType, DummyClass.class);

        JsonValue jsonValue = JsonNavigator.navigate(jsonType, "prefer");
        JsonArray jsonArray = jsonValue.get(JsonArray.class);
        Collection<Integer> integers = jsonParser.mapArray(jsonArray, LinkedList.class);


        System.out.println(integers.getClass().getTypeName());
        System.out.println(integers);
        System.out.println(testObject);
    }
}
