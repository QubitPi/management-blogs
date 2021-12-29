---
layout: post
title: HttpURLConnection User Guide
tags: [Java, HTTP, HTTPS]
color: rgb(237, 28, 36)
feature-img: "assets/img/post-cover/21-cover.png"
thumbnail: "assets/img/post-cover/21-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Why Would I Use a Low Level API like HttpURLConnection

HttpURLConnection gives you the comprehensive control over your HTTP/HTTPS communication. For example (**and this is the
reason I hate f**king Spring so much), you will realize that Spring Boot `RestTempate` actually secretly overwrites your
HTTP headers in the back, which might leave you 2 hours of stupid debugging session and make you look like a nuts, which
results in  this post.

The `HttpUrlConnection` class allows us to perform basic HTTP requests without the use of any additional libraries. All
the classes that we need are part of the "java.net" package.

The disadvantages of using this method are that the code can be more cumbersome than other HTTP libraries and that it
does not provide more advanced functionalities such as dedicated methods for adding headers or authentication.

## Creating A Request

We can create an `HttpUrlConnection` instance using the **`openConnection()`** method of the URL class. Note that this
method **only creates a connection object but doesn't establish the connection yet**.

The `HttpUrlConnection` class is used for all types of requests by setting the requestMethod attribute to one of the
values: `GET`, `POST`, `HEAD`, `OPTIONS`, `PUT`, `DELETE`, `TRACE`.

```java
URL url = new URL("http://example.com");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
```

## Adding Request Parameters

If we want to add parameters to a request, we have to **set the `doOutput` property to `true`, then write a string of
the form `param1=value&paramm2=value` to the `OutputStream` of the `HttpUrlConnection` instance:

```java
Map<String, String> parameters = new HashMap<>();
parameters.put("param1", "val");

con.setDoOutput(true);
DataOutputStream out = new DataOutputStream(con.getOutputStream());
out.writeBytes("Content-Disposition: form-data; name=\"" + "parameters" + "\"");
...
out.flush();
out.close();
```

## Setting Request Headers

Adding headers to a request can be achieved by using the `setRequestProperty()` method:

```java
con.setRequestProperty("Content-Type", "application/json");
```

To read the value of a header from a connection, we can use the `getHeaderField()` method:

```java
String contentType = con.getHeaderField("Content-Type");
```

## Reading the Response

Reading the response of the request can be done by parsing the `InputStream` of the `HttpUrlConnection` instance.

**To execute the request, we can use the `getResponseCode()`, `connect()`, `getInputStream()` or `getOutputStream()`
methods:

```java
int status = con.getResponseCode();
```

Finally, let's read the response of the request and place it in a content String:

```java
BufferedReader in = new BufferedReader(
  new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer content = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    content.append(inputLine);
}
in.close();
```

To close the connection, we can use the disconnect() method:

```java
con.disconnect();
```
