package org.httpServer.HttpResponse.response;

public class HttpResponse<T> {

    private HttpStatusLine statusLine;
    private HttpHeader header;
    private HttpBody<T> body;

    public HttpResponse(HttpStatusLine statusLine, HttpHeader header, HttpBody<T> body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public HttpStatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(HttpStatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public void setHeader(HttpHeader header) {
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
                + body.getBodyString();
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
