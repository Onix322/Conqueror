import org.utils.controller.ControllerManager;
import org.utils.controller.ControllerManagerImpl;
import org.utils.httpServer.HttpServer;
import org.utils.httpServer.HttpServerImpl;
import org.utils.configuration.Configuration;
import org.utils.configuration.ConfigurationImpl;
import org.utils.jsonService.JsonService;
import org.utils.jsonService.JsonServiceImpl;
import org.utils.jsonService.json.mapper.JsonMapper;
import org.utils.jsonService.json.mapper.JsonPrimitiveParser;
import org.utils.jsonService.json.mapper.ObjectMapper;
import org.utils.jsonService.json.parser.JsonParser;
import org.utils.jsonService.json.validator.JsonValidator;
import org.utils.jsonService.json.formatter.JsonFormat;
import org.entities.TestObject;
import org.utils.entityManager.EntityManager;
import org.utils.entityManager.EntityManagerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) {
        System.out.println("Starting app...");

        //*HTTP SERVER Configuration
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //*JsonParser Initialization
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
        JsonService JsonService = JsonServiceImpl.getInstance();

        //*EntityManager Initialization, Entities registering in Database
        EntityManager entityManager = EntityManagerImpl.getInstance();
        entityManager.registerEntityClass(TestObject.class);

        //*ControllerManager Initialization
        ControllerManager controllerManager = ControllerManagerImpl.getInstance();

        //*HTTP SERVER CREATE
        //TODO - annotations for Controllers
        //TODO - annotations for HttpMethods
        HttpServerImpl.init(configuration, executorService, JsonService, entityManager, controllerManager);
        HttpServer httpServer = HttpServerImpl.getInstance();

        //*HTTP SERVER START
        httpServer.start();

    }
}
