package src.com.server.exceptions;

public class NoSuchJsonPropertyError extends RuntimeException {
    public NoSuchJsonPropertyError(String message) {
        super(message);
    }
}
