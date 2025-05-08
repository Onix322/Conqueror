package org.utils.httpServer.request.httpRequestStartLine;

import org.utils.httpServer.HttpMethod;
import org.utils.httpServer.HttpVersion;

import java.net.URI;

public class HttpRequestStartLine {

    private HttpMethod method;
    private URI path;
    private HttpVersion version;

    public HttpRequestStartLine(HttpMethod method, URI path, HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URI getPath() {
        return path;
    }

    public void setPath(URI path) {
        this.path = path;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "HttpRequestStartLine{" +
                "method=" + method +
                ", path=" + path +
                ", version=" + version +
                '}';
    }
}
