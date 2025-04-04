package org.httpServer.request.httpRequest;

import org.httpServer.request.httpRequestBody.HttpRequestBody;
import org.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLine;

public class HttpRequest<T> {

    private HttpRequestStartLine startLine;
    private HttpRequestHeader httpRequestHeader;
    private HttpRequestBody<T> httpRequestBody;

    private HttpRequest(){

    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public void setStartLine(HttpRequestStartLine startLine) {
        this.startLine = startLine;
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public void setHttpRequestHeader(HttpRequestHeader httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }

    public HttpRequestBody<T> getHttpRequestBody() {
        return httpRequestBody;
    }

    public void setHttpRequestBody(HttpRequestBody<T> httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }
}
