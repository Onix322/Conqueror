package org.server.jsonService.json.parser;

import org.server.jsonService.json.types.JsonArray;
import org.server.jsonService.json.types.JsonObject;
import org.server.jsonService.json.types.JsonType;

import java.util.Collection;

public interface Parser {
    <T> T mapObject(JsonObject jsonType, Class<T> type) throws ReflectiveOperationException;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws ReflectiveOperationException;

    JsonType mapJson(Object o) throws IllegalAccessException;

    JsonType parse(String string);
}
