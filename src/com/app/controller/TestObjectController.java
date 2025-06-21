package src.com.app.controller;

import src.com.app.entity.TestObject;
import src.com.app.entity.TestObjectDto;
import src.com.app.service.TestObjectService;
import src.com.server.annotations.controller.mapping.methods.*;
import src.com.server.annotations.controller.mapping.parameters.RequestBody;
import src.com.server.httpServer.utils.response.HttpStatus;
import src.com.server.httpServer.utils.responseEntity.ResponseSuccessful;
import src.com.server.annotations.controller.Controller;

import java.util.List;

@Controller("/test-object")
public class TestObjectController {

    private final TestObjectService TEST_OBJECT_SERVICE;

    private TestObjectController(TestObjectService testObjectService) {
        this.TEST_OBJECT_SERVICE = testObjectService;
    }

    @GetMapping("/get-by-id/{integer}")
    public ResponseSuccessful<TestObject> getById(Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findById(id))
                .build();
    }

    @GetMapping
    public ResponseSuccessful<List<TestObject>> getAll() {
        return ResponseSuccessful.<List<TestObject>>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.findAll())
                .build();
    }

    @PostMapping
    public ResponseSuccessful<TestObject> create(@RequestBody TestObject testObject) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.create(testObject))
                .build();
    }

    @DeleteMapping("/{integer}")
    public ResponseSuccessful<Boolean> delete(Integer id) {
        return ResponseSuccessful.<Boolean>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.delete(id))
                .build();
    }

    @PutMapping("/put/var/{integer}")
    public ResponseSuccessful<TestObject> update(@RequestBody TestObject testObject, Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(testObject)
                .build();
    }

    @PutMapping("/put/{integer}")
    public ResponseSuccessful<Integer> update(Integer id) {
        return ResponseSuccessful.<Integer>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(id)
                .build();
    }

    @PutMapping("/put/id/{integer}")
    public ResponseSuccessful<TestObject> update(@RequestBody TestObjectDto testObjectDto, Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.update(testObjectDto, id))
                .build();
    }

    @PatchMethod("/patch/{integer}")
    public ResponseSuccessful<TestObject> modify(@RequestBody TestObjectDto testObjectDto, Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.modify(testObjectDto, id))
                .build();
    }
}
