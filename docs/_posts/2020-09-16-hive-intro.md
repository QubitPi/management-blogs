---
layout: post
title: Hive Intro
tags: [Hive, Hadoop]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/18-cover.png"
thumbnail: "assets/img/post-cover/18-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## What is Hive?

Apache Hive is an application that abstracts Hadoop data so that it can be accessed using an SQL-like language called
HiveQL. Using HiveQL, you can use a familiar procedural language to query large amounts of data on the grid as if you
were working with a relational database.

Hive offers a broad range of SQL semantics and integrates with both ODBC and JDBC interfaces, making it ideal for
analyzing data.

## Why Hive?

Hive is one of the fastest growing products for many reasons:

- **Battle-Tested Standard** - Hive is the SQL standard for Hadoop that has been around for many years.
- **Single Solution** - Hive works across a broad spectrum of data volumes and allows you to load, store, read, analyze,
  and combine data.
- **Interoperability** - HCatalog is a metastore that is part of Hive project. It allows Hive to work with Pig and
  HBase. Hive is also one of the few SQL solutions on Hadoop that has been widely certified by business intelligence
  (BI) vendors (10+ major vendors and growing).
- **Strong Support** - Hive is a top-level project (TLP) with the Apache Software Foundation (ASF). The Hive community
  is comprised of top-notch engineers and architects from well-known IT companies.
- **Improving Performance** - With each release, Hive's performance improves and is closing the gap with Pig. This
  performance increase is due to the changes in Hive and the underlying execution engine that Hive 0.13 and 0.14 run
  on Tez. There is even more reason to be optimistic for improved performance due to the
  [Stinger initiative](http://hortonworks.com/labs/stinger/), which is a broad, community-based effort to improve
  future versions of Hive.

## Accessing Hive

The diagram shows how a query made from the Hive CLI is transmitted to Hive, where it is translated into a MapReduce job
that is run by Hadoop. Client applications use the ODBC/JDBC drivers to communicate with HiveServer2 to relay queries
that like the queries from the CLI are converted into a MapReduce job that is executed on Hadoop.

![Accessing Hive Diagram]({{ "/assets/img/accessing_hive_services.png" | relative_url}})

## Hive vs. Pig
============

The table below shows the difference between Hive and Pig, highlighting when and where each should be used, respective
features, and available support.

|                         | Hive                                                             | Pig                                                                       |
|-------------------------|------------------------------------------------------------------|---------------------------------------------------------------------------|
| Where to Use            | Ad-hoc analytics and reporting                                   | ETL and pipeline data processing                                          |
| Language                | SQL (declarative)                                                | PigLatin (procedural)                                                     |
| Schema/Types            | Mandatory (implicit)                                             | Optional (explicit)                                                       |
| Partitions              | Yes                                                              | No, partition pruning with HCatalog                                       |
| Complex Processing      | Not a good fit for complex processing                            | Well suited where multi-query works with thousands of lines of Pig script |
| Client/Server           | Requires metastore server (HCatalog) and data registered with it | Client only. Works with HCatalog metastore                                |
| ODBC/JDBC               | Yes, through HiveServer2                                         | No                                                                        |
| Tez Support             | Present and stable from Hive 0.13                                | Tez support under development (Pig 0.14)                                  |
| ORC/Vectorization       | ORC and vectorization available                                  | ORC available with Pig 0.14, no vectorization yet                         |
| Transactions            | Yes (coming soon)                                                | No                                                                        |
| Cost-Based Optimization | Yes (coming soon)                                                | No                                                                        |

## When to Use Hive vs. HBase 

While it is reasonable to compare Pig and Hive, HBase and Hive serve very different purposes in the Hadoop ecosystem.
The table below highlights the differences and when you would consider using each.

|              | Hive                                                                                                                                                                                                                     | HBase                                                                                                                                                                                                  |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Where to Use | - Data warehousing and analytics on top of Hadoop/HDFS- <br />- Does not fit frequent and/or record-level updates (although support is getting added for ACID transactions)<br />- Query and analyze large volumes of data | - Distributed key-value store for persistence and random access on HDFS<br />- Build to support ten's of thousands of reads/ writes per second at record level<br />- Store and access values using keys |
| Access       | Primarily through Hive SQL                                                                                                                                                                                               | Java and REST APIs                                                                                                                                                                                     |
| SQL          | Getting close to SQL standards                                                                                                                                                                                           | Through Hive or Phoenix (SQL Skin on HBase) (not supported)                                                                                                                                            |
| Integration  | - Integrated with Pig through HCatalog<br />- Integrated with Oozie through support for Hive action and HCatalog partition notifications                                                                                   | - Integrated with Hive for SQL support<br />- Integrated with Pig (HBaseStorage) and Oozie (credential support)                                                                                          |

## Data Model

Hive data is organized into databases, tables, partitions, and buckets. Those familiar with SQL will be familiar with
databases that use a namespace to organize a group of tables and tables that have a schema defining column data.
Partitions allow you to create virtual columns based on keys that determine how data is stored. Users can identify rows
of data with partitions to run queries on instead of running the queries across an entire data set. Buckets allow you to
split partitions, allowing even more focused queries Skewed tables, like partitions, allow you to focus queries on a
subset of the data set by splitting the data into separate files so that certain files can be skipped when executing a
query.

The diagram below gives the general hierarchy of the data model and a general characteristic of each level. See
[Data Units](https://cwiki.apache.org/confluence/display/Hive/Tutorial#Tutorial-DataUnits) for more detailed
information.

![Data Model in Hive]({{ "/assets/img/data_model.png" | relative_url}})

## Hive and HCatalog

[HCatalog](https://cwiki.apache.org/confluence/display/Hive/HCatalog), part of Hive project, is the central metastore
for facilitating interoperability among various Hadoop tools. It not only acts as the table and storage management
layer, so Pig, MapReduce, and Hive can share data, but also presents a relational view of the data in HDFS, abstracts
where or in what format data is stored, and enables notifications of data availability.

![Hive and HCatalog]({{ "/assets/img/hive_hcatalog.png" | relative_url}})

## HiveServer2

[HiveServer2](https://cwiki.apache.org/confluence/display/Hive/Setting%20Up%20HiveServer2#SettingUpHiveServer2-HiveServer2)
is the JDBC/ODBC endpoint that Hive clients can use to communicate with Hive.

It supports the following:

* concurrent clients
* secure clusters and encryption
* user/global/session configuration
* DoAs support allowing Hive queries to run as the requester

![HiveServer2]({{ "/assets/img/hiveserver2.png" | relative_url}})
