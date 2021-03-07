---
layout: post
title: PriorityQueue
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Java Concurrency is a term that covers multithreading, concurrency, and parallelism. That includes the Java concurrency
tools, problems and solutions.

<!--more-->

* TOC
{:toc}

## How PriorityQueue Handles Both Natural Ordering and Comparator Ordering in One API Object

This is a brilliant strategy:

```java
private void siftDown(int k, E x) {
    if (comparator != null)
        siftDownUsingComparator(k, x);
    else
        siftDownComparable(k, x);
}
```

Take away: In Java, sorted collections orders by either natural ordering(Comparable) or Comparator. When implementing
such collections, PriorityQueue explicitly classifies them using the method above.

**This approach, however, looses the advantage of compile-time check**.

## PriorityQueue supports 2 kinds of API objects - Max and Min Heap

One smart implementation about PriorityQueue is that it supports both max and min heap in a single API object by using a
[Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html):

- Client passes a Comparator to decide if they want a max or min heap
- When no Comparator is provided, natural ordering is used

**Take away: A good way to avoid inheritance is to see if the differentiated behavior could be achieved by simply adding
a new state to the class.**

## Internal Comparator has a Lower Type Bound

Why would there be a lower bound of

```java
private final Comparator<? super E> comparator;
```

Consider a class hierarchy where a the topmost superclass implements an instantiation of the generic Comparable interface.

```java
class Person implements Comparable<Person> {
    ...
}

class Student extends Person {
    ...
}
```

Note, the Student class does not and cannot implement Comparable<Student> , because it would be a subtype of two
different instantiations of the same generic type then, and that is illegal(see below).

Consider also a method that tries to sort a sequence of subtype objects, such as a List<Student>.

```java
class Utilities {

    public static <T extends Comparable<T>> void sort(List<T> list) {
        ...
    }
    ...
}
```

This sort method cannot be applied to a list of students.

```java
List<Student> list = new ArrayList<Student>();
...
Utilities.sort(list);      // error
```

The reason for the error message is that the compiler infers the type parameter of the sort method as ``T:=Student`` and
that class ``Student`` is not ``Comparable<Student>``. It is ``Comparable<Person>``, but that does not meet the
requirements imposed by the bound of the type parameter of method sort.  It is required that ``T`` (i.e. Student ) is
``Comparable<T>`` (i.e. ``Comparable<Student>`` ), which in fact it is not.

In order to make the sort method applicable to a list of subtypes we would have to use a wildcard with a lower bound,
like in the re-engineered version of the sort method below.

```java
class Utilities {

    public static <T extends Comparable <? super T > > void sort(List<T> list) {
        ...
    }
}
```

Now, we can sort a list of students, because students are comparable to a supertype of `Student`, namely `Person`.

Reference - http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ204

**Take away - Although Generics give us great help on compile-time check, it also posts a lot of extra cares needed. For
example, when we declare a type of `Foo<T>`, we will need to decide, in the class implementation, things like "should it
be `<T>` or `<? super T>` or `<? extends T>`"**

_Can a class implement different instantiations of the same generic interface?_ **No, a type must not directly
or indirectly derive from two different instantiations of the same generic interface**. The reason for this restriction
is the translation by type erasure. After type erasure the different instantiations of the same generic interface
collapse to the same raw type.  At runtime there is no distinction between the different instantiations any longer.

```java

    class X implements Comparable<X> , Comparable<String> {  // error
        public int compareTo(X arg)    { ... }
        public int compareTo(String arg) { ... }
    }
```

During type erasure the compiler would not only remove the type arguments of the two instantiations of Comparable , it
would also try to create the necessary bridge methods:

```java
class X implements Comparable , Comparable {

    public int compareTo(X arg) {
        ...
    }

    public int compareTo(String arg) {
        ...
    }

    public int compareTo(Object arg) {
        return compareTo((X)arg);
    }

    public int compareTo(Object arg) {
        return compareTo((String)arg);
    }
}
```

The bridge method generation mechanism cannot handle this.

## Why is DEFAULT_INITIAL_CAPACITY 11?

A lots of online research explains about 11 in hash table, but PriorityQueue has nothing to do with hashing, it should
have something to do with the growth of internal array. Given that the value of 11 could simply be random such as
copy&paste from somewhere else, a simple benchmarking program shows some empirical evidence on the justification of 11:

```java
for (int i = 1; i <= 100; i++) {
    PriorityQueue<Integer> pq = new PriorityQueue<>(i);


    long start = System.nanoTime();
    for (int j = 0; j < 100; j++) {
        pq.add(new Random().nextInt(100));
    }
    long finish = System.nanoTime();
    System.out.println(String.valueOf(finish - start));
}
```

When inserting 100 random numbers:

![100]({{ "/assets/img/100-elements.png" | relative_url}})

When inserting 1000000 random numbers:

![1000000]({{ "/assets/img/1000000-elements.png" | relative_url}})

This shows that while it makes no big difference on large dataset, small datasets with about 11 as initial size gives
the optimal speed+memory performance

Notes taken while reading https://docs.oracle.com/javase/tutorial/java/generics/index.html
