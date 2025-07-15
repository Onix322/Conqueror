package framework.src.server.managers.controllerManager;

import framework.src.server.httpServer.utils.route.ControllerRoute;
import framework.src.server.metadata.ControllerMetaData;
import framework.src.server.processors.context.ApplicationContext;
import framework.src.server.annotations.component.Component;
import framework.src.server.annotations.controller.Controller;
import framework.src.server.processors.metadata.ControllerMetaDataProcessor;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

/**
 * ControllerManagerImpl is responsible for managing controller instances and their metadata.
 * It processes requests for controllers based on the specified route and retrieves the corresponding metadata.
 */
@Component
public final class ControllerManagerImpl implements ControllerManager {

    private final ControllerMetaDataProcessor PROCESSOR;
    private final ApplicationContext COMPONENT_PROCESSOR;

    private ControllerManagerImpl(ControllerMetaDataProcessor processor, ApplicationContext applicationContext) {
        this.PROCESSOR = processor;
        this.COMPONENT_PROCESSOR = applicationContext;
    }

    /**
     * Requests the metadata for a controller based on the provided route.
     *
     * @param route The route to find the controller for.
     * @return The metadata of the controller associated with the specified route.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is illegal.
     * @throws NoSuchMethodException If the method does not exist.
     */
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
