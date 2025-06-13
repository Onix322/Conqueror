package org.server.managers.entityManager;

import org.server.annotations.component.Component;
import org.server.database.mysql.utils.schemaHandler.SchemaHandler;
import org.server.processors.context.ApplicationContext;

import java.sql.SQLException;

@Component
public final class EntityManagerImpl implements EntityManager {

    private final ApplicationContext CONTEXT_PROCESSOR;
    private final SchemaHandler SCHEMA_HANDLER;

    private EntityManagerImpl(ApplicationContext applicationContext, SchemaHandler schemaHandler) {
        this.CONTEXT_PROCESSOR = applicationContext;
        this.SCHEMA_HANDLER = schemaHandler;
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
