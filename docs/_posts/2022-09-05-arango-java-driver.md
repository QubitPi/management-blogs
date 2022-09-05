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

The _jackson-dataformat-velocypack_ package also depends on **jackson-core**, **jackson-databind**, and 
**jackson-annotations** packages, but when using build tools like Maven, dependencies are automatically included. We may 
however want to use [jackson-bom](https://github.com/FasterXML/jackson-bom) to ensure dependency convergence across the 
entire project, for example in case there are in our project other libraries depending on different versions of the same 
Jackson packages.

{% highlight xml %}
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson</groupId>
            <artifactId>jackson-bom</artifactId>
            <version>...</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
{% endhighlight %}

> jackson-dataformat-velocypack is compatible with Jackson 2.10, 2.11, 2.12, and 2.13.


Connection
----------

To configure and open a connection to start ArangoDB:

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .serializer(new ArangoJack())
    .build();
{% endhighlight %}

> 📋 The default connection is to 127.0.0.1:8529.

### Configuring Serialization

Instance of `ArangoJack` offers the ability to configure the underlying `ObjectMapper`. `ArangoJack` can then be passed
to the driver through `ArangoDB.Builder.serializer(ArangoSerialization)`:

{% highlight java %}
ArangoJack arangoJack = new ArangoJack();
arangoJack.configure((mapper) -> {
    // your configuration here
});

ArangoDB arango = new ArangoDB.Builder()
    .serializer(arangoJack)
    // ...
    .build();
{% endhighlight %}

where the lambda argument `mapper` is an instance of `VPackMapper`, a subclass of `ObjectMapper`

#### Renaming Properties

To use a different serialized name for a field, use the annotation @JsonProperty

{% highlight java %}
public class MyObject {

    @JsonProperty("title")
    private String name;

    // ...
}
{% endhighlight %}

#### Ignoring Properties

To ignore fields use the annotation @JsonIgnore

{% highlight java %}
public class Value {

    @JsonIgnore
    public int internalValue;
}
{% endhighlight %}

#### Custom Serializer

The serialization and deserialization can be customized using the lower level Streaming API or the Tree Model API, 
creating and registering respectively `JsonSerializer<T>` and `JsonDeserializer<T>`, as specified by the Jackson API for 
CustomSerializers.

{% highlight java %}
static class PersonSerializer extends JsonSerializer<Person> {
    
    @Override
    public void serialize(Person value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // example using the Streaming API
        gen.writeStartObject();
        gen.writeFieldName("name");
        gen.writeString(value.name);
        gen.writeEndObject();
    }
}

static class PersonDeserializer extends JsonDeserializer<Person> {
    
    @Override
    public Person deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        // example using the Tree Model API
        Person person = new Person();
        JsonNode rootNode = parser.getCodec().readTree(parser);
        JsonNode nameNode = rootNode.get("name");
        if (nameNode != null && nameNode.isTextual()) {
            person.name = nameNode.asText();
        }
        return person;
    }
}

// registering using annotation
@JsonSerialize(using = PersonSerializer.class)
public static class Person {
    public String name;
}

// ...

// registering programmatically
ArangoJack arangoJack = new ArangoJack();
arangoJack.configure((mapper) -> {
    SimpleModule module = new SimpleModule("PersonModule");
    module.addDeserializer(Person.class, new PersonDeserializer());
    mapper.registerModule(module);
});
ArangoDB arangoDB = new ArangoDB.Builder().serializer(arangoJack).build();
{% endhighlight %}





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


Updating a Document
-------------------

{% highlight java %}
doc.addAttribute("c", "Bar");
collection.updateDocument(key, doc);
{% endhighlight %}


Deleting a Document
-------------------

{% highlight java %}
collection.deleteDocument(key);
{% endhighlight %}


Executing AQL Query
-------------------

[Arango Java driver offers ability to execute AQL programmatically][GraphTraversalsInAQLExampleTest] through 2 separate 
abstraction layers:

1. ArangoDatabase with **ArangoDatabase#query()**
2. ArangoDatabaseAsync with **ArangoDatabaseAsync#query()**

> Application design should take it into account that
> [ArangoDatabase][ArangoDatabase] and [ArangoDatabaseAsync][ArangoDatabaseAsync] are two separate types. Application,
> should it offers the ability to switch between syanc and async querying on the flight, should not depend on the
> driver's interface but have its own abstraction layer

For example, suppose we have a "java_driver_graph_test_db" database which has a [named graph](named graph) 
"traversalGraph" with one edge collection (called "edges") and one vertex collection (named "circles")

> The examples along with their setup above are taken from the valid tests from official Arango Java Driver, both
> [sync](GraphTraversalsInAQLExampleTest Sync) and [asyn](GraphTraversalsInAQLExampleTest Async).

To create a "**sync**" version of ArangoDB client:

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .serializer(new ArangoJack())
    .build();
ArangoDatabase syncDb = arangoDB.db(DbName.of("java_driver_graph_test_db"))
{% endhighlight %}

To create an "**async**" version of the client:

{% highlight java %}
ArangoDB arangoDB = new ArangoDBAsync.Builder()
    .serializer(new ArangoJack())
    .build();
ArangoDatabase asyncDb = arangoDB.db(DbName.of("java_driver_graph_test_db"))
{% endhighlight %}

The following example shows an how the query below can be issued against database:

{% highlight javascript %}
FOR vertex
    IN 1..3
    OUTBOUND
    'circles/A'
    GRAPH 'traversalGraph'
    FILTER vertex.radius == 10
    RETURN vertex._key
{% endhighlight %}

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
> 
> The examples below will assume the above and omit the check for the purpose of brevity

{% highlight java %}
String queryString = "FOR vertex IN 1..3 OUTBOUND 'circles/A' GRAPH 'traversalGraph' FILTER vertex.radius == @radius RETURN vertex._key";

Map<String, Object> bindVars = Collections.singletonMap("radius", 10);

ArangoCursor<BaseDocument> cursor = syncDb.query(queryString, bindVars, null, BaseDocument.class);
ArangoCursorAsync<BaseDocument> cursor = asyncDb.query(queryString, bindVars, null, BaseDocument.class).get();
{% endhighlight %}

> 📋 Note that the AQL query uses the @radius placeholder which has to be bound to a value


[named graph]: https://qubitpi.github.io/jersey-guide/finalized/2022/09/03/arango-general-graphs.html#named-graphs
[ArangoDatabase]: https://github.com/arangodb/arangodb-java-driver/blob/master/src/main/java/com/arangodb/ArangoDatabase.java
[ArangoDatabaseAsync]: https://github.com/arangodb/arangodb-java-driver/blob/master/src/main/java/com/arangodb/async/ArangoDatabaseAsync.java
[GraphTraversalsInAQLExampleTest Sync]: https://github.com/arangodb/arangodb-java-driver/blob/master/src/test/java/com/arangodb/example/graph/GraphTraversalsInAQLExampleTest.java
[GraphTraversalsInAQLExampleTest Async]: https://github.com/arangodb/arangodb-java-driver/blob/master/src/test/java/com/arangodb/async/example/graph/GraphTraversalsInAQLExampleTest.java
