package org.server.parsers.jsonService.json.mapper;

import org.server.parsers.jsonService.json.properties.JsonProperty;
import org.server.parsers.jsonService.json.properties.JsonValue;
import org.server.parsers.jsonService.json.types.JsonArray;
import org.server.parsers.jsonService.json.types.JsonObject;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

@Component
public final class ObjectMapper {

    private ObjectMapper() {
    }

    public <T> T mapObject(JsonObject jsonObject, Class<T> clazz) throws ReflectiveOperationException {
        T instance = this.createInstance(clazz);
        Field[] fields = instance.getClass().getDeclaredFields();

        if (fields.length != jsonObject.get().length) {
            throw new ClassCastException("JsonObject must have same properties as: " + clazz);
        }
        for (Field field : fields) {
            JsonProperty jsonProperty = jsonObject.getProperty(field.getName());
            JsonValue jsonValue = jsonProperty.value();
            field.setAccessible(true);
            if (jsonValue.get() instanceof JsonArray valueArray) {
                field.set(instance, this.mapToArray(valueArray, LinkedList.class));
            } else if (jsonValue.get() instanceof JsonObject valueObject) {
                field.set(instance, this.mapObject(valueObject, field.getType()));
            } else if (field.getType().isEnum()) {
                field.set(instance, this.enumResolver(jsonValue, field));
            } else {
                field.set(instance, jsonValue.get(field.getType()));
            }
        }

        return instance;
    }

    private Object enumResolver(JsonValue jsonValue, Field field) {
        for (Object e : field.getType().getEnumConstants()) {
            if (e.toString().equals(jsonValue.get())) {
                System.out.println(field.getType().cast(e));
                System.out.println(field.getType().cast(e).getClass());
                return field.getType().cast(e);
            }
        }
        throw new NoSuchElementException("No such enum: " + jsonValue.get());
    }

    @SuppressWarnings("unchecked")
    private <T, E> Collection<E> mapToArray(JsonArray jsonArray, Class<T> clazz) throws ReflectiveOperationException {
        Collection<E> instance = (Collection<E>) createInstance(clazz);
        for (JsonValue value : jsonArray.get()) {
            if (value.get() instanceof JsonObject jsonObject) {
                instance.add((E) this.mapObject(jsonObject, value.get().getClass()));
            } else {
                instance.add((E) value.get());
            }
        }

        return instance;
    }

    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass)
            throws ReflectiveOperationException {
        return this.mapToArray(jsonArray, collectionClass);
    }

    private <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ReflectiveOperationException(e.getLocalizedMessage());
        }
    }
}
