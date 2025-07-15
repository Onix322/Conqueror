package framework.src.server.httpServer.utils.response.httpResponse;

import framework.src.server.httpServer.utils.response.HttpConnectionType;
import framework.src.server.httpServer.utils.response.httpResponseHeaders.HttpResponseHeader;
import framework.src.server.httpServer.utils.response.httpResponseStartLine.HttpResponseStartLine;
import framework.src.server.httpServer.utils.response.httpResponseBody.HttpResponseBody;

/**
 * Represents an HTTP response, encapsulating the start line, headers, connection type, and body.
 * This class provides methods to construct the full response string and access its components.
 */
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

