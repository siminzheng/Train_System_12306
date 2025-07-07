package com.jiawa.train.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/business")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World Business!";
    }
}
