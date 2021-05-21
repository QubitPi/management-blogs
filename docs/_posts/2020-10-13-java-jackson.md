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

## Troubleshooting

### [No serializer found for class ...](https://www.baeldung.com/jackson-jsonmappingexception)

By default, Jackson 2 will only work with fields that are either public, or have a public getter methods, i.e.
serializing an entity that has all fields private or package private will fail:

The obvious solution is to add getters for the fields - if the entity is under our control. If that is not the case and
modifying the source of the entity is not possible - then Jackson provides us with a few alternatives.

#### **Globally** Auto Detect Fields With Any Visibility

```java
objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
```

This will allow the private and package private fields to be detected without getters, and serialization will work
correctly

#### Detected All Fields at the **Class Level**

```java
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class someClass { ... }
```

### [How to Add Multiple JsonInclude Annotation Type Using JsonInclude?](https://stackoverflow.com/questions/23631511/jackson-jsoninclude-how-to-add-multiple-jsoninclude-annotation-type)

How can I tell a class to include only NON_EMPTY and NON_NULL values only, Using

```java
@JsonInclude(Include.NON_NULL)
@JsonInclude(Include.NON_EMPTY)
public class Foo {
    String a;
}
```

which shows up as having error of duplicate annotation.
                  
"Null is always considered empty" - [Jackson's site](http://static.javadoc.io/com.fasterxml.jackson.core/jackson-annotations/2.7.1/com/fasterxml/jackson/annotation/JsonInclude.Include.html#NON_EMPTY)

So the NON_EMPTY rule covers both cases.
