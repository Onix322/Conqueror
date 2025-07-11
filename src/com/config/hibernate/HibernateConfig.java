package src.com.config.hibernate;

import jakarta.persistence.Entity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import src.com.server.annotations.component.configuration.ComponentConfig;
import src.com.server.annotations.component.configuration.ForceInstance;
import src.com.server.processors.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentConfig
public class HibernateConfig {

    @ForceInstance
    public Configuration registerCfg(configuration.Configuration configuration, ApplicationContext applicationContext){
        Configuration cfg = new Configuration();
        List<Class<?>> entities = applicationContext.getEntities()
                .stream()
                .filter(e -> e.isAnnotationPresent(Entity.class))
                .toList();

        Map<String, String> props = new HashMap<>();
        cfg.setProperty("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        cfg.setProperty("jakarta.persistence.jdbc.url", configuration.readProperty("database.url"));
        cfg.setProperty("jakarta.persistence.jdbc.user", configuration.readProperty("database.user"));
        cfg.setProperty("jakarta.persistence.jdbc.password", configuration.readProperty("database.password"));
        cfg.setProperty("hibernate.hbm2ddl.auto", configuration.readProperty("hibernate.hbm2ddl-auto"));
        cfg.setProperty("hibernate.show_sql", configuration.readProperty("hibernate.show_sql"));
        cfg.setProperty("hibernate.archive.autodetection", "class");

        cfg.addAnnotatedClasses(entities.toArray(Class[]::new));

        return cfg;
    }

    @ForceInstance
    public SessionFactory registerEntityManagerFactory(Configuration configuration) {
        return configuration.buildSessionFactory();
    }
}
