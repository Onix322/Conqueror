package src.com.server.httpServer.utils.request.httpRequestStartLine;

import src.com.server.httpServer.utils.httpMethod.HttpMethod;
import src.com.server.httpServer.utils.HttpVersion;

import java.net.URI;

/**
 * Represents the start line of an HTTP request.
 * This includes the HTTP method, the request path, and the HTTP version.
 */
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
