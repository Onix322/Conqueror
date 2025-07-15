package framework.src.server.parsers.json.utils.types;

import framework.src.server.exceptions.NoSuchJsonPropertyError;
import framework.src.server.parsers.json.utils.properties.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 * JsonObject represents a JSON object containing an array of properties.
 * It implements JsonType and JsonIterator to provide functionality for
 * accessing and manipulating the properties of the JSON object.
 */
public class JsonObject implements JsonType, JsonIterator<JsonProperty> {

    private JsonProperty[] properties;

    public JsonObject(JsonProperty[] property) {
        this.properties = property;
    }

    public JsonProperty[] get() {
        return properties;
    }

    @Override
    public void set(JsonProperty[] array) {
        properties = array;
    }

    public JsonProperty getProperty(String name) throws NoSuchJsonPropertyError{
        return Arrays.stream(get())
                .filter(p -> p.key().get().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchJsonPropertyError("No property called: '" + name + "'"));
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonObject that = (JsonObject) object;
        return Objects.deepEquals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(properties);
    }

    @Override
    public String toString() {
        String objectString = Arrays.toString(properties);
        return '{' + objectString.substring(1, objectString.length() - 1) + '}';
    }
}
