package org.server.controllerManager;

import org.server.httpServer.route.ControllerRoute;
import org.server.metadata.ControllerMetaData;
import org.server.processors.context.ContextProcessor;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.controller.Controller;
import org.server.processors.metadata.ControllerMetaDataProcessor;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

@Component
public final class ControllerManagerImpl implements ControllerManager {

    private final ControllerMetaDataProcessor PROCESSOR;
    private final ContextProcessor COMPONENT_PROCESSOR;

    private ControllerManagerImpl(ControllerMetaDataProcessor processor, ContextProcessor contextProcessor) {
        this.PROCESSOR = processor;
        this.COMPONENT_PROCESSOR = contextProcessor;
    }

    public ControllerMetaData requestController(ControllerRoute route) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Class<?> controllerClazz = this.COMPONENT_PROCESSOR.getContext()
                .keySet()
                .stream()
                .filter(k -> k.isAnnotationPresent(Controller.class))
                .filter(k -> k.getAnnotation(Controller.class).value().equals(route.getRoute()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No controller registered with route: " + route));

        return this.PROCESSOR.process(controllerClazz, Controller.class);
    }
}
