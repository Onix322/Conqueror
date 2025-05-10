package org.server.controllerManager;

import org.server.annotations.Controller;
import org.server.annotations.mapping.Mapping;
import org.server.exepltions.DuplicateMappingMethod;
import org.server.httpServer.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ControllerManagerImpl implements ControllerManager {

    private final Map<String, ControllerTemplate> CONTROLLERS;

    private ControllerManagerImpl() {
        this.CONTROLLERS = new LinkedHashMap<>();
    }

    private static class Init {
        private static ControllerManagerImpl INSTANCE = null;
    }

    public static synchronized void init() {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new ControllerManagerImpl();
        }
    }

    public static ControllerManagerImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("ControllerManager not initialized. Use ControllerManagerImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public Map<String, ControllerTemplate> getControllers() {
        return Map.copyOf(this.CONTROLLERS);
    }

    @Override
    public ControllerTemplate requestController(String path) {
        return this.CONTROLLERS.get(path);
    }

    @Override
    public ControllerManager registerController(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!clazz.isAnnotationPresent(Controller.class)) {
            throw new IllegalArgumentException(clazz + " doesn't have a @Controller(value = String) annotation");
        }

        Annotation annotation = clazz.getAnnotation(Controller.class);
        Method valueMethod = annotation.getClass().getDeclaredMethod("value");
        String value = (String) valueMethod.invoke(annotation);

        ControllerTemplate controllerTemplate = new ControllerTemplate(value, clazz, this.getMappedMethods(clazz));
        this.CONTROLLERS.put(value, controllerTemplate);
        return this;
    }

    private Map<String, MappingMethod> getMappedMethods(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String, MappingMethod> mappedMethods = new LinkedHashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            Set<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations())
                    .filter(a -> a.annotationType().isAnnotationPresent(Mapping.class))
                    .collect(Collectors.toSet());
            if (annotations.isEmpty()) continue;

            this.getMappedMethods(annotations, method, mappedMethods, clazz);
        }
        return mappedMethods;
    }

    private void getMappedMethods(Set<Annotation> annotations, Method method, Map<String, MappingMethod> mappedMethods, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?>[] parameters = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();

        for (Annotation annotation : annotations) {

            Method valueMethod = annotation.annotationType().getDeclaredMethod("value");
            String value = (String) valueMethod.invoke(annotation);

            Method gotHttpMethod = annotation.annotationType().getDeclaredMethod("httpMethod");
            HttpMethod httpMethod = (HttpMethod) gotHttpMethod.invoke(annotation);

            MappingMethod mappingMethod = new MappingMethod(value, method.getName(), parameters, returnType, httpMethod);
            if (mappedMethods.containsKey(value)) {
                throw new DuplicateMappingMethod(clazz + " has duplicated mapping annotation: " + annotation);
            }
            mappedMethods.put(value, mappingMethod);
        }
    }
}
