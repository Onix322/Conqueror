package org.server.managers.database.driverManager;

import org.server.configuration.Configuration;
import org.server.processors.context.annotations.Component;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * It makes the connection with the database
 */
@Component
public final class MySqlConnection implements ConnectionManager {

    private final Connection CONNECTION;

    private MySqlConnection(Configuration configuration) throws MalformedURLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        this.CONNECTION = this.init(configuration);
    }

    private Connection init(Configuration configuration) throws MalformedURLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
        String driverPath = configuration.readProperty("database.driver");
        String url = configuration.readProperty("database.url");
        String user = configuration.readProperty("database.user");
        String password = configuration.readProperty("database.password");
        DriverManager.registerDriver(this.driver(driverPath));
        return DriverManager.getConnection(url, user, password);
    }

    public Connection connect() {
        return this.CONNECTION;
    }

    @Override
    public void close() throws SQLException {
        this.CONNECTION.close();
    }

    private Driver driver(String path) throws MalformedURLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File(path).toURI().toURL()});
        Object instance = Class.forName("com.mysql.cj.jdbc.Driver", true, classLoader)
                .getDeclaredConstructor()
                .newInstance();

        return new DriverConnector((Driver) instance);
    }
}
