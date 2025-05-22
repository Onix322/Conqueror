package org.server.processors.entities;

import org.server.configuration.Configuration;
import org.server.processors.components.annotations.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Will automatically save entity's class and create structure for them in Database
 */
@Component
public final class EntityProcessor {
    private final Configuration CONFIGURATION;
    private final Set<Class<?>> ENTITIES;

    private EntityProcessor(Configuration configuration) {
        this.CONFIGURATION = configuration;
        this.ENTITIES = new LinkedHashSet<>();
    }

    public void register() {

    }
}
