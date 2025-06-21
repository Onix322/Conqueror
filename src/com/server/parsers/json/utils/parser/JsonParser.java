package src.com.server.parsers.json.utils.parser;

import src.com.server.exceptions.JsonNotValid;
import src.com.server.exceptions.JsonPropertyFormatError;
import src.com.server.parsers.json.utils.coordinate.Coordinate;
import src.com.server.parsers.json.utils.mapper.JsonMapper;
import src.com.server.parsers.primitiveParser.PrimitiveParser;
import src.com.server.parsers.json.utils.validator.JsonValidator;
import src.com.server.parsers.json.utils.formatter.JsonFormat;
import src.com.server.parsers.json.utils.formatter.JsonFormatedString;
import src.com.server.parsers.json.utils.mapper.ObjectMapper;
import src.com.server.parsers.json.utils.properties.JsonKey;
import src.com.server.parsers.json.utils.properties.JsonProperty;
import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;
import src.com.server.annotations.component.Component;

import java.util.*;

@Component
public final class JsonParser implements Parser{

    private final JsonValidator JSON_VALIDATOR;
    private final JsonFormat JSON_FORMAT;
    private final ObjectMapper OBJECT_MAPPER;
    private final JsonMapper JSON_MAPPER;
    private final PrimitiveParser PRIMITIVE_PARSER;

    private JsonParser(JsonValidator jsonValidator, JsonFormat jsonFormat, ObjectMapper objectMapper, JsonMapper jsonMapper, PrimitiveParser primitiveParser) {
        this.JSON_FORMAT = jsonFormat;
        this.OBJECT_MAPPER = objectMapper;
        this.JSON_VALIDATOR = jsonValidator;
        this.JSON_MAPPER = jsonMapper;
        this.PRIMITIVE_PARSER = primitiveParser;
    }

    public <T> T mapObject(JsonObject jsonType, Class<T> type) throws Exception {
        return this.OBJECT_MAPPER.mapObject(jsonType, type);
    }
    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception {
        return this.OBJECT_MAPPER.mapArray(jsonArray, collectionClass);
    }

    public JsonType mapJson(Object o) throws Exception {
        if (o instanceof Collection<?>) {
            return this.JSON_MAPPER.toJsonArray((Collection<?>) o);
        } else {
            return this.JSON_MAPPER.toJsonObject(o);
        }
    }

    public JsonType parse(String string) {
        //format
        JsonFormatedString jsonFormatedString = JSON_FORMAT.format(string);

        //validate
        if (!JSON_VALIDATOR.isValidJsonValue(jsonFormatedString)) {
            throw new JsonNotValid("Json is not valid!");
        }

        //parsing && assembling
        Map<String, String> rawObjects = this.gatherRawObjects(jsonFormatedString);
        Map<String, JsonType> parsedObjects = this.parseTypes(rawObjects);

        return this.assembler(parsedObjects);
    }

    /*
     * With the help of coordinates will cut the objects and save them in a map (k= object id , v = object) witch will be the result
     */
    private Map<String, String> gatherRawObjects(JsonFormatedString json) {

        int numberOfObjects = this.countObjects(json);
        Map<String, String> rawObjects = new LinkedHashMap<>();
        Coordinate objectLocation = this.findDeepestObjectBounds(json);
        String lineJson = json.getString();

        while (numberOfObjects > 0 && objectLocation.getEndIndex() > objectLocation.getStartIndex()) {
            String id = "\"o-" + rawObjects.size() + "\"";
            String value = lineJson.substring(objectLocation.getStartIndex(), objectLocation.getEndIndex() + 1);
            rawObjects.put(id, value);
            lineJson = lineJson.replace(value, id);
            JsonFormatedString jsonFormatedString = JSON_FORMAT.format(lineJson);
            objectLocation = this.findDeepestObjectBounds(jsonFormatedString);
            numberOfObjects--;
        }

        return rawObjects;
    }

    private Map<String, JsonType> parseTypes(Map<String, String> objects) {

        Set<String> keys = objects.keySet();
        Map<String, JsonType> parsedTypes = new LinkedHashMap<>();

        for (String k : keys) {
            String v = objects.get(k);
            switch (v.charAt(0)) {
                case '[' -> {
                    JsonArray jsonArray = this.parseArray(v);
                    parsedTypes.put(k, jsonArray);
                }
                case '{' -> {
                    JsonObject jsonObjectImpl = this.parseObject(v);
                    parsedTypes.put(k, jsonObjectImpl);
                }
            }
        }
        return parsedTypes;
    }

    private JsonObject parseObject(String stringObject) {
        String regexObjects = "^\\{\\s*(?:\"[a-zA-Z_][a-zA-Z0-9_]*\"\\s*:\\s*(?:\"(?:\\\\[\"\\\\/bfnrt]|\\\\u[0-9a-fA-F]{4}|[^\"\\\\])*\"|-?\\d+(?:\\.\\d+)?|true|false|null)\\s*(?:,\\s*\"[a-zA-Z_][a-zA-Z0-9_]*\"\\s*:\\s*(?:\"(?:\\\\[\"\\\\/bfnrt]|\\\\u[0-9a-fA-F]{4}|[^\"\\\\])*\"|-?\\d+(?:\\.\\d+)?|true|false|null)\\s*)*)?\\s*}$";

        if (!stringObject.matches(regexObjects)) {
            throw new JsonNotValid("This is not valid object: " + stringObject);
        }
        List<JsonProperty> properties = new LinkedList<>();

        List<Coordinate> coordinates = this.getElementsLocation(stringObject);
        for (Coordinate coordinate : coordinates) {
            String value = stringObject.substring(coordinate.getStartIndex(), coordinate.getEndIndex());
            JsonProperty jsonValue = this.splitStringProperty(value);
            properties.add(jsonValue);
        }

        return new JsonObject(properties.toArray(new JsonProperty[0]));
    }

    private JsonArray parseArray(String stringArray) {
        String regexArrays = "^\\[\\s*(?:\"(?:\\\\[\"\\\\/bfnrt]|\\\\u[0-9a-fA-F]{4}|[^\"\\\\])*\"|-?\\d+(?:\\.\\d+)?|true|false|null)(?:\\s*,\\s*(?:\"(?:\\\\[\"\\\\/bfnrt]|\\\\u[0-9a-fA-F]{4}|[^\"\\\\])*\"|-?\\d+(?:\\.\\d+)?|true|false|null))*\\s*]$";

        if (!stringArray.matches(regexArrays)) {
            throw new JsonNotValid("This is not valid array: " + stringArray);
        }

        List<JsonValue> values = new LinkedList<>();
        List<Coordinate> coordinates = this.getElementsLocation(stringArray);

        for (Coordinate coordinate : coordinates) {
            String value = stringArray.substring(coordinate.getStartIndex() + 1, coordinate.getEndIndex());
            JsonValue jsonValue;
            if(this.PRIMITIVE_PARSER.isPrimitive(value)){
                jsonValue = new JsonValue(this.PRIMITIVE_PARSER.parse(value));
            } else {
                jsonValue = new JsonValue(value);
            }
            values.add(jsonValue);
        }
        return new JsonArray(values.toArray(new JsonValue[0]));
    }

    private JsonProperty splitStringProperty(String stringProperty) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] keyValue = new String[2];
        int currentIndex = 0;

        for (int i = 1; i < stringProperty.length(); i++) {
            char c = stringProperty.charAt(i);

            if (c == ':' || i == stringProperty.length() - 1 && currentIndex == 0) {
                keyValue[0] = stringBuilder.toString().trim();
                stringBuilder.setLength(0);
                currentIndex++;
                continue;
            }
            stringBuilder.append(c);
        }

        if (currentIndex > 0) {
            keyValue[1] = stringBuilder.toString().trim();
            stringBuilder.setLength(0);
        }

        if (keyValue[0] == null || keyValue[1] == null) {
            throw new JsonPropertyFormatError("Property format is invalid: " + stringProperty);
        }

        if(this.PRIMITIVE_PARSER.isPrimitive(keyValue[1])){
            return new JsonProperty(
                    new JsonKey(keyValue[0]),
                    new JsonValue(this.PRIMITIVE_PARSER.parse(keyValue[1]))
            );
        }
        return new JsonProperty(
                new JsonKey(keyValue[0]),
                new JsonValue(keyValue[1])
        );
    }

    /**
     * Replaces placeholder references (e.g., "o-0", "o-1") in parsed JSON structures
     * with their actual JsonType values (JsonObject or JsonArray).
     *
     * <p>During parsing, nested JSON structures are extracted and replaced with string IDs
     * like "o-0", "o-1" to simplify processing. This method resolves those placeholders and
     * recursively reconstructs the full object hierarchy by replacing them with the actual
     * parsed objects from the provided map.</p>
     *
     * @param parseObj a map containing all parsed JSON objects and arrays,
     *                 where keys are placeholder IDs (e.g., "o-0") and values are JsonType instances
     * @return the root JsonType object (the last added entry), fully assembled with no remaining placeholders
     */
    private JsonType assembler(Map<String, JsonType> parseObj) {

        for (Map.Entry<String, JsonType> entry : parseObj.entrySet()) {
            JsonType value = entry.getValue();

            if (value instanceof JsonObject jsonObject) {
                for (JsonProperty property : jsonObject.get()) {
                    JsonValue rawVal = property.value();
                    if (rawVal.get() instanceof String strVal && strVal.matches("\"o-\\d+\"")) {
                        JsonType resolved = parseObj.get(strVal);
                        if (resolved != null) {
                            property.setValue(new JsonValue(resolved));
                        }
                    }
                }
            }

            if (value instanceof JsonArray jsonArray) {
                for (JsonValue val : jsonArray.get()) {
                    Object rawVal = val.get();
                    if (rawVal instanceof String strVal && strVal.matches("\"o-\\d+\"")) {
                        JsonType resolved = parseObj.get(strVal);
                        if (resolved != null) {
                            val.setValue(new JsonValue(resolved));
                        }
                    }
                }
            }
        }

        List<String> keys = parseObj.keySet().stream().toList();
        return parseObj.get(keys.getLast());
    }

    /*
     * Locates the properties/values in objects / arrays and returns a List wit all coordinates
     */
    private List<Coordinate> getElementsLocation(String string) {

        List<Coordinate> coordinates = new LinkedList<>();
        int tempIndex = 0;
        boolean inStringChar = false;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            inStringChar = JSON_VALIDATOR.toggleInString(c, i, string, inStringChar);
            if (inStringChar) continue;

            if (c == ',' || c == ']' || c == '}') {
                Coordinate coordinate = Coordinate.builder()
                        .setStartIndex(tempIndex)
                        .setEndIndex(i)
                        .build();

                coordinates.add(coordinate);
                tempIndex = i;
            }
        }

        return coordinates;
    }

    /*
     * Counting objects
     */
    private Integer countObjects(JsonFormatedString json) {
        int number = 0;
        String lineJson = json.getString();
        boolean inStringChar = false;

        for (int i = 0; i < lineJson.length(); i++) {
            char c = lineJson.charAt(i);

            inStringChar = JSON_VALIDATOR.toggleInString(c, i, lineJson, inStringChar);
            if (inStringChar) continue;

            if (c == '{' || c == '[') {
                number++;
            }
        }

        return number;
    }

    /*
     * This method finds the deepest object in the string and returns it's location (as coordinates)
     * Coordinates will be used to cut the object from string , add it in a map with k= object id , v = object
     */
    private Coordinate findDeepestObjectBounds(JsonFormatedString json) {
        int maxDepth = 0;
        int currentDepth = 0;
        int startIndex = -1;
        int endIndex = -1;
        int tempStartIndex = -1;
        boolean inStringChar = false;
        String lineJson = json.getString();

        for (int i = 0; i < lineJson.length(); i++) {
            char c = lineJson.charAt(i);
            inStringChar = JSON_VALIDATOR.toggleInString(c, i, lineJson, inStringChar);
            if (inStringChar) continue;

            switch (c) {
                case '{', '[' -> {
                    currentDepth++;
                    if (currentDepth > maxDepth) {
                        maxDepth = currentDepth;
                        tempStartIndex = i;
                    }
                }
                case '}', ']' -> {
                    if (currentDepth == maxDepth && startIndex == -1) {
                        startIndex = tempStartIndex;
                        endIndex = i;
                    }
                    currentDepth--;
                }
            }
        }

        return Coordinate.builder()
                .setEndIndex(endIndex)
                .setStartIndex(startIndex)
                .build();
    }
}
