package utils.configuration;

import java.io.IOException;
import java.util.Properties;

public interface Configuration {

    Properties readAll() throws IOException;

    String readProperty(String name);
}
