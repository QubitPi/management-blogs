---
layout: post
title: Spring Boot FAQ
tags: [Spring Boot, Spring]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Why Does My Spring Boot App Always Shutdown Immediately After Starting?

Suppose you have a spring boot project that packages to a JAR called `app.jar`. Running

    java -jar .../app.jar
    
succeeds but terminates immediately so that no apps are ending up running. This means the app is not a webapp because it
doesn't have an embedded container (e.g. Tomcat) on the classpath. Adding one fixed it. If you are using Maven, then add
this in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
