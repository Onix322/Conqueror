package org.server.controllerManager;

import org.server.metadata.ClassMetaData;

import java.util.Map;

public interface ControllerManager {
    Map<String, ClassMetaData> getControllers();

    ClassMetaData requestController(String path);

    <E, T extends Map<String, E>> E request(String path, T map);

    <T> ControllerManager registerController(Class<T> clazz) throws Exception;
}
