package src.com.server.database.mysql.utils.schemaHandler.schemaMode;

import src.com.server.configuration.Configuration;
import src.com.server.annotations.component.Component;

/*
* Takes "database.schema-mode" value from configuration
* Translates the value in a java constant (SchemaMode enum)
* and stores the SchemaMode constant.
* */
@Component
public class CurrentSchemaMode {

    private final SchemaMode SCHEMA_MODE;

    private CurrentSchemaMode(Configuration configuration){
        this.SCHEMA_MODE = this.translate(configuration.readProperty("database.schema-mode"));
    }

    public SchemaMode getCurrentSchemaMode(){
        return this.SCHEMA_MODE;
    }

    private SchemaMode translate(String mode){
        return SchemaMode.getMode(mode);
    }
}
