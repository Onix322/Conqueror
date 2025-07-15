package framework.src.server.httpServer.utils.route;

/**
 * MethodRoute is a utility class that encapsulates the route of an HTTP method.
 * It allows for easy access and manipulation of the route, which is a string representing
 * the path of the HTTP method.
 */
public class MethodRoute {
    private String route;

    public MethodRoute(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "MethodRoute{" +
                "route='" + route + '\'' +
                '}';
    }
}
