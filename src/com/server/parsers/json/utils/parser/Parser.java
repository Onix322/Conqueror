package src.com.server.parsers.json.utils.parser;

import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

import java.util.Collection;

public sealed interface Parser permits JsonParser{
    <T> T mapObject(JsonObject jsonType, Class<T> type) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String string);
}
