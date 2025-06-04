package org.server.managers.databaseManager;

import org.server.exepltions.AnnotationException;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Column;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.List;

@Component
public class FieldConvertor {

    private final JDBCTypeResolver JDBC_TYPE_RESOLVER;

    private FieldConvertor(JDBCTypeResolver jdbcTypeResolver) {
        this.JDBC_TYPE_RESOLVER = jdbcTypeResolver;
    }

    /*
     * Is converting a field in an EntityColumn
     * */
    public EntityColumn convertor(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new AnnotationException("No @Column annotation present on field: " + field);
        }

        String columnName = field.getAnnotation(Column.class).name();
        boolean unique = field.getAnnotation(Column.class).unique();
        boolean primaryKey = field.getAnnotation(Column.class).primary();
        boolean nullable = field.getAnnotation(Column.class).nullable();
        SQLType type = JDBC_TYPE_RESOLVER.getJdbcType(field);

        return new EntityColumn(columnName, unique, primaryKey, nullable, type);
    }

    /*
     * Is creating sql table column syntax e.g: column_name VARCHAR(255) UNIQUE NOT NULL
     * */
    public String sqlFormat(EntityColumn entityColumn) {
        String base = entityColumn.getColumnName() + " " + entityColumn.getType();

        if (entityColumn.getType().equals(JDBCType.VARCHAR)) base += "(255)";
        if (entityColumn.isPrimaryKey()) return base + " PRIMARY KEY";
        if (entityColumn.isUnique()) base += " UNIQUE";
        if (entityColumn.isNullable()) base += " NOT NULL";
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
            stringBuilder.append(this.sqlFormat(ec));
            if (i < entityColumns.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
