package org.app.controller;

import org.app.entity.TestObject;
import org.server.httpServer.HttpMethod;
import org.server.processors.context.annotations.controller.Controller;
import org.server.processors.context.annotations.controller.mapping.GetMapping;
import org.server.httpServer.response.HttpStatus;
import org.server.processors.context.annotations.controller.mapping.PostMapping;
import org.server.httpServer.responseEntity.ResponseEntity;

import java.util.List;

@Controller("/test-object")
public class TestObjectController {

    private TestObjectController(){
    }

    @GetMapping
    public static ResponseEntity<String> get() {
        return ResponseEntity.<String>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData("This is the first message")
                .build();
    }

    @GetMapping("/getSecond")
    public static ResponseEntity<String> getSecondMessage() {
        return ResponseEntity.<String>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData("This is the second message")
                .build();
    }

    @GetMapping("/get-var/{integer}/{string}")
    public static ResponseEntity<TestObject> getWithVar(Integer integer, String name) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(new TestObject(1, name, integer, 1))
                .build();
    }

    @PostMapping
    public static ResponseEntity<TestObject> create(TestObject testObject) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(testObject)
                .build();
    }
}
