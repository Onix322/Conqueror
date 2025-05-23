package org.server.entityManager;


public sealed interface EntityManager permits EntityManagerImpl {

    Class<?> askForClass(String[] fieldsNames);

    <T> boolean contains(Class<T> clazz);
}
