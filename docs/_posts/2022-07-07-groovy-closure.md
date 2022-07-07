---
layout: post
title: What are Groovy Closures?
tags: [Groovy, Testing]
color: rgb(220, 73, 0)
feature-img: "assets/img/post-cover/36-cover.png"
thumbnail: "assets/img/post-cover/36-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

A closure in Groovy is an open, anonymous, block of code that can take arguments, return a value and be assigned to a 
variable. A closure may reference variables declared in its surrounding scope. In opposition to the formal definition of
a closure, Closure in the Groovy language can also contain free variables which are defined outside of its surrounding 
scope. While breaking the formal concept of a closure, it offers a variety of advantages which are described in this 
chapter.

<!--more-->

* TOC
{:toc}

Syntax
------

### Defining a Closure

A closure definition follows this syntax:

```groovy
{ [closureParameters -> ] statements }
```

Where `[closureParameters->]` is an optional comma-delimited list of parameters, and `statements` are 0 or more Groovy 
statements. The parameters look similar to a method parameter list, and these parameters may be typed or untyped.

When a parameter list is specified, the `->` character is required and serves to separate the arguments from the closure 
body. The _statements_ portion consists of 0, 1, or many Groovy statements.

Some examples of valid closure definitions:

```groovy
{ item++ } // A closure referencing a variable named 'item"

{ -> item++ } // It is possible to explicitly separate closure parameters from code by adding an arrow (->)

{ println it } // A closure using an implicit parameter (it)

{ it -> println it } // An alternative version where "it" is an explicit parameter

{ name -> println name } // In that case it is often better to use an explicit name for the parameter

{ String x, int y -> println "hey ${x} the value is ${y}" } // A closure accepting two typed parameters

// A closure can contain multiple statements
{ reader ->
    def line = reader.readLine()
    line.trim()
}
```

### Closures as an Object

A closure is an instance of the `groovy.lang.Closure` class, making it assignable to a variable or a field as any other 
variable, despite being a block of code:

```groovy
// You can assign a closure to a variable, and it is an instance of "groovy.lang.Closure""  
def listener = { e -> println "Clicked on $e.source" }
assert listener instanceof Closure

Closure callback = { println 'Done!' } // If not using "def" or "var", use "groovy.lang.Closure" as the type  

// 	Optionally, you can specify the return type of the closure by using the generic type of "groovy.lang.Closure"
Closure<Boolean> isTextFile = { File it -> it.name.endsWith('.txt') }
```

### Calling a Closure

A closure, as an anonymous block of code, can be called like any other method. If you define a closure which takes no 
argument like this:

```groovy
def code = { 123 }
```

Then the code inside the closure will only be executed when you _call_ the closure, which can be done by using the
variable as if it was a regular method:

```groovy
assert code() == 123
```

Alternatively, you can be explicit and use the `call` method:

```groovy
assert code.call() == 123
```

The principle is the same if the closure accepts arguments:

```groovy
def isOdd = { int i -> i%2 != 0 } // define a closure which accepts an int as a parameter

assert isOdd(3) == true // it can be called directly

assert isOdd.call(2) == false // or using the call method                            

def isEven = { it%2 == 0 } // same goes for a closure with an implicit argument ("it")

assert isEven(3) == false // which can be called directly using ("arg")

assert isEven.call(2) == true // or using "call"
```

Unlike a method, **a closure always returns a value when called**.
