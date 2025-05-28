package org.server.managers.databaseManager;

public interface EntityData<T extends EntityData<T>> {

    boolean equals(Object object);

    int hashCode();

    String toString();

}
