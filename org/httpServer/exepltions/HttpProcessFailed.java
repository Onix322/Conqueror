package org.httpServer.exepltions;

public class HttpProcessFailed extends RuntimeException {
    public HttpProcessFailed(String message) {
        super(message);
    }
}
