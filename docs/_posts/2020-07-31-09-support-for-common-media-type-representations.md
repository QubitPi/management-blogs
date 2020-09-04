---
layout: post
title: Support for Common Media Type Representations
tags: [JSON, Jackson, XML]
color: rgb(0, 191, 255)
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## JSON

Jersey JSON support comes as a set of extension modules where each of these modules contains an implementation of a
`Feature` that needs to be registered into your `Configurable` instance (client/server). There are multiple frameworks
that provide support for JSON processing and/or JSON-to-Java binding. The modules listed below provide support for JSON
representations by integrating the individual JSON frameworks into Jersey. At present, Jersey integrates with the
following modules to provide JSON support: 

* **[MOXy](#moxy)** - JSON binding support via MOXy is a default and preferred way of supporting JSON binding in your
  Jersey applications since Jersey 2.0. When JSON MOXy module is on the class-path, Jersey will automatically discover
  the module and seamlessly enable JSON binding support via MOXy in your applications. (See Section
  [Auto-Discoverable Features](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html#auto-discoverable-features).)
* **[Java API for JSON Processing (JSON-P)](#java-api-for-json-processing-json-p)**
* **[Jackson](#jackson-1x-and-2x)**
* **[Jettison](#jettison)**

### Approaches to JSON Support

Each of the aforementioned extension modules uses one or more of the three basic approaches available when working with
JSON representations: 

* POJO based JSON binding support
* JAXB based JSON binding support
* Low-level JSON parsing & processing support

The first method is pretty generic and allows you to map any Java Object to JSON and vice versa. The other two
approaches limit you in Java types your resource methods could produce and/or consume. JAXB based approach is useful if
you plan to utilize certain JAXB features and support both XML and JSON representations. The last, low-level, approach
gives you the best fine-grained control over the out-coming JSON data format.

#### POJO support

POJO support represents the easiest way to convert your Java Objects to JSON and back.

Media modules that support this approach are [MOXy](#moxy) and [Jackson](#jackson-1x-and-2x)

#### JAXB based JSON support
     
Taking this approach will save you a lot of time, if you want to easily produce/consume both JSON and XML data format.
With JAXB beans you will be able to use the same Java model to generate JSON as well as XML representations. Another
advantage is simplicity of working with such a model and availability of the API in Java SE Platform. JAXB leverages
annotated POJOs and these could be handled as simple Java beans.

A disadvantage of JAXB based approach could be if you need to work with a very specific JSON format. Then it might be
difficult to find a proper way to get such a format produced and consumed. This is a reason why a lot of configuration
options are provided, so that you can control how JAXB beans get serialized and de-serialized. The extra configuration
options however requires you to learn more details about the framework you are using.

Following is a very simple example of how a JAXB bean could look like. 

```java
@XmlRootElement
public class MyJaxbBean {

    public String name;
    public int age;
 
    public MyJaxbBean() {
        // JAXB needs this
    }
 
    public MyJaxbBean(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Using the above JAXB bean for producing JSON data format from you resource method, is then as simple as:

```java
@GET
@Produces("application/json")
public MyJaxbBean getMyBean() {
    return new MyJaxbBean("Agamemnon", 32);
}
```

Notice, that JSON specific mime type is specified in `@Produces` annotation, and the method returns an instance of
`MyJaxbBean`, which JAXB is able to process. Resulting JSON in this case would look like:

```json
{
    "name":"Agamemnon",
    "age":"32"
}
```

A proper use of JAXB annotations itself enables you to control output JSON format to certain extent. Specifically,
renaming and omitting items is easy to do directly just by using JAXB annotations. For example, the following example
depicts changes in the above mentioned `MyJaxbBean` that will result in `{"king":"Agamemnon"}` JSON output.

```java
@XmlRootElement
public class MyJaxbBean {
 
    @XmlElement(name="king")
    public String name;
 
    @XmlTransient
    public int age;
 
    // several lines removed
}
``` 

Media modules that support this approach are [MOXy](#moxy), [Jackson](#jackson-1x-and-2x), [Jettison](#jettison)

#### Low-level based JSON support

JSON Processing API is a new standard API for parsing and processing JSON structures in similar way to what SAX and StAX
parsers provide for XML. The API is part of Java EE 7 and later. Another such JSON parsing/processing API is provided by
Jettison framework. Both APIs provide a low-level access to producing and consuming JSON data structures. By adopting
this low-level approach you would be working with `JsonObject` (or `JSONObject` respectively) and/or `JsonArray` (or
`JSONArray` respectively) classes when processing your JSON data representations.

The biggest advantage of these low-level APIs is that you will gain full control over the JSON format produced and
consumed. You will also be able to produce and consume very large JSON structures using streaming JSON parser/generator
APIs. On the other hand, dealing with your data model objects will probably be a lot more complex, compared to the POJO
or JAXB based binding approach. Differences are depicted at the following code snippets.

Let's start with JAXB-based approach.

```java
MyJaxbBean myBean = new MyJaxbBean("Agamemnon", 32);
```

Above you construct a simple JAXB bean, which could be written in JSON as `{"name":"Agamemnon", "age":32}`

Now to build an equivalent `JsonObject`/`JSONObject` (in terms of resulting JSON expression), you would need several
more lines of code. The following example illustrates how to construct the same JSON data using the standard Java EE 7
JSON-Processing API.

```java
JsonObject myObject = Json
        .createObjectBuilder()
        .add("name", "Agamemnon")
        .add("age", 32)
        .build();
```

And at last, here's how the same work can be done with Jettison API. 

```java
JSONObject myObject = new JSONObject();

try {
    myObject.put("name", "Agamemnon");
    myObject.put("age", 32);
} catch (JSONException exception) {
    LOGGER.log(Level.SEVERE, "Error ...", exception);
}
```

Media modules that support the low-level JSON parsing and generating approach are
[Java API for JSON Processing (JSON-P)](#java-api-for-json-processing-json-p) and [Jettison](#jettison). Unless you have
a strong reason for using the non-standard Jettison API, we recommend you to use the new standard Java API for JSON
Processing (JSON-P) API instead.

### MOXy

#### Dependency

To use MOXy as your JSON provider you need to add `jersey-media-moxy` module to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-moxy</artifactId>
    <version>2.31</version>
</dependency>
```

If you're not using Maven make sure to have all needed dependencies (see
[jersey-media-moxy](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-media-moxy/dependencies.html))
on the classpath.

#### Configure and register

As stated in the Section [Auto-Discoverable Features](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html#auto-discoverable-features)
as well as earlier in this chapter, MOXy media module is one of the modules where you don't need to explicitly register
its `Feature`s (`MoxyJsonFeature`) in your client/server Configurable as this feature is automatically discovered and
registered when you add `jersey-media-moxy` module to your class-path.

The auto-discoverable `jersey-media-moxy` module defines a few properties that can be used to control the automatic
registration of `MoxyJsonFeature` (besides the generic
[CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#FEATURE_AUTO_DISCOVERY_DISABLE)
an the its client/server variants):

* [CommonProperties.MOXY_JSON_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#MOXY_JSON_FEATURE_DISABLE)

* [ServerProperties.MOXY_JSON_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#MOXY_JSON_FEATURE_DISABLE)

* [ClientProperties.MOXY_JSON_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ClientProperties.html#MOXY_JSON_FEATURE_DISABLE)

> 📋 A manual registration of any other Jersey JSON provider feature (except for Java API for
> [JSON Processing (JSON-P)](#java-api-for-json-processing-json-p)) disables the automated enabling and configuration of
> `MoxyJsonFeature`.

To configure `MessageBodyReader<T>`s or `MessageBodyWriter<T>`s provided by MOXy you can simply create an instance of
[MoxyJsonConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/moxy/json/MoxyJsonConfig.html)
and set values of needed properties. For most common properties you can use a particular method to set the value of the
property or you can use more generic methods to set the property: 

* [MoxyJsonConfig#property(java.lang.String, java.lang.Object)](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/moxy/json/MoxyJsonConfig.html#property-java.lang.String-java.lang.Object-) - sets a property value for both Marshaller and Unmarshaller.

* [MoxyJsonConfig#marshallerProperty(java.lang.String, java.lang.Object)](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/moxy/json/MoxyJsonConfig.html#marshallerProperty-java.lang.String-java.lang.Object-) - sets a property value for Marshaller.

* [MoxyJsonConfig#unmarshallerProperty(java.lang.String, java.lang.Object)](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/moxy/json/MoxyJsonConfig.html#unmarshallerProperty-java.lang.String-java.lang.Object-) - sets a property value for Unmarshaller. 

```java
final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
 
final MoxyJsonConfig configuration = new MoxyJsonConfig()
        .setNamespacePrefixMapper(namespacePrefixMapper)
        .setNamespaceSeparator(':');
```

In order to make `MoxyJsonConfig` visible for MOXy you need to create and register `ContextResolver<T>` in your
client/server code.

```java
final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
 
final MoxyJsonConfig moxyJsonConfig = MoxyJsonConfig()
            .setNamespacePrefixMapper(namespacePrefixMapper)
            .setNamespaceSeparator(':');
 
final ContextResolver<MoxyJsonConfig> jsonConfigResolver = moxyJsonConfig.resolver();
```

Another way to pass configuration properties to the underlying `MOXyJsonProvider` is to set them directly into your
`Configurable` instance (see an example below). These are overwritten by properties set into the
[MoxyJsonConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/moxy/json/MoxyJsonConfig.html).

```java
new ResourceConfig().property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".")
```

There are some properties for which Jersey sets the default value when `MessageBodyReader<T>`/`MessageBodyWriter<T>`
from MOXy is used and they are:

| **Property**                                                                       | **Default Value**                              |
|------------------------------------------------------------------------------------|------------------------------------------------|
| `javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT`                                  | `false`                                        |
| `org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_INCLUDE_ROOT`             | `false`                                        |
| `org.eclipse.persistence.jaxb.MarshallerProperties#JSON_MARSHAL_EMPTY_COLLECTIONS` | `true`                                         |
| `org.eclipse.persistence.jaxb.JAXBContextProperties#JSON_NAMESPACE_SEPARATOR`      | `org.eclipse.persistence.oxm.XMLConstants#DOT` |

```java
// Building client with MOXy JSON feature enabled.
final Client client = ClientBuilder
        .newBuilder()
        .register(MoxyJsonFeature.class)
        .register(jsonConfigResolver)
        .build();
```

```java
// Create JAX-RS application.
final Application application = new ResourceConfig()
        .packages("org.glassfish.jersey.examples.jsonmoxy")
        .register(MoxyJsonFeature.class)
        .register(jsonConfigResolver);
```

#### Examples

Jersey provides a [JSON MOXy example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-moxy) on how to
use MOXy to consume/produce JSON.

### Java API for JSON Processing (JSON-P)

#### Dependency

To use JSON-P as your JSON provider you need to add `jersey-media-json-processing` module to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-processing</artifactId>
    <version>2.31</version>
</dependency>
```

If you're not using Maven make sure to have all needed dependencies (see
[jersey-media-json-processing](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-media-json-processing/dependencies.html))
on the class-path. 

#### Configure and register

As stated in Section
[Auto-Discoverable Features](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html#auto-discoverable-features)
JSON-Processing media module is one of the modules where you don't need to explicitly register its `Feature`s (
`JsonProcessingFeature`) in your client/server `Configurable` as this feature is automatically discovered and registered
when you add `jersey-media-json-processing` module to your classpath.

As for the other modules, `jersey-media-json-processing` has also few properties that can affect the registration of
`JsonProcessingFeature` (besides
[CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#FEATURE_AUTO_DISCOVERY_DISABLE)
and the like):

* [CommonProperties.JSON_PROCESSING_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#JSON_PROCESSING_FEATURE_DISABLE)
* [ServerProperties.JSON_PROCESSING_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#JSON_PROCESSING_FEATURE_DISABLE)
* [ClientProperties.JSON_PROCESSING_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ClientProperties.html#JSON_PROCESSING_FEATURE_DISABLE)

To configure `MessageBodyReader<T>`s/`MessageBodyWriter<T>`s provided by JSON-P you can simply add values for supported
properties into the `Configuration` instance (client/server). Currently supported are these properties:

* `JsonGenerator.PRETTY_PRINTING ("javax.json.stream.JsonGenerator.prettyPrinting")`

```java
// Building client with JSON-Processing JSON feature enabled.
ClientBuilder.newClient(
        new ClientConfig()
                .register(JsonProcessingFeature.class)
                .property(JsonGenerator.PRETTY_PRINTING, true)
);
```

```java
// Create JAX-RS application.
final Application application = new ResourceConfig()
        .register(JsonProcessingFeature.class)
        .packages("org.glassfish.jersey.examples.jsonp")
        .property(JsonGenerator.PRETTY_PRINTING, true);
```

#### Examples

Jersey provides a
[JSON Processing example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-processing-webapp) on how to
use JSON-Processing to consume/produce JSON.

### Jackson (1.x and 2.x)

#### Dependency

To use Jackson 2.x as your JSON provider you need to add `jersey-media-json-jackson` module to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson</artifactId>
    <version>2.31</version>
</dependency>
```

To use Jackson 1.x it'll look like:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jackson1</artifactId>
    <version>2.31</version>
</dependency>
```

If you're not using Maven make sure to have all needed dependencies (see
[jersey-media-json-jackson](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-media-json-jackson/dependencies.html)
or
[jersey-media-json-jackson1](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-media-json-jackson1/dependencies.html)
) on the classpath. 

#### Configure and register

> 📋 Note that there is a difference in namespaces between Jackson 1.x (`org.codehaus.jackson`) and Jackson 2.x
> (`com.fasterxml.jackson`). 

Jackson JSON processor could be controlled via providing a custom Jackson `ObjectMapper` instance. This could be handy
if you need to redefine the default Jackson behaviour and to fine-tune how your JSON data structures look like. Detailed
description of all Jackson features is out of scope of this guide. The example below gives you a hint on how to wire
your `ObjectMapper` instance into your Jersey application.

In order to use Jackson as your JSON (JAXB/POJO) provider you need to register
[JacksonFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jackson/JacksonFeature.html)
([Jackson1Feature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jackson1/Jackson1Feature.html))
and a `ContextResolver<T>` for `ObjectMapper`, if needed, in your `Configurable` (client/server).

```java
@Provider
public class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {
 
    final ObjectMapper defaultObjectMapper;
 
    public MyObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }
 
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defaultObjectMapper;
    }
 
    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.configure(Feature.INDENT_OUTPUT, true);
 
        return result;
    }
 
    // ...
}
```

To view the complete example source code, see
[MyObjectMapperProvider](https://github.com/eclipse-ee4j/jersey/blob/master/examples/json-jackson/src/main/java/org/glassfish/jersey/examples/jackson/MyObjectMapperProvider.java)
class from the [JSON-Jackson](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-jackson) example.

```java
// Building client with Jackson JSON feature enabled.
final Client client = ClientBuilder
        .newBuilder()
        .register(MyObjectMapperProvider.class)  // No need to register this provider if no special configuration is required.
        .register(JacksonFeature.class)
        .build();
```

```java
// Creating JAX-RS application with Jackson JSON feature enabled.
final Application application = new ResourceConfig()
        .packages("org.glassfish.jersey.examples.jackson")
        .register(MyObjectMapperProvider.class)  // No need to register this provider if no special configuration is required.
        .register(JacksonFeature.class);
```

#### Examples

Jersey provides [JSON Jackson (2.x) example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-jackson)
and [JSON Jackson (1.x)](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-jackson1) example showing how
to use Jackson to consume/produce JSON. 

### Jettison

JAXB approach for (de)serializing JSON in Jettison module provides, in addition to using pure JAXB, configuration
options that could be set on an
[JettisonConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jettison/JettisonConfig.html)
instance. The instance could be then further used to create a
[JettisonJaxbContext](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jettison/JettisonJaxbContext.html),
which serves as a main configuration point in this area. To pass your specialized `JettisonJaxbContext` to Jersey, you
will finally need to implement a JAXBContext `ContextResolver<T>` (see below).

#### Dependency

To use Jettison as your JSON provider you need to add `jersey-media-json-jettison` module to your `pom.xml` file:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-json-jettison</artifactId>
    <version>2.31</version>
</dependency>
```

If you're not using Maven make sure to have all needed dependencies (see
[jersey-media-json-jettison](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-media-json-jettison/dependencies.html))
on the classpath.

#### JSON Notations

`JettisonConfig` allows you to use two JSON notations. Each of these notations serializes JSON in a different way.
Following is a list of supported notations:

* JETTISON_MAPPED (default notation)
* [BADGERFISH](http://wiki.open311.org/JSON_and_XML_Conversion/#the-badgerfish-convention)

You might want to use one of these notations, when working with more complex XML documents. Namely when you deal with
multiple XML namespaces in your JAXB beans.

Individual notations and their further configuration options are described below. Rather then explaining rules for
mapping XML constructs into JSON, the notations will be described using a simple example. Following are JAXB beans,
which will be used.

```java
/**
 * JAXB beans for JSON supported notations description, simple address bean.
 */
@XmlRootElement
public class Address {

    public String street;
    public String town;
 
    public Address() {
    
    }
 
    public Address(String street, String town) {
        this.street = street;
        this.town = town;
    }
}
```

```java
/**
 * JAXB beans for JSON supported notations description, contact bean.
 */
@XmlRootElement
public class Contact {
 
    public int id;
    public String name;
    public List<Address> addresses;
 
    public Contact() {
    
    }
 
    public Contact(int id, String name, List<Address> addresses) {
        this.name = name;
        this.id = id;
        this.addresses = (addresses != null) ? new LinkedList<Address>(addresses) : null;
    }
}
```

Following text will be mainly working with a contact bean initialized with: 

```java
Address[] addresses = {new Address("Long Street 1", "Short Village")};
Contact contact = new Contact(2, "Bob", Arrays.asList(addresses));
```

i.e. contact bean with `id=2`, `name="Bob"` containing a single address (`street="Long Street 1"`,
`town="Short Village"`). 

All below described configuration options are documented also in api-docs at
[JettisonConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jettison/JettisonConfig.html). 

##### Jettison mapped notation

If you need to deal with various XML namespaces, you will find Jettison `mapped` notation pretty useful. Lets define a
particular namespace for `id` item:

```java
...
@XmlElement(namespace="http://example.com")
public int id;
...
```

Then you simply configure a mapping from XML namespace into JSON prefix as follows:

```java
Map<String,String> ns2json = new HashMap<String, String>();
ns2json.put("http://example.com", "example");
context = new JettisonJaxbContext(
        JettisonConfig.mappedJettison().xml2JsonNs(ns2json).build(),
        types
);
```

Resulting JSON will look like in the example below.

```json
{
    "contact":{
        "example.id":2,
        "name":"Bob",
        "addresses":{
            "street":"Long Street 1",
            "town":"Short Village"
        }
    }
}
```

Please note, that `id` item became `example.id` based on the XML namespace mapping. If you have more XML namespaces in
your XML, you will need to configure appropriate mapping for all of them.

Another configurable option introduced in Jersey version 2.2 is related to serialization of JSON arrays with Jettison's
mapped notation. When serializing elements representing single item lists/arrays, you might want to utilise the
following Jersey configuration method to explicitly name which elements to treat as arrays no matter what the actual
content is. 

```java
context = new JettisonJaxbContext(
        JettisonConfig.mappedJettison().serializeAsArray("name").build(),
        types
);
```

Resulting JSON will look like in the example below, unimportant lines removed for sanity.

```json
{
    "contact":{
        ...
        "name":["Bob"],
        ...
    }
}
```

##### Badgerfish notation

From JSON and JavaScript perspective, this notation is definitely the worst readable one. You will probably not want to
use it, unless you need to make sure your JAXB beans could be flawlessly written and read back to and from JSON, without
bothering with any formatting configuration, namespaces, etc.

`JettisonConfig` instance using Badgerfish notation could be built with

```java
JettisonConfig.badgerFish().build()
```

and the JSON output will be as follows.

```json
{
    "contact":{
        "id":{
            "$":"2"
        },
        "name":{
            "$":"Bob"
        },
        "addresses":{
            "street":{
              "$":"Long Street 1"
            },
            "town":{
              "$":"Short Village"
            }
        }
    }
}
```

#### Configure and register

In order to use Jettison as your JSON (JAXB/POJO) provider you need to register
[JettisonFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/jettison/JettisonFeature.html)
and a `ContextResolver<T>` for `JAXBContext` (if needed) in your `Configurable` (client/server).

```java
@Provider
public class JaxbContextResolver implements ContextResolver<JAXBContext> {
 
    private final JAXBContext context;
    private final Set<Class<?>> types;
    private final Class<?>[] cTypes = {Flights.class, FlightType.class, AircraftType.class};
 
    public JaxbContextResolver() throws Exception {
        this.types = new HashSet<Class<?>>(Arrays.asList(cTypes));
        this.context = new JettisonJaxbContext(JettisonConfig.DEFAULT, cTypes);
    }
 
    @Override
    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }
}
```

```java
// Building client with Jettison JSON feature enabled
final Client client = ClientBuilder
        .newBuilder()
        .register(JaxbContextResolver.class)  // No need to register this provider if no special configuration is required.
        .register(JettisonFeature.class)
        .build();
```

```java
// Creating JAX-RS application with Jettison JSON feature enabled.
final Application application = new ResourceConfig()
        .packages("org.glassfish.jersey.examples.jettison")
        .register(JaxbContextResolver.class)  // No need to register this provider if no special configuration is required.
        .register(JettisonFeature.class);
```

#### Examples

Jersey provides an [JSON Jettison example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-jettison) on
how to use Jettison to consume/produce JSON.

### `@JSONP` - JSON with Padding Support

Jersey provides out-of-the-box support for [JSONP](http://en.wikipedia.org/wiki/JSONP) - JSON with padding. The
following conditions has to be met to take advantage of this capability:

* Resource method, which should return wrapped JSON, needs to be annotated with
  [@JSONP](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/JSONP.html)
  annotation.
* `MessageBodyWriter<T>` for `application/json` media type, which also accepts the return type of the resource method,
   needs to be registered (see [JSON](#json) section of this chapter).
* User's request has to contain `Accept` header with one of the JavaScript media types defined (see below).

Acceptable media types compatible with `@JSONP` are: `application/javascript`, `application/x-javascript`,
`application/ecmascript`, `text/javascript`, `text/x-javascript`, `text/ecmascript`, `text/jscript`.

```java
@GET
@JSONP
@Produces({"application/json", "application/javascript"})
public JaxbBean getSimpleJSONP() {
    return new JaxbBean("jsonp");
}
```

Assume that we have registered a JSON providers and that the `JaxbBean` looks like:

```java
@XmlRootElement
public class JaxbBean {
 
    private String value;
 
    public JaxbBean() {

    }
 
    public JaxbBean(final String value) {
        this.value = value;
    }
 
    public String getValue() {
        return value;
    }
 
    public void setValue(final String value) {
        this.value = value;
    }
}
```

When you send a `GET` request with `Accept` header set to `application/javascript` you'll get a result entity that look
like

```
callback({
    "value" : "jsonp",
})
```

There are, of course, ways to configure wrapping method of the returned entity which defaults to `callback` as you can
see in the previous example. `@JSONP` has two parameters that can be configured: `callback` and `queryParam`. `callback`
stands for the name of the JavaScript callback function defined by the application. The second parameter, `queryParam`,
defines the name of the query parameter holding the name of the callback function to be used (if present in the
request). Value of `queryParam` defaults to `__callback` so even if you do not set the name of the query parameter
yourself, client can always affect the result name of the wrapping JavaScript callback method. 

> 📋 `queryParam` value (if set) always takes precedence over `callback` value.

Lets modify our example a little bit:

```java
@GET
@Produces({"application/json", "application/javascript"})
@JSONP(callback = "eval", queryParam = "jsonpCallback")
public JaxbBean getSimpleJSONP() {
    return new JaxbBean("jsonp");
}
```

And make two requests:

```
curl -X GET http://localhost:8080/jsonp
```

will return

```
eval({
    "value" : "jsonp",
})
```

and the

```
curl -X GET http://localhost:8080/jsonp?jsonpCallback=alert
```

will return

```
alert({
    "value" : "jsonp",
})
```

You could also take a look at a provided
[JSON with Padding example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/json-with-padding).

## XML

As you probably already know, Jersey uses `MessageBodyWriter<T>`s and `MessageBodyReader<T>`s to parse incoming requests
and create outgoing responses. Every user can create its own representation but this is not recommended. XML is proven
standard for interchanging information, especially in web services. Jerseys supports low level data types used for
direct manipulation and JAXB XML entities.

### Low level XML support

Jersey currently support several low level data types

* [StreamSource](https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/stream/StreamSource.html)
* [SAXSource](https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/sax/SAXSource.html)
* [DOMSource](https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/dom/DOMSource.html)
* [Document](https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/Document.html)

You can use these types as the return type or as a method (resource) parameter. Lets say we want to test this feature
and we have [helloworld example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/helloworld) as a starting
point. All we need to do is add methods (resources) which consumes and produces XML and types mentioned above will be
used.

```java
@POST
@Path("StreamSource")
public StreamSource getStreamSource(StreamSource streamSource) {
    return streamSource;
}
 
@POST
@Path("SAXSource")
public SAXSource getSAXSource(SAXSource saxSource) {
    return saxSource;
}
 
@POST
@Path("DOMSource")
public DOMSource getDOMSource(DOMSource domSource) {
    return domSource;
}
 
@POST
@Path("Document")
public Document getDocument(Document document) {
    return document;
}
```

Both `MessageBodyWriter<T>` and `MessageBodyReader<T>` are used in this case, all we need is a POST request with some
XML document as a request entity. To keep this as simple as possible only root element with no content will be sent:
`"<test />"`. You can create JAX-RS client to do that or use some other tool, for example `curl`:

```
curl -v http://localhost:8080/base/helloworld/StreamSource -d "<test/>"
```

You should get exactly the same XML from our service as is present in the request; in this case, XML headers are added
to response but content stays. Feel free to iterate through all resources. 

### Getting started with JAXB

Good start for people which already have some experience with JAXB annotations is
[JAXB example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/jaxb). You can see various use-cases there.
This text is mainly meant for those who don't have prior experience with JAXB. Don't expect that all possible
annotations and their combinations will be covered in this section,
[JAXB (JSR 222 implementation)](https://github.com/eclipse-ee4j/jaxb-ri) is pretty complex and comprehensive. But if you
just want to know how you can interchange XML messages with your REST service, you are looking at the right place.

Lets start with simple example. Suppose we have class `Planet` and service which produces "Planets".

```java
@XmlRootElement
public class Planet {

    public int id;
    public String name;
    public double radius;
}
```

```java
@Path("planet")
public class Resource {
 
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Planet getPlanet() {
        final Planet planet = new Planet();
 
        planet.id = 1;
        planet.name = "Earth";
        planet.radius = 1.0;
 
        return planet;
    }
}
```

You can see there is some extra annotation declared on Planet class, particularly ***`@XmlRootElement`***. This is an
JAXB annotation which maps java classes to XML elements. We don't need to specify anything else, because `Planet` is a
very simple class and all fields are public. In this case, XML element name will be derived from the class name or you
can set the name property: `@XmlRootElement(name="yourName")`.

Our resource class will respond to GET `/planet` with

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<planet>
    <id>1</id>
    <name>Earth</name>
    <radius>1.0</radius>
</planet>
```

This could be obtained by 

```java
Planet planet = webTarget.path("planet").request(MediaType.APPLICATION_XML_TYPE).get(Planet.class);
```

There is pre-created `WebTarget` object which points to our applications context root and we simply add path (in our
case its planet), accept header (not mandatory, but service could provide different content based on this header; for
example text/html can be served for web browsers) and at the end we specify that we are expecting `Planet` class via
GET request.

### POJOs

Sometimes you don't want to add JAXB annotations to source code and you still want to have resources consuming and
producing XML representation of your classes. In this case, ***`JAXBElement`*** class should help you. Let's redo planet
resource but this time we won't have an `@XmlRootElement` annotation on `Planet` class.

```java
@Path("planet")
public class Resource {
 
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public JAXBElement<Planet> getPlanet() {
        Planet planet = new Planet();
 
        planet.id = 1;
        planet.name = "Earth";
        planet.radius = 1.0;
 
        return new JAXBElement<Planet>(new QName("planet"), Planet.class, planet);
    }
 
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public void setPlanet(JAXBElement<Planet> planet) {
        System.out.println("setPlanet " + planet.getValue());
    }
}
```

As you can see, everything is little more complicated with `JAXBElement`. This is because now you need to explicitly set
element name for `Planet` class XML representation. Client side is even more complicated than server side because you
can't do `JAXBElement<Planet>` so JAX-RS client API provides way how to workaround it by declaring subclass of
`GenericType<T>`.

```java
// GET
GenericType<JAXBElement<Planet>> planetType = new GenericType<JAXBElement<Planet>>() {};
 
Planet planet = (Planet) webTarget.path("planet").request(MediaType.APPLICATION_XML_TYPE).get(planetType).getValue();
System.out.println("### " + planet);
 
// POST
planet = new Planet();
 
// ...
 
webTarget.path("planet").post(new JAXBElement<Planet>(new QName("planet"), Planet.class, planet));
``` 

### Using custom JAXBContext

In some scenarios you can take advantage of using custom ***`JAXBContext`***. ***Creating `JAXBContext` is an expensive
operation and if you already have one created, same instance can be used by Jersey***. Other possible use-case for this
is when you need to set some specific things to `JAXBContext`, for example to set a different class loader.

```java
@Provider
public class PlanetJAXBContextProvider implements ContextResolver<JAXBContext> {

    private JAXBContext context = null;
 
    public JAXBContext getContext(Class<?> type) {
        if (type != Planet.class) {
            return null; // we don't support nothing else than Planet
        }
 
        if (context == null) {
            try {
                context = JAXBContext.newInstance(Planet.class);
            } catch (JAXBException e) {
                // log warning/error; null will be returned which indicates that this
                // provider won't/can't be used.
            }
        }
 
        return context;
    }
}
```

Sample above shows simple `JAXBContext` creation, all you need to do is put this `@Provider` annotated class somewhere
where Jersey can find it. Users sometimes have problems with using provider classes on client side, so just to reminder
- you have to declare them in the client config (client does not do anything like package scanning done by server).

```java
ClientConfig config = new ClientConfig();
config.register(PlanetJAXBContextProvider.class);
 
Client client = ClientBuilder.newClient(config);
```

### MOXy

If you want to use MOXy as your JAXB implementation instead of JAXB RI you have two options. You can either use the
standard JAXB mechanisms to define the `JAXBContextFactory` from which a `JAXBContext` instance would be obtained
(for more on this topic, read JavaDoc on `JAXBContext`) or you can add `jersey-media-moxy` module to your project (see
below) and register/configure `MoxyXmlFeature` class/instance in the `Configurable`.

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-moxy</artifactId>
    <version>2.31</version>
</dependency>
```

```java
// Register the MoxyXmlFeature class.
final ResourceConfig config = new ResourceConfig()
        .packages("org.glassfish.jersey.examples.xmlmoxy")
        .register(MoxyXmlFeature.class);
```

```java
// Configure and register an MoxyXmlFeature instance.

// Configure Properties.
final Map<String, Object> properties = new HashMap<String, Object>();
// ...
 
// Obtain a ClassLoader you want to use.
final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
 
final ResourceConfig config = new ResourceConfig()
        .packages("org.glassfish.jersey.examples.xmlmoxy")
        .register(
                new MoxyXmlFeature(
                        properties,
                        classLoader,
                        true, // Flag to determine whether eclipselink-oxm.xml file should be used for lookup.
                        CustomClassA.class,
                        CustomClassB.class // Classes to be bound.
                )
        );
```