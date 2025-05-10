package org.server.controllerManager;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface ControllerManager {
    Map<String, ControllerTemplate> getControllers();

    ControllerTemplate requestController(String path);

    ControllerManager registerController(Class<?> clazz) throws Exception;
}
