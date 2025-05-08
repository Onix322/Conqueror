package org.entityManager;

import org.exepltions.NoEntityMatchesJson;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityManagerImpl implements EntityManager {

    private final Set<Class<?>> ENTITIES;

    private EntityManagerImpl() {
        this.ENTITIES = new HashSet<>();
    }

    private static class Init {
        private static final EntityManagerImpl INSTANCE = new EntityManagerImpl();
    }

    public static EntityManager getInstance() {
        return Init.INSTANCE;
    }

    @Override
    public Set<Class<?>> getEntities() {
        return Set.copyOf(ENTITIES);
    }

    @Override
    public <T> EntityManager registerEntityClass(Class<T> entity) {
        ENTITIES.add(entity);
        return this;
    }

    @Override
    public Class<?> askForClass(String[] fieldsNames){

        Class<?> gotClass = null;

        for(Class<?> entityClass : ENTITIES){
            Set<String> fields = Stream.of(entityClass.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            if(fields.containsAll(List.of(fieldsNames))){
                gotClass = entityClass;
            }
        }
        if(gotClass == null) throw new NoEntityMatchesJson();
        return gotClass;
    }

    @Override
    public <T> EntityManager removeEntityClass(Class<T> clazz) {
        ENTITIES.remove(clazz);
        return this;
    }

    @Override
    public <T> boolean contains(Class<T> clazz){
        return ENTITIES.contains(clazz);
    }
}
