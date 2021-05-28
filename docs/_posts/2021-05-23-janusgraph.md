---
layout: post
title: JanusGraph Reference
tags: [JanusGraph, Graph, Database]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/17-cover.png"
thumbnail: "assets/img/post-cover/17-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

JanusGraph is a transactional graph database, with non-graph databases storage (e.g. HBase), that queries and presents
the data through [Gremlin graph query language](#gremlin---the-language-of-janusgraph). It is totally free and is Open
Source under the Apache 2 license.

<!--more-->

* TOC
{:toc}

> ⚠️ The official JanusGraph Doc is **so horribly written**. **Their Docker image sucks and doesn't work at all**.
> Instead, you would find this post much more helpful on getting yourself up to speed with JanusGraph than any other
> resources, because it
>
> 1. combines useful information from various sources and filters out stupid time-wasting readings from them
> 2. provides [copy&paste-instructions](#install-janusgraph) on spinning up a perfect JanusGraph server instance for you
>    to play with
> 3. covers basics as well as advanced topics, such as [performance issues ](#performance)

## Install JanusGraph

### Local (Mac)

#### [Install Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/brew.html)

[Running a full-fledged JanusGraph requires an indexing service](#configuration), we will use Elasticsearch for that
purpose. Without Elasticsearch installed, JanusGraph will throw such runtime error:

```
...
Caused by: java.net.ConnectException: Connection refused
	at sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
	at sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:715)
	at org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor.processEvent(DefaultConnectingIOReactor.java:174)
	at org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor.processEvents(DefaultConnectingIOReactor.java:148)
	at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor.execute(AbstractMultiworkerIOReactor.java:351)
	at org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager.execute(PoolingNHttpClientConnectionManager.java:221)
	at org.apache.http.impl.nio.client.CloseableHttpAsyncClientBase$1.run(CloseableHttpAsyncClientBase.java:64)
	at java.lang.Thread.run(Thread.java:748)
Could not instantiate implementation: org.janusgraph.diskstorage.es.ElasticSearchIndex
```

Note that JanusGraph is complaining about not being able to connect to a local Elasticsearch instance and, hence, let's
install it via `homebrew`:

    brew tap elastic/tap
    brew install elastic/tap/elasticsearch-full
    
To start Elasticsearch locally,
[locate the binary script under `bin` directory and fire `elasticsearch` executable](https://www.elastic.co/guide/en/elasticsearch/reference/current/brew.html#brew-layout).
For example

    /usr/local/var/homebrew/linked/elasticsearch-full/bin/elasticsearch
    
#### Install JanusGraph

In order to run JanusGraph, Java 8 SE is required. JanusGraph can be downloaded
[Releases](https://github.com/JanusGraph/janusgraph/releases) section of the project repository.

```bash
$ unzip janusgraph-0.5.3.zip
Archive:  janusgraph-0.5.3.zip
  creating: janusgraph-0.5.3/
...
```

Once you have unzipped the downloaded archive, you are ready to go.

#### Start the Gremlin Server

```bash
$ cd janusgraph-0.5.3/
$ ./bin/gremlin-server.sh start
```

#### Interact JanusGraph Through Gremlin Console

```bash
$ cd janusgraph-0.5.3
$ bin/gremlin.sh

         \,,,/
         (o o)
-----oOOo-(3)-oOOo-----
09:12:24 INFO  org.apache.tinkerpop.gremlin.hadoop.structure.HadoopGraph  - HADOOP_GREMLIN_LIBS is set to: /usr/local/janusgraph/lib
plugin activated: tinkerpop.hadoop
plugin activated: janusgraph.imports
gremlin>
```

The [Gremlin](#gremlin) Console interprets commands using [Apache Groovy](https://www.groovy-lang.org/), which is a
superset of Java

We will connect the Gremlin Console to [the server we just started](#start-the-gremlin-server) and redirect all of it's
queries to this server. This is done by using the
[`:remote` command](https://tinkerpop.apache.org/docs/3.4.6/reference/#console-remote-console):

```bash
gremlin> :remote connect tinkerpop.server conf/remote.yaml
==>Configured localhost/127.0.0.1:8182
```

The client and server, in this case, are running on the same machine. On a production environment, modify the parameters
in the conf/remote.yaml file accordingly.

At this point JanusGraph is up and running on your local machine. We shall play with it in [the next section](#basics).

## Basics

The data in this section is the _The Graph of the Gods_ shown in figure below:

![Error loading graph-of-the-gods-2.png!]({{ "/assets/img/graph-of-the-gods-2.png" | relative_url}})

| Figure Symbol      | Semantics                                                  |
|--------------------|------------------------------------------------------------|
| bold key           | a graph indexed key                                        |
| bold key with star | a graph indexed key that must have a unique value          |
| underlined key     | a [vertex-centric indexed](#vertex-centric-indexes) key    |
| hollow-head edge   | a functional/unique edge (no duplicates)                   |
| tail-crossed edge  | a unidirectional edge (can only traverse in one direction) |

### Load Data with Index

```bash
gremlin> graph = JanusGraphFactory.open('conf/janusgraph-berkeleyje-es.properties')
==>standardjanusgraph[berkeleyje:../db/berkeley]
gremlin> GraphOfTheGodsFactory.load(graph)
==>null
gremlin> g = graph.traversal()
==>graphtraversalsource[standardjanusgraph[berkeleyje:../db/berkeley], standard]
```

### Vertex Example

```bash
gremlin> saturn = g.V().has('name', 'saturn').next()
==>v[256]
gremlin> g.V(saturn).valueMap()
==>[name:[saturn], age:[10000]]
gremlin> g.V(saturn).in('father').in('father').values('name')
==>hercules
```

### Edge Example

```bash
gremlin> g.E().has('place', geoWithin(Geoshape.circle(37.97, 23.72, 50)))
==>e[a9x-co8-9hx-39s][16424-battled->4240]
==>e[9vp-co8-9hx-9ns][16424-battled->12520]
gremlin> g.E().has('place', geoWithin(Geoshape.circle(37.97, 23.72, 50))).as('source').inV().as('god2').select('source').outV().as('god1').select('god1', 'god2').by('name')
==>[god1:hercules, god2:hydra]
==>[god1:hercules, god2:nemean]
```

### Traversal Examples

Tt has been demonstrated, in [the previous example](#vertex-example), that Saturn's grandchild was Hercules. Hercules is
the vertex that is 2-steps away from Saturn along the `in('father')` path.

```bash
gremlin> hercules = g.V(saturn).repeat(__.in('father')).times(2).next()
==>v[1536]
```

## Gremlin - The Language of JanusGraph

* [What the hell is "Gremlin"?](https://docs.janusgraph.org/basics/gremlin/#:~:text=Gremlin%20is%20JanusGraph's%20query%20language,graph%20traversals%20and%20mutation%20operations.&text=It%20is%20developed%20independently%20from,supported%20by%20most%20graph%20databases.)
* [The original treatise on Gremlin Language]({{ "/assets/pdf/i-hate-paper.pdf" | relative_url}})
* [TinkerPop](https://tinkerpop.apache.org/docs/current/reference/) - Gremlin language [syntax system](#tinkerpop-syntax)

### Traverse a Graph Using TinkerPop

[Apache TinkerPop](http://tinkerpop.apache.org/) is an open source Graph Computing Framework. It's language is called
"Gremlin". Gremlin helps you navigate the vertices and edges of a graph.

To get Gremlin to traverse a graph, you need a `Graph` instance, which holds the
[structure](http://tinkerpop.apache.org/docs/3.3.0/reference/#_the_graph_structure) and data of the graph. TinkerPop is
a graph abstraction layer over different graph databases and different graph processors, so there are many types of
`Graph` instances you can choose from to instantiate. The best `Graph` instance to start with however is
[TinkerGraph](http://tinkerpop.apache.org/docs/3.3.0/reference/#tinkergraph-gremlin). TinkerGraph is a fast, in-memory
graph database with a small handful of configuration options, making it a good choice for beginners.

Let's play with an example graph that looks like the following:

![Error loading tinkerpop-modern.png!]({{ "/assets/img/tinkerpop-modern.png" | relative_url}})

The graph above can be instantiated in the console this way:

```
gremlin> graph = TinkerFactory.createModern()
==>tinkergraph[vertices:6 edges:6]
gremlin> g = graph.traversal()
==>graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]
```

The first command creates a `Graph` instance named `graph`. The 2nd command generates a `TraversalSource`, which
provides additional information to Gremlin, such as the
[traversal strategies](http://tinkerpop.apache.org/docs/3.3.0/reference/#traversalstrategy) and the
[traversal engine](http://tinkerpop.apache.org/docs/3.3.0/reference/#graphcomputer) to use.

When `TraversalSource` is ready, we shall traverse the Graph:

```
gremlin> g.V() //1\
==>v[1]
==>v[2]
==>v[3]
==>v[4]
==>v[5]
==>v[6]
gremlin> g.V(1) //2\
==>v[1]
gremlin> g.V(1).values('name') //3\
==>marko
gremlin> g.V(1).outE('knows') //4\
==>e[7][1-knows->2]
==>e[8][1-knows->4]
gremlin> g.V(1).outE('knows').inV().values('name') //5\
==>vadas
==>josh
gremlin> g.V(1).out('knows').values('name') //6\
==>vadas
==>josh
gremlin> g.V(1).out('knows').has('age', gt(30)).values('name') //7\
==>josh
```

> 📋️ A `Traversal` is essentially an `Iterator` so if you have code like `x = g.V()`, the **`x` does not contain the
> results of the `g.V()` query**. Rather, that statement assigns an `Iterator` to `x`. To get your results, you would
> then need to iterate through `x`. It is very important to understand this because in the context of the console typing
> `g.V()` instantly returns a value. The console does some magic for you by noticing that `g.V()` returns an `Iterator`
> and then automatically iterates the results. In short, when writing Gremlin outside of the console always remember
> that you must iterate your `Traversal` manually in some way for it to do anything.

### Graph

A graph is a collection of vertices and edges , where a vertex is an entity which represents some domain object (e.g. a
person, a place, etc.) and an edge represents the relationship between two vertices. 

![Error loading modern-edge-1-to-3-1.png!]({{ "/assets/img/modern-edge-1-to-3-1.png" | relative_url}})

The diagram above shows a graph with two vertices, one with a **unique identifier** of "1" and another with a unique
identifier of "3". There is an edge connecting the two with a unique identifier of "9". It is important to know that the
edge has a direction which goes out from vertex "1" and in to vertex "3'.

> ⚠️ Most TinkerPop implementations do not allow for identifier assignment. They will rather assign their own
> identifiers and ignore assigned identifiers that you attempt to assign to them.

Vertices and edges can each be given **label**s to categorize them

![Error loading modern-edge-1-to-3-3.png!]({{ "/assets/img/modern-edge-1-to-3-3.png" | relative_url}})

#### Creating a Graph

```
gremlin> graph = TinkerGraph.open()
==>tinkergraph[vertices:0 edges:0]
gremlin> g = graph.traversal()
==>graphtraversalsource[tinkergraph[vertices:0 edges:0], standard]
gremlin> v1 = g.addV("person").property(id, 1).property("name", "marko").property("age", 29).next()
==>v[1]
gremlin> v2 = g.addV("software").property(id, 3).property("name", "lop").property("lang", "java").next()
==>v[3]
gremlin> g.addE("created").from(v1).to(v2).property(id, 9).property("weight", 0.4)
==>e[9][1-created->3]
```

Note that TinkerGraph allows for identifier assignment, which is not the case with most graph databases.

### Why TinkerPop?

The goal of TinkerPop is to make it easy for developers to create graph applications by providing APIs and tools that
simplify their endeavors. **One of the fundamental aspects to what TinkerPop offers in this area lies in the fact that
TinkerPop is an abstraction layer over different graph databases and different graph processors**. As an abstraction
layer, TinkerPop provides a way to avoid vendor lock-in to a specific database or processor. This capability provides
immense value to developers who are thus afforded options in their architecture and development because:

### Gremlin Server

[Gremlin Server](http://tinkerpop.apache.org/docs/3.3.0/reference/#gremlin-server) provides a way to remotely execute
Gremlin scripts against one or more `Graph` instances hosted within it. It does this by exposing different endpoints,
which allow a request containing a Gremlin script to be processed with results returned.

```bash
$ curl -X POST -d "{\"gremlin\":\"g.V(x).out().values('name')\", \"language\":\"gremlin-groovy\", \"bindings\":{\"x\":1}}" "http://localhost:8182"
```

```json
{
    "requestId": "f67dbfff-b33a-4ae3-842d-c6e7c97b246b",
    "status": {
        "message": "",
        "code": 200,
        "attributes": {
            "@type": "g:Map",
            "@value": []
        }
    },
    "result": {
        "data": {
            "@type": "g:List",
            "@value": ["lop", "vadas", "josh"]
        },
        "meta": {
            "@type": "g:Map",
            "@value": []
        }
    }
}

```

### TinkerPop Syntax

#### Traversal

##### Start Steps

Only those steps on the `GraphTraversalSource` can start a graph traversal

* `addE()` - [Adds an Edge to start the traversal](#adde---addedge-step)
* `addV()` - [Adds a Vertex to start the traversal](#addv---addvertex-step)
* `E()` - [Reads edges from the graph to start the traversal](#ve---graph-step)
* `V()` - [Reads vertices from the graph to start the traversal](#ve---graph-step)
* `inject()` - [Inserts arbitrary objects to start the traversal](#inject---inject-step)

##### addE() - AddEdge Step

![Error loading addedge-step.png!]({{ "/assets/img/addedge-step.png" | relative_url}})

```groovy
g.V(1)
    .as('a')
    .out('created')
    .in('created')
    .where(neq('a'))
    .addE('co-developer')
    .from('a')
    .property('year',2009) // Add a co-developer edge with a year-property between marko and his collaborators.
```

##### addV() - AddVertex Step

```groovy
g.addV('person').property('name','stephen')
```

##### V()/E() - Graph Step

Graph steps are those that read vertices, `V()`, or edges, `E()`, from a graph. The V()-step is usually used to start a
graph traversal, but can also be used mid-traversal. **The E()-step on the other hand can only be used as a start step.**

##### inject() - Inject Step

The concept of "injectable steps" makes it possible to insert arbitrary objects into a traversa:

```groovy
g.V(4).out().values('name').inject('daniel')
g.V(4).out().values('name').inject('daniel').map {it.get().length()}
g.V(4).out().values('name').inject('daniel').map {it.get().length()}.path()
```

![Error loading inject-step.png!]({{ "/assets/img/inject-step.png" | relative_url}})

##### Terminal Steps

```groovy
g.V().out('created').hasNext() // hasNext() determines whether there are available results
g.V().out('created').next() // next() will return the next result
g.V().out('created').next(2) // next(n) will return the next n results in a list
g.V().out('nothing').tryNext() // tryNext() will return an Optional and thus, is a composite of hasNext()/next()
g.V().out('created').toList() // toList() will return all results in a list
g.V().out('created').toSet() // toSet() will return all results in a set and thus, duplicates removed
g.V().out('created').toBulkSet() // toBulkSet() will return all results in a weighted set and thus, duplicates preserved via weighting
results = ['blah',3]
g.V().out('created').fill(results) // fill(collection) will put all results in the provided collection and return the collection when complete
g.addV('person').iterate() // iterate() does not exactly fit the definition of a terminal step in that it doesn’t return a result, but still returns a traversal - it does however behave as a terminal step in that it iterates the traversal and generates side effects without returning the actual result.
```

There is also the `promise()` terminator step, which can only be used with remote traversals to
[Gremlin Server](https://tinkerpop.apache.org/docs/current/reference/#connecting-gremlin-server) or
[RGPs](https://tinkerpop.apache.org/docs/current/reference/#connecting-rgp). It starts a promise to execute a function
on the current Traversal that will be completed in the future.

Finally, [explain()-step](#explain---explain-step) is also a terminal step

##### explain() - Explain Step

The `explain()`-step will return a `TraversalExplanation`. A traversal explanation details how the traversal (prior to
`explain()`) will be compiled given the registered
[traversal strategies](https://tinkerpop.apache.org/docs/current/reference/#traversalstrategy). A `TraversalExplanation`
has a `toString()` representation with 3-columns. The first column is the traversal strategy being applied. The second
column is the traversal strategy category:

* [D]ecoration,
* [O]ptimization,
* [P]rovider optimization,
* [F]inalization, and
* [V]erification.

Finally, the third column is the state of the traversal post strategy application. The final traversal is the resultant
execution plan.

```groovy
g.V().hasLabel('person').outE().identity().inV().count().is(gt(5)).explain()
```

For traversal profiling information, please see [profile()-step](#profile---profile-step).

##### profile() - Profile Step

_to be continued._

## Architecture

JanusGraph supports adapting external data storage, including

* [Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
* [HBase](https://docs.janusgraph.org/storage-backend/hbase/)

The indexing is also modular and is backed by one of

* [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
* [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
* [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)

JanusGraph has embedded mode (same JVM) and standalone mode

## JanusGraph Management

### Configuration

A JanusGraph graph database cluster consists of one or multiple JanusGraph instances. To open a JanusGraph instance, a
configuration has to be provided which specifies how JanusGraph should be set up and run.

A JanusGraph configuration specifies which components JanusGraph should use, controls all operational aspects of a
JanusGraph deployment, and provides a number of tuning options to get maximum performance from a JanusGraph cluster.

**At a minimum**, a JanusGraph configuration

1. must define the persistence engine that JanusGraph should use as a
   [storage backend](https://docs.janusgraph.org/storage-backend/).
2. If advanced graph query support (e.g full-text search, geo search, or range queries) is required an additional
   [indexing backend](https://docs.janusgraph.org/index-backend/) must be configured. 
3. If query performance is a concern, then [caching](https://docs.janusgraph.org/basics/cache/) should be enabled.


## Performance

### Indexing

Most graph queries start the traversal from a list of vertices or edges that are identified by their properties.
JanusGraph supports two different kinds of indexing to speed up query processing:

1. [**graph index**](#graph-index) - makes global retrieval operations efficient on large graphs
2. [**vertex-centric index**](#vertex-centric-indexes) -  speeds up the actual traversal through the graph, in
   particular when traversing through vertices with many incident edges.

#### Graph Index

Graph indexes are global index structures over the entire graph which allow efficient retrieval of vertices or edges by
their properties. For instance, consider the following queries:

```groovy
g.V().has('name', 'hercules')
g.E().has('reason', textContains('loves'))
```

The first query asks for all vertices with the name "hercules". The second asks for all edges where the property reason
contains the word "loves". Without a graph index answering those queries would require a full scan over all vertices or
edges in the graph to find those that match the given condition which is very inefficient and infeasible for huge
graphs.

JanusGraph distinguishes between two types of graph indexes

1. [**Composite Index**](#composite-index) - very fast and efficient but limited to equality lookups for a particular, previously-defined
   combination of property keys
2. [**Mixed Index**](#mixed-index) - can be used for lookups on any combination of indexed keys and support multiple condition predicates in
   addition to equality depending on the backing index store
   
##### Composite Index

Composite indexes retrieve vertices or edges by one or a (fixed) composition of multiple keys. Consider the following
composite index definitions.

```bash
graph.tx().rollback() //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
age = mgmt.getPropertyKey('age')
mgmt.buildIndex('byNameComposite', Vertex.class).addKey(name).buildCompositeIndex()
mgmt.buildIndex('byNameAndAgeComposite', Vertex.class).addKey(name).addKey(age).buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameComposite').call()
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameAndAgeComposite').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameComposite"), SchemaAction.REINDEX).get()
mgmt.updateIndex(mgmt.getGraphIndex("byNameAndAgeComposite"), SchemaAction.REINDEX).get()
mgmt.commit()
```

First, two property keys "name" and "age" are already defined. Next, a simple composite index on just the "name"
property key (also called "key-index") is built. JanusGraph will use this index to answer the following query:

```groovy
g.V().has('name', 'hercules')
```

The second composite graph index includes both keys. JanusGraph will use this index to answer the following query:

```groovy
g.V().has('age', 30).has('name', 'hercules')
```

> 📋 All keys of a composite graph index must be found in the query's equality conditions for the index to be hit. For
> example, the following query cannot be answered with either of the indexes because it only contains a constraint on
> "age" but not "name":
>
>     g.V().has('age', 30)
>
> In addition, composite graph indexes can only be used for **equality** constraints like those in the queries above.
> The following query will only hit the index defined on the "name" key because the "age" constraint is not an equality
> constraint:
>
>     g.V().has('name', 'hercules').has('age', inside(20, 50))
>
> _Composite indexes do not require configuration of an external indexing backend and are supported through the primary
> storage backend. Hence, composite index modifications are persisted through the same transaction as graph
> modifications which means that those changes are atomic and/or consistent if the underlying storage backend supports
> atomicity and/or consistency._

###### Index Uniqueness

Composite indexes can also be used to enforce property uniqueness in the graph. If a composite graph index is defined as
`unique()` there can be at most one vertex or edge for any given concatenation of property values associated with the
keys of that index. For instance, to enforce that names are unique across the entire graph the following composite graph
index would be defined.

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
mgmt.buildIndex('byNameUnique', Vertex.class).addKey(name).unique().buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameUnique').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameUnique"), SchemaAction.REINDEX).get()
mgmt.commit()
```

> ⚠️ To enforce uniqueness against an eventually consistent storage backend, the
> [consistency](https://docs.janusgraph.org/advanced-topics/eventual-consistency/) of the index must be explicitly set
> to enabling locking.
     
##### Mixed Index

Mixed indexes retrieve vertices or edges by any combination of previously added property keys. Mixed indexes provide
more flexibility than composite indexes and support additional condition predicates beyond equality. On the other hand,
**mixed indexes are slower for most equality queries than composite indexes**.

Unlike composite indexes, mixed indexes require the configuration of an
[indexing backend](https://docs.janusgraph.org/index-backend/) and use that indexing backend to execute lookup
operations. JanusGraph can support multiple indexing backends in a single installation. Each indexing backend must be
uniquely identified by name in the JanusGraph configuration which is called the **indexing backend name**.

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
age = mgmt.getPropertyKey('age')
mgmt.buildIndex('nameAndAge', Vertex.class).addKey(name).addKey(age).buildMixedIndex("search")
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'nameAndAge').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("nameAndAge"), SchemaAction.REINDEX).get()
mgmt.commit()
```

> ⚠️ The example above defines a mixed index containing the property keys "name" and "age". The definition refers to the
> indexing backend name **`search`** so that JanusGraph knows which configured indexing backend it should use for this
> particular index. **The `search` parameter specified in the `buildMixedIndex` call must match the **second** part in
> the JanusGraph configuration definition like this: index.**search**. backend If the index was named `solrsearch` then
> the configuration definition would appear like this: index.solrsearch.backend.

While the index definition example looks similar to the composite index above, it provides greater query support and can
answer any of the following queries.

```bash
g.V().has('name', textContains('hercules')).has('age', inside(20, 50))
g.V().has('name', textContains('hercules'))
g.V().has('age', lt(50))
g.V().has('age', outside(20, 50))
g.V().has('age', lt(50).or(gte(60)))
g.V().or(__.has('name', textContains('hercules')), __.has('age', inside(20, 50)))
```

Mixed indexes support [full-text search](#index-parameters-and-full-text-search), range search,
[geo search](#geo-mapping) and more. Please refer to
[Search Predicates and Data Types](https://docs.janusgraph.org/index-backend/search-predicates/) for a list of
predicates supported by a specific indexing backend.

> 📋 Unlike composite indexes, mixed indexes do not support uniqueness.

###### Adding Property Keys

New keys (of property) can be added to an existing mixed index which allows subsequent queries to include this key in
the query condition:

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
location = mgmt.makePropertyKey('location').dataType(Geoshape.class).make()
nameAndAge = mgmt.getGraphIndex('nameAndAge')
mgmt.addIndexKey(nameAndAge, location)
mgmt.commit()
//Previously created property keys already have the status ENABLED, but
//our newly created property key "location" needs to REGISTER so we wait for both statuses
ManagementSystem.awaitGraphIndexStatus(graph, 'nameAndAge').status(SchemaStatus.REGISTERED, SchemaStatus.ENABLED).call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("nameAndAge"), SchemaAction.REINDEX).get()
mgmt.commit()
```

###### Index Parameters and Full-Text Search

When defining a mixed index, a list of parameters can be optionally specified for each property key added to the index.
These parameters control how the particular key is to be indexed. Whether these are supported depends on the configured
index backend. A particular index backend might also support custom parameters in addition to the ones listed here.

* When the value is **indexed as text**, the string is **tokenized** into a bag of words (the exact tokenization depends
  on the indexing backend and its configuration) which allows the user to
  efficiently query for all matches that contain one or multiple words. This is commonly referred to as _full-text
  search_
* When the value is **indexed as a character string**, the string is index "as-is" without any further analysis or
  tokenization. **This facilitates queries looking for an exact character sequence match**. This is commonly referred to
  as _string search_.
  
To specify this indexing option, use `Mapping.TEXT` (index as text) or `Mapping.STRING` (index as character sequences)
as in

```bash
mgmt = graph.openManagement()
summary = mgmt.makePropertyKey('booksummary').dataType(String.class).make()
mgmt.buildIndex('booksBySummary', Vertex.class).addKey(summary, Mapping.TEXT.asParameter()).buildMixedIndex("search")
mgmt.commit()

mgmt = graph.openManagement()
name = mgmt.makePropertyKey('bookname').dataType(String.class).make()
mgmt.buildIndex('booksBySummary', Vertex.class).addKey(name, Mapping.STRING.asParameter()).buildMixedIndex("search")
mgmt.commit()
```

When a string property is indexed as text, only full-text search predicates are supported. **Full-text search is
case-insensitive**.

* `textContains` - is true if (at least) **one word** inside the text string matches the query string
* `textContainsPrefix`: is true if (at least) **one word** inside the text string begins with the query string
* `textContainsRegex`: is true if (at least) **one word** inside the text string matches the given regular expression
* `textContainsFuzzy`: is true if (at least) **one word** inside the text string is similar to the query String (based on Levenshtein edit distance)

```groovy
import static org.janusgraph.core.attribute.Text...

g.V().has('booksummary', textContains('unicorns'))
g.V().has('booksummary', textContainsPrefix('uni'))
g.V().has('booksummary', textContainsRegex('.*corn.*'))
g.V().has('booksummary', textContainsFuzzy('unicorn'))
```

When a string property is indexed as character sequence, the string value is indexed and can be queried "as-is" -
including stop words and non-letter characters. However, in this case the query must match the entire string value.
Hence, the string mapping is useful when indexing short character sequences that are considered to be one token.

Only the following predicates are supported in this case:

* `eq` - if *the string* is identical to the query string
* `neq` - if *the string* is different than the query string
* `textPrefix` - if *the string* value starts with the given query string
* `textRegex` - if *the string* value matches the given regular expression in its entirety
* `textFuzzy` - if *the string* value is similar to the given query string (based on Levenshtein edit distance)

```groovy
import static org.apache.tinkerpop.gremlin.process.traversal.P...
import static org.janusgraph.core.attribute.Text...

g.V().has('bookname', eq('unicorns'))
g.V().has('bookname', neq('unicorns'))
g.V().has('bookname', textPrefix('uni'))
g.V().has('bookname', textRegex('.*corn.*'))
g.V().has('bookname', textFuzzy('unicorn'))
```

If you are using **Elasticsearch** it is possible to index properties as **both** text and string allowing you to use
all of the predicates for exact and fuzzy matching:

```bash
mgmt = graph.openManagement()
summary = mgmt.makePropertyKey('booksummary').dataType(String.class).make()
mgmt.buildIndex('booksBySummary', Vertex.class).addKey(summary, Mapping.TEXTSTRING.asParameter()).buildMixedIndex("search")
mgmt.commit()
```

Note that the data will be stored in the index **twice**, once for exact matching and once for fuzzy matching.

###### TinkerPop Text Predicates

It is also possible to use the TinkerPop text predicates with JanusGraph, but these predicates do not make use of
indices which means that they require filtering in memory which can be very costly.

```groovy
import static org.apache.tinkerpop.gremlin.process.traversal.TextP...;

g.V().has('bookname', startingWith('uni'))
g.V().has('bookname', endingWith('corn'))
g.V().has('bookname', containing('nico'))
```

###### Geo Mapping

By default, JanusGraph supports indexing geo properties with point type and querying geo properties by circle or box. To
index a non-point geo property with support for querying by any geoshape type, specify the mapping as
`Mapping.PREFIX_TREE`:

```bash
mgmt = graph.openManagement()
name = mgmt.makePropertyKey('border').dataType(Geoshape.class).make()
mgmt.buildIndex('borderIndex', Vertex.class).addKey(name, Mapping.PREFIX_TREE.asParameter()).buildMixedIndex("search")
mgmt.commit()
```

Additional parameters can be specified to tune the configuration of the underlying prefix tree mapping. These optional
parameters include the number of levels used in the prefix tree as well as the associated precision.

```bash
mgmt = graph.openManagement()
name = mgmt.makePropertyKey('border').dataType(Geoshape.class).make()
mgmt.buildIndex('borderIndex', Vertex.class).addKey(name, Mapping.PREFIX_TREE.asParameter(), Parameter.of("index-geo-max-levels", 18), Parameter.of("index-geo-dist-error-pct", 0.0125)).buildMixedIndex("search")
mgmt.commit()
```

Note that some indexing backends (e.g. Solr) may require additional external schema configuration to support and tune
indexing non-point properties.

##### Key/Property Ordering

```bash
g.V().has('name', textContains('hercules')).order().by('age', desc).limit(10)
```

* When composite graph index is used, all results will be **sorted in-memory**, which will be very costly for large
  result sets.

##### Label Constraint

To index vertices or edges with only a particular label (e.g. index only gods by their names, but not every vertex (a
human) that has a "name" property). It is possible to restrict the index to a particular vertex or edge label using the
`indexOnly` method of the index builder. The following example creates a composite index for the property key "name"
that indexes only vertices labeled "god".

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
name = mgmt.getPropertyKey('name')
god = mgmt.getVertexLabel('god')
mgmt.buildIndex('byNameAndLabel', Vertex.class).addKey(name).indexOnly(god).buildCompositeIndex()
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitGraphIndexStatus(graph, 'byNameAndLabel').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getGraphIndex("byNameAndLabel"), SchemaAction.REINDEX).get()
mgmt.commit()
```

> 📋 When a composite index with label restriction is defined as unique, the uniqueness constraint only applies to
> properties on vertices or edges for the specified label.

##### Composite v.s. Mixed Indexes

* Use a composite index for exact match index retrievals. Composite indexes do not require configuring or operating an
  external index system and are often significantly faster than mixed indexes.
  - As an exception, use a mixed index for exact matches when the number of distinct values for query constraint is
    relatively small or if one value is expected to be associated with many elements in the graph (i.e. in case of low
    selectivity).
* Use a mixed indexes for numeric range, full-text or geo-spatial indexing. Also, using a mixed index can speed up the
  `order().by()` queries.

#### Vertex-Centric Indexes

Vertex-centric indexes are local index structures built individually per vertex. In large graphs vertices can have
thousands of incident edges. Traversing through those vertices can be very slow because a large subset of the incident
edges has to be retrieved and then filtered in memory to match the conditions of the traversal. Vertex-centric indexes
can speed up such traversals by using localized index structures to retrieve only those edges that need to be traversed.

Without a vertex-centric index, a query like the following would retrieve all battled edges even though there are only a
handful of matching edges:

```bash
h = g.V().has('name', 'hercules').next()
g.V(h).outE('battled').has('time', inside(10, 20)).inV()
```

Building a vertex-centric index speeds up such traversal queries. Note, this initial index example already exists in the
example graph dataset (Graph of the Gods) as an index named edges. As a result, running the step of
`mgmt.buildEdgeIndex(battled, 'battlesByTime', Direction.BOTH, Order.desc, time)
` below will result in a uniqueness constraint error.

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
time = mgmt.getPropertyKey('time')
battled = mgmt.getEdgeLabel('battled')
mgmt.buildEdgeIndex(battled, 'battlesByTime', Direction.BOTH, Order.desc, time)
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitRelationIndexStatus(graph, 'battlesByTime').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getRelationIndex(battled, "battlesByTime"), SchemaAction.REINDEX).get()
mgmt.commit()
```

This example builds a vertex-centric index which indexes `battled` edges in both direction by time in descending order.
A vertex-centric index is built against a particular edge label which is the first argument to the method
`JanusGraphManagement.buildEdgeIndex()`

A vertex-centric index can be defined with multiple keys.

```bash
graph.tx().rollback()  //Never create new indexes while a transaction is active
mgmt = graph.openManagement()
time = mgmt.getPropertyKey('time')
rating = mgmt.makePropertyKey('rating').dataType(Double.class).make()
battled = mgmt.getEdgeLabel('battled')
mgmt.buildEdgeIndex(battled, 'battlesByRatingAndTime', Direction.OUT, Order.desc, rating, time)
mgmt.commit()
//Wait for the index to become available
ManagementSystem.awaitRelationIndexStatus(graph, 'battlesByRatingAndTime', 'battled').call()
//Reindex the existing data
mgmt = graph.openManagement()
mgmt.updateIndex(mgmt.getRelationIndex(battled, 'battlesByRatingAndTime'), SchemaAction.REINDEX).get()
mgmt.commit()
```

Note, that the order in which the property keys are specified is important because vertex-centric indexes are
**prefix indexes**. This means, that `battled` edges are indexed by "rating" first and then "time".

```bash
h = g.V().has('name', 'hercules').next()
g.V(h).outE('battled').property('rating', 5.0) //Add some rating properties (1)
g.V(h).outE('battled').has('rating', gt(3.0)).inV() // (2)
g.V(h).outE('battled').has('rating', 5.0).has('time', inside(10, 50)).inV() // (3)
g.V(h).outE('battled').has('time', inside(10, 50)).inV() // (4)
```

The `battlesByRatingAndTime` index can speed up the first two but not the third query.

Multiple vertex-centric indexes can be built for the same edge label in order to support different constraint
traversals. JanusGraph query optimizer attempts to pick the most efficient index for any given traversal. Vertex-centric
index **supports equality and range/interval constraints only**.

> 📋 The property keys used in a vertex-centric index must have an explicitly defined data type (i.e. not
> `Object.class`) which supports a native sort order, which means the keys must implement both `Comparable` and
> `OrderPreservingSerializer` of their serializers. Those types include
>
> * Boolean
> * UUID
> * Byte
> * Float
> * Long
> * String
> * Integer
> * Date
> * Double
> * Character, and
> * Short

JanusGraph automatically builds vertex-centric indexes per edge label and property key. That means, even with thousands
of incident `battled` edges, queries like `g.V(h).out('mother')` or `g.V(h).values('age')` are efficiently answered by
the local index.

Vertex-centric indexes cannot speed up unconstrained traversals which require traversing through all incident edges of a
particular label. Those traversals will become slower as the number of incident edges increases. Often, such traversals
can be rewritten as constrained traversals that can utilize a vertex-centric index to ensure acceptable performance at
scale.

##### Ordered Traversals

```bash
h = g..V().has('name', 'hercules').next()
g.V(h).local(outE('battled').order().by('time', desc).limit(10)).inV().values('name')
g.V(h).local(outE('battled').has('rating', 5.0).order().by('time', desc).limit(10)).values('place')
```

Such queries can also be efficiently answered by vertex-centric indexes if the order key matches the key of the index
and the requested order (i.e. ascending or descending) is the same as the one defined for the index. The `battlesByTime`
index would be used to answer the first query and `battlesByRatingAndTime` applies to the second.
