package org.server.controllerManager;

import org.server.httpServer.HttpMethod;

import java.util.Arrays;

public class MappingMethod {
    private String path;
    private String name;
    private Class<?>[] parameters;
    private Class<?> returnType;
    private HttpMethod httpMethod;

    public MappingMethod(String path, String name, Class<?>[] parameters, Class<?> returnType, HttpMethod httpMethod) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public void setParameters(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "MappingMethod{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", returnType=" + returnType +
                ", httpMethod=" + httpMethod +
                '}';
    }
}
