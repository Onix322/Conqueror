package org.server.managers.database.databaseManager.entityData;

public interface EntityData<T extends EntityData<T>> {

    boolean equals(Object object);

    int hashCode();

    String toString();

}
