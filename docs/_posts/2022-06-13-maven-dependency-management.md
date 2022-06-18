---
layout: post
title: Introduction to the Dependency Mechanisme
tags: [Maven]
color: rgb(126, 31, 255)
feature-img: "assets/img/post-cover/20-cover.png"
thumbnail: "assets/img/post-cover/20-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

The dependency management section is a mechanism for centralizing dependency information. When you have a set of
projects that inherit from a common parent, it's possible to put all information about the dependency in the common POM
and have simpler references to the artifacts in the child POMs. The mechanism is best illustrated through some examples. 
Given these two POMs which extend the same parent:

Project A:

```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-a</artifactId>
            <version>1.0</version>
            <exclusions>
                <exclusion>
                    <groupId>group-c</groupId>
                    <artifactId>excluded-artifact</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-b</artifactId>
            <version>1.0</version>
            <type>bar</type>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

Project B:

```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>group-c</groupId>
            <artifactId>artifact-b</artifactId>
            <version>1.0</version>
            <type>war</type>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-b</artifactId>
            <version>1.0</version>
            <type>bar</type>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

These two example POMs share a common dependency and each has one non-trivial dependency. This information can be put in 
the parent POM like this:

```xml
<project>
    ...
    <dependencyManagement>
        <dependencies>
            <dependency>
              <groupId>group-a</groupId>
              <artifactId>artifact-a</artifactId>
              <version>1.0</version>
              <exclusions>
                <exclusion>
                  <groupId>group-c</groupId>
                  <artifactId>excluded-artifact</artifactId>
                </exclusion>
              </exclusions>
            </dependency>
     
            <dependency>
                <groupId>group-c</groupId>
                <artifactId>artifact-b</artifactId>
                <version>1.0</version>
                <type>war</type>
                <scope>runtime</scope>
            </dependency>
       
            <dependency>
                <groupId>group-a</groupId>
                <artifactId>artifact-b</artifactId>
                <version>1.0</version>
                <type>bar</type>
                <scope>runtime</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

Then the two child POMs become much simpler:

```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-a</artifactId>
        </dependency>
     
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-b</artifactId>
            <!-- This is not a jar dependency, so we must specify type. -->
            <type>bar</type>
        </dependency>
    </dependencies>
</project>
```

```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>group-c</groupId>
            <artifactId>artifact-b</artifactId>
            <!-- This is not a jar dependency, so we must specify type. -->
            <type>war</type>
        </dependency>
   
        <dependency>
            <groupId>group-a</groupId>
            <artifactId>artifact-b</artifactId>
            <!-- This is not a jar dependency, so we must specify type. -->
            <type>bar</type>
        </dependency>
    </dependencies>
</project>
```

> 📋 In two of these dependency references, we had to specify the `<type/>` element. This is because the minimal set of 
> information for matching a dependency reference against a dependencyManagement section includes
>
> * groupId
> * artifactId
> * type
> * classifier
>
> In many cases, these dependencies will refer to jar artifacts with no classifier. This allows us to shorthand the 
> identity set to groupId + artifactId, since the default for the type field is jar, and the default classifier is
> `null`.
> 