package src.com.server.parsers.json;


import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

import java.util.Collection;

//T is representing the class implementation
/**
 * JsonService is an interface that defines methods for mapping JSON objects and arrays to Java types,
 * as well as parsing JSON strings into JsonType objects.
 * It is designed to be implemented by classes that provide JSON parsing and mapping functionality.
 * <T> is a generic type parameter that allows the methods to work with any class type.
 * <E> is a generic type parameter for collections, allowing the methods to return collections of any type.
 */
public sealed interface JsonService permits JsonServiceImpl {

    <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws Exception;

    <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass) throws Exception;

    JsonType mapJson(Object o) throws Exception;

    JsonType parse(String json);
}
