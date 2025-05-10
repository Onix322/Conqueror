package org.server.exepltions;

public class MissingHttpStartLine extends RuntimeException {
    public MissingHttpStartLine(String message) {
        super(message);
    }
}
