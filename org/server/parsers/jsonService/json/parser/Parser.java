package org.server.parsers.jsonService.json.parser;

import org.server.parsers.jsonService.json.types.JsonArray;
import org.server.parsers.jsonService.json.types.JsonObject;
import org.server.parsers.jsonService.json.types.JsonType;

import java.util.Collection;

public sealed interface Parser permits JsonParser{
    <T> T mapObject(JsonObject jsonType, Class<T> type) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String string);
}
