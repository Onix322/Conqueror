package org.server.handlers;

import org.server.annotations.component.Component;
import org.server.annotations.controller.mapping.parameters.RequestBody;
import org.server.httpServer.utils.request.httpRequest.HttpRequest;
import org.server.httpServer.utils.route.PathVariable;
import org.server.metadata.RouteMetaData;
import org.server.processors.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class RouteHandler {

    private final ApplicationContext CONTEXT_PROCESSOR;

    private RouteHandler(ApplicationContext applicationContext) {
        this.CONTEXT_PROCESSOR = applicationContext;
    }

    public Object handleRoute(RouteMetaData route, HttpRequest request) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object instanceController = this.CONTEXT_PROCESSOR.requestInstance(route.getControllerMetaData().getClassOf());
        switch (route.getMethodMetaData().getHttpMethod()) {
            case GET -> {
                return this.handleGetMapping(route, instanceController);
            }
            case POST -> {
                return this.handlePostMapping(route, request, instanceController);
            }
            case DELETE -> {
                return this.handleDeleteMapping(route, instanceController);
            }
            case PUT -> {
                return this.handlePutMapping(route, request, instanceController);
            }
            case PATCH -> {
                throw new NoSuchMethodException("MAPPING METHOD COMING SOON...");
            }
            default ->
                    throw new NoSuchMethodException(route.getMethodMetaData().getHttpMethod() + " has not been implemented yet.");
        }
    }

    private Object handleGetMapping(RouteMetaData route, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length > 0) {
            List<Object> vars = this.getPathVars(route);
            return this.returnTypeInstance(instanceController, route, vars);
        } else {
            return this.returnTypeInstance(instanceController, route);
        }
    }

    private Object handlePostMapping(RouteMetaData route, HttpRequest request, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (request.getHttpRequestBody().getBody() == null) {
            throw new IllegalArgumentException("HTTP " + request.getStartLine().getMethod() + " body is empty or null: " + request.getHttpRequestBody().getBody());
        }
        List<Object> vars = List.of(request.getHttpRequestBody().getBody());
        return this.returnTypeInstance(instanceController, route, vars);
    }

    private Object handleDeleteMapping(RouteMetaData route, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length == 0) {
            throw new NoSuchElementException("Delete mapping must have at least 1 path variable!");
        }
        List<Object> vars = this.getPathVars(route);
        return this.returnTypeInstance(instanceController, route, vars);
    }

    private Object handlePutMapping(RouteMetaData route, HttpRequest request, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if(Arrays.stream(route.getMethodMetaData().getParameters()).anyMatch(p -> p.isAnnotationPresent(RequestBody.class))){
            if (request.getHttpRequestBody().getBody() == null) {
                throw new IllegalArgumentException("HTTP " + request.getStartLine().getMethod() + " body is empty or null: " + request.getHttpRequestBody().getBody());
            }
            List<Object> pathVars = this.getPathVars(route);
            pathVars.add(request.getHttpRequestBody().getBody());
            List<Object> repositionedVals = this.repositionParameters(route.getMethodMetaData().getParameters(), pathVars);
            return this.returnTypeInstance(instanceController, route, repositionedVals);
        }

        if (route.getPathVariables().length > 0) {
            List<Object> vars = this.getPathVars(route);
            return this.returnTypeInstance(instanceController, route, vars);
        } else {
            return this.returnTypeInstance(instanceController, route);
        }
    }

    // WITH path variables
    private Object returnTypeInstance(Object instanceController, RouteMetaData routeMetaData, List<Object> vars) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = routeMetaData.getControllerMetaData().getClassOf()
                .getDeclaredMethod(routeMetaData.getMethodMetaData().getName(), routeMetaData.getMethodMetaData().getParametersClasses());
        method.setAccessible(true);
        return method.invoke(instanceController, vars.toArray(new Object[0]));
    }

    // WITHOUT path variables
    private Object returnTypeInstance(Object instanceController, RouteMetaData routeMetaData) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = routeMetaData.getControllerMetaData().getClassOf()
                .getDeclaredMethod(routeMetaData.getMethodMetaData().getName());
        method.setAccessible(true);
        return method.invoke(instanceController);
    }

    private List<Object> getPathVars(RouteMetaData routeMetaData) {
        return Arrays.stream(routeMetaData.getPathVariables())
                .map(PathVariable::value)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Object> repositionParameters(Parameter[] parameters, List<Object> pathValues){
        List<Object> arrangedValues = new LinkedList<>();
        for (Parameter p : parameters){
            for (Object pv : pathValues){
                if(p.getType().equals(pv.getClass())){
                    arrangedValues.add(pv);
                }
            }
        }
        return arrangedValues;
    }
}
