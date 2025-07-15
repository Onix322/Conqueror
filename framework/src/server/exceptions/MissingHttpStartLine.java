package framework.src.server.exceptions;

public class MissingHttpStartLine extends RuntimeException {
    public MissingHttpStartLine(String message) {
        super(message);
    }
}
