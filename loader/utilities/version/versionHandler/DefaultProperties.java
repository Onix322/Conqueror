package loader.utilities.version.versionHandler;

import java.util.Map;
import java.util.Properties;

public class DefaultProperties {
    private static Map<String, String> properties = Map.ofEntries(
            Map.entry("java.specification.version", System.getProperty("java.specification.version"))
    );

    public static String get(String key){
        return properties.getOrDefault(key, System.getProperty(key, null));
    }

    public static void add(String key, String value){
        properties.put(key, value);
    }
    public static void addAll(Properties prop){
        prop.forEach((k, v) -> properties.put((String) k, (String) v));
    }
    public static void addAll(Map<String, String> prop){
        properties.putAll(prop);
    }
    public static Map<String, String> getProperties() {
        return Map.copyOf(properties);
    }
}
