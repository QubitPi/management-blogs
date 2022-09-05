---
layout: post
title: ArangoDB Java Driver
tags: [ArangoDB, Database, Knowledge Graph, Java]
category: FINALIZED
color: rgb(178, 105, 61)
feature-img: "assets/img/post-cover/10-cover.png"
thumbnail: "assets/img/post-cover/10-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->


Project Configuration
---------------------

To use the ArangoDB Java driver you need to import 2 libraries into your project:

1. [arangodb-java-driver](https://github.com/arangodb/arangodb-java-driver): the driver itself
2. [jackson-dataformat-velocypack](https://github.com/arangodb/jackson-dataformat-velocypack): a data format backend 
   implementation enabling VelocyPack support for [Jackson Databind API](https://github.com/FasterXML/jackson-databind).

In a Maven project, you need to add the following dependencies to POM file:

{% highlight xml %}
<dependencies>
    <dependency>
        <groupId>com.arangodb</groupId>
        <artifactId>arangodb-java-driver</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>com.arangodb</groupId>
        <artifactId>jackson-dataformat-velocypack</artifactId>
        <version>...</version>
    </dependency>
</dependencies>
{% endhighlight %}


Connection
----------

To configure and open a connection to start ArangoDB:

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .serializer(new ArangoJack())
    .build();
{% endhighlight %}

> 📋 The default connection is to 127.0.0.1:8529.


Creating a Database
-------------------

{% highlight java %}
ArangoDatabase db = arangoDB.db(DbName.of("mydb"));
db.create();
{% endhighlight %}


Creating a Collection
---------------------

{% highlight java %}
ArangoCollection collection = db.collection("firstCollection");
collection.create();
{% endhighlight %}

### Get Collection Size

{% highlight java %}
arangoDb.db(databaseName).collection(collectionName).count().getCount();
{% endhighlight %}


Creating a Document
-------------------

After we have [created the collection](#creating-a-collection), you can add documents to it. Any object can be added as
a document to the database and be retrieved from the database as an object.

In this example, the [**BaseDocument**](#basedocument) class provided with the driver is used. The attributes of the 
document are stored in a map as key/value pair:

{% highlight java %}
String key = "myKey";
BaseDocument doc = new BaseDocument(key);
doc.addAttribute("a", "Foo");
doc.addAttribute("b", 42);
collection.insertDocument(doc);
{% endhighlight %}

### BaseDocument

In a NoSQL database it is common to retrieve documents with an unknown attribute structure. Furthermore, the amount and 
types of attributes may differ in documents resulting from a single query.

With the latest version of the Java driver of ArangoDB an object called **BaseDocument** is provided. The structure is
very simple. It only has four attributes:

{% highlight java %}
public class BaseDocument {

    String id;
    String key;
    String revision;
    Map<String, Object> properties;
}
{% endhighlight %}

The first three attributes are the system attributes `_id`, `_key`, and `_rev`. The fourth attribute is a HashMap. The
key is a String, the value an object. These properties contain all non system attributes of the document. The map can 
contain values of the following types:

* `Map<String, Object>`
* `List<Object>`
* `Boolean`
* `Number`
* `String`
* `null`

To retrieve a document we use BaseDocument as type:

{% highlight java %}
ArangoDB.Builder arango = new ArangoDB.Builder().builder();
DocumentEntity<BaseDocument> myObject = arango
        .db()
        .collection("myCollection")
        .getDocument("myDocumentKey", BaseDocument.class);
{% endhighlight %}




Reading a Document
------------------

{% highlight java %}
BaseDocument readDocument = collection.getDocument(key, BaseDocument.class);
{% endhighlight %}

### Read a Document as Jackson JsonNode

{% highlight java %}
JsonNode jsonNode = collection.getDocument(key, ObjectNode.class);
{% endhighlight %}


Executing AQL Query
-------------------

[Arango Java driver offers ability to execute AQL programmatically](https://github.com/QubitPi/arangodb-java-driver/blob/master/src/test/java/com/arangodb/example/graph/GraphTraversalsInAQLExampleTest.java). The method is **ArangoDatabase#query()**.

For example, suppose we have a named graph "traversalGraph"

> The examples along with their setup above are taken from the
> [valid tests from official Arango Java Driver](https://github.com/QubitPi/arangodb-java-driver/tree/master/src/test/java/com/arangodb/example/graph)

### Example - Querying All Vertices

```java
        String queryString = "FOR v IN 1..3 OUTBOUND 'circles/A' GRAPH 'traversalGraph' RETURN v._key";
        ArangoCursor<String> cursor = db.query(queryString, null, null, String.class);
        Collection<String> result = cursor.asListRemaining();
        assertThat(result).hasSize(10);

        queryString = "WITH circles FOR v IN 1..3 OUTBOUND 'circles/A' edges RETURN v._key";
        cursor = db.query(queryString, null, null, String.class);
        result = cursor.asListRemaining();
        assertThat(result).hasSize(10);
```

>  ⚠️ [**Always Check for DB Existence Before Executing Query**](https://github.com/arangodb/arangodb-java-driver/issues/254),
> otherwise a runtime exception will break the application with the following error:
> 
> ```
> Cause:class com.arangodb.ArangoDBException --> Msg:Response: 404, Error: 1228 - database not found
> com.arangodb.ArangoDBException: Response: 404, Error: 1228 - database not found
> ```
> 
> We should make sure the table exists before executing query using, for example:
> 
> {% highlight java %}
> ArangoDatabase db = ...
> 
> if (!db.exists()) {
>     // execute logic on non-existing databases
> }
> {% endhighlight %}