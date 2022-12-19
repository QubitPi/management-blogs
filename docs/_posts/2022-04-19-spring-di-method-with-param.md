---
layout: post
title: Spring - Dependency injection in @Bean method parameters
tags: [Spring Boot, Dependency Injection]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/34-cover.png"
thumbnail: "assets/img/post-cover/34-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

In a `@Configuration` class, the methods annotated with `@Bean` may depend on other beans to initialize themselves.
Other beans should be annotated with `@Bean` as well to be registered with the Spring container.

Spring provides a mechanism where we can pass such bean dependencies with `@Bean` method parameters. They are injected
by the framework just like an
[arbitrary method's dependencies are resolved](https://www.logicbig.com/tutorials/spring-framework/spring-core/using-autowired-annotation-on-arbitrary-methods.html)

There are following scenarios:

![Error loading bean-params.png!]({{ "/assets/img/bean-params.png" | relative_url}})

## Injecting by Type

If there's only one bean instance available to be injected to the injection target point then it will be injected
successfully by type.

## Injecting by Name

If there are more than one instance of the same type available for a target injection point then there's a conflict
(ambiguity). Spring doesn't know which particular instance to be injected in that case. If the name of parameter is the
same as bean's definition method (the method annotated with `@Bean`) name then the dependency is resolved by name.

The bean's definition method can provide a different name than the method name by using `@Bean(name = ...)`, the
injection point method's parameter name should match in that case as well.

## Injecting by Bean's Name with Matching @Qualifier

If there's an ambiguity then it can also be resolved if the injection point method parameter add a `@Qualifier`
annotation with matching target bean's name.

## Injecting by Matching @Qualifiers

Ambiguity can also be resolved by using `@Qualifier` on the both sides. This is important when a bean provider method
has already indented to be exposed as a `@Qualifier` per business logic sense, so that a particular bean's
implementation can be changed without updating all injection points.
