package org.httpServer;

import utils.configuration.Configuration;

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

    public static class HttpServerInit {
        private static HttpServerImpl instance;

        public synchronized static void create(Configuration configuration, ExecutorService executorService) {
            if (instance == null) {
                System.out.println("Setting up server configuration...");
                instance = new HttpServerImpl(configuration, executorService);
            }
        }

        public static HttpServerImpl getInstance() {
            if (instance == null) {
                throw new IllegalStateException("Server not initialized!");
            }
            return instance;
        }
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
        String line= in.readLine();

        while(!line.isEmpty()){
            System.out.println(line);
            line = in.readLine();
        }

        System.out.println("Got request!");
        clientSocket.shutdownInput();
    }

    private void sendResponse(Socket clientSocket) throws IOException {

        String response = """
                HTTP/1.1 200 OK\r\n
                Content-Type: application/json
                Connection: Closed\r\n
                {
                    "status": 200
                }
                """;

        clientSocket.getOutputStream()
                .write(response.getBytes(StandardCharsets.UTF_8));

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
