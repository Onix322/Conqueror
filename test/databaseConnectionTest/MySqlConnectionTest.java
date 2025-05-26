package test.databaseConnectionTest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.sql.*;
import java.util.Properties;

public final class MySqlConnectionTest {

    public static Connection connect() throws SQLException, MalformedURLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = "jdbc:mysql://localhost:3306/hoodie";
        String port = "localhost";
        String user = "root";
        String password = "root";

        Driver driver = driver();
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        return driver.connect(url, props);
    }

    public static Driver driver() throws URISyntaxException, MalformedURLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        File file = new File("org/libs/mysql-connector-j-9.3.0.jar");

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});

        Object instance = Class.forName("com.mysql.cj.jdbc.Driver", true, classLoader)
                .getConstructor()
                .newInstance();
        return (Driver) instance;
    }

    public static void main(String[] args) throws MalformedURLException, SQLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try(Connection connection = connect()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM hoodie.product");
            System.out.println();
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                System.out.println(resultSet.getCharacterStream(i));
            }
        };
    }
}