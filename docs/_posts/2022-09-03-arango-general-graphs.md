---
layout: post
title: Managing Graphs through Arango Command Line
tags: [ArangoDB, Database, Knowledge Graph, arangosh]
category: FINALIZED
color: rgb(128, 165, 76)
feature-img: "assets/img/post-cover/8-cover.png"
thumbnail: "assets/img/post-cover/8-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


arangosh
--------

The ArangoDB shell (arangosh) is a command-line client tool that can be used for administration of ArangoDB servers.

It offers a **V8 JavaScript shell environment**, in which you can use _JS_ interfaces and modules like the
[db object](https://www.arangodb.com/docs/stable/appendix-references-dbobject.html) to manage collections or run ad-hoc 
queries for instance, access the [General Graph module](#general-graphs) or other features.

It can be used as **interactive shell** (REPL) as well as to execute a JavaScript string or file. It is _NOT_ a general 
command line like PowerShell or Bash however. Commands like `curl` are not possible inside of this JS shell.


ArangoDB Graphs
---------------

A Graph consists of vertices and edges. Edges are stored as documents in edge collections. A vertex can be a document of a document collection or of an edge collection (so edges can be used as vertices). Which collections are used within a named graph is defined via edge definitions. A named graph can contain more than one edge definition, at least one is needed. Graphs allow you to structure your models in line with your domain and group them logically in collections and giving you the power to query them in the same graph queries.

### Mapping SQL Concepts to Graph Concepts

In SQL you commonly have the construct of a **relation table** to store relations between two data tables. **An edge collection is somewhat similar to these relation tables**; **vertex collections resemble the data tables** with the objects to connect. _While simple graph queries with fixed number of hops via the relation table may be doable in SQL with several nested joins, graph databases can handle an arbitrary number of these hops over edge collections_ - this is called **traversal**. Also edges in one edge collection may point to several vertex collections. It's common to have attributes attached to edges, i.e. a label naming this interconnection. Edges have a direction, with their relations **\_from** and **\_to** pointing from one document to another document stored in vertex collections. In queries you can define in which directions the edge relations may be followed:

* **OUTBOUND**: _from → _to
* **INBOUND**: _from ← _to
* **ANY**: _from ↔ _to


### General Graphs

