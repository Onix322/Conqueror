package src.com.server.httpServer.utils.response;

public enum HttpStatus {
    // SUCCESS (2xx)
    OK(200, "OK"),
    CREATED(201, "Resource created successfully"),
    ACCEPTED(202, "Request accepted for processing"),
    NO_CONTENT(204, "No content"),

    // REDIRECT (3xx)
    MOVED_PERMANENTLY(301, "Resource moved permanently"),
    FOUND(302, "Resource found (temporary redirect)"),
    NOT_MODIFIED(304, "Resource not modified"),

    // CLIENT ERROR (4xx)
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Resource permanently gone"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable entity"),
    TOO_MANY_REQUESTS(429, "Too many requests"),

    // SERVER ERROR (5xx)
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SERVER_NOT_CONNECTED(500, "Connection shutdown"),
    NOT_IMPLEMENTED(501, "Not implemented"),
    BAD_GATEWAY(502, "Bad gateway"),
    SERVICE_UNAVAILABLE(503, "Service unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway timeout");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private String stackTrace;

    public String getStackTrace() {
        return stackTrace;
    }

    public HttpStatus withStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        this.stackTrace = sb.toString();
        return this;
    }

    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    public boolean isRedirect() {
        return code >= 300 && code < 400;
    }

    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    public boolean isServerError() {
        return code >= 500;
    }

    @Override
    public String toString() {
        return "ServerResponseCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                (stackTrace.isEmpty() ? "" : ", stackTrace='" + stackTrace + '\'') +
                '}';
    }
}
