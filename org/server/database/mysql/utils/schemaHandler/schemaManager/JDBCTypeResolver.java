package org.server.database.mysql.utils.schemaHandler.schemaManager;

import org.server.annotations.component.Component;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLType;
import java.sql.Types;
import java.util.Collection;
import java.util.Map;

@Component
public class JDBCTypeResolver {

    private JDBCTypeResolver() {
    }

    private static final Map<Class<?>, SQLType> TYPE_MAP = Map.ofEntries(
            Map.entry(String.class, JDBCType.VARCHAR),
            Map.entry(byte[].class, JDBCType.VARBINARY),
            Map.entry(boolean.class, JDBCType.BOOLEAN),
            Map.entry(Boolean.class, JDBCType.BOOLEAN),
            Map.entry(short.class, JDBCType.SMALLINT),
            Map.entry(Short.class, JDBCType.SMALLINT),
            Map.entry(int.class, JDBCType.INTEGER),
            Map.entry(Integer.class, JDBCType.INTEGER),
            Map.entry(long.class, JDBCType.BIGINT),
            Map.entry(Long.class, JDBCType.BIGINT),
            Map.entry(float.class, JDBCType.FLOAT),
            Map.entry(Float.class, JDBCType.FLOAT),
            Map.entry(double.class, JDBCType.DOUBLE),
            Map.entry(Double.class, JDBCType.DOUBLE),
            Map.entry(java.math.BigDecimal.class, JDBCType.DECIMAL),
            Map.entry(java.sql.Date.class, JDBCType.DATE),
            Map.entry(java.sql.Time.class, JDBCType.TIME),
            Map.entry(java.sql.Timestamp.class, JDBCType.TIMESTAMP),
            Map.entry(java.sql.Clob.class, JDBCType.CLOB),
            Map.entry(java.sql.Blob.class, JDBCType.BLOB),
            Map.entry(java.sql.Array.class, JDBCType.ARRAY),
            Map.entry(java.sql.Struct.class, JDBCType.STRUCT),
            Map.entry(java.net.URL.class, JDBCType.DATALINK),
            Map.entry(java.sql.SQLXML.class, JDBCType.SQLXML),
            Map.entry(java.sql.ResultSet.class, JDBCType.OTHER),
            Map.entry(Object.class, JDBCType.JAVA_OBJECT)
    );

    public SQLType getJdbcType(Field field) {
        Class<?> type = field.getType();

        if (Collection.class.isAssignableFrom(type)) {
            return JDBCType.ARRAY;
        }

        if (Map.class.isAssignableFrom(type)) {
            return JDBCType.OTHER;
        }

        return TYPE_MAP.getOrDefault(type, JDBCType.OTHER);
    }

    public String getJdbcTypeName(int jdbcType) {
        for (var f : Types.class.getFields()) {
            try {
                if (f.getType() == int.class && f.getInt(null) == jdbcType) {
                    return f.getName();
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return "UNKNOWN";
    }
}