package org.server.managers.databaseManager;

import org.server.exepltions.IllegalClassException;
import org.server.managers.driverManager.ConnectionManager;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseManager {
    private final ConnectionManager CONNECTION_MANAGER;
    private final FieldConvertor DB_FIELD_CONVERTOR;

    private DatabaseManager(ConnectionManager connection, FieldConvertor fieldConvertor) {
        this.CONNECTION_MANAGER = connection;
        this.DB_FIELD_CONVERTOR = fieldConvertor;
    }

    public PreparedStatement preparedStatement(String sql) throws RuntimeException {
        try {
            return this.CONNECTION_MANAGER.connect().prepareStatement(sql);
        } catch (MalformedURLException | SQLException | URISyntaxException | ClassNotFoundException |
                 InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityTable createEntityTable(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        Field[] fields = entity.getDeclaredFields();
        List<EntityColumn> entityColumns = this.convertFields(fields);

        return new EntityTable(
                entity.getAnnotation(Entity.class).name(),
                entityColumns
        );
    }

    public String createTableSqlFormat(EntityTable entityTable) {

        //TODO 1. column per field
        //TODO 2. annotations for columns with attributes (name, uniques, nullable, primary_key)
        //TODO 3. annotations for relationships (1-1, 1-N, N-1, N-N)

        return "CREATE TABLE "
                + entityTable.getName()
                + this.DB_FIELD_CONVERTOR.slqEntityColumnsFormat(entityTable.getColumns());
    }

    private List<EntityColumn> convertFields(Field[] fields) {
        List<EntityColumn> entityColumns = new LinkedList<>();

        for (Field field : fields) {
            EntityColumn entityColumn = this.DB_FIELD_CONVERTOR.convertor(field);
            entityColumns.add(entityColumn);
        }

        return entityColumns;
    }

    public void autoload(Set<Class<?>> applicationEntities) throws SQLException {
        Iterator<Class<?>> entityIterator = applicationEntities.stream().iterator();
//        PreparedStatement preparedStatement;
        while (entityIterator.hasNext()) {
            Class<?> entity = entityIterator.next();
            EntityTable entityTable = this.createEntityTable(entity);
            if (this.existsTable(entityTable)) {
                System.out.println("[DatabaseManager] -> " + "Existing table for entity: '" + entity.getSimpleName() +
                        "', table name: '" + entityTable.getName() + "'");
                continue;
            }
            String tableSql = this.createTableSqlFormat(entityTable);
            try (PreparedStatement preparedStatement = this.preparedStatement(tableSql)) {
                preparedStatement.execute();
            }
        }
    }

    public void stop() throws SQLException {
        System.out.println();
        this.CONNECTION_MANAGER.close();
    }

    public boolean existsTable(EntityTable entityTable) throws SQLException {
        String sql = "SELECT EXISTS (" +
                "  SELECT 1" +
                "  FROM INFORMATION_SCHEMA.TABLES" +
                "  WHERE TABLE_NAME = '" + entityTable.getName() +
                "') AS table_exists";
        try (PreparedStatement preparedStatement = this.preparedStatement(sql)) {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        }
    }
}
