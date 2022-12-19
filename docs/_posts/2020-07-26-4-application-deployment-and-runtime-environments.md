---
layout: post
title: Application Deployment and Runtime Environments
tags: [Deployment, endpoint]
color: rgb(91, 94, 166)
feature-img: "assets/img/post-cover/4-cover.png"
thumbnail: "assets/img/post-cover/4-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

This article is an overview of various server-side environments currently capable of running JAX-RS applications on top
of Jersey server runtime. Jersey supports wide range of server environments from lightweight http containers up to
full-fledged Java EE servers. Jersey applications can also run in an [OSGi](https://www.osgi.org/) runtime. The way how
the application is published depends on whether the application shall run in a Java SE environment or within a
container. 

---
**NOTE**

This chapter is focused on server-side Jersey deployment models. The
[Jersey client runtime](https://qubitpi.github.io/jersey-guide/2020/07/27/5-client-api.html) does not have any
specific container requirements and runs in plain Java SE 6 or higher runtime. 

---

## JAX-RS Application Model

JAX-RS provides a deployment agnostic abstract class `Application` for declaring root resource and provider classes, and
root resource and provider singleton instances. A Web service may extend this class to declare root resource and
provider classes. For example,

```java
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(HelloWorldResource.class);
        return set;
    }
}
```

Alternatively it is possible to reuse
***[ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html),
which is Jersey's own implementations of `Application` class***. This class can either be directly instantiated and then
configured or it can be extended and the configuration code placed into the constructor of the extending class. The
approach typically depends on the chosen deployment runtime.

**Compared to `Application`, the `ResourceConfig` provides advanced capabilities to simplify registration of JAX-RS
components**, such as scanning for root resource and provider classes in a provided classpath or a set of package names
etc. All JAX-RS component classes that are either manually registered or found during scanning are automatically added
to the set of classes that are returned by `getClasses`. For example, the following application class that extends from
`ResourceConfig` scans during deployment for JAX-RS components in packages `org.foo.rest` and `org.bar.rest`:

---
**NOTE**

Package scanning ignores an inheritance and therefore `@Path` annotation on parent classes and interfaces will be
ignored. These classes won't be registered as the JAX-RS component classes. 

---

```java
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages("org.foo.rest;org.bar.rest");
    }
}
```

---
**NOTE**

Later in this article, the term `Application` subclass is frequently used. Whenever used, this term refers to the JAX-RS
Application Model explained above. 

---

## Auto-Discoverable Features

By default Jersey 2.x does not implicitly register any extension features from the modules available on the classpath,
unless explicitly stated otherwise in the documentation of each particular extension. Users are expected to explicitly
register the extension `Feature`s using their `Application` subclass. For a few Jersey provided modules however there is
no need to explicitly register their extension `Feature`s as these are discovered and registered in the `Configuration`
(on client/server) automatically by Jersey runtime whenever the modules implementing these features are present on the
classpath of the deployed JAX-RS application. The modules that are automatically discovered include: 

* JSON binding feature from `jersey-media-moxy`
* `jersey-media-json-processing`
* `jersey-bean-validation`

Besides these modules there are also few features/providers present in `jersey-server` module that are discovered by
this mechanism and their availability is affected by Jersey auto-discovery support configuration (see
[Configuring Feature Auto-discovery Mechanism](#configuring-feature-auto-discovery-mechanism)), namely: 

* [WadlFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/wadl/WadlFeature.html) - enables WADL processing.
* [UriConnegFilter](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/filter/UriConnegFilter.html) - a URI-based content negotiation filter.

### Configuring Feature Auto-discovery Mechanism

The mechanism of feature auto-discovery in Jersey that described above is enabled by default. It can be disabled by
using special (common/server/client) properties:

**Common auto discovery properties**

* [CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#FEATURE_AUTO_DISCOVERY_DISABLE):
When set, disables auto discovery globally on client/server.
  
* [CommonProperties.JSON_PROCESSING_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#JSON_PROCESSING_FEATURE_DISABLE):
When set, disables configuration of Json Processing (JSR-353) feature.

* [CommonProperties.MOXY_JSON_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#MOXY_JSON_FEATURE_DISABLE):
When set, disables configuration of MOXy Json feature.

For each of these properties there is a client/server counter-part that is only honored by the Jersey client or server
runtime respectively (see [ClientProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ClientProperties.html)/[ServerProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html)).
When set, each of these client/server specific auto-discovery related properties overrides the value of the related
common property. 

## Configuring the Classpath Scanning

Jersey uses a common Java Service Provider mechanism to obtain all service implementations. It means that Jersey scans
the whole class path to find appropriate `META-INF/services/` files. The class path scanning may be time consuming. The
more jar or war files on the classpath the longer the scanning time. In use cases where you need to save every
millisecond of application bootstrap time, you may typically want to disable the services provider lookup in Jersey. 

**List of SPIs recognized by Jersey**

* `AutoDiscoverable` (server, client): it means if you disable service loading the AutoDiscoverable feature is
  automatically disabled too

* `ForcedAutoDiscoverable` (server, client): Jersey always looks for these auto discoverable features even if the
  service loading is disabled

* `HeaderDelegateProvider` (server, client)

* `ComponentProvider` (server)

* `ContainerProvider` (server)

* `AsyncContextDelegateProvider` (server/Servlet)

**List of additional SPIs recognized by Jersey in case the `metainf-services` module is on the classpath**

* `MessageBodyReader` (server, client)

* `MessageBodyWriter` (server, client)

* `ExceptionMapper` (server, client)

Since it is possible to configure all SPI implementation classes or instances manually in your `Application` subclass,
disabling services lookup in Jersey does not affect any functionality of Jersey core modules and extensions and can save
dozens of ms during application initialization in exchange for a more verbose application configuration code. 

The services lookup in Jersey (enabled by default) can be disabled via a dedicated
[CommonProperties.METAINF_SERVICES_LOOKUP_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#METAINF_SERVICES_LOOKUP_DISABLE)
property. There is a client/server counter-part that only disables the feature on the client or server respectively:
[ClientProperties.METAINF_SERVICES_LOOKUP_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ClientProperties.html#METAINF_SERVICES_LOOKUP_DISABLE)/[ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#METAINF_SERVICES_LOOKUP_DISABLE).
As in all other cases, the client/server specific properties overrides the value of the related common property, when
set. 

For example, following code snippet disables service provider lookup and manually registers implementations of different JAX-RS and Jersey provider types

```java
ResourceConfig resourceConfig = new ResourceConfig(MyResource.class);
resourceConfig.register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
resourceConfig.register(org.glassfish.jersey.server.validation.ValidationFeature.class);
resourceConfig.register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);
resourceConfig.register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
resourceConfig.property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
```

Similarly, in scenarios where the deployment model requires extending the `Application` subclass (e.g. in all Servlet
container deployments), the following code could be used to achieve the same application configuration:

```java
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
        register(org.glassfish.jersey.server.validation.ValidationFeature.class);
        register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);
        register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
        property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
    }
}
```

## Java SE Deployment Environments

### HTTP servers

Java based HTTP servers represent a minimalistic and flexible way of deploying Jersey application. The HTTP servers are
usually embedded in the application and configured and started programmatically. In general, Jersey container for a
specific HTTP server provides a custom factory method that returns a correctly initialized HTTP server instance.

#### JDK Http Server

Starting with Java SE 6, Java runtime ships with a built-in lightweight HTTP server. Jersey offers integration with this
Java SE HTTP server through the `jersey-container-jdk-http` container extension module. Instead of creating the
[HttpServer](http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html)
instance directly, use the `createHttpServer()` method of
[JdkHttpServerFactory](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jdkhttp/JdkHttpServerFactory.html),
which creates the `HttpServer` instance configured as a Jersey container and initialized with the supplied `Application`
subclass. 

Creating new Jersey-enabled jdk http server is as easy as: 

```java
URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
ResourceConfig config = new ResourceConfig(MyResource.class);
HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
```

A JDK HTTP Container dependency needs to be added: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-jdk-http</artifactId>
    <version>2.31</version>
</dependency>
```

#### Grizzly HTTP Server

[Grizzly](https://javaee.github.io/grizzly/) is a multi-protocol framework built on top of Java
[NIO](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html). Grizzly aims to simplify development of
robust and scalable servers. Jersey provides a container extension module that enables support for using Grizzly as a
plain vanilla HTTP container that runs JAX-RS applications. Starting a Grizzly server to run a JAX-RS or Jersey
application is one of the most lightweight and easy ways of exposing a functional RESTful services application. 

Grizzly HTTP container supports injection of Grizzly-specific `org.glassfish.grizzly.http.server.Request` and
`org.glassfish.grizzly.http.server.Response` instances into JAX-RS and Jersey application resources and providers.
However, since Grizzly `Request` is not proxiable, the injection of Grizzly `Request` into singleton (by default)
JAX-RS / Jersey providers is only possible via `javax.inject.Provider` instance. (Grizzly `Response` does not suffer the
same restriction.)

```java
URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
ResourceConfig config = new ResourceConfig(MyResource.class);
HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
```

The container extension module dependency to be added is:

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-grizzly2-http</artifactId>
    <version>2.31</version>
</dependency>
```

#### Jetty HTTP Server

[Jetty](https://www.eclipse.org/jetty/) is a popular Servlet container and HTTP server. We will not look into Jetty's
capabilities as a Servlet container (although Jersey is using it in its tests and examples), because there is nothing
specific to Jetty when using a Servlet-based deployment model, which is extensively described later in
[Servlet-based Deployment](#servlet-based-deployment) section. We will here only focus on describing how to use Jetty's
HTTP server. 

Jetty HTTP container supports injection of Jetty-specific `org.eclipse.jetty.server.Request` and
`org.eclipse.jetty.server.Response` instances into JAX-RS and Jersey application resources and providers. However, since
Jetty HTTP `Request` is not proxiable, the injection of Jetty `Request` into singleton (by default) JAX-RS / Jersey
providers is only possible via `javax.inject.Provider` instance. (Jetty `Response` does not suffer the same restriction.) 

```java
URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
ResourceConfig config = new ResourceConfig(MyResource.class);
Server server = JettyHttpContainerFactory.createServer(baseUri, config);
```

And, of course, we add the necessary container extension module dependency: 
 
```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-jetty-http</artifactId>
    <version>2.31</version>
</dependency>
``` 

---
**NOTE**

Jetty HTTP container does not support deployment on context paths other than root path ("/"). Non-root context path is
ignored during deployment.

---

#### Netty HTTP Server

Netty is a [NIO](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html) client server framework which
enables quick and easy development of network applications such as protocol servers and clients. Jersey supports Netty
as a container and as a client connector - we present here how to use the container.

```java
URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
ResourceConfig resourceConfig = new ResourceConfig(HelloWorldResource.class);
Channel server = NettyHttpContainerProvider.createServer(baseUri, resourceConfig, false);
```

And, of course, we add the necessary container extension module dependency: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-netty-http</artifactId>
    <version>2.31</version>
</dependency>
```

---
**NOTE**

Jetty HTTP container does not support deployment on context paths other than root path ("/"). Non-root context path is
ignored during deployment.

---

## Creating programmatic JAX-RS endpoint
   
JAX-RS specification also defines the ability to programmatically create a JAX-RS application
***endpoint (i.e. container)*** for any instance of a `Application` subclass. For example, Jersey supports creation of
[Grizzly](http://grizzly.java.net/) HttpHandler instance as follows: 

```java
HttpHandler endpoint = RuntimeDelegate.getInstance().createEndpoint(new MyApplication(), HttpHandler.class);
```

Once the [Grizzly](http://grizzly.java.net/) `HttpHandler` endpoint is created, it can be used for in-process deployment
to a specific base URL. 

## Servlet-based Deployment

In a Servlet container, JAX-RS defines multiple deployment options depending on the Servlet API version supported by the
Servlet container. Following sections describe these options in detail. 

### Servlet 2.x Container

Jersey integrates with any Servlet containers supporting at least Servlet 2.5 specification. Running on a Servlet
container that supports Servlet API 3.0 or later gives you the advantage of wider feature set (especially asynchronous
request processing support) and easier and more flexible deployment options. In this section we will focus on the basic
deployment models available in any Servlet 2.5 or higher container. 

***In Servlet 2.5 environment, you have to explicitly declare the Jersey container Servlet in your Web application's
`web.xml` deployment descriptor file***.

```xml
<web-app>
    <servlet>
        <servlet-name>MyApplication</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            ...
        </init-param>
    </servlet>
    ...
    <servlet-mapping>
        <servlet-name>MyApplication</servlet-name>
        <url-pattern>/myApp/*</url-pattern>
    </servlet-mapping>
    ...
</web-app>
```

Alternatively, you can register Jersey container as a filter: 

```xml
<web-app>
    <filter>
        <filter-name>MyApplication</filter-name>
        <filter-class>org.glassfish.jersey.servlet.ServletContainer</filter-class>
        <init-param>
            ...
        </init-param>
    </filter>
    ...
    <filter-mapping>
        <filter-name>MyApplication</filter-name>
        <url-pattern>/myApp/*</url-pattern>
    </filter-mapping>
    ...
</web-app>
```

---
**Important**

Servlet 2.x API does not provide a way how to programmatically read the filter mappings. To make application deployed
using filter work correctly, either Servlet 3.x container must be used (`jersey-container-servlet` instead of
`jersey-container-servlet-core`), or the context path of the app needs to be defined using init parameter
`jersey.config.servlet.filter.contextPath`. 

---

The content of the `<init-param>` element will vary depending on the way you decide to configure Jersey resources.

#### Custom Application subclass

If you extend the `Application` class to provide the list of relevant root resource classes (`getClasses()`) and
singletons (`getSingletons()`), i.e. your JAX-RS application model, you then need to register it in your web application
`web.xml` deployment descriptor using a Servlet or Servlet filter initialization parameter with a name of
`javax.ws.rs.Application` as follows: 

```xml
<init-param>
    <param-name>javax.ws.rs.Application</param-name>
    <param-value>org.foo.MyApplication</param-value>
</init-param>
```

Jersey will consider all the classes returned by `getClasses()` and `getSingletons()` methods of your `Application`
implementation. 

---
**NOTE**

***The name of the configuration property as defined by JAX-RS specification is indeed `javax.ws.rs.Application` and not
`javax.ws.rs.core.Application` as one might expect***.

---

#### Jersey package scanning

If there is no configuration properties to be set and deployed application consists only from resources and providers
stored in particular packages, you can instruct Jersey to scan these packages and register any found resources and
providers automatically: 

```xml
<init-param>
    <param-name>jersey.config.server.provider.packages</param-name>
    <param-value>
        org.foo.myresources,org.bar.otherresources
    </param-value>
</init-param>
<init-param>
    <param-name>jersey.config.server.provider.scanning.recursive</param-name>
    <param-value>false</param-value>
</init-param>
```

Jersey will automatically discover the resources and providers in the selected packages. You can also decide whether
Jersey should recursively scan also sub-packages by setting the `jersey.config.server.provider.scanning.recursive`
property. The default value is `true`, i.e. the recursive scanning of sub-packages is enabled.

#### Selecting concrete resource and provider classes

While the above-mentioned package scanning is useful esp. for development and testing, you may want to have a little bit
more control when it comes to production deployment in terms of being able to enumerate specific resource and provider
classes. In Jersey it is possible to achieve this even without a need to implement a custom `Application` subclass. The
specific resource and provider fully-qualified class names can be provided in a comma-separated value of
`jersey.config.server.provider.classnames` initialization parameter. 

```xml
<init-param>
    <param-name>jersey.config.server.provider.classnames</param-name>
    <param-value>
        org.foo.myresources.MyDogResource,
        org.bar.otherresources.MyCatResource
    </param-value>
</init-param>
```

---
**NOTE**

All of the techniques that have been described in this section also apply to Servlet containers that support Servlet API
3.0 and later specification. Newer Servlet specifications only give you additional features, deployment options and more
flexibility. 

---

### Servlet 3.x Container

#### Descriptor-less deployment

There are multiple deployment options in the Servlet 3.0 container for a JAX-RS application defined by implementing a
custom `Application` subclass. For simple deployments, no `web.xml` is necessary at all. Instead, an
`@ApplicationPath` annotation can be used to annotate the custom `Application` subclass and define the base application
URI for all JAX-RS resources configured in the application: 

```java
@ApplicationPath("resources")
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages("org.foo.rest;org.bar.rest");
    }
}
```

---
**NOTE**

There are many other convenience methods in the `ResourceConfig` that can be used in the constructor of your custom
subclass to configure your JAX-RS application, see [ResourceConfig API documentation](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html)
for more details. 

---

In case you are not providing `web.xml` deployment descriptor for your maven-based web application project, you need to
configure your `maven-war-plugin` to ignore the missing `web.xml` file by setting [failOnMissingWebXml](http://maven.apache.org/plugins/maven-war-plugin/war-mojo.html#failOnMissingWebXml)
configuration property to `false` in your project `pom.xml` file: 

```xml
<plugins>
    ...
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-war-plugin</artifactId>
        <version>2.3</version>
        <configuration>
            <failOnMissingWebXml>false</failOnMissingWebXml>
        </configuration>
    </plugin>
    ...
</plugins>
```

#### Deployment using `web.xml` descriptor

Another Servlet 3.x container deployment model is to declare the JAX-RS application details in the `web.xml`. This is
typically suitable for more complex deployments, e.g. when security model needs to be properly defined or when
additional initialization parameters have to be passed to Jersey runtime. JAX-RS 1.1 and later specifies that a fully
qualified name of the class that implements `Application` may be used in the definition of a `<servlet-name>` element as
part of your application's `web.xml` deployment descriptor. 

Following example illustrates this approach: 

```xml
<web-app>
    <servlet>
        <servlet-name>org.foo.rest.MyApplication</servlet-name>
    </servlet>
    ...
    <servlet-mapping>
        <servlet-name>org.foo.rest.MyApplication</servlet-name>
        <url-pattern>/resources</url-pattern>
    </servlet-mapping>
    ...
</web-app>
```

Note that the `<servlet-class>` element is omitted from the Servlet declaration. This is a correct declaration utilizing
the Servlet 3.0 extension mechanism described in detail in the Section
["Servlet Pluggability Mechanism"](#servlet-pluggability-mechanism) section. Also note that `<servlet-mapping>` is used
in the example to define the base resource URI. 

---
**Tip**

When running in a Servlet 2.x it would instead be necessary to declare the Jersey container Servlet or Filter and pass
the `Application` implementation class name as one of the init-param entries, as described in Section 
["Servlet 2.x Container"](#servlet-2x-container). 

---

#### Servlet Pluggability Mechanism

Servlet framework pluggability mechanism is a feature introduced with Servlet 3.0 specification. It simplifies the
configuration of various frameworks built on top of Servlets. Instead of having one `web.xml` file working as a central
point for all the configuration options, it is possible to modularize the deployment descriptor by using the concept of
so-called ***web fragments*** - several specific and focused `web.xml` files. A set of web fragments basically builds up
the final deployment descriptor. This mechanism also provides SPI hooks that enable web frameworks to register
themselves in the Servlet container or customize the Servlet container deployment process in some other way. This
section describes how JAX-RS and Jersey leverage the Servlet pluggability mechanism.

##### JAX-RS application without an Application subclass

If no `Application` (or `ResourceConfig`) subclass is present, Jersey will dynamically add a Jersey container Servlet
and set its name to `javax.ws.rs.core.Application`. **The web application path will be scanned and all the root resource
classes (the classes annotated with `@Path` annotation) as well as any providers that are annotated with `@Provider`
annotation packaged with the application will be automatically registered in the JAX-RS application**. The web
application has to be packaged with a deployment descriptor specifying at least the mapping for the added
`javax.ws.rs.core.Application` Servlet: 

```xml
<web-app version="3.0"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 
    <!-- Servlet declaration can be omitted in which case
         it would be automatically added by Jersey -->
    <servlet>
        <servlet-name>javax.ws.rs.core.Application</servlet-name>
    </servlet>
 
    <servlet-mapping>
        <servlet-name>javax.ws.rs.core.Application</servlet-name>
        <url-pattern>/myresources/*</url-pattern>
    </servlet-mapping>
</web-app>
```

##### JAX-RS application with a custom Application subclass

When a custom `Application` subclass is provided, in such case the Jersey server runtime behavior depends on whether or
not there is a Servlet defined to handle the application subclass.

If the `web.xml` contains a Servlet definition, that has an initialization parameter
`javax.ws.rs.Application` whose value is the fully qualified name of the `Application` subclass, Jersey does not perform
any additional steps in such case.

If no such Servlet is defined to handle the custom `Application` subclass, Jersey dynamically adds a Servlet with a
fully qualified name equal to the name of the provided `Application` subclass. To define the mapping for the added
Servlet, you can either annotate the custom `Application` subclass with an `@ApplicationPath` annotation (Jersey will
use the annotation value appended with `/*` to automatically define the mapping for the Servlet), or specify the mapping
for the Servlet in the `web.xml` descriptor directly. 

In the following example, let's assume that the JAX-RS application is defined using a custom `Application` subclass
named `org.example.MyApplication`. Then the `web.xml` file could have the following structure:

```xml
<web-app version="3.0"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 
    <!-- Servlet declaration can be omitted in which case
         it would be automatically added by Jersey -->
    <servlet>
        <servlet-name>org.example.MyApplication</servlet-name>
    </servlet>
 
    <!-- Servlet mapping can be omitted in case the Application subclass
         is annotated with @ApplicationPath annotation; in such case
         the mapping would be automatically added by Jersey -->
    <servlet-mapping>
        <servlet-name>org.example.MyApplication</servlet-name>
        <url-pattern>/myresources/*</url-pattern>
    </servlet-mapping>
</web-app>
```

---
**Note**

If your custom `Application` subclass is packaged in the `war`, it defines which resources will be taken into account.

* If both `getClasses()` and `getSingletons()` methods return an empty collection, then ALL the root resource classes
and providers packaged in the web application archive will be used, Jersey will automatically discover them by scanning
the `.war` file. 
* If any of the two mentioned methods - `getClasses()` or `getSingletons()` returns a non-empty collection, only those
classes and/or singletons will be published in the JAX-RS application. 

---

|                       Condition                      | Jersey action |           Servlet Name          |                                        `web.xml`                                       |
|:----------------------------------------------------:|:-------------:|:-------------------------------:|:------------------------------------------------------------------------------------:|
| No `Application` subclass                              | Adds Servlet  | `javax.ws.rs.core.Application`    | Servlet mapping is required                                                          |
| `Application` subclass handled by existing Servlet     | No action     | Already defined                 | Not required                                                                         |
| `Application` subclass NOT handled by existing Servlet | Adds Servlet  | FQN of the `Application` subclass | if no `@ApplicationPath` on the `Application` subclass, then Servlet mapping is required |

### Jersey Servlet container modules

Jersey uses its own
[ServletContainer](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/servlet/ServletContainer.html)
implementation of Servlet and Servlet Filter API to integrate with Servlet containers. As any JAX-RS runtime, Jersey
provides support for Servlet containers that support Servlet specification version 2.5 and higher. **To support JAX-RS
2.0 asynchronous resources on top of a Servlet container, support for Servlet specification version 3.0 or higher is
required**. 

When deploying to a Servlet container, Jersey application is typically packaged as a `.war` file. As with any other
Servlet application, JAX-RS application classes are packaged in `WEB-INF/classes` or `WEB-INF/lib` and required
application libraries are located in `WEB-INF/lib`. For more details, please refer to the Servlet Specification
([JSR 315](https://jcp.org/en/jsr/detail?id=315)). 

Jersey provides two Servlet modules. The first module is the Jersey core Servlet module that provides the core Servlet
integration support and is required in any Servlet 2.5 or higher container: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet-core</artifactId>
</dependency>
```

To support additional Servlet 3.x deployment modes and asynchronous JAX-RS resource programming model, an additional
Jersey module is required:

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
</dependency>
```

The `jersey-container-servlet` module depends on `jersey-container-servlet-core` module, therefore when it is used, it
is not necessary to explicitly declare the `jersey-container-servlet-core` dependency. 

Note that in simple cases, you don't need to provide the deployment descriptor (`web.xml`) and can use the
`@ApplicationPath annotation`, as described in
["JAX-RS application without an Application subclass"](#jax-rs-application-without-an-application-subclass) section. 

## Java EE Platform

This section describes, how you can publish Jersey JAX-RS resources as various Java EE platform elements. JAX-RS and
Jersey give you wide choice of possibilities and it is up to your taste (and design of your application), what Java EE
technology you decide to use for the management of your resources.

### Managed Beans

Jersey supports the use of Java EE Managed beans as root resource classes, providers as well as `Application`
subclasses. 

In the code below, you can find an example of a bean, that uses a managed-bean interceptor defined as a JAX-RS bean. The
bean is used to intercept calls to the resource method `getIt()`:

```java
@ManagedBean
@Path("/managedbean")
public class ManagedBeanResource {
 
    public static class MyInterceptor {
        @AroundInvoke
        public String around(InvocationContext ctx) throws Exception {
            System.out.println("around() called");
            return (String) ctx.proceed();
        }
    }
 
    @GET
    @Produces("text/plain")
    @Interceptors(MyInterceptor.class)
    public String getIt() {
        return "Hi managed bean!";
    }
}
```

### Context and Dependency Injection (CDI)

CDI beans can be used as Jersey root resource classes, providers as well as `Application` subclasses. Providers and
`Application` subclasses have to be singleton or application scoped. 

The next example shows a usage of a CDI bean as a JAX-RS root resource class. We assume, that CDI has been enabled. The
code snipped uses the type-safe dependency injection provided in CDI by using another bean (`MyOtherCdiBean`):

```java
@Path("/cdibean")
public class CdiBeanResource {

    @Inject MyOtherCdiBean bean;  // CDI injected bean
 
    @GET
    @Produces("text/plain")
    public String getIt() {
        return bean.getIt();
    }
}
``` 

The above works naturally inside any Java EE compliant AS container. In Jersey version 2.15, container agnostic CDI
support was introduced. This feature allows you to publish CDI based JAX-RS resources also in other containers. Jersey
cdi-webapp example shows Jersey/CDI integration in Grizzly HTTP and Apache Tomcat server. Detailed description of Jersey
CDI support outside of a fully fledged Java EE application container could be found in
[Jersey CDI Container Agnostic Support](https://qubitpi.github.io/jersey-guide/2020/08/16/25-jersey-cdi-container-agnostic-support.html).

### Enterprise Java Beans (EJB)

Stateless and Singleton Session beans can be used as Jersey root resource classes, providers and/or `Application`
subclasses. You can choose from annotating the methods in the EJB's local interface or directly the method in an
interface-less EJB POJO. JAX-RS specifications requires its implementors to discover EJBs by inspecting annotations on
classes (or local interfaces), but not in the deployment descriptors (`ejb-jar.xml`). As such, to keep your JAX-RS
application portable, do not override EJB annotations or provide any additional meta-data in the deployment descriptor
file. 

Following example consists of a stateless EJB and a local interface used in Jersey: 

```java
@Local
public interface LocalEjb {

    @GET
    @Produces("text/plain")
   public String getIt();
}
 
@Stateless
@Path("/stateless")
public class StatelessEjbResource implements LocalEjb {

    @Override
    public String getIt() {
        return "Hi Stateless!";
    }
}
```

---
**Note**

Please note that Jersey currently does not support deployment of JAX-RS applications packaged as standalone EJB modules
(ejb-jars). To use EJBs as JAX-RS resources, the EJBs need to be packaged either directly in a WAR or in an EAR that
contains at least one WAR. This is to ensure Servlet container initialization that is necessary for bootstrapping of the
Jersey runtime. 

---

### Java EE Servers

#### GlassFish Application Server

You don't need to add any specific dependencies on GlassFish, Jersey is already packaged within GlassFish. You only need
to add the `provided`-scoped dependencies to your project to be able to compile it. At runtime, GlassFish will make sure
that your application has access to the Jersey libraries.

Started with version 2.7, Jersey allows injecting Jersey specific types into CDI enabled JAX-RS components using the
`@javax.inject.Inject` annotation. This covers also custom HK2 bindings, that are configured as part of Jersey
application. The feature specifically enables usage of Jersey monitoring statistics (provided that the statistic feature
is turned on) in CDI environment, where injection is the only mean to get access to monitoring data.

Since both CDI and HK2 use the same injection annotation, Jersey could get confused in certain cases, which could lead
to nasty runtime issues. The get better control over what Jersey evaluates as HK2 injection, end-users could take
advantage of newly introduced,
[Hk2CustomBoundTypesProvider](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/ext/cdi1x/spi/Hk2CustomBoundTypesProvider.html),
SPI. Please see the linked javadoc to get detailed information on how to use the SPI in your application.

#### Oracle WebLogic Server

WebLogic 12.1.2 and earlier supports only JAX-RS 1.1 ([JSR 311](https://jcp.org/en/jsr/detail?id=311)) out of the box
with Jersey 1.x (WebLogic 12.1.2 ships with Jersey 1.13). To update the version of Jersey 1.x in these earlier WebLogic
releases, please read the
[Updating the Version of Jersey JAX-RS RI](https://docs.oracle.com/middleware/1212/wls/RESTF/version-restful-service.htm#RESTF197)
chapter in the WebLogic RESTful Web Services Development Guide.

In WebLogic 12.1.3, Jersey 1.18 is shipped as a default JAX-RS 1.1 provider. In this version of WebLogic, JAX-RS 2.0
(using Jersey 2.5.1) is supported as an optionally installable shared library. Please read through the
[WebLogic 12.1.3 RESTful Web Services Development Guide](https://docs.oracle.com/middleware/1213/wls/RESTF/use-jersey20-ri.htm#RESTF290) for details how to enable JAX-RS 2.0 support on WebLogic 12.1.3. 

#### Other Application Servers

Third party Java EE application servers usually ship with a JAX-RS implementation. If you want to use Jersey instead of
the default JAX-RS provider, you need to add Jersey libraries to your classpath and disable the default JAX-RS provider
in the container.

In general, Jersey will be deployed as a Servlet and the resources can be deployed in various ways, as described in this
section. However, the exact steps will vary from vendor to vendor.

## OSGi

OSGi support has been added to the Jersey version 1.2. Since then, you should be able to utilize standard OSGi means to
run Jersey based web applications in OSGi runtime as described in the OSGi Service Platform Enterprise Specification.
At the time of writing, Jersey is compatible with OSGi 4.2.0.

The two supported ways of running an OSGi web application are: 

* WAB (Web Application Bundle)
* HTTP Service

WAB is in fact just an OSGified WAR archive. HTTP Service feature allows you to publish Java EE Servlets in the OSGi
runtime. 

Two examples were added to the Jersey distribution to depict the above mentioned features and show how to use them with
Jersey:

* [WAB Example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/osgi-helloworld-webapp)
* [HTTP Service example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/osgi-http-service)

Both examples are multi-module maven projects and both consist of an application OSGi bundle module and a test module.
The tests are based on the [PAX Exam](https://ops4j1.jira.com/wiki/spaces/PAXEXAM3/overview) framework. Both OSGi
examples also include a readme file containing instructions how to manually run the example applications using
[Apache Felix](https://felix.apache.org) framework.

The rest of the chapter describes how to run the above mentioned examples on GlassFish 4 application server.

### Enabling the OSGi shell in Glassfish

Since GlassFish utilizes Apache Felix, an OSGi runtime comes out of the box with GlassFish. However, for security
reasons, the OSGi shell has been turned off. You can however explicitly enable it either by starting GlassFish the
`asadmin` console and creating a Java system property `glassfish.osgi.start.level.final` and setting its value to `3`:

```
~/glassfish/bin$ ./asadmin
Use "exit" to exit and "help" for online help.
asadmin>
``` 

You can check the actual value of the java property (loaded from the configuration file):

```
asadmin>  list-jvm-options
...
-Dglassfish.osgi.start.level.final=2
...
```

And change the value by typing: 

```
asadmin>  create-jvm-options --target server -Dglassfish.osgi.start.level.final=3
```

The second option is to change the value in the `osgi.properties` configuration file:

```properties
# Final start level of OSGi framework. This is used by GlassFish launcher code
# to set the start level of the OSGi framework once server is up and running so that
# optional services can start. The initial start level of framework is controlled using
# the standard framework property called org.osgi.framework.startlevel.beginning
glassfish.osgi.start.level.final=3
```

You can then execute the Felix shell commands by typing `osgi <felix_command>` in the `asadmin` console. For example: 

```
asadmin> osgi lb
... list of bundles ...
```

or launching the shell using `osgi-shell` command in the admin console (the domain must be started, otherwise the osgi
shell won't launch):

```
asadmin> osgi-shell
Use "exit" to exit and "help" for online help.
gogo$
```

and execute the osgi commands directly (without the "`osgi`" prefix): 

```
gogo$ lb
... list of bundles ...
```

### WAB Example

As mentioned above, WAB is just an OSGi-fied WAR archive. Besides the usual OSGi headers it must in addition contain a
special header, Web-ContextPath, specifying the web application context path. Our WAB has (beside some other) the
following headers present in the manifest:

```manifest
Web-ContextPath: helloworld
Webapp-Context: helloworld
Bundle-ClassPath: WEB-INF/classese
```

Here, the second header is ignored by GlassFish, but may be required by other containers not fully compliant with the
OSGi Enterprise Specification mentioned above. The third manifest header worth mentioning is the Bundle-ClassPath
specifying where to find the application Java classes within the bundle archive. More about manifest headers in OSGi can
be found in the [OSGi Wiki](https://www.osgi.org/community/wiki/). 

For more detailed information on the example please see the
[WAB Example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/osgi-helloworld-webapp) source code. This
example does not package into a single war file. Instead a war and a set of additional jars is produced during the
build. See the next example to see how to deploy OSGi based Jersey application to GlassFish. 

### HTTP Service Example

---
**Note**

When deploying an OSGi HTTP Service example to GlassFish, please make sure the OSGi HTTP Service bundle is installed on
your GlassFish instance. 

---

You can directly install and activate the Jersey application bundle. In case of our example, you can either install the
example bundle stored locally (and alternatively build from Jersey sources): 

1: Build (optional)

```
examples$ cd osgi-http-service/bundle
bundle$ mvn clean package
```

2: Install into OSGi runtime: 

```
gogo$ install file:///path/to/file/bundle.jar
Bundle ID: 303
```

or install it directly from the maven repository: 

```
gogo$ install http://central.maven.org/maven2/org/glassfish/jersey/examples/osgi-http-service/bundle/<version>/bundle-<version>.jar
Bundle ID: 303
```

Make sure to replace `<version>` with an appropriate version number. Which one is appropriate depends on the specific
GlassFish 4.x version you are using. The version of the bundle cannot be higher than the version of Jersey integrated in
your GlassFish 4.x server. Jersey bundles declare dependencies on other bundles at the OSGi level and those dependencies
are version-sensitive. If you use example bundle from let's say version 2.5, but Glassfish has Jersey 2.3.1,
dependencies will not be satisfied and bundle will not start. If this happens, the error will look something like this: 

```
gogo$ lb
...
303 | Installed  |    1| jersey-examples-osgi-http-service-bundle (2.5.0.SNAPSHOT)
gogo$ start 303
 
org.osgi.framework.BundleException: Unresolved constraint in bundle
org.glassfish.jersey.examples.osgi-http-service.bundle [303]: Unable to resolve 308.0: missing requirement
[303.0] osgi.wiring.package; (&(osgi.wiring.package=org.glassfish.jersey.servlet)
(version>=2.5.0)(!(version>=3.0.0)))
 
gogo$
```

In the opposite scenario (example bundle version 2.3.1 and Glassfish Jersey version higher), everything should work
fine.

Also, if you build GlassFish from the main trunk sources and use the example from most recent Jersey release, you will
most likely be able to run the examples from the latest Jersey release, as Jersey team typically integrates all newly
released versions of Jersey immediately into GlassFish.

As a final step, start the bundle: 

```
gogo$ start 303
```

Again, the Bundle ID (in our case 303) has to be replaced by the correct one returned from the `install` command.

The example app should now be up and running. You can access it on
`http://localhost:8080/osgi/jersey-http-service/status`. Please see
[HTTP Service example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/osgi-http-service) source code for
more details on the example. 

## Other Environments

### Oracle Java Cloud Service

As Oracle Public Cloud is based on WebLogic server, the same applies as in the paragraph about WebLogic deployment (see
Section [Oracle WebLogic Server](#oracle-weblogic-server)). More on developing applications for Oracle Java Cloud
Service can be found in this
[guide](https://docs.oracle.com/cloud/131/developer_services/CSJSU/java-develop.htm#BABHDAJH).
