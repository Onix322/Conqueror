package org.server.controllerManager;

import org.server.httpServer.route.ControllerRoute;
import org.server.processors.metadata.ControllerMetaDataProcessor;
import org.server.processors.components.annotations.Component;
import org.server.processors.components.annotations.controller.Controller;
import org.server.metadata.ControllerMetaData;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
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
