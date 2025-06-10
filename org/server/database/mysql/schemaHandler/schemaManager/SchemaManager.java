package org.server.database.mysql.schemaHandler.schemaManager;

import org.server.exceptions.AnnotationException;
import org.server.exceptions.IllegalClassException;
import org.server.database.mysql.entityData.EntityColumn;
import org.server.database.mysql.entityData.EntityTable;
import org.server.database.mysql.driverManager.ConnectionManager;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Component
public class SchemaManager {
    private final ConnectionManager CONNECTION_MANAGER;
    private final FieldConvertor DB_FIELD_CONVERTOR;
    private final SqlStatements SQL_FORMATTER;

    private SchemaManager(ConnectionManager connection, FieldConvertor fieldConvertor, SqlStatements sqlStatements) {
        this.CONNECTION_MANAGER = connection;
        this.DB_FIELD_CONVERTOR = fieldConvertor;
        this.SQL_FORMATTER = sqlStatements;
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
        List<EntityColumn> entityColumns = this.DB_FIELD_CONVERTOR.convertor(fields);

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

    public boolean addColumn(Class<?> entity, Field field) {
        EntityTable entityTable = EntityTable.builder()
                .setName(entity.getAnnotation(Entity.class).name())
                .build();

        EntityColumn entityColumn = this.DB_FIELD_CONVERTOR.convertor(field);

        String sql = this.SQL_FORMATTER.addColumnSql(entityTable, entityColumn);

        try (PreparedStatement preparedStatement = this.preparedStatement(sql)) {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteColumn(Class<?> entity, String columnName) {
        EntityTable entityTable = EntityTable.builder()
                .setName(entity.getAnnotation(Entity.class).name())
                .build();

        String sql = this.SQL_FORMATTER.deleteColumnSql(entityTable, columnName);

        try (PreparedStatement preparedStatement = this.preparedStatement(sql)) {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getColumnNames(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new AnnotationException("Annotation not present on: " + entity.getName());
        }
        try {
            Connection connection = this.CONNECTION_MANAGER.connect();
            String catalogName = connection.getCatalog();
            String tableName = entity.getAnnotation(Entity.class).name();
            ResultSet resultSet = connection.getMetaData()
                    .getColumns(catalogName, null, tableName, null);
            List<String> columnNames = new LinkedList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            return columnNames.toArray(String[]::new);
        } catch (MalformedURLException | SQLException | URISyntaxException | ClassNotFoundException |
                 InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
