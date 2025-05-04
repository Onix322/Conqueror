package org.json.parser_v2.json.types;

import org.exepltions.NoSuchJsonPropertyError;
import org.json.parser_v2.json.properties.JsonProperty;

import java.util.Arrays;

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

    public JsonProperty getProperty(String name) {
        return Arrays.stream(get())
                .filter(p -> p.key().get().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchJsonPropertyError("No property called: '" + name + "'"));
    }

    @Override
    public String toString() {
        String objectString = Arrays.toString(properties);
        return '{' + objectString.substring(1, objectString.length() - 1) + '}';
    }
}
