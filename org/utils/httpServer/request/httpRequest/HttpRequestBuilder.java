package org.utils.httpServer.request.httpRequest;

import org.utils.httpServer.HttpMethod;
import org.utils.httpServer.HttpVersion;
import org.utils.httpServer.request.httpRequestBody.HttpRequestBody;
import org.utils.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.utils.httpServer.request.httpRequestStartLine.HttpRequestStartLine;

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
