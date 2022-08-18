---
layout: post
title: PUT v.s. POST
tags: [HTTP, Webservice]
category: FINALIZED
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/26-cover.png"
thumbnail: "assets/img/post-cover/26-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

We should choose between PUT and POST based on [idempotence](http://en.wikipedia.org/wiki/Idempotent) of the action.

**PUT** implies putting a resource - completely replacing whatever is available at the given URL with a different thing.
By definition, a PUT is idempotent. Do it as many times as you like, and the result is the same. `x=5` is idempotent.
You can PUT a resource whether it previously exists, or not (eg, to Create, or to Update)!

**POST updates a resource, adds a subsidiary resource, or causes a change. A POST is not idempotent, in the way that
`x++` is not idempotent.

By this argument, PUT is for creating when you know the URL of the thing you will create. POST can be used to create
when you know the URL of the "factory" or manager for the category of things you want to create.

so:

    POST /expense-report

or:

    PUT /expense-report/10929
