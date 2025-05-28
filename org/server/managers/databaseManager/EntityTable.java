package org.server.managers.databaseManager;

import java.util.List;
import java.util.Objects;

public class EntityTable implements EntityData<EntityTable>{
    private String name;
    private List<EntityColumn> columns;

    public EntityTable(String name, List<EntityColumn> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EntityColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        EntityTable that = (EntityTable) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getColumns(), that.getColumns());
    }

    @Override
    public String toString() {
        return "EntityTable{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getColumns());
    }
}
