package org.server.exepltions;

public class NoEmptyConstructorFound extends RuntimeException {
    public NoEmptyConstructorFound() {
        super("Create an empty constructor for the entity!");
    }
}
