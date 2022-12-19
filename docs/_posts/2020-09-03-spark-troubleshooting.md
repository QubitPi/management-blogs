---
layout: post
title: Apache Spark - Troubleshooting
tags: [Apache Spark]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/6-cover.png"
thumbnail: "assets/img/post-cover/6-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

### Versioning Problem - `ClassNotFound/NoSuchMethod Exception`

If adding the corresponding jar to classpath doesn't help, what happened was you add a target version to your
application jar. The Spark cluster, however, also have a same package with a different version from your target version.
The classloader simply loaded the Spark-version and ignored yours.

People deploying to Hadoop-environment are familiar with the "NoClassDefinitionFound" or "NoSuchMethod" exceptions. They
know it's because a version mismatch between that they use in app and what's actually load at runtime. They are sure
what version they use for their app because they can see it in POM. To look at what version is actually loaded at
runtime, people can use the following approach:

```java
Class clazz = ClassToCheckVersionFor.class;
URL location = clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class");

LOG.debug("Loaded version is '{}'", location.toString());
```

#### Solution

Use `spark.driver.userClassPathFirst` and `spark.executor.userClassPathFirst`

### Set executor log level

To set the log level on all executors, set it inside the JVM on each worker. Run the code below to set it:

```java
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

LogManager.getRootLogger().setLevel(Level.DEBUG)
Logger LOG = LogFactory.getLog("EXECUTOR-LOG:")
```
