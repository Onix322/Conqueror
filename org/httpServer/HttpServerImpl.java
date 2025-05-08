package org.httpServer;

import org.configuration.Configuration;
import org.controller.ControllerManager;
import org.entityManager.EntityManager;
import org.httpServer.request.httpRequest.HttpRequest;
import org.httpServer.request.httpRequestBody.HttpRequestBody;
import org.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.httpServer.request.httpRequestHeader.HttpRequestHeaderFactory;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLine;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLineFactory;
import org.httpServer.response.HttpConnectionType;
import org.httpServer.response.HttpStatus;
import org.httpServer.response.httpResponse.HttpResponse;
import org.httpServer.response.httpResponse.HttpResponseFactory;
import org.services.jsonService.JsonService;
import org.services.jsonService.json.properties.JsonKey;
import org.services.jsonService.json.properties.JsonProperty;
import org.services.jsonService.json.types.JsonArray;
import org.services.jsonService.json.types.JsonObject;
import org.services.jsonService.json.types.JsonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class HttpServerImpl implements HttpServer {

    private final Configuration CONFIGURATION;
    private final ExecutorService EXECUTOR_SERVICE;
    private final JsonService JSON_PARSER;
    private final EntityManager ENTITY_MANAGER;
    private final ControllerManager CONTROLLER_MANAGER;

    private HttpServerImpl(Configuration configuration, ExecutorService executorService, JsonService jsonParser, EntityManager entityManager, ControllerManager controllerManager) {
        this.CONFIGURATION = configuration;
        this.EXECUTOR_SERVICE = executorService;
        this.JSON_PARSER = jsonParser;
        this.ENTITY_MANAGER = entityManager;
        this.CONTROLLER_MANAGER = controllerManager;
    }

    private static class Init {
        private static HttpServerImpl instance;
    }

    public synchronized static void create(Configuration configuration, ExecutorService executorService, JsonService jsonParser, EntityManager entityManager, ControllerManager controllerManager) {
        if (Init.instance == null) {
            System.out.println("Setting up server configuration...");
            Init.instance = new HttpServerImpl(configuration, executorService, jsonParser, entityManager, controllerManager);
        }
    }

    public static HttpServerImpl getInstance() {
        if (Init.instance == null) {
            throw new IllegalStateException("Server not initialized!");
        }
        return Init.instance;
    }

    @Override
    public Configuration getConfig() {
        return CONFIGURATION;
    }

    @Override
    public void start() {
        System.out.println("Starting server...");

        int port = (Integer.parseInt(getConfig().readProperty("server.port")));
        String address = getConfig().readProperty("server.hostname");

        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);

        try {
            ServerSocket serverSocket = new ServerSocket(inetSocketAddress.getPort(), 0, inetSocketAddress.getAddress());
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                this.listen(serverSocket.accept());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    private void listen(Socket clientSocket) {
        EXECUTOR_SERVICE.submit(() -> {
            try (clientSocket) {
                //* STEP 1: handle request
                HttpRequest request = this.handleRequest(clientSocket);

                //* STEP 2: handle response based on request
                HttpResponse response = this.handleResponse(request);

                //* STEP 3: send response
                this.sendResponse(clientSocket, response);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getLocalizedMessage());
            }
        });
    }

    private HttpRequest handleRequest(Socket clientSocket) throws Exception {

        HttpRequestStartLine startLine = null;
        List<HttpRequestHeader> headers = new ArrayList<>();
        HttpRequestBody body = new HttpRequestBody(null);
        StringBuilder bodyBuilder = new StringBuilder();
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));


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

        //read body
        while (in.ready() && (b = in.read()) != -1) {
            bodyBuilder.append(Character.toString(b));
        }
        //* VA trimite URI si clasa entitatii catre (baza de date) mai in detaliu

        if (!bodyBuilder.isEmpty()) {
            JsonType jsonType = JSON_PARSER.parse(bodyBuilder.toString());
            Object obj;
            if (jsonType instanceof JsonObject jsonObject) {
                //ask for entity class from ENTITY_MANAGER
                String[] fieldsNames = Stream.of(jsonObject.get())
                        .map(JsonProperty::key)
                        .map(JsonKey::get).distinct()
                        .toArray(String[]::new);

                Class<?> clazz = ENTITY_MANAGER.askForClass(fieldsNames);
                obj = JSON_PARSER.mapObject(jsonObject, clazz);
            } else if (jsonType instanceof JsonArray jsonArray) {
                obj = JSON_PARSER.mapArray(jsonArray, LinkedList.class);
            } else {
                obj = null;
            }

            body.setBody(obj);
        } else {
            body.setBody("");
        }
        clientSocket.shutdownInput();

        return HttpRequest.builder()
                .setHttpRequestHeader(headers)
                .setStartLine(startLine)
                .setHttpRequestBody(body)
                .build();
    }

    //TODO CREATE THE RESPONSE

    private HttpResponse handleResponse(HttpRequest httpRequest) throws IllegalAccessException {

        HttpRequestStartLine startLine = httpRequest.getStartLine();

        return HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                HttpConnectionType.CLOSED,
                httpRequest.getHttpRequestBody().getBody()
        );
    }

    private void sendResponse(Socket clientSocket, HttpResponse httpResponse) throws IOException {
        System.out.println("Line 102 HttpServerImpl: \n" + httpResponse.getResponseString());

        clientSocket.getOutputStream()
                .write(httpResponse.getResponseString().getBytes(StandardCharsets.UTF_8));

        clientSocket.shutdownOutput();
    }

    @Override
    public String toString() {
        return "HttpServerImpl{" + CONFIGURATION + '}';
    }
}
