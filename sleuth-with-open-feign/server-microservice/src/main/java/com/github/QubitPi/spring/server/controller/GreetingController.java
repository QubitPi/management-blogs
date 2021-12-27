package com.github.QubitPi.spring.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("greeting")
public class GreetingController {

    private static final Logger LOG = LoggerFactory.getLogger(GreetingController.class);

    @GetMapping(value = "/hello")
    public String getGreeting(@RequestHeader HttpHeaders headers) {
        LOG.info("Received request");
        LOG.info("Headers={}", headers);
        return "Hello";
    }
}
