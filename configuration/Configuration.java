package configuration;

import framework.src.server.exceptions.ConfigPropertyNonExistent;

import java.io.IOException;
import java.util.Properties;

/**
 * Interface for reading configuration properties.
 * Implementations must provide methods to read all properties or a specific property by name.
 */
public sealed interface Configuration permits ConfigurationImpl {

    /**
     * Reads all configuration properties.
     *
     * @return a {@link Properties} object containing all configuration properties
     * @throws IOException if an I/O error occurs while reading properties
     */
    Properties readAll() throws IOException;

    /**
     * Reads the value of a specific configuration property.
     *
     * @param name the name of the property to read
     * @return the value of the specified property
     * @throws ConfigPropertyNonExistent if the property does not exist
     */
    String readProperty(String name) throws ConfigPropertyNonExistent;
}
