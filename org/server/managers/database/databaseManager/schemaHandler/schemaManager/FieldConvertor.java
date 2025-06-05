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

}
