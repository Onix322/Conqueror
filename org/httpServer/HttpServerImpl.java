package org.httpServer;

import org.configuration.Configuration;
import org.httpServer.request.httpRequest.HttpRequest;
import org.httpServer.request.httpRequestHeader.HttpRequestHeader;
import org.httpServer.request.httpRequestHeader.HttpRequestHeaderFactory;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLine;
import org.httpServer.request.httpRequestStartLine.HttpRequestStartLineFactory;
import org.httpServer.response.HttpConnectionType;
import org.httpServer.response.HttpStatus;
import org.httpServer.response.httpResponse.HttpResponse;
import org.httpServer.response.httpResponse.HttpResponseFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class HttpServerImpl implements HttpServer {

    private final Configuration CONFIGURATION;
    private final ExecutorService EXECUTOR_SERVICE;

    private HttpServerImpl(Configuration configuration, ExecutorService executorService) {
        this.CONFIGURATION = configuration;
        this.EXECUTOR_SERVICE = executorService;
    }

    private static class Init {
        private static HttpServerImpl instance;
    }

    public synchronized static void create(Configuration configuration, ExecutorService executorService) {
        if (Init.instance == null) {
            System.out.println("Setting up server configuration...");
            Init.instance = new HttpServerImpl(configuration, executorService);
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
        EXECUTOR_SERVICE.submit(() -> {
            try {
                this.handleRequest(clientSocket);
                this.sendResponse(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleRequest(Socket clientSocket) throws IOException {

        HttpRequestStartLine startLine;
        List<HttpRequestHeader> headers = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        //read headers
        String line = in.readLine();

        while (line != null && !line.isEmpty()) {
            if (Pattern.compile("^(GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH|TRACE)\\s/\\S*\\sHTTP/\\d\\.\\d$").matcher(line).find()) {
                startLine = HttpRequestStartLineFactory.create(line);
            }

            if (Pattern.compile("\\w+:\\s").matcher(line).find()) {
                headers.add(HttpRequestHeaderFactory.create(line));
            }
            System.out.println(line);
            line = in.readLine();
        }

        int b = in.read();

        while (b > -1 && in.ready()){
            System.out.println(b);
            b = in.read();
            line = in.readLine();
            System.out.println(line);
        }


        clientSocket.close();
        clientSocket.shutdownInput();
        //TODO return HttpRequest
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
