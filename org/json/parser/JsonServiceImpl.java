package org.json.parser;

import org.exepltions.NoEmptyConstructorFound;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonServiceImpl implements JsonService {

    private final NumberFormat NUMBER_FORMAT;

    private JsonServiceImpl(NumberFormat numberFormat) {
        this.NUMBER_FORMAT = numberFormat;
    }

    private static class Init {
        private static JsonServiceImpl INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonServiceImpl(numberFormat);
        }
    }

    public static JsonServiceImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("JsonParser not initialized! call JsonParser.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public String generate(Object object) {
        return this.generate(object, null);
    }

    @Override
    public Object map(String stringLikeJson, Class<?> clazz) throws IllegalAccessException, ParseException {

        Map<String, Object> objectMapped = this.getProperties(stringLikeJson);

        Object instance = this.createInstance(clazz);

        List<Field> fields = Arrays.stream(instance.getClass().getDeclaredFields())
                .toList();

        for (Field field: fields){
            field.setAccessible(true);
            field.set(instance, objectMapped.get(field.getName()));
        }

        return instance;
    }

    private Object createInstance(Class<?> clazz) {
        try {

            Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                    .toList()
                    .stream()
                    .filter(c -> c.getParameterCount() == 0)
                    .findAny()
                    .orElseThrow(NoEmptyConstructorFound::new);


            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (NoEmptyConstructorFound | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            System.err.println(e.getCause().getMessage());
            throw new NoEmptyConstructorFound();
        }
    }

    @Override
    public Map<String, Object> getProperties(String stringLikeJson) throws ParseException {
        Map<String, Object> properties = new HashMap<>();
        List<String> lines = Arrays.stream(stringLikeJson.replace("\r\n", "")
                        .replace(" ", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .split(",")).toList();

        for (String line : lines){
            List<?> prop = List.of(line.split(":"));
            String value = (String) prop.getLast();
            if (value.matches("\\d")) {
                properties.put((String) prop.getFirst(), NUMBER_FORMAT.parse(value).intValue());
            } else {
                properties.put((String) prop.getFirst(), prop.getLast());
            }
        }

        return properties;
    }

    private String generate(Object object, HashMap<String, Object> args) {

        HashMap<String, Object> map = new HashMap<>();

        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if (fieldValue instanceof String) {
                    map.put('"' + field.getName() + '"', '"' + fieldValue.toString() + '"');
                } else if (fieldValue instanceof Number) {
                    map.put('"' + field.getName() + '"', fieldValue.toString());
                } else {
                    return this.generate(field.get(object), map);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return map.toString()
                .replace('=', ':');
    }
}
