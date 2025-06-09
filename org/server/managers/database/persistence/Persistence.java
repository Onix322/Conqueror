package org.server.managers.database.persistence;

import java.util.Optional;

public interface Persistence<T , ID extends Number> {

    Optional<T> findById(Class<T> entity, ID id);

    boolean save(T entity) throws NoSuchFieldException, IllegalAccessException;
}
