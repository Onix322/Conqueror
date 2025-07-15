package framework.src.server.exceptions;

public class ConfigPropertyNonExistent extends RuntimeException {
    public ConfigPropertyNonExistent(String propertyName) {
        super("This property: '" + propertyName + "' doesn't exits!");
    }
}
