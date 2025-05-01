package org.json.parser_v2;

import org.exepltions.JsonNotValid;

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonParser {

    private final NumberFormat NUMBER_FORMAT;
    private final JsonValidator JSON_VALIDATOR;
    private final JsonFormat JSON_FORMAT;

    private JsonParser(NumberFormat numberFormat, JsonValidator jsonValidator, JsonFormat jsonFormat) {
        this.JSON_FORMAT = jsonFormat;
        this.JSON_VALIDATOR = jsonValidator;
        this.NUMBER_FORMAT = numberFormat;
    }

    private static class Init {
        private static JsonParser INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat, JsonValidator jsonValidator, JsonFormat jsonFormat) {
        if (JsonParser.Init.INSTANCE == null) {
            JsonParser.Init.INSTANCE = new JsonParser(numberFormat, jsonValidator, jsonFormat);
        }
    }

    public static JsonParser getInstance() {
        if (JsonParser.Init.INSTANCE == null) {
            throw new IllegalStateException("JsonParser not initialized! call JsonServiceImpl.init()");
        }
        return JsonParser.Init.INSTANCE;
    }

    public Map<String, String> parse(String string) {
        //format
        JsonString jsonString = JSON_FORMAT.format(string);

        //validate
        if(!JSON_VALIDATOR.isValidJsonValue(jsonString)) {
            throw new JsonNotValid("Json is not valid!");
        }

        //parse
        return this.gatherRawObjects(jsonString);
    }

    /*
     * With the help of coordinates will cut the objects and save them in a map (k= object id , v = object) witch will be the result
     */
    private Map<String, String> gatherRawObjects(JsonString json) {

        int numberOfObjects = this.countObjects(json);
        Map<String, String> rawObjects = new LinkedHashMap<>();
        JsonCoordinate objectLocation = this.findDeepestObjectBounds(json);
        String lineJson = json.getString();

        while (numberOfObjects > 0 && objectLocation.getEndIndex() > objectLocation.getStartIndex()) {
            String id = "\"o-" + rawObjects.size() + "\"";
            String value = lineJson.substring(objectLocation.getStartIndex(), objectLocation.getEndIndex() + 1);
            rawObjects.put(id, value);
            lineJson = lineJson.replace(value, id);
            JsonString jsonString = JSON_FORMAT.format(lineJson);
            objectLocation = this.findDeepestObjectBounds(jsonString);
            numberOfObjects--;
        }

        return rawObjects;
    }

    /*
     * Counting objects
     */
    private Integer countObjects(JsonString json) {
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

    private JsonCoordinate findDeepestObjectBounds(JsonString json) {
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

        return new JsonCoordinate.JsonCoordinateBuilder(startIndex, endIndex).build();
    }

}
