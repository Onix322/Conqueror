package org.server.controllerManager;

import org.server.processors.ClassProcessor;
import org.server.processors.annotations.controller.Controller;
import org.server.metadata.ClassMetaData;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControllerManagerImpl implements ControllerManager {

    private final Map<String, ClassMetaData> CONTROLLERS;
    private final ClassProcessor PROCESSOR;

    private ControllerManagerImpl(ClassProcessor processor) {
        this.PROCESSOR = processor;
        this.CONTROLLERS = new LinkedHashMap<>();
    }

    private static class Init {
        private static ControllerManagerImpl INSTANCE = null;
    }

    public static synchronized void init(ClassProcessor processor) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new ControllerManagerImpl(processor);
        }
    }

    public static ControllerManagerImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("ControllerManager not initialized. Use ControllerManagerImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public Map<String, ClassMetaData> getControllers() {
        return Map.copyOf(this.CONTROLLERS);
    }

    @Override
    public ClassMetaData requestController(String path) {
        return this.CONTROLLERS.get(path);
    }

    @Override
    public <E, T extends Map<String, E>> E request(String path, T map){
        String key = "";
        for (String cKey : map.keySet()) {
            String cleanKey = cKey.replaceAll("/\\{[A-z0-9]+}", "");
            if (path.contains(cleanKey) && cKey.length() >= key.length()) {
                key = cKey;
            }
        }
        return map.get(key);
    }

    public <T> ControllerManager registerController(Class<T> clazz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (!clazz.isAnnotationPresent(Controller.class)) {
            throw new IllegalArgumentException(clazz + " doesn't have a @Controller(value = String) annotation");
        }
        ClassMetaData classMetaData = this.PROCESSOR.process(clazz, Controller.class);

        this.CONTROLLERS.put(
                classMetaData.getPath(),
                classMetaData
        );

        return this;
    }
}
