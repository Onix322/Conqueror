package src.com.app.service;

import jakarta.persistence.EntityNotFoundException;
import src.com.app.entity.TestObject;
import src.com.app.repository.RepositoryTestObject;
import src.com.server.annotations.component.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TestObjectService {

    private final RepositoryTestObject repository;

    private TestObjectService(RepositoryTestObject repository) {
        this.repository = repository;
    }

    public TestObject findById(Integer id){
        Optional<TestObject> optional = this.repository.findById(TestObject.class, id);

        if(optional.isEmpty()) {
            throw new EntityNotFoundException("Entity not found in database");
        }
        return optional.get();
    }

    public List<TestObject> findAll(){
        return this.repository.findAll(TestObject.class);
    }

    public boolean remove(Integer id){
        return this.repository.removeById(TestObject.class, id);
    }

    public TestObject create(TestObject entity){
        return this.repository.save(entity);
    }

    public TestObject update(TestObject entity, Integer id){
        return this.repository.update(TestObject.class, entity, id);
    }
}
