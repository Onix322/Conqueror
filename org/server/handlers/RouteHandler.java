package org.server.handlers;

import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.route.PathVariable;
import org.server.metadata.MethodMetaData;
import org.server.metadata.RouteMetaData;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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

    public Object handleRoute(RouteMetaData route, HttpRequest request) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (route.getMethodMetaData().getHttpMethod()){
            case GET ->{
                return this.handleGetMapping(route);
            }
            case POST -> {
                return this.handlePostMapping(route, request);
            }
            default -> throw new NoSuchMethodException(route.getMethodMetaData().getHttpMethod() + " has not been implemented yet.");
        }
    }

    private Object handleGetMapping(RouteMetaData route) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(route.getPathVariables().length > 0){
            List<Object> vars = Arrays.stream(route.getPathVariables())
                    .map(PathVariable::value)
                    .toList();

            return route.getControllerMetaData().getClassOf()
                    .getMethod(route.getMethodMetaData().getName(), route.getMethodMetaData().getParameters())
                    .invoke(route.getControllerMetaData().getClassOf(), vars.toArray(new Object[0]));
        } else {
            return route.getControllerMetaData().getClassOf()
                    .getMethod(route.getMethodMetaData().getName())
                    .invoke(route.getControllerMetaData().getClassOf());
        }
    }

    private Object handlePostMapping(RouteMetaData route, HttpRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(request.getHttpRequestBody().getBody() == null){
            throw new IllegalArgumentException("HTTP POST body is empty or null: " + request.getHttpRequestBody().getBody());
        }
        return route.getControllerMetaData().getClassOf()
                .getMethod(route.getMethodMetaData().getName(), route.getMethodMetaData().getParameters())
                .invoke(route.getControllerMetaData().getClassOf(), request.getHttpRequestBody().getBody());
    }
}
