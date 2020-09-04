---
layout: post
title: Asynchronous Services and Clients
tags: [Asynchronous API, AsyncResponse, Callback, Chunked Output, Chunked Input]
color: rgb(255, 111, 97)
author: QubitPi
excerpt_separator: <!--more-->
---

This post describes the usage of ***asynchronous API*** on the client and server side. The term async will be sometimes
used interchangeably with the term asynchronous in this chapter. 

<!--more-->

* TOC
{:toc}

## Asynchronous Server API

**Request processing on the server works by default in a synchronous processing mode**, which means that a client
connection of a request is processed in a single I/O container thread. Once the thread processing the request returns to
the I/O container, the container can safely assume that the request processing is finished and that the client
connection can be safely released including all the resources associated with the connection. **This model is typically
sufficient for processing of requests for which the processing resource method execution takes a relatively short
time**. However, in cases where a resource method execution is known to take a long time to compute the result,
server-side ***asynchronous processing model*** should be used. In this model, the association between a request
processing thread and client connection is broken. I/O container that handles incoming request may no longer assume that
a client connection can be safely closed when a request processing thread returns. Instead a facility for explicitly
suspending, resuming and closing client connections needs to be exposed. Note that **the use of server-side asynchronous
processing model will not improve the request processing time perceived by the client. It will however increase the
throughput of the server**, by releasing the initial request processing thread back to the I/O container while the
request may still be waiting in a queue for processing or the processing may still be running on another dedicated
thread. The released I/O container thread can be used to accept and process new incoming request connections.

The following example shows a simple asynchronous resource method defined using the new JAX-RS async API:


```java
@Path("/resource")
public class AsyncResource {

    @GET
    public void asyncGet(@Suspended final AsyncResponse asyncResponse) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = veryExpensiveOperation();
                asyncResponse.resume(result);
            }
 
            private String veryExpensiveOperation() {
                // ... very expensive operation
            }
        }).start();
    }
}
```

In the example above, a resource `AsyncResource` with one GET method `asyncGet` is defined. The `asyncGet` method
injects a JAX-RS ***`AsyncResponse`*** instance using a JAX-RS ***`@Suspended`*** annotation. Please note that
***`AsyncResponse` must be injected by the `@Suspended` annotation*** and not by `@Context`, because not only does
`@Suspended` inject response but also says that the method is executed in the asynchronous mode. By **injecting the
`AsyncResponse` parameter into a resource method we tell the Jersey runtime that the method is supposed to be invoked
using the asynchronous processing mode**, that is the client connection should not be automatically closed by the
underlying I/O container when the method returns. Instead, the injected **`AsyncResponse` instance (that represents the
suspended client request connection) will be used to explicitly send the response back to the client using some other
thread**. In other words, Jersey runtime knows that when the `asyncGet` method completes, the response to the client may
not be ready yet and the processing must be suspended and wait to be explictly resumed with a response once it becomes
available. Note that the method `asyncGet` *returns void* in our example. This is perfectly valid in case of an
asynchronous JAX-RS resource method, even for a GET method, as the response is never returned directly from the resource
method as its return value. Instead, the response is later returned using `AsyncResponse` instance as it is demonstrated
in the example. The `asyncGet` resource method **starts a new thread** and exits from the method. In that state the
request processing is suspended and the container thread (the one which entered the resource method) is returned back to
the container's thread pool and it can process other requests. New thread started in the resource method may execute an
expensive operation which might take a long time to finish. Once a result is ready it is resumed using the
***`resume()`*** method on the `AsyncResponse` instance. The resumed response is then processed in the new thread by
Jersey in a same way as any other synchronous response, including execution of filters and interceptors, use of
exception mappers as necessary and sending the response back to the client.

It is important to note that the asynchronous response (`asyncResponse` in the example) does not need to be resumed from
the thread started from the resource method. The asynchronous response can be resumed even from different request
processing thread as it is shown in the the example of the `AsyncResponse` javadoc. In the javadoc example the async
response suspended from the GET method is resumed later on from the POST method. The suspended async response is passed
between requests using a static field and is resumed from the other resource method running on a different request
processing thread.

Imagine now a situation when there is a long delay between two requests and you would not like to let the client wait
for the response "forever" or at least for an unacceptable long time. In asynchronous processing model, occurrences of
such situations should be carefully considered with client connections not being automatically closed when the
processing method returns and the response needs to be resumed explicitly based on an event that may actually even never
happen. To tackle these situations ***asynchronous timeouts*** can be used.

The following example shows the usage of timeouts: 

```java
@GET
public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
    asyncResponse.setTimeoutHandler(new TimeoutHandler() {
        @Override
        public void handleTimeout(AsyncResponse asyncResponse) {
            asyncResponse.resume(
                    Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .entity("Operation time out.")
                            .build()
        )};
    });

    asyncResponse.setTimeout(20, TimeUnit.SECONDS);
 
    new Thread(new Runnable() {
 
        @Override
        public void run() {
            String result = veryExpensiveOperation();
            asyncResponse.resume(result);
        }
 
        private String veryExpensiveOperation() {
            // ... very expensive operation that typically finishes within 20 seconds
        }
    }).start();
}
```

By default, there is no timeout defined on the suspended `AsyncResponse` instance. ***A custom timeout and timeout event
handler may be defined using `setTimeoutHandler(TimeoutHandler)` and `setTimeout(long, TimeUnit)` methods***. The
`setTimeoutHandler(TimeoutHandler)` method defines the handler that will be invoked when timeout is reached. The handler
resumes the response with the response code 503 (from `Response.Status.SERVICE_UNAVAILABLE`). A timeout interval can be
also defined without specifying a custom timeout handler (using just the `setTimeout(long, TimeUnit)` method). In such
case the default behaviour of Jersey runtime is to throw a `ServiceUnavailableException` that gets mapped into 503,
"Service Unavailable" HTTP error response, as defined by the JAX-RS specification.

### Asynchronous Server-side Callbacks

As operations in asynchronous cases might take long time and they are not always finished within a single resource
method invocation, JAX-RS offers facility to register ***callbacks*** to be invoked based on suspended async response
state changes. In Jersey you can register two JAX-RS callbacks:

* ***`CompletionCallback`*** that is executed when request finishes or fails, and
* ***`ConnectionCallback`*** which is executed when a connection to a client is closed or lost. 

```java
@Path("/resource")
public class AsyncResource {

    private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;
 
    @GET
    public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
        asyncResponse.register(new CompletionCallback() {
            @Override
            public void onComplete(Throwable throwable) {
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    numberOfSuccessResponses++;
                } else {
                    numberOfFailures++;
                    lastException = throwable;
                }
            }
        });
 
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = veryExpensiveOperation();
                asyncResponse.resume(result);
            }
 
            private String veryExpensiveOperation() {
                // ... very expensive operation
            }
        }).start();
    }
}
```

A completion callback is registered using `register(...)` method on the `AsyncResponse` instance. A registered
completion callback is bound only to the response(s) to which it has been registered. In the example the
`CompletionCallback` is used to calculate successfully processed responses or failures, and to store last exception.
This is only a simple case demonstrating the usage of the callback. You can use completion callback to release the
resources, change state of internal resources or representations or handle failures. The method has an argument
`Throwable` which is set only in case of an error. Otherwise the parameter will be `null`, which means that the response
was successfully written. ***The callback is executed only after the response is written to the client (not immediately
after the response is resumed)***.

The AsyncResponse `register(...)` method is overloaded and offers options to register a single callback as an `Object`
(in the example), as a `Class` or multiple callbacks using `varags`.

As some async requests may take long time to process the client may decide to terminate its connection to the server
before the response has been resumed or before it has been fully written to the client. To deal with these use cases a
***`ConnectionCallback`*** can be used. This callback will be executed only if the connection was prematurely terminated
or lost while the response is being written to the back client. Note that this callback will not be invoked when a
response is written successfully and the client connection is closed as expected. See javadoc of `ConnectionCallback`
for more information.

### Chunked Output

Jersey offers a facility for sending response to the client in multiple more-or-less independent chunks using a
***chunked output***. Each response chunk usually takes some (longer) time to prepare before sending it to the client.
The most important fact about response chunks is that you want to send them to the client immediately as they become
available without waiting for the remaining chunks to become available too. The first bytes of each chunked response
consists of the HTTP headers that are sent to the client. As noted above, the entity of the response is then sent in
chunks as they become available. Client knows that the response is going to be chunked, so it reads each chunk of the
response separately, processes it, and waits for more chunks to arrive on the same connection. After some time, the
server generates another response chunk and send it again to the client. Server keeps on sending response chunks until
it closes the connection after sending the last chunk when the response processing is finished.

In Jersey you can use
***[ChunkedOutput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ChunkedOutput.html)***
to send response to a client in chunks. Chunks are strictly defined pieces of a response body that can be marshalled as
a separate entities using Jersey/JAX-RS `MessageBodyWriter<T>` providers. A chunk can be `String`, `Long` or JAXB bean
serialized to XML or JSON or any other dacustom type for which a `MessageBodyWriter<T>` is available.

**The resource method that returns `ChunkedOutput` informs the Jersey runtime that the response will be chunked and that
the processing works asynchronously** as such. You do not need to inject `AsyncResponse` to start the asynchronous
processing mode in this case. Returning a `ChunkedOutput` instance from the method is enough to indicate the
asynchronous processing. Response headers will be sent to a client when the resource method returns and the client will
wait for the stream of chunked data which you will be able to write from different thread using the same `ChunkedOutput`
instance returned from the resource method earlier. The following example demonstrates this use case:

```java
@Path("/resource")
public class AsyncResource {

    @GET
    public ChunkedOutput<String> getChunkedResponse() {
        final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
 
        new Thread() {
            public void run() {
                try {
                    String chunk;
 
                    while ((chunk = getNextString()) != null) {
                        output.write(chunk);
                    }
                } catch (IOException e) {
                    // IOException thrown when writing the
                    // chunks of response: should be handled
                } finally {
                    output.close();
                        // simplified: IOException thrown from
                        // this close() should be handled here...
                }
            }
        }.start();
 
        // the output will be probably returned even before
        // a first chunk is written by the new thread
        return output;
    }
 
    private String getNextString() {
        // ... long running operation that returns
        //     next string or null if no other string is accessible
    }
}
```

The example above defines a GET method that returns a `ChunkedOutput` instance. The generic type of `ChunkedOutput`
defines the chunk types (in this case chunks are `String`s). Before the instance is returned a new thread is started
that writes individual chunks into the chunked output instance named `output`. Once the original thread returns from the
resource method, Jersey runtime writes headers to the container response but does not close the client connection yet
and waits for the response data to be written to the chunked output. New thread in a loop calls the method
`getNextString()` which returns a next `String` or `null` if no other `String` exists (the method could for example load
latest data from the database). Returned `String`s are written to the chunked output. Such a written chunks are
internally written to the container response and client can read them. At the end the chunked `output` is closed which
determines the end of the chunked response. Please note that ***you must close the `output` explicitly*** in order to
close the client connection as Jersey does not implicitly know when you are finished with writing the chunks.

A chunked output can be processed also from threads created from another request as it is explained in the sections
above. This means that one resource method may e.g. only return a `ChunkedOutput` instance and other resource method(s)
invoked from another request thread(s) can write data into the chunked output and/or close the chunked response.

## Client API

The client API supports asynchronous processing too. Simple usage of asynchronous client API is shown in the following
example:

```java
final AsyncInvoker asyncInvoker = target()
        .path("http://example.com/resource/")
        .request()
        .async();
final Future<Response> responseFuture = asyncInvoker.get();

System.out.println("Request is being processed asynchronously.");

final Response response = responseFuture.get(); // get() waits for the response to be ready
    
System.out.println("Response received.");
```

The difference against synchronous invocation is that the http method call `get()` is not called on `SyncInvoker` but on
***`AsyncInvoker`***. The `AsyncInvoker` is returned from the call of method `Invocation.Builder.async()` as shown
above. `AsyncInvoker` offers methods similar to `SyncInvoker` but these methods do not return a response synchronously.
Instead a `Future<...>` representing response data is returned. These method calls also return immediately without
waiting for the actual request to complete. In order to get the response of the invoked `get()` method, the
`responseFuture.get()` is invoked which waits for the response to be finished (this call is blocking as defined by the
Java SE Future contract). 

Asynchronous Client API in JAX-RS is fully integrated in the fluent JAX-RS Client API flow, so that the async
client-side invocations can be written fluently just like in the following example:

```java
final Future<Response> responseFuture = target()
        .path("http://example.com/resource/")
        .request()
        .async()
        .get();
```

To work with asynchronous results on the client-side, all standard `Future` API facilities can be used. For example, you
can use the `isDone()` method to determine whether a response has finished to avoid the use of a blocking call to
`Future.get()`.

### Asynchronous Client Callbacks

Similarly to the server side, in the client API you can register asynchronous callbacks too. You can use these callbacks
to be notified when a response arrives instead of waiting for the response on `Future.get()` or checking the status by
`Future.isDone()` in a loop. A client-side asynchronous invocation callback can be registered as shown in the following
example:

```java
final Future<Response> responseFuture = target()
        .path("http://example.com/resource/")
        .request()
        .async()
        .get(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                System.out.println("Response status code " + response.getStatus() + " received.");
            }
 
            @Override
            public void failed(Throwable throwable) {
                System.out.println("Invocation failed.");
                throwable.printStackTrace();
            }
        });
```

The registered callback is expected to implement the ***`InvocationCallback`*** interface that defines two methods.
First method `completed(Response)` gets invoked when an invocation successfully finishes. The result response is passed
as a parameter to the callback method. The second method `failed(Throwable)` is invoked in case the invocation fails and
the exception describing the failure is passed to the method as a parameter. In this case since the callback generic
type is `Response`, the `failed(Throwable)` method would only be invoked in case the invocation fails because of an
internal client-side processing error. It would not be invoked in case a server responds with an HTTP error code, for
example if the requested resource is not found on the server and HTTP 404 response code is returned. In such case
`completed(Response)` callback method would be invoked and the response passed to the method would contain the returned
error response with HTTP 404 error code. This is a special behavior in case the generic callback return type is
`Response`. In the next example an exception is thrown (or `failed(Throwable)` method on the invocation callback is
invoked) even in case a non-2xx HTTP error code is returned.

As with the synchronous client API, you can retrieve the response entity as a Java type directly without requesting a
`Response` first. In case of an `InvocationCallback`, you need to set its generic type to the expected response entity
type instead of using the `Response` type as demonstrated in the example below:

```java
final Future<String> entityFuture = target()
        .path("http://example.com/resource/")
        .request()
        .async()
        .get(new InvocationCallback<String>() {

            @Override
            public void completed(String response) {
                System.out.println("Response entity '" + response + "' received.");
            }
 
            @Override
            public void failed(Throwable throwable) {
                System.out.println("Invocation failed.");
                throwable.printStackTrace();
            }
        });

System.out.println(entityFuture.get());
```

Here, the generic type of the invocation callback information is used to unmarshall the HTTP response content into a
desired Java type.

> ⚠️ Please note that in this case the method `failed(Throwable throwable)` would be invoked even for cases when a
> server responds with a non HTTP-2xx HTTP error code. This is because in this case the user does not have any other
> means of finding out that the server returned an error response.

### Chunked Input

In an [earlier section](#chunked-output) the `ChunkedOutput` was described. It was shown how to use a chunked output on
the server. ***In order to read chunks on the client the
[ChunkedInput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ChunkedInput.html)
can be used to complete the story***.

You can, of course, process input on the client as a standard input stream but if you would like to leverage Jersey
infrastructure to provide support of translating message chunk data into Java types using a `ChunkedInput` is much more
straightforward. See the usage of the `ChunkedInput` in the following example:

```java
final Response response = target()
        .path("http://example.com/resource/")
        .request()
        .get();
final ChunkedInput<String> chunkedInput =
        response
        .readEntity(new GenericType<ChunkedInput<String>>() {});

String chunk;
while ((chunk = chunkedInput.read()) != null) {
    System.out.println("Next chunk received: " + chunk);
}
```

The response is retrieved in a standard way from the server. The entity is read as a `ChunkedInput` entity. In order to
do that the ***`GenericEntity<T>`*** is used to preserve a generic information at run time. If you would not use
`GenericEntity<T>`, Java language generic type erasure would cause that the generic information to get lost at compile
time and an exception would be thrown at run time complaining about the missing chunk type definition.

In the next lines in the example, individual chunks are being read from the response. Chunks can come with some delay,
so they will be written to the console as they come from the server. After receiving last chunk the `null` will be
returned from the `read()` method. This will mean that the server has sent the last chunk and closed the connection.
Note that the ***`read()` is a blocking operation** and the invoking thread is blocked until a new chunk comes.

Writing chunks with `ChunkedOutput` is simple, you only call method `write()` which writes exactly one chunk to the
output. With the input reading it is slightly more complicated. The `ChunkedInput` does not know how to distinguish
chunks in the byte stream unless being told by the developer. In order to define custom chunks boundaries, the
`ChunkedInput` offers possibility to register a
***[ChunkParser](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/ChunkParser.html)***
which reads chunks from the input stream and separates them. Jersey provides several chunk parser implementation and you
can implement your own parser to separate your chunks if you need. In our example above the default parser provided by
Jersey is used that separates chunks based on presence of a `\r\n` delimiting character sequence.

Each incoming input stream is firstly parsed by the `ChunkParser`, then each chunk is processed by the proper
`MessageBodyReader<T>`. You can define the media type of chunks to aid the selection of a proper `MessageBodyReader<T>`
in order to read chunks correctly into the requested entity types (in our case into `String`s). 
