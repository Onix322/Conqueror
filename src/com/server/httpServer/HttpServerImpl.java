package src.com.server.httpServer;

import src.com.server.annotations.component.Component;
import configuration.Configuration;
import src.com.server.handlers.RouteHandler;
import src.com.server.handlers.TransformationHandler;
import src.com.server.managers.ExceptionManager;
import src.com.server.httpServer.utils.httpMethod.BodyRequirement;
import src.com.server.httpServer.utils.request.httpRequest.HttpRequest;
import src.com.server.httpServer.utils.response.HttpConnectionType;
import src.com.server.httpServer.utils.response.HttpStatus;
import src.com.server.httpServer.utils.response.httpResponse.HttpResponse;
import src.com.server.httpServer.utils.response.httpResponse.HttpResponseFactory;
import src.com.server.httpServer.utils.responseEntity.ResponseFailed;
import src.com.server.logger.Logger;
import src.com.server.metadata.RouteMetaData;
import src.com.server.parsers.json.JsonService;
import src.com.server.parsers.json.utils.types.JsonType;
import src.com.server.processors.route.RouteProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * HttpServerImpl is the implementation of the HttpServer interface.
 * It handles incoming HTTP requests, processes them, and sends back responses.
 * The server runs on a specified port and listens for incoming connections.
 */
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

    /**
     * Returns the configuration of the server.
     * This method is used to retrieve the server's configuration properties.
     *
     * @return Configuration object containing server properties
     */
    @Override
    public Configuration getConfig() {
        return CONFIGURATION;
    }

    /**
     * Starts the HTTP server.
     * It binds to the specified port and listens for incoming connections.
     * When a client connects, it processes the request in a separate thread.
     */
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

    /**
     * Stops the HTTP server.
     * This method closes the executor service, which stops processing any further requests.
     */
    @Override
    public void stop() {
        EXECUTOR_SERVICE.close();
    }

    /**
     * Listens for incoming client connections and processes requests.
     * This method is executed in a separate thread for each client connection.
     *
     * @param clientSocket the socket representing the client connection
     */
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

    /**
     * Handles the casting of the HTTP request body based on the requirements specified in the RouteMetaData.
     * It checks if the body is required or optional and processes it accordingly.
     *
     * @param routeMetaData RouteMetaData containing method metadata for casting
     * @param request HttpRequest to be processed
     * @return HttpRequest with the body cast to the appropriate type
     * @throws Exception if there is an error during casting
     */
    private HttpRequest handleCasting(RouteMetaData routeMetaData, HttpRequest request) throws Exception {
        if (!request.getStartLine().getMethod().hasBody().equals(BodyRequirement.FORBIDDEN)) {
            return this.TRANSFORMATION_HANDLER.handleCasting(routeMetaData, request);
        }
        return request;
    }

    /**
     * Handles the incoming HTTP request from the client socket.
     * It reads the request data, parses it, and returns an HttpRequest object.
     *
     * @param clientSocket the socket representing the client connection
     * @return HttpRequest object containing the parsed request data
     * @throws Exception if there is an error during request handling
     */
    private HttpRequest handleRequest(Socket clientSocket) throws Exception {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        HttpRequest httpRequest = this.TRANSFORMATION_HANDLER.handleTransformation(in);
        clientSocket.shutdownInput();

        return httpRequest;
    }

    /**
     * Handles the response for the given route and request.
     * It processes the request using the RouteHandler and returns an HttpResponse object.
     *
     * @param route the RouteMetaData containing information about the route
     * @param request the HttpRequest to be processed
     * @return HttpResponse object containing the response data
     * @throws Exception if there is an error during response handling
     */
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

    /**
     * Handles errors that occur during request processing.
     * It maps the exception to an appropriate HTTP status and creates a ResponseFailed object.
     *
     * @param e the exception that occurred
     * @return HttpResponse object containing the error response
     */
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

    /**
     * Sends the HTTP response back to the client.
     * It writes the response data to the client socket's output stream.
     *
     * @param clientSocket the socket representing the client connection
     * @param httpResponse the HttpResponse to be sent
     */
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
