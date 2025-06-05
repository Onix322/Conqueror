package org.server.managers.database.databaseManager.schemaHandler;

import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SchemaManager;
import org.server.managers.database.databaseManager.schemaHandler.schemaMode.CurrentSchemaMode;
import org.server.managers.database.databaseManager.schemaHandler.schemaMode.SchemaMode;
import org.server.processors.context.annotations.Component;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

/*
* Main purpose of this class is to dictate how the schema is managed and handled constraint by
* the result coming from CurrentSchemaMode class and automatically makes actions over the schema
* */
@Component
public class SchemaHandler {
    private final SchemaMode SCHEMA_MODE;
    private final SchemaManager SCHEMA_MANAGER;

    private SchemaHandler(CurrentSchemaMode currentSchemaMode, SchemaManager schemaManager) {
        this.SCHEMA_MODE = currentSchemaMode.getCurrentSchemaMode();
        this.SCHEMA_MANAGER = schemaManager;
    }

    public void autoload(Set<Class<?>> applicationEntities) throws SQLException {
        Iterator<Class<?>> entityIterator = applicationEntities.stream().iterator();
        this.handle(entityIterator);
    }

    public boolean handle(Iterator<Class<?>> entityIterator) throws SQLException {
        System.out.println("[" + this.getClass().getSimpleName() + "] -> Setting schema-mode on: " + SCHEMA_MODE);
        return switch (SCHEMA_MODE) {
            case NONE -> this.none();
            case CREATE -> this.create(entityIterator);
            case UPDATE -> this.update(entityIterator);
        };
    }

    /*
     * does NOTHING
     * */
    public boolean none() {
        return true;
    }

    /*
     * UPDATING existing entities
     * */
    public boolean update(Iterator<Class<?>> entityIterator) {
        return false;
    }

    /*
     * RECREATING all entities from zero
     * */
    public boolean create(Iterator<Class<?>> entityIterator) throws SQLException {
        while (entityIterator.hasNext()) {
            Class<?> entity = entityIterator.next();
            if(this.SCHEMA_MANAGER.existsTable(entity)){
                this.SCHEMA_MANAGER.deleteEntityTable(entity);
            }
            this.SCHEMA_MANAGER.createEntityTable(entity);
        }
        return true;
    }
}
