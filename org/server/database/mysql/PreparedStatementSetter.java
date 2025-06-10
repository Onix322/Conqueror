package org.server.database.mysql;

import org.server.processors.context.annotations.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class PreparedStatementSetter {
    private PreparedStatementSetter() {
    }

    public PreparedStatement autoSetter(PreparedStatement ps, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            int index = i + 1;
            Object v = values[i];

            switch (v) {
                case null -> ps.setObject(index, null);
                case String s -> ps.setString(index, s);
                case Integer integer -> ps.setInt(index, integer);
                case Long l -> ps.setLong(index, l);
                case Short aShort -> ps.setShort(index, aShort);
                case Byte b -> ps.setByte(index, b);
                case Boolean b -> ps.setBoolean(index, b);
                case Float aFloat -> ps.setFloat(index, aFloat);
                case Double aDouble -> ps.setDouble(index, aDouble);
                case BigDecimal bigDecimal -> ps.setBigDecimal(index, bigDecimal);
                case Date date -> ps.setDate(index, date);
                case Time time -> ps.setTime(index, time);
                case Timestamp timestamp -> ps.setTimestamp(index, timestamp);
                case java.util.Date date -> ps.setTimestamp(index, new Timestamp(date.getTime()));
                case LocalDate localDate -> ps.setDate(index, Date.valueOf(localDate));
                case LocalTime localTime -> ps.setTime(index, Time.valueOf(localTime));
                case LocalDateTime localDateTime -> ps.setTimestamp(index, Timestamp.valueOf(localDateTime));
                case Instant instant -> ps.setTimestamp(index, Timestamp.from(instant));
                case byte[] bytes -> ps.setBytes(index, bytes);
                case Blob blob -> ps.setBlob(index, blob);
                case Clob clob -> ps.setClob(index, clob);
                case Array array -> ps.setArray(index, array);
                case UUID uuid -> ps.setObject(index, v, Types.OTHER);
                case Enum anEnum -> ps.setString(index, anEnum.name());
                default -> ps.setObject(index, v); // fallback
            }
        }

        return ps;
    }
}
