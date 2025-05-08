package org.utils.jsonService;


import org.utils.jsonService.json.types.JsonArray;
import org.utils.jsonService.json.types.JsonObject;
import org.utils.jsonService.json.types.JsonType;

import java.util.Collection;

//T is representing the class implementation
public interface JsonService {

    <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws ReflectiveOperationException;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws ReflectiveOperationException;

    JsonType mapJson(Object o) throws IllegalAccessException;

    JsonType parse(String json);
}
