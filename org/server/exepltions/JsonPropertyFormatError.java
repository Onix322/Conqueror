package org.server.exepltions;

public class JsonPropertyFormatError extends RuntimeException {
    public JsonPropertyFormatError(String message) {
        super(message);
    }
}
