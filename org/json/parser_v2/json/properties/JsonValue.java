package org.json.parser_v2.json.properties;

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
    public String toString() {
        return value.toString();
    }
}
