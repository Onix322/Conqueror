package org.server.entityManager;


import java.util.Set;

public sealed interface EntityManager permits EntityManagerImpl{

    Set<Class<?>> getEntities();

    <T> EntityManager registerEntityClass(Class<T> entity);

    <T> EntityManager removeEntityClass(Class<T> entity);

    Class<?> askForClass(String[] fieldsNames);

    <T> boolean contains(Class<T> clazz);
}
