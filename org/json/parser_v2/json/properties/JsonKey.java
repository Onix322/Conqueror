package org.json.parser_v2.json.properties;

import java.util.Objects;

public class JsonKey implements JsonKeyValue<String>{
    private String key;

    public JsonKey(String key) {
        this.key = this.deleteKeyQuotes(key);
    }

    @Override
    public String get() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String deleteKeyQuotes(String key) {
        if (key.charAt(0) == '"') key = key.substring(1, key.length() - 1);
        if (key.charAt(key.length() - 1) == '"') key = key.substring(0, key.length() - 2);

        return key;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonKey that = (JsonKey) object;
        return Objects.equals(get(), that.get());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(get());
    }

    @Override
    public String toString() {
        return key;
    }
}
