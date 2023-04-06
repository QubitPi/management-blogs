---
layout: post
title: TestDouble
tags: [Software Testing]
color: rgb(240, 91, 161)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
authors: [martin-fowler]
excerpt_separator: <!--more-->
---

Test Double is a generic term for any case where we replace a production object for testing purposes. 

<!--more-->

* TOC
{:toc}

There are various kinds of double that Gerard lists:

- **Dummy objects** are passed around but never actually used. Usually they are just used to fill parameter lists.
- **Fake objects** actually have working implementations, but usually take some shortcut which makes them not suitable
  for production (an _InMemoryTestDatabase_ is a good example).
- **Stubs** provide canned answers to calls made during the test, usually not responding at all to anything outside
  what's programmed in for the test.
- **Spies** are stubs that also record some information based on how they were called. One form of this might be an
  email service that records how many messages it was sent.
- [**Mocks**](https://martinfowler.com/articles/mocksArentStubs.html) are pre-programmed with expectations which form a 
  specification of the calls they are expected to receive. They can throw an exception if they receive a call they don't 
  expect and are checked during verification to ensure they got all the calls they were expecting.
