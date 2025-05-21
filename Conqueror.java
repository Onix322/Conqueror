import org.app.controller.TestObjectController;
import org.app.entity.TestObject;
import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.controllerManager.ControllerManager;
import org.server.entityManager.EntityManager;
import org.server.httpServer.HttpServer;
import org.server.processors.components.ComponentProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting app...");

        //* Server Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //* SingletonProcessor Initialization
        ComponentProcessor componentProcessor = new ComponentProcessor(configuration, executorService);
        componentProcessor.applicationContextInit();

        componentProcessor.requestInstance(EntityManager.class)
                .registerEntityClass(TestObject.class);
        componentProcessor.requestInstance(ControllerManager.class)
                .registerController(TestObjectController.class);

        //* Server start
        componentProcessor.requestInstance(HttpServer.class)
                .start();
    }
}
