package org.server.metadata;

import org.server.httpServer.HttpMethod;
import org.server.httpServer.route.MethodRoute;

import java.util.Arrays;

public class MethodMetaData implements MetaData<MethodMetaData> {
    private MethodRoute path;
    private String name;
    private Class<?>[] parameters;
    private Class<?> returnType;
    private HttpMethod httpMethod;

    public MethodMetaData(MethodRoute path, String name, Class<?>[] parameters, Class<?> returnType, HttpMethod httpMethod) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.httpMethod = httpMethod;
    }

    public MethodRoute getPath() {
        return path;
    }

    public void setPath(MethodRoute path) {
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
                "MethodRoute='" + path+ '\'' +
                ", name='" + name + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", returnType=" + returnType +
                ", httpMethod=" + httpMethod +
                '}';
    }

    @Override
    public MethodMetaData getMetaData() {
        return this;
    }
}
