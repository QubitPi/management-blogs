---
layout: post
title: Jersey CDI Container Agnostic Support
tags: [CDI, Request Scope]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Containers Known to Work With Jersey CDI Support

To stick with JAX-RS specification, Jersey has to support JAX-RS/[CDI](https://javaee.github.io/tutorial/cdi-basic.html)
integration in Java EE environment. The two containers supporting JAX-RS/CDI integration out of the box are
***Oracle GlassFish*** and ***Oracle WebLogic*** application server.

Apache Tomcat is another Servlet container that is known to work fine with Jersey CDI support. However, things do not
work there out of the box. You need to enable CDI support in Tomcat e.g. using Weld.
[Jersey CDI](https://github.com/eclipse-ee4j/jersey/tree/master/examples/cdi-webapp) example shows how a WAR application
could be packaged (see `tomcat-packaging` profile in the `pom` file) in order to enable JAX-RS/CDI integration in Tomcat
with Jersey using Weld.

If not bundled already with underlying Servlet container, the following Jersey module needs to be packaged with the
application or otherwise included in the container class-path:

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.cdi</groupId>
    <artifactId>jersey-cdi1x</artifactId>
    <version>2.31</version>
</dependency>
```

## Request Scope Binding

There is a common pattern for all above mentioned containers. Jersey CDI integration builds upon existing CDI/Servlet
integration there. In other words, in all above cases, ***Jersey application must be deployed as a Servlet***, where the
underlying Servlet container has CDI integrated already and CDI container bootstrapped properly.

The key feature in CDI/Servlet integration is proper request scope binding. If this feature was missing, you would not
be able to use any request scoped CDI beans in your Jersey application. To make Jersey work with CDI in containers that
do not have request scope binding resolved, some extra work is required. 

To allow smooth integration with Jersey request scope a new SPI,
[ExternalRequestScope](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/spi/ExternalRequestScope.html),
was introduced in Jersey version 2.15. An SPI implementation should be registered via the standard META-INF/services
mechanism and needs to make sure CDI implentation request scope has been properly managed and request scoped data kept
in the right context. For performance reasons, at most a single external request scope provider is allowed by Jersey
runtime. 

## Jersey Weld SE Support

The extra work to align HTTP request with CDI request scope was already done by Jersey team for Weld 2.x implementation.
In order to utilize Jersey/Weld request scope binding, you need to use the following module:

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.cdi</groupId>
    <artifactId>jersey-weld2-se</artifactId>
    <version>2.31</version>
</dependency>
```

Then you could use your CDI backed JAX-RS components in a Jersey application running in Grizzly HTTP container
bootstrapped as follows:

```java
Weld weld = new Weld();
weld.initialize();
 
final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
        URI.create("http://localhost:8080/weld/"),
        jerseyResourceConfig
);
 
// ...
 
server.shutdownNow();
weld.shutdown();
```

The above pattern could be applied also for other Jersey supported HTTP containers as long as you stick with CDI Weld
2.x implementation. You simply add the above mentioned `jersey-weld2-se` module into you class-path and bootstrap the
Weld container manually before starting the HTTP container. 
