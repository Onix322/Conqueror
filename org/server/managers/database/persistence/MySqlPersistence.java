package org.server.managers.database.persistence;

import org.server.exceptions.AnnotationException;
import org.server.managers.database.databaseManager.entityData.EntityColumn;
import org.server.managers.database.databaseManager.entityData.EntityTable;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.FieldConvertor;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SchemaManager;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SqlStatements;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Column;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

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
            throw new AnnotationException("@Entity is not present for: " + entity.getClass().getName());
        }
        String tableName = entity.getAnnotation(Entity.class).name();
        List<EntityColumn> columns = this.FIELD_CONVERTOR.convertor(entity.getClass());
        return EntityTable.builder()
                .setName(tableName)
                .setColumns(columns)
                .build();
    }

    @Override
    public T findById(Class<T> entity, ID id) {
        try {
            String sql = this.SQL_STATEMENTS.findByIdSql(entity, id);
            System.out.println(sql);
            //TODO create object constructor
            ResultSet resultSet = this.SCHEMA_MANAGER.preparedStatement(sql).executeQuery();
            Object instance = entity.getConstructor().newInstance();
            while (resultSet.next()){
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
            }

            return entity.cast(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean save(T entity) {
        try {
            EntityTable entityTable = this.transform(entity.getClass());
            String sql = this.SQL_STATEMENTS.addRowSql(entityTable, entity);
            System.out.println(sql);
            PreparedStatement preparedStatement = this.SCHEMA_MANAGER.preparedStatement(sql);
            Object[] values = this.SQL_STATEMENTS.getColumnValue(entityTable, entity)
                    .values()
                    .toArray();

            this.STATEMENT_SETTER.autoSetter(preparedStatement, values).execute();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
