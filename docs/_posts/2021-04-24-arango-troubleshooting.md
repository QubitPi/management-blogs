---
layout: post
title: ArangoDB Reference
tags: [ArangoDB, Database, Graph Data, Best Practices]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## [Always Check for DB Existence Before Executing Query](https://github.com/arangodb/arangodb-java-driver/issues/254)

Otherwise a runtime exception will break the application with the following error: 

```
Cause:class com.arangodb.ArangoDBException --> Msg:Response: 404, Error: 1228 - database not found
com.arangodb.ArangoDBException: Response: 404, Error: 1228 - database not found
```

We should make sure the table exists before executing query using, for example:

```java
ArangoDatabase db = ...

if (!db.exists()) {
    // execute logic on non-existing databases
}
```
