package org.server.jsonService.json.mapper;

import org.server.jsonService.json.properties.JsonKey;
import org.server.jsonService.json.properties.JsonProperty;
import org.server.jsonService.json.properties.JsonValue;
import org.server.jsonService.json.types.JsonArray;
import org.server.jsonService.json.types.JsonObject;
import org.server.primitiveParser.PrimitiveParser;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class JsonMapper {

    private final PrimitiveParser PRIMITIVE_PARSER;

    private JsonMapper(PrimitiveParser primitiveParser) {
        this.PRIMITIVE_PARSER = primitiveParser;
    }

    private static class Init {
        private static JsonMapper INSTANCE = null;
    }

    public synchronized static void init(PrimitiveParser primitiveParser) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonMapper(primitiveParser);
        }
    }

    public static JsonMapper getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("JsonMapper not initialized! call JsonMapper.init()");
        }
        return Init.INSTANCE;
    }

    public JsonObject toJsonObject(Object o) throws Exception {
        List<JsonProperty> properties = new LinkedList<>();
        Field[] fields = o.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(o);
            JsonKey jsonKey = new JsonKey(field.getName());
            JsonValue jsonValue = this.getJsonValue(fieldValue);
            JsonProperty jsonProperty = new JsonProperty(jsonKey, jsonValue);
            properties.add(jsonProperty);
        }

        return new JsonObject(properties.toArray(new JsonProperty[0]));
    }

    public JsonArray toJsonArray(Collection<?> o) throws Exception {

        List<JsonValue> values = new LinkedList<>();

        for (Object v : o) {
            System.out.println(v + " " + v.getClass());
            JsonValue jsonValue = this.getJsonValue(v);
            values.add(new JsonValue(jsonValue));
        }

        return new JsonArray(values.toArray(new JsonValue[0]));
    }

    private JsonValue getJsonValue(Object o) throws Exception {
        JsonValue jsonValue;
        if (this.PRIMITIVE_PARSER.isWrapperClass(o.getClass())){
            jsonValue = new JsonValue(o);
        } else if (o instanceof String) {
            jsonValue = new JsonValue('"' + o.toString() + '"');
        } else if (o instanceof Collection<?>) {
            jsonValue = new JsonValue(this.toJsonArray((Collection<?>) o));
        } else {
            jsonValue = new JsonValue(this.toJsonObject(o));
        }
        return jsonValue;
    }
}
