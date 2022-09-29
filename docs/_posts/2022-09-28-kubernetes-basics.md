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


Kubernetes Components
---------------------

When you deploy Kubernetes, you get a cluster.

A Kubernetes cluster consists of a set of worker machines, called **nodes**, that run containerized applications. Every 
cluster has at least one worker node. The worker node(s) host the **Pods** that are the components of the application 
workload. The **control plane** manages the worker nodes and the Pods in the cluster. In production environments, the 
control plane usually runs across multiple computers and a cluster usually runs multiple nodes, providing
fault-tolerance and high availability.

![Error loading components-of-kubernetes.svg]({{ "/assets/img/components-of-kubernetes.svg" | relative_url}})

### Control Plane

The control plane's components make global decisions about the cluster (for example, scheduling), as well as detecting
and responding to cluster events (for example, starting up a new pod when a deployment's `replicas` field is
unsatisfied).

#### API server

The **API server** is a component of the Kubernetes control plane that exposes the Kubernetes API. The API server is the 
front end for the Kubernetes control plane. API server is designed to scale horizontally. We can run several instances
of API server and balance traffic between those instances.

#### etcd Data Store

Consistent and highly-available key value store used as Kubernetes' backing store for all cluster data.

#### Scheduler

Control plane component that watches for newly created Pods with no assigned node, and selects a node for them to run on.

#### Control Manager

Control plane component that runs controller processes.

> A controller runs a control loop that watches the shared state of a cluster through API server and makes changes
> attempting to move the current state toward the desired state

Logically, each controller is a separate process, but to reduce complexity, they are all compiled into a single binary
and run in a single process.

#### Cloud Control Manager

A Kubernetes control plane component that embeds cloud-specific control logic. The cloud controller manager lets you link our cluster into our cloud provider's API, and separates out the components that interact with that cloud platform from components that only interact with our cluster.

The cloud control manager only runs controllers that are specific to a cloud provider. If we are running Kubernetes on our own premises, or in a learning environment inside our own PC, the cluster does not have a cloud controller manager.

As with the [Kubernetes' control manager](#control-manager), the cloud control manager combines several logically independent control loops into a single binary that we run as a single process. We can scale horizontally (run more than one copy) to improve performance or to help tolerate failures.

Some [Kubernetes' control manager](#control-manager) controllers do depend on cloud control manager, such as

* Node controller: For checking the cloud provider to determine if a node has been deleted in the cloud after it stops responding
* Route controller: For setting up routes in the underlying cloud infrastructure
* Service controller: For creating, updating and deleting cloud provider load balancers

### Node

#### kubelet

An agent that runs on each node in cluster and makes sure that containers are running in a Pod.

The kubelet takes a set of PodSpecs that are provided through various mechanisms and ensures that the containers
described in those PodSpecs are running and healthy. The kubelet doesn't manage containers which were not created by 
Kubernetes.

#### kube-proxy

kube-proxy is a network proxy that runs on each node in cluster, implementing part of the Kubernetes Service concept.

kube-proxy maintains network rules on nodes. These network rules allow network communication to our Pods from network 
sessions inside or outside of our cluster.

kube-proxy uses the operating system packet filtering layer if there is an available one. Otherwise, kube-proxy forwards 
the traffic itself.

#### Container Runtime

The container runtime is the software that is responsible for running containers. Kubernetes supports container runtimes 
such as [containerd](https://containerd.io/docs/), [CRI-O](https://cri-o.io/#what-is-cri-o), and any other
implementation of the
[Kubernetes CRI (Container Runtime Interface)](https://github.com/kubernetes/community/blob/master/contributors/devel/sig-node/container-runtime-interface.md).


Kubernetes on AWS (EKS)
-----------------------

Amazon Elastic Kubernetes Service (Amazon EKS) is a managed service that you can use to run Kubernetes on AWS without 
needing to install, operate, and maintain your own Kubernetes control plane or nodes.

### Why Do We Use EKS?

Wh do we care about spinning up an EKS cluster on Amazon? Why not choose to create our own Kubernetes cluster?

Bootstrapping a Kubernetes cluster involves securing and managing our application, plus cluster, networking, and storage 
configuration. On top of this, Kubernetes maintenance involves upgrades to the cluster, the underlying operating system, 
and much more. Using AWS' managed Kubernetes service, EKS, will ensure that your cluster is configured correctly and
gets updates and patches on time.

AWS' EKS works out-of-the-box with the rest of Amazon's infrastructure. Elastic Load Balancers (ELB) are used to expose 
services to the outside world. Your cluster uses Elastic Block Storage (EBS) to store persistent data. Amazon ensures
that the data is online and available to your cluster.

Amazon EKS provides far better scalability than self-hosted Kubernetes. The control plane makes sure that pods are 
launched across multiple physical nodes. If any of the nodes go down, application will still be online. But if we manage 
our own cluster, we will have to ensure that different VMs (EC2 instances) are on different availability zones. If we 
can't guarantee that, then running different pods on the same physical server won't bring much fault tolerance.


### Create a New Kubernetes Cluster

#### Installing eksctl

[eksctl](https://eksctl.io/) is a simple command line tool for creating and managing Kubernetes clusters on Amazon EKS. 
It provides the fastest and easiest way to create a new cluster with nodes for Amazon EKS.

To install eksctl on Mac using Homebrew

```bash
brew tap weaveworks/tap
brew install weaveworks/tap/eksctl
```

> A common homebrew issue is when we execute `brew install` or `brew tap`, we receive the error like the following:
> 
> ```bash
> brew tap weaveworks/tap
> Running `brew update --auto-update`...
> ==> Tapping weaveworks/tap
> Cloning into '/.../weaveworks/homebrew-tap'...
> fatal: unable to connect to github.com:
> github.com[0: 20.205.243.166]: errno=Operation timed out
> 
> Error: Failure while executing; `git clone https://github.com/weaveworks/homebrew-tap /.../weaveworks/homebrew-tap --origin=origin --template=` exited with 128.
> ```
> 
> First we should try to download an arbitrary big file to confirm our network is working. In fact, we could also manually
> clone the git repo related to the error and see if that works. In this example:
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

#### Create Amazon EKS Cluster and Nodes



```bash
eksctl create cluster --name my-cluster --region region-code --fargate
```


#### AWS Identity and Access Management (IAM)

IAM provides the infrastructure necessary to control authentication and authorization for a user's account. The IAM 
infrastructure includes the following elements:

![Error loading intro-diagram _policies_800.png]({{ "/assets/img/intro-diagram _policies_800.png" | relative_url}})

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

##### Creating IAM Admin User and User Group

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


[root user tasks]: https://docs.aws.amazon.com/general/latest/gr/root-vs-iam.html#aws_tasks-that-require-root
[create IAM admin]: https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html

