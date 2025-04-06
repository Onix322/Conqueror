package org.entityManager;

import java.util.*;

public class EntityManagerImpl implements EntityManager {

    private final Map<String, Class<?>> ENTITIES;

    private EntityManagerImpl() {
        this.ENTITIES = new HashMap<>();
    }

    private static class Init {
        private static final EntityManagerImpl INSTANCE = new EntityManagerImpl();
    }

    public static EntityManager getInstance() {
        return Init.INSTANCE;
    }

    @Override
    public Map<String, Class<?>> getEntities() {
        return Map.copyOf(ENTITIES);
    }

    @Override
    public <T> EntityManager registerEntityClass(String name, Class<T> entity) {
        ENTITIES.put(name, entity);
        return this;
    }

    @Override
    public EntityManager removeEntityClass(String name) {
        ENTITIES.remove(name);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> requestEntityClass(String name) {
        return (Class<T>) ENTITIES.get(name);
    }
}
