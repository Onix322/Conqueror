package org.json.parser_v2;

import java.text.ParseException;
import java.util.Map;

public class JsonServiceImpl implements JsonService {

    private final JsonParser JSON_PARSER;

    private JsonServiceImpl(JsonParser jsonParser) {
        this.JSON_PARSER = jsonParser;
    }

    private static class Init {
        private static JsonServiceImpl INSTANCE = null;
    }

    public synchronized static void init(JsonParser jsonParser) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonServiceImpl(jsonParser);
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

}
