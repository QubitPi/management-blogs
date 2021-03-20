---
layout: post
title: Modeling Business Data with An Unstructured Approach
tags: [Java, Big Data]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/christmas-fair-3.png"
thumbnail: "assets/img/pexels/design-art/christmas-fair-3.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

People know that Big Data comes with 3V, which is Volume, Velocity, and Variety. What **Variety** means is that data
comes from many data sources. An enterprise data pipeline could source data from structured sources, such as MySQL and
Oracle, or unstructured sources, which includes for example HDFS or MongoDB.

It is great when a platform has a large amount of data to play with. The challenge, however, is how we manage those data
efficiently.

## An Example Business Scenario

Let's say you are developing a platform that captures and analyzes people's hobbies. The platform serves 2 features:

1. Allow users to browse hobby information
2. A downstream team will analyze the data in order to extract insights on how people manage their lives.

You are targeting at very few specific hobby categories: 

1. Traveling
2. KPOP Choreography
3. Entrepreneurship

You decided you crawl data from different websites, such as Yelp, Pinterests, and Expedia.com, and store the in a
database

## Structured Approach

One of the structured approach is the have a
[fully-denormalized](https://qubitpi.github.io/jersey-guide/2020/09/06/mysql-polymorphism.html#concrete-table-inheritance) database schema to store all 3 kinds of entity data. In
this approach, data is "typed".

There will be 3 tables each of which is created for the 3 hobbies defined above. The table schema will be different.

### Challenges 

With this approach, developers have to deal with 3 types of complexities:

1. **Interpreting incoming data before persisting it**
2. **Maintaining database schema**
3. **Interpreting data on after fetching it from database**

Interpreting the incoming data includes deserializing it to a correct **type** in order to match which table it should
persist to.

## Unstructured Approach

A database storing those data often serves 2 purposes:

1. As a source of data retrieval need from customer
2. Data store for downstream machine manipulation

One the customer side (or business side), people usually requires little modification on the provided data,
this makes the data interpretation on the ingestion phase unnecessary. For example, the data is best served if it is
unchanged or part of it is served unchanged (this is probably another reason why [GraphQL](https://graphql.org/) is
becoming a mainstream API choice) 

We will keep the incoming data uninterpreted and store them as "[document](https://en.wikipedia.org/wiki/Document-oriented_database)"
This requires no interpretation on the incoming data. In this approach, data is "typeless".

Using unstructured approach, we only need to deal with one type of complexity, which is the _interpretatio of the data
on use_. **The idea is to keep delaying the interpretation of the data until necessary**. A lot of type checking can be
avoided when things are pushed down
