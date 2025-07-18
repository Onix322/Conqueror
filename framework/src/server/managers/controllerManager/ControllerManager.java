package framework.src.server.managers.controllerManager;

import framework.src.server.httpServer.utils.route.ControllerRoute;
import framework.src.server.metadata.ControllerMetaData;

import java.lang.reflect.InvocationTargetException;

/**
 * ControllerManager interface for managing controller requests.
 * This interface defines a method to request a controller based on a given route.
 * It is designed to be implemented by classes that handle the logic of retrieving
 * and managing controllers in the application.
 */
public sealed interface ControllerManager permits ControllerManagerImpl{
    ControllerMetaData requestController(ControllerRoute route) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
