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
