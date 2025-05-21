package org.server.controllerManager;

import org.server.httpServer.route.ControllerRoute;
import org.server.processors.metadata.ControllerMetaDataProcessor;
import org.server.processors.components.annotations.Singleton;
import org.server.processors.components.annotations.controller.Controller;
import org.server.metadata.ControllerMetaData;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public final class ControllerManagerImpl implements ControllerManager {

    private final Map<String, ControllerMetaData> CONTROLLERS;
    private final ControllerMetaDataProcessor PROCESSOR;

    private ControllerManagerImpl(ControllerMetaDataProcessor processor) {
        this.PROCESSOR = processor;
        this.CONTROLLERS = new LinkedHashMap<>();
    }

    @Override
    public Map<String, ControllerMetaData> getControllers() {
        return Map.copyOf(this.CONTROLLERS);
    }

    @Override
    public ControllerMetaData requestController(ControllerRoute route) {
        return this.CONTROLLERS.get(route.getRoute());
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
        ControllerMetaData controllerMetaData = this.PROCESSOR.process(clazz, Controller.class);

        this.CONTROLLERS.put(
                controllerMetaData.getPath().getRoute(),
                controllerMetaData
        );

        return this;
    }
}
