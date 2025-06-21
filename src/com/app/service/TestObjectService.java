package src.com.app.service;

import src.com.app.entity.TestObject;
import src.com.app.entity.TestObjectDto;
import src.com.app.repository.Repository;
import src.com.server.exceptions.NoSuchEntity;
import src.com.server.annotations.component.Component;

import java.util.List;

@Component
public class TestObjectService {

    private final Repository<TestObject> REPOSITORY;

    private TestObjectService(Repository<TestObject> testObjectRepository) {
        this.REPOSITORY = testObjectRepository;
    }

    public TestObject findById(Integer id) {
        return this.REPOSITORY.findById(TestObject.class, id)
                .orElseThrow(() -> new NoSuchEntity("No such entity in database."));
    }

    public List<TestObject> findAll() {
        return this.REPOSITORY.findAll(TestObject.class);
    }

    public TestObject create(TestObject testObject) {
        return this.REPOSITORY.save(testObject);
    }

    public boolean delete(Integer id) {
        return this.REPOSITORY.removeById(TestObject.class, id);
    }

    public TestObject update(TestObjectDto testObjectDto, Integer id){
        return this.REPOSITORY.update(TestObject.class, testObjectDto, id);
    }

    public TestObject modify(TestObjectDto testObjectDto, Integer id){
        return this.REPOSITORY.modify(TestObject.class, testObjectDto, id);
    }
}

