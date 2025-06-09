package org.app.service;

import org.app.entity.TestObject;
import org.app.repository.TestObjectRepository;
import org.server.processors.context.annotations.Component;

@Component
public class TestObjectService {

    private final TestObjectRepository TEST_OBJECT_REPOSITORY;

    private TestObjectService(TestObjectRepository testObjectRepository) {
        this.TEST_OBJECT_REPOSITORY = testObjectRepository;
    }

    public TestObject findById(Integer id){
        return this.TEST_OBJECT_REPOSITORY.findById(TestObject.class, id);
    }

    public void create(TestObject testObject) {
        this.TEST_OBJECT_REPOSITORY.save(testObject);
    }
}

