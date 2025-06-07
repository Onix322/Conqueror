package org.server.managers.database.databaseManager.schemaHandler.schemaManager;

import org.server.exepltions.AnnotationException;
import org.server.managers.database.databaseManager.entityData.EntityColumn;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Column;

import java.lang.reflect.Field;
import java.sql.SQLType;

@Component
public class FieldConvertor {

    private final JDBCTypeResolver JDBC_TYPE_RESOLVER;

    private FieldConvertor(JDBCTypeResolver jdbcTypeResolver) {
        this.JDBC_TYPE_RESOLVER = jdbcTypeResolver;
    }

    /*
     * Is converting a field in an EntityColumn based on metadata provided by @Column
     * */
    public EntityColumn convertor(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new AnnotationException("No @Column annotation present on field: " + field);
        }

        Column column = field.getAnnotation(Column.class);
        String columnName = column.name();
        boolean unique = column.unique();
        boolean primaryKey = column.primary();
        boolean nullable = column.nullable();
        boolean autoIncrement = column.autoIncrement();
        SQLType type = JDBC_TYPE_RESOLVER.getJdbcType(field);

        return new EntityColumn(columnName, unique, primaryKey, nullable, autoIncrement, type);
    }

}
