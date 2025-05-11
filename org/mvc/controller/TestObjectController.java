package org.mvc.controller;

import org.mvc.entities.TestObject;
import org.server.annotations.controller.Controller;
import org.server.annotations.controller.mapping.GetMapping;
import org.server.httpServer.response.HttpStatus;
import org.server.responseEntity.ResponseEntity;

@Controller("/test-object")
public class TestObjectController {

    @GetMapping
    public static ResponseEntity<String> get() {
        return ResponseEntity.<String>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData("This is the second message")
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

    @GetMapping("/get-var/{integer}/{name}")
    public static ResponseEntity<TestObject> getWithVar(Integer integer, String name) {
        return ResponseEntity.<TestObject>builder()
                .setHttpStatus(HttpStatus.OK.getCode())
                .setMessage(HttpStatus.OK.getMessage())
                .setData(new TestObject(name, integer))
                .build();
    }
}
