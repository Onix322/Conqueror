package org.httpServer;

import org.httpServer.response.httpResponse.HttpResponse;
import org.httpServer.response.httpResponse.HttpResponseFactory;
import org.httpServer.response.HttpStatus;
import org.configuration.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

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

    public static HttpServerImpl getInstance(){
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

    private void listen(Socket clientSocket) {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                this.receiveRequest(clientSocket);
                this.sendResponse(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void receiveRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = in.readLine();

        while (!line.isEmpty()) {
            System.out.println(line);
            line = in.readLine();
        }

        System.out.println("Got request!");
        clientSocket.shutdownInput();
    }

    private void sendResponse(Socket clientSocket) throws IOException {

        HttpResponse<String> httpResponse = HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                "body"
        );

        System.out.println("Line 102 HttpServerImpl: \n" + httpResponse.getResponseString());

        clientSocket.getOutputStream()
                .write(httpResponse.getResponseString().getBytes(StandardCharsets.UTF_8));

        clientSocket.shutdownOutput();
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    @Override
    public String toString() {
        return "HttpServerImpl{" + CONFIGURATION + '}';
    }
}
