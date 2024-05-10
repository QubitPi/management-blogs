---
layout: post
title: Helping Employee with Spring Development
tags: [Java, Spring, Spring Boot]
color: rgb(9, 102, 194)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

A tech leader should focus on one team working on a common goal and avoid personal bias.

<!--more-->

This is a blog about management right? Why would I have a post on a technology I am never in favor of? (Yes, I hate
Spring).

One day a backend intern came to me for help, because he had a trouble debugging a Spring Boot integration tests issue. 
Looked like he just ran out of all ideas.

Having investigated the bug, I resolved the root issue for him and also realized that
[this was not a hard technical problem at all](#spring-it-with-spock-setup), even for a college intern.

I knew blaming him on his face for such an easy bug would not change anything. At the end of the day, I moved on with 2 
followup actions:

1. Reading books on better interviewing/employee training/employee motivations
2. since blaming is always the worst option and a leader should focus on moving the team forward to a common business
   solution, **it helps the team** by having a dedicated *troubleshooting guide* where I can pick up solution for common
   issues quickly and save myself time in the future

### Integration Testing with Spring and Spock

#### `@WebMvcTest(MyController.class)` or `@SpringBootTest`?

![Error loading WebMvcTest-SpringBootTest.png]({{ "/assets/img/WebMvcTest-SpringBootTest.png" | relative_url}})

They all share a common `TestApplication`:

```java
/**
 * {@link TestApplication} is used for both {@code @WebMvcTest(Controller.class)} and {@code @SpringBootTest} annotated
 * integration tests.
 */
@ComponentScan({"..."})
@SpringBootApplication
public class TestApplication {

    /**
     * Inject test dependencies and start test application for integration tests.
     *
     * @param args  Not used
     */
    public static void main(final String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
```

Note the `@ComponentScan` which is very important. While we structure our code in a sensible way, we need to specify
where our configuration classes are. There is another approach, however, which is not having `@ComponentScan` and
annotate each test with specific config classes using

```groovy
@WebMvcTest(MyController.class)
@ContextConfiguration(classes = [MyController.class, MyControllerConfig.class, MyControllerITConfig.class])
class FileControllerITSpec extends Specification {
    
}
```

#### Spring IT with Spock Setup

> [The aha moment](https://stackoverflow.com/a/70383811) - spock-spring
>
> [Reference](https://www.baeldung.com/spring-spock-testing)

Suppose we would like to IT test the following controller:

```java
@RestController
@RequestMapping("/hello")
public class WebController {

    @GetMapping
    public String salutation() {
        return "Hello world!";
    }
}
```

We start by adding the dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>3.2.1</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.4</version>
</dependency>

<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-spring</artifactId>
    <version>2.4-M4-groovy-4.0</version>
    <scope>test</scope>
</dependency>
```

[Taking `@SpringBootTest` as an example](#webmvctestmycontrollerclass-or-springboottest), the test that checks if all 
Beans in the Spring application context are loaded is the following:

```groovy
@SpringBootTest
@AutoConfigureMockMvc
class RequestFilterPlusControllerSpec extends Specification {

    @Autowired (required = false)
    private Controller myController

    @Autowired
    MockMvc mockMvc

    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
        webController
    }
}
```

That's it. `webController` should come out as non-null which means dependency injection has wired up correctly in tests

Note also that `@AutoConfigureMockMvc` is required in order to inject `MockMvc`.

#### Load application.yml Configuration in SpringBoot Test

> [Reference](https://stackoverflow.com/a/38712718)

One option is to work with profiles. Create a file called **application-test.yml**, move all properties we need for 
those tests to that file and then add the `@ActiveProfiles` annotation to test class:

```java
@ActiveProfiles("test") // Like this
public class MyIntTest{
}
```

#### Implementing and Testing File Upload

```java
@PostMapping(value = "/upload")
@ResponseStatus(HttpStatus.CREATED)
public void uploadFile(@NotNull @RequestParam("file") final MultipartFile file) {
    file.getOriginalFilename();
    
    ...
}
```

```groovy
    def "test file upload"() {
        when:
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "pride-and-prejudice-by-jane-austen.txt",
                "text/plain",
                new File("src/test/resources/pride-and-prejudice-by-jane-austen.txt").bytes
        )

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .multipart("/file/upload")
                        .file(mockMultipartFile)

        then:
        mockMvc.perform(builder).andExpect(status().isCreated())
    }
```
