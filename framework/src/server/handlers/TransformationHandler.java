package framework.src.server.handlers;

import framework.src.server.annotations.component.Component;
import framework.src.server.annotations.controller.mapping.parameters.RequestBody;
import framework.src.server.exceptions.MissingHttpStartLine;
import framework.src.server.httpServer.utils.httpMethod.BodyRequirement;
import framework.src.server.httpServer.utils.request.httpRequest.HttpRequest;
import framework.src.server.httpServer.utils.request.httpRequestBody.HttpRequestBody;
import framework.src.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeader;
import framework.src.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeaderFactory;
import framework.src.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;
import framework.src.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLineFactory;
import framework.src.server.metadata.MethodMetaData;
import framework.src.server.metadata.RouteMetaData;
import framework.src.server.parsers.json.JsonService;
import framework.src.server.parsers.json.utils.types.JsonArray;
import framework.src.server.parsers.json.utils.types.JsonObject;
import framework.src.server.parsers.json.utils.types.JsonType;

import java.io.BufferedReader;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * TransformationHandler is responsible for handling the transformation of HTTP requests.
 * It reads the request from a BufferedReader, parses the headers, and processes the body
 * according to the requirements specified in the RouteMetaData.
 */
@Component
public final class TransformationHandler {

    private final JsonService JSON_SERVICE;

    private TransformationHandler(JsonService jsonService) {
        this.JSON_SERVICE = jsonService;
    }

    /**
     * Handles the transformation of an HTTP request from a BufferedReader.
     * It reads the start line, headers, and body of the request, and returns an HttpRequest object.
     *
     * @param in BufferedReader to read the HTTP request from
     * @return HttpRequest object containing the parsed request data
     * @throws Exception if there is an error during parsing
     */
    public HttpRequest handleTransformation(BufferedReader in) throws Exception {

        HttpRequestStartLine startLine = null;
        List<HttpRequestHeader> headers = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        int b;

        //read headers
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (Pattern.compile("^(GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH|TRACE)\\s/\\S*\\sHTTP/\\d\\.\\d$").matcher(line).find()) {
                startLine = HttpRequestStartLineFactory.create(line);
            }

            if (Pattern.compile("\\w+:\\s").matcher(line).find()) {
                headers.add(HttpRequestHeaderFactory.create(line));
            }
        }

        if (startLine == null) {
            throw new MissingHttpStartLine("No StartLine found in http request");
        }

        //if HttpMethod must not have a body jump over
        if (startLine.getMethod().hasBody().equals(BodyRequirement.FORBIDDEN)) {
            return HttpRequest.builder()
                    .setHttpRequestHeader(headers)
                    .setStartLine(startLine)
                    .setHttpRequestBody(new HttpRequestBody(null))
                    .build();
        }

        //read body
        while (in.ready() && (b = in.read()) != -1) {
            bodyBuilder.append(Character.toString(b));
        }

        HttpRequestBody httpRequestBody = new HttpRequestBody(bodyBuilder.toString());

        return HttpRequest.builder()
                .setHttpRequestHeader(headers)
                .setStartLine(startLine)
                .setHttpRequestBody(httpRequestBody)
                .build();
    }

    /**
     * Handles the casting of the HTTP request body based on the requirements specified in the RouteMetaData.
     * It checks if the body is required or optional and processes it accordingly.
     *
     * @param routeMetaData RouteMetaData containing method metadata for casting
     * @param request HttpRequest to be processed
     * @return HttpRequest with the body cast to the appropriate type
     * @throws Exception if there is an error during casting
     */
    public HttpRequest handleCasting(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        System.out.println(request.getStartLine().getMethod().hasBody());
        return switch (request.getStartLine().getMethod().hasBody()){
            case REQUIRED -> this.castRequiredBody(routeMetaData, request);
            case OPTIONAL -> this.castOptionalBody(routeMetaData, request);
            default -> request;
        };
    }

    /**
     * Casts the HTTP request body to the required type specified in the RouteMetaData.
     * It parses the raw body and maps it to the appropriate class type.
     *
     * @param routeMetaData RouteMetaData containing method metadata for casting
     * @param request HttpRequest to be processed
     * @return HttpRequest with the body cast to the required type
     * @throws Exception if there is an error during casting
     */
    public HttpRequest castRequiredBody(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        HttpRequestBody body = new HttpRequestBody(null);
        String rawBody = request.getHttpRequestBody().getBody(String.class);
        Class<?> clazz = this.getRequestBodyParam(routeMetaData.getMethodMetaData());
        JsonType jsonType = JSON_SERVICE.parse(rawBody);
        Object obj;

        if (jsonType instanceof JsonObject jsonObject) {
            obj = JSON_SERVICE.mapObject(jsonObject, clazz);
        } else if (jsonType instanceof JsonArray jsonArray) {
            obj = JSON_SERVICE.mapArray(jsonArray, LinkedList.class);
        } else if (rawBody.isEmpty()) {
            obj = "";
        } else {
            obj = null;
        }

        body.setBody(obj);

        return HttpRequest.builder()
                .setHttpRequestHeader(request.getHttpRequestHeader())
                .setStartLine(request.getStartLine())
                .setHttpRequestBody(body)
                .build();
    }

    /**
     * Casts the HTTP request body to an optional type specified in the RouteMetaData.
     * It checks if the body is eligible for casting and processes it accordingly.
     *
     * @param routeMetaData RouteMetaData containing method metadata for casting
     * @param request HttpRequest to be processed
     * @return HttpRequest with the body cast to the optional type if applicable
     * @throws Exception if there is an error during casting
     */
    public HttpRequest castOptionalBody(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        boolean isEligibleForBody = Arrays.stream(routeMetaData.getMethodMetaData().getParameters())
                .anyMatch(p -> p.isAnnotationPresent(RequestBody.class));

        if(isEligibleForBody){
            return this.castRequiredBody(routeMetaData, request);
        }
        return request;
    }

    /**
     * Retrieves the class type of the request body parameter from the method metadata.
     * It checks for the presence of the @RequestBody annotation and ensures that there is exactly one such parameter.
     *
     * @param method MethodMetaData containing method parameters
     * @return Class type of the request body parameter
     * @throws NoSuchElementException if no @RequestBody is present or if multiple are found
     */
    private Class<?> getRequestBodyParam(MethodMetaData method) throws NoSuchElementException {
        List<Parameter> parameters = Arrays.stream(method.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .toList();

        if (parameters.isEmpty()) {
            throw new NoSuchElementException("No @RequestBody is present.");
        } else if (parameters.size() > 1) {
            throw new NoSuchElementException("Ambiguous annotation: multiple @RequestBody, only 1 allowed.");
        }

        return parameters.getFirst().getType();
    }
}
