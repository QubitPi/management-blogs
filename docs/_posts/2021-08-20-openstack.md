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

### Architecture Requirements

#### Enterprise Requirements

The following sections describe business, usage, and performance considerations for customers which will impact cloud
architecture design.

##### Cost

Financial factors are a primary concern for any organization. Cost considerations may influence the type of cloud that
you build. For example, a general purpose cloud is unlikely to be the most cost-effective environment for specialized
applications. Unless business needs dictate that cost is a critical factor, cost should not be the sole consideration
when choosing or designing a cloud.

Consider the following costs categories when designing a cloud:

* Compute resources
* Networking resources
* Replication
* Storage
* Management
* Operational costs

It is also important to consider how costs will increase as your cloud scales. Choices that have a negligible impact in
small systems may considerably increase costs in large systems. In these cases, it is important to minimize capital
expenditure (CapEx) at all layers of the stack.

##### Time-to-market

The ability to deliver services or products within a flexible time frame is a common business factor when building a
cloud. Allowing users to self-provision and gain access to compute, network, and storage resources on-demand may
decrease time-to-market for new products and applications.

You must balance the time required to build a new cloud platform against the time saved by migrating users away from
legacy platforms. In some cases, existing infrastructure may influence your architecture choices. For example, using
multiple cloud platforms may be a good option when there is an existing investment in several applications, as it could
be faster to tie the investments together rather than migrating the components and refactoring them to a single
platform.

##### Revenue Opportunity

Revenue opportunities vary based on the intent and use case of the cloud. The requirements of a commercial,
customer-facing product are often very different from an internal, private cloud. You must consider what features make
your design most attractive to your users.

##### Capacity Planning and Scalability

Capacity and the placement of workloads are key design considerations for clouds. A long-term capacity plan for these
designs must incorporate growth over time to prevent permanent consumption of more expensive external clouds. To avoid
this scenario, account for **future** applications' capacity requirements and plan growth appropriately.

It is difficult to predict the amount of load a particular application might incur if the number of users fluctuates,
or the application experiences an unexpected increase in use. It is possible to define application requirements in terms
of vCPU, RAM, bandwidth, or other resources and plan appropriately. However, other clouds might not use the same meter
or even the same oversubscription rates.

Oversubscription is a method to emulate more capacity than may physically be present. For example, a physical hypervisor
node with 32 GB RAM may host 24 instances, each provisioned with 2 GB RAM. As long as all 24 instances do not
concurrently use 2 full gigabytes, this arrangement works well. However, some hosts take oversubscription to extremes
and, as a result, performance can be inconsistent. If at all possible, determine what the oversubscription rates of each
host are and plan capacity accordingly.

##### Performance

Performance is a critical consideration when designing any cloud, and becomes increasingly important as size and complexity grow. While single-site, private clouds can be closely controlled, multi-site and hybrid deployments require more careful planning to reduce problems such as network latency between sites.

**To be continued...**

## Use Cases (Learn from Others)

* [How Yahoo! Uses Neutron for Ironic](https://www.openstack.org/videos/summits/tokio-2015/how-yahoo-uses-neutron-for-ironic)
* [Yahoo Engineering - Operating OpenStack at Scale](https://yahooeng.tumblr.com/post/159795571841/operating-openstack-at-scale)
