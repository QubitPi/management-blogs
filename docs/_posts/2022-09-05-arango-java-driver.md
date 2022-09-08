---
layout: post
title: ArangoDB Java Driver
tags: [ArangoDB, Database, Knowledge Graph, Java]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/10-cover.png"
thumbnail: "assets/img/post-cover/10-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

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
// this instance is thread-safe
ArangoDB arangoDB = new ArangoDB.Builder()
    .serializer(new ArangoJack())
    .build();
{% endhighlight %}

> 📋 The default connection is to 127.0.0.1:8529.

### Driver Setup

The setup above goes with default configuration. A properties file called "**arangodb.properties**" is automatically
loaded if exists in the classpath.

The driver is configured with the following default values:

| **property-key**         | **description**                         | **default value** |
|:------------------------:|:---------------------------------------:|:-----------------:|
| arangodb.hosts           | ArangoDB hosts                          | 127.0.0.1:8529    |
| arangodb.timeout         | connect & request timeout (millisecond) | 0                 |
| arangodb.user            | Basic Authentication User               | root              |
| arangodb.password        | Basic Authentication Password           |                   |
| arangodb.jwt             | Authentication JWT                      |                   |
| arangodb.useSsl          | use SSL connection                      | false             |
| arangodb.chunksize       | VelocyStream Chunk content-size (bytes) | 30000             |
| arangodb.connections.max | max number of connections               | 1 VST, 20 HTTP    |
| arangodb.protocol        | used network protocol                   | VST               |

To customize the configuration the parameters can be changed in the following way:

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .host("192.168.182.50", 8888)
    .build();
{% endhighlight %}

or with a custom properties file (e.g. "my.properties")

{% highlight java %}
InputStream in = MyClass.class.getResourceAsStream("my.properties");
ArangoDB arangoDB = new ArangoDB.Builder()
    .loadProperties(in)
    .build();
{% endhighlight %}

The my.properties file looks like the following

```properties
arangodb.hosts=127.0.0.1:8529,127.0.0.1:8529
arangodb.user=root
arangodb.password=
```

### Network protocol

The drivers default used network protocol is the binary protocol VelocyStream. To use HTTP, we can set the configuration 
`useProtocol` to **Protocol.HTTP\_JSON** for HTTP with JSON content or **Protocol.HTTP\_VPACK** for HTTP with VelocyPack 
content. For example

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .useProtocol(Protocol.VST)
    .build();
{% endhighlight %}

In case we do set the configuration to HTTP, we have to add the apache httpclient to classpath as well.

{% highlight java %}
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.1</version>
</dependency>
{% endhighlight %}

### SSL

To use SSL, you have to set the configuration `useSsl` to `true` and set a `SSLContext`.

{% highlight java %}
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

ArangoDB arangoDB = new ArangoDB.Builder()
    .useSsl(true)
    .sslContext(createSslContext())
    .build();

private SSLContext createSslContext() {
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(this.getClass().getResourceAsStream(SSL_TRUSTSTORE), SSL_TRUSTSTORE_PASSWORD.toCharArray());

    final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(ks, SSL_TRUSTSTORE_PASSWORD.toCharArray());

    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);

    final SSLContext sc = SSLContext.getInstance("TLS");
    sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

    return sc;
}
{% endhighlight %}

> No additional configuration is required to use TLSv1.3 (if available on the server side), but a JVM that supports it
> is required (OpenJDK 11 or later, or distributions of Java 8 with TLSv1.3 support).

### Connection Pooling

The driver supports connection pooling for VelocyStream with a default of 1 and HTTP with a default of 20 maximum connections per host. To change this value use the method **maxConnections(Integer)** in ArangoDB.Builder

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .maxConnections(8)
    .build();
{% endhighlight %}

> ⚠️ The driver does not explicitly release connections. To avoid exhaustion of resources when no connection is needed,
> we can clear the connection pool (close all connections to the server) or use
> [connection TTL](#connection-time-to-live).
> 
> ```java
> arangoDB.shutdown();
> ```
> 
> Opening and closing connections very frequently can exhaust the amount of connections allowed by the operating system. 
> TCP connections enter a special state `WAIT_TIME` after close, and typically remain in this state for two minutes 
> (maximum segment life * 2). These connections count towards the global limit, which depends on the operating system
> but is usually around 28,000. Thus **connections should thus be reused as much as possible**.

### Thread Safety

The driver can be used concurrently by multiple threads. All the following classes are thread safe:

* com.arangodb.ArangoDB
* com.arangodb.ArangoDatabase
* com.arangodb.ArangoCollection
* com.arangodb.ArangoGraph
* com.arangodb.ArangoVertexCollection
* com.arangodb.ArangoEdgeCollection
* com.arangodb.ArangoView
* com.arangodb.ArangoSearch

Any other class should not be considered thread safe. In particular classes representing request options (e.g.
com.arangodb.model) and response entities (e.g. com.arangodb.entity) are not thread safe.

### Fallback Hosts

The driver supports configuring multiple hosts. The first host is used to open a connection to. When this host is not 
reachable the next host from the list is used. To use this feature just call the method `host(String, int)` multiple
times.

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .host("host1", 8529)
    .host("host2", 8529)
    .build();
{% endhighlight %}

Since version 4.3 the driver support acquiring a list of known hosts in a cluster setup or a **single server setup with 
followers**. In the latter case the driver has to be able to successfully open a connection to at least one host to get 
the list of hosts. Then it can use this list when fallback is needed. To use this feature just pass `true` to the method 
`acquireHostList(boolean)`.

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .acquireHostList(true)
    .build();
{% endhighlight %}

### Load Balancing

Since version 4.3 the driver supports load balancing for cluster setups in two different ways.

#### Round Robin

A round robin load balancing has the driver iterates through a list of known hosts and performs every request on a 
different host than the request before.

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .loadBalancingStrategy(LoadBalancingStrategy.ROUND_ROBIN)
    .build();
{% endhighlight %}

Just like the [Fallback hosts feature](#fallback-hosts) the round robin load balancing strategy can use the 
`acquireHostList` configuration to acquire a list of all known hosts in the cluster. Doing so only requires the manually 
configuration of only one host. Because this list is updated frequently it makes load balancing over the whole cluster 
very comfortable.

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .loadBalancingStrategy(LoadBalancingStrategy.ROUND_ROBIN)
    .acquireHostList(true)
    .build();
{% endhighlight %}

#### Random with Session Support

The second load balancing strategy allows to pick a random host from the configured or acquired list of hosts and sticks 
to that host as long as the connection is open. This strategy is useful for an application - using the driver - which 
provides a session management where each session has its own instance of `ArangoDB` build from a global configured list
of hosts. In this case it could be wanted that every sessions sticks with all its requests to the same host but not all 
sessions should use the same host. This load balancing strategy also works together with
[`acquireHostList`](#fallback-hosts).

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .loadBalancingStrategy(LoadBalancingStrategy.ONE_RANDOM)
    .acquireHostList(true)
    .build();
{% endhighlight %}

### Active Failover

In case of an [Active Failover deployment][Active Failover deployment] the driver should be configured in the following
way:

* the load balancing strategy must be either set to **LoadBalancingStrategy.NONE** or not set at all, since that would be the default
* [acquireHostList](#fallback-hosts) should be set to `true`

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .loadBalancingStrategy(LoadBalancingStrategy.NONE)
    .acquireHostList(true)
    .build();
{% endhighlight %}

### VST Keep-Alive

Since version 6.8 the driver supports setting keep-alive interval (in seconds) for VST connections. If set, every VST 
connection will perform a no-op request at the specified intervals, to avoid to be closed due to inactivity by the
server (or by the external environment, e.g. firewall, intermediate routers, operating system, ...).

This option can be set using the key `arangodb.connections.keepAlive.interval` in the properties file or
programmatically from the driver builder:

{% highlight java %}
ArangoDB arangoDB = new ArangoDB.Builder()
    .keepAliveInterval(1800) // 30 minutes
    .build();
{% endhighlight %}

If not set or set to `null` (default), no keep-alive probes will be sent.

### Connection Time to Live

Since version 4.4 the driver supports setting a TTL (time to life) in **milliseconds** for connections managed by the 
internal [connection pool](#connection-pooling)

{% highlight java %}
ArangoDB arango = new ArangoDB.Builder()
    .connectionTtl(5 * 60 * 1000)
    .build();
{% endhighlight %}

In this example all connections will be closed/reopened after 5 minutes.

Connection TTL can be disabled setting it to null:

{% highlight java %}
.connectionTtl(null)
{% endhighlight %}

> The default TTL is `null` (no automatic connection closure).


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

Arango Java driver offers ability to execute AQL programmatically through 2 separate abstraction layers:

1. ArangoDatabase with **ArangoDatabase#query()**
2. ArangoDatabaseAsync with **ArangoDatabaseAsync#query()**

> 📋 Application design should take it into account that [ArangoDatabase][ArangoDatabase] and
> [ArangoDatabaseAsync][ArangoDatabaseAsync] are loaded as two _separate_ types. Application, should it offers the
> ability to switch between syanc and async querying on the flight, shall not depend on the driver's interface but have 
> its own abstraction layer

For example, suppose we have a "java_driver_graph_test_db" database which has a [named graph](named graph) 
"traversalGraph" with one edge collection (called "edges") and one vertex collection (named "circles")

> The examples along with their setup above are taken from the valid tests from official Arango Java Driver, both
> [sync][GraphTraversalsInAQLExampleTest Sync] and [async][GraphTraversalsInAQLExampleTest Async].

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
> ```java
> ArangoDatabase db = ...
>
> if (!db.exists()) {
>     // execute logic on non-existing databases
> }
> ```
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
[Active Failover deployment]: https://qubitpi.github.io/jersey-guide/finalized/2022/09/05/arango-architecture.html#active-failover
