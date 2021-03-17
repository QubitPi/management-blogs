---
layout: post
title: How to Deal with Polymorphism in MySQL?
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Take a look at Martin Fowler's [Patterns of Enterprise Application Architecture](http://www.martinfowler.com/eaaCatalog/):

#### [Single Table Inheritance](http://www.martinfowler.com/eaaCatalog/singleTableInheritance.html)

**Represents an inheritance hierarchy of classes as a single table that has columns for all the fields of the various
classes**.

For a full description see
[Patterns of Enterprise Application Architecture - Martin Fowler](../../java/pdfs/architecture/Patterns%20of%20Enterprise%20Application%20Architecture%20-%20Martin%20Fowler.pdf)
page 278

![Single Table Inheritance Example Diagram]({{ "/assets/img/single-table-inheritance.png" | relative_url}})

Relational databases don't support inheritance, so when mapping from objects to databases we have to consider how to
represent our nice inheritance structures in relational tables. When mapping to a relational database, we try to
minimize the joins that can quickly mount up when processing an inheritance structure in multiple tables. Single Table
Inheritance maps all fields of all classes of an inheritance structure into a single table.

#### [Class Table Inheritance](http://www.martinfowler.com/eaaCatalog/classTableInheritance.html)

**Represents an inheritance hierarchy of classes with one table for each class**.

For a full description see [Patterns of Enterprise Application Architecture - Martin Fowler](../../java/pdfs/architecture/Patterns%20of%20Enterprise%20Application%20Architecture%20-%20Martin%20Fowler.pdf)
page 285

![Single Table Inheritance Example Diagram]({{ "/assets/img/class-table-inheritance.png" | relative_url}})

A very visible aspect of the object-relational mismatch is the fact that relational databases don't support inheritance.
You want database structures that map clearly to the objects and allow links anywhere in the inheritance structure.
Class Table Inheritance supports this by using one database table per class in the inheritance structure.

#### [Concrete Table Inheritance](http://www.martinfowler.com/eaaCatalog/concreteTableInheritance.html)

**Represents an inheritance hierarchy of classes with one table per concrete class in the hierarchy**.

For a full description see
[Patterns of Enterprise Application Architecture - Martin Fowler](../../java/pdfs/architecture/Patterns%20of%20Enterprise%20Application%20Architecture%20-%20Martin%20Fowler.pdf)
page 293

![Concrete Table Inheritance]({{ "/assets/img/concrete-table-inheritance.png" | relative_url}})

As any object purist will tell you, relational databases don't support inheritance - a fact that complicates
object-relational mapping. Thinking of tables from an object instance point of view, a sensible route is to take each
object in memory and map it to a single database row. This implies Concrete Table Inheritance, where there's a table for
each concrete class in the inheritance hierarchy.
