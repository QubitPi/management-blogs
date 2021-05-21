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

## Learn JanusGraph

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



## Architecture

JanusGraph supports adapting external data storage, including

* Cassandra
* HBase

The indexing is also modular and is backed by one of

* Elasticsearch
* Apache Solr
* Apache Lucene

JanusGraph has embedded mode (same JVM) and standalone mode
