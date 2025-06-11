package org.server.database;

import java.util.Optional;

/**
 * This interface defines the core persistence methods used throughout the application.
 * These methods must be implemented when a new database driver is added.
 * All database operations in the application should rely on this interface for consistency.

 * How to use it:
 * 1. Create a folder where the implementation of the interface will be
 * 2. Create the implementation inside the folder.
 * 3. Use the implementation in repositories.
 */
public interface Persistence<T, ID extends Number> {

    Optional<T> findById(Class<T> entity, ID id);

    void save(T entity);
}
