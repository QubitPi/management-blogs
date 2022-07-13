---
layout: post
title: The Difference Between Mocks and Stubs
tags: [Testing]
color: rgb(240, 91, 161)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

There are several definitions of objects, that are not real. The general term is test double. This term encompasses:
dummy, fake, stub, mock.

<!--more-->

* TOC
{:toc}

When they were first introduced, many people easily confused mock objects with the common testing notion of using stubs. 
A problem of unit testing is that to make a single unit work, you often need other units. For this post I'm going to
follow the vocabulary of [Gerard Meszaros's book](https://martinfowler.com/books/meszaros.html). It's not what everyone 
uses, but I think it's a good vocabulary and since it's my essay I get to pick which words to use.

Meszaros uses the term **Test Double** as the generic term for any kind of pretend object used in place of a real object 
for testing purposes. The name comes from the notion of a Stunt Double in movies. (One of his aims was to avoid using any 
name that was already widely used.) Meszaros then defined five particular kinds of double:

* **Dummy** objects are passed around but never actually used. Usually they are just used to fill parameter lists.
* **Fake** objects actually have working implementations, but usually take some shortcut which makes them not suitable for 
  production (an in [memory database](https://martinfowler.com/bliki/InMemoryTestDatabase.html) is a good example).
* **Stubs** provide canned answers to calls made during the test, usually not responding at all to anything outside
  what's programmed in for the test.
* **Spies** are stubs that also _record some information based on how they were called_. One form of this might be an 
  email service that records how many messages it was sent.
* **Mocks** are objects pre-programmed with expectations which form a specification of the calls they are expected to 
  receive.

Of these kinds of doubles, only mocks insist upon behavior verification. The other doubles can, and usually do, use
state verification. Mocks actually do behave like other doubles during the exercise phase, as they need to make the SUT 
(System under Test) believe it's talking with its real collaborators - but mocks differ in the setup and the
verification phases.
