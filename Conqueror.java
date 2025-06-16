import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.logger.Logger;
import org.server.managers.entityManager.EntityManager;
import org.server.httpServer.HttpServer;
import org.server.processors.context.ApplicationContext;

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

        //* ApplicationContext Initialization
        ApplicationContext applicationContext = new ApplicationContext(configuration, executorService);
        applicationContext.applicationContextInit();

        //* Entities autoload
        applicationContext.requestInstance(EntityManager.class)
                        .autoload();

        //* Server start
        applicationContext.requestInstance(HttpServer.class)
                .start();
    }
}
