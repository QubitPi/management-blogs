---
layout: post
title: OpenStack Reference Guide (Updated Daily...)
tags: [OpenStack]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/20-cover.png"
thumbnail: "assets/img/post-cover/20-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

The OpenStack project is an open source cloud computing platform for all types of clouds, which aims to be simple to
implement, massively scalable, and feature rich. Developers and cloud computing technologists from around the world
create the OpenStack project.

OpenStack provides an Infrastructure-as-a-Service (IaaS) solution through
[a set of interrelated services](https://www.openstack.org/software/project-navigator/openstack-components). Each
service offers an Application Programming Interface (API) that facilitates this integration. Depending on your needs, you can
install some or all services.

> **Infrastructure-as-a-Service (IaaS)**
> 
> IaaS is a provisioning model in which an organization outsources physical components of a data center, such as
> storage, hardware, servers, and networking components. A service provider owns the equipment and is responsible for
> housing, operating and maintaining it. The client typically pays on a per-use basis. IaaS is a model for providing
> cloud services.

## The OpenStack Architecture

### Conceptual Architecture

The following diagram shows the relationships among the OpenStack services:

![openstack-conceptual-architecture.png not loaded property]({{ "/assets/img/openstack-conceptual-architecture.png" | relative_url}})

### Logical architecture

To design, deploy, and configure OpenStack, administrators must understand the logical architecture.

OpenStack consists of several independent parts, named the **OpenStack services**. All services authenticate through a
common **Identity service**. Individual services interact with each other through public APIs, except where privileged
administrator commands are necessary.

Internally, OpenStack services are composed of several processes. All services have at least one API process, which
listens for API requests, preprocesses them and passes them on to other parts of the service. _With the exception of the
Identity service_, the actual work is done by distinct processes.

For communication between the processes of one service, an
[AMQP](https://en.wikipedia.org/wiki/Advanced_Message_Queuing_Protocol/) message broker is used. The service's state is
tored in a database. When deploying and configuring your OpenStack cloud, you can choose among several message broker
and database solutions, such as RabbitMQ, MySQL, MariaDB, and SQLite.

Users can access OpenStack via the web-based user interface implemented by the Horizon Dashboard, via command-line
clients and by issuing API requests through tools like browser plug-ins or curl. For applications, several SDKs are
available. Ultimately, all these access methods issue REST API calls to the various OpenStack services.

The following diagram shows the most common, but not the only possible, architecture for an OpenStack cloud:

![openstack-logical-architecture.png not loaded property]({{ "/assets/img/openstack-logical-architecture.png" | relative_url}})

## Use Cases (Learn from Others)

* [How Yahoo! Uses Neutron for Ironic](https://www.openstack.org/videos/summits/tokio-2015/how-yahoo-uses-neutron-for-ironic)
* [Yahoo Engineering - Operating OpenStack at Scale](https://yahooeng.tumblr.com/post/159795571841/operating-openstack-at-scale)
