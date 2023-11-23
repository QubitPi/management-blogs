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


Who are the Project's Users?
----------------------------

Successful open source projects require users. But many open source projects do not clearly understand who uses the 
software they produce, nor do they typically have a sense of what those users do with the software.

Consider developing a set of personas to characterize archetypes of key users engaging with your project. Ask yourself:

* What job must be done, and how does the software help someone perform that job?
* Who (what type of user, occupying what type of role in an organization) is most likely to need to perform that job,
  and to use this software to do it?

Your answer will likely be something like IT professionals, mobile application developers, or web developers. Next,
refine your target user persona by asking follow-up questions designed to hone in on a number of criteria, including:

* Is the project more useful to an individual or an organization?
* Is your project particularly useful in a specific industry vertical or business domain?
* What size organization will find your project most useful? Are you targeting systems administrators in large
  enterprises, or small and medium-sized businesses?
* What are the job titles of the people who will be downloading, installing, and using your project? Are they the same 
  people, or are the users different from the project administrators?
* What is the relationship between the people who download and install open source projects and the people who evaluate
  and purchase commercial products?

Your answers to these questions will impact your priorities for structuring the project, promoting it, and even
engineering it (as your answers will prompt you to prioritize developing certain features over others). For example, if 
your project runs in a datacenter or on a cluster of servers, your audience will typically be a business-focused
audience, people running IT professionally (or as a volunteer in a university). For a mobile application or a web 
development framework, the majority of your audience will be running your project on personal computers, workstations,
or mobile phones. Each of these groups is solving different problems and has distinct motivations—and those factors
impact your engineering, marketing, and community-building decisions.

