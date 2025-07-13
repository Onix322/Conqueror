package src.com.server.handlers;

import src.com.server.annotations.component.Component;
import src.com.server.annotations.controller.mapping.parameters.RequestBody;
import src.com.server.exceptions.AnnotationException;
import src.com.server.httpServer.utils.request.httpRequest.HttpRequest;
import src.com.server.httpServer.utils.route.PathVariable;
import src.com.server.metadata.RouteMetaData;
import src.com.server.processors.context.ApplicationContext;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RouteHandler is responsible for handling HTTP requests and invoking the appropriate controller methods
 * based on the route metadata. It processes different HTTP methods (GET, POST, DELETE, PUT, PATCH)
 * and manages path variables and request bodies.
 */
@Component
public final class RouteHandler {

    private final ApplicationContext CONTEXT_PROCESSOR;

    private RouteHandler(ApplicationContext applicationContext) {
        this.CONTEXT_PROCESSOR = applicationContext;
    }

    /**
     * Handles the routing of HTTP requests to the appropriate controller methods based on the route metadata.
     * It determines the HTTP method and invokes the corresponding method on the controller instance.
     *
     * @param route   The metadata of the route to be handled.
     * @param request The HTTP request containing information such as path variables and request body.
     * @return The result of invoking the controller method, which can be a response object or void.
     */
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

    /**
     * Handles the GET request mapping by invoking the appropriate method on the controller instance.
     * It checks for path variables and returns the result of the method invocation.
     *
     * @param route             The metadata of the route to be handled.
     * @param instanceController The controller instance to invoke the method on.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
    private Object handleGetMapping(RouteMetaData route, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length > 0) {
            List<Object> vars = this.getPathVars(route);
            return this.returnTypeInstance(instanceController, route, vars);
        } else {
            return this.returnTypeInstance(instanceController, route);
        }
    }

    /**
     * Handles the POST request mapping by invoking the appropriate method on the controller instance.
     * It checks for the request body and returns the result of the method invocation.
     *
     * @param route             The metadata of the route to be handled.
     * @param request           The HTTP request containing the body.
     * @param instanceController The controller instance to invoke the method on.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
    private Object handlePostMapping(RouteMetaData route, HttpRequest request, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (request.getHttpRequestBody().getBody() == null) {
            throw new IllegalArgumentException("HTTP " + request.getStartLine().getMethod() + " body is empty or null: " + request.getHttpRequestBody().getBody());
        }
        List<Object> vars = List.of(request.getHttpRequestBody().getBody());
        return this.returnTypeInstance(instanceController, route, vars);
    }

    /**
     * Handles the DELETE request mapping by invoking the appropriate method on the controller instance.
     * It checks for path variables and returns the result of the method invocation.
     *
     * @param route             The metadata of the route to be handled.
     * @param instanceController The controller instance to invoke the method on.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
    private Object handleDeleteMapping(RouteMetaData route, Object instanceController) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (route.getPathVariables().length == 0) {
            throw new NoSuchElementException("Delete mapping must have at least 1 path variable!");
        }
        List<Object> vars = this.getPathVars(route);
        return this.returnTypeInstance(instanceController, route, vars);
    }

    /**
     * Handles the PUT request mapping by invoking the appropriate method on the controller instance.
     * It checks for path variables and request body, and returns the result of the method invocation.
     *
     * @param route             The metadata of the route to be handled.
     * @param request           The HTTP request containing the body.
     * @param instanceController The controller instance to invoke the method on.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
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

    /**
     * Handles the PATCH request mapping by invoking the appropriate method on the controller instance.
     * It checks for path variables and request body, and returns the result of the method invocation.
     *
     * @param route             The metadata of the route to be handled.
     * @param request           The HTTP request containing the body.
     * @param instanceController The controller instance to invoke the method on.
     * @return The result of the method invocation.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws IllegalAccessException If access to the method is denied.
     * @throws InstantiationException If an error occurs while creating an instance of a class.
     * @throws NoSuchFieldException If a field is not found in a class.
     */
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

    /**
     * Invokes the method on the controller instance with the provided path variables.
     *
     * @param instanceController The controller instance to invoke the method on.
     * @param routeMetaData      The metadata of the route containing method and controller information.
     * @param vars               The list of path variable values to be passed as arguments.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
    private Object returnTypeInstance(Object instanceController, RouteMetaData routeMetaData, List<Object> vars) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = routeMetaData.getControllerMetaData().getClassOf()
                .getDeclaredMethod(routeMetaData.getMethodMetaData().getName(), routeMetaData.getMethodMetaData().getParametersClasses());
        method.setAccessible(true);
        return method.invoke(instanceController, vars.toArray(new Object[0]));
    }

    /**
     * Invokes the method on the controller instance without any path variables.
     *
     * @param instanceController The controller instance to invoke the method on.
     * @param routeMetaData      The metadata of the route containing method and controller information.
     * @return The result of the method invocation.
     * @throws NoSuchMethodException If the method does not exist in the controller.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is denied.
     */
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

    /**
     * Repositions the parameters based on their types and the provided path values.
     * This method ensures that the parameters are arranged in the order they are defined in the method.
     *
     * @param parameters   The array of parameters defined in the method.
     * @param pathValues   The list of path values extracted from the request.
     * @return A list of arranged parameter values matching the method's parameter types.
     */
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
