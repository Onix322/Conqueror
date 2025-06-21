import src.com.App;
import src.com.server.configuration.Configuration;
import src.com.server.configuration.ConfigurationImpl;
import loader.Loader;

public class Conqueror {

    public static void main(String[] args) throws Exception {

        //* Server Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        //* Start App
        Loader.load(configuration, args);
        App.start(configuration);

    }
}
