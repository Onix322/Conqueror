package tests.jsonServiceTest.objectMapperTest;

import org.services.jsonService.json.formatter.JsonFormat;
import org.services.jsonService.json.navigator.JsonNavigator;
import org.services.jsonService.json.objectMapper.JsonPrimitiveCast;
import org.services.jsonService.json.objectMapper.ObjectMapper;
import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.properties.JsonValue;
import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonType;
import org.services.jsonService.json.validator.JsonValidator;

import java.util.Arrays;
import java.util.List;

public class ObjectMapperTest {
    public static void main(String[] args) throws NoSuchMethodException {
        String json =
                """
                            {
                                "name":"Alex",
                                "age":24,
                                "address": {
                                    "street":"ion"
                                },
                                "prefer": [
                                    "pp",
                                    1,
                                    2.23,
                                    false
                                ]
                            }
                        """;


        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance()
        );

        JsonParser jsonParser = JsonParser.getInstance();

        JsonType jsonType = jsonParser.parse(json);
        DummyClass testObject = jsonParser.map(jsonType, DummyClass.class);

        JsonValue jsonValue = JsonNavigator.navigate(jsonType, "prefer");

        if(jsonValue.get() instanceof JsonArray jsonArray){
            List.of(jsonArray.get()).forEach(System.out::println);
        }
        System.out.println(jsonValue.get());
        System.out.println(testObject);
    }
}
