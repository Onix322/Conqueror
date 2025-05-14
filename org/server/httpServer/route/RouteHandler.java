package org.server.httpServer.route;

import org.server.exepltions.HttpStartLineIncorrect;
import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.metadata.RouteMetaData;

public class RouteHandler {

    private RouteHandler() {
    }

    private static class Init {
        private static RouteHandler INSTANCE = null;
    }

    public synchronized static void init() {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new RouteHandler();
        }
    }

    public static RouteHandler getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("RouteHandler not initialized! Use RouteHandler.init()");
        }
        return Init.INSTANCE;
    }

    public RouteMetaData handleRouting(HttpRequest request) {
        return switch (request.getStartLine().getMethod()) {
            case GET, PUT, HEAD, POST, PATCH, DELETE, OPTIONS -> null;
            default -> throw new HttpStartLineIncorrect();
        };
    }
}
