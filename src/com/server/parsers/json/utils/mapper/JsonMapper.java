package src.com.server.parsers.json.utils.mapper;

import src.com.server.parsers.json.utils.properties.JsonKey;
import src.com.server.parsers.json.utils.properties.JsonProperty;
import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.primitiveParser.PrimitiveParser;
import src.com.server.annotations.component.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * JsonMapper is a utility class that provides methods to convert Java objects
 * into JSON representations. It uses reflection to access the fields of the object
 * and constructs JSON objects and arrays accordingly.
 */
@Component
public final class JsonMapper {

    private final PrimitiveParser PRIMITIVE_PARSER;

    private JsonMapper(PrimitiveParser primitiveParser) {
        this.PRIMITIVE_PARSER = primitiveParser;
    }

    public PrimitiveParser getPrimitiveParser() {
        return this.PRIMITIVE_PARSER;
    }

    /**
     * Converts a Java object into a JSON object.
     *
     * @param o the object to convert
     * @return a JsonObject representing the object
     * @throws Exception if an error occurs during conversion
     */
    public JsonObject toJsonObject(Object o) throws Exception {
        List<JsonProperty> properties = new LinkedList<>();
        Field[] fields = o.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(o);
            JsonKey jsonKey = new JsonKey(field.getName());
            JsonValue jsonValue = this.getJsonValue(fieldValue);
            JsonProperty jsonProperty = new JsonProperty(jsonKey, jsonValue);
            properties.add(jsonProperty);
        }

        return new JsonObject(properties.toArray(new JsonProperty[0]));
    }

    /**
     * Converts a collection of objects into a JSON array.
     *
     * @param o the collection to convert
     * @return a JsonArray representing the collection
     * @throws Exception if an error occurs during conversion
     */
    public JsonArray toJsonArray(Collection<?> o) throws Exception {

        List<JsonValue> values = new LinkedList<>();

        for (Object v : o) {
            System.out.println(v + " " + v.getClass());
            JsonValue jsonValue = this.getJsonValue(v);
            values.add(new JsonValue(jsonValue));
        }

        return new JsonArray(values.toArray(new JsonValue[0]));
    }

    /**
     * Converts an object into a JsonValue based on its type.
     *
     * @param o the object to convert
     * @return a JsonValue representing the object
     * @throws Exception if an error occurs during conversion
     */
    private JsonValue getJsonValue(Object o) throws Exception {
        JsonValue jsonValue;
        if(o == null){
            return null;
        } else if (this.PRIMITIVE_PARSER.isWrapperClass(o.getClass())) {
            jsonValue = new JsonValue(o);
        } else if (o instanceof String) {
            jsonValue = new JsonValue('"' + o.toString() + '"');
        } else if (o instanceof Collection<?>) {
            jsonValue = new JsonValue(this.toJsonArray((Collection<?>) o));
        } else if (o instanceof Enum<?>) {
            jsonValue = new JsonValue('"' + ((Enum<?>) o).name() + '"');
        } else {
            jsonValue = new JsonValue(this.toJsonObject(o));
        }
        return jsonValue;
    }
}
