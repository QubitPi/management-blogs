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

## Gremlin

* [What the hell is "Gremlin"?](https://docs.janusgraph.org/basics/gremlin/#:~:text=Gremlin%20is%20JanusGraph's%20query%20language,graph%20traversals%20and%20mutation%20operations.&text=It%20is%20developed%20independently%20from,supported%20by%20most%20graph%20databases.)
* [The original treatise on Gremlin Language]({{ "/assets/pdf/i-hate-paper.pdf" | relative_url}})

## Learn JanusGraph Basics

### Using Docker

    docker run -it -p 8182:8182 janusgraph/janusgraph ./bin/gremlin.sh

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

* `addE()` - Adds an Edge to start the traversal (example).
* `addV()` - Adds a Vertex to start the traversal (example).
* `E()` - Reads edges from the graph to start the traversal (example).
* `inject()` - Inserts arbitrary objects to start the traversal (example).
* [`V()`](#v-graph-step) - Reads vertices from the graph to start the traversal.

#### V(): Graph Step

Graph steps are those that read vertices, `V()`, or edges, `E()`, from a graph. The V()-step is usually used to start a
graph traversal, but can also be used mid-traversal. **The E()-step on the other hand can only be used as a start step.**

#### Terminal Steps

```groovy
g.V().out('created').hasNext() //// (1)
g.V().out('created').next() //// (2)
g.V().out('created').next(2) //// (3)
g.V().out('nothing').tryNext() //// (4)
g.V().out('created').toList() //// (5)
g.V().out('created').toSet() //// (6)
g.V().out('created').toBulkSet() //// (7)
results = ['blah',3]
g.V().out('created').fill(results) //// (8)
g.addV('person').iterate() //9
```



_To be continued..._

## Architecture

JanusGraph supports adapting external data storage, including

* [Cassandra](https://docs.janusgraph.org/storage-backend/cassandra/)
* [HBase](https://docs.janusgraph.org/storage-backend/hbase/)

The indexing is also modular and is backed by one of

* [Elasticsearch](https://docs.janusgraph.org/index-backend/elasticsearch/)
* [Apache Solr](https://docs.janusgraph.org/index-backend/solr/)
* [Apache Lucene](https://docs.janusgraph.org/index-backend/lucene/)

JanusGraph has embedded mode (same JVM) and standalone mode


[]: #