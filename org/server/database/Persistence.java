package org.server.database;

import java.util.Optional;

/**
 * This interface defines the core persistence methods used throughout the application.
 * These methods must be implemented when a new database driver is added.
 * All database operations in the application should rely on this interface for consistency.
 */
public interface Persistence<T, ID extends Number> {

    Optional<T> findById(Class<T> entity, ID id);

    void save(T entity);
}