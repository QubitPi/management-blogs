---
layout: post
title: Elasticsearch
tags: [Elasticsearch]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Java API

The Java REST Client comes in 2 flavors:

1. [Java Low Level REST Client](#java-low-level-rest-client): the official low-level client for Elasticsearch. It allows
   to communicate with an Elasticsearch cluster through http. Leaves requests marshalling and responses un-marshalling
   to users. It is compatible with all Elasticsearch versions.
2. [Java High Level REST Client](#java-high-level-rest-client): the official high-level client for Elasticsearch. Based
   on the low-level client, it exposes API specific methods and takes care of requests marshalling and responses
   un-marshalling.

### Java Low Level REST Client

The low-level client's features include:

* minimal dependencies
* load balancing across all available nodes
* failover in case of node failures and upon specific response codes
* failed connection penalization (whether a failed node is retried depends on how many consecutive times it failed; the
  more failed attempts the longer the client will wait before trying that same node again)
* persistent connections
* trace logging of requests and responses
* optional automatic discovery of cluster nodes

> The low-level Java REST client internally uses the [Apache Http Async Client](https://hc.apache.org) to send http
> requests.

#### Initialization

A `RestClient` instance can be built through the corresponding `RestClientBuilder` . The only required argument is one
or more hosts that the client will communicate with, provided as instances of [`HttpHost`](https://hc.apache.org/) as
follows:

```java
RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"),
        new HttpHost("localhost", 9201, "http")
).build();
```

The `RestClient` class **is thread-safe** and **ideally** has the same lifecycle as the application that uses it. It is
important that it gets closed when no longer needed so that all the resources used by it get properly released, as well
as the underlying http client instance and its threads:

```java
restClient.close();
```

`RestClientBuilder` also allows to optionally set the following configuration parameters while building the `RestClient`
instance:

```java
RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "http")
);

// Set the default headers that need to be sent with each request, to prevent having to specify them with each single
// request
Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
builder.setDefaultHeaders(defaultHeaders);

// Set a listener that gets notified every time a node fails, in case actions need to be taken. Used internally when
// sniffing on failure is enabled.
builder.setFailureListener(
        new RestClient.FailureListener() {
            
            @Override
            public void onFailure(Node node) {
                // ...
            }
        }
);

// Set the node selector to be used to filter the nodes the client will send requests to among the ones that are set to
// the client itself. This is useful for instance to prevent sending requests to dedicated master nodes when sniffing is
// enabled. By default the client sends requests to every configured node.
builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);

// Set a callback that allows to modify the default request configuration (e.g. request timeouts, authentication, etc)
builder.setRequestConfigCallback(
        new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setSocketTimeout(10000);
            } 
        }
);

// Set a callback that allows to modify the http client configuration (e.g. encrypted communication over ssl)
builder.setHttpClientConfigCallback(
        new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder
            ) {
                return httpClientBuilder.setProxy(
                        new HttpHost("proxy", 9000, "http"));
            }
        }
);
```

#### Sending Request

Once the `RestClient` has been created, requests can be sent by calling

* `performRequest`, which is **synchronous** and will **block** the calling thread; it returns when the request is
  successful or throw an exception if it fails. For example
  
  ```java
  Request request = new Request("GET", "/");   
  Response response = restClient.performRequest(request);
  ```
  
* `performRequestAsync`, which is asynchronous and accepts a listener gets called when the request is successful or
  throws an Exception if it fails. For instance
  
  ```java
  Request request = new Request("GET", "/");
  Cancellable cancellable = restClient.performRequestAsync(
      request,
      new ResponseListener() {
  
          @Override
          public void onSuccess(Response response) {
              ...
          }
  
          @Override
          public void onFailure(Exception exception) {
              ...
          }
  });
  ```

You can add request parameters to request object:

```java
request.addParameter("pretty", "true");
```

You can set the body of request using `HttpEntity`:

```java
request.setEntity(
        new NStringEntity(
                "{\"json\":\"text\"}",
                ContentType.APPLICATION_JSON
        )
);
```

> ⚠️ The `ContentType` specified in the `HttpEntity` is important because it will be used to set the `Content-Type`
> header so that Elasticsearch can properly parse the content.

You can also set it to a string which will default to a `ContentType` of "application/json":

```
request.setJsonEntity("{\"json\":\"text\"}");
```

##### Request Options

The `RequestOptions` class holds parts of the request that should be shared between many requests in the same
application. You can make a singleton instance and share it between all requests:

```java
private static final RequestOptions COMMON_OPTIONS;

static {
    RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
    builder.addHeader("Authorization", "Bearer " + TOKEN);
    builder.setHttpAsyncResponseConsumerFactory(
            new HttpAsyncResponseConsumerFactory
                    .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)
    );
    COMMON_OPTIONS = builder.build();
}
```

Note that there is no need to set the "Content-Type" header because the client will automatically set that from the
`HttpEntity` attached to the request.

You can set the `NodeSelector` which controls which nodes will receive requests. `NodeSelector.SKIP_DEDICATED_MASTERS`
is a good choice.

You can also customize the response consumer used to buffer the asynchronous responses. The default consumer will buffer
up to 100MB of response on the JVM heap. If the response is larger then the request will fail. You could, for example,
lower the maximum size which might be useful if you are running in a heap constrained environment like the example
above.

Once you’ve created the singleton you can use it when making requests:

```java
request.setOptions(COMMON_OPTIONS);
```

You can also customize these options on a per request basis. For example, this adds an extra header:

```java
RequestOptions.Builder options = COMMON_OPTIONS.toBuilder();
options.addHeader("cats", "knock things off of other things");
request.setOptions(options);
```

##### Multiple Parallel Asynchronous Actions

The client is quite happy to execute many actions in parallel. The following example indexes many documents in parallel.
In a real world scenario you'd probably want to use the `_bulk` API instead, but the example is illustrative.

```java
final CountDownLatch latch = new CountDownLatch(documents.length);
for (int i = 0; i < documents.length; i++) {
    Request request = new Request("PUT", "/posts/doc/" + i);
    //let's assume that the documents are stored in an HttpEntity array
    request.setEntity(documents[i]);
    restClient.performRequestAsync(
            request,
            new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    latch.countDown();
                }

                @Override
                public void onFailure(Exception exception) {
                    latch.countDown();
                }
            }
    );
}
latch.await();
```

##### Cancelling Asynchronous Requests

The `performRequestAsync` method returns a `Cancellable` that exposes a single public method called `cancel`. Such
method can be called to cancel the on-going request. Cancelling a request will result in aborting the http request
through the underlying http client. On the server side, **this does not automatically translate to the execution of that
request being cancelled, which needs to be specifically implemented in the API itself**.

The use of the `Cancellable` instance is optional and you can safely ignore this if you don't need it. A typical usecase
for this would be using this together with frameworks like Rx Java. Cancelling no longer needed requests is a good way
to avoid putting unnecessary load on Elasticsearch.

```java
Request request = new Request("GET", "/posts/_search");
Cancellable cancellable = restClient.performRequestAsync(
    request,
    new ResponseListener() {
        @Override
        public void onSuccess(Response response) {
            ...
        }

        @Override
        public void onFailure(Exception exception) {
            ...
        }
    }
);
cancellable.cancel();
```

#### Reading Responses

The `Response` object, either returned by the synchronous `performRequest` methods or received as an argument in
`ResponseListener#onSuccess(Response)`, wraps the response object returned by the http client and exposes some
additional information.

```java
Response response = restClient.performRequest(new Request("GET", "/"));
RequestLine requestLine = response.getRequestLine(); // Information about the performed request
HttpHost host = response.getHost();
int statusCode = response.getStatusLine().getStatusCode();
Header[] headers = response.getHeaders();
String responseBody = EntityUtils.toString(response.getEntity());
```

### Java High Level REST Client

The Java High Level REST Client works on top of the Java Low Level REST client. Its main goal is to expose API specific
methods, that **accept request objects as an argument and return response objects (type-safe)**

Each API can be called synchronously or asynchronously. The synchronous methods return a response object, while the
asynchronous methods, whose names end with the `async` suffix, require a listener argument that is notified (on the
thread pool managed by the low level client) once a response or an error is received.

#### Example

```java
try (XContentParser parser = XContentFactory
        .xContent(XContentType.JSON)
        .createParser(
                new NamedXContentRegistry(
                        new SearchModule(
                                Settings.EMPTY,
                                false,
                                Collections.emptyList()
                        ).getNamedXContents()
                ),
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                query
        )
) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.parseXContent(parser);
    
    getEsRestClient()
            .getRestHighLevelClient(storeURL)
            .search(
                    new SearchRequest(index).source(searchSourceBuilder),
                    RequestOptions.DEFAULT
            );
} catch (IOException exception) {
    String message = String.format("Error on quering ES: %s", exception.getMessage());
    LOG.error(message, exception);
    throw new IllegalStateException(message, exception);
}
```

## REST API

#### Search

Returns search hits that match the query defined in the request.

##### Get All Data by Index

```
GET /my-index-000001/_search
```

#### Get Data by Query

Querying

```
POST /my-index-000001/_search?from=40&size=20
{
    "query": {
        "term": {
            "user.id": "kimchy"
        }
    }
}
```

Although Elasticsearch API also supports attaching query string as request body in GET, it is, however,
[not recommended](https://stackoverflow.com/questions/978061/http-get-with-request-body#comment53906725_983458)

> ⚠️ **Pay special attention to the `from` and `size` parameters**.
> [**By default, searches return the top 10 matching hits**](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#paginate-search-results).
> **Use `from` and `size` in order to page through a larger set of result**. We could also attach them in the JSON query
> 
> ```
> POST /my-index-000001/_search
> {
>     "from": 40,
>     "size": 20,
>     "query": {
>         "term": {
>             "user.id": "kimchy"
>         }
>     }
> }
```

##### Get All Data

##### Query Parameters

###### from

(Optional, integer) Starting document offset. Defaults to 0.

By default, you cannot page through more than 10,000 hits using the `from` and [`size`](#size) parameters. To page
through more hits, use the
[search_after](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#search-after)
parameter.

###### size

(Optional, integer) Defines the number of hits to return. Defaults to **10**.

By default, you cannot page through more than 10,000 hits using the [`from`](#from) and `size` parameters. To page
through more hits, use the
[search_after](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#search-after)
parameter.