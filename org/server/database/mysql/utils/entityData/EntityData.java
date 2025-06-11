package org.server.database.mysql.utils.entityData;

public interface EntityData<T extends EntityData<T>> {

    boolean equals(Object object);

    int hashCode();

    String toString();

}
