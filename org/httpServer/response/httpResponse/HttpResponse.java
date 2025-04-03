package org.httpServer.response.httpResponse;

import org.httpServer.response.httpResponseHeaders.HttpResponseHeader;
import org.httpServer.response.httpResponseStartLine.HttpResponseStartLine;
import org.httpServer.response.httpResponseBody.HttpResponseBody;

public class HttpResponse<T> {

    private HttpResponseStartLine statusLine;
    private HttpResponseHeader header;
    private HttpResponseBody<T> body;

    public HttpResponse(HttpResponseStartLine statusLine, HttpResponseHeader header, HttpResponseBody<T> body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public HttpResponseStartLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(HttpResponseStartLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpResponseHeader getHeader() {
        return header;
    }

    public void setHeader(HttpResponseHeader header) {
        this.header = header;
    }

    public HttpResponseBody<T> getBody() {
        return body;
    }

    public void setBody(HttpResponseBody<T> body) {
        this.body = body;
    }

    public String getResponseString(){
        return statusLine.getStatusLineString() + "\r\n"
                + header.getHeaderString() + "\r\n\n"
                + body.toJson();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
