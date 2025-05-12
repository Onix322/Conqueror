package org.server.httpServer.route;

import org.server.controllerManager.ControllerManager;
import org.server.metadata.ClassMetaData;
import org.server.metadata.MethodMetaData;
import org.server.exepltions.HttpStartLineIncorrect;
import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.request.httpRequestStartLine.HttpRequestStartLine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class RouteHandler {

    private final ControllerManager CONTROLLER_MANAGER;

    private RouteHandler(ControllerManager controllerManager) {
        this.CONTROLLER_MANAGER = controllerManager;
    }

    private static class Init {
        private static RouteHandler INSTANCE = null;
    }

    public synchronized static void init(ControllerManager controllerManager) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new RouteHandler(controllerManager);
        }
    }

    public static RouteHandler getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("RouteHandler not initialized! Use RouteHandler.init()");
        }
        return Init.INSTANCE;
    }

    /// /////
    public Route handleRouting(HttpRequest request) {
        return switch (request.getStartLine().getMethod()) {
            case GET -> this.getHandler(request);
            case PUT, HEAD, POST, PATCH, DELETE, OPTIONS -> null;
            default -> throw new HttpStartLineIncorrect();
        };
    }

    public Route getHandler(HttpRequest request) {
        String path = request.getStartLine().getPath().getRawPath();
        ClassMetaData classMetadata = this.CONTROLLER_MANAGER.request(path, this.CONTROLLER_MANAGER.getControllers());
        MethodMetaData methodMetadata = this.CONTROLLER_MANAGER.request(path, classMetadata.getMethodsMetaData());
        PathVariable[] variables = this.subtractPathVariables(request.getStartLine(), classMetadata.getPath() + methodMetadata.getPath());
        List<String> fragments = Arrays.stream(this.subtractFragments(request.getStartLine())).toList();

        ControllerRoute controllerRoute = new ControllerRoute(fragments.getFirst());
        MappingMethodRoute mappedMethodRoute = new MappingMethodRoute(methodMetadata.getPath());

        return Route.builder()
                .setMappedMethodRoute(mappedMethodRoute)
                .setControllerRoute(controllerRoute)
                .setPathVariables(variables)
                .build();
    }

    public String[] subtractFragments(HttpRequestStartLine startLine) {
        String path = startLine.getPath().getRawPath();
        return this.subtractFragments(path);
    }

    public String[] subtractFragments(String path) {
        return Stream.of(path.split("/"))
                .filter(fragment -> !fragment.isEmpty())
                .map(fragment -> "/" + fragment)
                .toArray(String[]::new);
    }

    public PathVariable[] subtractPathVariables(HttpRequestStartLine startLine, String fullPathFromMetaData) {
        List<String> fragmentsStartLine = Arrays.stream(this.subtractFragments(startLine)).toList();
        List<String> fragmentsMappingMethod = Arrays.stream(subtractFragments(fullPathFromMetaData)).toList();
        List<PathVariable> pathVariables = new LinkedList<>();

        if (fragmentsStartLine.size() != fragmentsMappingMethod.size()) {
            throw new IllegalArgumentException(
                    "Size of arguments: "
                            + Arrays.toString(fragmentsMappingMethod.toArray(String[]::new))
                            + " incompatible with: "
                            + Arrays.toString(fragmentsStartLine.toArray(String[]::new))
            );
        }

        for (int i = 0; i < fragmentsMappingMethod.size(); i++) {
            String mapMetFrag = fragmentsMappingMethod.get(i);
            String startLinePath = fragmentsStartLine.get(i);
            if (mapMetFrag.matches("/\\{([^}]+)}")) {
                PathVariable pathVariable = new PathVariable(
                        mapMetFrag.substring(1),
                        startLinePath.substring(1)
                );
                pathVariables.add(pathVariable);
            }
        }

        return pathVariables.toArray(PathVariable[]::new);
    }

    public String subtractFullPath(ClassMetaData classMetaData, MethodMetaData methodMetaData){
        return classMetaData.getPath() + methodMetaData.getPath();
    }

    public PathVariable[] getVariables(HttpRequest request) {
        String path = request.getStartLine().getPath().getRawPath();
        ClassMetaData classMetadata = this.CONTROLLER_MANAGER.request(path, this.CONTROLLER_MANAGER.getControllers());
        MethodMetaData methodMetadata = this.CONTROLLER_MANAGER.request(path, classMetadata.getMethodsMetaData());
        String fullPathFromMetadata = this.subtractFullPath(classMetadata, methodMetadata);

        return this.subtractPathVariables(request.getStartLine(), fullPathFromMetadata);
    }
}
