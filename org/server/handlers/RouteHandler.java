package org.server.handlers;

import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.route.PathVariable;
import org.server.metadata.RouteMetaData;
import org.server.processors.components.annotations.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Component
public final class RouteHandler {

    private RouteHandler() {
    }

    public Object handleRoute(RouteMetaData route, HttpRequest request) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        switch (route.getMethodMetaData().getHttpMethod()) {
            case GET -> {
                return this.handleGetMapping(route);
            }
            case POST -> {
                return this.handlePostMapping(route, request);
            }
            default ->
                    throw new NoSuchMethodException(route.getMethodMetaData().getHttpMethod() + " has not been implemented yet.");
        }
    }

    private Object handleGetMapping(RouteMetaData route) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length > 0) {
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
        if (request.getHttpRequestBody().getBody() == null) {
            throw new IllegalArgumentException("HTTP POST body is empty or null: " + request.getHttpRequestBody().getBody());
        }
        return route.getControllerMetaData().getClassOf()
                .getMethod(route.getMethodMetaData().getName(), route.getMethodMetaData().getParameters())
                .invoke(route.getControllerMetaData().getClassOf(), request.getHttpRequestBody().getBody());
    }
}
