package org.httpServer.exepltions;

public class NoCompatibleHttpVersionFound extends RuntimeException {
    public NoCompatibleHttpVersionFound() {
        super("No Compatible HttpVersion has been found in class HttpVersion");
    }
}
