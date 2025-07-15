package framework.src.server.parsers.json.utils.types;

import framework.src.server.parsers.json.utils.properties.JsonValue;

import java.util.Arrays;
import java.util.Objects;

/**
 * JsonArray represents a JSON array structure.
 * It implements JsonType and JsonIterator to provide access to its elements.
 * This class encapsulates an array of JsonValue objects.
 */
public class JsonArray implements JsonType, JsonIterator<JsonValue> {

    private JsonValue[] array;

    public JsonArray(JsonValue[] array) {
        this.array = array;
    }

    public JsonValue[] get() {
        return array;
    }

    public JsonValue get(int index) {
        return array[index];
    }

    public void set(JsonValue[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonArray jsonArray = (JsonArray) object;
        return Objects.deepEquals(get(), jsonArray.get());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(get());
    }
}
