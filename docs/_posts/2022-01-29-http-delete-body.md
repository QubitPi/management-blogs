---
layout: post
title: Is an Entity Body Allowed for an HTTP DELETE Request?
tags: [HTTP, HTTPS]
category: FINALIZED
color: rgb(0, 196, 0)
feature-img: "assets/img/post-cover/28-cover.png"
thumbnail: "assets/img/post-cover/28-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

The latest update to the HTTP 1.1 specification ([RFC 7231](https://www.rfc-editor.org/rfc/rfc7231#section-4.3.5))
explicitly permits an entity-body in a DELETE request:

> A payload within a DELETE request message has no defined semantics; sending a payload body on a DELETE request might
> cause some existing implementations to reject the request.

**With that being said, request parameters should be sent as path parameter**
