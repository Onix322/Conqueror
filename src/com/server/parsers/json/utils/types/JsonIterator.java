package src.com.server.parsers.json.utils.types;

/**
 * JsonIterator is a generic interface that defines methods for getting and setting
 * an array of type T. It is used to iterate over JSON data structures.
 *
 * @param <T> the type of elements in the array
 */
public interface JsonIterator<T> {
    T[] get();
    void set(T[] array);
}
