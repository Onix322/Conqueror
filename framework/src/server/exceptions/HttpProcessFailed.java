package framework.src.server.exceptions;

public class HttpProcessFailed extends RuntimeException {
    public HttpProcessFailed(String message) {
        super(message);
    }
}
