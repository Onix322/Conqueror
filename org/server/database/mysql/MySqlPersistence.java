package org.server.database.mysql;

import org.server.annotations.component.Component;
import org.server.annotations.entity.Column;
import org.server.annotations.entity.Entity;
import org.server.database.Persistence;
import org.server.database.mysql.utils.ColumnValueConvertor;
import org.server.database.mysql.utils.PreparedStatementSetter;
import org.server.database.mysql.utils.entityData.EntityColumn;
import org.server.database.mysql.utils.entityData.EntityTable;
import org.server.database.mysql.utils.schemaHandler.schemaManager.FieldConvertor;
import org.server.database.mysql.utils.schemaHandler.schemaManager.SchemaManager;
import org.server.database.mysql.utils.schemaHandler.schemaManager.SqlStatements;
import org.server.exceptions.AnnotationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/*
* Implementation of Persistence.class (interface)
* */
@Component
public class MySqlPersistence<T, ID extends Number> implements Persistence<T, ID> {

    private final SqlStatements SQL_STATEMENTS;
    private final FieldConvertor FIELD_CONVERTOR;
    private final SchemaManager SCHEMA_MANAGER;
    private final PreparedStatementSetter STATEMENT_SETTER;
    private final ColumnValueConvertor COLUMN_VALUE_CONVERTOR;

    protected MySqlPersistence(SqlStatements sqlStatements, FieldConvertor fieldConvertor, SchemaManager schemaManager, PreparedStatementSetter statementSetter, ColumnValueConvertor columnValueConvertor) {
        this.SQL_STATEMENTS = sqlStatements;
        this.FIELD_CONVERTOR = fieldConvertor;
        this.SCHEMA_MANAGER = schemaManager;
        this.STATEMENT_SETTER = statementSetter;
        this.COLUMN_VALUE_CONVERTOR = columnValueConvertor;
    }

    private EntityTable transform(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new AnnotationException("@Entity is not present for: " + entity.getName());
        }
        String tableName = entity.getAnnotation(Entity.class).name();
        List<EntityColumn> columns = this.FIELD_CONVERTOR.convertor(entity);
        return EntityTable.builder()
                .setName(tableName)
                .setColumns(columns)
                .build();
    }

    @Override
    public Optional<T> findById(Class<T> entity, ID id) {
        try {
            String sql = this.SQL_STATEMENTS.findByIdSql(entity, id);
            ResultSet resultSet = this.SCHEMA_MANAGER.preparedStatement(sql)
                    .executeQuery();
            if (!resultSet.next()) return Optional.empty();
            T instance = this.instanceGenerator(entity, resultSet);
            return Optional.of(instance);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll(Class<T> entity) {
        try {
            String sql = this.SQL_STATEMENTS.findAllSql(entity);
            ResultSet resultSet = this.SCHEMA_MANAGER.preparedStatement(sql)
                    .executeQuery();
            List<T> entities = new LinkedList<>();
            while (resultSet.next()) {
                T instance = this.instanceGenerator(entity, resultSet);
                entities.add(instance);
            }
            return entities;
        } catch (SQLException | NoSuchFieldException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeById(Class<T> entity, ID id) {
        try {
            String sql = this.SQL_STATEMENTS.removeByIdSql(entity, id);
            return this.SCHEMA_MANAGER.preparedStatement(sql)
                    .executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public T save(T entity) {
        try {
            EntityTable entityTable = this.transform(entity.getClass());
            String sql = this.SQL_STATEMENTS.addRowSql(entityTable, entity);
            PreparedStatement preparedStatement = this.SCHEMA_MANAGER.preparedStatement(sql);
            Object[] values = this.SQL_STATEMENTS.getColumnValue(entityTable, entity)
                    .values()
                    .toArray();

            this.STATEMENT_SETTER.autoSetter(preparedStatement, values).execute();
            return entity;
        } catch (NoSuchFieldException | SQLException | IllegalAccessException e) {
            throw new RuntimeException("Can't save the entity in database. " + e.getMessage());
        }
    }

    private T instanceGenerator(Class<T> entity, ResultSet resultSet) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException, NoSuchFieldException {
        Object instance = entity.getConstructor().newInstance();
        Field[] fields = instance.getClass().getDeclaredFields();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSetMetaData.getColumnName(i);
            Object columnValue = this.COLUMN_VALUE_CONVERTOR.autoConvertor(resultSet, i);
            Field field = Arrays.stream(fields)
                    .filter(f -> f.getAnnotation(Column.class).name().equals(columnName))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchFieldException("Field with @Column.name() set: " + columnName + " not found!"));
            field.setAccessible(true);
            field.set(instance, columnValue);
        }

        return entity.cast(instance);
    }
}
