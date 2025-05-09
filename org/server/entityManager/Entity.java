package org.server.entityManager;

public interface Entity<T> {

    Class<T> classOf();
    T get();
}
