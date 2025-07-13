package src.com.server.httpServer.utils.route;

/**
 * ControllerRoute is a utility class that encapsulates the route of a controller.
 * It provides methods to get and set the route, and overrides the toString method
 * for easy representation of the route.
 */
public class ControllerRoute {
    private String route;

    public ControllerRoute(String route) {
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
        return "ControllerRoute{" +
                "route='" + route + '\'' +
                '}';
    }
}
