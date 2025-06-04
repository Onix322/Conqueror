package org.server.managers.databaseManager;

import java.sql.SQLType;
import java.util.Objects;

public class EntityColumn implements EntityData<EntityColumn> {
    private String columnName;
    private boolean unique;
    private boolean primaryKey;
    private boolean nullable;
    private SQLType type;

    public EntityColumn(String columnName, boolean unique, boolean primaryKey, boolean nullable, SQLType type) {
        this.columnName = columnName;
        this.unique = unique;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public SQLType getType() {
        return type;
    }

    public void setType(SQLType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        EntityColumn that = (EntityColumn) object;
        return isUnique() == that.isUnique() && isPrimaryKey() == that.isPrimaryKey() && isNullable() == that.isNullable() && getType() == that.getType() && Objects.equals(getColumnName(), that.getColumnName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColumnName(), isUnique(), isPrimaryKey(), isNullable(), getType());
    }

    @Override
    public String toString() {
        return "EntityColumn{" +
                "columnName='" + columnName + '\'' +
                ", unique=" + unique +
                ", primaryKey=" + primaryKey +
                ", nullable=" + nullable +
                ", type=" + type +
                '}';
    }

}
