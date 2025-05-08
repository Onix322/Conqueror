package org.utils.exepltions;

public class NoSuchJsonPropertyError extends RuntimeException {
    public NoSuchJsonPropertyError(String message) {
        super(message);
    }
}
