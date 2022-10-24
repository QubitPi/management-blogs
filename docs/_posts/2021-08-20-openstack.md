---
layout: post
title: OpenStack Basics
tags: [OpenStack, LXC, LXD, Virtualization, Linux, Container]
category: FINALIZED
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

Hypervisor Baiscs
-----------------

* [QEMU](https://www.qemu.org/)
* [KVM](https://www.linux-kvm.org/page/Main_Page)
* [LXC](https://qubitpi.github.io/jersey-guide/2020/08/25/34-lxc.html)
* [LXD](https://qubitpi.github.io/jersey-guide/2020/08/24/33-lxd.html)

### Linux Containers (LXC)

[Linux Containers(LXC)](https://linuxcontainers.org/lxc/introduction/) are lightweight virtualization technology and
provide a free software virtualization system for computers running GNU/Linux. This is accomplished through kernel level
isolation, it allows one to run multiple virtual units(containers) simultaneously on the same host.

A container is a way to isolate a group of processes from the others on a running Linux system. By making use of
existing functionality like the Linux kernel's new resource management and resource isolation features(Cgroups and name
spaces), these processes can have their own private view of the operating system with its own process ID(PID) space,
file system structure and network interfaces.

Containers share the same kernel with anything else that is running on it, but can be constrained to only use a defined
amount of resources such as CPU, memory or I/O. By combining containers with other features like the Btrfs file system,
it will be possible to quickly set up multiple lightweight isolated Linux instances on a single host. Therefore
containers are better compared to Solaris zones or BSD jails.

Other benefits of containers are

* **Lightweight and resource-friendly** - Enables running multiple instances of an operating system or application on a
  single host, without inducing overhead on CPU and memory. This saves both rack space and power.
* **Comprehensive process and resource isolation** - Safely and securely run multiple applications on a single system
  without the risk of them interfering with each other. If security of one container has been compromised, the other
  containers are unaffected.
* **Run multiple distributions on a single server** - For example, we can run CentOS and Arch on Ubuntu host.
* **Rapid and Easy deployment** - Containers can be useful to quickly set up a "sandbox" environment, e.g. to test a new
  version of a Linux distribution or to simulate a "clean" environment for testing/QA purposes. When using the Btrfs
  file system for a container repository, new instances can be cloned and spawned in seconds.

#### Install LXC on Ubuntu

    sudo apt-get update
    sudo apt-get install lxc

#### Use LXC

**Create an Ubuntu Container**

    sudo lxc-create -t ubuntu -n <containerName>

**Start the Container**

    sudo lxc-start -n <containerName> -d
    sudo lxc-console -n <containerName>

This will default to using the same version and architecture as your machine, additional options are obviously available
(`-help` will list them). Login/Password are `ubuntu`/`ubuntu`.

**Stop the Container**

Once you are done, hit `Ctrl+a` then `q` to exit container console and stop the container using

    sudo lxc-stop -n <containerName>

**Get Info about a Container**

In some cases when you need to know IP address of a container, you can do

    sudo lxc-info -n <containerName>

which will print various information about a container, including its IP address.

> 📁 For More About LXC
> * [Debian](https://wiki.debian.org/LXC)
> * [Oracle](https://www.oracle.com/linux/technologies/oracle-linux-containers.html)

#### LXC Web Panel

For Newbie I would recommend to use ***[LXC Webpanel](https://lxc-webpanel.github.io/install.html)***, The good part is
that if you make a container through cli mode, It will show up in LXC Web Panel

    sudo apt-get install lxc debootstrap bridge-utils -y
    sudo su
    wget https://lxc-webpanel.github.com/tools/install.sh -O - | bash

Open Web Browser and Connect `http://your_ip_address:5000/`. Login with user **admin** and password **admin**

![lxc-web-panel-1]({{ "/assets/img/lxc-web-panel-1.png" | relative_url}})
![lxc-web-panel-2]({{ "/assets/img/lxc-web-panel-2.png" | relative_url}})

**LXC Network**

![lxc-web-panel-3]({{ "/assets/img/lxc-web-panel-3.png" | relative_url}})

**Container Settings**

![lxc-web-panel-4]({{ "/assets/img/lxc-web-panel-4.png" | relative_url}})

**Resource Limitation**

![lxc-web-panel-5]({{ "/assets/img/lxc-web-panel-5.png" | relative_url}})

**User Modification (Create, Delete Modify)**

![lxc-web-panel-6]({{ "/assets/img/lxc-web-panel-6.png" | relative_url}})
![lxc-web-panel-7]({{ "/assets/img/lxc-web-panel-7.png" | relative_url}})

### LXD

#### Install LXD

    sudo apt-add-repository ppa:ubuntu-lxc/stable
    sudo apt-get update
    sudo apt-get dist-upgrade
    sudo apt-get install lxd
    newgrp lxd

#### Storage backends

LXD supports multiple kinds of storage backends. But it doesn't support moving existing containers or images between
different storage backends.

One storage backend is ZFS which supports all the features LXD needs to offer the fastest and most reliable container
experience. This includes per-container disk quotas, immediate snapshot/restore, optimized migration(send/receive) and
instant container creation from an image.

To use ZFS as storage backend, install it using

    sudo apt-add-repository ppa:zfs-native/stable
    sudo apt-get update
    sudo apt-get install ubuntu-zfs

Next, configure LXD daemon to use ZFS

    sudo lxd init

The command above iteractively setup LXD and ZFS. In the example below, I'm simply using a sparse, loopback file for the
ZFS pool. Note that production servers might require different configurations.

    Name of the storage backend to use (dir or zfs): zfs
    Create a new ZFS pool (yes/no)? yes
    Name of the new ZFS pool: lxd
    Would you like to use an existing block device (yes/no)? no
    Size in GB of the new loop device (1GB minimum): 2
    Would you like LXD to be available over the network (yes/no)? no
    LXD has been successfully configured.

Now we can check the ZFS pool

    sudo zpool list
    sudo zpool status

#### Managing Containers

**Create Container**

LXD command line client comes with some official Ubuntu image sources. You can list them using this command

    lxc image list ubuntu:

To get the latest, tested, stable image of Ubuntu 14.04 LTS, for example, and name it

    lxc launch ubuntu:14.04 containerName

If you don't specify it, container will be assigned a random name. If you want a specific architecture, say a 32bit
Intel image, you can build a container with that image with

    lxc launch ubuntu:14.04/i386 container_name

**Listing Containers**

    lxc list

lists all your containers with default displayed information. On systems with a lot of containers, displaying the
default result can be a bit slow(retrieve network information from the containers), you may instead want

    lxc list --fast

which shows a different set of columns that require less processing on the server side.

You can also filter based on name or properties.

    lxc list security.privileged=true

In this example, only containers that are privileged(user namespace disabled) are listed.

    lxc list --fast alpine

which lists only the containers that have “alpine” in their names(complex regular expressions are also supported).

**Details About a Container**

    lxc info containerName

This command queries information about an specific container.

**tart Container**

    lxc start containerName

**Stop Container**

    lxc stop containerName (--force)

**Restart Container**

    lxc restart containerName (--force)

**Pause Container**

    lxc pause containerName

When a container is "paused", all the container tasks will be sent the equivalent of a SIGSTOP which means that they
will still be visible and will still be using memory but they won't get any CPU time from the scheduler. This is useful
if you have a CPU hungry container that takes quite a while to start but that you aren't constantly using. You can let
it start, then pause it, then start it again when needed.

**Delete Container**

    lxc delete containerName

#### ZFS

ZFS is a combination of logical volume manager and filesystem. Containers use it for their filesystem for the following
benefits

* snapshots
* copy-on-write cloning - "copy-on-write" means that everyone has a single shared copy of the same data until it's
  written, and then a copy is made. Usually, copy-on-write is used to resolve concurrency problems. In ZFS data blocks
  on disk of a container are allocated copy-on-write, a copy of this container shares the same data blocks. A copy of a
  block will be sent to the clone only if that block is modified in the original container. This reduces the number of
  new blocks that are to be allocated.
* continuous integrity checking against data corruption
* automatic repair
* efficient data compression


The OpenStack Architecture
--------------------------

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

By "architecture" in this context, we mean the component serivces we use to construct an OpenStack clould.

> ⚠️ Using OpenStack does not involve a "one-for-all" architecture. Becauase different organization imposes different
> set of needs and requirements. Aa minimum configuration is good for providing proof-of-concept for the purpose of
> learning about OpenStack. For a productionized architecture focusing a some specific use case or how to determine
> which architecture is required, there needs a "Architecture Design Guide" which is covered in this section.

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

###### Network

It is important to consider the functionality, security, scalability, availability, and testability of the network when
choosing a CMP and cloud provider.

* Decide on a network framework and design minimum functionality tests. This ensures testing and functionality persists
  during and after upgrades.
* Scalability across multiple cloud providers may dictate which underlying network framework you choose in different
  cloud providers. It is important to present the network API functions and to verify that functionality persists across
  all cloud endpoints chosen.
* High availability implementations vary in functionality and design. Examples of some common methods are
  active-hot-standby, active-passive, and active-active. Development of high availability and test frameworks is
  necessary to insure understanding of functionality and limitations.
* Consider the security of data between the client and the endpoint, and of traffic that traverses the multiple clouds.

For example, degraded video streams and low quality VoIP sessions negatively impact user experience and may lead to
productivity and economic loss.

* **Network misconfigurations** - Configuring incorrect IP addresses, VLANs, and routers can cause outages to areas of
  the network or, in the worst-case scenario, the entire cloud infrastructure. Automate network configurations to
  minimize the opportunity for operator error as it can cause disruptive problems.
* **Capacity planning** - Cloud networks require management for capacity and growth over time. Capacity planning
  includes the purchase of network circuits and hardware that can potentially have lead times measured in months or
  years.
* **Network tuning** - Configure cloud networks to minimize link loss, packet loss, packet storms, broadcast storms, and
  loops.
* **Single Point Of Failure (SPOF)** - Consider high availability at the physical and environmental layers. If there is
  a single point of failure due to only one upstream link, or only one power supply, an outage can become unavoidable.
* **Complexity** - An overly complex network design can be difficult to maintain and troubleshoot. While device-level
  configuration can ease maintenance concerns and automated tools can handle overlay networks, avoid or document
  non-traditional interconnects between functions and specialized hardware to prevent outages.
* **Non-standard features** - There are additional risks that arise from configuring the cloud network to take advantage
  of vendor specific features. One example is multi-link aggregation (MLAG) used to provide redundancy at the aggregator
  switch level of the network. MLAG is not a standard and, as a result, each vendor has their own proprietary
  implementation of the feature. MLAG architectures are not interoperable across switch vendors, which leads to vendor
  lock-in, and can cause delays or inability when upgrading components.
* **Dynamic resource expansion or bursting** - An application that requires additional resources may suit a multiple
  cloud architecture. For example, a retailer needs additional resources during the holiday season, but does not want to
  add private cloud resources to meet the peak demand. The user can accommodate the increased load by bursting to a
  public cloud for these peak load periods. These bursts could be for long or short cycles ranging from hourly to
  yearly.
  
##### Compliance and geo-location

An organization may have certain legal obligations and regulatory compliance measures which could require certain
workloads or data to not be located in certain regions.

Compliance considerations are particularly important for multi-site clouds. Considerations include:

* federal legal requirements
* local jurisdictional legal and compliance requirements
* image consistency and availability
* storage replication and availability (both block and file/object storage)
* authentication, authorization, and auditing (AAA)

Geographical considerations may also impact the cost of building or leasing data centers. Considerations include:

* floor space
* floor weight
* rack height and type
* environmental considerations
* power usage and power usage efficiency (PUE)
* physical security

##### Auditing

A well-considered auditing plan is essential for quickly finding issues. Keeping track of changes made to security
groups and tenant changes can be useful in rolling back the changes if they affect production. For example, if all
security group rules for a tenant disappeared, the ability to quickly track down the issue would be important for
operational and legal reasons. For more details on auditing, see the
[Compliance](https://docs.openstack.org/security-guide/compliance.html) in the OpenStack Security Guide.

##### Security

The importance of security varies based on the type of organization using a cloud. For example, government and financial
institutions often have very high security requirements. Security should be implemented according to asset, threat, and
vulnerability risk assessment matrices

##### Service level agreements

Service level agreements (SLA) must be developed in conjunction with business, technical, and legal input. Small,
private clouds may operate under an informal SLA, but hybrid or public clouds generally require more formal agreements
with their users.

For a user of a massively scalable OpenStack public cloud, there are no expectations for control over security,
performance, or availability. Users expect only SLAs related to uptime of API services, and very basic SLAs for services
offered. It is the user’s responsibility to address these issues on their own. The exception to this expectation is the
rare case of a massively scalable cloud infrastructure built for a private or government organization that has specific
requirements.

High performance systems have SLA requirements for a minimum quality of service with regard to guaranteed uptime,
latency, and bandwidth. The level of the SLA can have a significant impact on the network architecture and requirements
for redundancy in the systems.

Hybrid cloud designs must accommodate differences in SLAs between providers, and consider their enforceability.

##### Application Readiness

Some applications are tolerant of a lack of synchronized object storage, while others may need those objects to be
replicated and available across regions. **Understanding how the cloud implementation impacts new and existing
applications is important for risk mitigation, and the overall success of a cloud project**. Applications may have to be
written or rewritten for an infrastructure with little to no redundancy, or with the cloud in mind.

* **Application momentum** Businesses with existing applications may find that it is more cost effective to integrate
  applications on multiple cloud platforms than migrating them to a single platform.
* **No predefined usage model** The lack of a pre-defined usage model enables the user to run a wide variety of
  applications without having to know the application requirements in advance. This provides a degree of independence
  and flexibility that no other cloud scenarios are able to provide.
* **On-demand and self-service application** By definition, a cloud provides end users with the ability to
  self-provision computing power, storage, networks, and software in a simple and flexible way. The user must be able to
  scale their resources up to a substantial level without disrupting the underlying host operations. One of the benefits
  of using a general purpose cloud architecture is the ability to start with limited resources and increase them over
  time as the user demand grows.

##### Authentication

It is recommended to have a single authentication domain rather than a separate implementation for each and every site.
This requires an authentication mechanism that is highly available and distributed to ensure continuous operation.
Authentication server locality might be required and should be planned for.

##### Migration, Availability, Site Loss and Recovery

Outages can cause partial or full loss of site functionality. Strategies should be implemented to understand and plan
for recovery scenarios.

* The deployed applications need to continue to function and, more importantly, you must consider the impact on the
  performance and reliability of the application when a site is unavailable.
* It is important to understand what happens to the replication of objects and data between the sites when a site goes
  down. If this causes queues to start building up, consider how long these queues can safely exist until an error
  occurs.
* After an outage, ensure the method for resuming proper operations of a site is implemented when it comes back online.
  We recommend you architect the recovery to avoid race conditions.
* Cheaper storage makes the public cloud suitable for maintaining backup applications.
* Hybrid cloud architecture enables the migration of applications between different clouds.
* Business changes can affect provider availability. Likewise, changes in a provider's service can disrupt a hybrid
  cloud environment or increase costs.
* **Consumers of external clouds rarely have control over provider changes to APIs, and changes can break compatibility.
  Using only the most common and basic APIs can minimize potential conflicts**.
* **As of the Kilo release, there is no common image format that is usable by all clouds. Conversion or recreation of
  images is necessary if migrating between clouds. To simplify deployment, use the smallest and simplest images
  feasible, install only what is necessary, and use a deployment manager such as Chef or Puppet. Do not use golden
  images to speed up the process unless you repeatedly deploy the same images on the same cloud**.
* Organizations leveraging cloud-based services can embrace business diversity and utilize a hybrid cloud design to
  spread their workloads across multiple cloud providers. This ensures that no single cloud provider is the sole host
  for an application.









**To be continued...**

Use Cases (Learn from Others)
-----------------------------

* [How Yahoo! Uses Neutron for Ironic](https://www.openstack.org/videos/summits/tokio-2015/how-yahoo-uses-neutron-for-ironic)
* [Yahoo Engineering - Operating OpenStack at Scale](https://yahooeng.tumblr.com/post/159795571841/operating-openstack-at-scale)


Swift
-----

### Components

Object Storage uses the following components to deliver high availability, high durability, and high concurrency:

* [**Proxy servers**](#proxy-servers) handles all of the incoming API requests.
* [**Rings**](#rings) maps logical names of data to locations on particular disks.
* [**Zones**](#zones) solates data from other zones. A failure in one zone does not impact the rest of the cluster as
  data replicates across zones.
* [**Accounts and containers**](#accounts-and-containers) Each account and container are individual databases that are distributed across the cluster. An account database contains the list of containers in that account. A container database contains the list of objects in that container.
* **Objects** are the **data** itself.
* [**Partitions**](#partitions) store objects, account databases, and container databases and helps manage locations
  where data lives in the cluster.

![Error loading swift-objectstorage-buildingblocks.png]({{ "/assets/img/swift-objectstorage-buildingblocks.png" | relative_url}})

#### Proxy Servers

Proxy servers are the public face of Object Storage and handle all incoming API requests. Once a proxy server receives a
request, it determines the storage node based on the object's URL, for example,
`https://swift.example.com/v1/account/container/object`. Proxy servers also coordinate responses, handles failures, and
coordinate timestamps.

Proxy servers use a [shared-nothing architecture](https://en.wikipedia.org/wiki/Shared-nothing_architecture) and can be
scaled as needed based on projected workloads. A minimum of two proxy servers should be deployed behind a
separately-managed load balancer. If one proxy server fails, the other take over.

#### Rings

A ring represents a mapping between the names of entities stored in the cluster and their physical locations on disks. 
There are separate rings for accounts, containers, and objects. When components of the system need to perform an
operation on an object, container, or account, they need to interact with the corresponding ring to determine the 
appropriate location in the cluster.

The ring maintains this mapping using zones, devices, partitions, and replicas. Each partition in the ring is
replicated, by default, three times across the cluster, and partition locations are stored in the mapping maintained by 
the ring. The ring is also responsible for determining which devices are used as handoffs in failure scenarios.

Data can be isolated into zones in the ring. Each partition replica will try to reside in a different zone. A zone could 
represent a drive, a server, a cabinet, a switch, or even a data center.

The partitions of the ring are distributed among all of the devices in the Object Storage installation. When partitions 
need to be moved around (for example, if a device is added to the cluster), the ring ensures that a minimum number of 
partitions are moved at a time, and only one replica of a partition is moved at a time.

You can use weights to balance the distribution of partitions on drives across the cluster. This can be useful, for 
example, when differently sized drives are used in a cluster.

The ring is used by the proxy server and several background processes (like replication).

These rings are externally managed. The server processes themselves do not modify the rings, they are instead given new 
rings modified by other tools.

The ring uses a configurable number of bits from an MD5 hash for a path as a partition index that designates a device.
The number of bits kept from the hash is known as the partition power, and 2 to the partition power indicates the 
partition count. Partitioning the full MD5 hash ring allows other parts of the cluster to work in batches of items at
once which ends up either more efficient or at least less complex than working with each item separately or the entire 
cluster all at once.

Another configurable value is the replica count, which indicates how many of the partition-device assignments make up a 
single ring. For a given partition index, each replica’s device will not be in the same zone as any other replica’s 
device. Zones can be used to group devices based on physical locations, power separations, network separations, or any 
other attribute that would improve the availability of multiple replicas at the same time.

#### Zones

Object Storage allows configuring zones in order to isolate failure boundaries. If possible, each data replica resides
in a separate zone. At the smallest level, a zone could be a single drive or a grouping of a few drives. If there were 
five object storage servers, then each server would represent its own zone. Larger deployments would have an entire rack 
(or multiple racks) of object servers, each representing a zone. The goal of zones is to allow the cluster to tolerate 
significant outages of storage servers without losing all replicas of the data.

![Error loading objectstorage-zones.png]({{ "/assets/img/objectstorage-zones.png" | relative_url}})

#### Accounts and Containers

Each account and container is an individual SQLite database that is distributed across the cluster. An account database 
contains the list of containers in that account. A container database contains the list of objects in that container.

![Error loading objectstorage-accountscontainers.png]({{ "/assets/img/objectstorage-accountscontainers.png" | relative_url}})

To keep track of object data locations, each account in the system has a database that references all of its containers, 
and each container database references each object.

#### Partitions

A partition is a collection of stored data. This includes account databases, container databases, and objects.
Partitions are core to the replication system.

Think of a partition as a bin moving throughout a fulfillment center warehouse. Individual orders get thrown into the
bin. The system treats that bin as a cohesive entity as it moves throughout the system. A bin is easier to deal with
than many little things. It makes for fewer moving parts throughout the system.

System replicators and object uploads/downloads operate on partitions. As the system scales up, its behavior continues
to be predictable because the number of partitions is a fixed number.

Implementing a partition is conceptually simple: a partition is just a directory sitting on a disk with a corresponding 
hash table of what it contains.

![Error loading objectstorage-partitions.png]({{ "/assets/img/objectstorage-partitions.png" | relative_url}})

#### 

In order to ensure that there are three copies of the data everywhere, replicators continuously examine each partition. For each local partition, the replicator compares it against the replicated copies in the other zones to see if there are any differences.

The replicator knows if replication needs to take place by examining hashes. A hash file is created for each partition, which contains hashes of each directory in the partition. For a given partition, the hash files for each of the partition’s copies are compared. If the hashes are different, then it is time to replicate, and the directory that needs to be replicated is copied over.

This is where partitions come in handy. With fewer things in the system, larger chunks of data are transferred around (rather than lots of little TCP connections, which is inefficient) and there is a consistent number of hashes to compare.

The cluster has an eventually-consistent behavior where old data may be served from partitions that missed updates, but replication will cause all partitions to converge toward the newest data.