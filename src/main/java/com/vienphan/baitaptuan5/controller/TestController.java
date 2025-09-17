package com.vienphan.baitaptuan5.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Application is working!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
