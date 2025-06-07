package org.server.managers.database.databaseManager.entityData;

import java.sql.SQLType;
import java.util.Objects;

public class EntityColumn implements EntityData<EntityColumn> {
    private String columnName;
    private boolean unique;
    private boolean primaryKey;
    private boolean nullable;
    private boolean autoIncrement;
    private SQLType type;

    public EntityColumn(String columnName, boolean unique, boolean primaryKey, boolean nullable, boolean autoIncrement, SQLType type) {
        this.columnName = columnName;
        this.unique = unique;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public SQLType getType() {
        return type;
    }

    public void setType(SQLType type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public boolean equals(Object object) {

        if (object == null || getClass() != object.getClass()) return false;
        EntityColumn that = (EntityColumn) object;
        return isUnique() == that.isUnique() && isPrimaryKey() == that.isPrimaryKey() && isNullable() == that.isNullable() && isAutoIncrement() == that.isAutoIncrement() && Objects.equals(getColumnName(), that.getColumnName()) && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColumnName(), isUnique(), isPrimaryKey(), isNullable(), isAutoIncrement(), getType());
    }

    @Override
    public String toString() {
        return "EntityColumn{" +
                "columnName='" + columnName + '\'' +
                ", unique=" + unique +
                ", primaryKey=" + primaryKey +
                ", nullable=" + nullable +
                ", autoIncrement=" + autoIncrement +
                ", type=" + type +
                '}';
    }
}
