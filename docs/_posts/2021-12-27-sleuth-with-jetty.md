---
layout: post
title: Distributed Tracing with Spring Cloud Sleuth and Spring Cloud Zipkin
tags: [Spring, Visualization, Tracing, Jetty, Sleuth, Zipkin]
category: FINALIZED
color: rgb(252, 57, 13)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}
  
## Create a Deployable War File

> 📋 The source code in this post is in
> https://github.com/QubitPi/jersey-guide/tree/master/docs/assets/src/sleuth-with-open-feign

The first step in producing a deployable war file is to provide a `SpringBootServletInitializer` subclass and override
its `configure` method. Doing so makes use of Spring Framework's servlet 3.0 support and lets you configure your
application when it is launched by the servlet container. Typically, you should update your application's main class to
extend `SpringBootServletInitializer`, as shown in the following example:

```java
package com.github.QubitPi.spring.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ServerMicroserviceApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(ServerMicroserviceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerMicroserviceApplication.class, args);
    }
}
```

The next step is to update your build configuration such that your project produces a war file rather than a jar file.
If you use Maven and `spring-boot-starter-parent` (which configures Maven's war plugin for you), all you need to do is
to modify pom.xml to change the packaging to war, as follows:

```xml
<packaging>war</packaging>
```

The final step in the process is to ensure that the embedded servlet container does not interfere with the servlet
container to which the war file is deployed. To do so, you need to mark the embedded servlet container dependency as
being provided.

If you use Maven, the following example marks the servlet container (Tomcat, in this case) as being provided:

```xml
<dependencies>
    <!-- ... -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
        <scope>provided</scope>
    </dependency>
    <!-- ... -->
</dependencies>
```

## Build the ".war" File

    mvn clean package

## Install Jetty

### Download Jetty

At [download page](https://www.eclipse.org/jetty/download.php), pick up a `.tgz` distribution, we will use
"9.4.44.v20210927" release as an example:

![Error loading download-jetty.png]({{ "/assets/img/download-jetty.png" | relative_url}})

### Install Jetty

Put the `tar.gz` file into a location of your choice as the install path and extract the Jetty binary using 

    tar -czvf jetty-distribution-9.4.44.v20210927.tar.gz

## Drop the ".war" File into the Jetty "webapp"

    cd jetty-distribution-9.4.44.v20210927/webapps/
    mv /path/to/.war .

Then rename the war file to "ROOT.war", the reason of which is so that the context path would be root context - `/`,
which is a common industry standard.

> 📋 Setting a Context Path
> 
> The context path is the prefix of a URL path that is used to select the context(s) to which an incoming request is
> passed. Typically a URL in a Java servlet server is of the format
> "http://hostname.com/contextPath/servletPath/pathInfo", where each of the path elements can be zero or more "/"
> separated elements. If there is no context path, the context is referred to as the **root context**. The root context
> must be configured as "/" but is reported as the empty string by the servlet
> [API `getContextPath()` method](https://www.eclipse.org/jetty/).
> 
> How we set the context path depends on how we deploy the web application (or `ContextHandler`). In this case, we
> configure the context path by **naming convention**:
> 
> If a web application is deployed using the WebAppProvider of the DeploymentManager without an XML IoC file, then **the
> name of the WAR file is used to set the context path**:
>
> * If the WAR file is named "myapp.war", then the context will be deployed with a context path of `/myapp`
> * **If the WAR file is named "ROOT.WAR" (or any case insensitive variation), then the context will be deployed with a
>   context path of `/`**
> If the WAR file is named "ROOT-foobar.war" (or any case insensitive variation), then the context will be deployed with
> a context path of / and a
> [virtual host](https://www.eclipse.org/jetty/documentation/jetty-9/index.html#configuring-virtual-hosts) of "foobar"

## Start the Webservice

    cd ../
    java -jar start.jar

> 📋 To specify the port that container exposes for our app, we could use
> 
> ```bash
> java -jar start.jar -Djetty.port=8081
> ```

## Firing The First Request

Open up a browser and hit "http://localhost:8081/greeting-client/get-greeting"，then in the Jetty log we will see

![Error loading sleuth-trace-id-example.png]({{ "/assets/img/sleuth-trace-id-exampley.png" | relative_url}})

## Install Zipkin

[Zipkin](https://zipkin.io/) is a very efficient tool for distributed tracing in the microservices ecosystem.
Distributed tracing, in general, is the latency measurement of each component in a distributed transaction where
multiple microservices are invoked to serve a single business usecase.

Distributed tracing is useful during debugging when lots of underlying systems are involved and the application becomes
slow in any particular situation. In such cases, we first need to identify which underlying service is actually slow.
Once the slow service is identified, we can work to fix that issue. Distributed tracing helps in identifying that slow
component in the ecosystem.

Zipkin was originally developed at Twitter, based on a concept of a Google paper that described Google's
internally-built distributed app debugger - [dapper](https://research.google/pubs/pub36356/). It manages both the
collection and lookup of this data. To use Zipkin, applications are instrumented to report timing data to it.

If you are troubleshooting latency problems or errors in an ecosystem, you can filter or sort all traces based on the
application, length of trace, annotation, or timestamp. By analyzing these traces, you can decide which components are
not performing as per expectations, and you can fix them.

Zipkin has 4 modules

1. **Collector** - Once any component sends the trace data, it arrives to Zipkin collector daemon. Here _the trace data
   is validated, stored, and indexed for lookups by the Zipkin collector_.
2. **Storage** - This module store and index the lookup data in backend. Cassandra, ElasticSearch and MySQL are
   supported.
3. **Search** - This module provides a simple JSON API for finding and retrieving traces stored in backend. The primary
   consumer of this API is the Web UI.
4. **Web UI** - A very nice UI interface for viewing traces.

[Installing and standing up a Zipkin instance](https://zipkin.io/pages/quickstart.html) is very easy:

    curl -sSL https://zipkin.io/quickstart.sh | bash -s
    java -jar zipkin.jar

```
$ java -jar zipkin.jar

                  oo
                 oooo
                oooooo
               oooooooo
              oooooooooo
             oooooooooooo
           ooooooo  ooooooo
          oooooo     ooooooo
         oooooo       ooooooo
        oooooo   o  o   oooooo
       oooooo   oo  oo   oooooo
     ooooooo  oooo  oooo  ooooooo
    oooooo   ooooo  ooooo  ooooooo
   oooooo   oooooo  oooooo  ooooooo
  oooooooo      oo  oo      oooooooo
  ooooooooooooo oo  oo ooooooooooooo
      oooooooooooo  oooooooooooo
          oooooooo  oooooooo
              oooo  oooo

     ________ ____  _  _____ _   _
    |__  /_ _|  _ \| |/ /_ _| \ | |
      / / | || |_) | ' / | ||  \| |
     / /_ | ||  __/| . \ | || |\  |
    |____|___|_|   |_|\_\___|_| \_|

:: version 2.23.16 :: commit b90f2b3 ::

2022-01-07 19:46:44.577  INFO [/] 2705 --- [oss-http-*:9411] c.l.a.s.Server: Serving HTTP at /0:0:0:0:0:0:0:0:9411 - http://127.0.0.1:9411/
```

The command above starts the Zipkin server with the default configuration.

Hitting the "http://127.0.0.1:9411", we will see

![Error loading zipkin-initial.png]({{ "/assets/img/zipkin-initial.png" | relative_url}})

### Integrating Zipkin into Spring Sleuth

To install Zipkin in the spring boot application, we need to add
[Zipkin starter dependency](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-zipkin):

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

Now firing "http://localhost:8081/greeting-client/get-greeting" again, we will see 

![Error loading zipkin-example.png]({{ "/assets/img/zipkin-example.png" | relative_url}})
