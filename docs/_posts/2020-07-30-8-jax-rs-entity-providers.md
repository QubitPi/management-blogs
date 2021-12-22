---
layout: post
title: JAX-RS Entity Providers
tags: [Request, Response, Serialization, Deserialization]
color: LightCoral
feature-img: "assets/img/post-cover/8-cover.png"
thumbnail: "assets/img/post-cover/8-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Entity payload, if present in an received HTTP message, is passed to Jersey from an I/O container as an input stream.
The stream may, for example, contain data represented as a plain text, XML or JSON document. However, in many JAX-RS
components that process these inbound data, such as resource methods or client responses, the JAX-RS API user can access
the inbound entity as an arbitrary Java object that is created from the content of the input stream based on the
representation type information. For example, an entity created from an input stream that contains data represented as a
XML document, can be converted to a custom JAXB bean. Similar concept is supported for the outbound entities. An entity
returned from the resource method in the form of an arbitrary Java object can be serialized by Jersey into a container
output stream as a specified representation. Of course, while JAX-RS implementations do provide default support for most
common combinations of Java type and it's respective on-the-wire representation formats, JAX-RS implementations do not
support the conversion described above for any arbitrary Java type and any arbitrary representation format by default.
Instead, a generic extension concept is exposed in JAX-RS API to allow application-level customizations of this JAX-RS
runtime to support for entity conversions. The JAX-RS extension API components that provide the user-level extensibility
are typically referred to by several terms with the same meaning, such as entity providers, message body providers,
message body workers or message body readers and writers. You may find all these terms used interchangeably throughout
the user guide and they all refer to the same concept.

In JAX-RS extension API (or SPI - service provider interface, if you like) the concept is captured in 2 interfaces. One
for handling inbound entity representation-to-Java de-serialization - `MessageBodyReader<T>` and the other one for
handling the outbound entity Java-to-representation serialization - `MessageBodyWriter<T>`. A `MessageBodyReader<T>`, as
the name suggests, is an extension that supports reading the message body representation from an input stream and
converting the data into an instance of a specific Java type. A `MessageBodyWriter<T>` is then responsible for
converting a message payload from an instance of a specific Java type into a specific representation format that is sent
over the wire to the other party as part of an HTTP message exchange. Both of these providers can be used to provide
message payload serialization and de-serialization support on the server as well as the client side. A message body
reader or writer is always used whenever a HTTP request or response contains an entity and the entity is either
requested by the application code (e.g. injected as a parameter of JAX-RS resource method or a response entity read on
the client from a Response) or has to be serialized and sent to the other party (e.g. an instance returned from a JAX-RS
resource method or a request entity sent by a JAX-RS client). 

## How to Write Custom Entity Providers

A best way how to learn about entity providers is to walk through an example of writing one. Therefore we will describe
here the process of implementing a custom `MessageBodyWriter<T>` and `MessageBodyReader<T>` using a practical example.
Let's first setup the stage by defining a JAX-RS resource class for the server side story of our application.

```java
@Path("resource")
public class MyResource {

    @GET
    @Produces("application/xml")
    public MyBean getMyBean() {
        return new MyBean("Hello World!", 42);
    }
 
    @POST
    @Consumes("application/xml")
    public String postMyBean(MyBean myBean) {
        return myBean.anyString;
    }
}
```

The resource class defines `GET` and `POST` resource methods. Both methods work with an entity that is an instance of
`MyBean`. The `MyBean` class is defined below:

```java
@XmlRootElement
public class MyBean {

    @XmlElement
    public String anyString;

    @XmlElement
    public int anyNumber;
 
    public MyBean(String anyString, int anyNumber) {
        this.anyString = anyString;
        this.anyNumber = anyNumber;
    }

    public MyBean() {
        // empty constructor needed for deserialization by JAXB
    }
 
    @Override
    public String toString() {
        return "MyBean{" +
            "anyString='" + anyString + '\'' +
            ", anyNumber=" + anyNumber +
            '}';
    }
}
```

### `MessageBodyWriter`

The `MyBean` is a JAXB-annotated POJO. In `GET` resource method we return the instance of `MyBean` and we would like
Jersey runtime to serialize it into XML and write it as an entity body to the response output stream. We design a custom
`MessageBodyWriter<T>` that can serialize this POJO into XML. See the following code sample:

> ![http://xyq.163.com/images/emote/105.gif]({{ "/assets/img/105.gif" | relative_url}}) Please note, that this is only a demonstration of how to write a custom entity provider. Jersey already contains
> default support for entity providers that can serialize JAXB beans into XML. 

```java
@Produces("application/xml")
public class MyBeanMessageBodyWriter implements MessageBodyWriter<MyBean> {
 
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MyBean.class;
    }
 
    @Override
    public long getSize(MyBean myBean, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return -1;
    }
 
    @Override
    public void writeTo(
            MyBean myBean,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream
    ) throws IOException, WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MyBean.class);
            jaxbContext.createMarshaller().marshal(myBean, entityStream); // serialize the entity myBean to the entity output stream
        } catch (JAXBException exception) {
            throw new ProcessingException(
                    "Error serializing a MyBean to the output stream",
                    exception
            );
        }
    }
}
```

The `MyBeanMessageBodyWriter` implements the `MessageBodyWriter<T>` interface that contains three methods. In the next
sections we'll explore these methods more closely.

#### `MessageBodyWriter.isWriteable`

A method `isWriteable` should return `true` if the `MessageBodyWriter<T>` is able to write the given type. Method does
not decide only based on the Java type of the entity but also on annotations attached to the entity and the requested
representation media type.

Parameters `type` and `genericType` both define the entity, where `type` is a raw Java type (for example, a
`java.util.List` class) and `genericType` is a
[ParameterizedType](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/ParameterizedType.html)
including generic information (for example `List<String>`). 

Parameter `annotations` contains annotations that are either attached to the resource method and/or annotations that are
attached to the entity by building response like in the following piece of code: 

```java
@Path("resource")
public static class AnnotatedResource {
 
    @GET
    public Response get() {
        Annotation annotation = AnnotatedResource.class.getAnnotation(Path.class);
        return Response.ok()
                .entity("Entity", new Annotation[] {annotation})
                .build();
    }
}
```

In the example above, the `MessageBodyWriter<T>` would get annotations parameter containing a JAX-RS `@GET` annotation
as it annotates the resource method and also a `@Path` annotation as it is passed in the response (but not because it
annotates the resource; only resource method annotations are included). In the case of `MyResource` and method
`getMyBean` the annotations would contain the `@GET` and the `@Produces` annotation. 

The last parameter of the `isWriteable` method is the `mediaType` which is the media type attached to the response
entity by annotating the resource method with a `@Produces` annotation or the request media type specified in the JAX-RS
Client API. In our example, the media type passed to providers for the resource `MyResource` and method `getMyBean`
would be `"application/xml"`.

In our implementation of the `isWriteable` method, we just check that the type is `MyBean`. Please note, that ***this
method might be executed multiple times by Jersey runtime as Jersey needs to check whether this provider can be used for
a particular combination of entity Java type, media type, and attached annotations, which may be potentially a
performance hog***. You can limit the number of execution by properly defining the `@Produces` annotation on the
`MessageBodyWriter<T>`. In our case thanks to `@Produces` annotation, the provider will be considered as writeable (and
the method `isWriteable` might be executed) only if the media type of the outbound message is `"application/xml"`.
Additionally, the provider will only be considered as possible candidate and its `isWriteable` method will be executed,
if the generic type of the provider is either a sub class or super class of type parameter.

####  `MessageBodyWriter.writeTo`

Once a message body writer is selected as the most appropriate (see the Section
[Entity Provider Selection](#entity-provider-selection) for more details), its `writeTo` method is invoked. This method
receives parameters with the same meaning as in `isWriteable` as well as a few additional ones.

In addition to the parameters already introduced, the `writeTo` method also defines `httpHeaders` parameter, that
contains HTTP headers associated with the outbound message.

> ![http://xyq.163.com/images/emote/105.gif]({{ "/assets/img/105.gif" | relative_url}})️ When a `MessageBodyWriter<T>` is invoked, the headers still can be modified in this point and any modification will
> be reflected in the outbound HTTP message being sent. The modification of headers must however happen before a first
> byte is written to the supplied output stream. 

Another new parameter, `myBean`, contains the entity instance to be serialized (the type of entity corresponds to
generic type of `MessageBodyWriter<T>`). Related parameter `entityStream` contains the entity output stream to which the
method should serialize the entity. In our case we use JAXB to marshall the entity into the `entityStream`. Note, that
***the `entityStream` is not closed at the end of method; the stream will be closed by Jersey***. 

> ⚠️ Do not close the entity output stream in the `writeTo` method of your `MessageBodyWriter<T>` implementation. 

#### `MessageBodyWriter.getSize`

The method is deprecated since JAX-RS 2.0 and Jersey 2 ignores the return value. In JAX-RS 1.0 the method could return
the size of the entity that would be then used for `"Content-Length"` response header. In Jersey 2.0 the
`"Content-Length"` parameter is computed automatically using an internal outbound entity buffering. For details about
configuration options of outbound entity buffering see the javadoc of
[MessageProperties](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/MessageProperties.html).

> ![http://xyq.163.com/images/emote/105.gif]({{ "/assets/img/105.gif" | relative_url}})️ You can disable the Jersey outbound entity buffering by setting the buffer size to 0.

#### Testing a `MessageBodyWriter<T>`

Before testing the `MyBeanMessageBodyWrite`r, the writer must be registered as a custom JAX-RS extension provider. It
should be

* added to your application [ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html), or
* returned from your custom `Application` sub-class, or
* annotated with `@Provider` annotation to leverage JAX-RS provider auto-discovery feature. 

After registering the `MyBeanMessageBodyWriter` and `MyResource` class in our application, the request can be initiated
(in this example from Client API).

```java
WebTarget webTarget = // initialize web target to the context root of example application
Response response = webTarget.path("resource").request(MediaType.APPLICATION_XML).get();
System.out.println(response.getStatus());
String myBeanXml = response.readEntity(String.class);
System.out.println(myBeanXml);
```

The client code initiates the `GET` which will be matched to the resource method `MyResource.getMyBean()`. The response
entity is de-serialized as a `String`. The result of console output is:

```xml
200
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><myBean>
<anyString>Hello World!</anyString><anyNumber>42</anyNumber></myBean>
```

The returned status is 200 and the entity is stored in the response in a XML format. Next, we will look at how the
Jersey de-serializes this XML document into a `MyBean` consumed by our POST resource method. 

### `MessageBodyReader`

In order to de-serialize the entity of `MyBean` on the server or the client, we need to implement a custom
`MessageBodyReader<T>`. Our `MessageBodyReader<T>` implementation is listed below

```java
public static class MyBeanMessageBodyReader implements MessageBodyReader<MyBean> {
 
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MyBean.class;
    }
     
    @Override
    public MyBean readFrom(
            Class<MyBean> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream
    ) throws IOException, WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MyBean.class);
            return (MyBean) jaxbContext.createUnmarshaller().unmarshal(entityStream);
        } catch (JAXBException exception) {
            throw new ProcessingException("Error deserializing a MyBean.", exception);
        }
    }
}
```

It is obvious that the `MessageBodyReader<T>` interface is similar to `MessageBodyWriter<T>`. In the next couple of
sections we will explore it's API methods.

#### `MessageBodyReader.isReadable`

It defines the method `isReadable()` which has a very similar meaning as method `isWriteable()` in
`MessageBodyWriter<T>`. The method returns `true` if it is able to de-serialize the given type. The annotations parameter
contains annotations that are attached to the entity parameter in the resource method. In our POST resource method
`postMyBean` the entity parameter `myBean` is not annotated, therefore no annotation will be passed to the
`isReadable`. The mediaType parameter contains the entity media type. The media type, in our case, must be consumable by
the POST resource method, which is specified by placing a JAX-RS `@Consumes` annotation to the method. The resource
method `postMyBean()` is annotated with `@Consumes("application/xml")`, therefore for purpose of de-serialization of
entity for the `postMyBean()` method, only requests with entities represented as `"application/xml"` media type will
match the method. However, this method might be executed for entity types that are sub classes or super classes of the
declared generic type on the `MessageBodyReader<T>` will be also considered. It is a responsibility of the `isReadable`
method to decide whether it is able to de-serialize the entity and type comparison is one of the basic decision steps.

> ✏️ In order to reduce number of `isReadable` executions, always define correctly the consumable media type(s) with the
> `@Consumes` annotation on your custom `MessageBodyReader<T>`.

#### `MessageBodyReader.readFrom`

The `readForm()` method gets the parameters with the same meaning as in `isReadable()`. The additional `entityStream`
parameter provides a handle to the entity input stream from which the entity bytes should be read and de-serialized into
a Java entity which is then returned from the method. Our `MyBeanMessageBodyReader` de-serializes the incoming XML data
into an instance of `MyBean` using JAXB. 

> ⚠️ ***Do not close the entity input stream in your `MessageBodyReader<T>` implementation. The stream will be
> automatically closed by Jersey runtime***. 

#### Testing a `MessageBodyWriter<T>`

Now let's send a test request using the JAX-RS Client API. 

```java
final MyBean myBean = new MyBean("posted MyBean", 11);

Response response = webTarget
        .path("resource")
        .request("application/xml")
        .post(Entity.entity(myBean, "application/xml"));
 
System.out.println(response.getStatus());

final String responseEntity = response.readEntity(String.class);

System.out.println(responseEntity);
```

The console output is:

```
200
posted MyBean
```

#### Using Entity Providers with JAX-RS Client API

Both, `MessageBodyReader<T>` and `MessageBodyWriter<T>` can be registered in a configuration of JAX-RS Client API
components typically without any need to change their code:

```java
Client client = ClientBuilder
        .newBuilder()
        .register(MyBeanMessageBodyReader.class)
        .build();
 
Response response = client
        .target("http://example/comm/resource")
        .request(MediaType.APPLICATION_XML)
        .get();

System.out.println(response.getStatus());

MyBean myBean = response.readEntity(MyBean.class);

System.out.println(myBean);
```

The code above registers `MyBeanMessageBodyReader` to the `Client` configuration using a `ClientBuilder` which means
that the provider will be used for any `WebTarget` produced by the client instance.

> ![http://xyq.163.com/images/emote/105.gif]({{ "/assets/img/105.gif" | relative_url}})️ You could also register the JAX-RS entity (and any other) providers to individual `WebTarget` instances produced by
> the client. 

Then, using the fluent chain of method invocations, a resource target pointing to our `MyResource` is defined, a HTTP
GET request is invoked. The response entity is then read as an instance of a `MyBean` type by invoking the
`response.readEntity` method, that internally locates the registered `MyBeanMessageBodyReader` and uses it for entity
de-serialization.

The console output for the example is

```
200
MyBean{anyString='Hello World!', anyNumber=42}
```

## Entity Provider Selection

Usually there are many entity providers registered on the server or client side (be default there must be at least
providers mandated by the JAX-RS specification, such as providers for primitive types, byte array, JAXB beans, etc.).
JAX-RS defines an algorithm for selecting the most suitable provider for entity processing. This algorithm works with
information such as entity Java type and on-the-wire media type representation of entity, and searches for the most
suitable entity provider from the list of available providers based on the supported media type declared on each
provider (defined by `@Produces` or `@Consumes` on the provider class) as well as based on the generic type declaration
of the available providers. ***When a list of suitable candidate entity providers is selected and sorted based on the
rules defined in JAX-RS specification, a JAX-RS runtime then it invokes `isReadable` or `isWriteable` method
respectively on each provider in the list until a first provider is found that returns `true`***. This provider is then
used to process the entity.

The following steps describe the algorithm for selecting a `MessageBodyWriter<T>` (extracted from JAX-RS with little
modifications). The steps refer to the previously discussed example application. The `MessageBodyWriter<T>` is searched
for purpose of deserialization of `MyBean` entity returned from the method `getMyBean`. So, type is `MyBean` and media
type `"application/xml"`. Let's assume the runtime contains also registered providers, namely:

* `A`: `@Produces`(`"application/*"`) with generic type `<Object>`
* `B`: `@Produces`(`"*/*"`) with generic type `<MyBean>`
* `C`: `@Produces`(`"text/plain"`) with generic type `<MyBean>`
* `D`: `@Produces`(`"application/xml"`) with generic type `<Object>`
* `MyBeanMessageBodyWriter`: `@Produces`(`"application/xml"`) with generic type `<MyBean>`

The algorithm executed by a JAX-RS runtime to select a proper `MessageBodyWriter<T>` implementation is illustrated below

1. **Obtain the object that will be mapped to the message entity body. For a return type of Response or subclasses, the
   object is the value of the entity property, for other return types it is the returned object**. So in our case, for
   the resource method `getMyBean` the type will be `MyBean`.
2. **Determine the media type of the response**. In our case, for resource method `getMyBean` annotated with
   `@Produces("application/xml")`, the media type will be `"application/xml"`.
3. **Select the set of `MessageBodyWriter` providers that support the object and media type of the message entity
   body**. In our case, for entity media type `"application/xml"` and type `MyBean`, the appropriate
   `MessageBodyWriter<T>` will be the `A`, `B`, `D`, and `MyBeanMessageBodyWriter`. The provider `C` does not define the
   appropriate media type. `A` and `B` are fine as their type is more generic and compatible with `"application/xml"`.
4. **Sort the selected `MessageBodyWriter` providers with a primary key of generic type where providers whose generic
   type is the nearest superclass of the object class are sorted first and a secondary key of media type. Additionally,
   JAX-RS specification mandates that *custom, user registered providers have to be sorted ahead of default providers
   provided by JAX-RS implementation*. This is used as a tertiary comparison key. User providers are places prior to
   Jersey internal providers in to the final ordered list**. The sorted providers will be: `MyBeanMessageBodyWriter`,
   `B`, `D`, `A`.
5. **Iterate through the sorted `MessageBodyWriter<T>` providers and, utilizing the `isWriteable` method of each until
   you find a `MessageBodyWriter<T>` that returns `true`**. The first provider in the list - our
   `MyBeanMessageBodyWriter` returns `true` as it compares types and the types matches. If it would return false, the
   next provider `B` would by check by invoking its `isWriteable` method. 
6. **If step 5 locates a suitable `MessageBodyWriter<T>` then use its `writeTo` method to map the object to the entity
   body**. `MyBeanMessageBodyWriter.writeTo` will be executed and it will serialize the entity. **Otherwise, the server
   runtime MUST generate an `InternalServerErrorException`, a subclass of `WebApplicationException` with its status set
   to 500, and no entity and the client runtime MUST generate a `ProcessingException`. We have successfully found a
   provider, thus no exception is generated.
   
> ![http://xyq.163.com/images/emote/105.gif]({{ "/assets/img/105.gif" | relative_url}})️ JAX-RS 2.0 is incompatible with JAX-RS 1.x in one step of the entity provider selection algorithm. JAX-RS 1.x
> defines sorting keys priorities in the Step 4 in exactly opposite order. So, in JAX-RS 1.x the keys are defined in the
> order: primary media type, secondary type declaration distance where custom providers have always precedence to
> internal providers. If you want to force Jersey to use the algorithm compatible with JAX-RS 1.x, setup the property
> (to [ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html)
> or return from Application from its `getProperties` method):
> 
> `jersey.config.workers.legacyOrdering=true`
> 
> Documentation of this property can be found in the javadoc of
> [MessageProperties](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/message-body-workers.html#client-get-call).

The algorithm for selection of `MessageBodyReader<T>` is similar, including the incompatibility between JAX-RS 2.0 and
JAX-RS 1.x and the property to workaround it. The algorithm is defined as follows:

1. **Obtain the media type of the request. If the request does not contain a `Content-Type` header then use
   `application/octet-stream` media type**.
2. **Identify the Java type of the parameter whose value will be mapped from the entity body. The Java type on the
   server is the type of the entity parameter of the resource method. On the client it is the `Class` passed to
   `readFrom` method**. 
3. **Select the set of available `MessageBodyReader<T>` providers that support the media type of the request**. 
4. **Iterate through the selected `MessageBodyReader<T>` classes and, utilizing their `isReadable` method, choose the
   first `MessageBodyReader<T>` provider that supports the desired combination of Java type/media type/annotations
   parameters.
5. **If Step 4 locates a suitable `MessageBodyReader<T>`, then use its `readFrom` method to map the entity body to the
   desired Java type Otherwise, the server runtime MUST generate a `NotSupportedException` (HTTP 415 status code) and no
   entity and the client runtime MUST generate an instance of `ProcessingException`**.
   
## Jersey `MessageBodyWorkers` API

In case you need to directly work with JAX-RS entity providers, for example to serialize an entity in your resource
method, filter or in a composite entity provider, you would need to perform quite a lot of steps. You would need to
choose the appropriate `MessageBodyWriter<T>` based on the type, media type and other parameters. Then you would need to
instantiate it, check it by `isWriteable` method and basically perform all the steps that are normally performed by
Jersey that has been described in above.

To remove this burden from developers, Jersey exposes a proprietary public API that simplifies the manipulation of
entity providers. The API is defined by
[MessageBodyWorkers](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/MessageBodyWorkers.html)
interface and Jersey provides an implementation that can be injected using the `@Context` injection annotation. The
interface declares methods for selection of most appropriate `MessageBodyReader<T>` and `MessageBodyWriter<T>` based on
the rules defined in JAX-RS spec, methods for writing and reading entity that ensure proper and timely invocation of
interceptors and other useful methods.

See the following example of usage of `MessageBodyWorkers`.

```java
@Path("workers")
public static class WorkersResource {
 
    @Context
    private MessageBodyWorkers workers;
 
    @GET
    @Produces("application/xml")
    public String getMyBeanAsString() {
 
        final MyBean myBean = new MyBean("Hello World!", 42);
 
        // buffer into which myBean will be serialized
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 
        // get most appropriate MessageBodyWriter
        final MessageBodyWriter<MyBean> messageBodyWriter = workers.getMessageBodyWriter(
                MyBean.class,
                MyBean.class,
                new Annotation[]{},
                MediaType.APPLICATION_XML_TYPE
        );
 
        try {
            // use the MessageBodyWriter to serialize myBean into byteArrayOutputStream
            messageBodyWriter.writeTo(
                    myBean,
                    MyBean.class,
                    MyBean.class,
                    new Annotation[] {},
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedHashMap<String, Object>(),
                    byteArrayOutputStream
            );
        } catch (IOException exception) {
            throw new RuntimeException("Error while serializing MyBean.", exception);
        }
 
        // stringXmlOutput now contains XML representation:
        // "<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        // <myBean><anyString>Hello World!</anyString>
        // <anyNumber>42</anyNumber></myBean>"
        return byteArrayOutputStream.toString();
    }
}
```

In the example a resource injects `MessageBodyWorkers` and uses it for selection of the most appropriate
`MessageBodyWriter<T>`. Then the writer is utilized to serialize the entity into the buffer as XML document. The
`String` content of the buffer is then returned. This will cause that Jersey will not use `MyBeanMessageBodyWriter` to
serialize the entity as it is already in the `String` type (`MyBeanMessageBodyWriter` does not support String). Instead,
a simple `String`-based `MessageBodyWriter<T>` will be chosen and it will only serialize the `String` with XML to the
output entity stream by writing out the bytes of the `String`.

Of course, the code in the example does not bring any benefit as the entity could have been serialized by
`MyBeanMessageBodyWriter` by Jersey as in previous examples; the purpose of the example was to show how to use
`MessageBodyWorkers` in a resource method.

## Default Jersey Entity Providers

Jersey internally contains entity providers for these types with combination of media types (in brackets):

* `byte[]` (`*/*`)
* `String` (`*/*`)
* [InputStream](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html) (`*/*`)
* [Reader](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html) (`*/*`)
* [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) (`*/*`)
* [DataSource](https://docs.oracle.com/javase/8/docs/api/javax/activation/DataSource.html) (`*/*`)
* [Source](https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/Source.html) (`text/xml`, `application/xml` and media types of the form `application/*+xml`)
* [JAXBElement](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/JAXBElement.html) (`text/xml`, `application/xml` and media types of the form `application/*+xml`)
* `MultivaluedMap<K,V>` (`application/x-www-form-urlencoded`)
* `Form` (`application/x-www-form-urlencoded`)
* `StreamingOutput` (`*/*`) - this class can be used as an lightweight `MessageBodyWriter<T>` that can be returned from a
  resource method
* [Boolean](https://docs.oracle.com/javase/8/docs/api/java/lang/Boolean.html),
  [Character](https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html), and
  [Number](https://docs.oracle.com/javase/8/docs/api/java/lang/Number.html) (`text/plain`) - corresponding primitive
  types supported via boxing/unboxing conversion.
  
For other media type supported in jersey please see the post
[Support for Common Media Type Representations](https://qubitpi.github.io/jersey-guide/2020/07/31/09-support-for-common-media-type-representations.html)
which describes additional Jersey entity provider extensions for serialization to JSON, XML, serialization of
collections, [Multi Part](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/multipart/package-summary.html)
and others.
