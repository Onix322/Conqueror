package org.server.handlers;

import org.server.annotations.component.Component;
import org.server.annotations.controller.mapping.parameters.RequestBody;
import org.server.exceptions.AnnotationException;
import org.server.httpServer.utils.request.httpRequest.HttpRequest;
import org.server.httpServer.utils.route.PathVariable;
import org.server.metadata.RouteMetaData;
import org.server.processors.context.ApplicationContext;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class RouteHandler {

    private final ApplicationContext CONTEXT_PROCESSOR;

    private RouteHandler(ApplicationContext applicationContext) {
        this.CONTEXT_PROCESSOR = applicationContext;
    }

    public Object handleRoute(RouteMetaData route, HttpRequest request) {
        Object instanceController = this.CONTEXT_PROCESSOR.requestInstance(route.getControllerMetaData().getClassOf());

        try{
            return switch (route.getMethodMetaData().getHttpMethod()) {
                case GET -> this.handleGetMapping(route, instanceController);
                case POST -> this.handlePostMapping(route, request, instanceController);
                case DELETE -> this.handleDeleteMapping(route, instanceController);
                case PUT -> this.handlePutMapping(route, request, instanceController);
                case PATCH -> this.handlePatchMapping(route, request, instanceController);
                default -> throw new NoSuchMethodException(route.getMethodMetaData().getHttpMethod() + " has not been implemented yet.");
            };
        } catch (Exception e){
            throw new RuntimeException(e.getCause());
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

    private Object handlePutMapping(RouteMetaData route, HttpRequest request, Object instanceController)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        List<Object> args = new ArrayList<>();

        if (route.getPathVariables().length > 0) {
            args.addAll(getPathVars(route));
        }

        boolean expectsRequestBody = Arrays.stream(route.getMethodMetaData().getParameters())
                .anyMatch(p -> p.isAnnotationPresent(RequestBody.class));

        if (expectsRequestBody) {
            Object body = request.getHttpRequestBody().getBody();
            if (body == null) {
                throw new IllegalArgumentException("HTTP " + request.getStartLine().getMethod() +
                        " body is empty or null: ");
            }

            args.add(body);
        }

        List<Object> finalArgs = args.isEmpty()
                ? List.of(new Object[0])
                : repositionParameters(route.getMethodMetaData().getParameters(), args);


        return returnTypeInstance(instanceController, route, finalArgs);
    }

    private Object handlePatchMapping(RouteMetaData route, HttpRequest request, Object instanceController)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        Parameter bodyParameter = Arrays.stream(route.getMethodMetaData().getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationException("@RequestBody parameter not found!"));

        Object requestBody = request.getHttpRequestBody().getBody();

        if (requestBody == null) {
            throw new IllegalArgumentException("PATCH body is null!");
        }

        Object targetInstance = bodyParameter.getType().getConstructor().newInstance();

        Class<?> requestBodyClass = requestBody.getClass();
        Class<?> targetClass = targetInstance.getClass();

        for (Field sourceField : requestBodyClass.getDeclaredFields()) {
            sourceField.setAccessible(true);
            Field targetField = targetClass.getDeclaredField(sourceField.getName());
            targetField.setAccessible(true);
            Object value = sourceField.get(requestBody);
            if (value != null) {
                targetField.set(targetInstance, value);
            }
        }

        List<Object> args = new ArrayList<>();
        if (route.getPathVariables().length > 0) {
            args.addAll(getPathVars(route));
        }
        args.add(targetInstance);

        List<Object> finalArgs = this.repositionParameters(route.getMethodMetaData().getParameters(), args);

        return returnTypeInstance(instanceController, route, finalArgs);
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
