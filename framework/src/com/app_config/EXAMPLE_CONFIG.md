# Configuration files for the application
## This directory contains configuration files for the application, which can be used to set up various components.
### - **Example with Hibernate ORM Integration**: 
    - Create any configuration for any class and force it into the context
    - e.g.: Hibernate for database interactions using `@ComponentConfig` and `@ForceInstance` annotations.


### 🛠 Sample POM dependency for the build tool

```xml
<dependency>
  <groupId>org.hibernate.orm</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>6.3.0.Final</version>
</dependency>
```

### 🛠 Configuration class for Hibernate

```java
@ComponentConfig
public class HibernateConfig {

    @ForceInstance
    //Note the parameters are, in fact, Dependencies which will be injected.
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
```
### 🛠 And implementation of the repository configuration

```java
@Component
public class Repository<T, ID extends Number> {
    private final SessionFactory sessionFactory;
    public Repository(SessionFactoryImpl sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<T> findById(Class<T> entity, ID id) {
        Session session = this.sessionFactory.openSession();
        T result = session.byId(entity)
                .with(LockMode.NONE)
                .load(id);
        session.close();
        return Optional.ofNullable(result);
    }

    public List<T> findAll(Class<T> entity) {
        List<T> list;
        Session session = this.sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entity);
        Root<T> rootEntry = cq.from(entity);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = session.createQuery(all);
        list = allQuery.getResultList();
        session.close();
        return list;
    }

    public boolean removeById(Class<T> entity, ID id) {
        Session session = this.sessionFactory.openSession();
        Optional<T> objectBox = this.findById(entity, id);

        if (objectBox.isEmpty()) return false;

        Transaction transaction = session.beginTransaction();
        session.remove(objectBox.get());
        transaction.commit();
        session.close();
        return true;
    }

    public T save(T entity) {
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public T update(Class<T> entityClass, T entity, ID id) {
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;
        T result;
        try {
            transaction = session.beginTransaction();
            result = session.merge(entity);
            transaction.commit();
            session.flush();
            session.refresh(entity);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

        return result;
    }
}
```
