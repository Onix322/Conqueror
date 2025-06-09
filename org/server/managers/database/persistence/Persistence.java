package org.server.managers.database.persistence;

public interface Persistence<T , ID extends Number> {

    T findById(Class<T> entity, ID id);

    boolean save(T entity) throws NoSuchFieldException, IllegalAccessException;
}
