package org.httpServer;

import org.configuration.Configuration;
import org.entities.TestObject;
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
import utils.json.parser.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class HttpServerImpl implements HttpServer {

    private final Configuration CONFIGURATION;
    private final ExecutorService EXECUTOR_SERVICE;
    private final JsonParser JSON_PARSER;

    private HttpServerImpl(Configuration configuration, ExecutorService executorService, JsonParser jsonParser) {
        this.CONFIGURATION = configuration;
        this.EXECUTOR_SERVICE = executorService;
        this.JSON_PARSER = jsonParser;
    }

    private static class Init {
        private static HttpServerImpl instance;
    }

    public synchronized static void create(Configuration configuration, ExecutorService executorService, JsonParser jsonParser) {
        if (Init.instance == null) {
            System.out.println("Setting up server configuration...");
            Init.instance = new HttpServerImpl(configuration, executorService, jsonParser);
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
        } catch (IOException e) {
            this.stop();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    private void listen(Socket clientSocket) throws IOException {

        AtomicReference<HttpRequest> httpRequest = new AtomicReference<>();

        EXECUTOR_SERVICE.submit(() -> {
            try {
                httpRequest.set(this.handleRequest(clientSocket));
                this.sendResponse(clientSocket);
                clientSocket.close();
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private HttpRequest handleRequest(Socket clientSocket) throws IOException, URISyntaxException {

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

        if(!bodyBuilder.isEmpty()){
            System.out.println(bodyBuilder);

            //TODO fa o lista cu toate entitatile si sa o ia pe cea care se potriveste!!!!!
            body.setBody(JSON_PARSER.map(bodyBuilder.toString(), TestObject.class));
        }

        clientSocket.shutdownInput();

        return HttpRequest.builder()
                .setHttpRequestHeader(headers)
                .setStartLine(startLine)
                .setHttpRequestBody(body)
                .build();
    }

    private void sendResponse(Socket clientSocket) throws IOException {

        //TODO CREATE THE RESPONSE

        HttpResponse<String> httpResponse = HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                HttpConnectionType.CLOSED,
                "RESPONSE TEST"
        );

//        System.out.println("Line 102 HttpServerImpl: \n" + httpResponse.getResponseString());

        clientSocket.getOutputStream()
                .write(httpResponse.getResponseString().getBytes(StandardCharsets.UTF_8));

        clientSocket.shutdownOutput();
    }

    @Override
    public String toString() {
        return "HttpServerImpl{" + CONFIGURATION + '}';
    }
}
