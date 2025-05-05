package org.services.jsonService;

import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.types.JsonObject;

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
    public <T> T map(JsonObject jsonObject, Class<T> clazz) {
        return null;
    }

    @Override
    public JsonObject parse(String json) {
        return null;
    }
}
