---
layout: post
title: Filters and Interceptors
tags: [Filter, Interceptor, Request, Response, Name Binding, Dynamic Binding, Priorities]
category: FINALIZED
color: rgb(255, 105, 180)
feature-img: "assets/img/post-cover/10-cover.png"
thumbnail: "assets/img/post-cover/10-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

This post describes filters, interceptors and their configuration. Filters and interceptors can be used on both sides,
on the client and the server side. Filters can modify inbound and outbound requests and responses including modification
of headers, entity and other request/response parameters. Interceptors are used primarily for modification of entity
input and output streams. You can use interceptors for example to zip and unzip output and input entity streams.

## Filters

**Filters can be used when you want to modify any request or response parameters like headers**. For example you would
like to add a response header `"X-Powered-By"` to each generated response. Instead of adding this header in each
resource method you would use a response filter to add this header.

There are filters on the server side and the client side. Server filters include

* ContainerRequestFilter
* ContainerResponseFilter

Client filters include

* ClientRequestFilter
* ClientResponseFilter

### Server filters

The following example shows a simple container response filter adding a header to each response.

```java
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
 
public class PoweredByResponseFilter implements ContainerResponseFilter {
 
    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) throws IOException {
            responseContext.getHeaders().add("X-Powered-By", "Jersey :-)");
    }
}
```

In the example above the `PoweredByResponseFilter` always adds a header `"X-Powered-By"` to the response. The filter
must implement the `ContainerResponseFilter` and must be registered as a provider. The filter will be executed for every
response which is in most cases after the resource method is executed. Response filters are executed even if the
resource method is not run, for example when the resource method is not found and 404 "Not found" response code is
returned by the Jersey runtime. In this case the filter will be executed and will process the 404 response.

The `filter()` method has two arguments, the container request and container response. The `ContainerRequestContext` is
accessible only for read only purposes as the filter is executed already in response phase. The modifications can be
done in the `ContainerResponseContext`.

The following example shows the usage of a request filter. 

```java
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
 
public class AuthorizationRequestFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final SecurityContext securityContext = requestContext.getSecurityContext();
        if (securityContext == null || !securityContext.isUserInRole("privileged")) {
            requestContext.abortWith(
                    Response
                            .status(Response.Status.UNAUTHORIZED)
                            .entity("User cannot access the resource.")
                            .build()
            );
        }
    }
}
```

The request filter is similar to the response filter but does not have access to the `ContainerResponseContext` as no
response is accessible yet. Response filter implements `ContainerResponseFilter`. Request filter is executed before the
resource method is run and before the response is created. The filter has possibility to manipulate the request
parameters including request headers or entity.

The `AuthorizationRequestFilter` in the example above checks whether the authenticated user is in the privileged role.
If it is not then the request is aborted by calling `ContainerRequestContext.abortWith(Response response)` method.
***The method is intended to be called from the request filter in situation when the request should not be processed
further in the standard processing chain. When the filter method is finished the response passed as a parameter to the
`abortWith` method is used to respond to the request. Response filters, if any are registered, will be executed and
will have possibility to process the aborted response.

#### Pre-matching and post-matching filters

***All the request filters shown above was implemented as post-matching filters. It means that the filters would be
applied only after a suitable resource method has been selected to process the actual request i.e. after request
matching happens. Request matching is the process of finding a resource method that should be executed based on the
request path and other request parameters. Since post-matching request filters are invoked when a particular resource
method has already been selected, such filters can not influence the resource method matching process***.

To overcome such limitation, there is a possibility to mark a server request filter as a ***pre-matching filter***, i.e.
to annotate the filter class with the ***`@PreMatching`*** annotation. Pre-matching filters are request filters that are
executed before the request matching is started. Thanks to this, pre-matching request filters have the possibility to
influence which method will be matched. Such a pre-matching request filter example is shown here: 

```java
...
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
...
 
@PreMatching
public class PreMatchingFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // change all PUT methods to POST
        if (requestContext.getMethod().equals("PUT")) {
            requestContext.setMethod("POST");
        }
    }
}
```

The `PreMatchingFilter` is a simple pre-matching filter which changes all PUT HTTP methods to POST. This might be useful
when you want to always handle these PUT and POST HTTP methods with the same Java code. After the `PreMatchingFilter`
has been invoked, the rest of the request processing will behave as if the POST HTTP method was originally used. You
cannot do this in post-matching filters (standard filters without `@PreMatching` annotation) as the resource method is
already matched (selected). An attempt to tweak the original HTTP method in a post-matching filter would cause an
`IllegalArgumentException`.

As written above, pre-matching filters can fully influence the request matching process, which means you can even modify
request URI in a pre-matching filter by invoking the `setRequestUri(URI)` method of `ContainerRequestFilter` so that a
different resource would be matched.

Like in post-matching filters you can abort a response in pre-matching filters too.

### Client Filters

Client filters are similar to container filters. The response can also be aborted in the `ClientRequestFilter` which
would cause that no request will actually be sent to the server at all. A new response is passed to the abort method.
This response will be used and delivered as a result of the request invocation. Such a response goes through the client
response filters. This is similar to what happens on the server side. The process is shown in the following example:

```java
public class CheckRequestFilter implements ClientRequestFilter {
 
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getHeaders().get("Client-Name") == null) {
            requestContext.abortWith(
                    Response
                            .status(Response.Status.BAD_REQUEST)
                            .entity("Client-Name header must be defined.")
                            .build()
            );
        }
    }
}
```

The `CheckRequestFilter` above validates the outgoing request. It is checked for presence of a `Client-Name` header. If
the header is not present the request will be aborted with a made up response with an appropriate code and message in
the entity body. This will cause that the original request will not be effectively sent to the server but the actual
invocation will still end up with a response as if it would be generated by the server side. If there would be any
client response filter it would be executed on this response.

To summarize the workflow, for any client request invoked from the client API the client request filters
(`ClientRequestFilter`) are executed that could manipulate the request. If not aborted, the outcoming request is then
physically sent over to the server side and once a response is received back from the server the client response filters
(`ClientResponseFilter`) are executed that might again manipulate the returned response. Finally the response is passed
back to the code that invoked the request. If the request was aborted in any client request filter then the
client/server communication is skipped and the aborted response is used in the response filters.

## Interceptors

**Interceptors share a common API for the server and the client side**. Whereas filters are primarily intended to
manipulate request and response parameters like HTTP headers, URIs and/or HTTP methods, **interceptors are intended to
manipulate entities**, via manipulating entity input/output streams. If you for example need to encode entity body of a
client request then you could implement an interceptor to do the work for you.

There are two kinds of interceptors, `ReaderInterceptor` and `WriterInterceptor`. Reader interceptors are used to
manipulate inbound entity streams. These are the streams coming from the "wire". So, using a reader interceptor you can
manipulate request entity stream on the server side (where an entity is read from the client request) and response
entity stream on the client side (where an entity is read from the server response). Writer interceptors are used for
cases where entity is written to the "wire" which on the server means when writing out a response entity and on the
client side when writing request entity for a request to be sent out to the server. **Writer and reader interceptors are
executed before message body readers or writers are executed** and their primary intention is to wrap the entity streams
that will be used in message body reader and writers.

The following example shows a writer interceptor that enables GZIP compression of the whole entity body.

```java
public class GZIPWriterInterceptor implements WriterInterceptor {
 
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final OutputStream outputStream = context.getOutputStream();
        context.setOutputStream(new GZIPOutputStream(outputStream));
        context.proceed();
    }
}
```

The interceptor gets a output stream from the `WriterInterceptorContext` and sets a new one which is a GZIP wrapper of
the original output stream. After all interceptors are executed the output stream lastly set to the
`WriterInterceptorContext` will be used for serialization of the entity. In the example above the entity bytes will be
written to the `GZIPOutputStream` which will compress the stream data and write them to the original output stream. The
original stream is always the stream which writes the data to the "wire". When the interceptor is used on the server,
the original output stream is the stream into which writes data to the underlying server container stream that sends the
response to the client.

The interceptors wrap the streams and they itself work as wrappers. This means that each interceptor is a wrapper of
another interceptor and it is responsibility of each interceptor implementation to call the wrapped interceptor. This is
achieved by calling the `proceed()` method on the `WriterInterceptorContext`. This method will call the next registered
interceptor in the chain, so effectivelly this will call all remaining registered interceptors. Calling `proceed()` from
the last interceptor in the chain will call the appropriate message body reader. Therefore ***every interceptor must
call the `proceed()` method otherwise the entity would not be written***. The wrapping principle is reflected also in
the method name, `aroundWriteTo`, which says that the method is wrapping the writing of the entity.

The method `aroundWriteTo()` gets `WriterInterceptorContext` as a parameter. This context contains getters and setters
for header parameters, request properties, entity, entity stream and other properties. These are the properties which
will be passed to the final `MessageBodyWriter<T>`. ***Interceptors are allowed to modify all these properties. This
could influence writing of an entity by `MessageBodyWriter<T>` and even selection of such a writer***. By changing media
type (`WriterInterceptorContext.setMediaType()`) the interceptor can cause that different message body writer will be
chosen. The interceptor can also completely replace the entity if it is needed. However, ***for modification of headers,
request properties and such, the filters are usually more preferable choice. Interceptors are executed only when there
is any entity and when the entity is to be written***. So, *when you always want to add a new header to a response no
matter what, use filters as interceptors might not be executed when no entity is present*. Interceptors should modify
properties only for entity serialization and deserialization purposes.

Let's now look at an example of a `ReaderInterceptor`

```java
public class GZIPReaderInterceptor implements ReaderInterceptor {
 
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        final InputStream originalInputStream = context.getInputStream();
        context.setInputStream(new GZIPInputStream(originalInputStream));
        return context.proceed();
    }
}
```

The `GZIPReaderInterceptor` wraps the original input stream with the `GZIPInputStream`. All further reads from the
entity stream will cause that data will be decompressed by this stream. The interceptor method `aroundReadFrom()` must
return an entity. The entity is returned from the proceed method of the `ReaderInterceptorContext`. The proceed method
internally calls the wrapped interceptor which must also return an entity. The proceed method invoked from the last
interceptor in the chain calls message body reader which deserializes the entity end returns it. Every interceptor can
change this entity if there is a need but in the most cases interceptors will just return the entity as returned from
the proceed method.

As already mentioned above, interceptors should be primarily used to manipulate entity body. Similar to methods exposed
by `WriterInterceptorContext` the `ReaderInterceptorContext` introduces a set of methods for modification of
request/response properties like HTTP headers, URIs and/or HTTP methods (excluding getters and setters for entity as
entity has not been read yet). Again the same rules as for `WriterInterceptor` applies for changing these properties
(change only properties in order to influence reading of an entity).

## Filter and interceptor execution order

Let's look closer at the context of execution of filters and interceptors. The following steps describes scenario where
a JAX-RS client makes a POST request to the server. The server receives an entity and sends a response back with the
same entity. GZIP reader and writer interceptors are registered on the client and the server. Also filters are
registered on client and server which change the headers of request and response.

1. **Client request invoked**: The POST request with attached entity is built on the client and invoked.
2. **ClientRequestFilters**: client request filters are executed on the client and they manipulate the request headers.
3. **Client `WriterInterceptor`**: As the request contains an entity, writer interceptor registered on the client is
   executed before a `MessageBodyWriter` is executed. It wraps the entity output stream with the `GZipOutputStream`.
4. **Client `MessageBodyWriter`**: message body writer is executed on the client which serializes the entity into the
   new `GZipOutput` stream. This stream zips the data and sends it to the "wire".
5. **Server**: server receives a request. Data of entity is compressed which means that pure read from the entity input
   stream would return compressed data.
6. **Server pre-matching `ContainerRequestFilters`**: `ContainerRequestFilters` are executed that can manipulate
   resource method matching process.
7. **Server matching: resource method matching** is done.
8. **Server: post-matching `ContainerRequestFilters`**: `ContainerRequestFilters` post matching filters are executed.
   This include execution of all global filters (without name binding) and filters name-bound to the matched method.
9. **Server ReaderInterceptor**: reader interceptors are executed on the server. The `GZIPReaderInterceptor` wraps the
   input stream (the stream from the "wire") into the `GZipInputStream` and set it to context.
10. **Server MessageBodyReader**: server message body reader is executed and it deserializes the entity from new
    `GZipInputStream` (get from the context). This means the reader will read unzipped data and not the compressed data
    from the "wire".
11. **Server resource method is executed**: the deserialized entity object is passed to the matched resource method as a
    parameter. The method returns this entity as a response entity.
12. **Server ContainerResponseFilters are executed**: response filters are executed on the server and they manipulate
    the response headers. This include all global bound filters (without name binding) and all filters name-bound to the
    resource method.
13. **Server WriterInterceptor**: is executed on the server. It wraps the original output stream with a new
    `GZIPOuptutStream`. The original stream is the stream that "goes to the wire" (output stream for response from the
    underlying server container).
14. **Server MessageBodyWriter**: message body writer is executed on the server which serializes the entity into the
    `GZIPOutputStream`. This stream compresses the data and writes it to the original stream which sends this compressed
    data back to the client.
15. **Client receives the response**: the response contains compressed entity data.
16. **Client `ClientResponseFilters`**: client response filters are executed and they manipulate the response headers.
17. **Client response is returned**: the `javax.ws.rs.core.Response` is returned from the request invocation.
18. **Client code calls `response.readEntity()`**: read entity is executed on the client to extract the entity from the
    response.
19. **Client `ReaderInterceptor`**: the client reader interceptor is executed when `readEntity(Class)` is called. The
    interceptor wraps the entity input stream with `GZIPInputStream`. This will decompress the data from the original
    input stream.
20. **Client `MessageBodyReaders`**: client message body reader is invoked which reads decompressed data from
    `GZIPInputStream` and deserializes the entity.
21. **Client**: The entity is returned from the `readEntity()`.

It is worth to mention that in the scenario above the reader and writer interceptors are invoked only if the entity is
present (it does not make sense to wrap entity stream when no entity will be written). The same behaviour is there for
message body readers and writers. As mentioned above, interceptors are executed before the message body reader/writer as
a part of their execution and they can wrap the input/output stream before the entity is read/written. There are
exceptions when interceptors are not run before message body reader/writers but this is not the case of simple scenario
above. This happens for example when the entity is read many times from client response using internal buffering. Then
the data are intercepted only once and kept 'decoded' in the buffer. 

## Name binding

Filters and interceptors can be name-bound. ***Name binding is a concept that allows to say to a JAX-RS runtime that a
specific filter or interceptor will be executed only for a specific resource method***. When a filter or an interceptor
is limited only to a specific resource method we say that it is ***name-bound***. Filters and interceptors that do not
have such a limitation are called ***global***. 

Filter or interceptor can be assigned to a resource method using the ***`@NameBinding`*** annotation. **The annotation
is used as meta annotation for other user implemented annotations** that are applied to a providers and resource
methods. See the following example:

```java
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * @Compress annotation is the name binding annotation.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Compress {
    // intentionally left blanck
}
```

```java
... 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
...

@Path("helloworld")
public class HelloWorldResource {

    @GET
    @Produces("text/plain")
    public String getHello() {
        return "Hello World!";
    }

    @GET
    @Compress
    @Path("too-much-data")
    public String getVeryLongString() {
        String str = ... // very long string
        return str;
    }
}
```

```java
...
import java.util.zip.GZIPInputStream;
...

/**
 * GZIPWriterInterceptor will be executed only when resource methods annotated with {@link @Compress} annotation
 */
@Compress
public class GZIPWriterInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final OutputStream outputStream = context.getOutputStream();
        context.setOutputStream(new GZIPOutputStream(outputStream));
        context.proceed();
    }
}
```

The example above defines a new `@Compress` annotation which is a name binding annotation as it is annotated with
`@NameBinding`. The `@Compress` is applied on the resource method `getVeryLongString()` and on the interceptor
`GZIPWriterInterceptor`. The interceptor will be executed only if any resource method with such an annotation will be
executed. In our example case the interceptor will be executed only for the `getVeryLongString()` method. The
interceptor will not be executed for method `getHello()`. In this example the reason is probably clear. We would like to
compress only long data and we do not need to compress the short response of "Hello World!".

Name binding can be applied on a resource class. The `HelloWorldResource` above would be annotated with `@Compress`.
This would mean that all resource methods will use compression in this case.

There might be many name binding annotations defined in an application. **When any provider (filter or interceptor) is
annotated with more than one name binding annotation, then it will be executed for resource methods which contain *ALL*
these annotations**. So, for example if our interceptor would be annotated with another name binding annotation `@GZIP`
then the resource method would need to have both annotations attached, `@Compress` and `@GZIP`, otherwise the
interceptor would not be executed. Based on the previous paragraph we can even use the combination when the resource
method `getVeryLongString()` would be annotated with `@Compress` and resource class `HelloWorldResource` would be
annotated from with `@GZIP`. This would also trigger the interceptor as annotations of resource methods are aggregated
from resource method and from resource class. But this is probably just an edge case which will not be used so often.

Note that ***global filters are always executed***, even for resource methods which have any name binding annotations.

## Dynamic binding

***Dynamic binding*** is a way of assigning filters and interceptors to the resource methods in a dynamic manner. Name
binding uses a static approach and changes to binding require source code change and recompilation. With dynamic binding
you can implement code which defines bindings during the application initialization time. The following example shows
how to implement dynamic binding.


```java
@Path("helloworld")
public class HelloWorldResource {
 
    @GET
    @Produces("text/plain")
    public String getHello() {
        return "Hello World!";
    }
 
    @GET
    @Path("too-much-data")
    public String getVeryLongString() {
        String str = ... // very long string
        return str;
    }
}
```

```java
...
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.container.DynamicFeature;
...

/**
 * This dynamic binding provider registers GZIPWriterInterceptor only for HelloWorldResource and methods that contain
 * "VeryLongString" in their name.
 * <p>
 * It will be executed during application initialization phase.
 */
public class CompressionDynamicBinding implements DynamicFeature {
 
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (HelloWorldResource.class.equals(resourceInfo.getResourceClass()) &&
                resourceInfo.getResourceMethod().getName().contains("VeryLongString")
        ) {
            context.register(GZIPWriterInterceptor.class);
        }
    }
}
```

The example contains one `HelloWorldResource` which is known from the previous name binding example. The difference is
in the `getVeryLongString` method, which now does not define the `@Compress` name binding annotations. The binding is
done using the provider which implements `DynamicFeature` interface. The interface defines one configure method with
two arguments, `ResourceInfo` and `FeatureContext`. `ResourceInfo` contains information about the resource and method to
which the binding can be done. The configure method will be executed once for each resource method that is defined in
the application. In the example above the provider will be executed twice, once for the `getHello()` method and once for
`getVeryLongString()` ( once the `resourceInfo` will contain information about `getHello()` method and once it will
point to `getVeryLongString()`). If a dynamic binding provider wants to register any provider for the actual resource
method it will do that using provided `FeatureContext` which extends JAX-RS `Configurable` API. All methods for
registration of filter or interceptor classes or instances can be used. Such dynamically registered filters or
interceptors will be bound only to the actual resource method. In the example above the `GZIPWriterInterceptor` will be
bound only to the method `getVeryLongString()` which will cause that data will be compressed only for this method and
not for the method `getHello()`. The code of `GZIPWriterInterceptor` is in the examples above.

Note that filters and interceptors registered using dynamic binding are only additional filters run for the resource
method. If there are any name bound providers or global providers they will still be executed.

## Priorities

In case you register more filters and interceptors you might want to define an exact order in which they should be
invoked. The order can be controlled by the ***`@Priority`*** annotation defined by the `javax.annotation.Priority`
class. The annotation accepts an integer parameter of priority. Providers used in request processing
(`ContainerRequestFilter`, `ClientRequestFilter`) as well as entity interceptors (`ReaderInterceptor`,
`WriterInterceptor`) are sorted based on the priority in an ascending manner. So, ***a request filter with priority
defined with `@Priority(1000)` will be executed before another request filter with priority defined as
`@Priority(2000)`. Providers used during response processing (`ContainerResponseFilter`, `ClientResponseFilter`) are
executed in the reverse order*** (using descending manner), so a provider with the priority defined with
`@Priority(2000)` will be executed before another provider with priority defined with `@Priority(1000)`.

It's a good practice to assign a priority to filters and interceptors. Use ***`Priorities`*** class which defines
standardized priorities in JAX-RS for different usages, rather than inventing your own priorities. For example, when you
write an authentication filter you would assign a priority 1000 which is the value of **`Priorities.AUTHENTICATION`**.
The following example shows the filter from the beginning of this chapter with a priority assigned.

```java
...
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
...
 
@Priority(Priorities.HEADER_DECORATOR)
public class ResponseFilter implements ContainerResponseFilter {
 
    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) throws IOException {
        responseContext.getHeaders().add("X-Powered-By", "Jersey :-)");
    }
}
```

As this is a response filter and response filters are executed in the reverse order, any other filter with priority
lower than 3000 (`Priorities.HEADER_DECORATOR` is 3000) will be executed after this filter. So, for example
`AUTHENTICATION` filter (priority 1000) would be run after this filter.
