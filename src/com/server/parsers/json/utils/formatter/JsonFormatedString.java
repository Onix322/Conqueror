package src.com.server.parsers.json.utils.formatter;

import java.util.Objects;

/**
 * JsonFormatedString is a utility class that encapsulates a string formatted as JSON.
 * It provides methods to get and set the string, and overrides equals and hashCode
 * for proper comparison and hashing based on the string value.
 */
public class JsonFormatedString {

    private String string;

    JsonFormatedString(String string){
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
        JsonFormatedString that = (JsonFormatedString) object;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getString());
    }
}
