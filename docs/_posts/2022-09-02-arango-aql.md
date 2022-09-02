---
layout: post
title: ArangoDB Query Language
tags: [ArangoDB, Database, Knowledge Graph]
category: FINISHED
color: rgb(86, 113, 56)
feature-img: "assets/img/post-cover/7-cover.png"
thumbnail: "assets/img/post-cover/7-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


ArangoDB Query Language (AQL)
-----------------------------

### AQL Syntax

#### Query Types

An AQL query must either

* return a result (indicated by usage of the RETURN keyword) or
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

```
/* this is a comment */ RETURN 1
/* these */ RETURN /* are */ 1 /* multiple */ + /* comments */ 1
/* this is
   a multi line
   comment */
// a single line comment
```

An example AQL query may look like this:

```
FOR u IN users
    FILTER u.type == "newbie" && u.active == true
    RETURN u.name
```

### Data Queries

#### Data Access Queries

Retrieving data from the database with AQL does always include a `RETURN` operation, which is usually accompanied by a
`FOR` loop to iterate over the documents of a collection. The following query executes the loop body for all documents
of a collection called `users`. Each document is returned unchanged in this example:

```
FOR doc IN users
    RETURN doc
```

Instead of returning the raw `doc`, one can easily create a projection:

```
FOR doc IN users
    RETURN { user: doc, newAttribute: true }
```

For every user document, an object with two attributes is returned. The value of the attribute `user` is set to the
content of the user document, and `newAttribute` is a static attribute with the boolean value `true.

Operations like [`FILTER`](#filter), `SORT`, and `LIMIT` can be added to the loop body to narrow and order the result.

```
FOR doc IN users
    FILTER doc.status == "active"
    SORT doc.name
    LIMIT 10
```

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

### Graph

#### Traversal

##### Traversing Named Graph

```
[WITH vertexCollection1[, vertexCollection2[, ...vertexCollectionN]]]
FOR vertex[, edge[, path]]
IN [min[..max]]
OUTBOUND|INBOUND|ANY startVertex
GRAPH graphName
[PRUNE pruneCondition]
[OPTIONS options]
```

* `WITH`: optional for single server instances, but required for graph traversals in a cluster.
  - collections (collection, _repeatable_): list of vertex collections that will be involved in the traversal
* `FOR`: emits up to three variables:
  1. **vertex** (object): the current vertex in a traversal
  2. **edge** (object, _optional_): the current edge in a traversal
  3. **path** (object, _optional_): representation of the current path with two members:
    1. vertices: an array of all vertices on this path
    2. edges: an array of all edges on this path
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
    ignored due to depth constraints. However the search will not continue from this path, namely there will be no result having this path as a prefix. e.g.: Take the path: (A) -> (B) -> (C) starting at A and PRUNE on B will result in (A) and (A) -> (B) being valid paths, and (A) -> (B) -> (C) not returned, it got pruned on B.


##### Working with Collections Sets

```
[WITH vertexCollection1[, vertexCollection2[, ...vertexCollectionN]]]
FOR vertex[, edge[, path]]
IN [min[..max]]
OUTBOUND|INBOUND|ANY startVertex
edgeCollection1, ..., edgeCollectionN
[PRUNE pruneCondition]
[OPTIONS options]
```