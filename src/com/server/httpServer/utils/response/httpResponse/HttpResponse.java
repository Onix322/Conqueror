package src.com.server.httpServer.utils.response.httpResponse;

import src.com.server.httpServer.utils.response.HttpConnectionType;
import src.com.server.httpServer.utils.response.httpResponseHeaders.HttpResponseHeader;
import src.com.server.httpServer.utils.response.httpResponseStartLine.HttpResponseStartLine;
import src.com.server.httpServer.utils.response.httpResponseBody.HttpResponseBody;

public class HttpResponse {

    private HttpResponseStartLine startLine;
    private HttpResponseHeader header;
    private HttpConnectionType connection;
    private HttpResponseBody body;

    public HttpResponse(HttpResponseStartLine startLine, HttpResponseHeader header, HttpConnectionType httpConnectionType, HttpResponseBody body) {
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

    public HttpResponseBody getBody() {
        return body;
    }

    public void setBody(HttpResponseBody body) {
        this.body = body;
    }

    public String getResponseString(){
        return startLine.getStatusLineString() + "\r\n"
                + header.getHeaderString() + "\r\n"
                + "Connection: " + connection.getType() + "\r\n\n"
                + body.getBody();
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

