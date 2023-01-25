---
layout: post
title: Software Architecture
tags: [Architecture]
color: rgb(30, 159, 88)
feature-img: "assets/img/post-cover/1-cover.png"
thumbnail: "assets/img/post-cover/1-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

https://martinfowler.com/architecture/

When people in the software industry talk about "architecture", they refer to a hazily defined notion of the most
important aspects of the internal design of a software system. A good architecture is important, otherwise it becomes
slower and more expensive to add new capabilities in the future.

Good architecture is something that supports its own evolution, and is deeply intertwined with programming. What does a
architecture look like? How can teams create it? How to cultivate architectural thinking in a development organization?
This post talks about software architecture in general

<!--more-->

* TOC
{:toc}


Whether or not the forked project is already in the main (Maven Central) repository, make sure to use a different group
ID by **changing the `groupId`** in the fork, because if people don't change the `groupId`, the new library will clash
with the old one if both are attempting to push to Maven Central or the same repository.

The reality is, that once we start making changes to our local fork, it _is_ a different artifact. An artifact that is
made by our organization, not the one we took it from.

With regard to version number, if our changes are always going to be minor, I'd use the major and possibly the minor
version number of the source tree when I forked, otherwise, I'd start all over again at 1.0.0 and make a note in the
project POM regarding what version I forked it from.

For example:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.adamgent</groupId>
    <artifactId>project-xyz</artifactId> <!-- same artifact ID -->
    <version>1.0.0-RELEASE</version>
    <name>My Project XYZ</name> <!-- different display name -->
    <description>My fork of project XYZ created from v5.42.0 of original sources.</description>
    ....
```

It isn't any more difficult to switch a group ID for a dependency than to switch a version ID. Both mechanisms can be
used to easily get us the release artifact we want. However, by changing the group ID, we get the follow advantages:

- we can more easily keep track of our forks within Nexus
- we eliminate any confusion that might arise from having artifacts from an organization that they did not create
- we stay within the [standard guidelines](http://maven.apache.org/guides/mini/guide-naming-conventions.html) for Maven 
  version identifiers

> 📚 **References**
> 
> - [Original Answer](https://stackoverflow.com/a/20353758/14312712)
> - [Auxiliary Answer](https://stackoverflow.com/questions/12069546/how-to-properly-fork-a-maven-project#comment16121511_12069637)


Additional Resources
--------------------

- [Software architecture tools](https://softwarearchitecture.tools/#modelling-tools)
