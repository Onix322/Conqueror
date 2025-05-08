package org.utils.jsonService.json.types;

public interface JsonIterator<T> {
    T[] get();
    void set(T[] array);
}
