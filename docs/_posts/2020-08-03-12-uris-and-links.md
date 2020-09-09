---
layout: post
title: URIs and Links
tags: [URI, Links]
color: rgb(107, 91, 149)
feature-img: "assets/img/pexels/design-art/2020-08-03-12-uris-and-links/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-03-12-uris-and-links/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Building URIs
   
A very important aspect of REST is hyperlinks, URIs, in representations that clients can use to transition the Web
service to new application states (this is otherwise known as "hypermedia as the engine of application state"). HTML
forms present a good example of this in practice.

Building URIs and building them safely is not easy with
[URI](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html), which is why JAX-RS has the ***`UriBuilder`*** class
that makes it simple and easy to build URIs safely. `UriBuilder` can be used to build new URIs or build from existing
URIs. For resource classes it is more than likely that URIs will be built from the base URI the web service is deployed
at or from the request URI. The class ***`UriInfo`*** provides such information (in addition to further information, see
next section).

The following example shows URI building with `UriInfo` and `UriBuilder` from the bookmark example:

```java
@Path("/users/")
public class UsersResource {
 
    @Context
    UriInfo uriInfo;
 
    ...
 
    @GET
    @Produces("application/json")
    public JSONArray getUsersAsJsonArray() {
        JSONArray uriArray = new JSONArray();

        for (UserEntity userEntity : getUsers()) {
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            URI userUri = uriBuilder.path(userEntity.getUserId()).build();
            uriArray.put(userUri.toASCIIString());
        }

        return uriArray;
    }
}
```

`UriInfo` is obtained using the `@Context` annotation, and in this particular example injection onto the field of the
root resource class is performed, previous examples showed the use of `@Context` on resource method parameters.

`UriInfo` can be used to obtain URIs and associated `UriBuilder` instances for the following URIs: the base URI the
application is deployed at; the request URI; and the absolute path URI, which is the request URI minus any query
components.

The `getUsersAsJsonArray` method constructs a `JSONArrray`, where each element is a URI identifying a specific user
resource. The URI is built from the absolute path of the request URI by calling `UriInfo.getAbsolutePathBuilder()`. A
new path segment is added, which is the user ID, and then the URI is built. Notice that it is not necessary to worry
about the inclusion of '/' characters or that the user ID may contain characters that need to be percent encoded.
`UriBuilder` takes care of such details.

`UriBuilder` can be used to build/replace query or matrix parameters. URI templates can also be declared, for example
the following will build the URI `"http://localhost/segment?name=value"`:

```java
UriBuilder
        .fromUri("http://localhost/")
        .path("{a}")
        .queryParam("name", "{value}")
        .build("segment", "value");
```

## Resolve and Relativize

JAX-RS 2.0 introduced additional URI resolution and relativization methods in the `UriBuilder`:

* `UriInfo.resolve(java.net.URI)`
* `UriInfo.relativize(java.net.URI)`
* `UriBuilder.resolveTemplate(...)` (various arguments)

Resolve and relativize methods in `UriInfo` are essentially counterparts to the methods listed above -
`UriInfo.resolve(java.net.URI)` resolves given relative URI to an absolute URI using application context URI as the base
URI; `UriInfo.relativize(java.net.URI)` then transforms an absolute URI to a relative one, using again the applications
context URI as the base URI. 

***`UriBuilder` also introduces a set of methods that provide ways of resolving URI templates by replacing individual
templates with a provided value(s)***. A short example:

```java
final URI uri = UriBuilder
        .fromUri("http://{host}/{path}?q={param}")
        .resolveTemplate("host", "localhost")
        .resolveTemplate("path", "myApp")
        .resolveTemplate("param", "value").build();
 
uri.toString(); // returns "http://localhost/myApp?q=value"
```

See the `UriBuilder` javadoc for more details.

## Link

JAX-RS 2.0 introduces ***`Link`*** class, which serves as a representation of Web Link defined in
[RFC 5988](https://tools.ietf.org/html/rfc5988). The JAX-RS `Link` class adds API support for providing additional
metadata in HTTP messages, for example, if you are consuming a REST interface of a public library, you might have a
resource returning description of a single book. Then you can include links to related resources, such as a book
category, author, etc. to make the produced response concise but complete at the same time. Clients are then able to
query all the additional information they are interested in and are not forced to consume details they are not
interested in. At the same time, this approach relieves the server resources as only the information that is truly
requested is being served to the clients.

A `Link` can be serialized to an HTTP message (tyically a response) as additional HTTP header (there might be multiple
`Link` headers provided, thus multiple links can be served in a single message). Such HTTP header may look like:

```
Link: <http://example.com/TheBook/chapter2>; rel="prev"; title="previous chapter"
```

Producing and consuming Links with JAX-RS API is demonstrated in the following example: 

```java
// server side - adding links to a response:
Response response = Response
        .ok()
        .link("http://oracle.com", "parent")
        .link(new URI("http://eclipse-ee4j.github.io/jersey"), "framework")
        .build();
 
...
 
// client-side processing:
final Response response = target.request().get();
 
URI parentUri = response.getLink("parent").getUri();
URI frameworkUri = response.getLink("framework").getUri();
```

Instances of `Link` can be also created directly by invoking one of the factory methods on the `Link` API that returns a
`Link.Builder` that can be used to configure and produce new links.
