package org.json.parser_v2.json.types;

import org.json.parser_v2.json.properties.JsonValue;

import java.util.Arrays;
import java.util.Objects;

public final class JsonArray implements JsonType{
    private JsonValue[] array;

    public JsonArray(JsonValue[] array) {
        this.array = array;
    }

    public JsonValue[] getArray() {
        return array;
    }

    public void setArray(JsonValue[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return  Arrays.toString(array);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonArray jsonArray = (JsonArray) object;
        return Objects.deepEquals(getArray(), jsonArray.getArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getArray());
    }
}
