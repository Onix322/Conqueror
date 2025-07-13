package src.com.server.parsers.json.utils.parser;

import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

import java.util.Collection;

/**
 * Parser interface for JSON parsing operations.
 * This interface defines methods for mapping JSON objects and arrays to Java types,
 * as well as parsing JSON strings into JsonType objects.
 */
public sealed interface Parser permits JsonParser{
    <T> T mapObject(JsonObject jsonType, Class<T> type) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String string);
}
