---
layout: post
title: HBase and Alternatives
tags: [HBase, Hadoop]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/31-cover.png"
thumbnail: "assets/img/post-cover/31-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc} 

### Introduction

Apache HBase is an open source, distributed, versioned, column-oriented store modeled after Google's
[BigTable](http://en.wikipedia.org/wiki/BigTable): A Distributed Storage System for Structured Data by Chang et al.
Just as BigTable leverages the distributed data storage provided by the
[Google File System](http://en.wikipedia.org/wiki/Google_File_System), Apache HBase provides Bigtable-like
capabilities on top of Hadoop and the
[Hadoop distributed file system (HDFS)](http://en.wikipedia.org/wiki/HDFS#Hadoop_distributed_file_system).

Although HBase provides a relational database view over data (row and columns), it does not support SQL. Instead of
[Google's Chubby lock service](http://en.wikipedia.org/wiki/Distributed_lock_manager#Google.27s_Chubby_lock_service),
HBase uses [Apache ZooKeeper](http://en.wikipedia.org/wiki/Apache_ZooKeeper) as the coordination service. The following
description is for both HBase and BigTable unless otherwise specified. The HBase terms are used as default and BigTable
corresponding names are mentioned in bracket.

### Features

The following are the notable features of HBase:

- Linear and modular scalability.
- Strictly consistent reads and writes.
- Automatic and configurable sharding of tables.
- Automatic failover support between RegionServers.
- Convenient base classes for backing Hadoop MapReduce jobs with Apache HBase tables.
- Easy to use Java API for client access.
- Block cache and Bloom filters for real-time queries.
- Query predicate push down using server-side filters.
- Extensible JRuby-based (JIRB) shell.
- Support for exporting metrics via the Hadoop metrics subsystem to files or Ganglia; or via JMX.

### Design Overview

HDFS supports immutable files called HFile *SSTable*. HBase must split the table into several HFile and remember the
associate HFiles to a table. The range of rows assigned to a HFile is called Region *tablet*. Column families are stored
in separate HFiles. To modify the table content, HRegionServer *the tablet server* loads the content of the
corresponding HFile, manipulates the data in its memory, writes the resulting content to a new HFile, and deletes the
previous one. An index of the HFile data is kept at the end of the file.

Being loaded into memory implies that all sorting within a HFile can be done efficiently. It also implies possible data
loss after a sudden crash. To avoid that, all modifications are first stored in a log, HLoge. A HRegionServer uses one
HLoge for all loaded regions to reduce the number of writes to the file system. Periodically, the data from memory is
written into some HFile, which is called compaction. During compaction, no read and write is serviced.

To keep the metadata in normally limited size files, they are arranged in a tree of depth 2 to cover a large enough
number of regions. The address of the root HFile region is kept in ZooKeeper [Chubby]. HBaseMaster [master] assigns
regions to HRegionServers. The assignment is kept in ZooKeeper. Single-row transactions are supported using locks on
ZooKeeper.

### When to Use

http://hbase.apache.org/book/architecture.html#arch.overview.when

The most important consideration when looking at HBase is that, while it is a great solution to many problems, it is not
a silver bullet. HBase is not optimized for classic transactional applications or even relational analytics. It is also
not a complete substitute for HDFS when doing large batch MapReduce. Take a look at some of the differences detailed in
the sections below to get a sense of which applications are a good fit for HBase.

So, why should you use HBase? If your application does the following, you should 
consider HBase:

- uses a variable schema where each row is slightly different.
- can't add columns fast enough and most of them are `NULL` in each row.
- stores data in collections. For example: some meta data, message data or binary data is keyed on the same value.
- needs key-based access to data when storing or retrieving.

### Differences Between HBase and BigTable

BigTable categorizes the column families into locality groups and maintain all in  the same file, while HBase keeps each
column family in a separate file. Being in separate files implies more cost for accessing two fields from the same row.

### How Data is Structured in HBase

Data is structured in HTables in the following way:

- The row is the primary key.
- The timestamp is used to separate versions of an entry.
- The columns are in the format, which is predefined and could be an arbitrary string.

### HBase vs. SQL 

In contrast with SQL, HBase:

- Uses an iterator (Scanner) over all rows.
- Has no secondary index: there is no actual index, but the rows are physically sorted 
  based on the row key. 
- Has no advanced functions such as `join`, `groupby`, 
  `having`, etc. These must be rather be implemented by the developer.
- Can use  restricted iterators to resemble `select`, and `update`, and `delete` 
  on individual iterated rows.
- Supports transactions only for rows.

## Alternatives (NoSQL)

For more information, see the slideshare presentation 
[NoSQL Options Compared](http://www.slideshare.net/tazija/nosql-options-compared)

### Column-Oriented Store

Each storage block contains data from only one column.

#### Examples

[Cassandra](http://cassandra.apache.org/) provides a structured key-value store. Keys can map to multiple values, which
are grouped into column families. The column families are fixed when a Cassandra database is created, but columns can be
added to a family at any time. Furthermore, columns are added only to specified keys, so different keys can have
different numbers of columns.

### Document Store

Stores documents made up of tagged elements.

#### Examples

[CouchBase](http://www.couchbase.com/wiki/display/couchbase/Home) - was intended to be  a fast key-value store/cache
aimed primarily at Web apps. It later added the ability to persist data and handle complex queries. Data is stored in
JSON documents. 

See also [MongoDB](http://www.mongodb.org/).

### Key-Value Store

Hash table of keys and values.

#### Examples

[Redis](http://redis.io/) is primarily an in-memory database. While it does have some  capability for persisting data to
disk, its primary use is as a blazingly fast in-memory product. While it uses a key-value model, Redis should not be
confused for a simple in-memory cache, although it does enable expiration to be set for data items, as would a cache.

### Graph Database

Stores data in the nodes and relationships of a graph.

#### Examples

[Neo4j](http://www.neo4j.org/) describes itself as an "embedded, disk-based, fully  transactional Java persistence
engine that stores data structured in graphs rather than in tables." 
