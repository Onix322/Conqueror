package org.json.parser_v2.json.properties;

import java.util.Objects;

public class JsonProperty {
    private JsonKey key;
    private JsonValue value;

    public JsonProperty(JsonKey key, JsonValue value) {
        this.key = key;
        this.value = value;
    }

    public JsonKey getKey() {
        return key;
    }

    public void setKey(JsonKey key) {
        this.key = key;
    }

    public JsonValue getValue() {
        return value;
    }

    public void setValue(JsonValue value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonProperty that = (JsonProperty) object;
        return Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + key + "\": " + value;
    }
}
