package framework.src.server.httpServer.utils.request.httpRequest;

import framework.src.server.httpServer.utils.httpMethod.HttpMethod;
import framework.src.server.httpServer.utils.HttpVersion;
import framework.src.server.httpServer.utils.request.httpRequestBody.HttpRequestBody;
import framework.src.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeader;
import framework.src.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for constructing HTTP requests.
 * This class allows you to set the start line, headers, and body of the HTTP request.
 * It provides a fluent interface for building an HttpRequest object.
 */
public class HttpRequestBuilder {
    private HttpRequestStartLine startLine;
    private List<HttpRequestHeader> httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    public HttpRequestBuilder() throws URISyntaxException {
        this.startLine = new HttpRequestStartLine(HttpMethod.GET, new URI("/"), HttpVersion.HTTP_1_1);
        this.httpRequestHeader = new ArrayList<>();
        this.httpRequestBody = new HttpRequestBody(null);
    }

    public HttpRequestBuilder setStartLine(HttpRequestStartLine startLine) {
        this.startLine = startLine;
        return this;
    }

    public HttpRequestBuilder setHttpRequestHeader(List<HttpRequestHeader> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
        return this;
    }

    public HttpRequestBuilder setHttpRequestBody(HttpRequestBody httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
        return this;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public List<HttpRequestHeader> getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }

    public HttpRequest build(){
        return new HttpRequest(this);
    }

    @Override
    public String toString() {
        return "HttpRequestBuilder{" +
                "startLine=" + startLine +
                ", httpRequestHeader=" + httpRequestHeader +
                ", httpRequestBody=" + httpRequestBody +
                '}';
    }
}
