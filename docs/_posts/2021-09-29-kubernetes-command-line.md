---
layout: post
title: Kubernetes Command Line Reference
tags: [B2B, Kubernetes, Virtualization, CI/CD, CML]
category: FINALIZED
color: rgb(240,78,35)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Delete Namespace

    kubectl delete namespace <namespace-name> --force --grace-period=0

## Delete Pod

    kubectl delete pods --namespace=<namespace-name> <pod-name> --grace-period=0 --force
