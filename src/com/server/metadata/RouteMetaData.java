package src.com.server.metadata;

import src.com.server.httpServer.utils.route.PathVariable;

import java.util.Arrays;

/**
 * RouteMetaData is a class that encapsulates metadata about a specific route in the server.
 * It includes information about the controller, method, and any path variables associated with the route.
 * This class implements the MetaData interface to provide a structured way to access route metadata.
 */
public class RouteMetaData implements MetaData<RouteMetaData>{

    private ControllerMetaData controllerMetaData;
    private MethodMetaData methodMetaData;
    private PathVariable[] pathVariables;
    private final String FULL_PATH;

    public RouteMetaData(ControllerMetaData controllerMetaData, MethodMetaData methodMetaData, PathVariable[] pathVariables) {
        this.controllerMetaData = controllerMetaData;
        this.methodMetaData = methodMetaData;
        this.pathVariables = pathVariables;
        this.FULL_PATH = controllerMetaData.getPath().getRoute()
                + methodMetaData.getPath().getRoute();
    }

    public ControllerMetaData getControllerMetaData() {
        return controllerMetaData;
    }

    public void setControllerMetaData(ControllerMetaData controllerMetaData) {
        this.controllerMetaData = controllerMetaData;
    }

    public MethodMetaData getMethodMetaData() {
        return methodMetaData;
    }

    public void setMethodMetaData(MethodMetaData methodMetaData) {
        this.methodMetaData = methodMetaData;
    }

    public PathVariable[] getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(PathVariable[] pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getFullPath() {
        return FULL_PATH;
    }

    @Override
    public RouteMetaData getMetaData() {
        return this;
    }

    @Override
    public String toString() {
        return "RouteMetaData{" +
                "controllerMetaData=" + controllerMetaData +
                ", methodMetaData=" + methodMetaData +
                ", pathVariables=" + Arrays.toString(pathVariables) +
                ", FULL_PATH='" + FULL_PATH + '\'' +
                '}';
    }
}
