package org.json.parser_v2;

import org.exepltions.JsonNotValid;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
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


    //TODO for each object from value side replace it with a reference from a list of primitive objects
    //TODO Create validation for the end of the line checking for missing ',' unless is the last line in object / array
    public List<String> listTheProperties(String json) {
        String lineJson = json.replaceAll("\n\s+", "");

        StringBuilder property = new StringBuilder();
        List<String> properties = new ArrayList<>();
        System.out.println(lineJson);
        for (int i = 0, layer = 0; i < lineJson.length(); i++) {
            char c = lineJson.charAt(i);
            property.append(lineJson.charAt(i));

            switch (c){
                case '{', '[' -> ++ layer;
                case '}', ']' -> -- layer;
                case ',' -> {
                    if(layer <= 1){
                        properties.add(property.toString());
                        property.setLength(0);
                    }
                }
            }
            if(layer != 0 && i == lineJson.length() - 1){
                throw new JsonNotValid("You missed a '{' / '}' or '[' / ']'");
            }
        }

        if (!property.isEmpty()) {
            properties.add(property.toString());
        }

        return properties;
    }

    public Map<String, String> divideKeyValue(List<String> properties) {
        Map<String, String> pair = new LinkedHashMap<>();

        for (String line : properties) {
            String key = line.substring(0, line.indexOf(":")).replaceAll("\\W", "");
            String value = line.substring(line.indexOf(":") + 2, line.length() - 1);
            pair.put(key, value);
        }
        return pair;
    }
}
