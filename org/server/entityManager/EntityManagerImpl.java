package org.server.entityManager;

import org.server.exepltions.NoEntityMatchesJson;
import org.server.processors.context.ContextProcessor;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class EntityManagerImpl implements EntityManager {

    private final ContextProcessor CONTEXT_PROCESSOR;

    private EntityManagerImpl(ContextProcessor CONTEXT_PROCESSOR) {
        this.CONTEXT_PROCESSOR = CONTEXT_PROCESSOR;
    }

    @Override
    public Class<?> askForClass(String[] fieldsNames){

        Set<Class<?>> entities = this.CONTEXT_PROCESSOR.getEntities();

        Class<?> gotClass = null;

        for(Class<?> entityClass : entities){
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
    public <T> boolean contains(Class<T> clazz){
        return CONTEXT_PROCESSOR.getEntities().contains(clazz);
    }
}
