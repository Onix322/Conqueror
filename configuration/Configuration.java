package configuration;

import src.com.server.exceptions.ConfigPropertyNonExistent;

import java.io.IOException;
import java.util.Properties;

public sealed interface Configuration permits ConfigurationImpl {

    Properties readAll() throws IOException;

    String readProperty(String name) throws ConfigPropertyNonExistent;
}
