package org.server.entityManager;

import org.server.exepltions.NoEntityMatchesJson;

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
        private static EntityManagerImpl INSTANCE = null;
    }

    public static synchronized void init(){
        if(Init.INSTANCE == null){
            Init.INSTANCE = new EntityManagerImpl();
        }
    }

    public static EntityManagerImpl getInstance(){
        if(Init.INSTANCE == null){
            throw new IllegalStateException("EntityManager not initialized. Use EntityManagerImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public Set<Class<?>> getEntities() {
        return Set.copyOf(ENTITIES);
    }

    @Override
    public <T> EntityManager registerEntityClass(Class<T> entity) {
        ENTITIES.add(entity);

        //TODO when called automatically make an entity in DB
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
