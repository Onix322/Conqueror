package org.entityManager;


import java.util.Map;

public interface EntityManager {

    Map<String, Class<?>> getEntities();

    <T> EntityManager registerEntityClass(String name, Class<T> entity);

    EntityManager removeEntityClass(String name);

    <T> Class<T> requestEntityClass(String name);
}
