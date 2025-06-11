package org.server.handlers;

import org.server.httpServer.utils.request.httpRequest.HttpRequest;
import org.server.httpServer.utils.route.PathVariable;
import org.server.metadata.RouteMetaData;
import org.server.processors.context.ApplicationContext;
import org.server.annotations.component.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Component
public final class RouteHandler {

    private final ApplicationContext CONTEXT_PROCESSOR;

    private RouteHandler(ApplicationContext applicationContext) {
        this.CONTEXT_PROCESSOR = applicationContext;
    }

    public Object handleRoute(RouteMetaData route, HttpRequest request) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object instance = this.CONTEXT_PROCESSOR.requestInstance(route.getControllerMetaData().getClassOf());
        switch (route.getMethodMetaData().getHttpMethod()) {
            case GET -> {
                return this.handleGetMapping(route, instance);
            }
            case POST -> {
                return this.handlePostMapping(route, request, instance);
            }
            default ->
                    throw new NoSuchMethodException(route.getMethodMetaData().getHttpMethod() + " has not been implemented yet.");
        }
    }

    private Object handleGetMapping(RouteMetaData route, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length > 0) {
            List<Object> vars = Arrays.stream(route.getPathVariables())
                    .map(PathVariable::value)
                    .toList();

            return route.getControllerMetaData().getClassOf()
                    .getDeclaredMethod(route.getMethodMetaData().getName(), route.getMethodMetaData().getParameters())
                    .invoke(instance, vars.toArray(new Object[0]));
        } else {
            return route.getControllerMetaData().getClassOf()
                    .getDeclaredMethod(route.getMethodMetaData().getName())
                    .invoke(instance);
        }
    }

    private Object handlePostMapping(RouteMetaData route, HttpRequest request, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (request.getHttpRequestBody().getBody() == null) {
            throw new IllegalArgumentException("HTTP POST body is empty or null: " + request.getHttpRequestBody().getBody());
        }
        return route.getControllerMetaData().getClassOf()
                .getDeclaredMethod(route.getMethodMetaData().getName(), route.getMethodMetaData().getParameters())
                .invoke(instance, request.getHttpRequestBody().getBody());
    }
}
