package org.utils.controller;

import java.net.URI;
import java.util.Map;

public interface ControllerManager {
    Map<URI, Class<?>> getControllers();

    Class<?> requestController(URI path);

    <T> ControllerManager registerController(URI path, Class<T> clazz);
}
