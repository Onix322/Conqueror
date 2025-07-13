package src.com.server.httpServer.utils.response.httpResponseHeaders;

/**
 * Represents the headers of an HTTP response.
 * This class encapsulates the content length and content type of the response.
 * It provides methods to get and set these values, as well as a method to
 * return a formatted string representation of the headers.
 */
public class HttpResponseHeader {

    private Integer contentLength;
    private String contentType;

    public HttpResponseHeader(Integer contentLength, String contentType) {
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
