---
layout: post
title: Architecture - Design Principles
tags: [Architecture, Design]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Design principles makes software maintainable and ultimately boost team efficiency

<!--more-->

* TOC
{:toc}

## SOLID Principles

Good software systems begin with clean code. On the one hand, if the bricks aren’t well made, the architecture of the
building doesn’t matter much. On the other hand, you can make a substantial mess with well-made bricks. This is where
the SOLID principles come in.

The goal of the principles is the creation of mid-level software structures that:

* Tolerate change,
* Are easy to understand, and
* Are the basis of components that can be used in many software systems.

**The term "mid-level" refers to the fact that these principles are applied by programmers working at the module
level**. They are applied just above the level of the code and help to define the kinds of software structures used
within modules and components.

### Single Responsibility Principle

> [**A class should have only one reason to change**]({{ "/assets/pdf/agile-software-development.pdf" | relative_url}})

If a class has more than one responsibility, then the responsibilities become coupled. Changes to one responsiblity may
impair or inhibit the ability of the class to meet the others.

### Architectural Implication

**A module should have one, and only one, reason to change.**

Software systems are changed to satisfy users and stakeholders; those users and stakeholders are the "reason to change"
that the principle is talking about. Indeed, we can rephrase the principle to say this:

_A module should be responsible to one, and only one, user or stakeholder._

Unfortunately, the words "user" and "stakeholder" aren’t really the right words to use here. There will likely be more
than one user or stakeholder who wants the system changed in the same way. Instead, we’re really referring to a
group—one or more people who require that change. We’ll refer to that group as an actor.

Thus the final version of the SRP is:

**A module should be responsible to one, and only one, actor.**

Note that the "module" is just a source file.

Perhaps the best way to understand this principle is by looking at the symptoms of violating it:

#### Symptom 1: Accidental Duplication

Consider the `Employee` class from a payroll application. It has three methods:

1. `calculatePay()`,
2. `reportHours()`, and
3. `save()`

This class violates the SRP because those three methods are responsible to three very different actors.

* The `calculatePay()` method is specified by the accounting department, which reports to the _CFO_.
* The `reportHours()` method is specified and used by the human resources department, which reports to the _COO_.
* The `save()` method is specified by the database administrators (DBAs), who report to the _CTO_.

By putting the source code for these three methods into a single `Employee` class, the developers have coupled each of
these actors to the others. This coupling can cause the actions of the CFO’s team to affect something that the COO’s
team depends on.

#### Symptom 2: Merges

It’s not hard to imagine that merges will be common in source files that contain many different methods. This situation
is especially likely if those methods are responsible to different actors.

For example, suppose that the CTO's team decides that there should be a simple schema change to the `Employee` table in
the database. Suppose also that the COO's team decides that they need a change in the format of the hours report. Two
different developers, possibly from two different teams, check out the `Employee` source file and begin to make changes.
Unfortunately their changes collide. The result is a merge.

There are many other symptoms that we could investigate, but they all involve multiple people changing the same source
file for different reasons.

**The way to avoid this problem is to separate code that supports different actors.**

The Single Responsibility Principle is about functions and classes - but it reappears in a different form at two more
levels. At the level of components, it becomes the Common Closure Principle. At the architectural level, it becomes the
Axis of Change responsible for the creation of Architectural Boundaries.

### Open-Closed Principle

[**Software entities (classes, modules, functions, etc) should be open for extension, but closed for modification**]({{ "/assets/pdf/agile-software-development.pdf" | relative_url}})

Modules that conform to the Open-Closed Principle have 2 attributes:

1. **Open for extension**: the behavior of the module can be extended. As the requirements of the application change, we
   are able to extend the module with new behaviors that satisfy those changes. In other words, we are able to change
   what the module does.
2. **Closed for modification**: extending the behavior of a module does not result in changes to the source or binary
  code of the module. The binary executable version of the module, such as Jave `.jar`, remains untouched
  
#### Implement OCP - Abstraction

The abstractions are abstract base classes and the unbounded group of possible behaviors is represented by all of the
derivative classes.

### Liskov Substitution Principle

[**Subtypes must be substitutable for their base types**]({{ "/assets/pdf/agile-software-development.pdf" | relative_url}})

### Interface Segregation Principle

[**Clients should not be forced to depend on methods that they do not use**]({{ "/assets/pdf/agile-software-development.pdf" | relative_url}})

### Dependency Inversion Principle

[**High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions**]({{ "/assets/pdf/agile-software-development.pdf" | relative_url}})

## Do NOT Keep State in Object

While working at Tencent, I was debugging a auto-generated business report. It says "number of some software
modification per day is 10,000 by one people". "...That is impossible...Something is wrong" was my first impression.
Luckily, I picked up some random code and saw a fatal mistake there: I made a class stateful, which is why the number
of modifications of that software goes up forever...That saved my day...

What lessons I learned from that mistake is that keeping state in object makes it an idea place for bug to live. Let's
give an example:

```java
public class CountingGenerator {

    private final count = 0;

    public void incrementCount() {
        count++;
    }

    public void getCount() {
        return count;
    }
}
```

From the name itself, it doesn't prevent people from doing something like the following:

```java
CountingGenerator counter = new CountingGenerator();

for (Group group : groups) {
    for (People people : group) {
        count.incrementCount();
    }

    System.out.println(String.format("# of people in current group is", count))
}
```

This clearly shows a stateful object makes the program above generate the wrong output. The reason for that is, as an
API user, we never know how the internal of `CountingGenerator` works. A better approach is always assumes that
people will misinterpret and make the class stateless to avoid any possible unintentional mis-usage.

## Inversion of Control

### Brief Intro

Inversion of Control is a principle in software engineering by which the control of objects or portions of a program is
transferred to a container or framework. It's most often used in the context of object-oriented programming.

By contrast with traditional programming, in which our custom code makes calls to a library, IoC enables a framework to
take control of the flow of a program and make calls to our custom code. To enable this, frameworks use abstractions
with additional behavior built in. If we want to add our own behavior, we need to extend the classes of the framework or
plugin our own classes.

The advantages of this architecture are:

* decoupling the execution of a task from its implementation
* making it easier to switch between different implementations
* greater modularity of a program
* greater ease in testing a program by isolating a component or mocking its dependencies and allowing components to
communicate through contracts

Inversion of Control can be achieved through various mechanisms such as: Strategy design pattern, Service Locator
pattern, Factory pattern, and Dependency Injection (DI).

One of the entertaining things about the enterprise Java world is the huge amount of activity in building alternatives
to the mainstream J2EE technologies, much of it happening in open source. A lot of this is a reaction to the heavyweight
complexity in the mainstream J2EE world, but much of it is also exploring alternatives and coming up with creative
ideas. A common issue to deal with is how to wire together different elements: how do you fit together this web
controller architecture with that database interface backing when they were built by different teams with little
knowledge of each other. A number of frameworks have taken a stab at this problem, and several are branching out to
provide a general capability to assemble components from different layers. These are often referred to as lightweight
containers, examples include PicoContainer, and Spring.

### A Naive Example

To help make all of this more concrete I'll use a running example to talk about all of this. Like all of my examples
it's one of those super-simple examples; small enough to be unreal, but hopefully enough for you to visualize what's
going on without falling into the bog of a real example.

In this example I'm writing a component that provides a list of movies directed by a particular director. This
stunningly useful function is implemented by a single method.

```java
public class MovieLister {

    public List<Movie> moviesDirectedBy(String director) {
        List<Movie> allMovies = finder.findAll();

        for (Iterator it = allMovies.iterator(); it.hasNext();) {
        Movie movie = (Movie) it.next();
            if (!movie.getDirector().equals(director)) {
                it.remove();
            }
        }

        return allMovies;
    }
}
```

The real point is this `finder` object, or particularly how we connect the lister object with a particular finder
object. The reason why this is interesting is that I want my wonderful `moviesDirectedBy` method to be completely
independent of how all the movies are being stored. So all the method does is refer to a `finder`, and all that `finder`
does is to know how to respond to the `findAll` method. I can bring this out by defining an interface for the `finder`.

```java
public interface MovieFinder {

    List findAll();
}
```

Now all of this is very well decoupled, but at some point I have to come up with a concrete class to actually come up
with the movies. In this case I put the code for this in the constructor of my lister class.

```java
public class MovieLister {

    private MovieFinder finder;

    public MovieLister() {
        finder = new ColonDelimitedMovieFinder("movies1.txt");
    }
}
```

Now if I'm using this class for just myself, this is all fine and dandy. But what happens when my friends are
overwhelmed by a desire for this wonderful functionality and would like a copy of my program? If they also store their
movie listings in a colon delimited text file called "movies1.txt" then everything is wonderful. If they have a
different name for their movies file, then it's easy to put the name of the file in a properties file. But what if they
have a completely different form of storing their movie listing: a SQL database, an XML file, a web service, or just
another format of text file? In this case we need a different class to grab that data. Now because I've defined a
`MovieFinder` interface, this won't alter my `moviesDirectedBy` method. But I still need to have some way to get an
instance of the right finder implementation into place.

![The dependencies using a simple creation in the lister class]({{ "/assets/img/naive.png" | relative_url}})

The figure on the left shows the dependencies for this situation. The `MovieLister` class is dependent on both the
`MovieFinder` interface and upon the implementation. We would prefer it if it were only dependent on the interface,
but then how do we make an instance to work with?

We described this situation as a Plugin. The implementation class for the `finder` isn't linked into the program at
compile time, since I don't know what my friends are going to use. Instead we want my `lister` to work with any
implementation, and for that implementation to be plugged in at some later point, out of my hands. The problem is how
can I make that link so that my lister class is ignorant of the implementation class, but can still talk to an instance
to do its work.

Expanding this into a real system, we might have dozens of such services and components. In each case we can abstract
our use of these components by talking to them through an interface (and using an adapter if the component isn't
designed with an interface in mind). But if we wish to deploy this system in different ways, we need to use plugins to
handle the interaction with these services so we can use different implementations in different deployments.

So the core problem is how do we assemble these plugins into an application? This is one of the main problems that this
new breed of lightweight containers face, and universally they all do it using Inversion of Control.

### Inversion of Control

For containers the inversion is about how they lookup a plugin implementation. In my naive example the `lister` looked
up the `finder` implementation by directly instantiating it. This stops the `finder` from being a plugin. The
approach that these containers use is to ensure that any user of a plugin follows some convention that allows a separate
assembler module to inject the implementation into the lister.

Inversion of Control can be achieved through various mechanisms such as: Strategy design pattern, *Service Locator
pattern*, Factory pattern, and *Dependency Injection (DI)*.

#### Dependency Injection

The basic idea of the Dependency Injection is to have a separate object, an **assembler**, that populates a field in the
`lister` class with an appropriate implementation for the `finder` interface, resulting in a dependency diagram
below

![The dependencies for a Dependency Injector]({{ "/assets/img/injector.png" | relative_url}})

There are three main styles of dependency injection:

* Constructor Injection

```java
public class MovieLister {

    public MovieLister(MovieFinder finder) {
        this.finder = finder;
    }
}
```

* Setter Injection, and
* Interface Injection (Factory Pattern)

#### Service Locator

The basic idea behind a service locator is to have an object that knows how to get hold of all of the services that an
application might need. So a service locator for this application would have a method that returns a movie finder when
one is needed. Of course this just shifts the burden a tad, we still have to get the locator into the lister, resulting
in the dependencies below:

![The dependencies for a Service Locator]({{ "/assets/img/locator.png" | relative_url}})

The best standalone service locator is [Google Guava](https://github.com/google/guava)
