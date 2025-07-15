package framework.src.server.httpServer.utils.response.httpResponse;

import framework.src.server.httpServer.utils.response.HttpConnectionType;
import framework.src.server.httpServer.utils.response.httpResponseHeaders.HttpResponseHeader;
import framework.src.server.httpServer.utils.response.httpResponseStartLine.HttpResponseStartLine;
import framework.src.server.httpServer.utils.response.HttpStatus;
import framework.src.server.httpServer.utils.response.httpResponseBody.HttpResponseBody;

/**
 * Factory class to create instances of HttpResponse.
 * This class provides a static method to create an HttpResponse
 * with the specified parameters such as HTTP version, status, content type,
 * connection type, and body.
 */
public class HttpResponseFactory {
    public static HttpResponse create(String version, HttpStatus status, String contentType, HttpConnectionType connectionType, Object body){

        HttpResponseStartLine httpStatusLine = new HttpResponseStartLine(version, status);
        HttpResponseBody httpResponseBody = new HttpResponseBody(body);
        HttpResponseHeader httpHeader = new HttpResponseHeader(httpResponseBody.toString().length(), contentType);

        return new HttpResponse(httpStatusLine, httpHeader,connectionType, httpResponseBody);
    }
}
