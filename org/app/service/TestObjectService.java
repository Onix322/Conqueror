package org.app.service;

import org.app.entity.TestObject;
import org.app.repository.Repository;
import org.server.exceptions.NoSuchEntity;
import org.server.annotations.component.Component;

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

    public void create(TestObject testObject) {
        this.REPOSITORY.save(testObject);
    }
}

