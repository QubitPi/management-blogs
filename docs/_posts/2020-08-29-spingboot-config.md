---
layout: post
title: Spring Boot Configuration
tags: [Spring Boot, Spring]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/1-cover.png"
thumbnail: "assets/img/post-cover/1-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Swagger Integration

* [Basics](https://devwithus.com/documenting-spring-boot-rest-api-with-swagger/)

## Binding Mechanism in Spring Boot

The basic binding in Spring Boot is through the combination of `@Configuration` and `@Bean`:
https://howtodoinjava.com/spring-core/spring-configuration-annotation/

### @Resource vs @Autowired

### @Value

`@Value` can be used for injecting values into fields in Spring-managed beans, and it **can be applied at the field or
constructor/method parameter level**.

A **property file** is needed in order to define the values we want to inject with the `@Value` annotation. We annotate
a Spring config class with **`@PropertySource`**, with the properties file name, to specify the path of the property
file. For example:

```java
@Component
@PropertySource("classpath:values.properties")
public class PriorityProvider {
 
    private String priority;
 
    @Autowired
    public PriorityProvider(@Value("${property.qualifier.prefix.property}") String priority) {
        this.priority = priority;
    }
 
    // standard getter
}
```

!!! note "`application.properties` - the Default Property File"
    Boot applies its typical convention over configuration approach to property files. This means that we can simply put
    an application.properties file in our `src/main/resources directory`, and it will be auto-detected. We can then
    inject any loaded properties from it as normal.
    
    Using this default file, we don't have to explicitly register a `PropertySource` or even provide a path to a
    property file.
    
#### `@Value` with Enum

We can use enum with `@Value` annotation. A field or constructor parameter can be injected with enum value using
`@Value` annotation. For example, suppose we have an Enum class of

```java
public enum Fruit {
    APPLE,
    ORANGE,
    BANANA
}
```

and a property being picked up at runtime: 

```properties
market.fruit.bestseller = BANANA
```

We could inject value to an enum field using

```java
@Component
public class Foo {
    
    private final Fruit fruit;
    
    @Autowired
    public Foo(@Value("${market.fruit.bestseller}") Fruit fruit) {
        this.fruit = fruit;
    }
}
```

## What is `applicationContext.xml` file?

`applicationContext.xml` is effectively the same thing as `web.xml`. This file should be placed as
`/WEB-INF/applicationContext.xml` under `resource` directory.

Spring 3 MVC accessing HttpRequest from controller

Spring MVC will give you the HttpRequest if you just add it to your controller method signature:

For instance:

```java
/**
 * Generate a PDF report...
 */
@RequestMapping(value = "/report/{objectId}", method = RequestMethod.GET)
public @ResponseBody void generateReport(
        @PathVariable("objectId") Long objectId,
        HttpServletRequest request,
        HttpServletResponse response
) {

    // ...
    // Here you can use the request and response objects like:
    // response.setContentType("application/pdf");
    // response.getOutputStream().write(...);

}
```

As you see, simply adding the `HttpServletRequest` and `HttpServletResponse` objects to the signature makes Spring
MVC to pass those objects to your controller method. You'll want the `HttpSession` object too.

It is strongly recommended to have a look at the list of supported arguments that Spring MVC is able to auto-magically
inject to your handler methods:
https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments

## Logging

### Use Default Logging Pattern

Once you have included the default configuration, you can use its values in your own logback-spring.xml configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
    <!-- use Spring default values -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>
    …
</configuration>
```