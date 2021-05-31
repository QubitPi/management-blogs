---
layout: post
title: Sorting Stream
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

```java
public class User {

    private final String name;
    private final int age;
}

final List<User> users = Arrays.asList(
                  new User("C", 30),
                  new User("D", 40),
                  new User("A", 10),
                  new User("B", 20),
                  new User("E", 50));

List<User> sortedList = users.stream()
            .sorted(Comparator.comparingInt(User::getAge))
            .collect(Collectors.toList());
```