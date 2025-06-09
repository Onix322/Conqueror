package org.server.managers.database.databaseManager.schemaHandler.schemaManager;

import org.server.exceptions.AnnotationException;
import org.server.managers.database.databaseManager.entityData.EntityColumn;
import org.server.managers.database.databaseManager.entityData.EntityTable;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Column;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.*;

/*
* Factoring SQL strings.
* */
@Component
public class SqlStatements {

    private SqlStatements() {
    }

    public <T> Map<String, Object> getColumnValue(EntityTable entityTable, T entity) throws IllegalAccessException, NoSuchFieldException {
        if(entityTable.getColumns().size() != entity.getClass().getDeclaredFields().length){
            throw new IllegalStateException(entityTable.getName() + " columns number != " + entity.getClass().getName() + " field number.");
        }
        Map<String, Object> columnValues = new HashMap<>();

        List<EntityColumn> entityColumns = entityTable.getColumns();

        for(EntityColumn ec : entityColumns){

            Field field = Arrays.stream(entity.getClass()
                            .getDeclaredFields())
                    .peek(f -> f.setAccessible(true))
                    .filter(f -> f.getAnnotation(Column.class).name().equals(ec.getColumnName()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchFieldException("No column field for: " + ec));

            Object value = field.get(entity);

            columnValues.put(ec.getColumnName(), value);
        }

        return columnValues;
    }

    public <T, ID> String findByIdSql(Class<T> entity, ID id) throws NoSuchFieldException {
        if(!entity.isAnnotationPresent(Entity.class)){
            throw new AnnotationException("@Entity not present on: " + entity.getName());
        }

        String idFiledColumnName = Arrays.stream(entity.getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class).idColumn())
                .findFirst()
                .orElseThrow(() -> new NoSuchFieldException("No field with @Column annotation property 'idColumn' set 'true'."))
                .getAnnotation(Column.class)
                .name();

        return "SELECT * FROM "
                + entity.getAnnotation(Entity.class).name()
                + " WHERE "
                + idFiledColumnName
                + "=" + id;
    }

    public <T> String addRowSql(EntityTable entityTable, T entity) throws NoSuchFieldException, IllegalAccessException {

        Map<String, Object> columnValues = this.getColumnValue(entityTable, entity);
        String columnsToSelectSQL = '(' + String.join(",", columnValues.keySet().toArray(String[]::new)) + ')';
        StringBuilder sql = new StringBuilder("INSERT INTO "
                + entityTable.getName()
                + columnsToSelectSQL
                + " VALUES (")
                .append("?,".repeat(columnValues.size()));

        sql.deleteCharAt(sql.length() - 1);
        sql.append(')');
        return sql.toString();
    }

    public String addColumnSql(EntityTable entityTable, EntityColumn entityColumn){
        return "ALTER TABLE " +
                entityTable.getName() +
                " ADD " + this.entityColumnSql(entityColumn);
    }

    public String deleteColumnSql(EntityTable entityTable, String columName){
        return "ALTER TABLE " +
                entityTable.getName() +
                " DROP COLUMN " + columName;
    }

    public String createTableSql(EntityTable entityTable) {

        //TODO 3. annotations for relationships (1-1, 1-N, N-1, N-N);

        return "CREATE TABLE "
                + entityTable.getName()
                + this.slqEntityColumnsFormat(entityTable.getColumns());
    }

    public String deleteTableSql(EntityTable entityTable) {
        return "DROP TABLE " + entityTable.getName();
    }

    public String existTableSql(EntityTable entityTable){
        return "SELECT EXISTS (" +
                "  SELECT 1" +
                "  FROM INFORMATION_SCHEMA.TABLES" +
                "  WHERE TABLE_NAME = '" + entityTable.getName() +
                "') AS table_exists";
    }

    /*
     * Is creating SQL table column syntax e.g.: column_name VARCHAR(255) UNIQUE NOT NULL
     * */
    public String entityColumnSql(EntityColumn entityColumn) {
        String base = entityColumn.getColumnName() + " " + entityColumn.getType();
        if (entityColumn.getType().equals(JDBCType.VARCHAR)) base += "(255)";
        if (entityColumn.isPrimaryKey()) base += " PRIMARY KEY";
        if (entityColumn.isAutoIncrement()) base += " AUTO_INCREMENT";
        if (entityColumn.isUnique()) base += " UNIQUE";
        if (!entityColumn.isNullable()) base += " NOT NULL";
        return base;
    }

    /*
     * Is creating MULTIPLE sql table column syntax e.g:
     * (column_name1 VARCHAR(255) UNIQUE NOT NULL, column_name2 VARCHAR(255) UNIQUE NOT NULL)
     * */
    public String slqEntityColumnsFormat(List<EntityColumn> entityColumns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');

        for (int i = 0; i < entityColumns.size(); i++) {
            EntityColumn ec = entityColumns.get(i);
            stringBuilder.append(this.entityColumnSql(ec));
            if (i < entityColumns.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
