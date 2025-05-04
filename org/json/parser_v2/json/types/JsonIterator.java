package org.json.parser_v2.json.types;

public interface JsonIterator<T> {
    T[] get();
    void set(T[] array);
}
