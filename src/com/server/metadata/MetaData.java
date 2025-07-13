package src.com.server.metadata;

/**
 * MetaData interface represents a generic metadata structure.
 * It provides a method to retrieve metadata of type R.
 *
 * @param <R> the type of metadata
 */
public interface MetaData<R> {
    R getMetaData();
}
