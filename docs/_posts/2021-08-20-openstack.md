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

Performance is a critical consideration when designing any cloud, and becomes increasingly important as size and
complexity grow. While single-site, private clouds can be closely controlled, multi-site and hybrid deployments require
more careful planning to reduce problems such as

**For example, you should consider the time required to run a workload in different clouds and methods for reducing this
time. This may require moving data closer to applications or applications closer to the data they process, and grouping
functionality so that connections that require low latency take place over a single cloud rather than spanning clouds.**

This may also require a CMP (Cloud Management Platform) that can determine which cloud can most efficiently run which
types of workloads.

Using native OpenStack tools can help improve performance. For example, you can use Telemetry to measure performance and
the Orchestration service (heat) to react to changes in demand.

> 📋 Orchestration requires special client configurations to integrate with Amazon Web Services. For other types of
> clouds, use CMP features.

###### Cloud resource deployment

**The cloud user expects repeatable, dependable, and deterministic processes for launching and deploying cloud
resources**. You could deliver this through a web-based interface or publicly available API endpoints. All appropriate
options for requesting cloud resources must be available through some type of user interface, a command-line interface
(CLI), or API endpoints.

###### Consumption model

**Cloud users expect a fully self-service and on-demand consumption model**. When an OpenStack cloud reaches the
massively scalable size, expect consumption as a service in each and every way.

* **Everything must be capable of automation**. For example, everything from compute hardware, storage hardware,
  networking hardware, to the installation and configuration of the supporting software. Manual processes are
  impractical in a massively scalable OpenStack design architecture
* **Massively scalable OpenStack clouds require extensive metering and monitoring functionality to maximize the
  operational efficiency by keeping the operator informed about the status and state of the infrastructure. This
  includes full scale metering of the hardware and software status. A corresponding framework of logging and alerting is
  also required to store and enable operations to act on the meters provided by the metering and monitoring solutions.
  The cloud operator also needs a solution that uses the data provided by the metering and monitoring solution to
  provide capacity planning and capacity trending analysis**.

###### Location

For many use cases **the proximity of the user to their workloads has a direct influence on the performance of the
application and therefore should be taken into consideration in the design**. Certain applications require zero to
minimal latency that can only be achieved by deploying the cloud in multiple locations. These locations could be in
different data centers, cities, countries or geographical regions, depending on the user requirement and location of the
users.

###### Input-Output requirements

Input-Output performance requirements require researching and modeling before deciding on a final storage framework.
Running benchmarks for Input-Output performance provides a baseline for expected performance levels. If these tests
include details, then the resulting data can help model behavior and results during different workloads. Running
scripted smaller benchmarks during the lifecycle of the architecture helps record the system health at different points
in time. The data from these scripted benchmarks assist in future scoping and gaining a deeper understanding of an
organization's needs.

###### Scale

Scaling storage solutions in a storage-focused OpenStack architecture design is driven by initial requirements,
including IOPS, capacity, bandwidth, and future needs. Planning capacity based on projected needs over the course of a
budget cycle is important for a design. The architecture should balance cost and capacity, while also allowing
flexibility to implement new technologies and methods as they become available.

> **Input/Output Operations Per Second (IOPS)**
> 
> IOPS are a common performance measurement used to benchmark computer storage devices like hard disk drives, solid
> state drives, and storage area networks.

**To be continued...**

## Use Cases (Learn from Others)

* [How Yahoo! Uses Neutron for Ironic](https://www.openstack.org/videos/summits/tokio-2015/how-yahoo-uses-neutron-for-ironic)
* [Yahoo Engineering - Operating OpenStack at Scale](https://yahooeng.tumblr.com/post/159795571841/operating-openstack-at-scale)
