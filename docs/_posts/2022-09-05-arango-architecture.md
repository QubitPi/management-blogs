---
layout: post
title: ArangoDB Architecture
tags: [ArangoDB, Database, Knowledge Graph, Architecture]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/11-cover.png"
thumbnail: "assets/img/post-cover/11-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Deployment Modes
----------------

### Single Instance

Running a single instance of ArangoDB is the most simple way to get started. It means to run the ArangoDB Server binary 
`arangod` stand-alone, without replication, without failover opportunity and not as cluster together with other nodes.

The provided ArangoDB packages run as [single instances out of the box](https://www.arangodb.com/docs/stable/installation.html).

#### Docker

* https://hub.docker.com/_/arangodb
* [Passwordless login - `ARANGO_NO_AUTH=1`](https://www.arangodb.com/docs/stable/deployment-single-instance-manual-start.html#authentication)
* [Run container](https://www.arangodb.com/download-major/docker/):

  docker run -p 8529:8529 -e ARANGO_ROOT_PASSWORD=openSesame arangodb/arangodb:3.8.1

### Active Failover

WIP...
