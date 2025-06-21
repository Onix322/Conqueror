package src.com.server.database;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines the core persistence methods used throughout the application.
 * These methods must be implemented when a new database driver is added.
 * All database operations in the application should rely on this interface for consistency.
 * <p>
 * How to use it:
 * 1. Create a folder where the implementation of the interface will be
 * 2. Create the implementation inside the folder.
 * 3. Use the implementation in repositories.
 */
public interface Persistence<T, ID extends Number> {

    Optional<T> findById(Class<T> entity, ID id);

    List<T> findAll(Class<T> entity);

    boolean removeById(Class<T> entity, ID id);

    T save(T entity);

    T update(Class<T> entityClass, Object entity, ID id);

    T modify(Class<T> entityClass, Object modifier, ID id);
}
