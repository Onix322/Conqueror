package src.com.server.parsers.json.utils.types;

import src.com.server.exceptions.NoSuchJsonPropertyError;
import src.com.server.parsers.json.utils.properties.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

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
