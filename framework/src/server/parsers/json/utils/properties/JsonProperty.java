package framework.src.server.parsers.json.utils.properties;

import java.util.Objects;

/**
 * JsonProperty is a utility class that encapsulates a key-value pair in a JSON object.
 * It implements the JsonKeyValue interface, providing methods to access and manipulate
 * the key and value of the property.
 */
public class JsonProperty implements JsonKeyValue<JsonProperty> {
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
    public JsonProperty get() {
        return this;
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
