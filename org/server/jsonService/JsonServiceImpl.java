package org.server.jsonService;

import org.server.jsonService.json.parser.JsonParser;
import org.server.jsonService.json.types.JsonArray;
import org.server.jsonService.json.types.JsonObject;
import org.server.jsonService.json.types.JsonType;
import org.server.processors.components.annotations.Component;

import java.util.Collection;

@Component
public final class JsonServiceImpl implements JsonService {

    private final JsonParser JSON_PARSER;

    private JsonServiceImpl(JsonParser jsonParser) {
        this.JSON_PARSER = jsonParser;
    }

    @Override
    public <T> T mapObject(JsonObject jsonType, Class<T> clazz) throws Exception {
        return this.JSON_PARSER.mapObject(jsonType, clazz);
    }

    @Override
    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception {
        return this.JSON_PARSER.mapArray(jsonArray, collectionClass);
    }

    @Override
    public JsonType mapJson(Object o) throws Exception {
        return this.JSON_PARSER.mapJson(o);
    }

    @Override
    public JsonType parse(String json) {
        return this.JSON_PARSER.parse(json);
    }
}
