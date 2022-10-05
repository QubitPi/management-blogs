---
layout: post
title: Kubernetes - Container Manager
tags: [Virtualization, Kubernetes]
category: FINALIZED
color: rgb(49, 109, 230)
feature-img: "assets/img/post-cover/28-cover.png"
thumbnail: "assets/img/post-cover/28-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Kubernetes is an open source container orchestration engine for automating deployment, scaling, and management of
containerized applications.

<!--more-->

* TOC
{:toc}


Overview
--------

Kubernetes is a portable, extensible, open source platform for managing containerized workloads and services. It
facilitates both declarative configuration and automation. It has a large, rapidly growing ecosystem.

### Going Back in Time

![Error loading container-evolution.svg]({{ "/assets/img/container-evolution.svg" | relative_url}})

* **Traditional Deployment Era** Early on, organizations ran applications on physical servers. There was no way to
  define resource boundaries for applications in a physical server, and this caused **resource allocation issues**. For 
  example, if multiple applications run on a physical server, there can be instances where one application would take up 
  most of the resources, and as a result, the other applications would underperform. A solution for this would be to run 
  each application on a different physical server. But this **did not scale** as resources were underutilized, and it
  was expensive for organizations to maintain many physical servers.
* **Virtualized Deployment Era** As a solution, virtualization was introduced. It allows you to run multiple Virtual Machines (VMs) on a single physical server's CPU. Virtualization allows applications to be isolated between VMs and provides a level of security as the information of one application cannot be freely accessed by another application.

  Virtualization allows better utilization of resources in a physical server and allows better scalability because an 
  application can be added or updated easily, reduces hardware costs, and much more. With virtualization you can present
  a set of physical resources as a cluster of disposable virtual machines.

  Each VM is a _full machine running all the components_, including its own operating system, on top of the virtualized 
  hardware.
* **Container Deployment Era** Containers are similar to VMs, but they have relaxed isolation properties to share the
  Operating System among the applications. Therefore, containers are considered lightweight. Similar to a VM, a
  container has its own filesystem, share of CPU, memory, process space, and more. As they are decoupled from the
  underlying infrastructure, they are **portable across clouds and OS distributions**

  Containers have become popular because they provide extra benefits, such as

  - **Agile application creation and deployment** increased ease and efficiency of container image creation compared to
    VM image use. 
  - **Continuous development, integration, and deployment** provides for reliable and frequent container image build and
    deployment with quick and efficient rollbacks (due to image immutability)
  - Dev and Ops separation of concerns. Create application container images at build/release time rather than deployment
    time, thereby **decoupling applications from infrastructure**
  - Observability. Not only surfaces OS-level information and metrics, but also application health and other signals
  - **Environmental consistency across development, testing, and production**. Runs the same on a laptop as it does in
    the cloud
  - **Application-Centric Management** Raises the level of abstraction from running an OS on virtual hardware to running
    an application on an OSS using logical resources
  - **Promotes loosely coupled, distributed, elastic, liberated micro-services**. Applications are broken into smaller,
    independent pieces and can be deployed and managed dynamically - not a monolithic stack running on one big 
    single-purpose machine

### Why Do We Need Kubernetes When Containers are There Already?

Containers are a good way to bundle and run your application. In a production environment, we need to manage the
containers that run the application and ensure that there is no downtime. For example, if a container goes down, another
container needs to start. Wouldn't it be easier if this behavior was handled by a system?

This is how Kubernetes comes to the rescue! Kubernetes provides you with a framework to run distributed systems
resiliently. It takes care of scaling and failover for our application, provides deployment patterns, and more. For
example, Kubernetes can easily manage a canary deployment for our system.

> In software engineering, **canary deployment** is the practice of making staged releases. We roll out a software
> update to a small part of the users first, so they may test it and provide feedback. Once the change is accepted, the 
> update is rolled out to the rest of the users.

Kubernetes provides us with

* **Service discovery and load balancing** Kubernetes can expose a container using the DNS name or using their own IP
  address. If traffic to a container is high, kubernetes is able to load balance and distribute the network traffic so
  that the deployment is stable.
* **Storage Orchestration** Kubernetes allows us to automatically mount a storage system of our choice, such as local 
  storage, public cloud providers, and more. 
* **Automated rollouts and rollbacks** We can describe the desired state for our deployed containers using Kubernetes,
  and it can change the actual state to the desired state at a controlled rate. For example, we can automate Kubernetes
  to create new containers for our deployment, remove existing containers and adopt all their resources to the new 
  container.
* **Automatic bin packing** We provide Kubernets with a cluster of nodes that it can use to run containerized tasks. We
  tell Kubernetes how much CPU and memory each container needs. Kubernetes can fit containers onto our nodes to make the 
  best use of our resources
* **Self-healing** Kubernetes restarts containers that fail, replaces containers, kills containers that don't respond to
  our user-defined health check, and doesn't advertise them to clients until they are ready to serve
* **Secret and configuration management** Kubernetes lets us store and manage sensitive information, such as passwords,
  OAuth tokens, and SSH keys. We can deploy and updates secrets and application configuration without rebuilding our 
  container images, and without exposing secretes in our stack configuration

> In essense, Kubernetes comprises a set of independent, composable contorl process that continuously drive the current
> state towards the provided desired state. 

### Kubernetes Components

When you deploy Kubernetes, you get a cluster.

A Kubernetes cluster consists of a set of worker machines, called **nodes**, that run containerized applications. Every
cluster has at least one worker node. The worker node(s) host the **Pods** that are the components of the application
workload. The **control plane** manages the worker nodes and the Pods in the cluster. In production environments, the
control plane usually runs across multiple computers and a cluster usually runs multiple nodes, providing
fault-tolerance and high availability.

![Error loading components-of-kubernetes.svg]({{ "/assets/img/components-of-kubernetes.svg" | relative_url}})

#### Control Plane

The control plane's components make global decisions about the cluster (for example, scheduling), as well as detecting
and responding to cluster events (for example, starting up a new pod when a deployment's `replicas` field is
unsatisfied).

##### API server

The **API server** is a component of the Kubernetes control plane that exposes the Kubernetes API. The API server is the
front end for the Kubernetes control plane. API server is designed to scale horizontally. We can run several instances
of API server and balance traffic between those instances.

##### etcd Data Store

Consistent and highly-available key value store used as Kubernetes' backing store for all cluster data.

##### Scheduler

Control plane component that watches for newly created Pods with no assigned node, and selects a node for them to run on.

##### Control Manager

Control plane component that runs controller processes.

> A controller runs a control loop that watches the shared state of a cluster through API server and makes changes
> attempting to move the current state toward the desired state

Logically, each controller is a separate process, but to reduce complexity, they are all compiled into a single binary
and run in a single process.

##### Cloud Control Manager

A Kubernetes control plane component that embeds cloud-specific control logic. The cloud controller manager lets you link our cluster into our cloud provider's API, and separates out the components that interact with that cloud platform from components that only interact with our cluster.

The cloud control manager only runs controllers that are specific to a cloud provider. If we are running Kubernetes on our own premises, or in a learning environment inside our own PC, the cluster does not have a cloud controller manager.

As with the [Kubernetes' control manager](#control-manager), the cloud control manager combines several logically independent control loops into a single binary that we run as a single process. We can scale horizontally (run more than one copy) to improve performance or to help tolerate failures.

Some [Kubernetes' control manager](#control-manager) controllers do depend on cloud control manager, such as

* Node controller: For checking the cloud provider to determine if a node has been deleted in the cloud after it stops responding
* Route controller: For setting up routes in the underlying cloud infrastructure
* Service controller: For creating, updating and deleting cloud provider load balancers

#### Node

##### kubelet

An agent that runs on each node in cluster and makes sure that containers are running in a Pod.

The kubelet takes a set of PodSpecs that are provided through various mechanisms and ensures that the containers
described in those PodSpecs are running and healthy. The kubelet doesn't manage containers which were not created by
Kubernetes.

##### kube-proxy

kube-proxy is a network proxy that runs on each node in cluster, implementing part of the Kubernetes Service concept.

kube-proxy maintains network rules on nodes. These network rules allow network communication to our Pods from network
sessions inside or outside of our cluster.

kube-proxy uses the operating system packet filtering layer if there is an available one. Otherwise, kube-proxy forwards
the traffic itself.

#### Container Runtime

The container runtime is the software that is responsible for running containers. Kubernetes supports container runtimes
such as [containerd](https://containerd.io/docs/), [CRI-O](https://cri-o.io/#what-is-cri-o), and any other
implementation of the [Kubernetes CRI (Container Runtime Interface)][CRI].

### Kubernetes API

The core of Kubernetes' [control plane](#control-plane) is the [API server](#api-server). The API server exposes an
HTTP API that lets end users, different parts of your cluster, and external components communicate with one another.

The Kubernetes API lets us query and manipulate the state of API object in Kubernetes (for example, Pods, Namespaces,
ConfigMaps, and Events)

Most operations can be performed through the [kubectl](https://kubernetes.io/docs/reference/kubectl/) command-line 
interface or other command-line tools, such as [kubeadm](https://kubernetes.io/docs/reference/setup-tools/kubeadm/),
which in turn use the API. We can also access the API directly using REST calls.

Consider using one of the [client libraries](client libraries) if you are writing an application using the Kubernetes
API.

> Kubernetes stores the serialized state of objects in [etcd](https://etcd.io/)

The Kubernetes API can be extended through either

1. [Custom resources][custom resources], or
2. [Aggregation layer](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/apiserver-aggregation/)

Concepts
--------

### Nodes

Kubernetes runs our workload by placing containers into Pods to run on _Nodes_. A node may be a virtual or physical
machine, depending on the cluster. Each node is managed by the [control plane](#control-plane) and contains the services 
necessary to run Pods.

### Controllers

> In robotics and automation, a control loop is a **non-terminating** loop that regulates the **state** of a system
> 
> Take thermostat in a room as an example. When we set the temperature, that's telling the thermostat about our
> **desired state**. The actual room temperature is the **current state**. The thermostat acts to bring the current
> state closer to the desired state, by turning equipment on or off.

In Kubernetes, controllers are control loops that watch the state of our cluster, then make or request changes when
needed. Each controller tries to move the current cluster state closer to the desired state.

A controller tracks at least one Kubernetes resource type. These resource _objects_ have a spec field that represents
the desired state. The controller(s) for that resource are responsible for making the current state come closer to that
desired state

> **Understanding Kubernetes Objects**
> 
> _Kubernetes objects_ are persistent entities in the Kubernetes system. **Kubernetes uses these entities to represent
> the _state_ of our cluster**. They can describe
> 
> * what containerized applications are running (and on which nodes)
> * the resources available to those applications
> * the policies around how those applications behave, such as restart policies, upgrades, and fault-tolerance
>
> A Kubernetes object is a "record of intent" - once we create the object, the Kubernetes system will constantly work to
> ensure that object exists. By creating an object, we are effectively tellling the Kubernetes system what we want our
> cluster's workload to look like; this is our cluster's _desired state_.
> 
> To work with Kubernetes objects - whether to create, modify, or delete them - we will need to use the
> [**Kubernetes API**](#kubernetes-api). When we use the `kubectl` command-line interface, for example, the CLI makes
> the necessary Kubernetes API calls for us. We can also use the Kubernetes API directly in our own programs using one
> of the [client libraries][client libraries]
> 
> **Describing a Kubernetes Object**
> 
> When we create an object in Kubernetes, we must provide the object spec that describes its desired state, as well as
> some basic information about the object (such as name). When we use the Kubernetes API to create the object, that API
> request includ that information as JSON in the request body. **Most often, we provide the information to `kubectl` in
> a .yaml file**. `kubectl` converts the information to JSON for us.
> 
> Here is an example .yaml file that shows required fields and object spec for a Kubernetes Deployment:
> 
> ```yaml
> apiVersion: apps/v1
> kind: Deployment
> metadata:
>   name: nginx-deployment
> spec:
>   selector:
>     matchLabels:
>       app: nginx
>   replicas: 2 # tells deployment to run 2 pods matching the template
>   template:
>     metadata:
>       labels:
>         app: nginx
>     spec:
>       containers:
>       - name: nginx
>         image: nginx:1.14.2
>         ports:
>         - containerPort: 80
> ```
> 
> One way to create a Deployment using a .yml file like the one above is to use the
> [`kubectl apply`](https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands#apply) command (assuming
> the .yml file path is `https://k8s.io/examples/application/deployment.yaml`):
> 
> ```bash
> kubectl apply -f https://k8s.io/examples/application/deployment.yaml
> ```
> 
> The output is similar to this:
> 
> ```
> deployment.apps/nginx-deployment created
> ```
> 
> Note that each .yml object file, the following fields are required:
> 
> * `apiVersion` - which version of the Kubernetes API we are using to create this object
> * `kind` - the object type we would like to create
> * `metadata` - data that uniquely identifies the object, including a name, UID, and an optional namespace
> * `spec` - the desired state of the object. The precise format of this field is different for every Kubernetes object.
>   The [Kubernetes API Reference](https://kubernetes.io/docs/reference/kubernetes-api/) helps us find the spec format
>   for all of the objects we can create using Kubernetes

A controller operates in one of 2 patterns:

1. (More common) Send messages to the [API server](#api-server) that have useful side effects.
2. Carry out action itself

Let's discuss each of them separately below

#### Control vis API Server

The [Job](#jobs) controller is an example of a Kubernetes built-in controller. Built-in controllers manage state by
interacting with the cluster API server.

Job is a Kubernetes resource that runs a [Pod](#pods), or perhaps several Pods, to carry out a task and then stop. When
a Job controller sees a new task, it makes sure that the kubelets on a set of Nodes are running the right number of Pods
to get the work down. The Job controller does not run any Pods or containers itself. Instead, the job controller tells
the API server to create or remove Pods. Other components if the [control plane](#control-plane) act on the new
information and eventually the work is donw. 

After we create a new Job, the desired state is for that Job to be completed. The Job controller makes the current state
for that Job be closer to our desired state by creating Pods that do the work so that the Job is moving towards to
completion.

#### Direct Control

Some controllers need to make changes to things outside cluster. For example, if you use a control loop to make sure
there are enough [Nodes](#nodes) cluster, then that controller needs something outside the current cluster to set up new
Nodes when needed. 

> There has already been such [controller](https://github.com/kubernetes/autoscaler/) that horizontally scales nodes

Controllers that interact with external state find their desired state from the API server, then communicate directly
with an external system to bring the current state closer in line. Next it reports current state back to API server so
that other control loops can observe that reported data and take their own actions. 

> 📋️ Cloud native computing is an approach in software development that utilizes cloud computing to build and run
> scalable applications in modern, dynamic environments such as public, private, and hybrid clouds. Technologies such as
> containers, microservices, serverless functions and immutable infrastructure, deployed via declarative code are common
> elements of this architecture type

### Pods

_Pods_ are the smallest deployable units of computing that we can create and manage in Kubernetes. 

A _Pod_ is a group of one or more containers, with shared storage and network resources, and a specification for how to
run the containers. A Pod's contents are always co-located and co-scheduled, and run in a shared context. A Pod models
and application-specific "logical host": it contains one ore more application containers which are relatively tightly
coupled.

A Pod can contain [init containers](#init-containers) that run during Pod startup. We can also inject
[ephemeral containers](#ephemeral-containers) for debugging if the cluster offers this.

The shared context of a Pod is a set of Linux namespaces, cgroups, and potentially other facets of isolation - the same
things that isolated a container. Within a Pod's context, the individual applications may have further sub-isolations
applied.

> 📋 **cgroups** (**control groups**) is a Linux kernel feature that limits, accounts for, and isolates the resource
> usage (CPU, memoey, disk I/O, network, etc) of a collection of processes. 

A Pod is similar to a set of containers with shared namespaces and shared filesystem volumes.

For example, the following is a Pod which consists of a container running the image "nginx:1.14.2":

```xml
apiVersion: v1
kind: Pod
metadata:
    name: nginx
spec:
    containers:
    - name: nginx
      image: nginx:1.14.2
      ports:
      - containerPort: 80
```

To create the Pod defined above, run the following command (assuming the Pod definition is in a filed located at
`https://k8s.io/examples/pods/simple-pod.yaml`): 

```bash
kubectl apply -f https://k8s.io/examples/pods/simple-pod.yaml
```

**In general, however, Pods are not created directly** but are created using [template](#pod-templates) with workload 
resources such as

* [Job](#jobs)
* [Deployment](#deployments)
* [StatefulSet](#statefulsets)
* [DaemonSet](#daemonset)

Pods in a Kubernetes cluster are used in 2 main ways:

1. **Pods that run a single container**. The "one-container-per-Pod" model is the most common Kubernetes use case; we
   can think of this kind of Pod as a wrapper around a single container. **Kubernetes manages Pods rather than managing
   the containers directly**.
2. **Pods that run multiple containers working togeter** A Pod can encapsulate an application composed of multiple
   co-located containers that are tightly coupled and need to share resources. These co-located containers form a single
   cohesive unit of service. For example, one container serving data stored in a shared volume to the public, while a
   separate _sidecar_ container refreshes or updates those files, as in the following diagram. The Pod wraps these 
   containers, storage resources, and and ephemeral network identity together as a single unit.

   ![Error loading pod.svg]({{ "/assets/img/pod.svg" | relative_url}})
   
   Pods are designed to support multiple cooperating processes (as containers) that form a cohesive unit of service. The
   containers in a Pod are automatically co-located and co-scheduled on the same physical or virtual machine in the
   cluster. The containers can share resources and dependencies, communicate with each other, and coordinate when and
   how they are terminated.

   Some Pods have [init containers](#init-containers) as well as app containers. Init containers run and complete before
   app containers are started

> 📋 Container Design Patterns for Kubernetes - Sidecar Container
> 
> This note is a summary of
> [the original paper _Design Pattern for Container-Based Distributed Systems_]({{ "/assets/pdf/design-patterns-for-container-based-distributed-systems.pdf" | relative_url}})
>
> In general, design patterns are implemented to solve and reuse common well thought out architectures. Design patterns
> also introduce efficiency into our application and for our developers, reducing overhead and providing us with a way
> to reuse containers across our applications. There are several ways to group or to enhance containers inside of
> Kubernetes Pods. These patterns can be categorized as single node, multi-container patterns and advanced multi-node
> application patterns.
> 
> **(Single node, multiple container) Sidecar Pattern**
> 
> In these single node patterns, all containers are co-scheduled on a signle node or machine with events occurring
> between containers on a Pod
> 
> The sidecar container extends and works with the primary container. This pattern is best used when there is a clear
> difference between a primary container and any secondary tasks that need to be done for it. 
> 
> For example, a web server container (a primary application) that needs to have its logs parsed and forwarded to log
> storage (a secondary task) may use a side car container that takes care of the log forwarding. This same sidecar
> container can also be used in other places in the stack to forward logs for other web servers or even other
> applications.
> 
> ![Error loading sidecar-pattern.png]({{ "/assets/img/sidecar-pattern.png" | relative_url}})
> 
> **(Single node, multiple container) Ambassador/Proxy Pattern**
> 
> The ambassador pattern is another way to run additional services together with our main application container but it
> does so through a proxy. The primary goal of an ambassador container is to simplify the access of external services
> for the main application where the ambassador container acts as a service discovery layer. All configuration for the
> external service lives within the ambassador container. The ambassador container takes care of connecting to a
> service, keeping the connection open, re-connecting when something unexpected happens, and updating the configuration
> 
> With this pattern developers only need to think about their app connecting to a single server on the localhost. This
> pattern is unique to containers since all Pods running on the same machine will share the localhost network interface.
> 
> ![Error loading ambassador-pattern.png]({{ "/assets/img/ambassador-pattern.png" | relative_url}})
> 
> **(Single node, multiple container) Adapter Pattern**
> 
> The adapter container pattern generally transforms the output of the primary container into the output that fits the
> standards across our applications. For example, an adapter container could expose a standardized monitoring interface
> to our application even though the application does not implement it in a standard way. The adapter container takes
> care of converting the output into what is accepted at the cluster level. 
> 
> ![Error loading adaptor-pattern.png]({{ "/assets/img/adaptor-pattern.png" | relative_url}})
> 
> **(Multi-node application patterns) Leader Election Pattern**
> 
> In a multi-node pattern, containers are not on a single machine or node, instead these more advanced patterns
> coordinate communications across multiple nodes. According to Brendan Burns, "modular containers make it easier to
> build coordinate multi-node distributed applications
> 
> A common problem with distributed systems that replicated processes is the ability to elect a leader. Replication is
> commonly used to share the load among identical instances of a component, for example, an applications may need to
> distinguish one replica from a set as the "leader". If the election fails, another set must move in to take its place.
> We may also have multiple leaders that need to be elected in parallel across shards.
> 
> There are libraries that can handle such types of elections for us, but they are limited to a particular language and
> can be complex to implement. A pattern is to link a leader election library to our application through an election
> leader container. We can then deploy a set of leader-election containers, each one co-schedule with an instance of the
> application that needs the leader election. A simplified HTTP API can then be used over the localhost network to
> perform the election when needed. 
> 
> The idea behind this pattern is that the leader election containers can be built once and reused across our
> application
> 
> **(Multi-node application patterns) Work Queue Pattern**
> 
> This is another common problem in distributed computing. Like leader elections it also benefits from containerization.
> There are frameworks available so solve the problem but again are limited to a single language environment. Instead,
> a generic work queue container can be created and reused whenever this capability is required. The developer then will
> only need to create another container that can take input data and transform it into the required output. The generic
> work queue container in this case does the heavy lifting to coordinate the queue.
> 
> ![Error loading work-queue.png]({{ "/assets/img/work-queue.png" | relative_url}})
> 
> **(Multi-node application patterns) Scatter/Gather Pattern**
> 
> In this pattern, which is common in search engines, an external client sends an initial request to a "root" or to a
> "parent" node. The root scatters the request out to a group of servers to perform a set of tasks in parallel and where
> each shard returns partial data. The root is responsible for gathering the data into a single response for the
> origianl request.
> 
> ![Error scatter-gather.png]({{ "/assets/img/scatter-gather.png" | relative_url}})

Each Pod is meant to run a single instance of a given application. If we would like to scale application horizontally,
we should use multiple Pods, one for each instance. In Kubernetes, this is typically referred to as **replication**.
Replicated Pods are usually created and managed as a group by a **workload resource** and its
[**controller**](#controllers). For example, if a Node fails, a controller notices that Pods on that Node have stopped
working and creates a replacement Pod. The scheduler places the replacement Pod onto a healthy Node. 

#### Pod Templates

Controllers for **workload** resources create Pods from a _Pod template_ and manage those Pods on our behalf.

> **Workloads**
> 
> A workload is an application running inside a set of Pods. We don't manage Pod lifecycle directly. Instead, we use
> _workload resources_ that manage a set of Pods on our behalf. These resources configure [controllers](#controllers)
> that make sure Pods match the state moves to what we specified
> 
> Kubernetes provides several built-in workload resources:
> 
> * [Deployment](#deployments)
> * [ReplicaSet](#replicaset)
> * [StatefulSet](#statefulsets)
> * [DaemonSet](#daemonset)
> * [Job](#jobs)
> * [CronJob](#cronjob)
> 
> In the wider Kubernetes ecosystem, we can find third-party workload resources that provide additional behaviors by
> using [custom resource definition](custom resources)

Pod templates are _specifications_ for creating Pods and are included in workload resources such as
[deployments](#deployments). Controller for a workload resource uses this specification inside workload object to
construct actual Pods. Note that the Pod template is also part of the desired state. For example, the template below is
a simple Job with a template that starts one container. The container in that Pod prints a message then pauses:

```xml
apiVersion: batch/v1
kind: Job
metadata:
  name: hello
spec:
  template:
    spec:
      containers:
      - name: hello
        image: busybox:1.28
        command: ['sh', '-c', 'echo "Hello, Kubernetes!" && sleep 3600']
      restartPolicy: OnFailure
```

> 📋 Modifying the Pod template has no direct effect on the Pods that already exists. To make the modification
> effective, that resource will **automatically** create replacement Pods that uses the modified template. 

Pods natively provide 2 kins of shared resources for their constituent containers: networking & storage

#### Init Containers

#### Ephemeral Containers

### Deployments

### ReplicaSet

### StatefulSets

### DaemonSet

### Jobs

### CronJob

### Namespace

In Kubernetes, _namespaces_ provides a mechanism for isolating groups of resources within a single cluster. Names of
resources need to be unique within a namespace, but not across namespaces. Namespace-based scoping is applicable only
for namespaced objects (e.g. Deployments, Services, etc) and not for cluster-wide objects (e.g. StorageClass, Nodes,
PersistentVolumesn, etc)

#### When to Use Multiple Namespaces

Namespaces are intended for use in environments with many users spread across multiple teams, or porjects. For clusters
with a few to tens of users, we should not need to create or think about namespaces at all.

Namespaces provide a scope for names. Names of resources need to be unique within a namespace, but not across
namespaces. Namespaces cannot be nested inside one another and each Kubernetes resource can only be in one namespace.

Namespaces are a way to divide cluster resources between multiple users vis
[resource quota](https://kubernetes.io/docs/concepts/policy/resource-quotas/)

It is not necessary to use multiple namespaces to separate slightly different resources, such as different versions of
the same software: use labels to distinguish resources within the same namespace. 


Kubernetes on AWS (EKS)
-----------------------

Amazon **Elastic Kubernetes Service** (Amazon **EKS**) is a managed service that you can use to run Kubernetes on AWS 
without needing to install, operate, and maintain your own Kubernetes control plane or nodes.

> **Why Do We Use EKS?**
> 
> Wh do we care about spinning up an EKS cluster on Amazon? Why not choose to create our own Kubernetes cluster?
> 
> Bootstrapping a Kubernetes cluster involves securing and managing our application, plus cluster, networking, and
> storage configuration. On top of this, Kubernetes maintenance involves upgrades to the cluster, the underlying
> operating system, and much more. Using AWS' managed Kubernetes service, EKS, will ensure that your cluster is
> configured correctly and gets updates and patches on time.
> 
> AWS' EKS works out-of-the-box with the rest of Amazon's infrastructure. Elastic Load Balancers (ELB) are used to
> expose services to the outside world. Your cluster uses Elastic Block Storage (EBS) to store persistent data. Amazon 
> ensures that the data is online and available to your cluster.
> 
> Amazon EKS provides far better scalability than self-hosted Kubernetes. The control plane makes sure that pods are 
> launched across multiple physical nodes. If any of the nodes go down, application will still be online. But if we
> manage our own cluster, we will have to ensure that different VMs (EC2 instances) are on different availability zones. 
> If we can't guarantee that, then running different pods on the same physical server won't bring much fault tolerance.

### Create a New Kubernetes Cluster

This section documents how to create all of the required resources to get started with Amazon Elastic Kubernetes Service 
(Amazon EKS) using eksctl, a simple command line utility for creating and managing Kubernetes clusters on Amazon EKS. At 
the end of this section, we will have a running Amazon EKS cluster that we can deploy applications to.

Before we start, we must install and configure the following tools and resources that we need to create and manage an 
Amazon EKS cluster.

* **kubectl** A command line tool for working with Kubernetes clusters. This guide requires that you use version 1.23 or later. For more information, see [Installing kubectl][Installing kubectl].
* **eksctl** A command line tool for working with EKS clusters that automates many individual tasks. We use version 
  0.112.0 or later. For more information, see [Installing eksctl][Installing eksctl].
* Required [**IAM permissions**](#aws-identity-and-access-management-iam) The IAM security principal that we're using
  must have permissions to work with Amazon EKS IAM roles and service linked roles, AWS CloudFormation, and a VPC and 
  related resources. Okay, to put it simple, we will create a admin user that will cover all of these permissions

#### AWS Identity and Access Management (IAM)

IAM provides the infrastructure necessary to control authentication and authorization for a user's account. The IAM
infrastructure includes the following elements:

![Error loading intro-diagram-policies-800.png]({{ "/assets/img/intro-diagram-policies-800.png" | relative_url}})

* **IAM Resources** The user, group, role, policy, and identity provider objects that are stored in IAM. As with other
  AWS services, we can add, edit, and remove resources from IAM. A resource is an object that exists within a service.
  Examples include an Amazon EC2 instance, an IAM user, and an Amazon S3 bucket. **The service defines a set of actions
  that can be performed on each resource**. If you create a request to perform an unrelated action on a resource, that
  request is denied. For example, if you request to delete an IAM role but provide an IAM group resource, the request
  fails.
* **IAM Identities** The IAM resource objects that are used to identify and group. We can attach a policy to an IAM
  identity. These include users, groups, and roles.
* **IAM Entities** The IAM resource objects that AWS uses for authentication. These include IAM users and roles.
* **Principals** A person or application that can make a request for an action or operation on an AWS resource. The
  principal is authenticated as the AWS account root user or an IAM entity to make requests to AWS. As a best practice,
  do not use root user credentials for daily work. Instead, create IAM entities (users and roles). We can also support
  federated users or programmatic access to allow an application to access our AWS account.

  When a principal tries to use the AWS Management Console, the AWS API, or the AWS CLI, that principal sends a request
  to AWS. The request includes the following information

  - **Actions or operations** The actions or operations that the principal wants to perform. This can be an action in
    the AWS Management Console, or an operation in the AWS CLI or AWS API.
  - **Resources** The AWS resource object upon which the actions or operations are performed.
  - **Principal** The person or application that used an entity (user or role) to send the request. Information about
    the principal includes the policies that are associated with the entity that the principal used to sign in.
  - **Environment data** Information about the IP address, user agent, SSL enabled status, or the time of day.
  - **Resource data** Data related to the resource that is being requested. This can include information such as a
    DynamoDB table name or a tag on an Amazon EC2 instance.

  AWS gathers the request information into a request context, which is used to evaluate and authorize the request.
* **Authentication**  A principal must be authenticated (signed in to AWS) using their credentials to send a request to
  AWS. Some services, such as Amazon S3 and AWS STS, allow a few requests from anonymous users. However, they are the
  exception to the rule.

  To authenticate from the console as a root user, we must sign in with our email address and password. As an IAM user,
  provide our account ID or alias, and then our user name and password. To authenticate from the API or AWS CLI, we must
  provide our access key and secret key. We might also be required to provide additional security information. For
  example, AWS recommends that we use multi-factor authentication (MFA) to increase the security of our account.
* **Authorization** We must also be authorized (allowed) to complete our request. During authorization, AWS uses values
  from the request context to check for policies that apply to the request. It then uses the policies to determine whether
  to allow or deny the request. **Most policies are stored in AWS as JSON documents** and specify the permissions for
  principal entities. There are several types of policies that can affect whether a request is authorized. _To provide
  our users with permissions to access the AWS resources in their own account, we need only identity-based policies_.
  Resource-based policies are popular for granting cross-account access. The other policy types are advanced features
  and should be used carefully.

  AWS checks each policy that applies to the context of a request. If a single permissions policy includes a denied
  action, AWS denies the entire request and stops evaluating. This is called an **explicit deny**. Because requests are
  denied by default, AWS authorizes a request only if every part of the request is allowed by the applicable permissions
  policies.

##### Create IAM Admin User and User Group

As a best practice, do not use the AWS account root user for any task where it's not required. Instead,
[create a new IAM user for each person that requires administrator access][create IAM admin]. Then make those users
administrators by placing the users into an "Administrators" user group to which you attach the AdministratorAccess
managed policy.

> ⚠️ **Safeguard our root user credentials and don't use them for everyday tasks** ⚠️
>
> When we create an AWS account you establish a root username and password to sign in to the AWS Management Console.
> Safeguard our root user credentials the same way we would protect other sensitive personal information. We can do
> this by configuring MFA for our root user credentials. It is not recommended to generate access keys for our root
> user, because they allow full access to all our resources for all AWS services, including our billing information.
> Don't use our root user for everyday tasks. Use the root user to complete the tasks that only the root user can
> perform. For the complete list of these tasks, see [Tasks that require root user credentials][root user tasks] in the
> _AWS General Reference_.

#### Installing eksctl

[eksctl](https://eksctl.io/) is a simple command line tool for creating and managing Kubernetes clusters on Amazon EKS. 
It provides the fastest and easiest way to create a new cluster with nodes for Amazon EKS.

To install eksctl on Mac using Homebrew

```bash
brew tap weaveworks/tap
brew install weaveworks/tap/eksctl
```

> 📋 A common homebrew issue is when we execute `brew install` or `brew tap`, we receive the error like the following:
> 
> ```bash
> brew tap weaveworks/tap
> Running `brew update --auto-update`...
> ==> Tapping weaveworks/tap
> Cloning into '/.../weaveworks/homebrew-tap'...
> fatal: unable to connect to github.com:
> github.com[0: 20.205.243.166]: errno=Operation timed out
> 
> Error: Failure while executing; `git clone https://github.com/weaveworks/homebrew-tap /.../weaveworks/homebrew-tap 
> --origin=origin --template=` exited with 128.
> ```
> 
> First we should try to download an arbitrary big file to confirm our network is working. In fact, we could also
> manually clone the git repo related to the error and see if that works. In this example:
> 
> ```bash
> git clone git@github.com:weaveworks/homebrew-tap.git
> ```
> 
> If this works as well, try switching from http scheme to ssh using
> 
> ```bash
> git config --global url.ssh://git@github.com/.insteadOf https://github.com/
> ```

#### Setting up AWS Credentials for eksctl

This section details how we configure the credentials we need to use the eksctl service.

##### Generate EKS Policy

> 📋 We manage access in AWS by creating **policies** and attaching them to IAM identities (users, groups of users, or 
> roles) or AWS resources. A policy is an object in AWS that, when associated with an identity or resource, defines
> their permissions. AWS evaluates these policies when an IAM principal (user or role) makes a request. Permissions in
> the policies determine whether the request is allowed or denied. Most policies are stored in AWS as JSON documents.
> AWS supports six types of policies
> 
> 1. identity-based policies
> 2. resource-based policies
> 3. permissions boundaries
> 4. Organizations SCPs
> 5. ACLs, and
> 6. session policies.

First all pull down https://github.com/aerospike-examples/kubernetes-aws

```bash
git clone git@github.com:aerospike-examples/kubernetes-aws.git
cd kubernetes-aws
```

The policy we need is in `eks.iam.policy.template`. Some permissions however are account specific - we will see this if
we look for the text "account-id" in `eks.iam.policy.template` - this needs replacing with our own account ID.

To find our account ID, log into the AWS console. Select "My Account"

![Error loading aws-myaccount.png]({{ "/assets/img/aws-myaccount.png" | relative_url}})

We will see our account ID in the next screen. Copy this.

![Error loading copy-account-id.png]({{ "/assets/img/copy-account-id.png" | relative_url}})

From the kubernetes-aws project we just cloned, run

```bash
./make-policy.sh ACCOUNT_ID
```

The result will be saved as **eks.iam.policy**.

Go back to the AWS console. Select the IAM Service in the AWS console (Services->IAM) and click "Policies"

![Error loading aws-policy.png]({{ "/assets/img/aws-policy.png" | relative_url}})

Next "Create Policy". Select "JSON" rather than "Visual Editor", remove the JSON you see and replace with the contents
of **eks.iam.policy**. Our screen should look like

![Error loading eks-policy.png]({{ "/assets/img/eks-policy.png" | relative_url}})

Now click "Review Policy". Give your policy a name e.g. EKS.

![Error loading eks-policy-name.png]({{ "/assets/img/eks-policy-name.png" | relative_url}})

Finally click "Create Policy", bottom right of the above screen.

##### Create EKS User Group

Next we shall create an IAM group and add the EKS policy to it.

Select "Groups", from the left hand IAM menu.

![Error loading iam-group.png]({{ "/assets/img/iam-group.png" | relative_url}})

Click "Create New Group". Give our group a name e.g. "EKS".

![Error loading group-name.png]({{ "/assets/img/group-name.png" | relative_url}})

**We would grant our [admin user](#create-iam-admin-user-and-user-group) full control of this EKS cluster**. This is
the best opportunity to attach this user to this group on this page as well.

Click "Next Step". Search for [the policy we just created](#generate-eks-policy) and select.

![Error loading select-eks-group.png]({{ "/assets/img/select-eks-group.png" | relative_url}})

Click "Next Step", followed by "Create Group". We should see our new group, EKS, appear in the group listing screen.

![Error loading see-group.png]({{ "/assets/img/see-group.png" | relative_url}})

##### Obtaining Access Key & Secret Key

We would poplulate eksctl with this user's credential, which is his/her **Access Key** & **Secret Key**.

We can use the AWS Management Console to create a new IAM user's access keys. Use our AWS account ID or account alias,
our IAM user name, and our password to sign in to the [IAM console](https://console.aws.amazon.com/iam).

> 📋 For our convenience, the AWS sign-in page uses a browser cookie to remember our IAM username and account
> information. If we previously signed in as a different user, choose Sign in to a different account near the bottom of 
> the page to return to the main sign-in page. From there, we can type your AWS account ID or account alias to be 
> redirected to the IAM user sign-in page for our account.

In the navigation bar on the upper right, choose our username, and then choose **My Security Credentials**.

![Error loading aws-security-credential.png]({{ "/assets/img/aws-security-credential.png" | relative_url}})

Expand the **Access keys (access key ID and secret access key)** section. To create an access key, choose
**Create New Access Key**. If this feature is disabled, then we must delete one of the existing keys before we can
create a new one. A warning explains that we have only this one opportunity to view or download the secret access key.
To copy the key to paste it somewhere else for safekeeping, choose **Show Access Key**. To save the access key ID and 
secret access key to a `.csv` file to a secure location on our computer, choose **Download Key File**.

##### AWS CLI Credential Setup

We are now in a position to cache our credentials on disk so they can be used by the AWS CLI or eksctl.

We will need to install the [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html) first.

In the environment in which we will be using the AWS CLI / eksctl type

```bash
aws configure
```

and fill in the [access key and secret access key](#obtaining-access-key--secret-key). We are also required to add in
the default [AWS region](https://docs.aws.amazon.com/general/latest/gr/eks.html) you wish to use. In the end, our 
credentials are stored in `~/.aws/credentials`.

![Error loading aws-configure.png]({{ "/assets/img/aws-configure.png" | relative_url}})

#### Create Amazon EKS Cluster and Nodes

We can create a cluster with one of the following node types

* [Fargate - Linux](https://docs.aws.amazon.com/AmazonECS/latest/userguide/what-is-fargate.html)
* Managed nodes - Linux

We will create our cluster with Fargate. To learn more about each type, see
[Amazon EKS nodes](https://docs.aws.amazon.com/eks/latest/userguide/eks-compute.html). After your cluster is deployed,
you can add other node types.

Create our Amazon EKS cluster with the following command. We can replace `my-cluster` with our own value. The name can 
contain only alphanumeric characters (case-sensitive) and hyphens. It must start with an alphabetic character and can't
be longer than 100 characters. Replace `region-code` with any AWS Region that is supported by Amazon EKS. For a list of 
AWS Regions, see [Amazon EKS endpoints and quotas](https://docs.aws.amazon.com/general/latest/gr/eks.html) in the AWS 
General Reference guide.

```bash
eksctl create cluster --name my-cluster --region region-code --fargate
```

Cluster creation takes several minutes. During creation you'll see several lines of output. The last line of output is 
similar to the following example line.

```bash
...
[✓]  EKS cluster "my-cluster" in "region-code" region is ready
```

eksctl created a kubectl config file in `~/.kube` or added the new cluster's configuration within an existing config
file in `~/.kube` on our computer.

> **Organizing Cluster Access Using kubeconfig Files**
> 
> A file that is used to configure access to cluster is called a **kubeconfig file**. `kubectl` command-line tool uses 
> this kubeconfig files to find the information it needs to choose a cluster and communicate with the API server of a 
> cluster.
> 
> By default, `kubectl` looks for a file named **config** in the **~/.kube** directory. We can specify other kubeconfig
> files by settings the `KUBECONFIG` environment variable or by setting the `--kubeconfig` flag, though

After cluster creation is complete, view the AWS CloudFormation stack named **eksctl-my-cluster-cluster** in the
[AWS CloudFormation console](https://console.aws.amazon.com/cloudformation) to see all of the resources that were
created.

#### View Kubernetes Resources

To view our cluster nodes.

```bash
kubectl get nodes -o wide
```

The example output is as follows.

```
NAME                                                    STATUS   ROLES    AGE     VERSION              INTERNAL-IP       EXTERNAL-IP   OS-IMAGE         KERNEL-VERSION                  CONTAINER-RUNTIME
fargate-ip-192-168-141-147.region-code.compute.internal Ready    <none>   8m3s    v1.23.7-eks-7c9bda   192.168.141.147   <none>        Amazon Linux 2   5.4.156-83.273.amzn2.x86_64   containerd://1.3.2
fargate-ip-192-168-164-53.region-code.compute.internal  Ready    <none>   7m30s   v1.23.7-eks-7c9bda   192.168.164.53    <none>        Amazon Linux 2   5.4.156-83.273.amzn2.x86_64   containerd://1.3.2
```

To view the workloads running on our cluster:

```bash
$ kubectl get pods -A -o wide

NAMESPACE     NAME                       READY   STATUS    RESTARTS   AGE   IP                NODE                                                      NOMINATED NODE   READINESS GATES
kube-system   coredns-69dfb8f894-9z95l   1/1     Running   0          18m   192.168.164.53    fargate-ip-192-168-164-53.region-code.compute.internal    <none>           <none>
kube-system   coredns-69dfb8f894-c8v66   1/1     Running   0          18m   192.168.141.147   fargate-ip-192-168-141-147.region-code.compute.internal   <none>           <none>
```


Kubernetes Usage Guide
----------------------

Now that we have learned what Kubernetes is and how to deploy an instance, we will then focus on using and managing
various kubernetes resources, such as deploying an application onto it

### Deploy an Application

#### Create a Namespace

A namespace allows us to group resources in Kubernetes. For more information, see [Namespace](#namespace) for more
details. 


[CRI]: https://github.com/kubernetes/community/blob/master/contributors/devel/sig-node/container-runtime-interface.md
[root user tasks]: https://docs.aws.amazon.com/general/latest/gr/root-vs-iam.html#aws_tasks-that-require-root
[create IAM admin]: https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html
[Installing kubectl]: https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html
[Installing eksctl]: https://docs.aws.amazon.com/eks/latest/userguide/eksctl.html
[client libraries]: https://kubernetes.io/docs/reference/using-api/client-libraries/
[custom resources]: https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/