package org.json.parser_v2;

import org.exepltions.JsonNotValid;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class JsonServiceImpl implements JsonService {

    private final NumberFormat NUMBER_FORMAT;

    private JsonServiceImpl(NumberFormat numberFormat) {
        this.NUMBER_FORMAT = numberFormat;
    }

    private static class Init {
        private static JsonServiceImpl INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonServiceImpl(numberFormat);
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

    private Integer countObjects(String lineJson){
        int number = 0;

        for (int i = 0; i < lineJson.length(); i++) {
            if(lineJson.charAt(i) == '{') number++;
        }

        return number;
    }
    public int[] findTheHighestObjectLocation(String lineJson) {
        int level = 0;
        int highestIndex = 0;
        int closingColumnIndex = 0;

        for (int i = 0, layer = 0; i < lineJson.length(); i++) {
            char c = lineJson.charAt(i);

            switch (c) {
                case '{':
                case '[':
                    ++layer;
                    break;
                case '}', ']':
                    --layer;
            }

            if (layer > level) {
                level = layer;
                highestIndex = i;
            }
        }

        for (int j = highestIndex; j < lineJson.length(); j++) {
            char c = lineJson.charAt(j);
            if (c == '}') {
                closingColumnIndex = j;
                break;
            }
        }

        return new int[]{highestIndex, closingColumnIndex};
    }

    public Map<String, String> gatherRawObjects(String json){
        Map<String, String> rawObjects = new LinkedHashMap<>();
        String lineJson = json.replaceAll("\n\s+", "");
        int numberOfObjects = this.countObjects(lineJson);
        int[] objectLocation = this.findTheHighestObjectLocation(lineJson);

        for (int i = 0; i < numberOfObjects - 1; i++) {

            String id = "o-" + rawObjects.size();
            String value = lineJson.substring(objectLocation[0], objectLocation[1] + 1);
            rawObjects.put(id,value);
            lineJson = lineJson.replace(value, id);
            objectLocation = this.findTheHighestObjectLocation(lineJson);
        }
        return rawObjects;
    }
}
