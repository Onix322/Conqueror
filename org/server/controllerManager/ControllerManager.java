package org.server.controllerManager;

import org.server.httpServer.route.ControllerRoute;
import org.server.metadata.ControllerMetaData;

import java.util.Map;

public sealed interface ControllerManager permits ControllerManagerImpl{
    Map<String, ControllerMetaData> getControllers();

    ControllerMetaData requestController(ControllerRoute route);

    <T> ControllerManager registerController(Class<T> clazz) throws Exception;
}
