---
layout: post
title: Groovy Closure to Type Coercion
tags: [Groovy, Testing]
category: FINALIZED
color: rgb(220, 73, 0)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Closures can be converted into interfaces or single-abstract method types.

<!--more-->

* TOC
{:toc}

Assigning a Closure to a SAM Type
---------------------------------

A SAM type is a type which defines a single abstract method. This includes:

* Functional interfaces

```groovy
interface Predicate<T> {
    boolean accept(T obj)
}
```

* Abstract classes with single abstract method

```groovy
abstract class Greeter {
    abstract String getName()
    void greet() {
        println "Hello, $name"
    }
}
```

Any closure can be converted into a SAM type using the `as` operator:

```groovy
Predicate filter = { it.contains 'G' } as Predicate
assert filter.accept('Groovy') == true

Greeter greeter = { 'Groovy' } as Greeter
greeter.greet()
```

However, the `as Type` expression is optional since Groovy 2.2.0. We can omit it and simply write:

```groovy
Predicate filter = { it.contains 'G' }
assert filter.accept('Groovy') == true

Greeter greeter = { 'Groovy' }
greeter.greet()
```

With `as Type` gone, we are also allowed to use method pointers, as shown in the following example:

```groovy
boolean doFilter(String s) { s.contains('G') }

Predicate filter = this.&doFilter
assert filter.accept('Groovy') == true

Greeter greeter = GroovySystem.&getVersion
greeter.greet()
```

Closure to Arbitrary Type Coercion
----------------------------------

In addition to SAM types, a closure can be coerced to any type and in particular interfaces. Let’s define the following 
interface:

```groovy
interface FooBar {
    int foo()
    void bar()
}
```

You can coerce a closure into the interface using the `as` keyword:

```groovy
def impl = { println 'ok'; 123 } as FooBar
```

This produces a class for which all methods are implemented using the closure:

```groovy
assert impl.foo() == 123
impl.bar()
```

But it is also possible to coerce a closure to any class. For example

```groovy
class FooBar {
    int foo() { 1 }
    void bar() { println 'bar' }
}

def impl = { println 'ok'; 123 } as FooBar

assert impl.foo() == 123
impl.bar()
```
