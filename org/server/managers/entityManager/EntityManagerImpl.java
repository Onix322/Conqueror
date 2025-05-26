package org.server.managers.entityManager;

import org.server.managers.databaseManager.DatabaseManager;
import org.server.exepltions.NoEntityMatchesJson;
import org.server.processors.context.ContextProcessor;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class EntityManagerImpl implements EntityManager {

    private final ContextProcessor CONTEXT_PROCESSOR;
    private final DatabaseManager DATABASE_MANAGER;

    private EntityManagerImpl(ContextProcessor contextProcessor, DatabaseManager databaseManager) {
        this.CONTEXT_PROCESSOR = contextProcessor;
        this.DATABASE_MANAGER = databaseManager;
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

    @Override
    public void autoload() throws MalformedURLException, SQLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.DATABASE_MANAGER.autoload(this.CONTEXT_PROCESSOR.getEntities());
    }
}
