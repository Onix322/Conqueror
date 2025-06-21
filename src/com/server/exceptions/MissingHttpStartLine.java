package src.com.server.exceptions;

public class MissingHttpStartLine extends RuntimeException {
    public MissingHttpStartLine(String message) {
        super(message);
    }
}
