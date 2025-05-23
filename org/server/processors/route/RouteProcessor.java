package org.server.processors.route;

import org.server.controllerManager.ControllerManager;
import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.request.httpRequestStartLine.HttpRequestStartLine;
import org.server.httpServer.route.ControllerRoute;
import org.server.httpServer.route.PathVariable;
import org.server.metadata.ControllerMetaData;
import org.server.metadata.MethodMetaData;
import org.server.metadata.RouteMetaData;
import org.server.primitiveParser.PrimitiveParser;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

@Component
public final class RouteProcessor {

    private final ControllerManager CONTROLLER_MANAGER;
    private final PrimitiveParser PRIMITIVE_PARSER;

    private RouteProcessor(ControllerManager controllerManager, PrimitiveParser primitiveParser) {
        this.CONTROLLER_MANAGER = controllerManager;
        this.PRIMITIVE_PARSER = primitiveParser;
    }

    public RouteMetaData process(HttpRequest request) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        ControllerMetaData controllerMetaData = this.processControllerMetaData(request.getStartLine());
        MethodMetaData methodMetaData = this.processMethodMetaData(controllerMetaData, request.getStartLine());

        //if methodMetaData has parameters then is expecting some values
        //which has to gather them from request.
        if(methodMetaData.getParameters().length > 0){
            PathVariable[] pathVariables = this.processPathVariables(request.getStartLine(),
                    controllerMetaData.getPath().getRoute() + methodMetaData.getPath().getRoute());
            return new RouteMetaData(controllerMetaData, methodMetaData, pathVariables);
        }
        return new RouteMetaData(controllerMetaData, methodMetaData, new PathVariable[0]);
    }

    private ControllerMetaData processControllerMetaData(HttpRequestStartLine startLine) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
            throw new NoSuchElementException("No controller for path/route: " + path);
        }

        return controllerMetaData;
    }

    private MethodMetaData processMethodMetaData(ControllerMetaData controllerMetaData, HttpRequestStartLine startLine) {

        String path = startLine.getPath().getRawPath().replaceAll(controllerMetaData.getPath().getRoute(), "");

        if(path.isEmpty() || path.length() == 1){
            path = "/" + startLine.getMethod()
                    .name()
                    .toLowerCase(Locale.ROOT);
        }

        Map<String, MethodMetaData> methods = controllerMetaData.getMethodsMetaData();
        List<String> fragments = new ArrayList<>(Arrays.stream(this.pathFragments(path)).toList());

        //first try raw fragments
        //If the method we are looking for must not contain any variables.
        MethodMetaData methodMetaData = methods.get(path);
        if (methodMetaData != null) {
            if (this.pathFragments(methodMetaData.getPath().getRoute()).length == fragments.size()) {
                return methodMetaData;
            }
        }

        //second try with processed fragments e.g /1/name -> /{integer}/{string}
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
            throw new NoSuchElementException("No method for path/route: " + path);
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

        return "/{" + stringParsed.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "}";
    }
}
