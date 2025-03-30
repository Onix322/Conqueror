package utils.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {

    private static final Properties PROPERTIES = new Properties();

    private ConfigurationImpl() {
        this.loadProperties();
    }

    private static class SingletonHelper {
        public static final ConfigurationImpl instance = new ConfigurationImpl();
    }

    public static ConfigurationImpl getInstance() {
        return SingletonHelper.instance;
    }

    @Override
    public Properties readAll() {
        return PROPERTIES;
    }

    @Override
    public String readProperty(String name) {
        return PROPERTIES.getProperty(name);
    }

    public void refreshProperties() {
        this.loadProperties();
    }

    private void loadProperties() {
        try {
            PROPERTIES.load(this.getStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private InputStream getStream() {
        return ClassLoader.getSystemResourceAsStream("./config.properties");
    }

    @Override
    public String toString(){
        return "Configuration" + PROPERTIES.toString();
    }
}
