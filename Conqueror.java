import org.controller.ControllerManager;
import org.controller.ControllerManagerImpl;
import org.httpServer.HttpServer;
import org.httpServer.HttpServerImpl;
import org.configuration.Configuration;
import org.configuration.ConfigurationImpl;
import org.services.jsonService.JsonService;
import org.services.jsonService.JsonServiceImpl;
import org.services.jsonService.json.mapper.JsonMapper;
import org.services.jsonService.json.mapper.JsonPrimitiveParser;
import org.services.jsonService.json.mapper.ObjectMapper;
import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.validator.JsonValidator;
import org.services.jsonService.json.formatter.JsonFormat;
import utils.entities.TestObject;
import org.entityManager.EntityManager;
import org.entityManager.EntityManagerImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) {
        System.out.println("Starting app...");

        //*HTTP SERVER Configuration
        //Stocheaza informatii, setari, variabile importante ale configuratiei
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //*JsonParser Initialization
        //Asigura mappare de obiecte din json si invers
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

        //*EntityManager Initialization, Entities registering in DataBase

        EntityManager entityManager = EntityManagerImpl.getInstance();
        entityManager.registerEntityClass(TestObject.class);

        //*ControllerManager Initialization
        //Tine evitdenta tuturor controlarelor create
        ControllerManager controllerManager = ControllerManagerImpl.getInstance();



        //*HTTP SERVER CREATE
        //TODO - annotations for Controllers
        //TODO - annotations for HttpMethods
        HttpServerImpl.create(configuration, executorService, JsonService, entityManager, controllerManager);
        HttpServer httpServer = HttpServerImpl.getInstance();

        //*HTTP SERVER START
        httpServer.start();

    }
}
