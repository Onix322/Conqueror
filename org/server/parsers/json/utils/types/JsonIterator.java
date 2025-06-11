package org.server.parsers.json.utils.types;

public interface JsonIterator<T> {
    T[] get();
    void set(T[] array);
}
