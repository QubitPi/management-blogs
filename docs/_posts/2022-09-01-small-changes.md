---
layout: post
title: [Tech Management] Preliminary - Anatomy of Small Pull Request
tags: [Git]
category: FINALIZED
color: rgb(246, 77, 39)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Writing clean code is just one of the many factors to care about when creating a pull request. Large pull requests cause
a significant overhead during the code review and facilitate bugs introduction in the codebase. That's why we need to
care about the pull request itself. It should be **short, have a good title and description, and do just one thing**.

> A good pull request will
>
> * be reviewed quickly;
> * reduce bug introduction into codebase;
> * facilitate new developers onboarding;
> * not block other developers;
> * speed up the code review process and, consequently the product development.


The Size of a Pull Request (PR)
-------------------------------

![Error loading small-vs-big-pr.png]({{ "/assets/img/small-vs-big-pr.png" | relative_url}})

The first step to identify complex pull requests is to look out for **big diffs**. Several studies are showing that it's 
harder to find bugs when reviewing a lot of code. In addition, large pull requests will block other developers who may 
be depending on the code.

But how can we determine the perfect pull request size? A study of a Cisco Systems programming team revealed that a
review of **200 - 400 lines of codes** over **60 to 90 minutes** should **yield 70 - 90% defect discovery**.

**With this number in mind, a good pull request should not have more than 250 lines of code changed**

![Error pull-request-review-time.png]({{ "/assets/img/pull-request-review-time.png" | relative_url}})

As we can see from the chart above, pull requests with **more than 250 lines** of changes tend to take more than 1 hour
to be reviewed.


Feature Breakdown
-----------------

Feature breakdown is an art. The more we do it, the easier it gets. What do I mean by Feature breakdown?

> Is understanding a big feature and breaking it into small pieces that make sense and can be merged into the codebase 
> piece by piece without breaking anything.

Let's say that we need to create a subscribe feature on our app. It's just a form that accepts an email address and
saves it. Without knowing how our app works, we can already break it into eight pull requests:

1. Create a model to save emails
2. Create a route to receive requests
3. Create a controller
4. Create a service to save it in the database (business logic)
5. Create a policy to handle access control
6. Create a subscribe component (frontend)
7. Create a button to call the subscribe component
8. Add the subscribe button in the interface

As we can see, we broke this feature into many parts, and most of these tasks can be done simultaneously by different 
developers.

### Single Responsibility Principle

> The **single responsibility principle** is a computer programming principle that states that every module or class
> should have responsibility over a single part of the functionality provided by the software, and that responsibility 
> should be entirely encapsulated by the class.

Just like classes and modules, pull requests should do only one thing. Pull requests that follow the SRP reduces the 
overhead caused by revising a code that attempts to solve several problems. Before submitting a PR for review, try
applying the principle of single responsibility. If this code is doing more than one thing, break it into other Pull 
Requests.


Title and Description Matter
----------------------------

When creating the PR, the author should care about the title and the description.

Imagine that the code reviewer is arriving in our team today without knowing what is going on, and even so, they should
be able to understand the changes.

![Error example-pr-desc.png]({{ "/assets/img/example-pr-desc.png" | relative_url}})

### The Title of the PR Should be Self-explanatory

The title should be sufficient to understand what is being changed. For example: 

* Add test case for getEventTarget
* Improve cryptic error message when creating a component starting with a lowercase letter

### Make a Useful Description

* Describe what was changed in the pull request.
* Explain why this PR exists.
* Make it clear how it does what it sets out to do. E.g., Does it change a column in the database? How is this being
  done? What happens to the old data?
* Use screenshots to demonstrate what has changed.
