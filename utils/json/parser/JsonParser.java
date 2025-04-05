package utils.json.parser;

import org.httpServer.exepltions.NoEmptyConstructorFound;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class JsonParser {

    private final NumberFormat NUMBER_FORMAT;

    private JsonParser(NumberFormat numberFormat) {
        this.NUMBER_FORMAT = numberFormat;
    }

    private static class Init {
        private static JsonParser INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat){
        if(Init.INSTANCE == null){
            Init.INSTANCE = new JsonParser(numberFormat);
        }
    };


    public static JsonParser getInstance() {
        if(Init.INSTANCE == null){
            throw new IllegalStateException("JsonParser not initialized! call JsonParser.init()");
        }
        return Init.INSTANCE;
    }

    public String generate(Object object) {
        return this.generate(object, null);
    }

    public Object map(String stringLikeJson, Class<?> clazz) {


        System.out.println("from mapper: ");
        System.out.println(this.getProperties(stringLikeJson));

        Map<String, Object> objectMapped = this.getProperties(stringLikeJson);

        Object instance = this.createInstance(clazz);

        Arrays.stream(instance.getClass().getDeclaredFields())
                .toList()
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(instance, objectMapped.get(field.getName()));
                    } catch (Exception e) {
                        System.err.println(e.getCause().getMessage());
                        throw new RuntimeException(e);
                    }
                });

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
        } catch (NoEmptyConstructorFound | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getCause().getMessage());
            throw new NoEmptyConstructorFound();
        }
    }

    private Map<String, Object> getProperties(String stringLikeJson) {
        Map<String, Object> properties = new HashMap<>();
        Arrays.stream(stringLikeJson.replace("\r\n", "")
                        .replace(" ", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .split(","))
                .toList()
                .forEach(line -> {
                    List<?> prop = List.of(line.split(":"));
                    String value = (String) prop.getLast();
                    if (value.matches("\\d")) {
                        try {
                            properties.put((String) prop.getFirst(), NUMBER_FORMAT.parse(value).intValue());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        properties.put((String) prop.getFirst(), prop.getLast());
                    }
                });

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
