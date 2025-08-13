package configuration;

import framework.src.server.exceptions.ConfigPropertyNonExistent;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Path configPath = Path.of("configuration" + File.separator + "config.properties");

        try{
            InputStream in = configPath.toUri()
                    .toURL()
                    .openStream();

            if(in == null){
                Files.createDirectories(Path.of("configuration"));
                Files.createFile(Path.of("configuration/config.properties"));
            }

            return in;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Configuration" + PROPERTIES.toString();
    }
}
