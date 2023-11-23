---
layout: post
title: Developing a Strategy for Organization's Open Source Project
tags: [OSS]
color: rgb(236, 29, 35)
feature-img: "assets/img/post-cover/6-cover.png"
thumbnail: "assets/img/post-cover/6-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

Creating a strategy for any software project is difficult. Developing a strategy for an open source software project is
no different. The hardest part about launching a free software project is _transforming a private vision into a public 
one_, writes Karl Fogel in Producing Open Source Software. "You or your organization may know perfectly well what you
want, but expressing that goal comprehensibly to the world is a fair amount of work. It is essential, however, that you 
take the time to do it." That is because successful open source software projects require more than accessible code and
a willingness to collaborate. The most popular and effective projects set clear goals and thoroughly understand both
their communities and their target markets. This guide offers a series of 7 questions you and your team should ask as
you undertake the essential work of developing an effective strategy for your open source project.

* TOC
{:toc}


What is the Project?
--------------------

This is the fundamental question. How does the project help its users? What problem does it solve?

Before developing anything further, you must be able to describe your project to a potential user in the most succinct
way possible, without specialized language or jargon. Too often, open source projects describe themselves with
terminology unfamiliar to many potential users.

They also tend to focus on what a project does, rather than on **the problems a project solves**. For example, consider
the open source project Istio. Asked "[What is Istio](https://www.redhat.com/en/topics/microservices/what-is-istio)?"
one might be tempted to respond:

> "Istio is a service mesh."

But is that the simplest, clearest, and least confusing way to describe the Istio project? Do everyday users understand 
what a service mesh is? Do most developers? The shorthand doesn't help anyone who can't understand it.

So consider the audience for your project explanation. Talking to someone with a background in networking, for example,
you might describe the project as:

> "Istio is a framework that allows you to manage each application's data plane from a central control center."

Alternatively, for a system administrator or operations team member, a description that focuses on application
observability will provide a more useful frame of reference:

> "Istio is a central switchboard that lets you control the observability of network traffic between microservices
> across your enterprise."

And speaking to someone completely unfamiliar with the concept of a service mesh, we might instead choose more
metaphorical language:

> "Istio is like a traffic system for your application. A proxy used by Istio sits in front of each application node and 
> directs traffic coming into and out of the node. Traffic rules are set in a central control center for the
> application."

Ask as many probing follow-up questions as you must. In the end, just be sure that you understand the project, know what
it can (and can't) do, and can explain it to someone else easily and clearly.

