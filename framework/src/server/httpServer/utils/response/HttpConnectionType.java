package framework.src.server.httpServer.utils.response;

/**
 * Enum representing the connection types for HTTP responses.
 * This enum defines two types of connections: CLOSED and KEEP_ALIVE.
 * Each type is associated with a string representation used in HTTP headers.
 */
public enum HttpConnectionType {
    CLOSED("close"),
    KEEP_ALIVE("keep-alive");

    private final String type;

    HttpConnectionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
