package src.com.server.environment;

import configuration.Configuration;

public enum Environment {
    PROD("prod"),
    DEBUG("debug"),
    DEV("dev");

    private final String env;

    Environment(String env) {
        this.env = env;
    }

    public String getEnv() {
        return this.env;
    }

    public static Environment getConfigEnv(Configuration configuration) {
        String property = configuration.readProperty("app.env");
        for (Environment e : values()) {
            if (e.getEnv().equals(property)) {
                return e;
            }
        }
        return null;
    }
}
