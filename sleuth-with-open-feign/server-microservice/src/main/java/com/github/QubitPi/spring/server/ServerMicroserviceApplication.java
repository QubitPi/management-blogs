package com.github.QubitPi.spring.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerMicroserviceApplication.class, args);
    }
}
