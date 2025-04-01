package org.httpServer.HttpResponse.response.httpResponse;

import org.httpServer.HttpResponse.response.httpHeaders.HttpHeaders;
import org.httpServer.HttpResponse.response.httpStartLine.HttpStartLine;
import org.httpServer.HttpResponse.response.httpBody.HttpBody;

public class HttpResponse<T> {

    private HttpStartLine statusLine;
    private HttpHeaders header;
    private HttpBody<T> body;

    public HttpResponse(HttpStartLine statusLine, HttpHeaders header, HttpBody<T> body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public HttpStartLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(HttpStartLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public void setHeader(HttpHeaders header) {
        this.header = header;
    }

    public HttpBody<T> getBody() {
        return body;
    }

    public void setBody(HttpBody<T> body) {
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
