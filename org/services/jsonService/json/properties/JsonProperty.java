package org.services.jsonService.json.properties;

import java.util.Map;
import java.util.Objects;

public class JsonProperty implements JsonKeyValue<Map<JsonKey, JsonValue>>{
    private JsonKey key;
    private JsonValue value;

    public JsonProperty(JsonKey key, JsonValue value) {
        this.key = key;
        this.value = value;
    }

    public JsonKey key() {
        return key;
    }

    public void setKey(JsonKey key) {
        this.key = key;
    }

    public JsonValue value() {
        return value;
    }

    public void setValue(JsonValue value) {
        this.value = value;
    }

    @Override
    public Map<JsonKey, JsonValue> get() {
        return Map.of(this.key, this.value);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonProperty that = (JsonProperty) object;
        return Objects.equals(key(), that.key()) && Objects.equals(value(), that.value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key(), value());
    }

    @Override
    public String toString() {
        return "\"" + key + "\": " + value;
    }

}
