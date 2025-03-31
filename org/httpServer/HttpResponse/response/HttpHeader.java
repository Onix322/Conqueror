package org.httpServer.HttpResponse.response;

public class HttpHeader {

    private Integer contentLength;
    private String contentType;

    public HttpHeader(Integer contentLength, String contentType) {
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeaderString(){
        return  "Content-Length: " + contentLength
                + "\r\n"
                + "Content-Type: " + contentType;
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "contentLength=" + contentLength +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
