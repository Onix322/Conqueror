package src.com.config.mysql.utils.entityData;

public interface EntityData<T extends EntityData<T>> {

    boolean equals(Object object);

    int hashCode();

    String toString();

}
