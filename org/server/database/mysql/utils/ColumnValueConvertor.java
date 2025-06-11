package org.server.database.mysql.utils;

import org.server.annotations.component.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

@Component
public class ColumnValueConvertor {
    private ColumnValueConvertor(){}

    public Object autoConvertor(ResultSet rs, int indexColumn) {
        try {
            Object value = rs.getObject(indexColumn);
            if (value == null) return null;

            if (value instanceof String) return rs.getString(indexColumn);
            if (value instanceof Integer) return rs.getInt(indexColumn);
            if (value instanceof Long) return rs.getLong(indexColumn);
            if (value instanceof Short) return rs.getShort(indexColumn);
            if (value instanceof Byte) return rs.getByte(indexColumn);
            if (value instanceof Boolean) return rs.getBoolean(indexColumn);
            if (value instanceof Float) return rs.getFloat(indexColumn);
            if (value instanceof Double) return rs.getDouble(indexColumn);
            if (value instanceof BigDecimal) return rs.getBigDecimal(indexColumn);

            if (value instanceof java.sql.Date) return rs.getDate(indexColumn);
            if (value instanceof java.sql.Time) return rs.getTime(indexColumn);
            if (value instanceof java.sql.Timestamp) return rs.getTimestamp(indexColumn);

            if (value instanceof byte[]) return rs.getBytes(indexColumn);
            if (value instanceof Blob) return rs.getBlob(indexColumn);
            if (value instanceof Clob) return rs.getClob(indexColumn);
            if (value instanceof Array) return rs.getArray(indexColumn);

            // LocalDateTime fallback
            if (value instanceof Timestamp ts) {
                return ts.toLocalDateTime();
            }

            if (value instanceof UUID || value.toString().matches("^[a-f0-9\\-]{36}$")) {
                return UUID.fromString(value.toString());
            }

            return value; // fallback
        } catch (SQLException e) {
            throw new RuntimeException("Error converting column at index " + indexColumn, e);
        }
    }
}
