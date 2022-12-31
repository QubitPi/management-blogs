---
layout: post
title: Preliminary - Anatomy of Small Pull Request
tags: [Git, Management]
color: rgb(246, 77, 39)
feature-img: "assets/img/post-cover/4-cover.png"
thumbnail: "assets/img/post-cover/4-cover.png"
authors: [QubitPi]
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


Benefits of Small Pull Request (PR)
-----------------------------------

### Faster Code Reviews

The most obvious benefit of small pull requests is that they are reviewed faster. Engineers tend to postpone — and often 
forget — code reviews when they see large pull requests.

Even when people start reviewing large pull requests quickly, it can easily take a lot of time to read them through. The 
longer it takes to review the pull request, the more likely it is that it won’t be done in one go. With large pull 
requests, reviewers often lose their focus and get pulled into other stuff.

### Better Code Reviews

![Error loading small-vs-big-pr.png]({{ "/assets/img/small-vs-big-pr.png" | relative_url}})

If you've reviewed large PRs, you’ve probably experienced code review fatigue. Keeping all the context of a large pull 
request in your head is very consuming — especially if the pull request includes multiple separate changes. The result
is that the reviewer starts skimming code, leaves fewer comments, and ends up with a superficial "LGTM" review.

### Improved Code Quality

Have you ever looked at a large pull request, cringed, and thought: “This solution is not very good, but starting again 
from scratch would be too much work.” I certainly have — especially in cases where I’ve failed to help a
less-experienced engineer understand the problem and solution properly.

Small pull requests speed up the feedback cycle. This ensures earlier discussion about the chosen solution and better 
design decisions. End result: higher quality code.

### Protect Work Flow

Small pull requests help you protect your flow by making it easier to get back to the task at hand if something
interrupts your work. And something always does.

To work in small pull requests, you need to learn how to split your work into smaller parts. For example, instead of 
working on a single huge "build feature" task, you should have a list of small, incremental tasks like "add database 
table", "add feature gate", etc.

Smaller tasks require you to keep less context in your mind. Less context makes it easier to get into flow. Suddenly, a 
thing that felt like a "mega-interruption" from your coworker becomes a small hindrance.

![Error loading pull-request-todo-list.png]({{ "/assets/img/pull-request-todo-list.png" | relative_url}})

### Build Features Faster

Smaller pull requests help you stay motivated and keep momentum.

Splitting large entities of work into smaller subtasks is
[proven to make you move faster](https://www.researchgate.net/publication/232501090_A_Theory_of_Goal_Setting_Task_Performance).
Your work will feel more rewarding as you get a steady supply of small doses of dopamine every time you merge a pull 
request.

You're also less likely to get stuck. Like when you have a big ball of mud in your hands and it's hard to know what you 
were doing and what do to next.

It's common for a feature to be delayed for days — or even weeks — because someone who is on the critical path has a 
half-made, large pull request sitting in some WIP branch that they are not able to finish just yet. Maybe they've been 
pulled to another high-priority task, are helping with hiring, or simply got sick.

When you build in small pull requests, these kinds of blockers are less likely. Even when they happen, small pull
requests are easier to hand over to other engineers in the team.

### Improve Collaboration

**When you split your work into smaller pull requests, you’ll also notice that it becomes easier to work on features 
together. Instead of a single engineer ploughing through a feature on their own for two weeks, you can have a few 
engineers do the same thing in less than a week.**

**Improved collaboration has multiple benefits including increased learning at work, higher job satisfaction, and higher 
quality solutions.**


How Small Should A PR Be?
-------------------------

The first step to identify complex pull requests is to look out for **big diffs**. Several studies are showing that it's 
harder to find bugs when reviewing a lot of code. In addition, large pull requests will block other developers who may 
be depending on the code.

But how can we determine the perfect pull request size? A study of a Cisco Systems programming team revealed that a
review of **200 - 400 lines of codes** over **60 to 90 minutes** should **yield 70 - 90% defect discovery**.

**With this number in mind, a good pull request should not have more than 250 lines of code changed**

![Error pull-request-review-time.png]({{ "/assets/img/pull-request-review-time.png" | relative_url}})

As we can see from the chart above, pull requests with **more than 250 lines** of changes tend to take more than 1 hour
to be reviewed.

> The focus, however, should not be hitting a specific number of lines. Instead, focus on learning how to split work
> into smaller increments so it can be built through small and focused pull requests.
> 
> **Measuring the lines changed in pull requests is useful for understanding the overall trend in the way we work**.
> It's a reflection, not a principle to bind. Empirically, most changes you're doing can be shipped in pull requests
> that have less than 200 lines changed. Anything above 500 lines is definitely large. Often, these pull requests could 
> have been shipped in smaller increments.


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
