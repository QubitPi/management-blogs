package com.github.QubitPi.spring.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "greeting", path = "/")
public interface GreetingClient {

    @GetMapping(value = "/greeting/hello")
    String getGreeting();
}
