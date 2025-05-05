package org.services.jsonService.json.parser;

import org.services.jsonService.json.types.JsonType;

public interface Parser {
    <T> T map(JsonType jsonType, Class<T> clazz) throws NoSuchMethodException;
    JsonType parse(String string);
}
