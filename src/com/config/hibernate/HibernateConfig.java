package src.com.config.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import src.com.server.annotations.component.configuration.ComponentConfig;
import src.com.server.annotations.component.configuration.ForceInstance;

@ComponentConfig
public class HibernateConfig {

    @ForceInstance
    public Configuration registerHibernateCfg(src.com.server.configuration.Configuration configuration){
        String user = configuration.readProperty("database.user");
        String password = configuration.readProperty("database.password");
        String url = configuration.readProperty("database.url");
        String dialect = configuration.readProperty("hibernate.dialect");
        String hbm2ddl = configuration.readProperty("hibernate.hbm2ddl-auto");

        return new Configuration()
                .setCredentials(user, password)
                .setJdbcUrl(url)
                .setProperty("hbm2ddl.auto", hbm2ddl)
                .setProperty("dialect", dialect);
    }

    @ForceInstance
    public SessionFactory registerSessionFactory(Configuration configurationHibernate){
        return configurationHibernate.buildSessionFactory();
    }
}
