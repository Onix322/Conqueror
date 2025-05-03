package org.json.parser_v2.json.properties;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JsonKey {
    private String name;

    public JsonKey(String name) {
        this.name = this.deleteKeyQuotes(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String deleteKeyQuotes(String name){
        if(name.charAt(0) == '"') name = name.substring(1, name.length() - 1);
        if(name.charAt(name.length() - 1) == '"') name = name.substring(0,name.length() - 2);

        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonKey that = (JsonKey) object;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
