package com.github.QubitPi.spring.client.controller;

import com.github.QubitPi.spring.client.GreetingClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting-client")
public class GreetingClientController {

    private static final Logger LOG = LoggerFactory.getLogger(GreetingClientController.class);

    @Autowired
    private GreetingClient greetingClient;

    @ResponseBody
    @GetMapping(value = "/get-greeting")
    public String getGreeting(@RequestHeader HttpHeaders headers) {
        LOG.info("Sending request...");
        LOG.info("Headers={}", headers);
        return greetingClient.getGreeting();
    }
}
