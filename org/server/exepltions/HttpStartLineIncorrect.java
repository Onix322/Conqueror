package org.server.exepltions;

public class HttpStartLineIncorrect extends RuntimeException {
    public HttpStartLineIncorrect() {
        super("Http Start Line is incorrect written, and could not be processed properly!");
    }
}
