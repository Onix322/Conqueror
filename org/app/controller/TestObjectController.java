package org.app.controller;

import org.app.entity.TestObject;
import org.app.service.TestObjectService;
import org.server.annotations.controller.mapping.DeleteMapping;
import org.server.httpServer.utils.response.HttpStatus;
import org.server.httpServer.utils.responseEntity.ResponseEntity;
import org.server.annotations.controller.Controller;
import org.server.annotations.controller.mapping.GetMapping;
import org.server.annotations.controller.mapping.PostMapping;

import java.util.List;

@Controller("/test-object")
public class TestObjectController {

    private final TestObjectService TEST_OBJECT_SERVICE;

    private TestObjectController(TestObjectService testObjectService) {
        this.TEST_OBJECT_SERVICE = testObjectService;
    }

    @GetMapping("/get-by-id/{integer}")
    public ResponseEntity<TestObject> getById(Integer id) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findById(id))
                .build();
    }

    @GetMapping
    public ResponseEntity<List<TestObject>> getAll() {
        return ResponseEntity.<List<TestObject>>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findAll())
                .build();
    }

    @PostMapping
    public ResponseEntity<TestObject> create(TestObject testObject) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.create(testObject))
                .build();
    }

    @DeleteMapping("/{integer}")
    public ResponseEntity<Boolean> delete(Integer id) {
        return ResponseEntity.<Boolean>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.delete(id))
                .build();
    }
}
