---
layout: post
title: [WIP] Distributed Tracing with Spring Cloud Sleuth and Spring Cloud Zipkin
tags: [Spring, Visualization, Tracing, Jetty]
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

Put the `tar.gz` file into a location of your choice as the intsall path and extract the Jetty binary using 

    tar -czvf jetty-distribution-9.4.44.v20210927.tar.gz

## Drop the ".war" File into the Jetty "webapp"

    cd jetty-distribution-9.4.44.v20210927/webapps/
    mv /path/to/.war .

Then rename the war file to "example.war" (You see why we do this through
`jetty-distribution-9.4.44.v20210927/webapps/README.TXT`)

## Start the Webservice

    cd ../
    java -jar start.jar

(**To be continued and polished...**)
