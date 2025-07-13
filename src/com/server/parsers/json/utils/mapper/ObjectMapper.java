package src.com.server.parsers.json.utils.mapper;

import src.com.server.exceptions.NoSuchJsonPropertyError;
import src.com.server.parsers.json.utils.properties.JsonProperty;
import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.annotations.component.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * ObjectMapper is a utility class that maps JSON objects to Java objects.
 * It uses reflection to set the fields of the target object based on the properties of the JSON object.
 */
@Component
public final class ObjectMapper {

    private ObjectMapper() {
    }

    /**
     * Maps a JSON object to a Java object of the specified class.
     *
     * @param jsonObject The JSON object to map.
     * @param clazz      The class of the target object.
     * @param <T>        The type of the target object.
     * @return An instance of the target object with fields set from the JSON object.
     * @throws RuntimeException If there is an error during mapping.
     */
    public <T> T mapObject(JsonObject jsonObject, Class<T> clazz) {
        try {
            T instance = this.createInstance(clazz);
            Field[] fields = instance.getClass().getDeclaredFields();

            if (fields.length != jsonObject.get().length) {
                throw new ClassCastException("JSON must have same properties as: " + clazz);
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
        } catch (NoSuchJsonPropertyError | IllegalArgumentException | ReflectiveOperationException |
                 ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves an enum value from a JSON value based on the field type.
     *
     * @param jsonValue The JSON value to resolve.
     * @param field     The field representing the enum type.
     * @return The resolved enum constant.
     * @throws NoSuchElementException If no matching enum constant is found.
     */
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

    /**
     * Maps a JSON array to a collection of the specified class type.
     *
     * @param jsonArray The JSON array to map.
     * @param clazz     The class of the collection type.
     * @param <T>       The type of the collection elements.
     * @param <E>       The type of the elements in the collection.
     * @return A collection containing the mapped elements from the JSON array.
     * @throws ReflectiveOperationException If there is an error during reflection operations.
     */
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

    /**
     * Maps a JSON array to a collection of the specified class type.
     *
     * @param jsonArray The JSON array to map.
     * @param collectionClass The class of the collection type.
     * @param <E> The type of the elements in the collection.
     * @return A collection containing the mapped elements from the JSON array.
     * @throws Exception If there is an error during mapping.
     */
    public <E> Collection<E> mapArray(JsonArray jsonArray, Class<? extends Collection> collectionClass)
            throws Exception {
        return this.mapToArray(jsonArray, collectionClass);
    }

    /**
     * Creates an instance of the specified class using its no-argument constructor.
     *
     * @param clazz The class to create an instance of.
     * @param <T>   The type of the class.
     * @return An instance of the specified class.
     * @throws ReflectiveOperationException If there is an error during reflection operations.
     */
    private <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new ReflectiveOperationException("No empty constructor found! " + e.getLocalizedMessage());
        }
    }
}
