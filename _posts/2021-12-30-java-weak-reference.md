---
layout: post
title: Understanding Weak References
tags: [Java]
color: rgb(245, 111, 27)
feature-img: "assets/img/post-cover/22-cover.png"
thumbnail: "assets/img/post-cover/22-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}
  
## Strong References

A **strong reference** is an ordinary Java reference, the kind you use every day. For example, the code:

```java
StringBuffer buffer = new StringBuffer();
```

creates a new `StringBuffer` and stores a strong reference to it in the variable `buffer`. Yes, yes, this is kiddie
stuff, but bear with me. _The important part about strong references -- the part that makes them "strong" -- is how they
interact with the garbage collector_. Specifically, if an object is reachable via a chain of strong references (strongly
reachable), it is not eligible for garbage collection. As you don't want the garbage collector destroying objects you're
working on, this is normally exactly what you want.
