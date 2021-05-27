---
layout: post
title: Java Functional Programming
tags: [Java, Functional Programming]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/31-cover.png"
thumbnail: "assets/img/post-cover/31-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Functional Programming Basics

Functional programming contains the following key concepts:

* Functions as first class objects
* Pure functions
* Higher order functions

Pure functional programming has a set of rules to follow too:

* No state
* No side effects
* Immutable variables
* Favour recursion over looping

Functional programming is not the right tool for every problem out there. Especially the idea of "no side effects" makes
it hard to e.g. write to a database (that is a side effect). You need to learn what problems functional programming is
good at solving, and which it is not.

## Functions as First Class Objects

In the functional programming paradigm, functions are first class objects in the language. That means that you can
create an "instance" of a function, as have a variable reference that function instance, just like a reference to a
String, Map or any other object. Functions can also be passed as parameters to other functions.

In Java, methods are not first class objects. The closest we get is Java Lambda Expressions.

## Pure Functions

A function is a pure function if:

* The execution of the function has no side effects.
* The return value of the function depends only on the input parameters passed to the function.

Here is an example of a pure function (method) in Java:

```java
public class ObjectWithPureFunction{

    public int sum(int a, int b) {
        return a + b;
    }
}
```

Contrarily, here is an example of a non-pure function:

```java
public class ObjectWithNonPureFunction{
    private int value = 0;

    public int add(int nextValue) {
        this.value += nextValue;
        return this.value;
    }
}
```

## Higher Order Functions

A function is a higher order function if at least one of the following conditions are met:

* The function takes one or more functions as parameters.
* The function returns another function as result.

For example

```java
public interface IFactory<T> {
    T create();
}
```

```java
public interface IProducer<T> {
    T produce();
}
```

```java
public interface IConfigurator<T> {
    void configure(T t);
}
```

```java
public class HigherOrderFunctionClass {

    public <T> IFactory<T> createFactory(IProducer<T> producer, IConfigurator<T> configurator) {
        return () -> {
           T instance = producer.produce();
           configurator.configure(instance);
           return instance;
        }
    }
}
```

## No State

"No state" is means no state external to the function. A function may have local variables containing temporary state
internally, but the function cannot reference any member variables of the class or object the function belongs to.

Here is an example of a function that uses no external state:

```java
public class Calculator {

    public int sum(int a, int b) {
        return a + b;
    }
}
```

Contrarily, here is an example of a function that uses external state:

```java
public class Calculator {

    private int initVal = 5;

    public int sum(int a) {
        return initVal + a;
    }
}
```

## No Side Effects

Another rule in the functional programming paradigm is that of no side effects. This means, that a function cannot
change any state outside of the function. Changing state outside of a function is referred to as a _side effect_.

State outside of a function refers both to member variables in the class, and member variables inside parameters to the
functions, or state in external systems like file systems or databases.

## Immutable Variables

A third rule in the functional programming paradigm is that of immutable variables. Immutable variables makes it easier
to avoid side effects.
