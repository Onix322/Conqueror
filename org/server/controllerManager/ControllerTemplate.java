package org.server.controllerManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ControllerTemplate {
    private String path;
    private Class<?> controllerClass;
    private Map<String, MappingMethod> mappingMethods;

    public ControllerTemplate(String path, Class<?> controllerClass, Map<String, MappingMethod> mappingMethods) {
        this.path = path;
        this.controllerClass = controllerClass;
        this.mappingMethods = mappingMethods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    public Map<String, MappingMethod> getMappingMethods() {
        return mappingMethods;
    }

    public void setMappingMethods(Map<String, MappingMethod> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }

    @Override
    public String toString() {
        return "ControllerTemplate{" +
                "path='" + path + '\'' +
                ", controllerClass=" + controllerClass +
                ", mappingMethods=" + mappingMethods +
                '}';
    }
}
