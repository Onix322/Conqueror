package org.server.configuration;

import org.server.exepltions.ConfigPropertyNonExistent;

import java.io.IOException;
import java.util.Properties;

public sealed interface Configuration permits ConfigurationImpl {

    Properties readAll() throws IOException;

    String readProperty(String name) throws ConfigPropertyNonExistent;
}
