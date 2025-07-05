import loader.Loader;
import src.com.App;
import src.com.server.configuration.Configuration;
import src.com.server.configuration.ConfigurationImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Conqueror {

    public static void main(String[] args) throws Exception {

        //* Server Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        //* Start App
        Loader.load(configuration, executorService, args);
        App.start(configuration, executorService);


    }

}
