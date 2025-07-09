package src.com.config.mysql.utils.schemaHandler.schemaMode;

import src.com.server.exceptions.SchemaModeNotSupported;

import java.util.Arrays;

public enum SchemaMode {
    NONE("none"), // does NOTHING
    UPDATE("update"), // ONLY Modify or update
    CREATE("create"); // CREATE / RECREATE all entities

    private final String MODE;

    SchemaMode(String mode) {
        this.MODE = mode;
    }

    public static SchemaMode getMode(String mode){
        return Arrays.stream(SchemaMode.values()).filter(sm -> sm.MODE.equals(mode))
                .findAny()
                .orElseThrow(() -> new SchemaModeNotSupported("Schema mode not supported: " + mode));
    }
}
