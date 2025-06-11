package org.server.database.mysql.utils.driverManager;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public sealed interface ConnectionManager permits MySqlConnection {

    Connection connect() throws SQLException, MalformedURLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    void close() throws SQLException;
}
