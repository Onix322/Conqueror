package org.services.jsonService.json.objectMapper;

import org.services.jsonService.json.properties.JsonProperty;
import org.services.jsonService.json.properties.JsonValue;
import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonObject;
import org.services.jsonService.json.types.JsonType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectMapper {

    private ObjectMapper() {
    }

    private static class Init {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper getInstance() {
        return Init.INSTANCE;
    }

    public <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws ReflectiveOperationException {
        T instance = this.createInstance(clazz);
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields){
            JsonProperty jsonProperty = jsonObject.getProperty(field.getName());
            field.setAccessible(true);
            field.set(instance, jsonProperty.value().get(field.getType()));
        }

        return instance;
    }

    public <E> Collection<E> map(JsonArray jsonArray, Collection<E> collection, Class<E> elementType) throws ReflectiveOperationException {
        for(JsonValue value : jsonArray.get()){
            collection.add(value.get(elementType));
        }
        return collection;
    }

    private <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException{
        try{
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveOperationException(e.getMessage());
        }
    }
}
