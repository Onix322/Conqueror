package org.server.httpServer;

import org.server.configuration.Configuration;
import org.server.controllerManager.ControllerManager;
import org.server.httpServer.route.PathVariable;
import org.server.metadata.ControllerMetaData;
import org.server.metadata.MethodMetaData;
import org.server.entityManager.EntityManager;
import org.server.httpServer.request.transformationHandler.TransformationHandler;
import org.server.httpServer.request.httpRequest.HttpRequest;
import org.server.httpServer.response.HttpConnectionType;
import org.server.httpServer.response.HttpStatus;
import org.server.httpServer.response.httpResponse.HttpResponse;
import org.server.httpServer.response.httpResponse.HttpResponseFactory;
import org.server.httpServer.route.RouteHandler;
import org.server.jsonService.JsonService;
import org.server.metadata.RouteMetaData;
import org.server.primitiveParser.PrimitiveParser;
import org.server.processors.RouteProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class HttpServerImpl implements HttpServer {

    private final Configuration CONFIGURATION;
    private final ExecutorService EXECUTOR_SERVICE;
    private final EntityManager ENTITY_MANAGER;
    private final ControllerManager CONTROLLER_MANAGER;
    private final TransformationHandler TRANSFORMATION_HANDLER;
    private final RouteHandler ROUTE_HANDLER;
    private final PrimitiveParser PRIMITIVE_PARSER;
    private final JsonService JSON_SERVICE;
    private final RouteProcessor ROUTE_PROCESSOR;

    private HttpServerImpl(Configuration configuration, ExecutorService executorService, EntityManager entityManager, ControllerManager controllerManager, TransformationHandler transformationHandler, RouteHandler routeHandler, PrimitiveParser primitiveParser, JsonService jsonService, RouteProcessor routeProcessor) {
        this.CONFIGURATION = configuration;
        this.EXECUTOR_SERVICE = executorService;
        this.ENTITY_MANAGER = entityManager;
        this.CONTROLLER_MANAGER = controllerManager;
        this.TRANSFORMATION_HANDLER = transformationHandler;
        this.ROUTE_HANDLER = routeHandler;
        this.PRIMITIVE_PARSER = primitiveParser;
        this.JSON_SERVICE = jsonService;
        this.ROUTE_PROCESSOR = routeProcessor;
    }

    private static class Init {
        private static HttpServerImpl instance;
    }

    public synchronized static void init(Configuration configuration, ExecutorService executorService, EntityManager entityManager, ControllerManager controllerManager, TransformationHandler transformationHandler, RouteHandler routeHandler, PrimitiveParser primitiveParser, JsonService jsonService, RouteProcessor routeProcessor) {
        if (Init.instance == null) {
            System.out.println("Setting up server configuration...");
            Init.instance = new HttpServerImpl(configuration, executorService, entityManager, controllerManager, transformationHandler, routeHandler, primitiveParser, jsonService, routeProcessor);
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
                RouteMetaData routeMetaData = this.ROUTE_PROCESSOR.process(request);
                System.out.println(routeMetaData);
                //* STEP 2: handle response based on request
                HttpResponse response = this.handleResponse(routeMetaData);

                //* STEP 3: send response
                this.sendResponse(clientSocket, response);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getLocalizedMessage());
            }
        });
    }

    private HttpRequest handleRequest(Socket clientSocket) throws Exception {

        //*creating HttpRequest
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest httpRequest = this.TRANSFORMATION_HANDLER.transformToHttpRequest(in);
        clientSocket.shutdownInput();

        return httpRequest;
    }

    private HttpResponse handleResponse(RouteMetaData route) throws Exception {
        ControllerMetaData controllerMetadata = route.getControllerMetaData();
        MethodMetaData methodMetadata = route.getMethodMetaData();
        Object responseBody;

        if(route.getPathVariables().length > 0){

            List<Object> vars = Arrays.stream(route.getPathVariables())
                    .map(PathVariable::value)
                    .toList();

            responseBody = controllerMetadata.getClassOf()
                    .getMethod(methodMetadata.getName(), methodMetadata.getParameters())
                    .invoke(controllerMetadata.getClassOf(), vars.toArray(new Object[0]));
        } else {
            responseBody = controllerMetadata.getClassOf()
                    .getMethod(methodMetadata.getName())
                    .invoke(controllerMetadata.getClassOf());
        }

        return HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                HttpConnectionType.CLOSED,
                this.JSON_SERVICE.mapJson(responseBody)
        );
    }

    private void sendResponse(Socket clientSocket, HttpResponse httpResponse) throws Exception {
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
