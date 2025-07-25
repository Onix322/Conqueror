package framework.src.server.httpServer.utils.request.httpRequest;

import framework.src.server.httpServer.utils.request.httpRequestBody.HttpRequestBody;
import framework.src.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeader;
import framework.src.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Represents an HTTP request, encapsulating the start line, headers, and body.
 * This class is used to construct and manage HTTP requests in a structured manner.
 */
public class HttpRequest {

    private HttpRequestStartLine startLine;
    private List<HttpRequestHeader> httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    HttpRequest(HttpRequestBuilder httpRequestBuilder){
        this.httpRequestBody = httpRequestBuilder.getHttpRequestBody();
        this.httpRequestHeader = httpRequestBuilder.getHttpRequestHeader();
        this.startLine = httpRequestBuilder.getStartLine();
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public void setStartLine(HttpRequestStartLine startLine) {
        this.startLine = startLine;
    }

    public List<HttpRequestHeader> getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public void setHttpRequestHeader(List<HttpRequestHeader> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }

    public void setHttpRequestBody(HttpRequestBody httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequestBuilder builder() throws URISyntaxException {
        return new HttpRequestBuilder();
    }
    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", httpRequestHeader=" + httpRequestHeader +
                ", httpRequestBody=" + httpRequestBody +
                '}';
    }
}
