package org.mvc.controller;

import org.server.annotations.Controller;
import org.server.annotations.mapping.GetMapping;

@Controller("/test-object")
public class TestObjectController {

    @GetMapping
    public static String get() {
        return "TestObject Test get method";
    }

    @GetMapping("/getSecond")
    public static String getSecondMessage() {
        return "This is the second message";
    }
}
