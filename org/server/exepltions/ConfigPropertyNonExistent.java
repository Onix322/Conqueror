package org.server.exepltions;

public class ConfigPropertyNonExistent extends RuntimeException {
    public ConfigPropertyNonExistent(String propertyName) {
        super("This property: '" + propertyName + "' doesn't exits!");
    }
}
