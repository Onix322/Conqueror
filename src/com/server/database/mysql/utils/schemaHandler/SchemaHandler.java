package src.com.server.database.mysql.utils.schemaHandler;

import src.com.server.database.mysql.utils.schemaHandler.schemaManager.SchemaManager;
import src.com.server.database.mysql.utils.schemaHandler.schemaMode.CurrentSchemaMode;
import src.com.server.database.mysql.utils.schemaHandler.schemaMode.SchemaMode;
import src.com.server.annotations.component.Component;
import src.com.server.annotations.entity.Column;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

/*
 * The Main purpose of this class is to dictate how the schema is managed and handled constraint by
 * the result coming from CurrentSchemaMode class and automatically makes actions over the schema
 *
 * USED ONLY ON STARTUP
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
     * does NOTHING over the schema.
     * */
    public boolean none() {
        return true;
    }

    /*
     * UPDATING existing entities without deleting any values / configuration
     * Compares the metadata from database and entity metadata for any differences
     * */
    public boolean update(Iterator<Class<?>> entityIterator) throws SQLException {

        // 1. identify new modifications made on schema.
        while (entityIterator.hasNext()) {
            Class<?> entity = entityIterator.next();

            if (!this.SCHEMA_MANAGER.existsTable(entity)) {
                this.SCHEMA_MANAGER.createEntityTable(entity);
            }

            List<Field> fieldsFound = new ArrayList<>(List.of(entity.getDeclaredFields()));
            Iterator<Field> fields = fieldsFound.listIterator();
            List<String> columnNames = new ArrayList<>(Arrays.stream(this.SCHEMA_MANAGER.getColumnNames(entity)).toList());

            while (fields.hasNext()) {
                Field field = fields.next();
                if (!field.isAnnotationPresent(Column.class)) {
                    fields.remove();
                    fieldsFound.remove(field);
                    continue;
                }

                String fieldName = field.getAnnotation(Column.class).name();
                if (columnNames.contains(field.getName())) {
                    fields.remove();
                    fieldsFound.remove(field);
                    columnNames.remove(fieldName);
                }
            }

            // 2. apply modifications

            // 2.1 add remaining the remaining fields with no columns in db
            if (!fieldsFound.isEmpty()) {
                fieldsFound.forEach(f -> this.SCHEMA_MANAGER.addColumn(entity, f));
            }

            // 2.2 delete remaining columns with no fields in entity class
            if (!columnNames.isEmpty()) {
                columnNames.forEach(cn -> this.SCHEMA_MANAGER.deleteColumn(entity, cn));
            }
        }
        return false;
    }

    /*
     * RECREATING all entities from zero
     * */
    public boolean create(Iterator<Class<?>> entityIterator) throws SQLException {
        while (entityIterator.hasNext()) {
            Class<?> entity = entityIterator.next();
            if (this.SCHEMA_MANAGER.existsTable(entity)) {
                this.SCHEMA_MANAGER.deleteEntityTable(entity);
            }
            this.SCHEMA_MANAGER.createEntityTable(entity);
        }
        return true;
    }
}
