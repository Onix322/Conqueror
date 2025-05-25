package org.server.driverManager;

import org.server.configuration.Configuration;
import org.server.processors.context.annotations.Component;

import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * It will make the connection with the database
 */
@Component
public final class MySqlConnection implements ConnectionManager {

    private final Configuration CONFIGURATION;

    private MySqlConnection(Configuration configuration) {
        this.CONFIGURATION = configuration;
    }

    public Connection connect() throws SQLException, MalformedURLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = this.CONFIGURATION.readProperty("database.url");
        String user = this.CONFIGURATION.readProperty("database.user");
        String password = this.CONFIGURATION.readProperty("database.password");

        DriverManager.deregisterDriver(this.diver());
        System.out.println(DriverManager.drivers());
        return DriverManager.getConnection(url, user, password);
    }

    public Driver diver() throws URISyntaxException, MalformedURLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        String path = "org/server/driverManager/mysql-connector-j-9.3.0.jar";

        URLClassLoader classLoader = new URLClassLoader(new URL[]{new URI(path).toURL()});
        Class<?> cls = Class.forName("java.sql.Driver", true, classLoader);
        Object instance = cls.getConstructor().newInstance();
        return (Driver) instance;
    }
}
