package org.httpServer.request.httpRequest;

import org.httpServer.HttpMethod;
import org.httpServer.HttpVersion;
import org.httpServer.request.httpRequestBody.HttpRequestBody;
import org.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLine;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestBuilder<T> {
    private HttpRequestStartLine startLine;
    private HttpRequestHeader httpRequestHeader;
    private HttpRequestBody<?> httpRequestBody;

    public HttpRequestBuilder() throws URISyntaxException {
        this.startLine = new HttpRequestStartLine(HttpMethod.GET, new URI("/"), HttpVersion.HTTP_1_1);
        this.httpRequestHeader = new HttpRequestHeader("UNKNOWN", "UNKNOWN");
        this.httpRequestBody = new HttpRequestBody<>(null);
    }

    public HttpRequestBuilder<T> setStartLine(HttpRequestStartLine startLine) {
        this.startLine = startLine;
        return this;
    }

    public HttpRequestBuilder<T> setHttpRequestHeader(HttpRequestHeader httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
        return this;
    }

    public HttpRequestBuilder<T> setHttpRequestBody(HttpRequestBody<T> httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
        return this;
    }
}
