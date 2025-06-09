package org.server.exceptions;

public class JsonNotValid extends RuntimeException {
    public JsonNotValid(String message) {
        super(message);
    }
}