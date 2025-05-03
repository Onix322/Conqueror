package org.exepltions;

public class NoSuchJsonPropertyError extends RuntimeException {
    public NoSuchJsonPropertyError(String message) {
        super(message);
    }
}
