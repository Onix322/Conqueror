package framework.src.server.parsers.json;

import framework.src.server.parsers.json.utils.parser.JsonParser;
import framework.src.server.parsers.json.utils.types.JsonArray;
import framework.src.server.parsers.json.utils.types.JsonObject;
import framework.src.server.parsers.json.utils.types.JsonType;
import framework.src.server.annotations.component.Component;

import java.util.Collection;

/**
 * JsonServiceImpl is a concrete implementation of the JsonService interface.
 * It provides methods to map JSON objects and arrays to Java types, and to parse JSON strings.
 */
@Component
public final class JsonServiceImpl implements JsonService {

    private final JsonParser JSON_PARSER;

    private JsonServiceImpl(JsonParser jsonParser) {
        this.JSON_PARSER = jsonParser;
    }

    /**
     * Returns the JsonParser instance used by this service.
     *
     * @return the JsonParser instance
     */
    @Override
    public <T> T mapObject(JsonObject jsonType, Class<T> clazz) throws Exception {
        return this.JSON_PARSER.mapObject(jsonType, clazz);
    }

    /**
     * Maps a JSON array to a collection of the specified type.
     *
     * @param jsonArray        the JSON array to map
     * @param collectionClass  the class of the collection type
     * @param <E>              the type of elements in the collection
     * @return a collection of mapped elements
     * @throws Exception if an error occurs during mapping
     */
    @Override
    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception {
        return this.JSON_PARSER.mapArray(jsonArray, collectionClass);
    }

    /**
     * Maps a Java object to a JSON representation.
     *
     * @param o the object to map
     * @return a JsonType representing the object
     * @throws Exception if an error occurs during mapping
     */
    @Override
    public JsonType mapJson(Object o) throws Exception {
        return this.JSON_PARSER.mapJson(o);
    }

    /**
     * Parses a JSON string into a JsonType object.
     *
     * @param json the JSON string to parse
     * @return a JsonType representing the parsed JSON
     */
    @Override
    public JsonType parse(String json) {
        return this.JSON_PARSER.parse(json);
    }
}
