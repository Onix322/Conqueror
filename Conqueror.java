import org.app.entity.TestObject;
import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.entityManager.EntityManager;
import org.server.httpServer.HttpServer;
import org.server.processors.components.ContextProcessor;

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
        ContextProcessor contextProcessor = new ContextProcessor(configuration, executorService);
        contextProcessor.applicationContextInit();

        contextProcessor.requestInstance(EntityManager.class)
                .registerEntityClass(TestObject.class);

        //* Server start
        contextProcessor.requestInstance(HttpServer.class)
                .start();
    }
}
