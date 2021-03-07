Design principles makes software maintainable and ultimately boost team efficiency



## SOLID Principles

### [Single responsibility principle](https://en.wikipedia.org/wiki/Single-responsibility_principle)

A class should only have a single responsibility, that is, only changes to one part of the software's specification
should be able to affect the specification of the class.

### [Open-closed principle](https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle)

"Software entities ... should be open for extension, but closed for modification."

### [Liskov substitution principle](https://en.wikipedia.org/wiki/Liskov_substitution_principle)

"Objects in a program should be replaceable with instances of their subtypes without altering the correctness of that
program." See also [design by contract](https://en.wikipedia.org/wiki/Design_by_contract).

### [Interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle)

"Many client-specific interfaces are better than one general-purpose interface."

### [Dependency inversion principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle)

One should "depend upon abstractions, [not] concretions."

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

![The dependencies using a simple creation in the lister class](../images/naive.png)

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

![The dependencies for a Dependency Injector](../images/injector.png)

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

![The dependencies for a Service Locator](../images/locator.png)

The best standalone service locator is [Google Guava](https://github.com/google/guava)
