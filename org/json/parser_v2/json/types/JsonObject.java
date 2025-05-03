package org.json.parser_v2.json.types;

import org.exepltions.NoSuchJsonPropertyError;
import org.json.parser_v2.json.properties.JsonProperty;

import java.util.Arrays;

public final class JsonObject implements JsonType {

    private JsonProperty[] properties;

    public JsonObject(JsonProperty[] property) {
        this.properties = property;
    }

    public JsonProperty[] getProperties() {
        return properties;
    }

    public void setProperties(JsonProperty[] properties) {
        this.properties = properties;
    }

    public JsonProperty getProperty(String name) {
        return Arrays.stream(getProperties())
                .filter(p -> p.getKey().getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchJsonPropertyError("No property called: '" + name + "'"));
    }

    @Override
    public String toString() {
        String objectString = Arrays.toString(properties);
        return '{' + objectString.substring(1, objectString.length() - 1) + '}';
    }
}
