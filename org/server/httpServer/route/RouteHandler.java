package org.server.httpServer.route;

import org.server.httpServer.request.httpRequest.HttpRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RouteHandler {
    private RouteHandler() {
    }

    private static class Init {
        private static RouteHandler INSTANCE = null;
    }

    public synchronized static void init() {
        if (RouteHandler.Init.INSTANCE == null) {
            RouteHandler.Init.INSTANCE = new RouteHandler();
        }
    }

    public static RouteHandler getInstance() {
        if (RouteHandler.Init.INSTANCE == null) {
            throw new IllegalStateException("RouteHandler not initialized! Use RouteHandler.init()");
        }
        return RouteHandler.Init.INSTANCE;
    }

    public Route createRoute(HttpRequest request) {
        String path = request.getStartLine().getPath().getRawPath();
        List<String> fragments = Arrays.stream(path.split("/"))
                .filter(fragment -> !fragment.isEmpty())
                .map(fragment -> "/" + fragment)
                .toList();

        String controllerRoute = fragments.getFirst();
        String mappedMethodRoute = String.join("/", fragments.subList(1, fragments.size()));

        if (mappedMethodRoute.isEmpty()) {
            mappedMethodRoute = "/" + request.getStartLine()
                    .getMethod()
                    .name()
                    .toLowerCase(Locale.ROOT);
        }
        return new Route(controllerRoute, mappedMethodRoute);
    }
}
