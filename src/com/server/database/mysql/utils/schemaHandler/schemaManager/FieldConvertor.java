package src.com.server.database.mysql.utils.schemaHandler.schemaManager;

import src.com.server.exceptions.AnnotationException;
import src.com.server.database.mysql.utils.entityData.EntityColumn;
import src.com.server.annotations.component.Component;
import src.com.server.annotations.entity.Column;
import src.com.server.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.sql.SQLType;
import java.util.LinkedList;
import java.util.List;

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
        boolean idColumn = column.idColumn();
        SQLType type = JDBC_TYPE_RESOLVER.getJdbcType(field);

        return new EntityColumn(columnName, unique, primaryKey, nullable, autoIncrement, idColumn, type);
    }

    public List<EntityColumn> convertor(Field[] fields) {
        List<EntityColumn> entityColumns = new LinkedList<>();

        for (Field field : fields) {
            EntityColumn entityColumn = this.convertor(field);
            entityColumns.add(entityColumn);
        }

        return entityColumns;
    }

    public List<EntityColumn> convertor(Class<?> entityCls){
        if(!entityCls.isAnnotationPresent(Entity.class)){
            throw new AnnotationException("No @Entity annotation present on field: " + entityCls);
        }
        Field[] fields = entityCls.getDeclaredFields();
        return this.convertor(fields);
    }
}
