package org.services.jsonService.json.objectMapper;

import org.services.jsonService.json.types.JsonType;

public class ObjectMapper {

    private ObjectMapper() {
    }

    private static class Init {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper getInstance() {
        return Init.INSTANCE;
    }

    public <T> T map(JsonType jsonType, Class<T> clazz) {

        return null;
    }
}
