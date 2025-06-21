package src.com.server.managers.controllerManager;

import src.com.server.httpServer.utils.route.ControllerRoute;
import src.com.server.metadata.ControllerMetaData;

import java.lang.reflect.InvocationTargetException;

public sealed interface ControllerManager permits ControllerManagerImpl{
    ControllerMetaData requestController(ControllerRoute route) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
