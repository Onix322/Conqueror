import org.controller.ControllerManager;
import org.controller.ControllerManagerImpl;
import org.httpServer.HttpServer;
import org.httpServer.HttpServerImpl;
import org.configuration.Configuration;
import org.configuration.ConfigurationImpl;
import utils.entities.TestObject;
import org.entityManager.EntityManager;
import org.entityManager.EntityManagerImpl;
import org.json.parser.JsonService;
import org.json.parser.JsonServiceImpl;

import java.text.NumberFormat;
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
        //TODO - support for arrays, objects and different json types
        //TODO - annotations for classes
        //TODO - annotations for fields
        JsonServiceImpl.init(NumberFormat.getInstance());
        JsonService JsonService = JsonServiceImpl.getInstance();

        //*EntityManager Initialization
        //Poate initializa clase in baza de date automat
        EntityManager entityManager = EntityManagerImpl.getInstance();
        entityManager.registerEntityClass("testObject", TestObject.class);

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
