---
layout: post
title: Jersey Configuration
tags: [Configuration, Microprofile, Jakarta EE, Helidon, SmallRye]
color: rgb(214, 80, 118)
author: QubitPi
feature-img: "assets/img/pexels/design-art/2020-08-06-15-jersey-configuration/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-06-15-jersey-configuration/cover.png"
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

This post provides Jersey configuration basics which includes configuration using default configuration provider
(included in Jersey by default) using system properties, and micro-profile configuration extension which allows
plugging-in of configuration modules based on micro profile configuration specification. 

## Jersey Default Configuration Provider

Since Jersey 2.29 it is possible to turn on the ability to convert the System properties into Configuration properties.
That can be done by using the System property, too: 

    java -Djersey.config.allowSystemPropertiesProvider=true -DNAME=VALUE`
    
Note that with the security manager turned on, write access permission is required to execute `System.getProperties()`.
With insufficient permissions, the warning message is logged (with `Level.FINER`) and only
[CommonProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html),
[ClientProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ClientProperties.html),
and
[ServerProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html)
properties are used, as the property names are known and `System.getProperty(name)` method can be used, which does not
require the write access permission.

## Micro Profile Configuration Provider

[Microprofile platform](https://projects.eclipse.org/proposals/eclipse-microprofile) became very popular lately and
[Microprofile Config Specification](https://microprofile.io/project/eclipse/microprofile-config) is a recommended way in
the [Jakarta EE](https://jakarta.ee/) world to configure the specifications under the Jakarta EE umbrella.

Jersey 2.29 comes with support for Microprofile Config implementation such as [Helidon](https://helidon.io/) or
[SmallRye](https://smallrye.io/). To configure the Jersey application, the `microprofile-config.properties` file needs
to be created in the `META-INF` folder. The required properties are then simply set in the
`microprofile-config.properties`: 

    NAME=VALUE
    
Then Jersey Microprofile Config extension is needed to be added: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.microprofile</groupId>
    <artifactId>jersey-mp-config</artifactId>
    <version>2.30</scope>
</dependency>
```

And the Microprofile Config implementation, such as Helidon:

```xml
<dependency>
    <groupId>io.helidon.microprofile.config</groupId>
    <artifactId>helidon-microprofile-config</artifactId>
    <version>1.3.1</version>
</dependency>
```

Or SmallRye:

```xml
<dependency>
    <groupId>io.smallrye</groupId>
    <artifactId>smallrye-config</artifactId>
    <version>1.3.6</version>
</dependency>
```

or any other suitable Microprofile Config implementation.
