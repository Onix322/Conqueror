package org.httpServer.HttpResponse.response.httpResponse;

import org.httpServer.HttpResponse.response.httpHeaders.HttpHeaders;
import org.httpServer.HttpResponse.response.httpStartLine.HttpStartLine;
import org.httpServer.HttpResponse.response.HttpStatus;
import org.httpServer.HttpResponse.response.httpBody.HttpBody;

public class HttpResponseFactory {
    public static <T> HttpResponse<T> create(String version, HttpStatus status, String contentType, T body){

        HttpStartLine httpStatusLine = new HttpStartLine(version, status);
        HttpBody<T> httpBody = new HttpBody<>(body);
        HttpHeaders httpHeader = new HttpHeaders(httpBody.toJson().length(), contentType);

        return new HttpResponse<>(httpStatusLine, httpHeader, httpBody);
    }
}
