package org.server.managers.database.databaseManager.schemaHandler.schemaManager;

import org.server.exepltions.IllegalClassException;
import org.server.managers.database.databaseManager.entityData.EntityColumn;
import org.server.managers.database.databaseManager.entityData.EntityTable;
import org.server.managers.database.driverManager.ConnectionManager;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Component
public class SchemaManager {
    private final ConnectionManager CONNECTION_MANAGER;
    private final FieldConvertor DB_FIELD_CONVERTOR;
    private final SqlQueryFactory SQL_FORMATTER;

    private SchemaManager(ConnectionManager connection, FieldConvertor fieldConvertor, SqlQueryFactory sqlQueryFactory) {
        this.CONNECTION_MANAGER = connection;
        this.DB_FIELD_CONVERTOR = fieldConvertor;
        this.SQL_FORMATTER = sqlQueryFactory;
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

    public void createEntityTable(Class<?> entity) throws SQLException {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        Field[] fields = entity.getDeclaredFields();
        List<EntityColumn> entityColumns = this.convertFields(fields);

        EntityTable entityTable = EntityTable.builder()
                .setName(entity.getAnnotation(Entity.class).name())
                .setColumns(entityColumns)
                .build();

        String tableSql = this.SQL_FORMATTER.createTableSql(entityTable);
        try (PreparedStatement preparedStatement = this.preparedStatement(tableSql)) {
            preparedStatement.execute();
        }
    }

    public boolean deleteEntityTable(Class<?> entity) throws SQLException {

        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        EntityTable entityTable = EntityTable.builder()
                .setName(entity.getAnnotation(Entity.class).name())
                .build();

        String sql = this.SQL_FORMATTER.deleteTableSql(entityTable);

        try (PreparedStatement preparedStatement = this.preparedStatement(sql)) {
            preparedStatement.execute();
            return true;
        }
    }

    private List<EntityColumn> convertFields(Field[] fields) {
        List<EntityColumn> entityColumns = new LinkedList<>();

        for (Field field : fields) {
            EntityColumn entityColumn = this.DB_FIELD_CONVERTOR.convertor(field);
            entityColumns.add(entityColumn);
        }

        return entityColumns;
    }

    public void stop() throws SQLException {
        System.out.println();
        this.CONNECTION_MANAGER.close();
    }

    public boolean existsTable(Class<?> entity) throws SQLException {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        EntityTable entityTable = EntityTable.builder()
                .setName(entity.getAnnotation(Entity.class).name())
                .build();
        String sql = this.SQL_FORMATTER.existTableSql(entityTable);

        try (PreparedStatement preparedStatement = this.preparedStatement(sql)) {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        }
    }
}
