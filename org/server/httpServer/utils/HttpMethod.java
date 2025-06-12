package org.server.httpServer.utils;

public enum HttpMethod {
    GET(false),
    POST(true),
    PUT(true),
    PATCH(true),
    DELETE(false),
    HEAD(false),
    OPTIONS(true);

    private final boolean REQUIRES_BODY;

    HttpMethod(boolean requiresBody) {
        this.REQUIRES_BODY = requiresBody;
    }

    public boolean hasBody() {
        return REQUIRES_BODY;
    }
}
