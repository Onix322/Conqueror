package org.server.controllerManager;

import org.server.httpServer.route.ControllerRoute;
import org.server.metadata.ControllerMetaData;

import java.util.Map;

public interface ControllerManager {
    Map<String, ControllerMetaData> getControllers();

    ControllerMetaData requestController(ControllerRoute route);

    <E, T extends Map<String, E>> E request(String path, T map);

    <T> ControllerManager registerController(Class<T> clazz) throws Exception;
}
