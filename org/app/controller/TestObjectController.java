package org.app.controller;

import org.app.entity.TestObject;
import org.app.service.TestObjectService;
import org.server.httpServer.response.HttpStatus;
import org.server.httpServer.responseEntity.ResponseEntity;
import org.server.processors.context.annotations.controller.Controller;
import org.server.processors.context.annotations.controller.mapping.GetMapping;
import org.server.processors.context.annotations.controller.mapping.PostMapping;

@Controller("/test-object")
public class TestObjectController {

    private final TestObjectService TEST_OBJECT_SERVICE;

    private TestObjectController(TestObjectService testObjectService) {
        this.TEST_OBJECT_SERVICE = testObjectService;
    }

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.<String>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData("This is the first message")
                .build();
    }

    @GetMapping("/getSecond")
    public ResponseEntity<String> getSecondMessage() {
        return ResponseEntity.<String>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData("This is the second message")
                .build();
    }

    @GetMapping("/get-var/{integer}/{string}")
    public ResponseEntity<TestObject> getWithVar(Integer integer, String name) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(new TestObject(1, name, integer, 1))
                .build();
    }

    @GetMapping("/get-by-id/{integer}")
    public ResponseEntity<TestObject> getById(Integer id) {
        TestObject testObject = this.TEST_OBJECT_SERVICE.findById(id);

        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(testObject)
                .build();
    }

    @PostMapping
    public ResponseEntity<TestObject> create(TestObject testObject) {
        this.TEST_OBJECT_SERVICE.create(testObject);
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(testObject)
                .build();
    }
}
