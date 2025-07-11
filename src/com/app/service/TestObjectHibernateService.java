package src.com.app.service;

import jakarta.persistence.EntityNotFoundException;
import src.com.app.entity.TestObject;
import src.com.app.repository.RepositoryTestObjectHibernate;
import src.com.server.annotations.component.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TestObjectHibernateService {

    private RepositoryTestObjectHibernate rtob;

    private TestObjectHibernateService(RepositoryTestObjectHibernate rtob) {
        this.rtob = rtob;
    }

    public TestObject findById(Integer id){
        Optional<TestObject> optional = this.rtob.findById(TestObject.class, id);

        if(optional.isEmpty()) {
            throw new EntityNotFoundException("Entity not found in database");
        }
        return optional.get();
    }

    public List<TestObject> findAll(){
        return this.rtob.findAll(TestObject.class);
    }

    public boolean remove(Integer id){
        return this.rtob.removeById(TestObject.class, id);
    }

    public TestObject create(TestObject entity){
        return this.rtob.save(entity);
    }
}
