package src.com.server.httpServer.utils.route;

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
