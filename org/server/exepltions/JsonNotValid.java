package org.server.exepltions;

public class JsonNotValid extends RuntimeException {
    public JsonNotValid(String message) {
        super(message);
    }
}