package src.com.server.parsers.json.utils.parser;

import src.com.server.annotations.component.Component;
import src.com.server.exceptions.JsonNotValid;
import src.com.server.exceptions.JsonPropertyFormatError;
import src.com.server.parsers.json.utils.coordinate.Coordinate;
import src.com.server.parsers.json.utils.formatter.JsonFormat;
import src.com.server.parsers.json.utils.formatter.JsonFormatedString;
import src.com.server.parsers.json.utils.mapper.JsonMapper;
import src.com.server.parsers.json.utils.mapper.ObjectMapper;
import src.com.server.parsers.json.utils.properties.JsonKey;
import src.com.server.parsers.json.utils.properties.JsonProperty;
import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;
import src.com.server.parsers.json.utils.validator.JsonValidator;
import src.com.server.parsers.primitiveParser.PrimitiveParser;

import java.util.*;

/**
 * JsonParser is responsible for parsing JSON strings into structured objects
 * and vice versa. It validates, formats, and maps JSON data to Java objects
 * and collections.
 */
@Component
public final class JsonParser implements Parser {

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

    /**
     * Maps a JsonObject to a Java object of the specified type.
     *
     * @param jsonType The JsonObject to map.
     * @param type     The class type to map to.
     * @param <T>      The type parameter for the mapped object.
     * @return An instance of the specified type mapped from the JsonObject.
     * @throws Exception If mapping fails.
     */
    public <T> T mapObject(JsonObject jsonType, Class<T> type) throws Exception {
        return this.OBJECT_MAPPER.mapObject(jsonType, type);
    }

    /**
     * Maps a JsonArray to a Collection of the specified type.
     *
     * @param jsonArray       The JsonArray to map.
     * @param collectionClass The class type of the collection to map to.
     * @param <E>             The type parameter for the elements in the collection.
     * @return A Collection of elements mapped from the JsonArray.
     * @throws Exception If mapping fails.
     */
    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception {
        return this.OBJECT_MAPPER.mapArray(jsonArray, collectionClass);
    }

    /**
     * Maps a Java object or collection to a JsonType (JsonObject or JsonArray).
     *
     * @param o The object or collection to map.
     * @return A JsonType representation of the input object.
     * @throws Exception If mapping fails.
     */
    public JsonType mapJson(Object o) throws Exception {
        if (o instanceof Collection<?>) {
            return this.JSON_MAPPER.toJsonArray((Collection<?>) o);
        } else {
            return this.JSON_MAPPER.toJsonObject(o);
        }
    }

    /**
     * Parses a JSON string into a structured JsonType object (JsonObject or JsonArray).
     *
     * @param string The JSON string to parse.
     * @return A JsonType representation of the parsed JSON.
     * @throws JsonNotValid If the JSON string is not valid.
     */
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


    /**
     * Extracts and replaces nested JSON objects from a formatted JSON string.
     *
     * <p>This method identifies the deepest nested JSON objects within the input string,
     * assigns them unique placeholder IDs (e.g., "o-0", "o-1"), and replaces the original
     * objects in the string with these placeholders. The extracted objects are stored
     * in a map where the keys are the placeholder IDs and the values are the corresponding
     * JSON object strings.</p>
     *
     * @param json The formatted JSON string to process.
     * @return A map containing placeholder IDs as keys and the corresponding JSON object strings as values.
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

    /**
     * Parses a map of JSON object strings into a map of JsonType objects.
     *
     * <p>This method iterates over the provided map of JSON object strings, determines
     * whether each string represents a JSON object or array, and parses it into the
     * corresponding JsonType (JsonObject or JsonArray). The parsed objects are stored
     * in a new map with the same keys as the input map.</p>
     *
     * @param objects A map containing JSON object strings as values.
     * @return A map containing the parsed JsonType objects.
     */
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

    /**
     * Parses a JSON string representing an object into a JsonObject.
     *
     * <p>This method validates the JSON string format, extracts properties,
     * and constructs a JsonObject from the parsed properties.</p>
     *
     * @param stringObject The JSON string to parse.
     * @return A JsonObject representation of the parsed JSON string.
     * @throws JsonNotValid If the JSON string is not valid.
     */
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

    /**
     * Parses a JSON string representing an array into a JsonArray.
     *
     * <p>This method validates the JSON string format, extracts values,
     * and constructs a JsonArray from the parsed values.</p>
     *
     * @param stringArray The JSON string to parse.
     * @return A JsonArray representation of the parsed JSON string.
     * @throws JsonNotValid If the JSON string is not valid.
     */
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
            if (this.PRIMITIVE_PARSER.isPrimitive(value)) {
                jsonValue = new JsonValue(this.PRIMITIVE_PARSER.parse(value));
            } else {
                jsonValue = new JsonValue(value);
            }
            values.add(jsonValue);
        }
        return new JsonArray(values.toArray(new JsonValue[0]));
    }

    /**
     * Splits a string property into a JsonProperty object.
     *
     * <p>This method takes a string formatted as "key: value" and splits it into
     * a JsonKey and JsonValue. It handles both primitive values and string values.</p>
     *
     * @param stringProperty The string property to split.
     * @return A JsonProperty object containing the key and value.
     * @throws JsonPropertyFormatError If the property format is invalid.
     */
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

        if (this.PRIMITIVE_PARSER.isPrimitive(keyValue[1])) {
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

    /**
     * Extracts the locations of JSON elements (objects and arrays) in a formatted JSON string.
     *
     * <p>This method identifies the start and end indices of each JSON element in the input string,
     * allowing for proper parsing and handling of nested structures.</p>
     *
     * @param string The formatted JSON string to analyze.
     * @return A list of Coordinate objects representing the start and end indices of each JSON element.
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

    /**
     * Counts the number of JSON objects and arrays in a formatted JSON string.
     *
     * <p>This method iterates through the string, counting occurrences of '{' and '['
     * while ignoring characters inside string literals.</p>
     *
     * @param json The formatted JSON string to analyze.
     * @return The count of JSON objects and arrays found in the string.
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

    /**
     * Finds the deepest JSON object or array bounds in a formatted JSON string.
     *
     * <p>This method traverses the string to determine the maximum depth of nested
     * objects or arrays and returns the start and end indices of the deepest one.</p>
     *
     * @param json The formatted JSON string to analyze.
     * @return A Coordinate object containing the start and end indices of the deepest object or array.
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
