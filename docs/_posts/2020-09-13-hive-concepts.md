---
layout: post
title: Hive Concepts
tags: [Hive, Hadoop]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Hive Data Model

Data in Hive is organized into:

* **Tables** - These are analogous to Tables in Relational Databases. Tables can be filtered, projected, joined and
  unioned. Additionally **all the data of a table is stored in a directory in HDFS**. Hive also supports the notion of
  external tables wherein a table can be created on prexisting files or directories in HDFS by providing the appropriate
  location to the table creation DDL. The rows in a table are organized into typed columns similar to Relational
  Databases.
* **Partitions** - Each Table can have one or more partition keys which determine how the data is stored, for example a
  table `T` with a date partition column `ds` had files with data for a particular date stored in the
  `<table location>/ds=<date>` **directory** in HDFS. Partitions allow the system to prune data to be inspected based on
  query predicates, for example a query that is interested in rows from `T` that satisfy the predicate
  `T.ds = '2008-09-01'` would only have to look at files in `<table location>/ds=2008-09-01/` directory in HDFS.
* **Buckets** - Data in each partition may in turn be divided into Buckets based on the hash of a column in the table.
  Each bucket is stored as a **file** in the partition directory. Bucketing allows the system to efficiently evaluate
  queries that depend on a sample of data (these are queries that use the `SAMPLE` clause on the table).

## Partition

Each Table can have one or more partition Keys which determines how the data is stored. Partitions - apart from being
storage units - also allow the user to efficiently identify the rows that satisfy a specified criteria; for example, a
date_partition of type STRING and country_partition of type STRING. Each unique value of the partition keys defines a
partition of the Table. For example, all "US" data from "2009-12-23" is a partition of the page_views table. Therefore,
if you run analysis on only the "US" data for 2009-12-23, you can run that query only on the relevant partition of the
table, thereby speeding up the analysis significantly. Note however, that just because a partition is named 2009-12-23
does not mean that it contains all or only data from that date; partitions are named after dates for convenience; it is
the user's job to guarantee the relationship between partition name and data content! Partition columns are virtual
columns, they are not part of the data itself but are derived on load.

### How to Partitioned Tables

Partitioned tables can be created using the `PARTITIONED BY` clause. A table can have one or more partition columns and
a separate data directory is created for each distinct value combination in the partition columns. Further, tables or
partitions can be bucketed using `CLUSTERED BY` columns, and data can be sorted within that bucket via `SORT BY`
columns. This can improve performance on certain kinds of queries.

If, when creating a partitioned table, you get this error: "FAILED: Error in semantic analysis: Column repeated in
partitioning columns," it means you are trying to include the partitioned column in the data of the table itself. You
probably really do have the column defined. However, the partition you create makes a pseudo-column on which you can
query, so you must rename your table column to something else (that users should not query on!).

For example, suppose your original unpartitioned table had three columns: id, date, and name:

    id     int,
    date   date,
    name   varchar

Now you want to partition on date. Your Hive definition could use "dtDontQuery" as a column name so that "date" can be
used for partitioning (and querying).

    create table table_name (
        id                int,
        dtDontQuery       string,
        name              string
    ) partitioned by (date string)

Now your users will still query on "`where date = '...'`" but the second column `dtDontQuery` will hold the original
values.
