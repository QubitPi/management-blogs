---
layout: post
title: Annotations
tags: [Java]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/36-cover.png"
thumbnail: "assets/img/post-cover/36-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Annotations were first introduced in the Java language with the third edition of the Java Language Specification (1) and
first implemented in Java 5.

Using annotations, we are able to add metadata information to our source code – build or deployment information,
configuration properties, compilation behavior or quality checks.

Unlike Javadocs, annotations are strong typed, having that any annotation in use has a corresponding Annotation Type
defined in the classpath. Besides that, annotations can be defined to be available at run-time – not possible with
Javadocs.

<!--more-->

* TOC
{:toc} 

## Annotation Syntax

Annotations always appears before the annotated piece of code and by convention, usually in its own line, indented at
the same level.

Annotations may apply to packages, types (classes, interfaces, enums and annotation types), variables (class, instance
and local variables - including those defined in a for or while loop), constructors, methods and parameters.

The simplest form of an annotation is without any element included, for example:

```java
@Override()
public void theMethod() {
    ...
}
```

In this case, parentheses may be omitted:

```java
@Override
public void theMethod() {
    ...
}
```

Annotations may include elements that are just name-value pairs separated by commas. Allowed types are primitives,
strings, enums and arrays of them:

```java
@Author(
    name = "Albert",
    created = "17/09/2010",
    revision = 3,
    reviewers = {"George", "Fred"}
)
public class SimpleAnnotationsTest {

    ...
}
```

When the annotation has only one element and its name is value, it can be omitted:

```java
@WorkProduct("WP00000182")
@Complexity(ComplexityLevel.VERY_SIMPLE)
public class SimpleAnnotationsTest {
    ...
}
```

Annotations may define default values for some or all of their elements. Elements with default values can be omitted
from an annotation declaration.

For example, assuming the Annotation Type Author defines default values for revision (default is 1) and reviewers
(default is an empty String array), the following two annotation declarations are equivalent:

```java
@Author(
    name = "Albert",
    created = "17/09/2010",
    revision = 1,
    reviewers = {})
public class SimpleAnnotationsTest() {

    ...
}

@Author(
    name = "Albert",        // defaults are revision 1
    created = "17/09/2010") // and no reviewers
public class SimpleAnnotationsTest() {

    ...
}
```

## Typical Uses of Annotations

- **`@Deprecated`**: Indicates that the marked element should not be used. The compiler will generate a warning
  whenever the marked element is used. It should be used alongside the Javadoc `@deprecated`, reserving the Javadoc to
  explain the motive for deprecating the element.
- **`@Override`**: Indicates that the element is meant to override an element declared in a superclass. The compiler
  will generate a warning if it finds a marked element that it is not really overriding anything. Although it is not
  required it is useful to detect errors - for example if after creating the subclass someone else modifies the
  superclass method signature, we will be warned as soon as we rebuild the source code.
- **`@SuppressWarnings`**: Indicates to the compiler that it should suppress some specific warning that the marked
  element would otherwise produce - for example to reduce compiler "noise" because of the use of deprecated API's or
  unchecked generics operations when interacting with legacy, pre Java 5, code

Since their introduction, many libraries and frameworks have incorporated annotations into their newer releases. With
annotations used in place with source code, these libraries and frameworks have reduced, even removed, the needs for
configuration files.

Brilliant examples can be seen in:

- Java Enterprise Edition and its main components:  Enterprise JavaBeans, Java Persistence API or Web Services API.
- Spring Framework: used thoroughly for configuration, dependency injection and inversion of control in the core
  framework and in other Spring projects.
- Apache Struts 2.

## Annotation Types

Annotation Types are special interfaces in the Java language that define custom annotations.

An annotation type is defined using `@interface` instead of `interface`:

```java
public @interface Author {

    String name();
    String created();
    int revision() default 1;
    String[] reviewers() default { };
}

public @interface Complexity {

    ComplexityLevel value() default ComplexityLevel.MEDIUM;
}

public enum ComplexityLevel {

    VERY_SIMPLE,
    SIMPLE,
    MEDIUM,
    COMPLEX,
    VERY_COMPLEX;
}
```

Annotation Types have some differences compared to regular interfaces:

- Only primitives, strings, enums, class literals and arrays of them are allowed. Note that as Objects in general are
  not allowed, arrays of arrays are not allowed in Annotation Types (every array is an object).
- The annotation elements are defined with a syntax very similar to that of methods, but keep in mind that modifiers
  and parameters are not allowed.
- Default values are defined using the default keyword followed by the value that will be a literal, an array
  initializer or an enum value.

As in any other class or interface, an Enum Type can be nested in an Annotation Type definition.

```java
public @interface Complexity {
    public enum Level {
        VERY_SIMPLE, SIMPLE, MEDIUM, COMPLEX, VERY_COMPLEX;
    }
}
```

## Annotations Used to Define Annotations

The JDK comes with some annotations that are used to modify the behavior of the Annotation Types that we are defining:

- **`@Documented`**: Indicates that the marked Annotation Type should be documented by Javadoc each time it is found
  in an annotated element.
- **`@Inherited`**: Indicates that the marked Annotation Type is inherited by subclasses. This way, if the marked
  annotation is not present in a subclass it inherits the annotation in the superclass, if present. Only applies to
  class inheritance and not to interface implementations.
- **`@Retention`**: Indicates how long the marked Annotation Type will be retained. Possible values are those of enum
  RetentionPolicy: `CLASS` (default - included in class files but not accessible at run-time), `SOURCE` (discarded by
  the compiler when the class file is created) and `RUNTIME` (available at run-time).
- **`@Target`**: Indicates the element types to which the marked Annotation Type is applicable. Possible values are
  those of enum ElementType: `ANNOTATION_TYPE`, `CONSTRUCTOR`, `FIELD`, `LOCAL_VARIABLE`, `METHOD`,
  `PACKAGE`, `PARAMETER` and `TYPE`.

## Annotation Processors

Annotations are great, sure. You can set any kind of metadata or configuration with them, with a well defined syntax and
different types to use.

From what we have seen until now, annotations have advantages compared with Javadocs but not enough to justify their
inclusion into the language. Therefore, is it possible to interact with annotations and get the most from them? Sure it
is:

- At **runtime**, annotations with runtime retention policy are accessible through reflection. The methods
  `getAnnotation()` and `getAnnotations()` in `Class` class will do the magic.
- At **compile time**, Annotation Processors, a specialized type of classes, will handle the different annotations found
  in code being compiled.

### The Annotation Processor API

When Annotations were first introduced in Java 5, the Annotation Processor API was not mature or standardized. A
standalone tool named apt, the Annotation Processor Tool, was needed to process annotations, and the Mirror API, used by
apt to write custom processors, was distributed in com.sun.mirror packages.

Starting with Java 6, Annotation Processors were standardized through [JSR 269](https://jcp.org/en/jsr/detail?id=269),
incorporated into the standard libraries and the tool apt seamlessly integrated with the Java Compiler Tool, javac.

Although we will only describe in detail the new Annotation Processor API from Java 6, you can find more information
about apt and the Mirror API in JDK 5 documentation [here](http://download.oracle.com/javase/1.5.0/docs/guide/apt/)
and [here](http://download.oracle.com/javase/1.5.0/docs/guide/apt/mirror/overview-summary.html).

An annotation processor is no more than a class that implements `javax.annotation.processing.Processor` interface and
adheres to the given contract. For our convenience an abstract implementation with common functionality for custom
processors is provided in the class `javax.annotation.processing.AbstractProcessor`.

The custom processor may use three annotations to configure itself:

1. `javax.annotation.processing.SupportedAnnotationTypes`: This annotation is used to register the annotations that
   the processor supports. Valid values are fully qualified names of annotation types – wildcards are allowed.
2. `javax.annotation.processing.SupportedSourceVersion`: This annotation is used to register the source version that
   the processor supports.
3. `javax.annotation.processing.SupportedOptions`: This annotation is used to register allowed custom options that may
   be passed through the command line.

Finally, we provide our implementation of the `process()` method.

### Writing our first Annotation Processor

Let's start writing our first Annotation Processor. Following the general notes on previous section, we build the
following class to process the `Complexity` annotation introduced before:

```java
package example.annotations.processors;

import ...

@SupportedAnnotationTypes("example.annotations.Complexity")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ComplexityProcessor extends AbstractProcessor {

    public ComplexityProcessor() {
        super();
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv
    ) {
        return true;
    }
}
```

This incomplete class, although does nothing when called, is registered to support annotations of type
`example.annotations.Complexity`. Therefore, each time the Java Compiler founds a class annotated with that type will
execute the processor, given that the process is available in the classpath.

To interact with the annotated class, the `process()` method receives two parameters:

1. A set of `java.lang.model.TypeElement` objects: Annotation processing is done in one or several rounds. In each
   round the processors are called and they receive in this set the types of the annotations being processed in the
   current round.
2. A `javax.annotation.processing.RoundEnvironment` objects: This object gives access to the annotated source elements
   being processed in the current and previous round.

In addition to the two parameters, a `ProcessingEnvironment` object is available in the `processingEnv` instance
variable. This object gives access to the log and also to a few utilities.

Using the `RoundEnvironment` object and the reflective methods of the `Element` interface, we can write a simple
implementation for an annotation processor that just logs the complexity of each annotated element found:

```java
for (Element elem : roundEnv.getElementsAnnotatedWith(Complexity.class)) {
    Complexity complexity = elem.getAnnotation(Complexity.class);
    String message = "annotation found in " + elem.getSimpleName()
                   + " with complexity " + complexity.value();
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
}
return true; // no further processing of this annotation type
```
