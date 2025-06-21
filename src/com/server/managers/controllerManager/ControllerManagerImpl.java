package src.com.server.managers.controllerManager;

import src.com.server.httpServer.utils.route.ControllerRoute;
import src.com.server.metadata.ControllerMetaData;
import src.com.server.processors.context.ApplicationContext;
import src.com.server.annotations.component.Component;
import src.com.server.annotations.controller.Controller;
import src.com.server.processors.metadata.ControllerMetaDataProcessor;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

@Component
public final class ControllerManagerImpl implements ControllerManager {

    private final ControllerMetaDataProcessor PROCESSOR;
    private final ApplicationContext COMPONENT_PROCESSOR;

    private ControllerManagerImpl(ControllerMetaDataProcessor processor, ApplicationContext applicationContext) {
        this.PROCESSOR = processor;
        this.COMPONENT_PROCESSOR = applicationContext;
    }

    public ControllerMetaData requestController(ControllerRoute route) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Class<?> controllerClazz = this.COMPONENT_PROCESSOR.getComponents()
                .keySet()
                .stream()
                .filter(k -> k.isAnnotationPresent(Controller.class))
                .filter(k -> k.getAnnotation(Controller.class).value().equals(route.getRoute()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No controller registered with route: " + route));

        return this.PROCESSOR.process(controllerClazz, Controller.class);
    }
}
