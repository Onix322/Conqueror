package src.com;

import src.com.server.configuration.Configuration;
import src.com.server.httpServer.HttpServer;
import src.com.server.managers.entityManager.EntityManager;
import src.com.server.processors.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class App {

    public static void start(Configuration configuration) throws Exception {
        System.out.println("Starting app...");

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
