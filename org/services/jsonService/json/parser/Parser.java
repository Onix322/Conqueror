package org.services.jsonService.json.parser;

import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonObject;
import org.services.jsonService.json.types.JsonType;

import java.util.Collection;

public interface Parser {
    <T> T mapObject(JsonObject jsonType, Class<T> type) throws ReflectiveOperationException ;
    <E> Collection<E> mapArray(JsonArray jsonType, Collection<E> collection, Class<E> elementType) throws ReflectiveOperationException;
    JsonType parse(String string);
}
