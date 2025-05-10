package org.server.httpServer.route;

public class Route {
    private String controllerRoute;
    private String mappedMethodRoute;

    public Route(String controllerRoute, String mappedMethodRoute) {
        this.controllerRoute = controllerRoute;
        this.mappedMethodRoute = mappedMethodRoute;
    }

    public String getControllerRoute() {
        return controllerRoute;
    }

    public void setControllerRoute(String controllerRoute) {
        this.controllerRoute = controllerRoute;
    }

    public String getMappedMethodRoute() {
        return mappedMethodRoute;
    }

    public void setMappedMethodRoute(String mappedMethodRoute) {
        this.mappedMethodRoute = mappedMethodRoute;
    }

    @Override
    public String toString() {
        return "Route{" +
                "controllerRoute='" + controllerRoute + '\'' +
                ", mappedMethodRoute='" + mappedMethodRoute + '\'' +
                '}';
    }
}
