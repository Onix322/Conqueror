package framework.src.server.processors.route;

import framework.src.server.managers.controllerManager.ControllerManager;
import framework.src.server.httpServer.utils.request.httpRequest.HttpRequest;
import framework.src.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;
import framework.src.server.httpServer.utils.route.ControllerRoute;
import framework.src.server.httpServer.utils.route.PathVariable;
import framework.src.server.metadata.ControllerMetaData;
import framework.src.server.metadata.MethodMetaData;
import framework.src.server.metadata.RouteMetaData;
import framework.src.server.parsers.primitiveParser.PrimitiveParser;
import framework.src.server.annotations.component.Component;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class RouteProcessor {

    private final ControllerManager CONTROLLER_MANAGER;
    private final PrimitiveParser PRIMITIVE_PARSER;

    private RouteProcessor(ControllerManager controllerManager, PrimitiveParser primitiveParser) {
        this.CONTROLLER_MANAGER = controllerManager;
        this.PRIMITIVE_PARSER = primitiveParser;
    }

    /**
     * Processes the given HttpRequest to extract route metadata.
     *
     * @param request the HttpRequest to process
     * @return RouteMetaData containing controller and method metadata, along with path variables if any
     */
    public RouteMetaData process(HttpRequest request) {
        try{
            ControllerMetaData controllerMetaData = this.processControllerMetaData(request.getStartLine());
            MethodMetaData methodMetaData = this.processMethodMetaData(controllerMetaData, request.getStartLine());

            //if methodMetaData has parameters, then is expecting some values
            //that have to gather them from request.
            int pathVarsCount = this.countPathVariables(methodMetaData);

            if(pathVarsCount > 0){
                System.out.println(methodMetaData.getPath().getRoute());
                PathVariable[] pathVariables = this.processPathVariables(request.getStartLine(),
                        controllerMetaData.getPath().getRoute() + methodMetaData.getPath().getRoute());
                return new RouteMetaData(controllerMetaData, methodMetaData, pathVariables);
            }
            return new RouteMetaData(controllerMetaData, methodMetaData, new PathVariable[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes the controller metadata based on the HTTP request start line.
     *
     * @param startLine the start line of the HTTP request
     * @return ControllerMetaData containing the controller's route and class information
     * @throws InvocationTargetException if an error occurs during method invocation
     * @throws IllegalAccessException if access to the method is denied
     * @throws NoSuchMethodException if the method does not exist
     * @throws NoSuchObjectException if no controller is found for the given path
     */
    private ControllerMetaData processControllerMetaData(HttpRequestStartLine startLine) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchObjectException {
        String path = startLine.getPath().getRawPath();
        String[] fragments = this.pathFragments(path);
        StringBuilder controllerPathBuilder = new StringBuilder();
        ControllerMetaData controllerMetaData = null;

        for (String fragment : fragments) {
            controllerPathBuilder.append(fragment);

            ControllerRoute controllerRoute = new ControllerRoute(controllerPathBuilder.toString());
            controllerMetaData = this.CONTROLLER_MANAGER.requestController(controllerRoute);
            if (controllerMetaData != null && controllerRoute.getRoute().length() == controllerMetaData.getPath().getRoute().length()) {
                break;
            }
        }

        if (controllerMetaData == null) {
            throw new NoSuchObjectException("No controller for path/route: " + path);
        }

        return controllerMetaData;
    }

    /**
     * Processes the method metadata based on the controller metadata and HTTP request start line.
     *
     * @param controllerMetaData the metadata of the controller
     * @param startLine          the start line of the HTTP request
     * @return MethodMetaData containing the method's route and HTTP method information
     * @throws NoSuchMethodException if no method is found for the given path
     */
    private MethodMetaData processMethodMetaData(ControllerMetaData controllerMetaData, HttpRequestStartLine startLine) throws NoSuchMethodException {

        String path = startLine.getPath().getRawPath().replaceAll(controllerMetaData.getPath().getRoute(), "");

        if(path.isEmpty() || path.length() == 1){
            path = "/" + startLine.getMethod()
                    .name()
                    .toLowerCase(Locale.ROOT);
        }

        Map<String, MethodMetaData> methods = controllerMetaData.getMethodsMetaData()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getHttpMethod().equals(startLine.getMethod()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<String> fragments = new ArrayList<>(Arrays.stream(this.pathFragments(path)).toList());

        //first, try raw fragments
        //If the method we are looking for must not contain any variables.
        MethodMetaData methodMetaData = methods.get(path);
        if (methodMetaData != null) {
            if (this.pathFragments(methodMetaData.getPath().getRoute()).length == fragments.size()) {
                return methodMetaData;
            }
        }

        //second try with processed fragments e.g. /1/name -> /{integer}/{string}
        for (int i = fragments.size() - 1; i >= 0; i--) {
            String fragment = this.handleVariable(fragments.get(i));
            fragments.set(i, fragment);
            String currentPath = this.joinFragments(fragments.toArray(String[]::new));
            methodMetaData = methods.get(currentPath);
            if (methodMetaData != null) {
                if (this.pathFragments(methodMetaData.getPath().getRoute()).length == fragments.size()) {
                    break;
                }
            }
        }

        if (methodMetaData == null) {
            throw new NoSuchMethodException("No method for path/route: " + path);
        }

        return methodMetaData;
    }

    /**
     * Processes path variables from the HTTP request start line and the route.
     *
     * @param startLine the start line of the HTTP request
     * @param route     the route to match against
     * @return an array of PathVariable objects representing the path variables found
     */
    private PathVariable[] processPathVariables(HttpRequestStartLine startLine, String route) {

        List<String> stFragments = Arrays.stream(this.pathFragments(startLine.getPath().getRawPath())).toList();
        List<String> rFragments = Arrays.stream(this.pathFragments(route)).toList();

        Queue<PathVariable> variables = new ArrayDeque<>();

        for (int i = 0; i < rFragments.size(); i++) {
            String rv = rFragments.get(i);
            String stv = stFragments.get(i);

            if (!rv.equals(stv)) {
                rv = rv.replaceAll("[/{}]", "");
                stv = stv.replaceAll("/", "");
                Object parsedVar = this.PRIMITIVE_PARSER.parse(stv);
                PathVariable pathVariable = new PathVariable(rv, parsedVar);
                variables.add(pathVariable);
            }
        }
        return variables.toArray(PathVariable[]::new);
    }

    /**
     * Splits the given path into fragments, filtering out empty fragments.
     *
     * @param path the path to split
     * @return an array of non-empty path fragments
     */
    private String[] pathFragments(String path) {
        return Stream.of(path.split("/"))
                .filter(f -> !f.isEmpty())
                .map(f -> "/" + f)
                .toArray(String[]::new);
    }

    /**
     * Joins the given path fragments into a single string, excluding specified indices.
     *
     * @param fragments   the array of path fragments to join
     * @param exceptIndex indices of fragments to exclude from the join
     * @return a string representing the joined path fragments
     */
    private String joinFragments(String[] fragments, int... exceptIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> integers = Arrays.stream(exceptIndex).boxed().toList();

        for (int i = 0; i < fragments.length; i++) {
            if (integers.contains(i)) continue;
            String fragment = fragments[i];
            stringBuilder.append(fragment);
        }

        return stringBuilder.toString();
    }

    /**
     * Handles a path variable by parsing it and returning a standardized format.
     *
     * @param fragment the path variable fragment to handle
     * @return a standardized string representation of the path variable, or null if parsing fails
     */
    private String handleVariable(String fragment) {
        String var = fragment.replaceAll("/", "");
        Object stringParsed = this.PRIMITIVE_PARSER.parse(var);
        if(stringParsed == null) return null;
        return "/{" + stringParsed.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "}";
    }

    /**
     * Counts the number of path variables in the given method metadata.
     *
     * @param methodMetaData the MethodMetaData to analyze
     * @return the count of path variables in the method's route
     */
    private int countPathVariables(MethodMetaData methodMetaData){
        int count = 0;
        String route = methodMetaData.getPath().getRoute();
        for (int i = 0; i < route.length(); i++) {
            if (route.charAt(i) == '{') {
                count++;
            }
        }
        return count;
    }
}
