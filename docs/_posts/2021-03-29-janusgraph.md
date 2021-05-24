---
layout: post
title: JanusGraph
tags: [JanusGraph, Graph, Database]
color: rgb(224, 1, 152)
feature-img: "assets/img/pexels/design-art/down-the-street-through-Osaka.png"
thumbnail: "assets/img/pexels/design-art/down-the-street-through-Osaka.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

> ⚠️ JanusGraph Doc is so horribly written! And their Docker image sucks!!! This post gives you much better materials
> that help you jump up with JanusGraph with good experiences, because it
>
> * Combines useful information from various sources and filter out stupid time-wasting texts
> * Provides copy-and-paste instructions on spinning up a perfect JanusGraph server instance
> * It covers basics as well as advance topics, such as performance issues 

## Install JanusGraph

### Local (Mac)

#### [Install Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/brew.html)

[Running JanusGraph requires an indexing service](#configuration), we will use Elasticsearch for that purpose. Without
Elasticsearch installed, JanusGraph will throw such runtime error:

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

## Gremlin

* [What the hell is "Gremlin"?](https://docs.janusgraph.org/basics/gremlin/#:~:text=Gremlin%20is%20JanusGraph's%20query%20language,graph%20traversals%20and%20mutation%20operations.&text=It%20is%20developed%20independently%20from,supported%20by%20most%20graph%20databases.)
* [The original treatise on Gremlin Language]({{ "/assets/pdf/i-hate-paper.pdf" | relative_url}})

## Learn JanusGraph Basics

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

## TinkerPop Syntax

### Traversal

#### Start Steps

Only those steps on the `GraphTraversalSource` can start a graph traversal

* `addE()` - [Adds an Edge to start the traversal](#adde---addedge-step)
* `addV()` - [Adds a Vertex to start the traversal](#addv---addvertex-step)
* `E()` - [Reads edges from the graph to start the traversal](#ve---graph-step)
* `V()` - [Reads vertices from the graph to start the traversal](#ve---graph-step)
* `inject()` - [Inserts arbitrary objects to start the traversal](#inject---inject-step)

#### addE() - AddEdge Step

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

### addV() - AddVertex Step

```groovy
g.addV('person').property('name','stephen')
```

#### V()/E() - Graph Step

Graph steps are those that read vertices, `V()`, or edges, `E()`, from a graph. The V()-step is usually used to start a
graph traversal, but can also be used mid-traversal. **The E()-step on the other hand can only be used as a start step.**

#### inject() - Inject Step

The concept of "injectable steps" makes it possible to insert arbitrary objects into a traversa:

```groovy
g.V(4).out().values('name').inject('daniel')
g.V(4).out().values('name').inject('daniel').map {it.get().length()}
g.V(4).out().values('name').inject('daniel').map {it.get().length()}.path()
```

![Error loading inject-step.png!]({{ "/assets/img/inject-step.png" | relative_url}})

#### Terminal Steps

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

#### explain() - Explain Step

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

#### profile() - Profile Step

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
2. **vertex-centric index** -  speeds up the actual traversal through the graph, in particular when traversing through
   vertices with many incident edges.

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

1. **Composite Index** - very fast and efficient but limited to equality lookups for a particular, previously-defined
   combination of property keys
2. **Mixed Index** - can be used for lookups on any combination of indexed keys and support multiple condition predicates in
   addition to equality depending on the backing index store

