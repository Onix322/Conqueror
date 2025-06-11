package org.server.httpServer.utils.route;

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
