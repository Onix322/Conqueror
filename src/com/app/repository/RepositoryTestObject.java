package src.com.app.repository;

import org.hibernate.internal.SessionFactoryImpl;
import src.com.app.entity.TestObject;
import src.com.config.hibernate.RepositoryHibernate;
import src.com.server.annotations.component.Component;

@Component
public class RepositoryTestObject extends RepositoryHibernate<TestObject, Integer> {
    public RepositoryTestObject(SessionFactoryImpl sessionFactory) {
        super(sessionFactory);
    }
}
