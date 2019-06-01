package com.sevensongoku.helloworld.test.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldControllor {

    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello World!";
    }
}
