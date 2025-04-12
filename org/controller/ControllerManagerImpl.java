package org.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ControllerManagerImpl implements ControllerManager {

    private final Map<URI, Class<?>> CONTROLLERS;

    private ControllerManagerImpl() {
        this.CONTROLLERS = new HashMap<>();
    }

    private static class Init{
        private static final ControllerManagerImpl INSTANCE = new ControllerManagerImpl();
    }

    public static ControllerManagerImpl getInstance(){
        return Init.INSTANCE;
    }

    @Override
    public Map<URI, Class<?>> getControllers() {
        return Map.copyOf(this.CONTROLLERS);
    }

    @Override
    public Class<?> requestController(URI path){
        return this.CONTROLLERS.get(path);
    }

    @Override
    public <T> ControllerManager registerController(URI path, Class<T> clazz){
        this.CONTROLLERS.put(path, clazz);
        return this;
    }
}
