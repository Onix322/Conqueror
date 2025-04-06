package org.entityManager;

public interface Entity<T> {

    Class<T> classOf();
    T get();
}
