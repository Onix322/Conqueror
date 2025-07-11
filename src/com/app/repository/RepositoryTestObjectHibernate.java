package src.com.app.repository;

import org.hibernate.internal.SessionFactoryImpl;
import src.com.app.entity.TestObject;
import src.com.config.hibernate.RepositoryHibernate;
import src.com.server.annotations.component.Component;

@Component
public class RepositoryTestObjectHibernate extends RepositoryHibernate<TestObject, Integer> {
    public RepositoryTestObjectHibernate(SessionFactoryImpl sessionFactory) {
        super(sessionFactory);
    }
}
