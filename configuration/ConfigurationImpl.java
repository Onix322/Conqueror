package configuration;

import src.com.server.exceptions.ConfigPropertyNonExistent;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationImpl implements Configuration {

    private static final Properties PROPERTIES = new Properties();

    private ConfigurationImpl() {
        this.loadProperties();
    }

    private static class Init {
        private static ConfigurationImpl INSTANCE = null;
    }

    public static synchronized void init() {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new ConfigurationImpl();
        }
    }

    public static ConfigurationImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("ConfigurationImpl not initialized. Use ConfigurationImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public Properties readAll() {
        return PROPERTIES;
    }

    @Override
    public String readProperty(String name) throws ConfigPropertyNonExistent {
        String property = PROPERTIES.getProperty(name);
        if (property == null || property.isEmpty()) {
            throw new ConfigPropertyNonExistent(name);
        }
        return property;
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

    private InputStream getStream() throws IOError {
        InputStream stream = ClassLoader.getSystemResourceAsStream("./config.properties");

        if (stream == null) {
            throw new IOError(new Throwable("File 'config.properties' is not created. Please create 'config.properties'!"));
        }

        return stream;
    }

    @Override
    public String toString() {
        return "Configuration" + PROPERTIES.toString();
    }
}
