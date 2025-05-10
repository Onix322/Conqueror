import org.mvc.controller.TestObjectController;
import org.server.controllerManager.ControllerManager;
import org.server.controllerManager.ControllerManagerImpl;
import org.server.httpServer.HttpServer;
import org.server.httpServer.HttpServerImpl;
import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.httpServer.request.transformationHandler.TransformationHandler;
import org.server.httpServer.route.RouteHandler;
import org.server.jsonService.JsonService;
import org.server.jsonService.JsonServiceImpl;
import org.server.jsonService.json.mapper.JsonMapper;
import org.server.jsonService.json.mapper.JsonPrimitiveParser;
import org.server.jsonService.json.mapper.ObjectMapper;
import org.server.jsonService.json.parser.JsonParser;
import org.server.jsonService.json.validator.JsonValidator;
import org.server.jsonService.json.formatter.JsonFormat;
import org.mvc.entities.TestObject;
import org.server.entityManager.EntityManager;
import org.server.entityManager.EntityManagerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting app...");

        //*HTTP SERVER Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //*JsonService Initialization
        JsonPrimitiveParser.init();
        JsonMapper.init(
                JsonPrimitiveParser.getInstance()
        );
        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance(),
                JsonMapper.getInstance(),
                JsonPrimitiveParser.getInstance()
        );
        JsonServiceImpl.init(JsonParser.getInstance());
        JsonService jsonService = JsonServiceImpl.getInstance();

        //*EntityManager Initialization, Entities registering in Database
        EntityManagerImpl.init();
        EntityManager entityManager = EntityManagerImpl.getInstance();
        entityManager.registerEntityClass(TestObject.class);

        //*ControllerManager Initialization
        ControllerManagerImpl.init();
        ControllerManager controllerManager = ControllerManagerImpl.getInstance();
        controllerManager.registerController(TestObjectController.class);

        //*TransformationHandler Initialization
        TransformationHandler.init(
                jsonService,
                entityManager
        );
        TransformationHandler transformationHandler = TransformationHandler.getInstance();

        //*RouteHandler Initialization
        RouteHandler.init();
        RouteHandler routeHandler = RouteHandler.getInstance();

        //*HTTP SERVER CREATE
        //TODO - annotations for Controllers
        //TODO - annotations for HttpMethods
        HttpServerImpl.init(
                configuration,
                executorService,
                entityManager,
                controllerManager,
                transformationHandler,
                routeHandler
        );
        HttpServer httpServer = HttpServerImpl.getInstance();

        //*HTTP SERVER START
        httpServer.start();

    }
}
