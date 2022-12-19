---
layout: post
title: What are Groovy Closures?
tags: [Groovy, Testing]
color: rgb(220, 73, 0)
feature-img: "assets/img/post-cover/36-cover.png"
thumbnail: "assets/img/post-cover/36-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

A closure in Groovy is an open, anonymous, block of code that can take arguments, return a value and be assigned to a 
variable. A closure may reference variables declared in its surrounding scope. In opposition to the formal definition of
a closure, Closure in the Groovy language can also contain free variables which are defined outside of its surrounding 
scope. While breaking the formal concept of a closure, it offers a variety of advantages which are described in this 
post.

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

Parameters
----------

### Normal Parameters

Parameters of closures follow the same principle as parameters of regular methods:

* an optional type
* a name
* an optional default value

Parameters are separated with commas:

```groovy
def closureWithOneArg = { str -> str.toUpperCase() }
assert closureWithOneArg('groovy') == 'GROOVY'

def closureWithOneArgAndExplicitType = { String str -> str.toUpperCase() }
assert closureWithOneArgAndExplicitType('groovy') == 'GROOVY'

def closureWithTwoArgs = { a,b -> a+b }
assert closureWithTwoArgs(1,2) == 3

def closureWithTwoArgsAndExplicitTypes = { int a, int b -> a+b }
assert closureWithTwoArgsAndExplicitTypes(1,2) == 3

def closureWithTwoArgsAndOptionalTypes = { a, int b -> a+b }
assert closureWithTwoArgsAndOptionalTypes(1,2) == 3

def closureWithTwoArgAndDefaultValue = { int a, int b=2 -> a+b }
assert closureWithTwoArgAndDefaultValue(1) == 3
```

### Implicit Parameter

**When a closure does not explicitly define a parameter list (using `->`), a closure always defines an implicit
parameter, named `it`**. This means that the following

```groovy
def greeting = { "Hello, $it!" }
assert greeting('Patrick') == 'Hello, Patrick!'
```

is stricly equivalent to

```groovy
def greeting = { it -> "Hello, $it!" }
assert greeting('Patrick') == 'Hello, Patrick!'
```

If you want to declare a closure which accepts no argument and must be restricted to calls without arguments, then you 
must declare it with an **explicit empty argument list**:

```groovy
def magicNumber = { -> 42 }

// this call will fail because the closure doesn't accept any argument
magicNumber(11)
```

### Varargs

It is possible for a closure to declare variable arguments like any other method. **Vargs methods** are methods that can 
accept a variable number of arguments if the last parameter is of variable length (or an array) like in the next
examples:

```groovy
def concat1 = { String... args -> args.join('') }
assert concat1('abc','def') == 'abcdef'

def concat2 = { String[] args -> args.join('') }            
assert concat2('abc', 'def') == 'abcdef'

def multiConcat = { int n, String... args ->                
    args.join('')*n
}
assert multiConcat(2, 'abc','def') == 'abcdefabcdef'
```

Delegation
----------

A closure actually defines 3 concepts:

1. [**`this`**](#this) - corresponds to the enclosing class where the closure is defined
2. [**`owner`**](#owner) - corresponds to the enclosing object where the closure is defined, which may be either a class 
   or a closure
3. [**`delegate`**](#delegate) - corresponds to a third party object where methods calls or properties are resolved 
   whenever the receiver of the message is not defined

### this

In a closure, there is a pre-define method called `getThisObject()` which returns the enclosing class where the closure
is defined. It is equivalent to using an explicit this:

```groovy
class Enclosing {
    void run() {
        def whatIsThisObject = { getThisObject() }          
        assert whatIsThisObject() == this                   
        def whatIsThis = { this }                           
        assert whatIsThis() == this                         
    }
}

class EnclosedInInnerClass {
    class Inner {
        Closure cl = { this }                               
    }
    void run() {
        def inner = new Inner()
        assert inner.cl() == inner                          
    }
}

class NestedClosures {
    void run() {
        def nestedClosures = {
            def cl = { this }                               
            cl()
        }
        assert nestedClosures() == this                     
    }
}
```

It is of course possible to call methods from the enclosing class this way:

```groovy
class Person {
    String name
    int age
    String toString() { "$name is $age years old" }

    String dump() {
        def cl = {
            String msg = this.toString()               
            println msg
            msg
        }
        cl()
    }
}
def p = new Person(name:'Janice', age:74)
assert p.dump() == 'Janice is 74 years old'
```

### owner

The owner is similar to [this](#this) except that it returns the _direct_ enclosing object, either a closure or a class:

```groovy
class Enclosing {
    void run() {
        def whatIsOwnerMethod = { getOwner() }               
        assert whatIsOwnerMethod() == this                   
        def whatIsOwner = { owner }                          
        assert whatIsOwner() == this                         
    }
}

class EnclosedInInnerClass {
    class Inner {
        Closure cl = { owner }                               
    }
    void run() {
        def inner = new Inner()
        assert inner.cl() == inner                           
    }
}

class NestedClosures {
    void run() {
        def nestedClosures = {
            def cl = { owner }                               
            cl()
        }
        assert nestedClosures() == nestedClosures            
    }
}
```

Note that in the third class `NestedClosures`,  nestedClosures() no longer equals to `this`, but its direct enclosing
object `nestedClosures` instead.

### delegate

While [this](#this) and [owner](#owner) refer to the lexical scope of a closure, the delegate is a user defined object 
that a closure will use. By default, the delegate is set to `owner`:

The delegate of a closure can be changed to any object. Let’s illustrate this by creating two classes, both of which 
define a property called name:

```groovy
class Person {
    String name
}
class Thing {
    String name
}

def p = new Person(name: 'Norman')
def t = new Thing(name: 'Teapot')
```

Then let's define a closure which fetches the `name` property on delegate:

```groovy
def upperCasedName = { delegate.name.toUpperCase() }
```

Then by changing the delegate of the closure, you can see that the target object will change:

```groovy
upperCasedName.delegate = p
assert upperCasedName() == 'NORMAN'
upperCasedName.delegate = t
assert upperCasedName() == 'TEAPOT'
```

In this case, `p` and `t` are delegates

At this point, the behavior is not different from having a `target` variable defined in the lexical scope of the
closure:

```groovy
def target = p
def upperCasedNameUsingVar = { target.name.toUpperCase() }
assert upperCasedNameUsingVar() == 'NORMAN'
```

### Delegation Strategy

Whenever, in a closure, a property is accessed without explicitly setting a receiver object, then a delegation strategy
is involved. For example

```groovy
class Person {
    String name
}
def p = new Person(name:'Igor')
def cl = { name.toUpperCase() }                 
cl.delegate = p                                 
assert cl() == 'IGOR'
```

The reason this code works is that the `name` property will be resolved transparently on the delegate object. We call
this behavior a **delegation strategy**. This is a very powerful way to resolve properties or method calls inside 
closures. There's no need to set an explicit delegate. The call will be made because the default delegation strategy of 
the closure makes it so. A closure actually defines multiple resolution strategies that you can choose:

* **`Closure.OWNER_FIRST`** is the default strategy. If a property/method exists on the owner, then it will be called on 
  the owner. If not, then the delegate is used.
* **`Closure.DELEGATE_FIRST` reverses the logic: the delegate is used first, then the owner
* **`Closure.OWNER_ONLY`** will only resolve the property/method lookup on the owner: the delegate will be ignored.
* **`Closure.DELEGATE_ONLY`** will only resolve the property/method lookup on the delegate: the owner will be ignored.
* **`Closure.TO_SELF`** can be used by developers who need advanced meta-programming techniques and wish to implement a custom resolution strategy: the resolution will not be made on the owner or the delegate but only on the closure class itself. It only makes sense to use this if you implement your own subclass of Closure.

To change the resolution strategy of a closure

```groovy
someClosure = Closure.DELEGATE_FIRST
```

> Note that if no delegates are found when `*_ONLY` strategy is activated, `groovy.lang.MissingPropertyException` will
be thrown.

Closures in GStrings
--------------------

> A GString is a string where you put variable to make it "dynamic". For example, "hello there ${user} how are you?"

Take the following code:

```groovy
def x = 1
def gs = "x = ${x}"
assert gs == 'x = 1'
```

The code behaves as you would expect, but what happens if you add:

```groovy
x = 2
assert gs == 'x = 2' // failed
```

You will see that the assert fails. The reasons are

1. a GString only evaluates lazily the toString representation of values
2. the syntax `${x}` in a GString does not represent a closure but an expression to `$x`, evaluated when the GString is 
   created.

In our example, the GString is created with an expression referencing x. When the GString is created, the value of x is
1, so the GString is created with a value of 1. When the assert is triggered, the GString is evaluated and 1 is
converted to a String using toString. When we change x to 2, we did change the value of x, but it is a different object, 
and the GString still references the old one.

A GString will only change its toString representation if the values it references are mutating. If the references
change, nothing will happen.

If you need a real closure in a GString and for example enforce lazy evaluation of variables, you need to use the 
alternate syntax `${-> x}` like in the fixed example:

```groovy
def x = 1
def gs = "x = ${-> x}"
assert gs == 'x = 1'

x = 2
assert gs == 'x = 2'
```

Closure in Functional Programming
---------------------------------

Closures, like
[lambda expressions in Java 8](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) are at the
core of the functional programming paradigm in Groovy. Some functional programming operations on functions are available 
directly on the `Closure` class

### Currying

In Groovy, currying refers to the concept of partial application. It does not correspond to the real concept of currying 
in functional programming because of the different scoping rules that Groovy applies on closures. Currying in Groovy
will let you set the value of one parameter of a closure, and it will return a new closure accepting one less argument.

#### Left Currying

Left currying is the fact of setting the left-most parameter of a closure, like in this example:

```groovy
def nCopies = { int n, String str -> str*n }    
def twice = nCopies.curry(2)                    
assert twice('bla') == 'blabla'                 
assert twice('bla') == nCopies(2, 'bla') 
```

#### Right Currying

Similarily to left currying, it is possible to set the right-most parameter of a closure:

```groovy
def nCopies = { int n, String str -> str*n }    
def blah = nCopies.rcurry('bla')                
assert blah(2) == 'blabla'                      
assert blah(2) == nCopies(2, 'bla')
```

#### Index-Based Currying

In case a closure accepts more than 2 parameters, it is possible to set an arbitrary parameter using ncurry:

```groovy
def volume = { double l, double w, double h -> l*w*h }      
def fixedWidthVolume = volume.ncurry(1, 2d)                 
assert volume(3d, 2d, 4d) == fixedWidthVolume(3d, 4d)       
def fixedWidthAndHeight = volume.ncurry(1, 2d, 4d)          
assert volume(3d, 2d, 4d) == fixedWidthAndHeight(3d)   
```

### Memoization

Memoization allows the result of the call of a closure to be cached. It is interesting if the computation done by a 
function (closure) is slow, but you know that this function is going to be called often with the same arguments. A
typical example is the Fibonacci suite. A naive implementation may look like this:

```groovy
def fib
fib = { long n -> n<2?n:fib(n-1)+fib(n-2) }
assert fib(15) == 610 // slow!
```

It is a naive implementation because 'fib' is often called recursively with the same arguments, leading to an
exponential algorithm.

This naive implementation can be "fixed" by caching the result of calls using memoize:

```groovy
fib = { long n -> n<2?n:fib(n-1)+fib(n-2) }.memoize()
assert fib(25) == 75025 // fast!
```

The behavior of the cache can be tweaked using alternate methods:

* `memoizeAtMost` will generate a new closure which caches at most n values
* `memoizeAtLeast` will generate a new closure which caches at least n values
* `memoizeBetween` will generate a new closure which caches at least n values and at most n values

The cache used in all memoize variants is a LRU cache.

### Composition

Closure composition corresponds to the concept of function composition, that is to say creating a new function by 
composing two or more functions (chaining calls), as illustrated in this example:

```groovy
def plus2  = { it + 2 }
def times3 = { it * 3 }

def times3plus2 = plus2 << times3
assert times3plus2(3) == 11
assert times3plus2(4) == plus2(times3(4))

def plus2times3 = times3 << plus2
assert plus2times3(3) == 15
assert plus2times3(5) == times3(plus2(5))

// reverse composition
assert times3plus2(3) == (times3 >> plus2)(3)
```

### Trampoline

Recursive algorithms are often restricted by a physical limit: the maximum stack height.

An approach that helps in those situations is by using Closure and its trampoline capability.

Closures are wrapped in a **`TrampolineClosure`**. Upon calling, a trampolined Closure will call the original Closure 
waiting for its result. If the outcome of the call is another instance of a TrampolineClosure, created perhaps as a
result to a call to the trampoline() method, the Closure will again be invoked. This repetitive invocation of returned 
trampolined Closures instances will continue until a value other than a trampolined Closure is returned. That value will 
become the final result of the trampoline. That way, calls are made serially, rather than filling the stack.

Here's an example of the use of trampoline() to implement the factorial function:

```groovy
def factorial
factorial = { int n, def accu = 1G ->
    if (n < 2) return accu
    factorial.trampoline(n - 1, n * accu)
}
factorial = factorial.trampoline()

assert factorial(1)    == 1
assert factorial(3)    == 1 * 2 * 3
assert factorial(1000) // == 402387260.. plus another 2560 digits
```
