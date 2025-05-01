package org.json.parser_v2;

import java.util.Objects;

public class JsonString {

    private String string;

    public JsonString(String string){
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonString that = (JsonString) object;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getString());
    }
}
