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


How Do You Engage with Your User Base Today?
--------------------------------------------

When people begin adopting your project, you will want them to not only use it but also engage with the project
community in order to improve it. Engagement is therefore key to growing both your user base and your project's
community. But you can engage with your users in many ways, each requiring different amounts of effort and producing
different outcomes. Determining where you should focus your community-building efforts can be difficult.

Take stock of the various ways you are currently engaging with project users. If possible, categorize these as low
touch, medium touch, and high touch strategies. Low-touch methods involve little interaction between potential users and
your community; that is, they allow potential users to engage with your project in a self-directed way. High touch
methods include one-to-one or one-to-few efforts; they require much more direct, sustained interaction between existing
community members and potential new ones.

Here are some examples of activities you can categorize this way:

* Low touch: **Project website** and **documentation**, pre-recorded or automated online training, newsletters,
  podcasts, and **blogs**. Low-touch activities are best for raising awareness of your project and encouraging users to
  look at it for the first time.
* Medium touch: Project mailing list, **bug tracker**, **community forum**, webinars, user groups, and conference
  presentations. Medium-touch activities can produce a network effect, not only by facilitating communication with users
  but also by enabling those users to help one another.
* High touch: Phone calls, personalized and guided training, conversations at conferences, and community meetings.
  high-touch activities are especially effective for building relationships with key community users, gathering
  community case studies, and turning particularly active users into advocates for your project.

Ideally, your project will have a healthy mix of each of these. By listing them this way, you are able to identify gaps
in your end-to-end community engagement strategy (one that allows someone initially unfamiliar with your project to
start using it, and then, over time, gain seniority in the project to the point of becoming a core contributor).

Additionally, listing your engagement activities this way helps you effectively allocate community-building resources.
Low-touch activities are excellent tools for assisting new users without intervention from senior community members but
are generally impersonal, and high-touch activities can be valuable on an individual basis but do not scale well.


What Alternatives to Your Project Already Exist?
------------------------------------------------

Any open source project strategy must account for a project's competition. Next, you will need to assess the field of
alternative projects in which your own project participates. Considerations here include:

* **Does your project have any direct competition?** If not, why is that? Are you breaking ground in an area of emerging
  technology? Or are people already using other projects to do the same kinds of work or solve the same sorts of
  problems, albeit in a different way?
* **How do other open source projects solve the problem differently than yours does?** If your competitive field
  features a strong incumbent, what can you learn by analyzing that project, its community, and its target users? How
  will you differentiate your work from theirs, and what will motivate people to adopt your project instead of others?
  If your field of competitors features no clear market leader, then is joining another project - rather than competing
  with it - a viable option? If not, why? Answering these questions will ultimately help you prioritize features and
  determine the best ways to engage potential users (for instance, perhaps you can join existing in-person or virtual
  gatherings related to your competitor's technology to spread your message). But at the end of the day, you beat your
  competitors by making yours **10 times better** than theirs

Answering these questions will also help you and your community hone your project's messaging. For example, if your
survey of the competitive field reveals that you are an upstart disruptor in a field dominated by an incumbent, then you
will most likely anchor your messaging to your competition (stressing how your project is less expensive than, or an
open source alternative to, or simpler and faster than another option competing for users' attention). If you are
participating in a new market and your goal for the project is to achieve market dominance, you will need to focus on
spreading your message quickly - which means a higher marketing budget or more aggressive community plan, and more focus
on defining the problems you solve for potential users.


Are You Already Associated with Adjacent Projects?
--------------------------------------------------

If your software is frequently used with (or is particularly useful to users of) another project, then your association
with those projects presents a great opportunity for growing awareness of your project in its early stages of
development. Accounting for these complementary, adjacent projects will help you define your own project's strategy.

For example, [Ceph](https://ceph.io/) can be used to manage storage for
[OpenStack](https://www.redhat.com/en/topics/openstack-35971) or
[Kubernetes](https://www.redhat.com/en/topics/containers/what-is-kubernetes). So the OpenStack and Kubernetes
communities are adjacent to Ceph's community. Catering to adjacent projects to find an audience may affect your
technology roadmap, the events you target with your community outreach efforts, and your investments in specific
integration features.

An adjacent project provides you with a potentially friendly audience working to solve the same problem your own project
does. You may be able to engage in market research, UX testing, and community-building activities with this group in
order to raise awareness of your project and meet potential new users. Adjacent communities important to your
competitors will be important to you, too.


What are Your Goals for the Project?
------------------------------------

This is every open source project's existential question - and surprisingly, many projects have difficulty answering it.
Next, then, you need to consider what you want to achieve by investing in your project. Why, in other words, does your
project exist?

This is especially critical for projects released by or driven by a vendor. If this is the case for your project, ask
yourself why you are choosing to open source this particular software. Your answer might be something like:

* To grow a market.
* To promote a standard.
* To disrupt a competitor.
* To increase demand for another product in our portfolio.

Each of these requires a different set of investments - and a different marketing strategy.

Understanding your reasons for open sourcing the project will help you clarify the investment required to achieve your
goal. In the absence of a strong common vision for the project's goals, you may find yourself underfunding the open
source project, in part because it is perceived as competition to the products you build on top of it.

Asking this important question also will help you align engineering, product, and sales teams as you develop an open
source product strategy


Who are Your Key Stakeholders?
------------------------------

In every project, only a small group of people will be deeply invested in the success of the project and can represent
the diverse set of interests that are important to its development. These people are your stakeholders.

In the case of vendor-sponsored projects, this group will typically include an engineering lead, someone from product
management, a member of product marketing, and a representative from the field (a field engineer, a sales lead, etc.).
You may also want to ensure the group includes someone from your content services or support organizations, as well as
someone from product security. You will brief this group on your initial project strategy and reconvene the group once
or twice every year to report on the state of the project and ensure continued alignment between project goals and the
investments necessary for achieving those goals.
