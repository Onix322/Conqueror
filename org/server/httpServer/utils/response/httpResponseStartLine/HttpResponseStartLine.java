package org.server.httpServer.utils.response.httpResponseStartLine;

import org.server.httpServer.utils.response.HttpStatus;

public class HttpResponseStartLine {
    private String version;
    private Integer statusCode;
    private String reasonPhrase;

    public HttpResponseStartLine(String version, HttpStatus status) {
        this.version = version;
        this.statusCode = status.getCode();
        this.reasonPhrase = status.getMessage();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getStatusLineString(){
        return version + " " + statusCode + " " + reasonPhrase;
    }

    @Override
    public String toString() {
        return "HttpStatusLine{" +
                "version='" + version + '\'' +
                ", statusCode=" + statusCode +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                '}';
    }
}
