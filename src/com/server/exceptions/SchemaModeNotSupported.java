package src.com.server.exceptions;

public class SchemaModeNotSupported extends RuntimeException {
    public SchemaModeNotSupported(String message) {
        super(message);
    }
}
