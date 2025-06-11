package org.server.parsers.json.utils.properties;

import java.util.Objects;

public class JsonValue implements JsonKeyValue<Object>{
    private Object value;

    public JsonValue(Object value) {
        this.value = value;
    }

    public <T> T get(Class<T> type) {
        return type.cast(value);
    }

    @Override
    public Object get() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonValue value1 = (JsonValue) object;
        return Objects.equals(value, value1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
