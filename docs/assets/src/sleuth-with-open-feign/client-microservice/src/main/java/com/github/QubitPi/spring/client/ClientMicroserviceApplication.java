package com.github.QubitPi.spring.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.github.QubitPi.spring.server.client", "com.github.QubitPi.spring.client"})
public class ClientMicroserviceApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(ClientMicroserviceApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(ClientMicroserviceApplication.class, args);
	}

}
