package org.httpServer.response.httpResponse;

import org.httpServer.response.HttpConnectionType;
import org.httpServer.response.httpResponseHeaders.HttpResponseHeader;
import org.httpServer.response.httpResponseStartLine.HttpResponseStartLine;
import org.httpServer.response.httpResponseBody.HttpResponseBody;

public class HttpResponse<T> {

    private HttpResponseStartLine startLine;
    private HttpResponseHeader header;
    private HttpConnectionType connection;
    private HttpResponseBody<T> body;

    public HttpResponse(HttpResponseStartLine startLine, HttpResponseHeader header, HttpConnectionType httpConnectionType, HttpResponseBody<T> body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
        this.connection = httpConnectionType;
    }

    public HttpResponseStartLine getStartLine() {
        return startLine;
    }

    public void setStartLine(HttpResponseStartLine startLine) {
        this.startLine = startLine;
    }

    public HttpResponseHeader getHeader() {
        return header;
    }

    public void setHeader(HttpResponseHeader header) {
        this.header = header;
    }

    public HttpConnectionType getConnection() {
        return connection;
    }

    public void setConnection(HttpConnectionType connection) {
        this.connection = connection;
    }

    public HttpResponseBody<T> getBody() {
        return body;
    }

    public void setBody(HttpResponseBody<T> body) {
        this.body = body;
    }

    public String getResponseString(){
        return startLine.getStatusLineString() + "\r\n"
                + header.getHeaderString() + "\r\n"
                + "Connection: " + connection.getType() + "\r\n\n"
                + body.toJson();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "startLine=" + startLine +
                ", header=" + header +
                ", connection=" + connection +
                ", body=" + body +
                '}';
    }
}

