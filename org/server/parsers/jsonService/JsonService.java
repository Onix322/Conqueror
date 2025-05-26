package org.server.parsers.jsonService;


import org.server.parsers.jsonService.json.types.JsonArray;
import org.server.parsers.jsonService.json.types.JsonObject;
import org.server.parsers.jsonService.json.types.JsonType;

import java.util.Collection;

//T is representing the class implementation
public sealed interface JsonService permits JsonServiceImpl{

    <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String json);
}
