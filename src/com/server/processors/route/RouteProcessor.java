package src.com.server.processors.route;

import src.com.server.managers.controllerManager.ControllerManager;
import src.com.server.httpServer.utils.request.httpRequest.HttpRequest;
import src.com.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;
import src.com.server.httpServer.utils.route.ControllerRoute;
import src.com.server.httpServer.utils.route.PathVariable;
import src.com.server.metadata.ControllerMetaData;
import src.com.server.metadata.MethodMetaData;
import src.com.server.metadata.RouteMetaData;
import src.com.server.parsers.primitiveParser.PrimitiveParser;
import src.com.server.annotations.component.Component;

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

    private String[] pathFragments(String path) {
        return Stream.of(path.split("/"))
                .filter(f -> !f.isEmpty())
                .map(f -> "/" + f)
                .toArray(String[]::new);
    }

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

    private String handleVariable(String fragment) {
        String var = fragment.replaceAll("/", "");
        Object stringParsed = this.PRIMITIVE_PARSER.parse(var);
        if(stringParsed == null) return null;
        return "/{" + stringParsed.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "}";
    }

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
