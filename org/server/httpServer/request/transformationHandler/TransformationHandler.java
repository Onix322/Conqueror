package org.server.httpServer.request.transformationHandler;

import org.server.entityManager.EntityManager;
import org.server.exepltions.MissingHttpStartLine;
import org.server.httpServer.HttpMethod;
import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.request.httpRequestBody.HttpRequestBody;
import org.server.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.server.httpServer.request.httpRequestHeader.HttpRequestHeaderFactory;
import org.server.httpServer.request.httpRequestStartLine.HttpRequestStartLine;
import org.server.httpServer.request.httpRequestStartLine.HttpRequestStartLineFactory;
import org.server.jsonService.JsonService;
import org.server.jsonService.json.properties.JsonKey;
import org.server.jsonService.json.properties.JsonProperty;
import org.server.jsonService.json.types.JsonArray;
import org.server.jsonService.json.types.JsonObject;
import org.server.jsonService.json.types.JsonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransformationHandler {

    private final JsonService JSON_SERVICE;
    private final EntityManager ENTITY_MANAGER;

    private TransformationHandler(JsonService jsonService, EntityManager entityManager) {
        this.ENTITY_MANAGER = entityManager;
        this.JSON_SERVICE = jsonService;
    }

    private static class Init {
        private static TransformationHandler INSTANCE = null;
    }

    public synchronized static void init(JsonService jsonService, EntityManager entityManager) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new TransformationHandler(jsonService, entityManager);
        }
    }

    public static TransformationHandler getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("TransformationHandler not initialized! Use TransformationHandler.init()");
        }
        return Init.INSTANCE;
    }

    public HttpRequest transformToHttpRequest(BufferedReader in) throws IOException, ReflectiveOperationException, URISyntaxException {

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

        if(startLine == null){
            throw new MissingHttpStartLine("No StartLine found in http request");
        }

        if(startLine.getMethod().equals(HttpMethod.GET)){
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

        return this.createHttpRequestBody(
                startLine,
                headers,
                bodyBuilder.toString()
        );
    }

    private HttpRequest createHttpRequestBody(HttpRequestStartLine startLine, List<HttpRequestHeader> headers, String rawBody) throws ReflectiveOperationException, URISyntaxException {
        HttpRequestBody body = new HttpRequestBody(null);
        JsonType jsonType = JSON_SERVICE.parse(rawBody);
        Object obj;

        if (jsonType instanceof JsonObject jsonObject) {
            //ask for entity class from ENTITY_MANAGER
            String[] fieldsNames = Stream.of(jsonObject.get())
                    .map(JsonProperty::key)
                    .map(JsonKey::get).distinct()
                    .toArray(String[]::new);

            Class<?> clazz = ENTITY_MANAGER.askForClass(fieldsNames);
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
                .setHttpRequestHeader(headers)
                .setStartLine(startLine)
                .setHttpRequestBody(body)
                .build();
    }
}
