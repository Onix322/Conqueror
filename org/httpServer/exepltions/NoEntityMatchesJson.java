package org.httpServer.exepltions;

public class NoEntityMatchesJson extends RuntimeException {
    public NoEntityMatchesJson() {
        super("No entity with this fields offered by current request json properties");
    }
}
