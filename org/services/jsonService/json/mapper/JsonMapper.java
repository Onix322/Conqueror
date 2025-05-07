package org.services.jsonService.json.mapper;

import org.services.jsonService.json.properties.JsonKey;
import org.services.jsonService.json.properties.JsonProperty;
import org.services.jsonService.json.properties.JsonValue;
import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class JsonMapper {

    private JsonMapper() {
    }

    private static class Init {
        private static final JsonMapper INSTANCE = new JsonMapper();
    }

    public static JsonMapper getInstance() {
        return JsonMapper.Init.INSTANCE;
    }

    public JsonObject toJsonObject(Object o) throws IllegalAccessException {

        Field[] fields = o.getClass().getDeclaredFields();

        List<JsonProperty> properties = new LinkedList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(o);
            JsonKey jsonKey = new JsonKey(field.getName());
            JsonValue jsonValue;
            if (fieldValue instanceof Collection<?>) {
                jsonValue = new JsonValue(this.toJsonArray((Collection<?>) o));
            } else {
                jsonValue = new JsonValue(fieldValue);
            }
            JsonProperty jsonProperty = new JsonProperty(jsonKey, jsonValue);
            properties.add(jsonProperty);
        }

        return new JsonObject(properties.toArray(new JsonProperty[0]));
    }

    public JsonArray toJsonArray(Collection<?> o) throws IllegalAccessException {

        List<JsonValue> values = new LinkedList<>();

        for (Object v : o) {
            JsonValue jsonValue;
            if (!(v instanceof Number || v instanceof Boolean || v instanceof Character)) {
                jsonValue = new JsonValue(this.toJsonObject(v));
            } else {
                jsonValue = new JsonValue(v);
            }
            values.add(new JsonValue(jsonValue));
        }

        return new JsonArray(values.toArray(new JsonValue[0]));
    }
}
