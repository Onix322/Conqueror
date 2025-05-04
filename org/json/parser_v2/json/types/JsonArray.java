package org.json.parser_v2.json.types;

import org.json.parser_v2.json.properties.JsonValue;

import java.util.Arrays;
import java.util.Objects;

public class JsonArray implements JsonType, JsonIterator<JsonValue> {

    private JsonValue[] array;

    public JsonArray(JsonValue[] array) {
        this.array = array;
    }

    public JsonValue[] get() {
        return array;
    }

    public void set(JsonValue[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonArray jsonArray = (JsonArray) object;
        return Objects.deepEquals(get(), jsonArray.get());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(get());
    }
}
