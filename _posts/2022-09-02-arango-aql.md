---
layout: post
title: ArangoDB Query Language (AQL)
tags: [ArangoDB, Database, Knowledge Graph]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/7-cover.png"
thumbnail: "assets/img/post-cover/7-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

The ArangoDB Query Language (AQL) can be used to retrieve and modify data that are stored in ArangoDB.

<!--more-->

AQL is mainly a declarative language, meaning that a query expresses what result should be achieved but not how it
should be achieved. AQL aims to be human-readable and therefore uses keywords from the English language. Another design 
goal of AQL was client independency, meaning that the language and syntax are the same for all clients, no matter what 
programming language the clients may use. Further design goals of AQL were the support of complex query patterns and the 
different data models ArangoDB offers.

In its purpose, AQL is similar to the Structured Query Language (SQL). AQL supports reading and modifying collection
data, but it doesn’t support data-definition operations such as creating and dropping databases, collections and
indexes. It is a pure data manipulation language (DML), not a data definition language (DDL) or a data control language 
(DCL).

The syntax of AQL queries is different to SQL, even if some keywords overlap. Nevertheless, AQL should be easy to 
understand for anyone with an SQL background.

* TOC
{:toc}


Fundamentals
------------

### AQL Syntax

#### Query Types

An AQL query must either

* return a result (indicated by usage of the `RETURN` keyword) or
* execute a data-modification operation (indicated by usage of one of the keywords `INSERT`, `UPDATE`, `REPLACE`,
  `REMOVE` or `UPSERT`).

The AQL parser will return an error if it detects more than one data-modification operation in the same query or if it
cannot figure out if the query is meant to be a data retrieval or a modification operation.

> AQL only allows **one** query in a single query string

#### Comments

Comments can be embedded at any position in a query.

AQL supports two types of comments:

1. Single line comments: These start with a double forward slash and end at the end of the line, or the end of the query
   string (whichever is first).
2. Multi line comments: These start with a forward slash and asterisk, and end with an asterisk and a following forward
   slash. They can span as many lines as necessary.

{% highlight javascript %}
/* this is a comment */ RETURN 1
/* these */ RETURN /* are */ 1 /* multiple */ + /* comments */ 1
/* this is
   a multi line
   comment */
// a single line comment
{% endhighlight %}

An example AQL query may look like this:

{% highlight javascript %}
FOR u IN users
    FILTER u.type == "newbie" && u.active == true
    RETURN u.name
{% endhighlight %}


Data Queries
------------

There are two fundamental types of AQL queries:

1. queries which access data (read documents)
2. queries which modify data (create, update, replace, delete documents)

### Data Access Queries

Retrieving data from the database with AQL does always include a **RETURN** operation. It can be used to return a static value, such as a string:

{% highlight javascript %}
RETURN "Hello ArangoDB!"
{% endhighlight %}

The query result is always an array of elements, even if a single element was returned and contains a single element in 
that case: `["Hello ArangoDB!"]`

The function **DOCUMENT()** can be called to retrieve a single document via its document handle, for instance:

{% highlight javascript %}
RETURN DOCUMENT("users/phil")
{% endhighlight %}

`RETURN` is usually accompanied by a **FOR loop** to iterate over the documents of a collection. The following query 
executes the loop body for all documents of a collection called _users_. Each document is returned unchanged in this 
example:

{% highlight javascript %}
FOR doc IN users
    RETURN doc
{% endhighlight %}

Instead of returning the raw doc, one can easily create a **projection**:

{% highlight javascript %}
FOR doc IN users
    RETURN {
        user: doc,
        newAttribute: true
    }
{% endhighlight %}

For every user document, an object with two attributes is returned. The value of the attribute _user_ is set to the 
content of the user document, and _newAttribute_ is a static attribute with the boolean value _true_.

Operations like **FILTER**, **SORT** and **LIMIT** can be added to the loop body to narrow and order the result. Instead 
of call to `DOCUMENT()` shown above, one can also retrieve the document that describes user phil like so:

{% highlight javascript %}
FOR doc IN users
    FILTER doc._key == "phil"
    RETURN doc
{% endhighlight %}

The document key is used in this example, but any other attribute could equally be used for filtering. Since the 
**document key is guaranteed to be unique**, no more than a single document will match this filter. For other attributes 
this may not be the case. To return a subset of active users (determined by an attribute called _status_), sorted by
name in ascending order, you can do:

{% highlight javascript %}
FOR doc IN users
    FILTER doc.status == "active"
    SORT doc.name
    LIMIT 10
{% endhighlight %}

> ⚠️ Note that the order of operations can influence the result significantly. Limiting the number of documents before a 
> filter is usually not what you want, because it easily misses a lot of documents that would fulfill the filter 
> criterion, but are ignored because of a premature `LIMIT` clause. `LIMIT` is, therefore, usually put at the very end, 
> after `FILTER`, `SORT` and other operations.

### Data Modification Queries

AQL supports the following data-modification operations:

* **INSERT**: insert new
  [documents](https://qubitpi.github.io/jersey-guide/finalized/2022/08/19/arangodb.html#databases-collections-and-documents)
  into a
  [collection](https://qubitpi.github.io/jersey-guide/finalized/2022/08/19/arangodb.html#databases-collections-and-documents)
* **UPDATE**: partially update existing documents in a collection
* **REPLACE**: completely replace existing documents in a collection
* **REMOVE**: remove existing documents from a collection
* **UPSERT**: conditionally insert or update documents in a collection

#### Modifying a Single Document

Let's start with the basics: `INSERT`, `UPDATE` and `REMOVE` operations on single documents. Here is an example that 
insert a document to an existing collection users:

{% highlight javascript %}
INSERT {
    firstName: "Anna",
    name: "Pavlova",
    profession: "artist"
} IN users
{% endhighlight %}

You may provide a key for the new document; otherwise, ArangoDB will create one for you.

{% highlight javascript %}
INSERT {
    _key: "GilbertoGil",
    firstName: "Gilberto",
    name: "Gil",
    city: "Fortalezza"
} IN users
{% endhighlight %}

As ArangoDB is schema-free, attributes of the documents may vary:

{% highlight javascript %}
INSERT {
    _key: "PhilCarpenter",
    firstName: "Phil",
    name: "Carpenter",
    middleName: "G.",
    status: "inactive"
} IN users

INSERT {
    _key: "NatachaDeclerck",
    firstName: "Natacha",
    name: "Declerck",
    location: "Antwerp"
} IN users
{% endhighlight %}

Update is quite simple. The following AQL statement will add or change the attributes status and location

{% highlight javascript %}
UPDATE "PhilCarpenter" WITH {
    status: "active",
    location: "Beijing"
} IN users
{% endhighlight %}

Replace is an alternative to update where all attributes of the document are replaced.

{% highlight javascript %}
REPLACE {
    _key: "NatachaDeclerck",
    firstName: "Natacha",
    name: "Leclerc",
    status: "active",
    level: "premium"
} IN users
{% endhighlight %}

Removing a document is simple if you know its key:

{% highlight javascript %}
REMOVE "GilbertoGil" IN users
{% endhighlight %}

or

{% highlight javascript %}
REMOVE { _key: "GilbertoGil" } IN users
{% endhighlight %}



Operations like [`FILTER`](#filter), `SORT`, and `LIMIT` can be added to the loop body to narrow and order the result.

```
FOR doc IN users
    FILTER doc.status == "active"
    SORT doc.name
    LIMIT 10
```

#### Modifying Multiple Documents

Data-modification operations are normally combined with `FOR` loops to iterate over a given list of documents. They can 
optionally be combined with `FILTER` statements and the like.

##### Batch Update with Filter

Let's start with an example that modifies existing documents in a collection users that match some condition:

{% highlight javascript %}
FOR user IN users
    FILTER user.status == "not active"
    UPDATE user WITH { status: "inactive" } IN users
{% endhighlight %}

Now, let's copy the contents of the collection users into the collection backup:

{% highlight javascript %}
FOR user IN users
    INSERT user IN backup
{% endhighlight %}

##### Batch Delete with Filter

Subsequently, let's find some documents in `users` collection and remove them from collection `backup`. The link between
the documents in both collections is established via the documents' keys:

{% highlight javascript %}
FOR user IN users
    FILTER user.status == "deleted"
    REMOVE user IN backup
{% endhighlight %}

The following example will remove all documents from both `users` and `backup`:

{% highlight javascript %}
LET r1 = (FOR user IN users  REMOVE user IN users)
LET r2 = (FOR user IN backup REMOVE user IN backup)
RETURN true
{% endhighlight %}

#### Returning Documents

Data-modification queries can optionally return documents. In order to reference the inserted, removed or modified 
documents in a `RETURN` statement, data-modification statements introduce the **OLD** and/or **NEW** pseudo-values:

{% highlight javascript %}
FOR i IN 1..100
    INSERT { value: i } IN test
    RETURN NEW

FOR user IN users
    FILTER user.status == "deleted"
    REMOVE user IN users
    RETURN OLD

FOR user IN users
    FILTER user.status == "not active"
    UPDATE user WITH { status: "inactive" } IN users
    RETURN NEW
{% endhighlight %}

**NEW refers to the inserted or modified document revision, and OLD refers to the document revision before update or 
removal**. `INSERT` statements can only refer to the `NEW` pseudo-value, and `REMOVE` operations only to `OLD`.
`UPDATE`, `REPLACE` and `UPSERT` can refer to either.

In all cases the full documents will be returned with all their attributes, including the potentially auto-generated 
attributes such as `_id`, `_key`, or `_rev` and the attributes not specified in the update expression of a partial
update.

##### Projections

It is possible to return a projection of the documents in `OLD` or `NEW` instead of returning the entire documents. This 
can be used to reduce the amount of data returned by queries.

For example, the following query will return only the keys of the inserted documents:

{% highlight javascript %}
FOR i IN 1..100
    INSERT { value: i } IN test
    RETURN NEW._key
{% endhighlight %}

##### Using OLD and NEW in the same query

For `UPDATE`, `REPLACE` and `UPSERT` statements, both `OLD` and `NEW` can be used to return the previous revision of a 
document together with the updated revision:

{% highlight javascript %}
FOR user IN users
    FILTER user.status == "not active"
    UPDATE user WITH { status: "inactive" } IN users
    RETURN { old: OLD, new: NEW }
{% endhighlight %}

##### Calculations with OLD or NEW

It is also possible to run additional calculations with `LET` statements between the data-modification part and the
final `RETURN` of an AQL query. For example, the following query performs an upsert operation and returns whether an 
existing document was updated, or a new document was inserted. It does so by checking the `OLD` variable after the
`UPSERT` and using a `LET` statement to store a temporary string for the operation type:

{% highlight javascript %}
UPSERT { name: "test" }
    INSERT { name: "test" }
    UPDATE { } IN users
LET opType = IS_NULL(OLD) ? "insert" : "update"
RETURN { _key: NEW._key, type: opType }
{% endhighlight %}




















##### Filter

The `FILTER` statement can be used to restrict the results to elements that match an arbitrary logical condition.

###### General Syntax

    FILTER expression

`expression` must be a condition that evaluates to either true or false. A condition contains Operators.

##### Operators

AQL supports a number of operators that can be used in expressions. There are comparison, logical, arithmetic, and the
ternary operator.

###### Comparison Operators

Comparison (or relational) operators compare two operands. They can be used with any input data types, and will return a
boolean result value.

The following comparison operators are supported:

| Operator | Description                                                 |
|----------|-------------------------------------------------------------|
| ==       | equality                                                    |
| !=       | inequality                                                  |
| <        | less than                                                   |
| <=       | less than or equal to                                       |
| >        | greater than                                                |
| >=       | greater than or equal to                                    |
| IN       | test if a value is contained in an array                    |
| NOT IN   | test if a value is not contained in an array                |
| LIKE     | tests if a string value matches a pattern                   |
| NOT LIKE | tests if a string value does not match a pattern            |
| =~       | tests if a string value matches a regular expression        |
| !~       | tests if a string value does not match a regular expression |

The comparison operators accept any data types for the first and second operands. However, `IN` and `NOT IN` will only
return a meaningful result if their right-hand operand is an array. `LIKE` and `NOT LIKE` will only execute if both
operands are string values. All four operators will not perform implicit type casts if the compared operands have
different types, i.e. they test for strict equality or inequality (0 is different to "0", `[0]`, false and null for
example).

Some examples for comparison operations in AQL:

```
     0  ==  null            // false
     1  >   0               // true
  true  !=  null            // true
    45  <=  "yikes!"        // true
    65  !=  "65"            // true
    65  ==  65              // true
  1.23  >   1.32            // false
   1.5  IN  [ 2, 3, 1.5 ]   // true
 "foo"  IN  null            // false
42  NOT IN  [ 17, 40, 50 ]  // true
 "abc"  ==  "abc"           // true
 "abc"  ==  "ABC"           // false
 "foo"  LIKE  "f%"          // true
 "foo"  NOT LIKE  "f%"      // false
 "foo"  =~  "^f[o].$"       // true
 "foo"  !~  "[a-z]+bar$"    // true
```

The `LIKE` operator checks whether its left operand matches the pattern specified in its right operand. The pattern can
consist of regular characters and wildcards. The supported wildcards are `_` to match a single arbitrary character, and
`%` to match any number of arbitrary characters. Literal `%` and `_` need to be escaped with a backslash. Backslashes
need to be escaped themselves, which effectively means that two reverse solidus characters need to precede a literal
percent sign or underscore. In arangosh, additional escaping is required, making it four backslashes in total preceding
the to-be-escaped character.

```
    "abc" LIKE "a%"          // true
    "abc" LIKE "_bc"         // true
"a_b_foo" LIKE "a\\_b\\_foo" // true
```

The `LIKE` operator is case-sensitive.

The `NOT LIKE` operator has the same characteristics as the `LIKE` operator but with the result negated. It is thus
identical to `NOT (... LIKE ...)`.

The regular expression operators `=~` and `!~` expect their left-hand operands to be strings, and their right-hand
operands to be strings containing valid regular expressions as specified in the documentation for the AQL function
[`REGEX_TEST()`](https://www.arangodb.com/docs/stable/aql/functions-string.html#regex_test).

> ⚠️ Note that `LIKE` and `NOT LIKE` work the same way as MySQL `LIKE` and `NOT LIKE`. In order to do fuzzy search with
> Arango `LIKE`, we have to include the `%` or `_` in search query. For example, the following query is not correct,
> which is why no match records were found:
>
> ![Error loading arango-like-incorrect.png!]({{ "/assets/img/arango-like-incorrect.png" | relative_url}})
>
> The next query, however, returns the expected results:
>
> ![Error loading arango-like-correct.png!]({{ "/assets/img/arango-like-correct.png" | relative_url}})
>
> Notice the `%` character in the `LIKE` clause

##### Operations

###### LIMIT

The `LIMIT` statement allows slicing the result array using an offset (which specifies how many elements from the result
shall be skipped. It must be 0 or greater) and a count (which specifies how many, at most, elements should be included
in the result). It reduces the number of elements in the result to at most the specified number. Two general forms of
`LIMIT` are followed:

    LIMIT count
    LIMIT offset, count

The first form is identical using the second form with an offset value of 0.

> Variables, expressions and sub-queries CANNOT be used for offset and count. The values for offset and count must be
> known at query compile time, which means that you can only use number literals, bind parameters or expressions that
> can be resolved at query compile time.

> ⚠️ When LIMIT is placed before FILTER in query, LIMITing operation is executed before the FILTERing operations.

#### Restrictions

The name of the modified collection (users and backup in the above cases) must be known to the AQL executor at 
query-compile time and cannot change at runtime. Using a bind parameter to specify the collection name is allowed.

**It is not possible to use multiple data-modification operations for the same collection in the same query, or follow
up a data-modification operation for a specific collection with a read operation for the same collection. Neither is it 
possible to follow up any data-modification operation with a traversal query (which may read from arbitrary collections 
not necessarily known at the start of the traversal)**.

That means you may not place several REMOVE or UPDATE statements for the same collection into the same query. It is 
however possible to modify different collections by using multiple data-modification operations for different
collections in the same query. In case you have a query with several places that need to remove documents from the same 
collection, it is recommended to collect these documents or their keys in an array and have the documents from that
array removed using a single REMOVE operation.

Data-modification operations can optionally be followed by `LET` operations to perform further calculations and a
`RETURN` operation to return data.

#### Transactional Execution

On a single server, data-modification operations are executed transactionally. If a data-modification operation fails,
any changes made by it will be rolled back automatically as if they never happened.

If the RocksDB engine is used and intermediate commits are enabled, a query may execute intermediate transaction commits 
in case the running transaction (AQL query) hits the specified size thresholds. In this case, the query's operations 
carried out so far will be committed and not rolled back in case of a later abort/rollback. That behavior can be 
controlled by adjusting the intermediate commit settings for the RocksDB engine.

In a cluster, AQL data-modification queries are currently not executed transactionally. Additionally, update, replace, 
upsert and remove AQL queries currently require the _key attribute to be specified for all documents that should be 
modified or removed, even if a shard key attribute other than _key was chosen for the collection.


Graph
-----

There are multiple ways to work with graphs in ArangoDB, as well as different ways to query your graphs using AQL. The
two options in managing graphs are to either use

* named graphs where ArangoDB manages the collections involved in one graph, or
* graph functions on a combination of document and edge collections.

Named graphs can be defined through the
[graph-module](https://qubitpi.github.io/jersey-guide/finalized/2022/09/03/arango-general-graphs.html) or via the
[web interface](https://qubitpi.github.io/jersey-guide/finalized/2022/08/19/arangodb.html#web-interface). The definition 
contains the name of the graph, and the vertex and edge collections involved. Since the management functions are layered 
on top of simple sets of document and edge collections, you can also use regular AQL functions to work with them.

Both variants (named graphs and loosely coupled collection sets a.k.a. anonymous graphs) are supported by the AQL
language constructs for graph querying. These constructs make full use of optimizations and therefore best performance
is to be expected:

* [AQL Traversals](#graph-traversals-in-aql) to follow edges connected to a start vertex, up to a variable depth. It can 
  be combined with AQL filter conditions.
* [AQL Shortest Path](#shortest-path-in-aql) to find the vertices and edges between two given vertices, with as few hops 
  as possible.

These types of queries are only useful if you use edge collections and/or graphs in your data model.

### Traversals Explained

A traversal starts at one specific document (_startVertex_) and follows all edges connected to this document. For all 
documents (_vertices_) that are targeted by these edges it will again follow all edges connected to them and so on. It
is possible to define how many of these follow iterations should be executed at least (_min_ depth) and at most (_max_ 
depth).

For all vertices that were visited during this process in the range between min depth and max depth iterations we will
get a result in form of a set with three items:

1. The **visited vertex**.
2. The edge pointing to it.
3. The complete path from startVertex to the **visited vertex** as object with an attribute _edges_ and an attribute 
   _vertices_, each of which is a list of corresponding elements. These lists are sorted, which means the first element
   in _vertices_ is the startVertex and the last is the visited vertex, and the n-th element in _edges_ connects the
   n-th element with the (n+1)-th element in _vertices_.

Let's take a look at a simple example to explain how it works. This is the graph that we are going to traverse:

![Error loading arango-graph-traversal-eg.png]({{ "/assets/img/arango-graph-traversal-eg.png" | relative_url}})

We use the following parameters for our query:

* We start at the vertex A.
* We use a min depth of 1.
* We use a max depth of 2.
* We follow only in OUTBOUND edges

![Error loading arango-graph-traversal-eg1.png]({{ "/assets/img/arango-graph-traversal-eg1.png" | relative_url}})

Now it walks to one of the direct neighbors of **A**, say **B** (note: ordering is not guaranteed!):

![Error loading arango-graph-traversal-eg2.png]({{ "/assets/img/arango-graph-traversal-eg2.png" | relative_url}})

The query will remember the state (red circle) and will emit the first result **A → B** (black box). This will also 
prevent the traverser to be trapped in cycles. Now again it will visit one of the direct neighbors of **B**, say **E**:

![Error loading arango-graph-traversal-eg3.png]({{ "/assets/img/arango-graph-traversal-eg3.png" | relative_url}})

We have limited the query with a max depth of 2, so it will not pick any neighbor of **E**, as the path from **A** to 
**E** already requires 2 steps. Instead, we will go back one level to **B** and continue with any other direct neighbor
there:

![Error loading arango-graph-traversal-eg4.png]({{ "/assets/img/arango-graph-traversal-eg4.png" | relative_url}})

Again after we produced this result we will step back to **B**. But there is no neighbor of **B** left that we have not 
yet visited. Hence we go another step back to **A** and continue with any other neighbor there.

![Error loading arango-graph-traversal-eg5.png]({{ "/assets/img/arango-graph-traversal-eg5.png" | relative_url}})

And identical to the iterations before we will visit **H**:

![Error loading arango-graph-traversal-eg6.png]({{ "/assets/img/arango-graph-traversal-eg6.png" | relative_url}})

And **J**:

![Error loading arango-graph-traversal-eg7.png]({{ "/assets/img/arango-graph-traversal-eg7.png" | relative_url}})

After these steps there is no further result left. So all together this query has returned the following paths:

* **A → B**
* **A → B → E**
* **A → B → C**
* **A → G**
* **A → G → H**
* **A → G → J**

### Graph Traversals in AQL

There are two slightly different syntaxes for traversals in AQL, one for named graphs and another to set of edge 
collections (anonymous graph).

#### Traversing Named Graphs

{% highlight javascript %}
[WITH vertexCollection1[, vertexCollection2[, ...vertexCollectionN]]]
FOR vertex[, edge[, path]]
    IN [min[..max]]
    OUTBOUND|INBOUND|ANY startVertex
    GRAPH graphName
    [PRUNE pruneCondition]
    [OPTIONS options]
{% endhighlight %}

* `WITH`: optional for single server instances, but required for graph traversals in a cluster.
  - **collections** (collection, _repeatable_): list of vertex collections that will be involved in the traversal
* `FOR`: emits up to three variables:
  1. **vertex** (object): the current vertex in a traversal
  2. **edge** (object, _optional_): the current edge in a traversal
  3. **path** (object, _optional_): the current path with two members:
     - vertices: an array of all vertices on this path
     - edges: an array of all edges on this path
* `IN min..max`: the minimal and maximal depth for the traversal:
  1. **min** (number, _optional_): edges and vertices returned by this query will start at the traversal depth of _min_
     (thus edges and vertices below will not be returned). If not specified, it defaults to 1. The minimal possible
     value is 0.
  2. **max** (number, _optional_): up to _max_ length paths are traversed. **If omitted, _max_ defaults to _min_. Thus
     only the vertices and edges in the range of _min_ are returned. _max_ can not be specified without _min_.
* `OUTBOUND|INBOUND|ANY`: follow outgoing, incoming, or edges pointing in either direction in the traversal; Please note
  that this can't be replaced by a bind parameter.
* **startVertex** (string|object): a vertex where the traversal will originate from. This can be specified in the form
  of an ID string or in the form of a document with the attribute _id. All other values will lead to a warning and an
  empty result. If the specified document does not exist, the result is empty as well and there is no warning.
* `GRAPH` **graphName** (string): the name identifying the named graph. Its vertex and edge collections will be looked
  up. Note that the graph name is like a regular string, hence it must be enclosed by quotation marks.
* `PRUNE` **condition** (AQL condition, _optional_, (since version 3.4.5)): A condition, like in a "FILTER" statement,
  which will be evaluated in **every step of the traversal, as early as possible**. The semantics of this condition is
  as follows:
  - If the condition evaluates to `true` this path will be considered as a result, it might still be post filtered or
    ignored due to depth constraints. The search, however, will not continue from this path, namely there will be no
    result having this path as a prefix. e.g.: Take the path: (A) -> (B) -> (C) starting at A and PRUNE on B will result 
    in (A) and (A) -> (B) being valid paths, and (A) -> (B) -> (C) not returned, it got pruned on B.
  - If the condition evaluates to `false` we will continue traversing down this path. The PRUNE condition can contain combinations of AND/OR statements.
* `OPTIONS` **options** (object, _optional_): used to modify the execution of the traversal. Attributes include
  - **order** (string):
    * **"bfs"** - the traversal will be a breadth-first traversal. The results will have all vertices at depth 1 in the 
      front of list, then all vertices at depth 2, and so on
    * **"dfs" (default)** - the traversal will be a depth-first traversa. It put all paths from min depth to max depth 
      for one vertex at depth 1 first, then for the next vertex at depth 1 and so on
    * **"weighted"** - Paths are enumerated with increasing cost. A path has an additional attribute **weight**
      containing the cost of the path after every step. The order of paths having the same cost is non-deterministic. 
      Negative weights are not supported and will abort the query with an error
  - **uniqueVertices** (string, _optional_):
    * "path" - each returned path has no duplicated vertices
    * "global" - each vertex is visited at most once during traversal. It is required to set **order** with "bfs" or 
      "weighted" because depth-first search would give unpredictable results. With this configuration the result is not 
      deterministic anymore. If there are multiple paths from _startVertex_ to _vertex_, one of those is picked. In case
      of a weighted traversal, the path with the lowest weight is picked, but in case of equal weights it is undefined 
      which one is chosen.
      "none" (default) - no uniqueness check is applied on vertices 
  - **uniqueEdges** (string, _optional_)
    * "path" (default) - each returned path has no duplicated edges
    * "none" - no uniqueness check is applied on edges. Note: Using this configuration the traversal will follow edges
      in cycles.
  - **edgeCollections** (string|array, _optional_): specify edges which the traversal is allowed to visit
  - **vertexCollections** (string|array, _optional_): specify vertices which the traversal is allowed to visit. The 
    starting vertex is always allowed
  - **weightAttribute** (string, _optional_): Specifies the name of an attribute that is used to look up the weight of
    an edge. If no attribute is specified or if it is not present in the edge document then the `defaultWeight` is used. 
    The attribute value must not be negative
  - **defaultWeight** (number, _optional_): Specifies the default weight of an edge. The value must not be negative. The 
    default value is 1.

#### Traversing Collections Sets

{% highlight javascript %}
[WITH vertexCollection1[, vertexCollection2[, ...vertexCollectionN]]]
FOR vertex[, edge[, path]]
    IN [min[..max]]
    OUTBOUND|INBOUND|ANY startVertex
    edgeCollection1, ..., edgeCollectionN
    [PRUNE pruneCondition]
    [OPTIONS options]
{% endhighlight %}

Instead of `GRAPH graphName` we may specify a list of edge collections. Vertex collections are determined by the edges
in the edge collections. The traversal options are the same as with the [named graph variant](#traversing-named-graphs), 
though the `edgeCollections` restriction option is redundant in this case.

If the same edge collection is specified multiple times, it will behave as if it were specified only once. Specifying
the same edge collection is only allowed when the collections do not have conflicting traversal directions.

ArangoSearch Views cannot be used as edge collections.

#### Traversing in Mixed Directions

For traversals with a list of edge collections you can optionally specify the direction for some of the edge
collections. For example, when we have three edge collections edges1, edges2 and edges3, where in edges2 the direction
has no relevance but in edges1 and edges3 the direction should be taken into account. In this case you can use OUTBOUND
as general traversal direction and ANY for edges2 as follows:

{% highlight javascript %}
FOR vertex IN OUTBOUND
    startVertex
    edges1, ANY edges2, edges3
{% endhighlight %}

#### Using Filters and the Explainer to Extrapolate the Costs

WIP

### Shortest Path in AQL

### k-Shortest Paths in AQL

### k Paths in AQL