package framework.src.server.parsers.json.utils.properties;

/**
 * JsonKeyValue is a generic interface that represents a key-value pair in JSON.
 * It provides a method to retrieve the value associated with the key.
 *
 * @param <R> the type of the value
 */
public interface JsonKeyValue<R> {
    R get();
}
