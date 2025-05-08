package tests.jsonServiceTest.objectMapperTest;

import org.services.jsonService.json.formatter.JsonFormat;
import org.services.jsonService.json.mapper.JsonMapper;
import org.services.jsonService.json.mapper.JsonPrimitiveParser;
import org.services.jsonService.json.mapper.ObjectMapper;
import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.properties.JsonKey;
import org.services.jsonService.json.properties.JsonProperty;
import org.services.jsonService.json.properties.JsonValue;
import org.services.jsonService.json.types.JsonObject;
import org.services.jsonService.json.types.JsonType;
import org.services.jsonService.json.validator.JsonValidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

        JsonPrimitiveParser.init();
        JsonMapper.init(
                JsonPrimitiveParser.getInstance()
        );
        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance(),
                JsonMapper.getInstance(),
                JsonPrimitiveParser.getInstance()
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
        List<JsonProperty> dummyClassProperties = new ArrayList<>();
        dummyClassProperties.add(new JsonProperty(new JsonKey("name"), new JsonValue("\"Alex\"")));
        dummyClassProperties.add(new JsonProperty(new JsonKey("age"), new JsonValue(24)));
        JsonObject jsonObjectDummyClassTemplate = new JsonObject(dummyClassProperties.toArray(new JsonProperty[0]));


        JsonType jsonObjectForm = jsonParser.mapJson(dummyClassTemplate);
        System.out.println("TEST 4: " + (jsonObjectDummyClassTemplate.equals(jsonObjectForm) ? "PASSED!" : "FAILED!"));
        System.out.println("TEST 4.1: " + (jsonObjectDummyClassTemplate.toString().equals(jsonObjectForm.toString()) ? "PASSED!" : "FAILED!"));
        JsonType jsonObjectFormArray = jsonParser.mapJson(dummyClassWithArrayTemplate);
        JsonType jsonObjectFormObject = jsonParser.mapJson(dummyClassWithObjectTemplate);
        System.out.println(jsonObjectForm);
        System.out.println(jsonObjectFormArray);
        System.out.println(jsonObjectFormObject.toString());
    }
}
