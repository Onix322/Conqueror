import org.httpServer.HttpServer;
import org.httpServer.HttpServerImpl;
import org.configuration.Configuration;
import org.configuration.ConfigurationImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) {
        System.out.println("Starting app...");

        //HTTP SERVER Configuration
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //HTTP SERVER CREATE
        HttpServerImpl.create(configuration, executorService);

        HttpServer httpServer = HttpServerImpl.getInstance();

        // HTTP SERVER START

        try{
            httpServer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
