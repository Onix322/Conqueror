package org.json.parser_v2;

import org.exepltions.JsonNotValid;

import javax.swing.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonServiceImpl implements JsonService {

    private final NumberFormat NUMBER_FORMAT;
    private final JsonValidator JSON_VALIDATOR;

    private JsonServiceImpl(NumberFormat numberFormat, JsonValidator jsonValidator) {
        this.NUMBER_FORMAT = numberFormat;
        this.JSON_VALIDATOR = jsonValidator;
    }

    private static class Init {
        private static JsonServiceImpl INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat, JsonValidator jsonValidator) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonServiceImpl(numberFormat, jsonValidator);
        }
    }

    public static JsonServiceImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("JsonParser not initialized! call JsonServiceImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public String generate(Object object) {
        return "";
    }


    @Override
    public <T> T map(String stringLikeJson, Class<T> clazz) throws IllegalAccessException, ParseException {
        //! de implementat
        return null;
    }

    @Override
    public Map<String, Object> getProperties(String stringLikeJson) {
        return Map.of();
    }

    private Integer countObjects(String lineJson) {
        int number = 0;

        for (int i = 0; i < lineJson.length(); i++) {
            if (lineJson.charAt(i) == '{' || lineJson.charAt(i) == '[') number++;
        }

        return number;
    }

    public int[] findTheHighestObjectLocation(String lineJson) {
        int level = 0;
        int highestIndex = 0;
        int closingColumnIndex = 0;
        boolean inStringCharacter = false;

        for (int i = 0, layer = 0; i < lineJson.length(); i++) {
            char c = lineJson.charAt(i);

            switch (c) {
                //! Determine if a char is part of a string chars: ", {, [ , } , ] , and comma (,)
                case '"' -> inStringCharacter = !inStringCharacter;
                case '{', '[' -> {
                    if (inStringCharacter) continue;
                    ++layer;
                }
                case '}', ']' -> {
                    if (inStringCharacter) continue;
                    --layer;
                }
            }

            if (layer > level) {
                level = layer;
                highestIndex = i;
            }
        }

        for (int j = highestIndex; j < lineJson.length(); j++) {
            char c = lineJson.charAt(j);
            if(c == '"') inStringCharacter = !inStringCharacter;
            if ((c == '}' || c == ']') && !inStringCharacter) {
                closingColumnIndex = j;
                break;
            }
        }

        return new int[]{highestIndex, closingColumnIndex};
    }

    public Map<String, String> gatherRawObjects(String json) {

        if(!JSON_VALIDATOR.isValidJsonValue(json)) {
            throw new JsonNotValid("Json is not valid!");
        }

        Map<String, String> rawObjects = new LinkedHashMap<>();
        String lineJson = json.replaceAll("\n\s+", "");
        int numberOfObjects = this.countObjects(lineJson);
        int[] objectLocation = this.findTheHighestObjectLocation(lineJson);

        while (numberOfObjects > 0 && objectLocation[1] > objectLocation[0]) {
            String id = "\"o-" + rawObjects.size() + "\"";
            String value = lineJson.substring(objectLocation[0], objectLocation[1] + 1);
            rawObjects.put(id, value);
            lineJson = lineJson.replace(value, id);
            objectLocation = this.findTheHighestObjectLocation(lineJson);
            numberOfObjects--;
        }

        return rawObjects;
    }
}
