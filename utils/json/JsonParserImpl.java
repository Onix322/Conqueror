package utils.json;

import java.lang.reflect.Field;
import java.util.HashMap;

public class JsonParserImpl implements JsonParser<String, Object> {

    private JsonParserImpl() {
    }

    private static class Init {
        private static final JsonParserImpl INSTANCE = new JsonParserImpl();
    }

    public static JsonParserImpl getInstance() {
        return Init.INSTANCE;
    }

    public String generate(Object object){
        return this.generate(object, null);
    }

    private String generate(Object object, HashMap<String, Object> args) {

        HashMap<String, Object> map = new HashMap<>();

        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(object);
                if(fieldValue instanceof String){
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
