package src.com.config.hibernate;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import src.com.server.database.Persistence;

import java.util.List;
import java.util.Optional;

public class RepositoryHibernate<T, ID extends Number> implements Persistence<T, ID> {

    private final SessionFactory emf;

    public RepositoryHibernate(SessionFactory emf) {
        this.emf = emf;
    }


    @Override
    public Optional<T> findById(Class<T> entity, ID id) {
        Session session = this.emf.openSession();
        T result = session.byId(entity)
                .with(LockMode.NONE)
                .load(id);
        session.close();
        return Optional.ofNullable(result);
    }

    @Override
    public List<T> findAll(Class<T> entity) {
        List<T> list;
        Session session = this.emf.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entity);
        Root<T> rootEntry = cq.from(entity);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = session.createQuery(all);
        list = allQuery.getResultList();
        session.close();
        return list;
    }

    @Override
    public boolean removeById(Class<T> entity, ID id) {
        Session session = this.emf.openSession();
        Optional<T> objectBox = this.findById(entity, id);

        if (objectBox.isEmpty()) return false;

        Transaction transaction = session.beginTransaction();
        session.remove(objectBox.get());
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public T save(T entity) {
        Session session = this.emf.openSession();
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

    @Override
    public T update(Class<T> entityClass, T entity, ID id) {
        Session session = this.emf.openSession();
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
