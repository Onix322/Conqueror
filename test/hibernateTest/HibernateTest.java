package test.hibernateTest;

import org.hibernate.cfg.Configuration;

public class HibernateTest {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();

        configuration.setCredentials("root", "root");

    }
}
