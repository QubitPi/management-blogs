---
layout: post
title: Reactive JAX-RS Client API
tags: [Reactive, API]
category: FINALIZED
color: purple
feature-img: "assets/img/post-cover/6-cover.png"
thumbnail: "assets/img/post-cover/6-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Reactive client extension is quite a generic API allowing end users to utilize the popular reactive programming model
when using JAX-RS Client. The API is designed to be extensible, so any existing reactive framework can integrate with it
and there is build in support for CompletionStage. Along with describing the API itself, this post also covers existing
extension modules and provides hints to implement a custom extension if needed. 

If you are not familiar with the JAX-RS Client API, it is recommended that you see post
[Client API](https://qubitpi.github.io/jersey-guide/2020/07/27/5-client-api.html) where the basics of JAX-RS Client API
along with some advanced techniques are described. 

---
**Warning**

Jersey 2.26 (JAX-RS 2.1 implementation) dropped Jersey-proprietary API in favor of JAX-RS 2.1 Reactive Client API. 

---

## Motivation for Reactive Client Extension

### The Problem

Imagine a travel agency whose information system consists of multiple basic services. These services might be built
using different technologies (JMS, EJB, WS, ...). For simplicity we presume that the services can be consumed using REST
interface via HTTP method calls (e.g. using a JAX-RS Client). We also presume that the basic services we need to work
with are:

* **Customers service** - provides information about customers of the travel agency.
* **Destinations service** - provides a list of visited and recommended destinations for an authenticated customer.
* **Weather service** - provides weather forecast for a given destination.
* **Quoting service** - provides price calculation for a customer to travel to a recommended destination.

The task is to create a publicly available feature that would, for an authenticated user, display a list of 10 last
visited places and also display a list of 10 new recommended destinations including weather forecast and price
calculations for the user. Notice that some of the requests (to retrieve data) depend on results of previous requests.
E.g. getting recommended destinations depends on obtaining information about the authenticated user first. Obtaining
weather forecast depends on destination information, etc. This relationship between some of the requests is an important
part of the problem and an area where you can take a real advantage of the reactive programming model.

One way how to obtain data is to make multiple HTTP method calls from the client (e.g. mobile device) to all services
involved and combine the retrieved data on the client. However, since the basic services are available in the internal
network only we'd rather create a public orchestration layer instead of exposing all internal services to the outside
world. The orchestration layer would expose only the desired operations of the basic services to the public. To limit
traffic and achieve lower latency we'd like to return all the necessary information to the client in a single response.

The orchestration layer is illustrated in the figure below. The layer accepts requests from the outside and is
responsible of invoking multiple requests to the internal services. When responses from the internal services are
available in the orchestration layer they're combined into a single response that is sent back to the client. 

{% include aligner.html images="pexels/2020-07-28-6-reactive-jax-rs-client-api/rx-client-problem.png" column=1 %}

The next sections describe various approaches (using JAX-RS Client) how the orchestration layer can be implemented.

### A Naive Approach

The simplest way to implement the orchestration layer is to use synchronous approach. For this purpose we can use JAX-RS
Client Sync API below. The implementation is simple to do, easy to read and straightforward to debug:

```java
final WebTarget destination = ...;
final WebTarget forecast = ...;
 
// Obtain recommended destinations.
List<Destination> recommended = Collections.emptyList();

try {
    recommended = destination
            .path("recommended").request()
            .header("Rx-User", "Sync") // Identify the user
            .get(new GenericType<List<Destination>>() {}); // Return a list of destinations
} catch (final Throwable throwable) {
    errors.offer("Recommended: " + throwable.getMessage());
}
 
// Forecasts. (depend on recommended destinations)
final Map<String, Forecast> forecasts = new HashMap<>();

for (final Destination dest : recommended) {
    try {
        forecasts.put(
            dest.getDestination(),
            forecast
                    .resolveTemplate("destination", dest.getDestination())
                    .request()
                    .get(Forecast.class)
        );
    } catch (final Throwable throwable) {
        errors.offer("Forecast: " + throwable.getMessage());
    }
}
```

The downside of this approach is its slowness. You need to sequentially process all the independent requests which means
that you're wasting resources. You are needlessly blocking threads, that could be otherwise used for some real work.

If you take a closer look at the example you can notice that at the moment when all the recommended destinations are
available for further processing we try to obtain forecasts for these destinations. Obtaining a weather forecast can be
done only for a single destination with a single request, so we need to make 10 requests to the Forecast service to get
all the destinations covered. In a synchronous way this means getting the forecasts one-by-one. When one response with a
forecast arrives we can send another request to obtain another one. This takes time. The whole process of constructing a
response for the client can be seen in figure below.

{% include aligner.html images="pexels/2020-07-28-6-reactive-jax-rs-client-api/rx-client-sync-approach.png " column=1 %}

As the figure shown, we have tried to quantify this with assigning an approximate time to every request we make to the
internal services. This way we can easily compute the time needed to complete a response for the client. For example,
obtaining

* *Customer details* takes 150 ms
* *Recommended destinations* takes 250 ms
* *Price calculation for a customer and destination* takes 170 ms (each)
* *Weather forecast for a destination* takes 330 ms (each)

When summed up, 5400 ms is approximately needed to construct a response for the client.

Synchronous approach is better to use for lower number of requests (where the accumulated time doesn't matter that much)
or for a single request that depends on the result of previous operations. 

### Optimized Approach

The amount of time needed by the synchronous approach can be lowered by invoking independent requests in parallel. We're
going to use JAX-RS Client Async API to illustrate this approach. The implementation in this case is slightly more
difficult to get right because of the nested callbacks and the need to wait at some points for the moment when all
partial responses are ready to be processed. The implementation is also a little bit harder to debug and maintain. The
nested calls are causing a lot of complexity here. An example of concrete Java code following the asynchronous approach
can be seen below

```java
final WebTarget destination = ...;
final WebTarget forecast = ...;
 
// Obtain recommended destinations. (does not depend on visited ones)
destination.path("recommended").request()
        .header("Rx-User", "Async") // Identify the user
        .async() // Async invoker
        // Return a list of destinations.
        .get(
            new InvocationCallback<List<Destination>>() {
                @Override
                public void completed(final List<Destination> recommended) {
                    final CountDownLatch innerLatch = new CountDownLatch(recommended.size());
     
                    // Forecasts. (depend on recommended destinations)
                    final Map<String, Forecast> forecasts = Collections.synchronizedMap(new HashMap<>());
                    for (final Destination dest : recommended) {
                        forecast.resolveTemplate("destination", dest.getDestination()).request()
                                .async()
                                .get(new InvocationCallback<Forecast>() {
                                    @Override
                                    public void completed(final Forecast forecast) {
                                        forecasts.put(dest.getDestination(), forecast);
                                        innerLatch.countDown();
                                    }
     
                                    @Override
                                    public void failed(final Throwable throwable) {
                                        errors.offer("Forecast: " + throwable.getMessage());
                                        innerLatch.countDown();
                                    }
                                });
                    }
     
                    // Have to wait here for dependent requests ...
                    try {
                        if (!innerLatch.await(10, TimeUnit.SECONDS)) {
                            errors.offer("Inner: Waiting for requests to complete has timed out.");
                        }
                    } catch (final InterruptedException e) {
                        errors.offer("Inner: Waiting for requests to complete has been interrupted.");
                    }
     
                    // Continue with processing.
                }
     
                @Override
                public void failed(final Throwable throwable) {
                    errors.offer("Recommended: " + throwable.getMessage());
                }
            }
        );
```

The example is a bit more complicated from the first glance. We provided an `InvocationCallback` to async get method.
One of the callback methods (`completed` or `failed`) is called when the request finishes. This is a pretty convenient
way to handle async invocations when no nested calls are present. Since we have some nested calls (obtaining weather
forecasts) we needed to introduce a
[CountDownLatch](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CountDownLatch.html) synchronization
primitive as we use asynchronous approach in obtaining the weather forecasts as well. The latch is decreased every time
a request, to the Forecasts service, completes successfully or fails. This indicates that the request actually finished
and it is a signal for us that we can continue with processing (otherwise we wouldn't have all required data to
construct the response for the client). This additional synchronization is something that was not present when taking
the synchronous approach, but it is needed here. 

Also the error processing can not be written as it could be in an ideal case. The error handling is scattered in too
many places within the code, that it is quite difficult to create a comprehensive response for the client.

On the other hand taking asynchronous approach leads to code that is as fast as it gets. The resources are used
optimally (no waiting threads) to achieve quick response time. The whole process of constructing the response for the
client can be seen in figure below. It only took 730 ms instead of 5400 ms which we encountered in the previous
approach. 

{% include aligner.html images="pexels/2020-07-28-6-reactive-jax-rs-client-api/rx-client-async-approach.png" column=1 %}

As you can guess, this approach, even with all it's benefits, is the one that is really hard to implement, debug and
maintain. It's a safe bet when you have many independent calls to make but it gets uglier with an increasing number of
nested calls.

### Reactive Approach

Reactive approach is a way out of the so-called **Callback Hell** which you can encounter when dealing with Java's
`Futures` or invocation callbacks. Reactive approach is based on a data-flow concept and the execution model propagate
changes through the flow. An example of a single item in the data-flow chain can be a JAX-RS Client HTTP method call.
When the JAX-RS request finishes then the next item (or the user code) in the data-flow chain is notified about the
continuation, completion or error in the chain. You're more describing what should be done next than how the next action
in the chain should be triggered. The other important part here is that the data-flows are composable. You can
compose/transform multiple flows into the resulting one and apply more operations on the result.

An example of this approach can be seen below. The APIs would be described in more detail in the next sections.

```java
final WebTarget destination = ...;
final WebTarget forecast = ...;
 
// Recommended places.
CompletionStage<List<Destination>> recommended = destination
        .path("recommended")
        .request()
        .header("Rx-User", "CompletionStage") // Identify the user
        .rx() // Reactive invoker
        .get(new GenericType<List<Destination>>() {}) // Return a list of destinations
        .exceptionally(throwable -> {
            errors.offer("Recommended: " + throwable.getMessage());
            return Collections.emptyList();
        });
 
// get Forecast for recommended destinations.
return recommended.thenCompose(destinations -> {
    List<CompletionStage<Recommendation>> recommendations = destinations.stream()
            .map(destination -> {
                // For each destination, obtain a weather forecast ...
                final CompletionStage<Forecast> forecastResult =
                        forecast.resolveTemplate("destination", destination.getDestination())
                                .request()
                                .rx()
                                .get(Forecast.class)
                                .exceptionally(throwable -> {
                                    errors.offer("Forecast: " + throwable.getMessage());
                                    return new Forecast(destination.getDestination(), "N/A");
                                });
             
                                //noinspection unchecked
                                return CompletableFuture
                                        .completedFuture(new Recommendation(destination))
                                        .thenCombine(forecastResult, Recommendation::forecast); // Set forecast for recommended destination
            })
            .collect(Collectors.toList());

    return sequence(recommendations); // Transform List<CompletionStage<Recommendation>> to CompletionStage<List<Recommendation>>
});
```

As you can see the code achieves the same work as the previous two examples. It's more readable than the pure
asynchronous approach even though it's equally fast. It's as easy to read and implement as the synchronous approach.
The error processing is also better handled in this way than in the asynchronous approach.

When dealing with a large amount of requests (that depend on each other) and when you need to compose/combine the
results of these requests, the reactive programming model is the right technique to use.

## Usage and Extension Modules

Reactive Client API is part of the JAX-RS specification since version 2.1.

When you compare synchronous invocation of HTTP calls

```java
Response response = ClientBuilder.newClient()
        .target("http://example.com/resource")
        .request()
        .get();
```

with asynchronous invocation

```java
Future<Response> response = ClientBuilder.newClient()
        .target("http://example.com/resource")
        .request()
        .async()
        .get();
```

it is apparent how to pretty conveniently modify the way how a request is invoked (from sync to async) only by calling
async method on an `Invocation.Builder`.

Naturally, it'd be nice to copy the same pattern to allow invoking requests in a reactive way. Just instead of async
you'd call rx on an extension of `Invocation.Builder`:

```java
CompletionStage<Response> response = ClientBuilder.newClient()
        .target("http://example.com/resource")
        .request()
        .rx()
        .get();
```

The first reactive interface in the invocation chain is `RxInvoker` which is very similar to `SyncInvoker` and
`AsyncInvoker`. It contains all methods present in the two latter JAX-RS interfaces but the `RxInvoker` interface is
more generic, so that it can be extended and used in particular implementations taking advantage of various reactive
libraries. Extending this new interface in a particular implementation also preserves type safety which means that
you're not loosing type information when a HTTP method call returns an object that you want to process further.

The method `rx()` in the example above is perfect example of that principle. It returns `CompletionStageRxInvoker`,
which extends `RxInvoker`.

As a user of the Reactive Client API you only need to keep in mind that you won't be working with `RxInvoker` directly.
You'd rather be working with an extension of this interface created for a particular implementation and you don't need
to be bothered much with why are things designed the way they are.

---
**Note**

To see how the `RxInvoker` should be extended, refer to Section
[Implementing Support for Custom Reactive Libraries (SPI)](#implementing-support-for-custom-reactive-libraries-spi). 

---

The important thing to notice here is that an extension of `RxInvoker` holds the type information and the Reactive
Client needs to know about this type to properly propagate it among the method calls you'll be making. This is the
reason why other interfaces (described bellow) are parametrized with this type. 

In order to extend the API to be used with other reactive frameworks, `RxInvokerProvider` needs to be registered into
the Client runtime:

```java
Client client = ClientBuilder.newClient();
client.register(RxFlowableInvokerProvider.class);
 
Flowable<String> responseFlowable = client
        .target("http://jersey.java.net")
        .request()
        .rx(RxFlowableInvoker.class)
        .get(String.class);
 
String responseString = responseFlowable.blockingFirst();
```

### Dependencies

JAX-RS mandates support for `CompletionStage`, which doesn't required any other dependency and can be used out of the
box.

To add support for a particular library, see the Section [Supported Reactive Libraries](#supported-reactive-libraries).

---
**Note**

If you're not using Maven (or other dependency management tool) make sure to add also all the transitive dependencies of
Jersey client module and any other extensions (when used) on the class-path. 

---

## Supported Reactive Libraries

There are already some available reactive (or reactive-like) libraries out there and Jersey brings support for some of
them out of the box. Jersey currently supports:

* [RxJava (Observable)](#rxjava---observable)
* [RxJava (Flowable)](#rxjava---flowable)

Each of them are described in details in the following sections

### RxJava - Observable

[RxJava](https://github.com/ReactiveX/RxJava), contributed by Netflix, is probably the most advanced reactive library
for Java at the moment. It's used for composing asynchronous and event-based programs by using observable sequences. It
uses the [observer pattern](https://en.wikipedia.org/wiki/Observer_pattern) to support these sequences of data/events
via its [Observable](http://reactivex.io/RxJava/javadoc//rx/Observable.html) entry point class which implements the
Reactive Pattern. `Observable` is actually the parameter type in the RxJava's extension of `RxInvoker`, called
[RxObservableInvoker](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/client/rx/rxjava/RxObservableInvoker.html).
This means that the return type of HTTP method calls is `Observable` in this case (accordingly parametrized).

Requests are by default invoked at the moment when a subscriber is subscribed to an observable (it's a cold
`Observable`). If not said otherwise a separate thread (JAX-RS Async Client requests) is used to obtain data. This
behavior can be overridden by providing an
[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) when a reactive
`Client` is created. 

#### Usage

The extensibility is built-in JAX-RS Client API, so there are no special dependencies on Jersey Client API other than
the extension itself.

```java
// New Client
Client client = ClientBuilder.newClient();
client.register(RxObservableInvokerProvider.class);
```

An example of obtaining `Observable` with JAX-RS `Response` from a remote service can be seen below

```java
Observable<Response> observable = RxObservable
        .newClient()
        .target("http://example.com/resource")
        .request()
        .rx(RxObservableInvoker.class)
        .get();
```

#### Dependencies

The RxJava support is available as an extension module in Jersey. For Maven users, simply add the following dependency
to your `pom.xml`: 

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.rx</groupId>
    <artifactId>jersey-rx-client-rxjava</artifactId>
    <version>2.31</version>
</dependency>
```

After this step you can use the extended client right away. The dependency transitively adds the `io.reactivex:rxjava`
dependency to your class-path as well.

---
**Note**

If you're not using Maven (or other dependency management tool) make sure to add also all the transitive dependencies of
this extension module (see
[jersey-rx-client-rxjava](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/project/jersey-rx-client-rxjava/dependencies.html))
on the class-path. 

---

### RxJava - Flowable

[RxJava](https://github.com/ReactiveX/RxJava), contributed by Netflix, is probably the most advanced reactive library
for Java at the moment. It's used for composing asynchronous and event-based programs by using observable sequences. It
uses the [observer pattern](en.wikipedia.org/wiki/Observer_pattern) to support these sequences of data/events via its
`Flowable` entry point class which implements the Reactive Pattern. `Flowable` is actually the parameter type in the
RxJava's extension of `RxInvoker`, called `RxFlowableInvoker`. This means that the return type of HTTP method calls is
`Flowable` in this case (accordingly parametrized).

Requests are by default invoked at the moment when a subscriber is subscribed to a flowable (it's a cold `Flowable`).
If not said otherwise a separate thread (JAX-RS Async Client requests) is used to obtain data. This behavior can be
overridden by providing an
[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) when a reactive
`Client` is created. 

#### Usage

The extensibility is built-in JAX-RS Client API, so there are no special dependencies on Jersey Client API other than
the extension itself.

```java
// New Client
Client client = ClientBuilder.newClient();
client.register(RxFlowableInvokerProvider.class);
```

An example of obtaining `Flowable` with JAX-RS `Response` from a remote service can be seen below

```java
Flowable<Response> observable = RxObservable
        .newClient()
        .target("http://example.com/resource")
        .request()
        .rx(RxFlowableInvoker.class)
        .get();
```

#### Dependencies

The RxJava support is available as an extension module in Jersey. For Maven users, simply add the following dependency
to your `pom.xml`:

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.rx</groupId>
    <artifactId>jersey-rx-client-rxjava2</artifactId>
    <version>2.31</version>
</dependency>
```

After this step you can use the extended client right away. The dependency transitively adds the `io.reactivex:rxjava2`
dependency to your class-path as well.

---
**Note**

If you're not using Maven (or other dependency management tool) make sure to add also all the transitive dependencies of
this extension module (see [jersey-rx-client-rxjava2](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/project/jersey-rx-client-rxjava2/dependencies.html))
on the class-path. 

---

## Implementing Support for Custom Reactive Libraries (SPI)

In case you want to bring support for some other library providing Reactive Programming Model into your application you
can extend functionality of Reactive JAX-RS Client by implementing `RxInvokerProvider`, registering that implementation
into the client runtime and then using `rx(Class<T>)` in your code.

### Implement `RxInvoker` and `RxInvokerProvider` interfaces

The first step when implementing support for another reactive library is to implement `RxInvoker`. JAX-RS API itself
contains one implementation, which will be used as an example: `CompletionStageRxInvoker`.

```java
public interface CompletionStageRxInvoker extends RxInvoker<CompletionStage> {

    @Override
    public CompletionStage<Response> get();
 
    @Override
    public <T> CompletionStage<T> get(Class<T> responseType);
 
    // ...
}
``` 

The important fact to notice is that the generic parameter of `RxInvoker` is `CompletionStage` and also that the return
type is overriden to be always `CompletionStage` with some generic param (`Response`; or `T`).

After having the extended `RxInvoker` interface, the implementor has to provide `RxInvokerProvider`, which will be
registered as an provider to a client instance.

```java
public static class CompletionStageRxInvokerProvider implements RxInvokerProvider<CompletionStageRxInvoker> {

    @Override
    public boolean isProviderFor(Class<?%gt; clazz) {
        return CompletionStage.class.equals(clazz);
    }
 
    @Override
    public CompletionStageRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
        return new CompletionStageRxInvoker() {
            // ...
        };
    }
}
```

### Example of using custom RxInvokerProvider

Considering the work above was done and the implementation of custom `RxInvoker` and `RxInvokerProvider` is available,
the client code using those extensions will be:

```java
Client client = ClientBuilder.newClient();
client.register(CompletionStageRxInvokerProvider.class); // register custom RxInvokerProvider
 
CompletionStage<Response> response = client
        .target("http://jersey.java.net")
        .request()
        .rx(CompletionStageRxInvoker.class)
        // Now we have an instance of CompletionStageRxInvoker returned from our registered RxInvokerProvider,
        // which is CompletionStageRxInvokerProvider in this particular scenario.
        .get();
```
