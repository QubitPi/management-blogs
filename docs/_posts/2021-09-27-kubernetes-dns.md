---
layout: post
title: Kubernetes DNS
tags: [B2B, Kubernetes, DNS, Virtualization, CI/CD]
category: FINALIZED
color: rgb(240,78,35)
feature-img: "assets/img/post-cover/1-cover.png"
thumbnail: "assets/img/post-cover/1-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

DNS resolving is the core of Kubernetes, because it enables flexible deployment and lots of useful features, such as
load balancing. It, therefore, deserves a dedicated post to explain how it works.

<!--more-->

* TOC
{:toc}
  
## DNS for Services and Pods

Kubernetes creates DNS records for services and pods. **You can contact services with consistent DNS names instead of IP
addresses**.

### Introduction

**Kubernetes DNS schedules a DNS Pod and Service on the cluster, and configures the kubelets to tell individual
containers to use the DNS Service's IP to resolve DNS names**.

Every Service defined in the cluster (including the DNS server itself) is assigned a DNS name. By default, a client
Pod's DNS search list includes the Pod's own namespace and the cluster's default domain.

A DNS query may return different results based on the namespace of the pod making it. **DNS queries that don't specify a
namespace are limited to the pod's namespace. Access services in other namespaces by specifying it in the DNS query**.

For example, consider a pod in a `test` namespace. A `data` service is in the `prod` namespace. A query for `data.prod`
returns the intended result, because it specifies the namespace.



