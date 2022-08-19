---
layout: post
title: What is HDFS Namespace
tags: [Hadoop, HDFS, Architecture]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/34-cover.png"
thumbnail: "assets/img/post-cover/34-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

I was designing a system interacting with HDFS and looking for some reference on HDFS namespace. I got unbelievably
surprised, however, that I was not able to find a simple definition of it. I wrote this post as a result of researching 
online about what the namespace is with respect to HDFS (HDFS guys are not the best documentors and still are not
after almost 10 years today).

<!--more-->

In order to get a sense of HDFS namespace, we shall start with the architecture of HDFS in terms of its storage.

* TOC
{:toc}


HDFS Architecture
-----------------

HDFS has a **master/slave architecture**. An HDFS cluster consists of a **single NameNode**, a master server that
manages the file system namespace and regulates access to files by clients. In addition, there are a number of
**DataNodes**, _usually one per node_ in the cluster, which manage storage attached to the nodes that they run on. HDFS 
exposes a file system namespace and allows user data to be stored in files. Internally, a file is split into one or more 
blocks and these blocks are stored in a set of DataNodes. The NameNode executes file system namespace operations like 
opening, closing, and renaming files and directories. It also determines the mapping of blocks to DataNodes. The
DataNodes are responsible for serving read and write requests from the file system's clients. The DataNodes also perform 
block creation, deletion, and replication upon instruction from the NameNode.

![Error loading hdfsarchitecture.png!]({{ "/assets/img/hdfsarchitecture.png" | relative_url}})

### The File System Namespace

HDFS supports a traditional hierarchical file organization. A user or an application can create directories and store
files inside these directories. The file system namespace hierarchy is similar to most other existing file systems; one
can create and remove files, move a file from one directory to another, or rename a file.

The NameNode maintains the file system namespace. Any change to the file system namespace or its properties is recorded
by the NameNode. An application can specify the number of replicas of a file that should be maintained by HDFS. The
number of copies of a file is called the replication factor of that file. This information is stored by the NameNode.

> What can be sensed from the discussion above is the **a namespace is empirically an entire file system unit with
> respect to a single HDFS cluster**, i.e. a single cluster/NameNode defines one namespace


Namespace in A Multi-Cluster
----------------------------

![Error loading hdfs-federation-background.png!]({{ "/assets/img/hdfs-federation-background.png" | relative_url}})

Now we know HDFS has two main layers:

1. **Namespace**
   - Consists of directories, files and blocks.
   - It supports all the namespace related file system operations such as create, delete, modify and list files and 
     directories.
2. Block Storage Service, which has two parts:
   a. Block Management (performed in the **NameNode**)
      - Provides DataNode cluster membership by handling registrations, and periodic heart beats.
      - Processes block reports and maintains location of blocks. 
      - Supports block related operations such as create, delete, modify and get block location. 
      - Manages replica placement, block replication for under replicated blocks, and deletes blocks that are over replicated. 
   b. Storage
      - Provided by **DataNodes** by storing blocks on the local file system and allowing read/write access.

The early HDFS architecture allows only a single namespace for the entire cluster. In that configuration, a single
NameNode manages the namespace. **HDFS Federation**, on the other hand, addresses this limitation by adding support for 
multiple NameNodes/namespaces to HDFS.

In order to scale the name service horizontally, federation uses multiple **independent** NameNodes/namespaces. The 
NameNodes are _federated_, _independent_, and do not require coordination with each other. **The DataNodes are used as
common storage for blocks by all the NameNodes**. Each DataNodes registers with all the NameNodes in the cluster.
DataNodes send periodic heartbeats and block reports. They also handle commands from the NameNodes.

![Error loading hdfs-federation.png!]({{ "/assets/img/hdfs-federation.png" | relative_url}})

### Block Pool

A **Block Pool** is a set of blocks that belong to a single namespace. DataNodes store blocks for all the block pools in 
the cluster. Each Block Pool is managed independently. This allows a namespace to generate Block IDs for new blocks
without the need for coordination with the other namespaces. A NameNode failure does not prevent the DataNode from
serving other NameNodes in the cluster.

A Namespace and its block pool together are called Namespace Volume. It is a self-contained unit of management. When a 
NameNode/namespace is deleted, the corresponding block pool at the DataNodes is deleted. Each namespace volume is
upgraded as a unit, during cluster upgrade.


Conclusion
----------

What does the discussion above mean for the business applications? It's the following:

**A namespace is with respect to a multi-cluster environment so as to namespace underlying file blocks. From the 
application's perspective, however, namespace should not be exposed. An application's boundary talk to either a single
cluster or multiple clusters and is agnostic of "namespace"**
