package tests.jsonServiceTest.objectMapperTest;

import org.services.jsonService.json.formatter.JsonFormat;
import org.services.jsonService.json.mapper.JsonMapper;
import org.services.jsonService.json.navigator.JsonNavigator;
import org.services.jsonService.json.mapper.ObjectMapper;
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
        String jsonDummyClass =
                """
                            {
                                "name": "Alex",
                                "age": 24
                            }
                        """;

        String jsonDummyClassWithArray =
                """
                            {
                                "name": "Alex",
                                "age": 24,
                                "strings": [
                                    "Dummy2",
                                    "Dummy3"
                                ]
                            }
                        """;

        String jsonDummyClassWithObject =
                """
                            {
                                "name": "Ion",
                                "age": 25,
                                "isProgrammer": false,
                                "dummyClass": {
                                    "name": "Alex",
                                    "age": 24
                                },
                                "object": {
                                    "name": "Alex",
                                    "age": 24,
                                    "strings": [
                                        "Dummy2",
                                        "Dummy3"
                                    ]
                                }
                            }
                        """;


        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance()
        );

        //* Test JSON -> Object
        JsonParser jsonParser = JsonParser.getInstance();
        DummyClass dummyClassTemplate = new DummyClass("Alex", 24);
        List<String> strings = new LinkedList<>();
        strings.add("Dummy2");
        strings.add("Dummy3");
        DummyClassWithArray dummyClassWithArrayTemplate = new DummyClassWithArray("Alex", 24, strings);
        DummyClassWithObject dummyClassWithObjectTemplate = new DummyClassWithObject("Ion", 25, false, dummyClassTemplate, dummyClassWithArrayTemplate);


        JsonType jsonTypeDummyParsed = jsonParser.parse(jsonDummyClass);
        DummyClass dummyClass = jsonParser.mapObject((JsonObject) jsonTypeDummyParsed, DummyClass.class);
        System.out.println("TEST 1: " + (dummyClass.equals(dummyClassTemplate) ? "PASSED!" : "FAILED!"));

        JsonType jsonTypeDummyClassWithArray = jsonParser.parse(jsonDummyClassWithArray);
        JsonObject jsonObjectDCWA = (JsonObject) jsonTypeDummyClassWithArray;
        DummyClassWithArray dummyClassWithArray = jsonParser.mapObject(jsonObjectDCWA, DummyClassWithArray.class);
        System.out.println("TEST 2: " + (dummyClassWithArray.equals(dummyClassWithArrayTemplate) ? "PASSED!" : "FAILED!"));


        JsonType jsonTypeDummyClassWithObject = jsonParser.parse(jsonDummyClassWithObject);
        JsonObject jsonObjectDCWO = (JsonObject) jsonTypeDummyClassWithObject;
        DummyClassWithObject dummyClassWithObject = jsonParser.mapObject(jsonObjectDCWO, DummyClassWithObject.class);
        System.out.println("TEST 3: " + (dummyClassWithObject.equals(dummyClassWithObjectTemplate) ? "PASSED!" : "FAILED!"));
        //* Test Object -> JSON

    }
}
