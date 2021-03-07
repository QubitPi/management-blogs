---
layout: post
title: Best Books on Java
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

*I'd like to take this moment to thank Yahoo!, a great company that fundamentally set up my tech career and my
unperishable passion to Java language*.

<!--more-->

* TOC
{:toc}

## Programming

* [Effective Java]({{ "/assets/pdf/effective-java.pdf" | relative_url}})
* [Clean Code]({{ "/assets/pdf/clean-code.pdf" | relative_url}})
* [Code Review Guidebook]({{ "/assets/pdf/review/main.pdf" | relative_url}})
* [The Pragmatic Programmer]({{ "/assets/pdf/the-pragmatic-programmer.pdf" | relative_url}})
* [Java Concurrency in Practice]({{ "/assets/pdf/java-concurrency-in-practice.pdf" | relative_url}})
* [Java Performance - The Definiteive Guide]({{ "/assets/pdf/java-performance-the-definitive-guide.pdf" | relative_url}})
* [Java Persistence with Hibernate]({{ "/assets/pdf/java-persistence-with-hibernate.pdf" | relative_url}})
* [深入理解 Java 虚拟机]({{ "/assets/pdf/深入理解Java虚拟机：JVM高级特性与最佳实践2.pdf" | relative_url}})

## References

* [Java Generics](http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html)
* [Java Practices](http://www.javapractices.com/home/HomeAction.do)
* [Understanding Weak References](https://web.archive.org/web/20061130103858/http://weblogs.java.net/blog/enicholas/archive/2006/05/understanding_w.html){: .btn .btn--primary .btn--small}

## Architecture

### [Robert C. Martin](http://cleancoder.com/products)'s Series

* Agile Software Development, Principles, Patterns, and Practices
* Clean Code: A Handbook of Agile Software Craftsmanship
* The Clean Coder: A Code Of Conduct For Professional Programmers
* Clean Architecture: A Craftsman's Guide to Software Structure and Design
* Clean Agile: Back to Basics

### Pattern-Oriented Software Architecture Series

* [Pattern-Oriented Software Architecture - Volume 1, A System of Patterns]({{ "/assets/pdf/Pattern-Oriented%20Software%20Architecture%20-%20Volume%201,%20A%20System%20of%20Patterns.pdf" | relative_url}})

### UML

* [UML Distilled A Brief Guide to the Standard Object Modeling Language](../pdfs/architecture/UML%20Distilled%20A%20Brief%20Guide%20to%20the%20Standard%20Object%20Modeling%20Language.pdf)
    - [PlantUML](../pdfs/architecture/plantuml-guide.pdf)
    - [Aggregation vs Composition](https://softwareengineering.stackexchange.com/a/61527)

#### Sequence UML Diagram

Sequence diagrams are probably the most important UML diagrams among not only the computer science community but also as
design-level models for business application development. Lately, they have become popular in depicting business
processes, because of their visually self-explanatory nature.

As the name suggests, sequence diagrams describe the sequence of messages and interactions that happen between actors
and objects. Actors or objects can be active only when needed or when another object wants to communicate with them. All
communication is represented in a chronological manner.

The best Sequence Diagram maker is [PlantUML](https://plantuml.com/):

* Define Boundary Classes, Control Classes, and Entity Classes:
  [https://stackoverflow.com/a/17028825](https://stackoverflow.com/a/17028825)

##### Usage

* Download JAR: https://plantuml.com/download
* `brew install graphviz`(mac)
* `java -jar plantuml.jar`
