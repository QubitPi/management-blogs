---
layout: post
title: Jackson
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/8-cover.png"
thumbnail: "assets/img/post-cover/8-cover.png"
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

### Specify Jackson to Only Use Fields

Default Jackson behaviour uses both properties (getters and setters) and fields to serialize and deserialize to json. To
use the fields as the canonical source of serialization, we can do this on an individual class basis with the
annotation:

```java
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
```

To make this global, 2 approaches shall work:

#### Method 1

Configure individual ObjectMappers like this:

```java
ObjectMapper mapper  = new ObjectMapper();
mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
```

#### Method 2

If you want a way to do this globally without worrying about the configuration of your `ObjectMapper`, you can create
your own annotation:

```java
@JacksonAnnotationsInside
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public @interface JsonExplicit {
    
    // intentionally left blank
}
```

Now you just have to annotate your classes with `@JsonExplicit` and you're good to go!

## Deserialization

### Convert Empty String to Enum

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Guis {

public enum Context {
    SDS,
    NAVIGATION
}

public enum Layer {
    Library,
    Status,
    Drivingdata
}

// client code
String s = "{\"context\":\"\",\"layer\":\"Drivingdata\"}";
ObjectMapper mapper = new ObjectMapper();
Guis o = mapper.readValue(s, Guis.class);
```

This results in the error of 

```
Exception in thread "main" com.fasterxml.jackson.databind.exc.InvalidFormatException: Can not deserialize value of type cq.speech.rsi.api.Guis$Context from String "": value not one of declared Enum instance names: [SDS NAVIGATION] at [Source: {"context":"","layer":"Drivingdata"}; line: 1, column: 12] (through reference chain: cq.speech.rsi.api.Guis["context"])
```

We can use a factory that returns a null value if the string doesn't match an enum literal:

```java
public enum Context {
    SDS,
    NAVIGATION;

    @JsonCreator
    public static Context forName(String name) {
        for(Context c: values()) {
            if(c.name().equals(name)) { //change accordingly
                return c;
            }
        }

        return null;
    }
}
```

The `JsonCreator` annotation tells Jackson to call the method to get an instance for the string.

Of course the implementation can change according to your logic, but this allows the use of `null values for the enum.

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

## Handling Polymorphism

Let's take a look at Jackson polymorphic type handling annotations:

* `@JsonTypeInfo` - indicates details of what type information to include in serialization
* `@JsonSubTypes` - indicates sub-types of the annotated type
* `@JsonTypeName` - defines a logical type name to use for annotated class

In the following example, we serialize/deserialize an entity "Zoo":

```java
public class Zoo {
    
    public Animal animal;

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = As.PROPERTY, 
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat.class, name = "cat")
    })
    public static class Animal {
        public String name;
    }

    @JsonTypeName("dog")
    public static class Dog extends Animal {
        public double barkVolume;
    }

    @JsonTypeName("cat")
    public static class Cat extends Animal {
        boolean likesCream;
        public int lives;
    }
}
```

Here's what serializing the Zoo instance with the Dog will result in:

```json
{
    "animal": {
        "type": "dog",
        "name": "lacy",
        "barkVolume": 0
    }
}
```

Now for de-serialization. Let's start with the following JSON input:

```json
{
    "animal":{
        "name":"lacy",
        "type":"cat"
    }
}
```

Then let's see how that gets unmarshalled to a Zoo instance:

```java
@Test
public void whenDeserializingPolymorphic_thenCorrect() throws IOException {
    String json = "{\"animal\":{\"name\":\"lacy\",\"type\":\"cat\"}}";

    Zoo zoo = new ObjectMapper()
      .readerFor(Zoo.class)
      .readValue(json);

    assertEquals("lacy", zoo.animal.name);
    assertEquals(Zoo.Cat.class, zoo.animal.getClass());
}
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
