package org.server.managers.database.entityManager;

import org.server.exepltions.NoEntityMatchesJson;
import org.server.managers.database.databaseManager.schemaHandler.SchemaHandler;
import org.server.processors.context.ContextProcessor;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class EntityManagerImpl implements EntityManager {

    private final ContextProcessor CONTEXT_PROCESSOR;
    private final SchemaHandler SCHEMA_HANDLER;

    private EntityManagerImpl(ContextProcessor contextProcessor, SchemaHandler schemaHandler) {
        this.CONTEXT_PROCESSOR = contextProcessor;
        this.SCHEMA_HANDLER = schemaHandler;
    }

    @Override
    public Class<?> askForClass(String[] fieldsNames) {

        Set<Class<?>> entities = this.CONTEXT_PROCESSOR.getEntities();

        Class<?> gotClass = null;

        for (Class<?> entityClass : entities) {
            Set<String> fields = Stream.of(entityClass.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            if (fields.containsAll(List.of(fieldsNames))) {
                gotClass = entityClass;
            }
        }
        if (gotClass == null) throw new NoEntityMatchesJson();
        return gotClass;
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        return CONTEXT_PROCESSOR.getEntities().contains(clazz);
    }

    @Override
    public void autoload() throws SQLException {
        this.SCHEMA_HANDLER.autoload(this.CONTEXT_PROCESSOR.getEntities());
    }
}
