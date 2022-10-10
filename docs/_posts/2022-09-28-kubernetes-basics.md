---
layout: post
title: Kubernetes Basics
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

   Pods allow data sharing and communication among their constituent containers. A Pod can specify a set of **shared
   storage [volumes](#volumes)**. All containers in the Pod can access the shared volumes, allowing those containers to
   **share data**. Volumes also persists data in a Pod to survive in case one of the containers within needs to be
   restarted. Another way of sharing and communicating is through network. Each Pod is assigned a unique IP for each
   address family. **Every container in a Pod shares the network namespace, including the IP address and network ports.
   Inside a Pod, the containers that belong to the Pod communicate with one another using _localhost_. When containers
   in a Pod communicate with entities outside of Pod, they must coordinate how they use the shared network resources
   (such as ports). Within a Pod, containers share an IP address and port space, and can find each other via
   localhost**. The containers in a Pod can also communicate with each other using standard inter-process communications 
   like SystemV semaphores or POSIX shared memory. Containers in different Pods have distinct IP addresses and cannot
   communicate by OS-level IPC without special configuration. Containers that want to interact with a container running
   in a different Pod can use IP networking to communicate. In addition, containers within the Pod see the system
   hostname as being the same as the configured `name` for the Pod. See [Kubernetes Networking](#networking) sections
   to learn more. 

   > The network model is implemented by the container runtime on each node. The most common container runtimes use
   > [Container Network Interface (CNI)](https://github.com/containernetworking/cni) plugins to manage their network and
   > security capabilities. 

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
> effective, that resource will **automatically** create **replacement** Pods that uses the modified template instead of
> updating or patching the existing Pods. 

#### Pod Lifecycle

#### Init Containers

#### Ephemeral Containers

### ReplicaSet

A ReplicaSet's purpose is to maintain a stable set of replica Pods running at any given time. As such, it is often used
to guarantee the availability of a specified number of identical Pods. However, a [Deployment](#deployments) is a 
higher-level resource that manges ReplicaSets and provides declarative updates to Pods along with a log of other useful
features. This means we may never need to manipulate ReplicaSet objects; use a Deployment instead

### Deployments

We describe a _desired state_ in a Deployment and the Deployment [controller](#controllers) changes the actual state to
the desired state at a controlled rate. We can define Deployments to create new ReplicaSets or to remove existing
Deployments and adopt all their resources with new Deployments

For example, the following creates a [ReplicaSet](#replicaset) to bring up 3 nginx Pods

```xml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
```

In this example, a Deployment named "nginx-deployment" is created. The Deployment creates 3 replicated Pods. 

> Note that the `.spec.selector` field defines how the Deployment finds which Pods to manage. In this case, we will
> deploy and manage Pod defined by the `app:nginx` Pod template because its template label is equal to `app:nginx`.

The "template" field contains the following specifications:

* The Pods instantiated out of this template are all labeled "app: nginx"
* The `.template.spec` indicates tha the Pods run only 1 container, which runs the nginx image pulled from
  [Docker Hub](https://hub.docker.com/) at version 1.14.2
* The container crated will have a name of "nginx" defined by the `.spec.template.spec.containers[0].name` field

This Deployment can be created using command (assuming the spec file above is located at
`https://k8s.io/examples/controllers/nginx-deployment.yaml`)

```bash
kubectl apply -f https://k8s.io/examples/controllers/nginx-deployment.yaml
```

Next, we can check if the Deployment was created with

```graphql
kubectl get deployments
```

> ⚠️ We would always want to attach a namespace to the command above to tell kubectl "in which namespace are we trying
> to find that deployment" by using the general form of
> 
> ```bash
> kubectl get deployments -n <namespace>
> ```
> 
> Since the nginx-deployment example above doesn't specify namespace, that deployment gets deployed to default
> namespace, which is why we can simply call `kubectl get deployments` and `kubectl` will simply look at default
> namespace instead. 

If the Deployment is still being created, the output is similar to the following

```
NAME               READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deployment   0/3     0            0           1s
```

* **READY** displays how many replicas of the application are available for use. It follows the pattern "ready/desired".
  In the example output above, we have 0 ready and our desired state would be having 3 ready (because we put 3 for the
  `.spec.replicas` field in the example above)
* **UP-TO-DATE** shows the number of replicas that have been updated to achieve the desired state
* **AVAILABLE** tells how many replicas of the application are available for use
* **AGE** indicates the amount of time that application has been running.

Note that a failed deployment (e.g. error during rollout) will still age the deployment. Hence an `AGE` of 50min does
NOT necessary mean the deployment has been a success; we should check Deployment rollout status instead, using
`kubectl rollout status deployment/nginx-deployment -n <namespace>` to verify whether or not the deployment is properly 
running. The output of this command should be similar to

```
$ kubectl rollout status deployment/nginx-deployment

Waiting for rollout to finish: 2 out of 3 new replicas have been updated...
deployment "nginx-deployment" successfully rolled out
```

Running the `kubectl get deployments` again a few seconds later, we shall see

```
NAME               READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deployment   3/3     3            3           18s
```

Notice that the Deployment has created all three replicas, and all replicas are up-to-date (they contain the latest Pod 
template) and available.

#### Update a Deployment

Let's say we would like to update the deployed resource defined by the example at the beginning of
[Nginx Deployments](#deployments) section by upgrading image version from nginx:1.14.2 to nginx:1.16.1, one of the two
commands below could be used:

```bash
kubectl set image deployment.v1.apps/nginx-deployment nginx=nginx:1.16.1

# or

kubectl set image deployment/nginx-deployment nginx=nginx:1.16.1
```

The output is similar to:

```
deployment.apps/nginx-deployment image updated
```

Alternatively, we can edit the Deployment interactively and change `.spec.template.spec.containers[0].image` from 
`nginx:1.14.2` to `nginx:1.16.1`:

```bash
kubectl edit deployment/nginx-deployment
```

The output is similar to:

```
deployment.apps/nginx-deployment edited
```

To see the rollout status, run:

```bash
$ kubectl rollout status deployment/nginx-deployment

Waiting for rollout to finish: 2 out of 3 new replicas have been updated...
...
deployment "nginx-deployment" successfully rolled out
```




#### Rollback a Deployment

Sometimes, we may want to rollback a Deployment; for example, when the Deployment is not stable, such as crash looping.
By default, all of the Deployment's rollout history is kept in the system so that we can rollback anytime we want.

> 📋  A Deployment's revision is created when a Deployment's rollout is triggered. The new revision is created if and 
> only if the Deployment's Pod template (`.spec.template`) is changed such as updating the labels or container images
> of the template. Other updates, such as scaling the Deployment, do not create a Deployment revision. This means that 
> when we roll back to an earlier revision, only the Deployment's Pod template part is rolled back.




### StatefulSets

### DaemonSet

### Jobs

### CronJob

### Storage

#### Volumes

> On-disk files in a container are ephemeral, which presents some problems for non-trivial applications when running in
> containers. One problem is the loss of files when a container crashes, because kubelet restarts the container with a
> clean state. A second problem occurs when sharing files between containers running together in a Pod. The Kubernetes
> volume abstraction solves both of these problems. 

Kubernetes supports many types of volumes. A Pod can use any number of volume types simultaneously. **Ephemeral volume**
types have a lifetime of a Pod, but **persistent volumes** exist beyond the lifetime of a Pod. When a Pod is removed,
Kubernetes destroys ephemeral volumes but not persistent volumes. For all types of volumes in a given Pod, however, data
is preserved across container restarts.

At its core, a volume is a directory, possibly with some data in it, which is accessible to the containers in a Pod. How
that directory comes to be, the medium that backs it, and the contents of it are determined by the particular volume
type used.

To use a volume, specify the volumes to provide for a Pod in `.spec.volumes` and declare where to mount those volumes
into containers in `.spec.containers[*].volumeMounts`. A process in a container sees a filesystem view composed of
the initial contents of the container image and volumes (if defined) mounted onto the container. _Volumes mount to a
specified paths within the image_. For each container defined within a Pod, we must independently specify where to
mount each volume that the container uses.

##### Types of Volumes

###### cdphfs

A cephfs volume allows an existing CephFS volume to be mounted onto our Pod. When a Pod is removed, the data in the
cephfs volume are preserved and the volume is merely unmounted. This means that a cephfs volume can be pre-populated
with data, and that data can be shared among Pods. The cephfs volume can be mounted by multiple writers simultaneously

> We must have our own Ceph server running before we can use it this way

See the [CephFS example](https://github.com/kubernetes/examples/tree/master/volumes/cephfs/) for more details

###### configMap

### Networking

https://kubernetes.io/docs/concepts/cluster-administration/networking/

### Namespace

In Kubernetes, _namespaces_ provides a mechanism for isolating groups of resources within a single cluster. Names of
resources need to be unique within a namespace, but not across namespaces. Namespace-based scoping is applicable only
for namespaced objects (e.g. Deployments, Services, etc) and not for cluster-wide objects (e.g. StorageClass, Nodes,
PersistentVolumesn, etc)

Namespaces are intended for use in environments with many users spread across multiple teams, or porjects. For clusters
with a few to tens of users, we should not need to create or think about namespaces at all.

Namespaces provide a scope for names. Names of resources need to be unique within a namespace, but not across
namespaces. Namespaces cannot be nested inside one another and each Kubernetes resource can only be in one namespace.

Namespaces are a way to divide cluster resources between multiple users vis
[resource quota](https://kubernetes.io/docs/concepts/policy/resource-quotas/)

It is not necessary to use multiple namespaces to separate slightly different resources, such as different versions of
the same software: use labels to distinguish resources within the same namespace. 

We can list the current namespaces in a cluster using:

```bash
$ kubectl get namespace

NAME              STATUS   AGE
default           Active   1d
kube-node-lease   Active   1d
kube-public       Active   1d
kube-system       Active   1d
```

Kubernetes starts with 4 initial namespaces:

1. **default** The default namespace for objects with namespace not specified
2. **kube-system** The namespace for objects created by the Kubernetes system
3. **kube-public** This namespace is readable by all users (including those not authenticated). This namespace is mostly
   reserved for cluster usage, in cases that some resources should be visible and readable publicly throughout the whole
   cluster. The public aspect of this namespace is only a convention, not a requirement
4. **kube-node-lease** This namespace holds
   [Lease](https://kubernetes.io/docs/reference/kubernetes-api/cluster-resources/lease-v1/) objects associated with each
   node. Node leases allow the kubelet to send heartbeats so that control plane can detect node failure

> 📋 **Heartbeats**, sent by Kubernetes nodes, help our cluster to determine the availability of each node, and to take
> action when failures are detected.

Each request kubectl command should be attached with a namespace unless the namespace is `default` using
`-n` or `--namespace` flat. For example

```bash
kubectl get pods -n <insert-namespace-name-here>
kubectl run nginx --image=nginx --namespace <insert-namespace-name-here>
```

We can permanently save the namespace for all subsequent kubectl commands in that context.

```bash
kubectl config set-context --current --namespace=<insert-namespace-name-here>
# Validate it
kubectl config view --minify | grep namespace:
```


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

### Creating a New Kubernetes Cluster

This section documents how to create all of the required resources to get started with Amazon Elastic Kubernetes Service 
(Amazon EKS) using eksctl, a simple command line utility for creating and managing Kubernetes clusters on Amazon EKS. At 
the end of this section, we will have a running Amazon EKS cluster that we can deploy applications to.

Before we start, we must install and configure the following tools and resources that we need to create and manage an 
Amazon EKS cluster.

* **kubectl** A command line tool for working with Kubernetes clusters. This guide requires that you use version 1.23 or later. For more information, see [Installing kubectl][Installing kubectl].
* **eksctl** A command line tool for working with EKS clusters that automates many individual tasks. We use version 
  0.112.0 or later. For more information, see [Installing eksctl][Installing eksctl].
* Required [**IAM permissions**](https://qubitpi.github.io/jersey-guide/finalized/2022/10/09/startup-auth.html#aws-identity-and-access-management-iam) The IAM security principal that we're using
  must have permissions to work with Amazon EKS IAM roles and service linked roles, AWS CloudFormation, and a VPC and 
  related resources. Okay, to put it simple, we will create a admin user that will cover all of these permissions

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

### Deleting an Amazon EKS Cluster

Please refer to https://docs.aws.amazon.com/eks/latest/userguide/delete-cluster.html for the step-by-step instructions

Kubernetes Usage Guide
----------------------

Now that we have learned what Kubernetes is and how to deploy an instance, we will then focus on using and managing
various kubernetes resources, such as deploying an application onto it and managing application efficiently

### Helm Charts

#### Charts

[Helm](https://helm.sh/) uses a packaging format called _charts_. **A chart is a collection of files that describe a
related set of Kubernetes resources**. A single chart might be used to deploy something simple, like a memcached pod, or
something complex, like a full web app stack with HTTP servers, databases, caches, and so on.

**Charts are created as files laid out in a particular directory tree**. They can be packaged into versioned archives to
be deployed.

If you want to download and look at the files for a published chart, without installing it, you can do so with

```bash
helm pull chartrepo/chartname
```

This section explains the chart format, and provides basic guidance for building charts with Helm.

##### The Chart File Structure

A chart is organized as a collection of files inside of a directory. **The directory name is the name of the chart**
(_without versioning information_). Thus, a chart describing WordPress would be stored in a "wordpress/" directory. In 
this directory, Helm will expect a structure that matches this:

```
wordpress/
  Chart.yaml          # A YAML file containing information about the chart
  LICENSE             # OPTIONAL: A plain text file containing the license for the chart
  README.md           # OPTIONAL: A human-readable README file
  values.yaml         # The default configuration values for this chart
  values.schema.json  # OPTIONAL: A JSON Schema for imposing a structure on the values.yaml file
  charts/             # A directory containing any charts upon which this chart depends.
  crds/               # Custom Resource Definitions
  templates/          # A directory of templates that, when combined with values,
                      # will generate valid Kubernetes manifest files.
  templates/NOTES.txt # OPTIONAL: A plain text file containing short usage notes
```

Helm reserves use of the `charts/`, `crds/`, and `templates/` directories, and of the listed file names. Other files
will be left as they are.

##### The Chart.yaml File

The `Chart.yaml` file is required for a chart. It contains the following fields:

```yaml
apiVersion: The chart API version (required)
name: The name of the chart (required)
version: A SemVer (https://semver.org/spec/v2.0.0.html) 2 version (required)
kubeVersion: A SemVer range of compatible Kubernetes versions (optional)
description: A single-sentence description of this project (optional)
type: The type of the chart (optional)
keywords:
  - A list of keywords about this project (optional)
home: The URL of this projects home page (optional)
sources:
  - A list of URLs to source code for this project (optional)
dependencies: # A list of the chart requirements (optional)
  - name: The name of the chart (nginx)
    version: The version of the chart ("1.2.3")
    repository: (optional) The repository URL ("https://example.com/charts") or alias ("@repo-name")
    condition: (optional) A yaml path that resolves to a boolean, used for enabling/disabling charts (e.g.subchart1.enabled )
    tags: # (optional)
      - Tags can be used to group charts for enabling/disabling together
    import-values: # (optional)
      - ImportValues holds the mapping of source values to parent key to be imported. Each item can be a string or pair of child/parent sublist items.
    alias: (optional) Alias to be used for the chart. Useful when we have to add the same chart multiple times
maintainers: # (optional)
    - name: The maintainers name (required for each maintainer)
      email: The maintainers email (optional for each maintainer)
      url: A URL for the maintainer (optional for each maintainer)
icon: A URL to an SVG or PNG image to be used as an icon (optional).
appVersion: The version of the app that this contains (optional). Needn't be SemVer. Quotes recommended.
deprecated: Whether this chart is deprecated (optional, boolean)
annotations:
    example: A list of annotations keyed by name (optional).
```

As of "v3.3.2", additional fields are not allowed. The recommended approach is to add custom metadata in `annotations`.

###### Charts and Versioning

Every chart must have a version number. A version must follow the
[SemVer 2 standard](https://semver.org/spec/v2.0.0.html). Helm uses version numbers as release markers. Packages in 
repositories are identified by name plus version.

For example, a nginx chart whose version field is set to `version: 1.2.3` will be named "nginx-1.2.3.tgz"

More complex SemVer 2 names are also supported, such as `version: 1.2.3-alpha.1+ef365`. But non-SemVer names are
explicitly disallowed by the system.

The `version` field in `Chart.yaml` is used by many of the Helm tools, including the CLI. When generating a package, the 
helm package command will use this version as a token in the package name. The system assumes that the version number in 
the chart package name matches the version number in the `Chart.yaml`. Failure to meet this assumption will cause an
error.

###### The "apiVersion" Field

The `apiVersion` field should always be "**v2**" for Helm charts that require at least Helm 3. Charts supporting
previous Helm versions have an apiVersion set to "v1" and are still installable by Helm 3.

###### The "kubeVersion" Field

The optional `kubeVersion` field can define semver constraints on supported Kubernetes versions. Helm will validate the
version constraints when installing the chart and fail if the cluster runs an unsupported Kubernetes version.

Version constraints may comprise space separated AND comparisons such as

```
>= 1.13.0 < 1.15.0
```

which themselves can be combined with the OR `||` operator like in the following example

```bash
>= 1.13.0 < 1.14.0 || >= 1.14.1 < 1.15.0
```

In this example the version 1.14.0 is excluded, which can make sense if a bug in certain versions is known to prevent
the chart from running properly.

Apart from version constrains employing operators `=` `!=` `>` `<` `>=` `<=` the following shorthand notations are
supported

* hyphen ranges for closed intervals, where `1.1 - 2.3.4` is equivalent to `>= 1.1 <= 2.3.4`.
* wildcards `x`, `X` and `*`, where `1.2.x` is equivalent to `>= 1.2.0 < 1.3.0`.
* tilde ranges (patch version changes allowed), where `~1.2.3` is equivalent to `>= 1.2.3 < 1.3.0`
* caret ranges (minor version changes allowed), where `^1.2.3` is equivalent to `>= 1.2.3 < 2.0.0`.

For a detailed explanation of supported semver constraints see
[Masterminds/semver](https://github.com/Masterminds/semver).

###### Deprecating Charts

When _managing charts in a Chart Repository_, it is sometimes necessary to deprecate a chart. The optional `deprecated`
field in `Chart.yaml` can be used to mark a chart as deprecated. If the latest version of a chart in the repository is
marked as deprecated, then the chart as a whole is considered to be deprecated. The chart name can be later reused by
publishing a newer version that is not marked as deprecated.

1. Update chart's "Chart.yaml" to mark the chart as deprecated, bumping the version
2. Release the new chart version in the Chart Repository
3. Release the new chart version in the Chart Repository

###### Chart Types

The `type` field defines the type of chart. There are two types

1. application, and
2. library

**Application** is the default type and it is the standard chart which can be operated on fully. The
[library chart](#library-charts) provides utilities or functions for the chart builder. A library chart differs from an 
application chart because it is not installable and usually doesn't contain any resource objects.

> 📋 An application chart can be used as a library chart. This is enabled by setting the type to "library". The chart 
> will then be rendered as a library chart where all utilities and functions can be leveraged. All resource objects of 
> the chart will not be rendered.

##### Chart LICENSE, README and NOTES

Charts can also contain files that describe the installation, configuration, usage and license of a chart.

A README for a chart should be formatted in Markdown (README.md), and should generally contain:

* A description of the application or service the chart provides
* Any prerequisites or requirements to run the chart
* Descriptions of options in `values.yaml` and default values
* Any other information that may be relevant to the installation or configuration of the chart

When hubs and other user interfaces display details about a chart that detail is pulled from the content in the
README.md file.

The chart can also contain a short plain text "templates/NOTES.txt" file that will be printed out after installation,
and when viewing the status of a release. This file is evaluated as a [template](#templates-and-values), and can be used
to display usage notes, next steps, or any other information relevant to a release of the chart. For example,
instructions could be provided for connecting to a database, or accessing a web UI. Since this file is printed to STDOUT
when running `helm install` or `helm status`, it is recommended to keep the content brief and point to the README for
greater detail.

##### Chart Dependencies

In Helm, one chart may depend on any number of other charts. These dependencies can be dynamically linked using the
`dependencies` field in `Chart.yaml` or brought in to the `charts/` directory and managed manually.

###### Managing Dependencies with the "dependencies" Field

The charts required by the current chart are defined as a list in the `dependencies` field. For example

```yaml
dependencies:
  - name: apache
    version: 1.2.3
    repository: https://example.com/charts
  - name: mysql
    version: 3.2.1
    repository: https://another.example.com/charts
```

> ⚠️ Note that we must also use `helm repo add` to add that repo locally using
> 
> ```bash
> $ helm repo add fantastic-charts https://fantastic-charts.storage.googleapis.com
> ```
> 
> With that we shall use the name of the repo instead of URL in dependency declaration like this
> 
> ```yaml
> dependencies:
>   - name: awesomeness
>     version: 1.0.0
>     repository: "@fantastic-charts"
> ```

Once we have defined dependencies, we can run `helm dependency update` and it will use our dependency file to
download all the specified charts into our `charts/` directory for us.

```bash
$ helm dep up foochart
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "local" chart repository
...Successfully got an update from the "stable" chart repository
...Successfully got an update from the "example" chart repository
...Successfully got an update from the "another" chart repository
Update Complete. Happy Helming!
Saving 2 charts
Downloading apache from repo https://example.com/charts
Downloading mysql from repo https://another.example.com/charts
```

When `helm dependency update` retrieves charts, it will store them as chart archives in the `charts/` directory. For the 
example above, we would expect to see the following files in the charts directory:

```
charts/
  apache-1.2.3.tgz
  mysql-3.2.1.tgz
```

Each denpendency entry may contain an optional field called **alias**. Adding an alias for a dependency chart would put
a chart in dependencies using alias as name of new dependency.

One can use alias in cases where they need to access a chart with other name(s).

```yaml
# parentchart/Chart.yaml

dependencies:
    - name: subchart
      repository: http://localhost:10191
      version: 0.1.0
      alias: new-subchart-1
    - name: subchart
      repository: http://localhost:10191
      version: 0.2.0
      alias: new-subchart-2
    - name: subchart
      repository: http://localhost:10191
      version: 0.3.0
```

From the example above we will refer to 3 dependencies in parent chart as "subchart", "new-subchart-1", and
"new-subchart-2", respectively

The last pair of optional fields are **tags** and **condition**. All charts are loaded by default. If `tags` or 
`condition` fields are present, they will be evaluated and used to control loading for the chart(s) they are applied to.

* **Condition** - The condition field holds one or more YAML paths (delimited by commas). If this path exists in the top
  parent's values and resolves to a boolean value, the chart will be enabled or disabled based on that boolean value.
  Only the first valid path found in the list is evaluated and if no paths exist then the condition has no effect.
* **Tags** - The tags field is a YAML list of labels to associate with this chart. In the top parent's values, all
  charts with tags can be enabled or disabled by assigning the tag with a boolean value.

For example, 

<table>
<tr>
<th>parentchart/Chart.yaml</th>
<th>parentchart/values.yaml</th>
</tr>

<tr>
<td>

{% highlight yaml %}
dependencies:
- name: subchart1
  repository: http://localhost:10191
  version: 0.1.0
  condition: subchart1.enabled, global.subchart1.enabled
  tags:
    - front-end
    - subchart1
- name: subchart2
  repository: http://localhost:10191
  version: 0.2.0
  condition: subchart2.enabled,global.subchart2.enabled
  tags:
    - back-end
    - subchart2
{% endhighlight %}

</td>

<td>

{% highlight yaml %}
subchart1:
  enabled: true
tags:
  front-end: false
  back-end: true
{% endhighlight %}

</td>
</tr>
</table>

In the above example all charts with the tag `front-end` would be disabled but since the `subchart1.enabled` path
evaluates to `true` in the parent's values, the condition will override the front-end tag and `subchart1` will be
enabled.

Since `subchart2` is tagged with `back-end` and that tag evaluates to `true`, `subchart2` will be enabled. Also note
that although `subchart2` has a condition specified, there is no corresponding path and value in the parent's values so
that condition has no effect.

The **`--set` parameter** can be used as usual to alter tag and condition values. For example

```
helm install --set tags.front-end=true --set subchart2.enabled=false
```

> **Tags and Condition Resolution**
> 
> * Conditions (when set in values) always override tags. The first condition path that exists wins and subsequent ones
>   for that chart are ignored.
> * Tags are evaluated as 'if any of the chart's tags are true then enable the chart'.
> * Tags and conditions values must be set in the top parent's values.
> * The `tags:` key in values must be a top level key. Globals and nested `tags:` tables are currently not supported.

In some cases it is desirable to allow a child chart's values to propagate to the parent chart and be shared as common
defaults. An additional benefit of using the exports format is that it will enable future tooling to introspect
user-settable values.

The keys containing the values to be imported can be specified in the parent chart's `dependencies` in the field
**import-values** using a YAML list. Each item in the list is a key which is imported from the child chart's **exports**
field.

If a child chart's values.yaml file contains an `exports` field at the root, its contents may be imported directly into
the parent's values by specifying the keys to import as in the example below:

<table>
<tr>
<th>parent's Chart.yaml file</th>
<th>child's values.yaml file</th>
</tr>

<tr>
<td>

{% highlight yaml %}
dependencies:
- name: subchart
  repository: http://localhost:10191
  version: 0.1.0
  import-values:
    - data
{% endhighlight %}

</td>
<td>

{% highlight yaml %}
exports:
  data:
    myint: 99
{% endhighlight %}

</td>
</tr>
</table>

The final parent values would contain our exported field:

```yaml
# parent's values

myint: 99
```

Note that the parent key `data` is not included in the parent's final values. 

Another approach is to specify the source key of the values to be imported (`child`) and the destination path in the 
parent chart's values (`parent`).

The `import-values` in the example below instructs Helm to take any values found at `child:` path and copy them to the
parent's values at the path specified in `parent`. For example

<table>
<tr>
<th>parent's Chart.yaml file</th>
<th>subchart1's values.yaml file</th>
</tr>

<tr>
<td>

{% highlight yaml %}
dependencies:
- name: subchart1
  repository: http://localhost:10191
  version: 0.1.0
  ...
  import-values:
    - child: default.data
      parent: myimports
{% endhighlight %}

</td>

<td>

{% highlight yaml %}
default:
  data:
    myint: 999
    mybool: true
{% endhighlight %}

</td>
</tr>
</table>

In the example above, values found at `default.data` in the subchart1's values will be imported to the `myimports` key
in the parent chart's values initialized as follows:

```yaml
# parent's final values

myimports:
    myint: 999
    mybool: true
    mystring: "helm rocks!"
```

The parent's final values now contains the `myint` and `mybool` fields imported from `subchart1`

###### Managing Dependencies Manually through the charts/ Directory

If _more control over dependencies_ is desired, these dependencies can be expressed explicitly by copying the dependency
charts into the "charts/" directory.

A dependency should be an _unpacked_ chart directory but its name cannot start with `_` or `.`. Such files are ignored
by the chart loader.

For example, if the WordPress chart depends on the Apache chart, the Apache chart (of the correct version) is supplied
in the WordPress chart's `charts/` directory:

```yaml
wordpress:
    Chart.yaml
    # ...
    charts/
        apache/
            Chart.yaml
            # ...
        mysql/
            Chart.yaml
            # ...
```

The example above shows how the WordPress chart expresses its dependency on Apache and MySQL by including those charts
inside of its `charts/` directory.

> 💡 To drop a dependency into your charts/ directory, use the helm pull command

###### Operational Aspects of using Dependencies

How does chart dependencies affect chart installation using `helm install` and `helm upgrade`?

Suppose that a chart named "A" creates the following Kubernetes objects

* namespace "A-Namespace"
* statefulset "A-StatefulSet"
* service "A-Service"

Furthermore, A is dependent on chart B that creates objects

* namespace "B-Namespace"
* replicaset "B-ReplicaSet"
* service "B-Service"

After installation/upgrade of chart A a single Helm release is created/modified. The release will create/update all of
the above Kubernetes objects in the following order:

* A-Namespace
* B-Namespace
* A-Service
* B-Service
* B-ReplicaSet
* A-StatefulSet

This is because when Helm installs/upgrades charts, the Kubernetes objects from the charts and all its dependencies are

* aggregated into a single set; then
* sorted by type followed by name; and then
* created/updated in that order.

Hence a single release is created with all the objects for the chart and its dependencies.

##### Templates and Values

Helm Chart templates are written in the [Go template language](https://golang.org/pkg/text/template/), with the addition
of 50 or so add-on template functions from the [Sprig library](https://github.com/Masterminds/sprig) and a few other
[specialized functions](https://helm.sh/docs/howto/charts_tips_and_tricks/).

All template files are stored in a chart's "templates/" folder. When Helm renders the charts, it will pass every file in
that directory through the template engine.

**Values for the templates are supplied two ways**:

1. [**Compile-Time**] Chart developers may supply a file called "values.yaml" in a chart. This file contains default 
   values.
2. [**Run-Time**] Chart users may supply a YAML file that contains values. This can be provided on the command line with
   `helm install`.

When a user supplies custom values, these values will override the values in the chart's "values.yaml" file.

###### Template Files

Template files follow the standard conventions for writing Go templates (see
[the text/template Go package documentation](https://golang.org/pkg/text/template/) for details). An example template
file might look something like this:

```yaml
apiVersion: v1
kind: ReplicationController
metadata:
  name: deis-database
  namespace: deis
  labels:
    app.kubernetes.io/managed-by: deis
spec:
  replicas: 1
  selector:
    app.kubernetes.io/name: deis-database
  template:
    metadata:
      labels:
        app.kubernetes.io/name: deis-database
    spec:
      serviceAccount: deis-database
      containers:
        - name: deis-database
          image: {{ .Values.imageRegistry }}/postgres:{{ .Values.dockerTag }}
          imagePullPolicy: {{ .Values.pullPolicy }}
          ports:
            - containerPort: 5432
          env:
            - name: DATABASE_STORAGE
              value: {{ default "minio" .Values.storage }}
```

> The `DATABASE_STORAGE` specified this way is the equivalent of `docker run -e DATABASE_STORAGE=minio`

The example above is a template for a Kubernetes replication controller. It uses the following 4 template values
(usually defined in a "values.yaml" file):

1. **`imageRegistry`**: The source registry for the Docker image.
2. **`dockerTag`**: The tag for the docker image.
3. **`pullPolicy`**: The Kubernetes pull policy.
4. **`storage`**: The storage backend, whose default is set to "minio"

To see many working charts, check out the CNCF [Artifact Hub](https://artifacthub.io/packages/search?kind=0).

###### Predefined Values

Values that are supplied via a "values.yaml" file (or via the `--set` flag) are accessible from the `.Values` object in
a template. But there are other pre-defined pieces of data you can access in your templates.

The following values are pre-defined and are available to every template; they cannot be overridden. As with all values,
the names are _case sensitive_.

* **`Release.Name`**: The name of the release (not the chart)
* **`Release.Namespace`**: The namespace the chart was released to.
* **`Release.Service`**: The service that conducted the release.
* **`Release.IsUpgrade`**: This is set to true if the current operation is an upgrade or rollback.
* **`Release.IsInstall`**: This is set to true if the current operation is an install.
* **`Chart`**: The contents of the `Chart.yaml`. Thus, the chart version is obtainable as `Chart.Version` and the
  maintainers are in `Chart.Maintainers`
* **`Files`**: A map-like object containing all non-special files in the chart. This will not give you access to
  templates, but will give you access to additional files that are present (unless they are excluded using
  `.helmignore`). Files can be accessed using `{{ index .Files "file.name" }}` or using the `{{.Files.Get name }}`
  function. You can also access the contents of the file as `[]byte` using `{{ .Files.GetBytes }}`
* **`Capabilities`**: A map-like object that contains information about the versions of Kubernetes
  (`{{ .Capabilities.KubeVersion }}`) and the supported Kubernetes API versions
  (`{{ .Capabilities.APIVersions.Has "batch/v1" }}`)

> ⚠️ Any unknown `Chart.yaml` fields will be dropped. They will not be accessible in the Chart object. Thus,
> "Chart.yaml" cannot be used to pass arbitrarily structured data into the template. The values file can be used for
> that, though.

###### Values Files

Considering the template in the [previous section](#template-files), a `values.yaml` file that supplies the necessary 
values would look like this:

```yaml
imageRegistry: "quay.io/deis"
dockerTag: "latest"
pullPolicy: "Always"
storage: "swift"
```

A values file is formatted in YAML. A chart may include a default `values.yaml` file. The Helm install command allows a
user to override values by supplying additional YAML values. For example

```bash
$ helm install --generate-name --values=myvals.yaml wordpress
```

When values are passed in this way, they will be merged into the default values file. For example, consider a
`myvals.yaml` file that looks like this:

```yaml
storage: "gcs"
```

When this is merged with the "values.yaml" in the chart, the resulting generated content will be:

```yaml
imageRegistry: "quay.io/deis"
dockerTag: "latest"
pullPolicy: "Always"
storage: "gcs"
```

###### Scope, Dependencies, and Values

Values files can declare values for the top-level chart, as well as for any of the charts that are included in that
chart's "charts/" directory. Or, to put it differently, **a values file can supply values to the chart as well as to any
of its dependencies**. For example, the demonstration WordPress chart above has both `mysql` and `apache` as dependencies.
The values file could supply values to all of these components

Charts at a higher level have access to all of the variables defined beneath. So the WordPress chart can access the
MySQL password as `.Values.mysql.password`. But lower level charts cannot access things in parent charts, so MySQL will
not be able to access the `title` property. Nor, for that matter, can it access `apache.port`.

Values are namespaced, but namespaces are pruned. So for the WordPress chart, it can access the MySQL password field as
`.Values.mysql.password`. But for the MySQL chart, the scope of the values has been reduced and the namespace prefix
removed, so it will see the password field simply as `.Values.password`.

###### Global Values

As of 2.0.0-Alpha.2, Helm supports special "global" value. For example:

```yaml
# parent chart values.yaml file

global:
  app: MyWordPress
```

The above adds a `global` section with the value `app: MyWordPress`. This value is available to all charts as
`.Values.global.app`. For example, the mysql templates may access app as `{{ .Values.global.app}}`, and so can the
apache chart. This provides a way of sharing one top-level variable with all subcharts, which is useful for things like 
setting `metadata` properties like labels.

If a subchart declares a global variable, that global will be passed _downward_ (to the subchart's subcharts), `not
upward_ to the parent chart. There is no way for a subchart to influence the values of the parent chart. In addition, 
global variables of parent charts take precedence over the global variables from subcharts.

###### Schema Files

Sometimes, a chart maintainer might want to define a structure on their values. This can be done by defining a schema in
the `values.schema.json` file. A schema is represented as a [JSON Schema](https://json-schema.org/). It might look
something like this:

```json
{
    "$schema": "https://json-schema.org/draft-07/schema#",
    "properties": {
        "image": {
            "description": "Container Image",
            "properties": {
                "repo": {
                    "type": "string"
                },
                "tag": {
                    "type": "string"
                }
            },
            "type": "object"
        },
        "name": {
            "description": "Service name",
            "type": "string"
        },
        "port": {
            "description": "Port",
            "minimum": 0,
            "type": "integer"
        },
        "protocol": {
            "type": "string"
        }
    },
    "required": [
        "protocol",
        "port"
    ],
    "title": "Values",
    "type": "object"
}
```

This schema will be applied to the values to validate it. Validation occurs when any of the following commands are
invoked:

* `helm install`
* `helm upgrade`
* `helm lint`
* `helm template`

An example of a `values.yaml` file that meets the requirements of this schema might look something like this:

```yaml
name: frontend
protocol: https
port: 443
```

**Note that the schema is applied to the final `.Values` object**, and not just to the `values.yaml` file. This means
that the following yaml file is valid, given that the chart is installed with the appropriate `--set` option shown
below.

```yaml
name: frontend
protocol: https
```

```bash
helm install --set port=443
```

Furthermore, the final `.Values` object is checked against all subchart schemas. This means that restrictions on a
subchart can't be circumvented by a parent chart. This also works backwards - if a subchart has a requirement that is
not met in the subchart's `values.yaml` file, the parent chart must satisfy those restrictions in order to be valid.

##### Custom Resource Definitions (CRDs)

Kubernetes provides a mechanism for declaring new types of Kubernetes objects. Using CustomResourceDefinitions (CRDs),
Kubernetes developers can declare custom resource types.

In Helm 3, CRDs are treated as a special kind of object. They are installed before the rest of the chart, and are
subject to some limitations.

CRD YAML files should be placed in the `crds/` directory inside of a chart. Multiple CRDs (separated by YAML start and
end markers) may be placed in the same file. Helm will attempt to load all of the files in the CRD directory into
Kubernetes.

CRD files cannot be templated. They must be plain YAML documents.

When Helm installs a new chart, it will upload the CRDs, pause until the CRDs are made available by the API server, and
then start the template engine, render the rest of the chart, and upload it to Kubernetes. Because of this ordering, CRD
information is available in the `.Capabilities` object in Helm templates, and Helm templates may create new instances of
objects that were declared in CRDs.

For example, if your chart had a CRD for CronTab in the `crds/` directory, you may create instances of the CronTab kind
in the `templates/` directory:

```
crontabs/
  Chart.yaml
  crds/
    crontab.yaml
  templates/
    mycrontab.yaml
```

The `crontab.yaml` file must contain the CRD with no template directives:

```yaml
kind: CustomResourceDefinition
metadata:
    name: crontabs.stable.example.com
spec:
    group: stable.example.com
    versions:
        - name: v1
          served: true
          storage: true
    scope: Namespaced
    names:
        plural: crontabs
        singular: crontab
        kind: CronTab

```

Then the template `mycrontab.yaml` may create a new `CronTab` (using templates as usual):

```yaml
apiVersion: stable.example.com
kind: CronTab
metadata:
    name: {{ .Values.name }}
spec:
    # ...
```

Helm will make sure that the `CronTab` kind has been installed and is available from the Kubernetes API server before it
proceeds installing the things in `templates/`.

###### Limitations on CRDs

Unlike most objects in Kubernetes, CRDs are installed globally. For that reason, Helm takes a very cautious approach in
managing CRDs. CRDs are subject to the following limitations:

* CRDs are never reinstalled. If Helm determines that the CRDs in the `crds/` directory are already present (regardless
  of version), Helm will not attempt to install or upgrade.
* CRDs are never installed on upgrade or rollback. **Helm will only create CRDs on installation operations**.
* CRDs are never deleted. Deleting a CRD automatically deletes all of the CRD's contents across all namespaces in the
  cluster. Consequently, Helm will not delete CRDs.

Operators who want to upgrade or delete CRDs are encouraged to do this manually and with great care.

##### Using Helm to Manage Charts

The `helm` tool has several commands for working with charts.

It can create a new chart for you:

```bash
$ helm create mychart
Created mychart/
```

Once you have edited a chart, helm can package it into a chart archive for you:

```bash
$ helm package mychart
Archived mychart-0.1.-.tgz
```

You can also use helm to help you find issues with your chart's formatting or information:

```bash
$ helm lint mychart
No issues found
```

#### Chart Hooks

#### Chart Tests

#### Library Charts

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
