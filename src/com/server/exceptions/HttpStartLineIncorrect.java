package src.com.server.exceptions;

public class HttpStartLineIncorrect extends RuntimeException {
    public HttpStartLineIncorrect() {
        super("Http Start Line is incorrect written, and could not be processed properly!");
    }
    public HttpStartLineIncorrect(String message) {
        super(message);
    }
}
