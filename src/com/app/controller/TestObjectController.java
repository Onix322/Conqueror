package src.com.app.controller;

import src.com.app.entity.TestObject;
import src.com.app.service.TestObjectService;
import src.com.server.annotations.controller.Controller;
import src.com.server.annotations.controller.mapping.methods.DeleteMapping;
import src.com.server.annotations.controller.mapping.methods.GetMapping;
import src.com.server.annotations.controller.mapping.methods.PostMapping;
import src.com.server.annotations.controller.mapping.methods.PutMapping;
import src.com.server.annotations.controller.mapping.parameters.RequestBody;
import src.com.server.httpServer.utils.response.HttpStatus;
import src.com.server.httpServer.utils.responseEntity.ResponseSuccessful;

import java.util.List;

@Controller("/test-object")
public class TestObjectController {

    private final TestObjectService TEST_OBJECT_SERVICE;

    private TestObjectController(TestObjectService testObjectService) {
        this.TEST_OBJECT_SERVICE = testObjectService;
    }

    @GetMapping("/get/{integer}")
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

    @DeleteMapping("/{integer}")
    public ResponseSuccessful<Boolean> remove(Integer id) {
        return ResponseSuccessful.<Boolean>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.remove(id))
                .build();
    }

    @PostMapping
    public ResponseSuccessful<TestObject> create(@RequestBody TestObject object) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.create(object))
                .build();
    }

    @PutMapping("/{integer}")
    public ResponseSuccessful<TestObject> update(@RequestBody TestObject object, Integer id) {
        return ResponseSuccessful.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(this.TEST_OBJECT_SERVICE.update(object, id))
                .build();
    }
}
