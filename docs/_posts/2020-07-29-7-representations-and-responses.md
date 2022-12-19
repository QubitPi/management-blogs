---
layout: post
title: Representations and Responses
tags: [Response, Java Type]
color: orange
feature-img: "assets/img/post-cover/7-cover.png"
thumbnail: "assets/img/post-cover/7-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Representations and Java Types

Previous posts on `@Produces` and `@Consumes` annotations referred to media type of an entity representation. Examples
above depicted resource methods that could consume and/or produce `String` Java type for a number of different media
types. This approach is easy to understand and relatively straightforward when applied to simple use cases.

To cover also other cases, handling non-textual data for example or handling data stored in the file system, etc.,
JAX-RS implementations are required to support also other kinds of media type conversions where additional,
non-`String`, Java types are being utilized. Following is a short listing of the Java types that are supported out of
the box with respect to supported media type: 

* All media types (`*/*`)

   - `byte[]`
   - `java.lang.String`
   - `java.io.Reader` (inbound only)
   - `java.io.File`
   - `javax.activation.DataSource`
   - `javax.ws.rs.core.StreamingOutput` (outbound only)
 
* XML media types (`text/xml`, `application/xml` and `application/...+xml`)

   - `javax.xml.transform.Source`
   - `javax.xml.bind.JAXBElement`
   - Application supplied JAXB classes (types annotated with
     [@XmlRootElement](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/annotation/XmlRootElement.html) or
     [@XmlType](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/annotation/XmlType.html))
     
* Form content (`application/x-www-form-urlencoded`)

   - `MultivaluedMap<String,String>`
   
* Plain text (`text/plain`)

   - `java.lang.Boolean`
   - `java.lang.Character`
   - `java.lang.Number`
   
Unlike method parameters that are associated with the extraction of request parameters, the method parameter associated
with the representation being consumed does not require annotating. In other words the representation (entity) parameter
does not require a specific 'entity' annotation. A method parameter without an annotation is an entity. A maximum of one
such unannotated method parameter may exist since there may only be a maximum of one such representation sent in a
request.

The representation being produced corresponds to what is returned by the resource method. For example JAX-RS makes it
simple to produce images that are instance of `File` as follows:

```java
@GET
@Path("/images/{image}")
@Produces("image/*")
public Response getImage(@PathParam("image") String image) {
    File f = new File(image);
    
    if (!f.exists()) {
      throw new WebApplicationException(404);
    }
    
    String mt = new MimetypesFileTypeMap().getContentType(f);
    return Response.ok(f, mt).build();
}
```

The `File` type can also be used when consuming a representation (request entity). In that case a temporary file will be
created from the incoming request entity and passed as a parameter to the resource method.

The `Content-Type` response header (if not set programmatically as described in the next section) will be automatically
set based on the media types declared by `@Produces` annotation. Given the following method, the most acceptable media
type is used when multiple output media types are allowed:

```java
@GET
@Produces({"application/xml", "application/json"})
public String doGetAsXmlOrJson() {
    ...
}
```

If `application/xml` is the most acceptable media type defined by the request (e.g. by header
`Accept: application/xml`), then the `Content-Type` response header will be set to `application/xml`.

## Building Responses

Sometimes it is necessary to return additional information in response to a HTTP request. Such information may be built
and returned using `Response` and `Response.ResponseBuilder`. For example, a common RESTful pattern for the creation of
a new resource is to support a POST request that returns a 201 (Created) status code and a `Location` header whose value
is the URI to the newly created resource. This may be achieved as follows:

```java
@POST
@Consumes("application/xml")
public Response post(String content) {
    URI createdUri = ...
    create(content);
    return Response
            .created(createdUri)
            .build();
}
```

In the code snippet above no representation produced is returned, this can be achieved by building an entity as part of
the response as follows:

```java
@POST
@Consumes("application/xml")
public Response post(String content) {
    URI createdUri = ...
    String createdContent = create(content);
    return Response
            .created(createdUri)
            .entity(Entity.text(createdContent))
            .build();
}
```

Response building provides other functionality such as setting the entity tag and last modified date of the
representation.

## `WebApplicationException` and Mapping Exceptions to Responses

Previous section shows how to return HTTP responses, that are built up programmatically. It is possible to use the very
same mechanism to return HTTP errors directly, e.g. when handling exceptions in a try-catch block. However, to better
align with the Java programming model, JAX-RS allows to define direct mapping of Java exceptions to HTTP error
responses. 

The following example shows throwing `CustomNotFoundException` from a resource method in order to return an error HTTP
response to the client:

```java
@Path("items/{itemId}/")
public Item getItem(@PathParam("itemId") String itemId) {
    Item item = getItems().get(itemId);
    if (item == null) {
      throw new CustomNotFoundException("Item, " + itemId + ", is not found");
    }
    
    return item;
}
```

This exception is an application specific exception that extends `WebApplicationException` and builds a HTTP response
with the 404 status code and an optional message as the body of the response: 

```java
public class CustomNotFoundException extends WebApplicationException {
 
    /**
     * Create a HTTP 404 (Not Found) exception.
     */
    public CustomNotFoundException() {
        super(Responses.notFound().build());
    }
    
    /**
     * Create a HTTP 404 (Not Found) exception.
     *
     * @param message  The {@link String} that is the entity of the 404 response.
     */
    public CustomNotFoundException(String message) {
        super(Response.status(Responses.NOT_FOUND).
        entity(message).type("text/plain").build());
    }
}
```

In other cases it may not be appropriate to throw instances of `WebApplicationException`, or classes that extend
`WebApplicationException`, and instead it may be preferable to map an existing exception to a response. For such cases
it is possible to use a custom exception mapping provider. The provider must implement the
`ExceptionMapper<E extends Throwable>` interface. For example, the following maps the
`EntityNotFoundException` to a HTTP 404 (Not Found) response:

```java
@Provider
public class EntityNotFoundMapper implements ExceptionMapper<javax.persistence.EntityNotFoundException> {

    public Response toResponse(javax.persistence.EntityNotFoundException exception) {
        return Response
                .status(404).
                entity(exception.getMessage()).
                type("text/plain").
                build();
    }
}
```

The above class is annotated with `@Provider`, this declares that the class is of interest to the JAX-RS runtime. Such a
class may be added to the set of classes of the `Application` instance that is configured. When an application throws an
`EntityNotFoundException` the `toResponse` method of the `EntityNotFoundMapper` instance will be invoked.

Jersey supports extension of the exception mappers. These extended mappers must implement the
`org.glassfish.jersey.spi.ExtendedExceptionMapper` interface. This interface additionally defines method
`isMappable(Throwable)` which will be invoked by the Jersey runtime when exception is thrown and this provider is
considered as mappable based on the exception type. Using this method the provider can reject mapping of the exception
before the method `toResponse` is invoked. The provider can for example check the exception parameters and based on them
return false and let other provider to be chosen for the exception mapping.

## Conditional GETs and Returning 304 (Not Modified) Responses

Conditional GETs are a great way to reduce bandwidth, and potentially improve on the server-side performance, depending
on how the information used to determine conditions is calculated. A well-designed web site may for example return 304
(Not Modified) responses for many of static images it serves.

JAX-RS provides support for conditional GETs using the contextual interface `Request`.

The following example shows conditional GET support:

```java
public SparklinesResource(
    @QueryParam("data") IntegerList data,
    @QueryParam("limits") @DefaultValue("0,100") Interval limits,
    @Context Request request,
    @Context UriInfo ui
) {
    if (data == null) {
        throw new WebApplicationException(400);
    }
    
    this.data = data;
    this.limits = limits;
    
    if (!limits.contains(data)) {
        throw new WebApplicationException(400);
    }
    
    this.tag = computeEntityTag(ui.getRequestUri());
    
    if (request.getMethod().equals("GET")) {
        Response.ResponseBuilder rb = request.evaluatePreconditions(tag);
        if (rb != null) {
            throw new WebApplicationException(rb.build());
        }
    }
}
```

The constructor of the `SparklinesResouce` root resource class computes an entity tag from the request URI and then
calls the `request.evaluatePreconditions` with that entity tag. If a client request contains an `If-None-Match` header
with a value that contains the same entity tag that was calculated then the `evaluatePreconditions` returns a pre-filled
out response, with the 304 status code and entity tag set, that may be built and returned. Otherwise,
`evaluatePreconditions` returns `null` and the normal response can be returned.

Notice that in this example the constructor of a resource class is used to perform actions that may otherwise have to be
duplicated to be invoked for each resource method. The life cycle of resource classes is per-request which means that
the resource instance is created for each request and therefore can work with request parameters and for example make
changes to the request processing by throwing an exception as it is shown in this example.
