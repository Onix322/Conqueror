package org.app.service;

import org.app.entity.TestObject;
import org.app.repository.TestObjectRepository;
import org.server.exceptions.NoSuchEntity;
import org.server.processors.context.annotations.Component;

import java.util.Optional;

@Component
public class TestObjectService {

    private final TestObjectRepository TEST_OBJECT_REPOSITORY;

    private TestObjectService(TestObjectRepository testObjectRepository) {
        this.TEST_OBJECT_REPOSITORY = testObjectRepository;
    }

    public TestObject findById(Integer id){
        return this.TEST_OBJECT_REPOSITORY.findById(TestObject.class, id)
                .orElseThrow(() -> new NoSuchEntity("No such entity in database."));
    }

    public void create(TestObject testObject) {
        this.TEST_OBJECT_REPOSITORY.save(testObject);
    }
}

