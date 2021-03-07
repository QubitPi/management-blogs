---
layout: post
title: Jackson
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Serialization

### Serializing Private Fields with No Getters

Serializing such objects often results in error like "... No serializer found ...". This is because the default
configuration of an `ObjectMapper` instance works for fields that are public or have public getters/setters. Instead
of changing the class definition by providing a public getter/setter, one could choose to specify
(to the underlying `VisibilityChecker`) a different property visibility rule. Jackson 1.9 provides the
`ObjectMapper.setVisibility()` for doing so. For the example:

```java
OBJECT_MAPPER.setVisibility(JsonMethod.FIELD, Visibility.ANY);
```

For Jackson >2.0:

```java
OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
```

## Deserialization

### Deserialise an Array of Objects

```java
private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

...

List<MyClass> myObjects = JSON_MAPPER.readValue(jsonInput, new TypeReference<List<MyClass>>(){});
```

### Create an `ObjectNode` from JSON String

```java
ObjectNode json = new ObjectMapper().readValue("{}", ObjectNode.class);
```
