package org.server.managers.entityManager;

import org.server.exceptions.NoEntityMatchesJson;
import org.server.database.mysql.utils.schemaHandler.SchemaHandler;
import org.server.processors.context.ApplicationContext;
import org.server.annotations.component.Component;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class EntityManagerImpl implements EntityManager {

    private final ApplicationContext CONTEXT_PROCESSOR;
    private final SchemaHandler SCHEMA_HANDLER;

    private EntityManagerImpl(ApplicationContext applicationContext, SchemaHandler schemaHandler) {
        this.CONTEXT_PROCESSOR = applicationContext;
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
