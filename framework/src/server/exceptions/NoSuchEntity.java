package framework.src.server.exceptions;

public class NoSuchEntity extends RuntimeException {
    public NoSuchEntity(String message) {
        super(message);
    }
}
