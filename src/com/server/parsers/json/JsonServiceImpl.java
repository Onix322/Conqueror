package src.com.server.parsers.json;

import src.com.server.parsers.json.utils.parser.JsonParser;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;
import src.com.server.annotations.component.Component;

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
