package framework.src.server.exceptions;

public class JsonPropertyFormatError extends RuntimeException {
    public JsonPropertyFormatError(String message) {
        super(message);
    }
}
