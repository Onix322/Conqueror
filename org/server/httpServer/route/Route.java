package org.server.httpServer.route;

import java.util.Arrays;

public class Route {
    private ControllerRoute controllerRoute;
    private MappingMethodRoute mappedMethodRoute;
    private PathVariable[] pathVariables;

    private Route(RouteBuilder routeBuilder) {
        this.controllerRoute = routeBuilder.getControllerRoute();
        this.mappedMethodRoute = routeBuilder.getMappedMethodRoute();
        this.pathVariables = routeBuilder.pathVariables;
    }

    public ControllerRoute getControllerRoute() {
        return controllerRoute;
    }

    public void setControllerRoute(ControllerRoute controllerRoute) {
        this.controllerRoute = controllerRoute;
    }

    public MappingMethodRoute getMappedMethodRoute() {
        return mappedMethodRoute;
    }

    public void setMappedMethodRoute(MappingMethodRoute mappedMethodRoute) {
        this.mappedMethodRoute = mappedMethodRoute;
    }

    public PathVariable[] getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(PathVariable[] pathVariables) {
        this.pathVariables = pathVariables;
    }

    public static RouteBuilder builder(){
        return new RouteBuilder();
    }

    public static class RouteBuilder{
        private ControllerRoute controllerRoute = null;
        private MappingMethodRoute mappedMethodRoute = null;
        private PathVariable[] pathVariables = new PathVariable[0];

        private RouteBuilder(){}

        public RouteBuilder setControllerRoute(ControllerRoute controllerRoute) {
            this.controllerRoute = controllerRoute;
            return this;
        }

        public RouteBuilder setPathVariables(PathVariable[] pathVariables) {
            this.pathVariables = pathVariables;
            return this;
        }

        public RouteBuilder setMappedMethodRoute(MappingMethodRoute mappedMethodRoute) {
            this.mappedMethodRoute = mappedMethodRoute;
            return this;
        }

        public ControllerRoute getControllerRoute() {
            return controllerRoute;
        }

        public MappingMethodRoute getMappedMethodRoute() {
            return mappedMethodRoute;
        }

        public PathVariable[] getPathVariables() {
            return pathVariables;
        }

        public Route build(){
            return new Route(this);
        }
    }

    @Override
    public String toString() {
        return "Route{" +
                "controllerRoute=" + controllerRoute +
                ", mappedMethodRoute=" + mappedMethodRoute +
                ", pathVariables=" + Arrays.toString(pathVariables) +
                '}';
    }
}
