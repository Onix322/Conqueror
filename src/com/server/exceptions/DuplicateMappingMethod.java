package src.com.server.exceptions;

public class DuplicateMappingMethod extends RuntimeException {
    public DuplicateMappingMethod(String message) {
        super(message);
    }
}
