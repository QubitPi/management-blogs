---
layout: post
title: Reading Notes - Evolutionary Database Design
tags: [Database, Design]
color: rgb(242, 146, 33)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Why Evolutionary Database Development?

Instead of trying to design your database schema up front early in the project, you instead build it up throughout the
life of a project to reflect the changing requirements defined by your stakeholders. Like it or not, requirements change
as your project progresses. Traditional approaches have denied this fundamental reality and have tried to "manage
change," a euphemism for preventing change, through various means. Practitioners of modern development techniques
instead choose to embrace change and follow techniques that enable them to evolve their work in step with evolving
requirements. Programmers have adopted techniques such as TDD, refactoring, and
[AMDD](http://www.agilemodeling.com/essays/amdd.htm) and have built new development tools to make this easy. It has also
been realized that we also need techniques and tools to support evolutionary database development.

Advantages to an evolutionary approach to database development include the following:

## Database Refactoring

You need a systematic way to refactor your code, including **good tools** and **techniques** to do so. _Most modern
integrated development environments (IDEs) now support code refactoring to some extent_, which is a good start. However,
to make refactoring work in practice, you also need to develop an **up-to-date regression-testing suite** that validates
that your code still works

You should refactor your code mercilessly because you are most productive when you are working on high-quality source
code.

A **database refactoring** is a simple change to a database schema that improves its design while retaining both its
behavioral and informational semantics. In other words, you cannot add new functionality or break existing
functionality, nor can you add new data or change the meaning of existing data. A database schema includes both 
**structural aspects**, such as table and view definitions, and **functional aspects**, such as stored procedures and
triggers.
