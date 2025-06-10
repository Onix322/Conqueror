package org.server.database.mysql.entityData;

public interface EntityData<T extends EntityData<T>> {

    boolean equals(Object object);

    int hashCode();

    String toString();

}
