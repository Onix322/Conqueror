package org.server.httpServer.route;

public class MappingMethodRoute {
    private String route;

    public MappingMethodRoute(String route) {
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
        return "MappingMethodRoute{" +
                "route='" + route + '\'' +
                '}';
    }
}
