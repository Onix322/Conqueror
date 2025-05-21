import org.mvc.controller.TestObjectController;
import org.mvc.entities.TestObject;
import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.controllerManager.ControllerManager;
import org.server.entityManager.EntityManager;
import org.server.httpServer.HttpServerImpl;
import org.server.processors.SingletonProcessor;

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
        SingletonProcessor singletonProcessor = new SingletonProcessor(configuration);
        singletonProcessor.force(executorService.getClass(), executorService);
        singletonProcessor.applicationContextInit();

        singletonProcessor.requestInstance(EntityManager.class).registerEntityClass(TestObject.class);
        singletonProcessor.requestInstance(ControllerManager.class).registerController(TestObjectController.class);

        //* Server start
        singletonProcessor.requestInstance(HttpServerImpl.class).start();
    }
}
