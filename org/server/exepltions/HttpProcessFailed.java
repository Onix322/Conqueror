package org.server.exepltions;

public class HttpProcessFailed extends RuntimeException {
    public HttpProcessFailed(String message) {
        super(message);
    }
}
