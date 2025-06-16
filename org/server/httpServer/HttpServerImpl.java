package org.server.httpServer;

import org.server.annotations.component.Component;
import org.server.configuration.Configuration;
import org.server.handlers.RouteHandler;
import org.server.handlers.TransformationHandler;
import org.server.managers.ExceptionManager;
import org.server.httpServer.utils.httpMethod.BodyRequirement;
import org.server.httpServer.utils.request.httpRequest.HttpRequest;
import org.server.httpServer.utils.response.HttpConnectionType;
import org.server.httpServer.utils.response.HttpStatus;
import org.server.httpServer.utils.response.httpResponse.HttpResponse;
import org.server.httpServer.utils.response.httpResponse.HttpResponseFactory;
import org.server.httpServer.utils.responseEntity.ResponseFailed;
import org.server.logger.Logger;
import org.server.metadata.RouteMetaData;
import org.server.parsers.json.JsonService;
import org.server.parsers.json.utils.types.JsonType;
import org.server.processors.route.RouteProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

@Component
public final class HttpServerImpl implements HttpServer {

    private final Configuration CONFIGURATION;
    private final ExecutorService EXECUTOR_SERVICE;
    private final TransformationHandler TRANSFORMATION_HANDLER;
    private final RouteHandler ROUTE_HANDLER;
    private final JsonService JSON_SERVICE;
    private final RouteProcessor ROUTE_PROCESSOR;
    private final ExceptionManager EXCEPTION_MAPPER;

    private HttpServerImpl(Configuration configuration,
                           ExecutorService executorService,
                           TransformationHandler transformationHandler,
                           RouteHandler routeHandler,
                           JsonService jsonService,
                           RouteProcessor routeProcessor,
                           ExceptionManager exceptionManager
    ) {
        this.CONFIGURATION = configuration;
        this.EXECUTOR_SERVICE = executorService;
        this.TRANSFORMATION_HANDLER = transformationHandler;
        this.ROUTE_HANDLER = routeHandler;
        this.JSON_SERVICE = jsonService;
        this.ROUTE_PROCESSOR = routeProcessor;
        this.EXCEPTION_MAPPER = exceptionManager;
    }

    @Override
    public Configuration getConfig() {
        return CONFIGURATION;
    }

    @Override
    public void start() {
        Logger.log(this.getClass(), "Starting server...");
        int port = (Integer.parseInt(getConfig().readProperty("server.port")));
        String address = getConfig().readProperty("server.hostname");

        InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);

        try (ServerSocket serverSocket = new ServerSocket(inetSocketAddress.getPort(), 0, inetSocketAddress.getAddress())) {
            Logger.log(this.getClass(), "Server is running!");
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                this.listen(clientSocket);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    private void listen(Socket clientSocket) {

        EXECUTOR_SERVICE.submit(() -> {
            try {
                //* STEP 1: handle request
                HttpRequest request = this.handleRequest(clientSocket);
                RouteMetaData routeMetaData = this.ROUTE_PROCESSOR.process(request);

                //* STEP 2: handle casting of HttpRequestBody from JSON to POJO
                HttpRequest requestCasted = this.handleCasting(routeMetaData, request);

                //* STEP 3: handle response based on request
                HttpResponse response = this.handleResponse(routeMetaData, requestCasted);

                //* STEP 4: send response
                this.sendResponse(clientSocket, response);
            } catch (Exception e) {
                HttpResponse err = this.handleError(e);
                this.sendResponse(clientSocket, err);
            }
        });
    }

    private HttpRequest handleCasting(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        if (!request.getStartLine().getMethod().hasBody().equals(BodyRequirement.FORBIDDEN)) {
            return this.TRANSFORMATION_HANDLER.handleCasting(routeMetaData, request);
        }
        return request;
    }

    private HttpRequest handleRequest(Socket clientSocket) throws Exception {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest httpRequest = this.TRANSFORMATION_HANDLER.handleTransformation(in);
        clientSocket.shutdownInput();

        return httpRequest;
    }

    private HttpResponse handleResponse(RouteMetaData route, HttpRequest request) throws Exception {
        Object responseBody = this.ROUTE_HANDLER.handleRoute(route, request);
        JsonType jsonResponse = null;
        if (responseBody != null) {
            jsonResponse = this.JSON_SERVICE.mapJson(responseBody);
        }

        return HttpResponseFactory.create(
                "HTTP/1.1",
                HttpStatus.OK,
                "application/json",
                HttpConnectionType.KEEP_ALIVE,
                jsonResponse
        );
    }

    private HttpResponse handleError(Exception e) {
        HttpStatus httpStatus = this.EXCEPTION_MAPPER.mapException(e);
        ResponseFailed responseFailed = new ResponseFailed(httpStatus, e.getCause().getLocalizedMessage());
        try {
            return HttpResponseFactory.create(
                    "HTTP/1.1",
                    httpStatus,
                    "application/json",
                    HttpConnectionType.CLOSED,
                    this.JSON_SERVICE.mapJson(responseFailed)
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendResponse(Socket clientSocket, HttpResponse httpResponse) {
        try {
            clientSocket.getOutputStream()
                    .write(httpResponse.getResponseString()
                            .getBytes(StandardCharsets.UTF_8)
                    );
            clientSocket.shutdownOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "HttpServerImpl{" + CONFIGURATION + '}';
    }
}
