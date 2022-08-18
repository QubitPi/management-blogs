---
layout: post
title: Jersey Test Framework
tags: [Jersey, Testing]
category: FINALIZED
color: rgb(244, 126, 54)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Jersey Test Framework originated as an internal tool used for verifying the correct implementation of server-side 
components. Testing RESTful applications became a more pressing issue with "modern" approaches like test-driven 
development and users started to look for a tool that could help with designing and running the tests as fast as
possible but with many options related to test execution environment.

<!--more-->

* TOC
{:toc}

Current implementation of **Jersey Test Framework** supports the following set of features:

* pre-configured client to access deployed application
* support for multiple containers - grizzly, in-memory, jdk, simple, jetty
* able to run against any external container
* automated configurable traffic logging

Jersey Test Framework is primarily based on JUnit but you can run tests using TestNG as well. It works almost out-of-the
box and it is easy to integrate it within your Maven-based project. While it is usable on all environments where you can
run JUnit, we support primarily the Maven-based setups.

Basics
------

```java
public class SimpleTest extends JerseyTest {
 
    @Path("hello")
    public static class HelloResource {
        @GET
        public String getHello() {
            return "Hello World!";
        }
    }
 
    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class);
    }
 
    @Test
    public void test() {
        final String hello = target("hello").request().get(String.class);
        assertEquals("Hello World!", hello);
    }
}
```

If you want to develop a test using Jersey Test Framework, you need to subclass
[JerseyTest](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java)
and configure the set of resources and/or providers that will be deployed as part of the test application. This short
code snippet shows basic resource class `HelloResource` used in tests defined as part of the `SimpleTest` class. The 
overridden `configure` method returns a
[`ResourceConfig`](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/latest/jersey/org/glassfish/jersey/server/ResourceConfig.html) of the test application,that contains only
the `HelloResource` resource class. `ResourceConfig` is a sub-class of JAX-RS
[Application](https://github.com/jakartaee/rest/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/core/Application.java). 
It is a Jersey convenience class for configuring JAX-RS applications. `ResourceConfig` also implements JAX-RS 
[Configurable](https://github.com/jakartaee/rest/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/core/Configurable.java) 
interface to make the application configuration more flexible.

Supported Containers
--------------------

[JerseyTest](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java)
supports deploying applications on various containers, all (except the external container wrapper) need to have some
"glue" code to be supported. Currently Jersey Test Framework provides support for

* Grizzly
* In-Memory
* JDK (`com.sun.net.httpserver.HttpServer`)
* Simple HTTP container (`org.simpleframework.http`), and
* Jetty HTTP container (`org.eclipse.jetty`).

A test container is selected based on various inputs.
[JerseyTest#getTestContainerFactory()](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java) is 
always executed, so if you override it and provide your own version of
[TestContainerFactory](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/spi/TestContainerFactory.java),
nothing else will be considered. Setting a system variable
[TestProperties#CONTAINER_FACTORY](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/TestProperties.java)
has similar effect. This way you may defer the decision on which containers you want to run your tests from the compile 
time to the test execution time. Default implementation of `TestContainerFactory` looks for container factories on 
classpath. If more than one instance is found and there is a Grizzly test container factory among them, it will be used;
if not, a warning will be logged and the first found factory will be instantiated.

Following is a brief description of all container factories supported in Jersey Test Framework.

* Jersey provides 2 different test container factories based on Grizzly. The `GrizzlyTestContainerFactory` creates a 
  container that can run as a light-weight, plain HTTP container. Almost all Jersey tests are using Grizzly HTTP test 
  container factory. Second factory is `GrizzlyWebTestContainerFactory` that is Servlet-based and supports Servlet 
  deployment context for tested applications. This factory can be useful when testing more complex Servlet-based 
  application deployments.

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework.providers</groupId>
      <artifactId>jersey-test-framework-provider-grizzly2</artifactId>
      <version>2.36</version>
  </dependency>
  ```
  
* In-Memory container is not a real container. It starts Jersey application and directly calls internal APIs to handle 
  request created by client provided by test framework. There is no network communication involved. This containers does 
  not support servlet and other container dependent features, but it is a perfect choice for simple unit tests.

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework.providers</groupId>
      <artifactId>jersey-test-framework-provider-inmemory</artifactId>
      <version>2.36</version>
  </dependency>
  ```
  
* `HttpServer` from Oracle JDK is another supported test container

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework.providers</groupId>
      <artifactId>jersey-test-framework-provider-jdk-http</artifactId>
      <version>2.36</version>
  </dependency>
  ```
  
* Simple container (`org.simpleframework.http`) is another light-weight HTTP container that integrates with Jersey and
  is supported by Jersey Test Framework.

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework.providers</groupId>
      <artifactId>jersey-test-framework-provider-simple</artifactId>
      <version>2.36</version>
  </dependency>
  ```
  
* **Jetty container** (`org.eclipse.jetty`) is another high-performance, light-weight HTTP server that integrates with 
  Jersey and is supported by Jersey Test Framework.

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework.providers</groupId>
      <artifactId>jersey-test-framework-provider-jetty</artifactId>
      <version>2.36</version>
  </dependency>
  ```

Running TestNG Tests
--------------------

It is possible to run not only JUnit tests but also tests based on TestNG. In order to do this you need to make sure the 
following 2 steps are fulfilled:

1. Extend [JerseyTestNg](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTestNg.java), or one of its inner classes
  **`JerseyTestNg.ContainerPerClassTest`**/**`JerseyTestNg.ContainerPerMethodTest`**, instead of
   [JerseyTest](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java).
2. Add TestNG to your class-patch, i.e.:

  ```xml
  <dependency>
      <groupId>org.glassfish.jersey.test-framework</groupId>
      <artifactId>jersey-test-framework-core</artifactId>
      <version>2.36</version>
  </dependency>
  <dependency>
      <groupId>org.testng</groupId>
      <artifactId>testng</artifactId>
      <version>...</version>
  </dependency>
  ```

To discuss the former requirement in more depth we need to take a look at the differences between JUnit and TestNG.
JUnit creates a new instance of a test class for every test present in that class which, from the point of view of
Jersey Test Framework, means that new test container and client is created for each test of a test class. However,
TestNG creates only one instance of a test class and the initialization of the test container depends more on 
setup/teardown methods (driven by `@BeforeXXX` and `@AfterXXX` annotations) than in JUnit. This means that, basically,
you can start one instance of test container for all tests present in a test class or separate test container for each
and every test. For this reason a separate subclasses of JerseyTestNg have been created:

* `JerseyTestNg.ContainerPerClassTest` creates one container to run all the tests in. Setup method is annotated with 
  **`@BeforeClass`**, teardown method with **`@AfterClass`**.

  For example take a look at `ContainerPerClassTest` test. It contains two test methods (`first` and `second`), one 
  singleton resource that returns an increasing sequence of number. Since we spawn only one instance of a test container 
  for the whole class the value expected in the first test is `1` and in the second it's `2`.

  ```java
  public class ContainerPerClassTest extends JerseyTestNg.ContainerPerClassTest {
   
      @Path("/")
      @Singleton
      @Produces("text/plain")
      public static class Resource {
   
          private int i = 1;
   
          @GET
          public int get() {
              return i++;
          }
      }
   
      @Override
      protected Application configure() {
          return new ResourceConfig(Resource.class);
      }
   
      @Test(priority = 1)
      public void first() throws Exception {
          test(1);
      }
   
      @Test(priority = 2)
      public void second() throws Exception {
          test(2);
      }
   
      private void test(final Integer expected) {
          final Response response = target().request().get();
   
          assertEquals(response.getStatus(), 200);
          assertEquals(response.readEntity(Integer.class), expected);
      }
  }
  ```
  
* `JerseyTestNg.ContainerPerMethodTest` creates separate container for each test. Setup method is annotated with 
  **`@BeforeMethod`**, teardown method with **`@AfterMethod`**.

  We can create a similar test to the previous one. Take a look at `ContainerPerMethodTest` test. It looks the same
  except the expected values and extending class: it contains two test methods (`first` and `second`), one singleton 
  resource that returns an increasing sequence of number. In this case we create a separate test container for each test 
  so value expected in the first test is `1` and in the second it's also `1`.

  ```java
  public class ContainerPerMethodTest extends JerseyTestNg.ContainerPerMethodTest {
  
      @Path("/")
      @Singleton
      @Produces("text/plain")
      public static class Resource {
  
          private int i = 1;
  
          @GET
          public int get() {
              return i++;
          }
      }
  
      @Override
      protected Application configure() {
          return new ResourceConfig(Resource.class);
      }
  
      @Test
      public void first() throws Exception {
          test(1);
      }
  
      @Test
      public void second() throws Exception {
          test(1);
      }
  
      private void test(final Integer expected) {
          final Response response = target().request().get();
  
          assertEquals(response.getStatus(), 200);
          assertEquals(response.readEntity(Integer.class), expected);
      }
  }
  ```

If you need more complex setup of your test you can achieve this by directly extending the
[JerseyTestNg](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTestNg.java)
class and creating setup/teardown methods suited to your needs and provide a strategy for storing and handling a test 
container / client instance (see `JerseyTestNg.configureStrategy(TestNgStrategy)` method).

Advanced Features
-----------------

### JerseyTest Features

JerseyTest provide `enable(...)`, `forceEnable(...)` and `disable(...)` methods, that give you control over configuring 
values of the properties defined and described in the `TestProperties` class. A typical code that overrides the default 
property values is listed below:

```java
public class SimpleTest extends JerseyTest {
    // ...
 
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
 
        // ...
 
    }
}
```

The code in the example above enables test traffic logging (inbound and outbound headers) as well as dumping the HTTP 
message entity as part of the traffic logging.

### External Container

Complicated test scenarios may require fully started containers with complex setup configuration, that is not easily 
doable with current Jersey container support. To address these use cases, Jersey Test Framework provides general
fallback mechanism - an **External Test Container Factory**. Support of this external container "wrapper" is provided as 
the following module:

```xml
<dependency>
    <groupId>org.glassfish.jersey.test-framework.providers</groupId>
    <artifactId>jersey-test-framework-provider-external</artifactId>
    <version>2.36</version>
</dependency>
```

As indicated, the "container" exposed by this module is just a wrapper or stub, that redirects all request to a
configured host and port. Writing tests for this container is similar to any other but you have to provide the
information about host and port during the test execution:

```bash
mvn test -Djersey.test.host=myhost.org -Djersey.config.test.container.port=8080
```

### Test Client Configuration

Tests might require some advanced client configuration. This is possible by overriding
[configureClient(ClientConfig clientConfig)](https://github.com/eclipse-ee4j/jersey/blob/master/test-framework/core/src/main/java/org/glassfish/jersey/test/JerseyTest.java)
method. Typical use case for this is registering more providers, such as
[`MessageBodyReader<T>`](https://github.com/jakartaee/rest/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/ext/MessageBodyReader.java)s
or
[`MessageBodyWriter<T>`](https://github.com/jakartaee/rest/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/ext/MessageBodyWriter.java)s,
or enabling additional features.

### Accessing the Logged Test Records Programmatically

Sometimes you might need to check a logged message as part of your test assertions. For this purpose Jersey Test
Framework provides convenient access to the logged records via **JerseyTest#getLastLoggedRecord()** and 
**JerseyTest#getLoggedRecords()** methods. Note that this feature is not enabled by default, see 
**TestProperties#RECORD_LOG_LEVEL** for more information.

Parallel Testing with Jersey Test Framework
-------------------------------------------

For a purpose of running multiple test containers in parallel you need to set the `TestProperties.CONTAINER_PORT` to
`0`. This will tell Jersey Test Framework (and the underlying test container) to use the first available port.

You can set the value as a system property (via command line option) or directly in the test (to not affect ports of
other tests):

```java
@Override
protected Application configure() {
    // Find first available port.
    forceSet(TestProperties.CONTAINER_PORT, "0");

    return new ResourceConfig(Resource.class);
}
```

The easiest way to setup your JUnit or TestNG tests to run in parallel is to configure Maven Surefire plugin. You can do this via configuration options `parallel` and `threadCount`, i.e.:

```xml
...
<configuration>
    <parallel>methods</parallel>
    <threadCount>5</threadCount>
    ...
</configuration>
...
```

For more information about this topic consult the following Maven Surefire articles:

* [Fork Options and Parallel Test Execution](http://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html)
* [Using TestNG - Running tests in parallel](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html#Running_tests_in_parallel)
* [Using JUnit - Running tests in parallel](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html#Running_tests_in_parallel)
