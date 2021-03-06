---
layout: post
title: Inject Request Headers
tags: [Spring Boot, Spring]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Inject Headers Info

### Accessing HTTP Headers

#### Individual Headers

If we need access to a specific header, we can configure `@RequestHeader` with the header name:

```java
@GetMapping("/greeting")
public ResponseEntity<String> greeting(@RequestHeader("accept-language") String language) {
    ...
}
```

Then, we can access the value using the variable passed into our method. If a header named "accept-language" isn't found
in the request, the method returns a "400 Bad Request" error.

Our headers don't have to be strings. For example, if we know our header is a number, we can declare our variable as a
numeric type:

```java
@GetMapping("/double")
public ResponseEntity<String> doubleNumber(@RequestHeader("my-number") int myNumber) {
    return new ResponseEntity<String>(String.format("%d * 2 = %d", myNumber, (myNumber * 2)), HttpStatus.OK);
}
```

#### All Headers

If we're not sure which headers will be present, or we need more of them than we want in our method's signature, we can
use the `@RequestHeader` annotation without a specific name.

We have a few choices for our variable type:

* `Map`
* `MultiValueMap`
* `HttpHeaders`

First, let's get the request headers as a Map:

```java
@GetMapping("/listHeaders")
public ResponseEntity<String> listAllHeaders(@RequestHeader Map<String, String> headers) {
    ...
}
```

If we use a `Map` and one of the headers has more than one value, we'll get only the first value. This is the equivalent
of using the `getFirst` method on a `MultiValueMap`.

If our headers may have multiple values, we can get them as a `MultiValueMap`:

```java
@GetMapping("/multiValue")
public ResponseEntity<String> multiValue(
  @RequestHeader MultiValueMap<String, String> headers) {
    headers.forEach((key, value) -> {
        LOG.info(String.format(
          "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
    });
        
    return new ResponseEntity<String>(
      String.format("Listed %d headers", headers.size()), HttpStatus.OK);
}
```