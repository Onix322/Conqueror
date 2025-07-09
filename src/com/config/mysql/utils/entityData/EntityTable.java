package src.com.config.mysql.utils.entityData;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EntityTable implements EntityData<EntityTable>{
    private String name;
    private List<EntityColumn> columns;

    private EntityTable(EntityTableBuilder entityTableBuilder) {
        this.name = entityTableBuilder.getName();
        this.columns = entityTableBuilder.getColumns();
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

    public static EntityTableBuilder builder(){
        return new EntityTableBuilder();
    }

    public static class EntityTableBuilder{
        private String name = "UNKNOWN";
        private List<EntityColumn> columns = new LinkedList<>();

        private EntityTableBuilder(){};

        public EntityTableBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public EntityTableBuilder setColumns(List<EntityColumn> columns) {
            this.columns = columns;
            return this;
        }

        public String getName() {
            return name;
        }

        public List<EntityColumn> getColumns() {
            return columns;
        }

        public EntityTable build(){
            return new EntityTable(this);
        }

        @Override
        public String toString() {
            return "EntityTableBuilder{" +
                    "name='" + name + '\'' +
                    ", columns=" + columns +
                    '}';
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            EntityTableBuilder that = (EntityTableBuilder) object;
            return Objects.equals(getName(), that.getName()) && Objects.equals(getColumns(), that.getColumns());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getColumns());
        }
    }
}
