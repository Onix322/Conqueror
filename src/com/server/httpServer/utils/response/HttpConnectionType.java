package src.com.server.httpServer.utils.response;

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
