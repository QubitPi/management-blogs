---
layout: post
title: Cycle Detection using MySQL
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/northern-lights.png"
thumbnail: "assets/img/pexels/design-art/northern-lights.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Why Are We Talking About Nasty Algorithm?

I hate algorithm, not because algorithm is horrible in general; it simply isn't a nice thing to think about in my life.
So let's start with Big Data, something that captures my interests instead.

### Big Data Processing

Within a very short period of time, Apache Hadoop, an open source implementation of Google's
[MapReduce paper](https://research.google/pubs/pub62/) and [Google File System](https://research.google/pubs/pub51/),
has become the de facto platform for processing and storing big data.

Higher-level domain-specific languages (DSL) implemented on top of Hadoop's Map-Reduce, such as
[Pig](http://pig.apache.org/) and [Hive](http://hive.apache.org/), quickly followed, making it simpler to write
applications running on Hadoop.

#### A Recurrent Problem

Hadoop, Pig, Hive, and many other projects provide the foundation for storing and processing large amounts of data in an
efficient way. Most of the time, it is not possible to perform all required processing with a single MapReduce, Pig, or
Hive job.

Multiple MapReduce, Pig, or Hive jobs often need to be chained together, producing and consuming intermediate data and
coordinating their flow of execution.

At Yahoo!, as developers started doing more complex processing using Hadoop, multistage Hadoop jobs became common. This
led to several ad hoc solutions to manage the execution and interdependency of these multiple Hadoop jobs. Not only does
Hadoop community face such problem, but business solution in general also have challenges maintaining a correct
executing inter-dependent jobs. [Tez](https://tez.apache.org/), for example, detects job dependency cycles:

 



but the whole graph is in memory(one-time config)

This good given that we are validating a "one-time-config" task graph. 



## Database Schema

```
mysql> DESC object_dependency;
+----------------------+------------+------+-----+---------+----------------+
| Field                | Type       | Null | Key | Default | Extra          |
+----------------------+------------+------+-----+---------+----------------+
| id                   | int(11)    | NO   | PRI | NULL    | auto_increment |
| object_id            | int(11)    | NO   |     | NULL    |                |
| dependency_object_id | int(11)    | NO   |     | NULL    |                |
| is_direct_dependency | tinyint(1) | NO   |     | NULL    |                |
+----------------------+------------+------+-----+---------+----------------+
```

```
CREATE TABLE `object_dependency` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `object_id` INT NOT NULL,
    `dependency_object_id` INT NOT NULL,
    `is_direct_dependency` BOOLEAN NOT NULL,
    PRIMARY KEY (`id`)
);

```


```
+----------------+                                       +----------------+
|                |                                       |                |
|    Object 1    |                                       |    Object 4    |
|                |-----+                            +---->                |
| object_id = 11 |     |      +----------------+    |    | object_id = 14 |
|                |     |      |                |    |    |                |
+----------------+     |      |    Object 3    |    |    +----------------+
                       +------>                |----+                      
+----------------+     |      | object_id = 13 |    |    +----------------+
|                |     |      |                |    |    |                |
|    Object 2    |     |      +----------------+    |    |    Object 5    |
|                |-----+                            +---->                |
| object_id = 12 |                                       | object_id = 15 |
|                |                                       |                |
+----------------+                                       +----------------+
```

```
mysql> SELECT * FROM object_dependency;
+----+-----------+----------------------+----------------------+
| id | object_id | dependency_object_id | is_direct_dependency |
+----+-----------+----------------------+----------------------+
|  1 |        13 |                   11 |                    1 |
|  2 |        13 |                   12 |                    1 |
|  3 |        14 |                   13 |                    1 |
|  4 |        14 |                   11 |                    0 |
|  5 |        14 |                   12 |                    0 |
|  6 |        15 |                   13 |                    1 |
|  7 |        15 |                   11 |                    0 |
|  8 |        15 |                   12 |                    0 |
+----+-----------+----------------------+----------------------+
```

## 

