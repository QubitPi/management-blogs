---
layout: post
title: Useful RestAssured Syntax
tags: [Java, Software Testing]
color: rgb(16, 155, 45)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
  {:toc}

Match JSON Ignoring Order
-------------------------

Use RestAssured's JsonPath to parse the JSON file into a Map and then compare it with Hamcrest Matchers. This way the
order etc didn't matter.

```java
import static org.hamcrest.Matchers.equalTo;
import io.restassured.path.json.JsonPath;

...

JsonPath expectedJson = new JsonPath(new File("/path/to/expected.json"));

given()
    ...
    .then()
    .body("", equalTo(expectedJson.getMap("")));
```
