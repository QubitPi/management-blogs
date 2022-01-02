---
layout: post
title: Server-Sent Events (SSE) Support
tags: [Server-Sent Events, SSE, Broadcasting]
color: rgb(221, 65, 36)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## What are Server-Sent Events

In a standard HTTP request-response scenario a client opens a connection, sends a HTTP request to the server (for
example a HTTP GET request), then receives a HTTP response back and the server closes the connection once the response
is fully sent/received. The initiative always comes from a client when the client requests all the data. In contrast,
***Server-Sent Events (SSE) is a mechanism that allows server to asynchronously push the data from the server to the
client once the client-server connection is established by the client***. Once the connection is established by the
client, it is the server who provides the data and decides to send it to the client whenever new "chunk" of data is
available. When a new data event occurs on the server, the data event is sent by the server to the client. Thus the name
*Server-Sent Events*. Note that at high level there are more technologies working on this principle, a short overview of
the technologies supporting server-to-client communication is in this list:

* ***Polling*** - With polling a client repeatedly sends new requests to a server. If the server has no new data, then
  it send appropriate indication and closes the connection. The client then waits a bit and sends another request after
  some time (after one second, for example).
* ***Long-polling*** - With long-polling a client sends a request to a server. If the server has no new data, it just
  holds the connection open and waits until data is available. Once the server has data (message) for the client, it
  uses the connection and sends it back to the client. Then the connection is closed.
* ***Server-Sent events*** - SSE is similar to the long-polling mechanism, except it does not send only one message per
  connection. The client sends a request and server holds a connection until a new message is ready, then it sends the
  message back to the client while still keeping the connection open so that it can be used for another message once it
  becomes available. Once a new message is ready, it is sent back to the client on the same initial connection. Client
  processes the messages sent back from the server individually without closing the connection after processing each
  message. So, SSE typically reuses one connection for more messages (called events). SSE also defines a dedicated media
  type that describes a simple format of individual events sent from the server to the client. SSE also offers standard
  javascript client API implemented most modern browsers. For more information about SSE, see the
  [SSE API specification](https://www.w3.org/TR/eventsource/).
* ***WebSocket*** - WebSocket technology is different from previous technologies as it provides a real full duplex
  connection. The initiator is again a client which sends a request to a server with a special HTTP header that informs
  the server that the HTTP connection may be "upgraded" to a full duplex TCP/IP WebSocket connection. If server supports
  WebSocket, it may choose to do so. Once a WebSocket connection is established, it can be used for bi-directional
  communication between the client and the server. Both client and server can then send data to the other party at will
  whenever it is needed. The communication on the new WebSocket connection is no longer based on HTTP protocol and can
  be used for example for for online gaming or any other applications that require fast exchange of small chunks of data
  in flowing in both directions.
  
## When to Use Server-Sent Events

As explained above, SSE is a technology that allows clients to subscribe to event notifications that originate on a
server. Server generates new events and sends these events back to the clients subscribed to receive the notifications.
In other words, ***SSE offers a solution for a one-way publish-subscribe model***.

A good example of the use case where SSE can be used is a simple message exchange RESTful service. Clients POST new
messages to the service and subscribe to receive messages from other clients. Let's call the resource messages. While
POSTing a new message to this resource involves a typical HTTP request-response communication between a client and the
messages resource, subscribing to receive all new message notifications would be hard and impractical to model with a
sequence of standard request-response message exchanges. Using Server-sent events provides a much more practical
approach here. You can use SSE to let clients subscribe to the messages resource via standard GET request (use a SSE
client API, for example javascript API or Jersey Client SSE API) and let the server broadcast new messages to all
connected clients in the form of individual events (in our case using Jersey Server SSE API). Note that with Jersey a
SSE support is implemented as an usual JAX-RS resource method. There's no need to do anything special to provide a SSE
support in your Jersey/JAX-RS applications, your SSE-enabled resources are a standard part of your RESTful Web
application that defines the REST API of your application. The following sections of this post describes SSE support in
Jersey in more details.

## Server-Sent Events API

In previous JAX-RS versions, no standard API for server-sent events was defined. The SSE support bundled with Jersey was
Jersey-specific. With JAX-RS 2.1, situation changed and ***SSE API is well defined in the `javax.ws.rs.sse` package***.

Following sections will describe the new SSE API. For backwards compatibility reasons, the original Jersey-specific API
remains valid and will be described in Section
[Jersey-specific Server-Sent Events API](#jersey-specific-server-sent-events-api)

Jersey contains support for SSE for both - server and client. SSE in Jersey is implemented as an extension supporting a
new media type using existing "chunked" messages support. However, in contrast to the original API, the instances of SSE
related classes are not to be obtained manually by invoking constructors, nor to be directly returned from the resource
methods. Actually, *the implementing classes in the `jersey.media.sse.internal` package should never be needed to be
imported*. ***The only API to be used is directly in the JAX-RS package (`javax.ws.rs.sse`). Only builders in the API
along with dependency injection should be used and provides access to the entire functionality***.

In order to take advantage of the SSE support, the `jersey-media-sse` module has to be on classpath. In maven, this can
be achieved by adding the dependency to the SSE media type module:

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-sse</artifactId>
</dependency>
```

The `Feature` defined in the module is (forced) auto-discoverable, which means having the module on classpath is
sufficient, no need to further register it in the code.

### Implementing SSE Support in a JAX-RS Resource (with JAX-RS SSE API)

### Simple SSE Resource Method

As mentioned above, the SSE related are not instantiated directly. In this case, Jersey takes care of the dependencies
and injects the `SseEventSink` (represents the output) and `Sse` (provides factory methods for other SSE related types,
in this case it is used to retrieve the event builder).

```java
...
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.sse.OutboundSseEvent;
...
 
@Path("events")
public static class SseResource {
 
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getServerSentEvents(@Context SseEventSink eventSink, @Context Sse sse) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                // ... code that waits 1 second
                final OutboundSseEvent event = sse
                        .newEventBuilder()
                        .name("message-to-client")
                        .data(String.class, "Hello world " + i + "!")
                        .build();
                eventSink.send(event);
            }
        }).start();
    }
}
```

The code above defines the resource deployed on URI `"/events"`. This resource has a single `@GET` resource method which
***returns void***. This is an important difference against the original API. It is Jersey's responsibility to bind the
injected `SseEventSink` to the output chain.

Once an outbound event is ready, it can be written to the `EventSink`. At that point the event is serialized by internal
`OutboundEventWriter` which uses an appropriate `MessageBodyWriter<T>` to serialize the `"Hello world " + i + "!"`
string. You can send as many messages as you like. At the end of the thread execution the response is closed which also
closes the connection to the client. After that, no more messages can be sent to the client on this connection. If the
client would like to receive more messages, it would have to send a new request to the server to initiate a new SSE
streaming connection. 

A client connecting to our SSE-enabled resource will receive the following data from the entity stream: 

    event: message-to-client
    data: Hello world 0!
    
    event: message-to-client
    data: Hello world 1!
    
    event: message-to-client
    data: Hello world 2!
    
    event: message-to-client
    data: Hello world 3!
    
    event: message-to-client
    data: Hello world 4!
    
    event: message-to-client
    data: Hello world 5!
    
    event: message-to-client
    data: Hello world 6!
    
    event: message-to-client
    data: Hello world 7!
    
    event: message-to-client
    data: Hello world 8!
    
    event: message-to-client
    data: Hello world 9!
    
Each message is received with a delay of one second. 

> 📝 If you have worked with streams in JAX-RS, you may wonder what is the difference between
> [ChunkedOutput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ChunkedOutput.html)
> and [StreamingOutput].
>
> ***`ChunkedOutput` is Jersey-specific API***. It lets you send "chunks" of data without closing the client connection
> using series of convenient calls to `ChunkedOutput.write` methods that take POJO + chunk media type as an input and
> then use the configured JAX-RS `MessageBodyWriter<T>` providers to figure out the proper way of serializing each chunk
> POJO to bytes. Additionally, `ChunkedOutput` writes can be invoked multiple times on the same outbound response
> connection, i.e. individual chunks are written in each write, not the full response entity.
>
> ***`StreamingOutput` is, on the other hand, a low level JAX-RS API*** that works with bytes directly. You have to
> implement `StreamingOutput` interface yourself. Also, its `write(OutputStream)` method will be invoked by JAX-RS
> runtime only once per response and the call to this method is blocking, i.e. the method is expected to write the
> entire entity body before returning.

### Broadcasting with Jersey SSE

JAX-RS SSE API defines
[SseBroadcaster](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseBroadcaster.html)
which allows to broadcast individual events to multiple clients. A simple broadcasting implementation is shown in the
following example:

```java
...
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.sse.SseBroadcaster;
...
 
@Singleton
@Path("broadcast")
public static class BroadcasterResource {

    private Sse sse;
    private SseBroadcaster broadcaster;
 
    public BroadcasterResource(@Context final Sse sse) {
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
    }
 
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String broadcastMessage(String message) {
        final OutboundSseEvent event = sse.newEventBuilder()
            .name("message")
            .mediaType(MediaType.TEXT_PLAIN_TYPE)
            .data(String.class, message)
            .build();
 
        broadcaster.broadcast(event);
 
        return "Message '" + message + "' has been broadcast.";
    }
 
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToBroadcast(@Context SseEventSink eventSink) {
        this.broadcaster.register(eventSink);
    }
}
```

Let's explore the example together. ***The `BroadcasterResource` resource class is annotated with `@Singleton`
annotation which tells Jersey runtime that only a single instance of the resource class should be used to serve all the
incoming requests to `/broadcast` path. This is needed as we want to keep an application-wide single reference to the
private broadcaster field so we can use the same instance for all requests***. Clients that want to listen to SSE events
first send a GET request to the `BroadcasterResource`, that is handled by the `listenToBroadcast()` resource method. The
method is injected with a new `SseEventSink` representing the connection to the requesting client and registers this
`eventSink` instance with the singleton broadcaster by calling its `subscribe()` method. The method then, as already
explained returns `void` and Jersey runtime is responsible for binding the injected `EventSink` instance so as it would
have been returned from the resource method (note that really returning the `EventSink` from the resource method will
cause failure) and to bind the `eventSink` instance with the requesting client and send the response HTTP headers to the
client. The client connection remains open and the client is now waiting ready to receive new SSE events. All the events
are written to the `eventSink` by broadcaster later on. This way developers can conveniently handle sending new events
to all the clients that subscribe to them.

When a client wants to broadcast new message to all the clients listening on their SSE connections, it sends a POST
request to `BroadcasterResource` resource with the message content. The method `broadcastMessage(String)` is invoked on
`BroadcasterResource` resource with the message content as an input parameter. A new SSE outbound event is built in the
standard way and passed to the broadcaster. The broadcaster internally invokes `write(OutboundEvent)` on all registered
`EventSinks`. After that the method just returns a standard text response to the POSTing client to inform the client
that the message was successfully broadcast. As you can see, the `broadcastMessage(String)` resource method is just a
simple JAX-RS resource method.

In order to implement such a scenario, you may have noticed, that the `SseBroadcaster` is not mandatory to complete the
use case. Individual `EventSinks` can be just stored in a collection and iterated over in the `broadcastMessage` method.
However, the `SseBroadcaster` internally identifies and handles also client disconnects. When a client closes the
connection, the `broadcaster` detects this and removes the stale connection from the internal collection of the
registered `EventSinks` as well as freeing all the server-side resources associated with the stale connection.
Additionally, the `SseBroadcaster` is implemented to be thread-safe, so that clients can connect and disconnect at any
time and `SseBroadcaster` will always broadcast messages to the most recent collection of registered and active set of
clients.  

## Consuming SSE Events within Jersey Clients

```java
import javax.ws.rs.sse.SseEventSource;
...
Client client = ClientBuilder.newBuilder().build();
WebTarget target = client.target("http://example.com/events");
SseEventSource sseEventSource = SseEventSource.target(target).build();
sseEventSource.register(event -> System.out.println(event.getName() + "; " + event.readData(String.class)));
sseEventSource.open();
 
// do other stuff, block here and continue when done
 
sseEventSource.close();
```

In this example, the `Client` instance is created (and initialized with [`SseFeature`](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html)
automatically). Then the `WebTarget` is built. In this case a request to the web target is not made directly in the
code, instead, the web target instance is used to initialize a new `SseEventSource.Builder` instance that is used to
build a new `SseEventSource`. The choice of `build()` method is important, as it tells the `SseEventSource.Builder` to
create a new `SseEventSource` that is not automatically connected to the target. The connection is established only
later by manually invoking the `sseEventSource.open()` method. A custom `java.util.function.Consumer<InboundSseEvent>`
implementation is used to listen to and process incoming SSE events. The method `readData(Class)` says that the event
data should be de-serialized from a received `InboundSseEvent` instance into a `String` Java type. This method call
internally executes `MessageBodyReader<T>` which de-serializes the event data. This is similar to reading an entity from
the `Response` by `readEntity(Class)`. The method `readData` can throw a `ProcessingException`.

After a connection to the server is opened by calling the `open()` method on the event source, the `eventSource` starts
listening to events. When an event comes, the listener will be executed by the event source. Once the client is done
with processing and does not want to receive events the connection by calling the `close()` method on the event source. 

There are other events than the incoming data that also may occur. The `SseEventSource` for instance always signals,
that it has finished processing events, or there might also be an error while processing the messages. There are total
of four overloaded `subscribe()` methods defined in the API. 

```java
// 1. basic one - the one we used in the example
void subscribe(Consumer<InboundSseEvent> onEvent)
 
// 2. with an error callback
void subscribe(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError)
 
// 3. with an error callback and completion callback
void subscribe(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError, Runnable onComplete)
 
// 4. complete one - with error callback, completion callback an onSubscribe callback
void subscribe(
        Consumer<SseSubscription> onSubscribe,
        Consumer<InboundSseEvent> onEvent,
        Consumer<Throwable> onError,
        Runnable onComplete
)
```

Few notes to the `subscribe()` methods:

* All the overloaded methods have the `onEvent` handler. As shown in the example, this parameter is used to consume the
  SSE events with data.
* Except the basic one-arg method, all the others contain an `onError` handler. In case of error, the `SseEventSource`
  invokes the `onError` method of all its subscribers that registered the handler. This makes it possible to react to
  the error conditions in a custom manner.
* Another possible argument is the `onComplete` handler. It is invoked every time when the `SseEventSource` terminates
  normally. Either `onComplete` or `onError` will be called every time.
* The complete `subscribe()` method adds the `onSubscribe()` callback. This gives the subscriber a tool to manage the
  load and do a back-pressure by incrementally requesting only certain amount of items. When `SseEventSource` registers
  a new subscriber, it calls its `onSubscribe` handler and hands over the `javax.ws.rs.sse.SseSubscription` instance.
  This class only has two methods - `request(long)` for asking for a certain amount of events (often used as
  `request(Long.MAX_VALUE)` when no back-pressure is needed) and `cancel()` to stop receiving further events. 
* When using the full-arg version of `subscribe()`, it is the caller's responsibility to manage the amount of data it
  can handle. The `sseSubscription.request()` method MUST be called, otherwise the subscriber will not receive ANY data.
  Furthermore, in the current `SseEventSource` implementation, such a subscriber will block a thread and will
  occasionally lead to overflow of an internal buffer in `SseEventSource`. As mentioned, calling
  `subscription.request(Long.MAX_VALUE)`, e.g. in the registered `onSubscribe` handler is sufficient (and is also a
  default behaviour for all the other overloaded methods).

### `SseEventSource` Reconnect Support

The `SseEventSource` implementation supports automated recuperation from a connection loss, including negotiation of
delivery of any missed events based on the last received SSE event `id` field value, provided this field is set by the
server and the negotiation facility is supported by the server. In case of a connection loss, the last received SSE
event id field value is sent in the `Last-Event-ID` HTTP request header as part of a new connection request sent to the
SSE endpoint. Upon a receipt of such reconnect request, the SSE endpoint that supports this negotiation facility is
expected to replay all missed events.

> 📝 ***Note that SSE lost-event-negotiation facility is a best-effort mechanism which does not provide any guarantee
> that all events would be delivered without a loss. You should therefore not rely on receiving every single event and
> design your client application code accordingly.***

By default, when a connection to the SSE endpoint is lost, the event source will use a default delay before attempting
to reconnect to the SSE endpoint. The SSE endpoint can however control the client-side retry delay by including a
special retry field value in any event sent to the client. Jersey `SseEventSource` implementation automatically tracks
any received SSE event retry field values set by the endpoint and adjusts the reconnect delay accordingly, using the
last received retry field value as the new reconnect delay.

In addition to handling the standard connection losses, Jersey `SseEventSource` automatically deals with any
`HTTP 503 Service Unavailable` responses received from the SSE endpoint, that include a `Retry-After` HTTP header with a
valid value. The HTTP 503 + Retry-After technique is often used by HTTP endpoints as a means of connection and traffic
throttling. In case a HTTP 503 + Retry-After response is received in return to a connection request from SSE endpoint,
Jersey `SseEventSource` will automatically schedule a reconnect attempt and use the received `Retry-After` HTTP header
value as a one-time override of the reconnect delay.

### Jersey-specific Server-Sent Events API


> ⚠️ Prior to JAX-RS 2.1, server-sent events was not standardized and was optional and implementation-specific. Jersey
> provided its own version of SSE implementation that remains valid and functional to achieve backwards compatibility.
> This implementation is a Jersey-specific extension of JAX-RS (2.0) standard. It works with common JAX-RS resources the
> same way as the JAX-RS 2.1 based implementation does.
>
> Both implementations are compatible, which means client based on Jersey-specific SSE implementation can talk to server
> resource implemented using JAX-RS 2.1 based implementation and vice versa.

This section briefly describes the Jersey-specific support for SSE, focusing on the differences against the
[new SSE implementation](#server-sent-events-api)

The API contains SSE support for both - server and client. To use the Jersey-specific SSE API you need to include the
dependency to the *SSE media type module* the same way as for the JAX-RS SSE implementation.

```xml
<dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-sse</artifactId>
</dependency>
```

> 📝 Prior to Jersey 2.8, you had to manually register
> [SseFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html)
> in your application. (The `SseFeature` is a feature that can be registered for both, the client and the server.) Since
> Jersey 2.8, the feature gets automatically discovered and registered when Jersey SSE module is put on the
> application's classpath. The automatic discovery and registration of SSE feature can be suppressed by setting
> [DISABLE_SSE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html#DISABLE_SSE)
> property to `true`. The behavior can also be selectively suppressed in either client or server runtime by setting
> [DISABLE_SSE_CLIENT](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html#DISABLE_SSE_CLIENT)
> or
> [DISABLE_SSE_SERVER](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html#DISABLE_SSE_SERVER)
> property respectively.

### Implementing SSE Support in a JAX-RS Resource

#### Simple SSE Resource Method

```java
...
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
...
 
@Path("events")
public static class SseResource {
 
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        final EventOutput eventOutput = new EventOutput();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        // ... code that waits 1 second
                        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
                        eventBuilder.name("message-to-client");
                        eventBuilder.data(String.class, "Hello world " + i + "!");
                        final OutboundEvent event = eventBuilder.build();
                        eventOutput.write(event);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error when writing the event.", e);
                } finally {
                    try {
                        eventOutput.close();
                    } catch (IOException ioClose) {
                        throw new RuntimeException("Error when closing the event output.", ioClose);
                    }
                }
            }
        }).start();
        return eventOutput;
    }
}
```

The code above defines the resource deployed on URI `"/events"`. This resource has a single `@GET` resource method which
returns as an entity `EventOutput` - an extension of generic Jersey
[ChunkedOutput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ChunkedOutput.html)
API for output chunked message processing.

In the example above, the resource method creates a new thread that sends a sequence of 10 events. There is a 1 second
delay between two subsequent events as indicated in a comment. Each event is represented by `OutboundEvent` type and is
built with a help of an outbound event Builder. The `OutboundEvent` reflects the standardized format of SSE messages and
contains properties that represent name (for named events), comment, data or id.

Once an outbound event is ready, it can be written to the `eventOutput`. At that point the event is serialized by
internal `OutboundEventWriter` which uses an appropriate `MessageBodyWriter<T>` to serialize the
`"Hello world " + i + "!"` string. You can send as many messages as you like. At the end of the thread execution the
response is closed which also closes the connection to the client. After that, no more messages can be sent to the
client on this connection. If the client would like to receive more messages, it would have to send a new request to the
server to initiate a new SSE streaming connection.

A client connecting to our SSE-enabled resource will receive the exact same output as in the corresponding example in
the JAX-RS implementation example.

    event: message-to-client
    data: Hello world 0!
    
    event: message-to-client
    data: Hello world 1!
    
    ...

#### Broadcasting

Jersey SSE server API defines
[SseBroadcaster](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseBroadcaster.html)
which allows broadcasting individual events to multiple clients. A simple broadcasting implementation is shown in the
following example:

```java
...
import org.glassfish.jersey.media.sse.SseBroadcaster;
...
 
@Singleton
@Path("broadcast")
public static class BroadcasterResource {
 
    private SseBroadcaster broadcaster = new SseBroadcaster();
 
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String broadcastMessage(String message) {
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        OutboundEvent event = eventBuilder
                .name("message")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, message)
                .build();
 
        broadcaster.broadcast(event);
        return "Message '" + message + "' has been broadcast.";
    }
 
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput listenToBroadcast() {
        final EventOutput eventOutput = new EventOutput();
        this.broadcaster.add(eventOutput);
        return eventOutput;
    }
}
```

The example is similar to its JAX-RS counterpart. The `listenToBroadcast()` resource method creates a new `EventOutput`
representing the connection to the requesting client and registers this `eventOutput` instance with the singleton
`broadcaster`, using its `add(EventOutput)` method. The method then returns the `eventOutput` which causes Jersey to
bind the `eventOutput` instance with the requesting client and send the response HTTP headers to the client. The client
connection remains open and the client is now waiting ready to receive new SSE events. All the events are written to the
`eventOutput` by `broadcaster` later on.

When a client wants to broadcast new message to all the clients listening on their SSE connections, it sends a POST
request to `BroadcasterResource` resource with the message content. The method `broadcastMessage(String)` is invoked on
`BroadcasterResource` resource with the message content as an input parameter. A new SSE outbound event is built in the
standard way and passed to the `broadcaster`. The `broadcaster` internally invokes `write(OutboundEvent)` on all
registered `EventOutputs`. After that the method just return a standard text response to the POSTing client to inform
the client that the message was successfully broadcast.

### Consuming SSE Events with Jersey Clients

On the client side, Jersey exposes APIs that support receiving and processing SSE events using two programming models:

1. ***Pull model*** - pulling events from a
   [EventInput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventInput.html), or
2. ***Push model*** - listening for asynchronous notifications of `EventSource`

The push model is similar to what is implemented in the JAX-RS SSE API. The pull model does not have a direct
counterpart in the JAX-RS API and has to be implemented by the developer, if require

#### Reading SSE Events with EventInput

The events can be read on the client side from a
[EventInput](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventInput.html).
See the following code:

```java
Client client = ClientBuilder
            .newBuilder()
            .register(SseFeature.class)
            .build();
WebTarget target = client.target("http://localhost:9998/events");
 
EventInput eventInput = target
        .request()
        .get(EventInput.class);

while (!eventInput.isClosed()) {
    final InboundEvent inboundEvent = eventInput.read();

    if (inboundEvent == null) {
        // connection has been closed
        break;
    }

    System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
}
```

At first, a new JAX-RS/Jersey client instance is created with a `SseFeature` registered. Then a `WebTarget` instance is
retrieved from the client and is used to invoke a HTTP request. The returned response entity is directly read as a
`EventInput` Java type, which is an extension of Jersey `ChunkedInput` that provides generic support for consuming
chunked message payloads. The code in the example then process starts a loop to process the inbound SSE events read from
the eventInput response stream. Each chunk read from the input is a `InboundEvent`. The method
`InboundEvent.readData(Class)` provides a way for the client to indicate what Java type should be used for the event
data de-serialization. In our example, individual events are de-serialized as `String` Java type instances. This method 
internally finds and executes a proper `MessageBodyReader<T>` which is used to do the actual de-serialization. This is
similar to reading an entity from the `Response` by `readEntity(Class)`. The method `readData` can also throw a
`ProcessingException`.

The `null` check on `inboundEvent` is necessary to make sure that the chunk was properly read and connection has not
been closed by the server. Once the connection is closed, the loop terminates and the program completes execution. The
client code produces the following console output: 

    message-to-client; Hello world 0!
    message-to-client; Hello world 1!
    message-to-client; Hello world 2!
    message-to-client; Hello world 3!
    message-to-client; Hello world 4!
    message-to-client; Hello world 5!
    message-to-client; Hello world 6!
    message-to-client; Hello world 7!
    message-to-client; Hello world 8!
    message-to-client; Hello world 9!

#### Asynchronous SSE Processing with `EventSource`

The main Jersey-specific SSE client API component used to read SSE events asynchronously is
[EventSource](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventSource.html).
The usage of the `EventSource` is shown on the following example. 

```java
Client client = ClientBuilder
        .newBuilder()
        .register(SseFeature.class)
        .build();
WebTarget target = client.target("http://example.com/events");

EventSource eventSource = EventSource
        .target(target)
        .build();
EventListener listener = new EventListener() {
    @Override
    public void onEvent(InboundEvent inboundEvent) {
        System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
    }
};
eventSource.register(listener, "message-to-client");
eventSource.open();

...

eventSource.close();
```

The `Client` instance is again created and initialized with
[SseFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/SseFeature.html).
Then the `WebTarget` is built. In this case a request to the web target is not made directly in the code, instead, the
web target instance is used to initialize a new
[EventSource.Builder](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventSource.Builder.html)
instance that is used to build a new `EventSource`. The choice of `build()` method is important, as it tells the
`EventSource.Builder` to create a new `EventSource` that is not automatically connected to the target. The connection is
established only later by manually invoking the `eventSource.open()` method. A custom
[EventListener](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventListener.html)
implementation is used to listen to and process incoming SSE events. The method `readData(Class)` says that the event
data should be de-serialized from a received `InboundEvent` instance into a `String` Java type. This method call
internally executes `MessageBodyReader<T>` which de-serializes the event data. This is similar to reading an entity from
the `Response` by `readEntity(Class)`. The method readData can throw a `ProcessingException`.

The custom event source listener is registered in the event source via `EventSource.register(EventListener, String)`
method. The next method arguments define the names of the events to receive and can be omitted. If names are defined,
the listener will be associated with the named events and will only be invoked for events with a name from the set of
defined event names. It will not be invoked for events with any other name or for events without a name.

> ⚠️ It is a common mistake to think that unnamed events will be processed by listeners that are registered to process
> events from a particular name set. That is NOT the case! Unnamed events are only processed by listeners that are not
> name-bound. The same limitation applied to HTML5 Javascript SSE Client API supported by modern browsers.

After a connection to the server is opened by calling the `open()` method on the event source, the `eventSource` starts
listening to events. When an event named `"message-to-client"` comes, the listener will be executed by the event source.
If any other event comes (with a name different from `"message-to-client"`), the registered listener is not invoked.
Once the client is done with processing and does not want to receive events anymore, it closes the connection by calling
the `close()` method on the event source. 

The listener from the example above will print the following output:

    message-to-client; Hello world 0!
    message-to-client; Hello world 1!
    message-to-client; Hello world 2!
    message-to-client; Hello world 3!
    message-to-client; Hello world 4!
    message-to-client; Hello world 5!
    message-to-client; Hello world 6!
    message-to-client; Hello world 7!
    message-to-client; Hello world 8!
    message-to-client; Hello world 9!
    
When browsing through the Jersey SSE API documentation, you may have noticed that the
[EventSource](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/media/sse/EventSource.html)
implements [EventListener] and provides an empty implementation for the `onEvent(InboundEvent inboundEvent)` listener
method. This adds more flexibility to the Jersey client-side SSE API. Instead of defining and registering a separate
event listener, in simple scenarios you can also choose to derive directly from the `EventSource` and override the empty
listener method to handle the incoming events. This programming model is shown in the following example:

```java
Client client = ClientBuilder
        .newBuilder()
        .register(SseFeature.class)
        .build();
WebTarget target = client.target("http://example.com/events");

EventSource eventSource = new EventSource(target) {
    @Override
    public void onEvent(InboundEvent inboundEvent) {
        if ("message-to-client".equals(inboundEvent.getName())) {
            System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
        }
    }
};
...
eventSource.close();
```

In this example, the `EventSource` is constructed directly using a single-parameter constructor. ***This way, the
connection to the SSE endpoint is by default automatically opened at the event source creation***. The implementation of
the `EventListener` has been moved into the overridden `EventSource.onEvent(...)` method. However, this time, the
listener method will be executed for all events - unnamed as well as with any name. Therefore the code checks the name
whether it is an event with the name `"message-to-client"` that we want to handle. Note that you can still register
additional `EventListeners` later on. The overridden method on the event source allows you to handle messages even when
no additional listeners are registered yet.

##### EventSource Reconnect Support

Reconnect support in Jersey-specific `EventSource` works the same way as in the implementation of the JAX-RS
`SseEventSource`. 
