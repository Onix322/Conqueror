package src.com.server.database.mysql.utils.driverManager;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverConnector implements Driver {

    private final Driver DRIVER;

    public DriverConnector(Driver driver) {
        this.DRIVER = driver;
    }

    @Override
    public boolean acceptsURL(String u) throws SQLException {
        return this.DRIVER.acceptsURL(u);
    }

    @Override
    public Connection connect(String u, Properties p) throws SQLException {
        return this.DRIVER.connect(u, p);
    }

    @Override
    public int getMajorVersion() {
        return this.DRIVER.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return this.DRIVER.getMinorVersion();
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return this.DRIVER.getPropertyInfo(u, p);
    }

    @Override
    public boolean jdbcCompliant() {
        return this.DRIVER.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
}
