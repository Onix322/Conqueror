package src.com.server.handlers;

import src.com.server.annotations.component.Component;
import src.com.server.annotations.controller.mapping.parameters.RequestBody;
import src.com.server.exceptions.MissingHttpStartLine;
import src.com.server.httpServer.utils.httpMethod.BodyRequirement;
import src.com.server.httpServer.utils.request.httpRequest.HttpRequest;
import src.com.server.httpServer.utils.request.httpRequestBody.HttpRequestBody;
import src.com.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeader;
import src.com.server.httpServer.utils.request.httpRequestHeader.HttpRequestHeaderFactory;
import src.com.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLine;
import src.com.server.httpServer.utils.request.httpRequestStartLine.HttpRequestStartLineFactory;
import src.com.server.metadata.MethodMetaData;
import src.com.server.metadata.RouteMetaData;
import src.com.server.parsers.json.JsonService;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

import java.io.BufferedReader;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

@Component
public final class TransformationHandler {

    private final JsonService JSON_SERVICE;

    private TransformationHandler(JsonService jsonService) {
        this.JSON_SERVICE = jsonService;
    }

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

    public HttpRequest handleCasting(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        System.out.println(request.getStartLine().getMethod().hasBody());
        return switch (request.getStartLine().getMethod().hasBody()){
            case REQUIRED -> this.castRequiredBody(routeMetaData, request);
            case OPTIONAL -> this.castOptionalBody(routeMetaData, request);
            default -> request;
        };
    }
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

    public HttpRequest castOptionalBody(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        boolean isEligibleForBody = Arrays.stream(routeMetaData.getMethodMetaData().getParameters())
                .anyMatch(p -> p.isAnnotationPresent(RequestBody.class));

        if(isEligibleForBody){
            return this.castRequiredBody(routeMetaData, request);
        }
        return request;
    }

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
