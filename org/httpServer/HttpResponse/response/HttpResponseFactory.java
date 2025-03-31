package org.httpServer.HttpResponse.response;

public class HttpResponseFactory<T> {
    public static <T> HttpResponse<T> create(String version, HttpStatus status, T body, String contentType){

        HttpStatusLine httpStatusLine = new HttpStatusLine(version, status);
        HttpBody<T> httpBody = new HttpBody<>(body);
        HttpHeader httpHeader = new HttpHeader(httpBody.getBodyString().length(), contentType);

        return new HttpResponse<T>(httpStatusLine, httpHeader, httpBody);
    }
}
