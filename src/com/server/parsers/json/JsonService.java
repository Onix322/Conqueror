package src.com.server.parsers.json;


import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

import java.util.Collection;

//T is representing the class implementation
public sealed interface JsonService permits JsonServiceImpl{

    <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String json);
}
