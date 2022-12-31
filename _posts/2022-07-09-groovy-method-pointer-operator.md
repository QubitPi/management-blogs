---
layout: post
title: Groovy Method Pointer Operator
tags: [Groovy, Testing]
color: rgb(220, 73, 0)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

It is often practical to be able to use a regular method as a closure. For example, you might want to use the currying 
abilities of a closure, but those are not available to normal methods. In Groovy, you can obtain a closure from any
method with the method pointer operator.

<!--more-->

* TOC
{:toc}

The method pointer operator (.&) can be used to store a reference to a method in a variable, in order to call it later:

```groovy
def str = 'example of method reference'            
def fun = str.&toUpperCase                         
def upper = fun()                                  
assert upper == str.toUpperCase()           
```

In the example above, we store a reference to the `toUpperCase` method on the `str` instance inside a variable named
`fun`, which can be called like a regular method. we can check that the result is the same as if we had called it
directly on `str`

The type of such a method pointer is a `groovy.lang.Closure`, so it can be used in any place a closure would be used.
For example

```groovy
def transform(List elements, Closure action) { 
    def result = []
    elements.each {
        result << action(it)
    }
    result
}

String describe(Person p) {                                       
    "$p.name is $p.age"
}

def action = this.&describe                                       
def list = [
    new Person(name: 'Bob',   age: 42),
    new Person(name: 'Julia', age: 35)
]                           
assert transform(list, action) == ['Bob is 42', 'Julia is 35']
```

Although method pointers are defined at compile-time. Arguments, however, are resolved at runtime. For instance

```groovy
def doSomething(String str) { str.toUpperCase() }    
def doSomething(Integer x) { 2*x }
def reference = this.&doSomething
assert reference('foo') == 'FOO'
assert reference(123)   == 246
```

To align with Java 8 method reference expectations, in Groovy 3 and above, we can use `new` as the method name to obtain
a method pointer to the constructor:

```groovy
def foo = BigInteger.&new
def fortyTwo = foo('42')
assert fortyTwo == 42G
```

Wou can obtain a method pointer to an instance method of a class. This method pointer takes an additional parameter as
the receiver instance to invoke the method on:

```groovy
def instanceMethod = String.&toUpperCase
assert instanceMethod('foo') == 'FOO'
```
