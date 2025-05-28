package org.server.managers.databaseManager;

import org.server.exepltions.AnnotationException;
import org.server.exepltions.IncompatibleTypeChangeException;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Column;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseFieldConvertor {

    public EntityColumn convertor(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new AnnotationException("No @Column annotation present on field: " + field);
        }

        String columnName = field.getAnnotation(Column.class).name();
        boolean unique = field.getAnnotation(Column.class).unique();
        boolean primaryKey = field.getAnnotation(Column.class).primary();
        boolean nullable = field.getAnnotation(Column.class).nullable();
        int type = this.handleType(field.getType());

        return new EntityColumn(columnName, unique, primaryKey, nullable, type);
    }

    public String sqlFormat(EntityColumn entityColumn){
        String base = entityColumn.getColumnName() + " " + entityColumn.getType();

        if(entityColumn.isPrimaryKey()) return base + " PRIMARY KEY";
        if(entityColumn.isUnique()) base += " UNIQUE";
        if(entityColumn.isNullable()) base += " NOT NULL";
        return base;

    }

    public String slqEntityColumnsFormat(List<EntityColumn> entityColumns){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');

        for (int i = 0; i < entityColumns.size(); i++) {
            EntityColumn ec = entityColumns.get(i);
            stringBuilder.append(this.sqlFormat(ec));
            if(i < entityColumns.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }


    public int handleType(Class<?> type) {
        if (type.isAssignableFrom(String.class)) return Types.VARCHAR;
        else if (type.isAssignableFrom(Integer.class)) return Types.INTEGER;
        else if (type.isAssignableFrom(Double.class)) return Types.DOUBLE;
        else if (type.isAssignableFrom(Timestamp.class)) return Types.TIMESTAMP;
        else if (type.isAssignableFrom(List.class) ||
                type.isAssignableFrom(Set.class)) return Types.ARRAY;

        throw new IncompatibleTypeChangeException("Not a supported type: " + type);
    }

}
