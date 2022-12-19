---
layout: post
title: Provider v.s. @Provider
tags: [Java]
color: rgb(8, 86, 112)
feature-img: "assets/img/post-cover/27-cover.png"
thumbnail: "assets/img/post-cover/27-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

A **Provider** is an interface defined in [JSR 330][Provider defined in JSR 330]. It is part of the _general_ dependency 
injection concept in Java. 

Whether we are no Java SR or EE, Provider will always be loadable from classpath. For example, the [HK2](HK2) is an
implementation of JSR-330 in a JavaSE environment and it conforms to the JSR 330 standard by loading the
[Provider][Provider defined in JSR 330] in its implementations.

**@Provider**, on the other hand, is a [_Java EE/Jakarta EE concept_](@Provider.java). If we are developing Java EE
applications, we can use both (Provider & @Provider) at the same time. 

[Provider defined in JSR 330]: https://qubitpi.github.io/jersey-guide/finalized/2022/06/27/jsr-330.html#provider
[HK2]: https://qubitpi.github.io/jersey-guide/finalized/2022/06/27/hk2.html
[@Provider.java]: https://github.com/jakartaee/rest/blob/master/jaxrs-api/src/main/java/jakarta/ws/rs/ext/Provider.java
