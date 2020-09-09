---
layout: post
title: Modules and dependencies
tags: [Dependency, POM]
feature-img: "assets/img/pexels/design-art/2020-07-24-2-modules-and-dependencies/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-07-24-2-modules-and-dependencies/cover.png"
color: green
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Java SE Compatibility

2.x branch:

* Until version 2.6, Jersey was compiled with Java SE 6. This has changed in Jersey 2.7.
* Up to version 2.25.x almost all Jersey components are compiled with Java SE 7 target. It means, that you will need at
  least Java SE 7 to be able to compile and run your application that is using latest Jersey. Only `core-common` and
  `core-client` modules are still compiled with Java class version runnable with Java SE 6.
* Since Jersey 2.26, all modules are build using Java SE 8 and there is no support for running it on older Java SE
  distributions.
* Since Jersey 2.29, all modules can be built using Java SE 11 and all distributed modules provide support for Java SE
  8+ (9,11).
  
## Introduction to Jersey Dependencies

Jersey is built, assembled and installed using [Apache Maven](https://maven.apache.org/). Non-snapshot Jersey releases
are deployed to the [Central Maven Repository](https://search.maven.org/). Jersey is also being deployed to
[Sonatype Maven repositories](https://oss.sonatype.org/), which contain also Jersey SNAPSHOT versions. In case you would
want to test the latest development builds check out the Sonatype Snapshots Maven repository. 

An application that uses Jersey and depends on Jersey modules is in turn required to also include in the application
dependencies the set of 3rd party modules that Jersey modules depend on. Jersey is designed as a pluggable component
architecture and different applications can therefore require different sets of Jersey modules. This also means that the
set of external Jersey dependencies required to be included in the application dependencies may vary in each application
based on the Jersey modules that are being used by the application.

Developers using Maven or a Maven-aware build system in their projects are likely to find it easier to include and
manage dependencies of their applications compared to developers using ant or other build systems that are not
compatible with Maven. This post will explain to both maven and non-maven developers how to depend on Jersey modules in
their application. Ant developers are likely to find the
[Ant Tasks for Maven](https://maven.apache.org/ant-tasks/index.html) very useful. 

## Common Jersey Use Cases

### Servlet Based Application on Glassfish

If you are using Glassfish application server, you don't need to package anything with your application, everything is
already included. You just need to declare (provided) dependency on JAX-RS API to be able to compile your application.

```xml
<dependency>
    <groupId>jakarta.ws.rs</groupId>
    <artifactId>jakarta.ws.rs-api</artifactId>
    <version>2.1.6</version>
    <scope>provided</scope>
</dependency>
```

If you are using any Jersey specific feature, you will need to depend on Jersey directly.

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet</artifactId>
    <version>2.31</version>
    <scope>provided</scope>
</dependency>
<!-- if you are using Jersey client specific features without the server side -->
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.31</version>
    <scope>provided</scope>
</dependency>
```

### Servlet Based Server-Side Application

Following dependencies apply to application server (servlet containers) without any integrated JAX-RS implementation.
Then application needs to include JAX-RS API and Jersey implementation in deployed application.

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <!-- if your container implements Servlet API older than 3.0, use "jersey-container-servlet-core"  -->
    <artifactId>jersey-container-servlet</artifactId>
    <version>2.31</version>
</dependency>
<!-- Required only when you are using JAX-RS Client -->
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.31</version>
</dependency>
```

### Client Application on JDK

Applications running on plain JDK using only client part of JAX-RS specification need to depend only on client. There
are various additional modules which can be added, like for example grizzly or apache or jetty connector (see
dependencies snipped below). Jersey client runs by default with plain JDK (using `HttpUrlConnection`). See post
[Client API](https://qubitpi.github.io/jersey-guide/2020/07/27/5-client-api.html) for more details. 

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.31</version>
</dependency>
```

Currently available connectors:

```xml
<dependency>
    <groupId>org.glassfish.jersey.connectors</groupId>
    <artifactId>jersey-grizzly-connector</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.connectors</groupId>
    <artifactId>jersey-apache-connector</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.connectors</groupId>
    <artifactId>jersey-jetty-connector</artifactId>
    <version>2.31</version>
</dependency>
```

### Server-Side Application on Supported Containers

Apart for a standard JAX-RS Servlet-based deployment that works with any Servlet container that supports Servlet 2.5 and
higher, Jersey provides support for programmatic deployment to the following containers:

* Grizzly 2 (HTTP and Servlet)
* JDK Http server
* Simple Http server
* Jetty Http server.

This post presents only required maven dependencies, more information can be found in post
[Application Deployment and Runtime Environments](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html).

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-grizzly2-http</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-grizzly2-servlet</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-jdk-http</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-simple-http</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-jetty-http</artifactId>
    <version>2.31</version>
</dependency>
 
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-jetty-servlet</artifactId>
    <version>2.31</version>
</dependency>
```