package org.httpServer.request.httpRequest;

import org.httpServer.HttpMethod;
import org.httpServer.HttpVersion;
import org.httpServer.request.httpRequestBody.HttpRequestBody;
import org.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
