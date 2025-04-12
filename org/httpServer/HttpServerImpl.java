package org.httpServer;

import org.controller.ControllerManager;
import org.configuration.Configuration;
import org.entityManager.EntityManager;
import org.exepltions.NoEntityMatchesJson;
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
import org.json.parser.JsonService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        } catch (IOException | ExecutionException | InterruptedException e) {
            this.stop();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    private void listen(Socket clientSocket) throws IOException, ExecutionException, InterruptedException {
        EXECUTOR_SERVICE.submit(() -> {
            try (clientSocket) {
                //* STEP 1: handle request
                HttpRequest request = this.handleRequest(clientSocket);

                //* STEP 2: handle response based on request
                HttpResponse response = this.handleResponse(request);

                //* STEP 3: send response
                this.sendResponse(clientSocket, response);
            } catch (IOException | URISyntaxException | ParseException | IllegalAccessException e) {
                this.stop();
                throw new RuntimeException(e);
            }
        });
    }

    private HttpRequest handleRequest(Socket clientSocket) throws IOException, URISyntaxException, ParseException, IllegalAccessException {

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
        // ? ce va face controllerul??
        // ? care e rolul acestuia? sa faca legatura dintre server si baza de date
        // ? cum o va face ?
        if (!bodyBuilder.isEmpty()) {
            Class<?> clazz = this.findEntityClass(ENTITY_MANAGER, JSON_PARSER.getProperties(bodyBuilder.toString()));
            Object obj = JSON_PARSER.map(bodyBuilder.toString(), clazz);
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

    private Class<?> findEntityClass(EntityManager entityManager, Map<String, Object> objectMap) throws NoEntityMatchesJson {

        return entityManager.getEntities()
                .values()
                .stream()
                .filter(entity -> Arrays.stream(entity.getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.toSet())
                        .equals(objectMap.keySet())
                )
                .findAny()
                .orElseThrow(NoEntityMatchesJson::new);
    }


    //TODO CREATE THE RESPONSE

    private HttpResponse handleResponse(HttpRequest httpRequest) {

        HttpRequestStartLine startLine = httpRequest.getStartLine();

        return HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                HttpConnectionType.CLOSED,
                "RESPONSE TEST"
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
