package org.server.managers.controllerManager;

import org.server.httpServer.utils.route.ControllerRoute;
import org.server.metadata.ControllerMetaData;

import java.lang.reflect.InvocationTargetException;

public sealed interface ControllerManager permits ControllerManagerImpl{
    ControllerMetaData requestController(ControllerRoute route) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
