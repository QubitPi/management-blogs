---
layout: post
title: Test in Spring Boot
tags: [Spring Boot, Spring]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Integration Tests with `@SpringBootTest`

With the `@SpringBootTest` annotation, Spring Boot provides a convenient way to start up an application context to be
used in a test.

## Integration Tests vs. Unit Tests

Before we start into integration tests with Spring Boot, let’s define what sets an integration test apart from a unit
test.

A unit test covers a single "unit", where a unit commonly is a single class, but can also be a cluster of cohesive
classes that is tested in combination.

An integration test can be any of the following:

- a test that covers multiple "units". It tests the interaction between two or more clusters of cohesive classes.
- a test that covers multiple layers. This is actually a specialization of the first case and might cover the
  interaction between a business service and the persistence layer, for instance.
- a test that covers the whole path through the application. In these tests, we send a request to the application and
  check that it responds correctly and has changed the database state according to our expectations.

Spring Boot provides the `@SpringBootTest` annotation which we can use to create an application context containing all
the objects we need for all of the above test types. Note, however, that overusing `@SpringBootTest` might lead to
very long-running test suites.

So, for simple tests that cover multiple units we should rather create plain tests, very similar to unit tests, in which
we manually create the object graph needed for the test and mock away the rest. This way, Spring doesn't fire up a whole
application context each time the test is started.

For tests that cover integration with the web layer or persistence layer, we can use
[@WebMvcTest](https://reflectoring.io/spring-boot-web-controller-test/) or
[@DataJpaTest](https://reflectoring.io/spring-boot-data-jpa-test/) instead. For integration with other layers, have a
look at Spring Boot's other
[test slice annotations](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-test-auto-configuration.html).
Note that these test slices will also take some time to boot up, though.

Finally, for tests that cover the whole Spring Boot application from incoming request to database, or tests that cover
certain parts of the application that are hard to set up manually, we can and should use `@SpringBootTest`

## Creating an ApplicationContext with `@SpringBootTest`

`@SpringBootTest` by default starts searching in the current package of the test class and then searches upwards through
the package structure, looking for a class annotated with `@SpringBootConfiguration` from which it then reads the
configuration to create an application context. This class is usually our main application class since the
`@SpringBootApplication` annotation includes the `@SpringBootConfiguration` annotation. It then creates an application
context very similar to the one that would be started in a production environment.

Because we have a full application context, including web controllers, Spring Data repositories, and data sources,
`@SpringBootTest` is very convenient for integration tests that go through all layers of the application:

```java
@SpringBootTest
@AutoConfigureMockMvc
class RegisterUseCaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registrationWorksThroughAllLayers() throws Exception {
        UserResource user = new UserResource("Zaphod", "zaphod@galaxy.net");

        mockMvc.perform(post("/forums/{forumId}/register", 42L)
                .contentType("application/json")
                .param("sendWelcomeMail", "true")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        UserEntity userEntity = userRepository.findByName("Zaphod");
        assertThat(userEntity.getEmail()).isEqualTo("zaphod@galaxy.net");
    }

}
```

Here, we additionally use `@AutoConfigureMockMvc` to add a `MockMvc` instance to the application context.

We use this `MockMvc` object to perform a `POST` request to our application and to verify that it responds as expected.

We then use the `UserRepository` from the application context to verify that the request has lead to an expected change
in the state of the database.

## Customizing the Application Context

We can turn a lot of knobs to customize the application context created by `@SpringBootTest`.

> ⚠️ Each customization of the application context is one more thing that makes it different from the "real" application
> context that is started up in a production setting. So, in order to make our tests as close to production as we can,
> **we should only customize what's really necessary to get the tests running**!

### Setting Custom Configuration Properties

Often, in tests it’s necessary to set some configuration properties to a value that’s different from the value in a
production setting:

```java
@SpringBootTest(properties = "foo=bar")
class SpringBootPropertiesTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

If the property `foo` exists in the default setting, it will be overridden by the value `bar` for this test.

## Externalizing Properties with `@ActiveProfiles`

If many of our tests need the same set of properties, we can create a configuration file
`application-<profile>.properties` or `application-<profile>.yml` and load the properties from that file by
activating a certain profile::

    # application-test.yml
    foo: bar

```java
@SpringBootTest
@ActiveProfiles("test")
class SpringBootProfileTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

One can also use
[@PropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html)
annotation to achieve the same goal.

Yet another way to customize a whole set of properties is with the `@TestPropertySource` annotation::

    # src/test/resources/foo.properties
    foo=bar

```java
@SpringBootTest
@TestPropertySource(locations = "/foo.properties")
class SpringBootPropertySourceTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

In addition, we can also use
[@ContextConfiguration](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/ContextConfiguration.html),
which is a very similar mechanism for specifying test class properties.

### Injecting Mocks with `@MockBean`

If we only want to test a certain part of the application instead of the whole path from incoming request to database,
we can replace certain beans in the application context by using `@MockBean`:

```java
@SpringBootTest
class MockBeanTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private RegisterUseCase registerUseCase;

    @Test
    void testRegister(){
        // given
        User user = new User("Zaphod", "zaphod@galaxy.net");
        boolean sendWelcomeMail = true;
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity(1L));

        // when
        Long userId = registerUseCase.registerUser(user, sendWelcomeMail);

        // then
        assertThat(userId).isEqualTo(1L);
    }
}
```

In this case, we have replaced the `UserRepository` bean with a mock. Using Mockito's `when` method, we have
specified the expected behavior for this mock in order to test a class that uses this repository.

### Adding Beans with `@Import`

If certain beans are not included in the default application context, but we need them in a test, we can import them
using the `@Import` annotation:

```java
package other.namespace;

@Component
public class Foo {

}
```

```java
@SpringBootTest
@Import(other.namespace.Foo.class)
class SpringBootImportTest {

    @Autowired
    Foo foo;

    @Test
    void test() {
        assertThat(foo).isNotNull();
    }
}
```

### Creating a Custom `@SpringBootApplication`

We can even create a whole custom Spring Boot application to start up in tests. If this application class is in the same
package as the real application class, but in the test sources rather than the production sources, `@SpringBootTest`
will find it before the actual application class and load the application context from this application instead.

Alternatively, we can tell Spring Boot which application class to use to create an application context:

```java
@SpringBootTest(classes = CustomApplication.class)
class CustomApplicationTest {

}
```

When doing this, however, we are **testing an application context that may be completely different from the production
environment**, so this should be a last resort only when the production application cannot be started in a test
environment. Usually, there are better ways, though, such as to make the real application context configurable to
exclude beans that will not start in a test environment. Let’s look at this in an example.

Let's say we use the `@EnableScheduling` annotation on our application class. Each time the application context is
started (even in tests), all `@Scheduled` jobs will be started and may conflict with our tests. We usually don't want
the jobs to run in tests, so we can create a second application class without the `@EnabledScheduling` annotation and
use this in the tests. However, the better solution would be to create a configuration class that can be toggled with a
property:

```java
@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "io.reflectoring.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SchedulingConfiguration {

}
```

We have moved the `@EnableScheduling` annotation from our application class to this special confgiuration class.
Setting the property `io.reflectoring.scheduling.enabled` to `false` will cause this class not to be loaded as part
of the application context:

```java
@SpringBootTest(properties = "io.reflectoring.scheduling.enabled=false")
class SchedulingTest {

    @Autowired(required = false)
    private SchedulingConfiguration schedulingConfiguration;

    @Test
    void test() {
        assertThat(schedulingConfiguration).isNull();
    }
}
```

We have now successfully deactivated the scheduled jobs in the tests.

## IT Design

A code base with a lot of `@SpringBootTest`-annotated tests may take quite some time to run. The Spring test support
is smart enough to only create an application context once and re-use it in following tests, but if different tests need
different application contexts, it will still create a separate context for each test, which takes some time for each
test.

All of the customizing options described above will cause Spring to create a new application context. So, we might want
to create one single configuration and use it for all tests so that the application context can be re-used.

### Pre-configure End-Object in End-to-End Integration Test

The name of this section might sound confusing to you, so let me explian what I meant by starting with an example:

You wrote a Spring Boot application. This app has a `MusicController` that gives you all information about your
personal music library. The `MusicController` is, by convension, backed by a `MusicService`, to which
`MusicController` delegates all user requests.

In order to improve the performance, you add a "caching layer" behind the `MusicService`; let's call the cache
"`MusicCache`". The facilitate our discussion, we will assume the cache is simply a Java `Map` whose key is
`musicId` and value is a `Music` Object.

```java
@RestController
@RequestMapping("/")
public class MetaManagerController {

    @Autowired
    private MusicService musicService;

    @RequestMapping(value="music/{musicId}", method=RequestMethod.GET)
    public List<Music> getMusicInfo(@PathVariable("musicId") String musicId) {
        return getMusicService().getById(musicId);
    }

    private MusicService getMusicService() {
        return musicService;
    }
}
```

For **demonstration** purpose, let's put `MusicCache` as a public field in `MusicService` (don't do this in
production!):

```java
public class MusicService {

    @Autowired
    private MusicCache musicCache;

    ...

    public getById(String musicId) {
        return getMusicCache().get(musicId);
    }

    private MusicCache getMusicCache() {
        return cache;
    }
}
```

Now you are writing integration test against `MusicController` and you would like to pre-insert some cache. The
approach is the following (In your test code):

```java
ServletContext servletContext = webApplicationContext.getServletContext();

MusicCache musicCache = webApplicationContext.getBean("musicCache");
musicCache.put("id1", testMusicObject1);
musicCache.put("id2", testMusicObject2);

String responseData = mockMvc.perform(get("/music/id1"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse().getContentAsString();

// veriry "responseData" has expected testMusicObject1
```