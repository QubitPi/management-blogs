---
layout: post
title: The Importance of Enforcing Software Architecture
tags: [Software, Architecture]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/35-cover.png"
thumbnail: "assets/img/post-cover/35-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

Software architecture is not about drawing on a canvas some complicated interactions, but about preserves the
maintainability of the software so that any new business requirement changes result in only small amount of software
changes. 

<!--more-->

* TOC
{:toc}

It doesn't take a huge amount of knowledge and skill to get a program working. Kids in high school do it all the time.
Young men and women in college start billion-dollar businesses based on scrabbling together a few lines of PHP or Ruby.
Hoards of junior programmers in cube farms around the world slog through massive requirements documents held in huge
issue tracking systems to get their systems to "work" by the sheer brute force of will. The code they produce may not be
pretty; but it works. It works because getting something to work - once - just isn't that hard.

Getting it right is another matter entirely. Getting software right is hard. It takes knowledge and skills that most
young programmers haven’t yet acquired. It requires thought and insight that most programmers don’t take the time to
develop. It requires a level of discipline and dedication that most programmers never dreamed they’d need. Mostly, it
takes a passion for the craft and the desire to be a professional.

And when you get software right, something magical happens: You don't need hordes of programmers to keep it working. You
don't need massive requirements documents and huge issue tracking systems. You don't need global cube farms and 24/7
programming.

When software is done right, it requires a fraction of the human resources to create and maintain. Changes are simple
and rapid. Defects are few and far between. Effort is minimized, and functionality and flexibility are maximized.

## Architecture Preserves Maintainability of Software

On my first day of working at Yahoo, my advisor told me "Enterprise software is all about readability and
maintainability". This section explains how architecture could help to keep the maintainability of software.

Software must be easy to change. When the stakeholders change their minds about a feature, that change should be simple
and easy to make. **The difficulty in making such a change should be proportional only to the scope of the change, and
not to the shape of the change**. It is this difference between scope and shape that often drives the growth in software
development costs. It is the reason that costs grow out of proportion to the size of the requested changes. It is the
reason that the first year of development is much cheaper than the second, and the second year is much cheaper than the
third.

From the stakeholders' point of view, they are simply providing a stream of changes of **roughly similar scope**. From
the developers' point of view, the stakeholders are giving them a stream of jigsaw puzzle pieces that they must fit into
a puzzle of ever-increasing complexity. Each new request is harder to fit than the last, because the shape of the system
does not match the shape of the request.

The problem, of course, is the architecture of the system. The more this architecture prefers one shape over another,
the more likely new features will be harder and harder to fit into that structure. Therefore architectures should be as
shape agnostic are practical.

There are systems that are practically impossible to change, because the cost of change exceeds the benefit of change.
Many systems reach that point in some of their features or configurations. If the business managers ask you for a
change, and your estimated costs for that change are unaffordably high, the business managers will likely be furious 
hat you allowed the system to get to the point where the change was impractical.

If architecture comes last, then the system will become ever more costly to develop, and eventually change will become
practically impossible for part or all of the system. If that is allowed to happen, it means the software development
team did not fight hard enough for what they knew was necessary.

## What is Architecture?

First of all, a software architect is a programmer; and continues to be a programmer. Never fall for the lie that
suggests that software architects pull back from code to focus on higher-level issues. They do not! Software architects
are the best programmers, and they continue to take programming tasks, while they also guide the rest of the team toward
a design that maximizes productivity. Software architects may not write as much code as other programmers do, but they
continue to engage in programming tasks. They do this because they cannot do their jobs properly if they are not
experiencing the problems that they are creating for the rest of the programmers.

The architecture of a software system is the shape given to that system by those who build it. The form of that shape is

* in the division of that system into components,
* the arrangement of those components, and
* the ways in which those components communicate with each other

**The purpose of that shape is to facilitate the _development_, _deployment_, _operation_, and maintenance of the
software system contained within it**.

**The primary purpose of architecture is to support the life cycle of the system. Good architecture makes the system
easy to understand, easy to develop, easy to maintain, and easy to deploy. The ultimate goal is to minimize the lifetime
cost of the system and to maximize programmer productivity**.

This is not to say that architecture plays no role in supporting the proper behavior of the system. It certainly does,
and that role is critical. But the role is passive and cosmetic, not active or essential.

### Why Architecture Should Aim At Facilitating ...

#### Development?

Different team structures imply different architectural decisions. On the one hand, a small team of five developers can
quite effectively work together to develop a monolithic system without well-defined components or interfaces. In fact,
such a team would likely find the strictures of an architecture something of an impediment during the early days of
development. This is likely the reason why so many systems lack good architecture: They were begun with none, because
the team was small and did not want the impediment of a superstructure.

On the other hand, a system being developed by five different teams, each of which includes seven developers, cannot
make progress unless the system is divided into well-defined components with reliably stable interfaces. If no other
factors are considered, the architecture of that system will likely evolve into five components - one for each team.

Such a component-per-team architecture is not likely to be the best architecture for deployment, operation, and
maintenance of the system. Nevertheless, it is the architecture that a group of teams will gravitate toward if they are
driven solely by development schedule.

#### Deployment?

To be effective, a software system must be deployable. The higher the cost of deployment, the less useful the system is.
A goal of a software architecture, then, should be to make a system that can be easily deployed with a single action.

Unfortunately, deployment strategy is seldom considered during initial development. This leads to architectures that may
make the system easy to develop, but leave it very difficult to deploy.

For example, in the early development of a system, the developers may decide to use a "micro-service architecture." They
may find that this approach makes the system very easy to develop since the component boundaries are very firm and the
interfaces relatively stable. However, when it comes time to deploy the system, they may discover that the number of
micro-services has become daunting; configuring the connections between them, and the timing of their initiation, may
also turn out to be a huge source of errors.

Had the architects considered deployment issues early on, they might have decided on fewer services, a hybrid of
services and in-process components, and a more integrated means of managing the interconnections.

#### Operation?

Almost any operational difficulty can be resolved by throwing more hardware at the system without drastically impacting
the software architecture.

This is not to say that an architecture that is well tuned to the operation of the system is not desirable. It is! It's
just that the cost equation leans more toward development, deployment, and maintenance.

The architecture of a system makes the operation of the system readily apparent to the developers. Architecture should
reveal operation. The architecture of the system should elevate the use cases, the features, and the required behaviors
of the system to first-class entities that are visible landmarks for the developers. This simplifies the understanding
of the system and, therefore, greatly aids in development and maintenance.

#### Maintenance?

### Keeping Options open

**Software was invented because we needed a way to quickly and easily change the behavior of machines. But that
flexibility depends critically on the shape of the system, the arrangement of its components, and the way those
components are interconnected.**

_The way you keep software soft is to leave as many options open as possible, for as long as possible. What are the
options that we need to leave open? They are the details that don’t matter_.

All software systems can be decomposed into two major elements

1. policy and
2. details.

The policy element embodies all the business rules and procedures. The policy is where the true value of the system
lives.

The details are those things that are necessary to enable humans, other systems, and programmers to communicate with the
policy, but that do not impact the behavior of the policy at all. They include IO devices, databases, web systems,
servers, frameworks, communication protocols, and so forth.

_**The goal of the architect is to create a shape for the system that recognizes policy as the most essential element of
the system while making the details irrelevant to that policy. This allows decisions about those details to be delayed
and deferred.**_

For example:

* It is not necessary to choose a database system in the early days of development, because the high-level policy should
  not care which kind of database will be used. Indeed, if the architect is careful, the high-level policy will not care
  if the database is relational, distributed, hierarchical, or just plain flat files.
* It is not necessary to choose a web server early in development, because the highlevel policy should not know that it
  is being delivered over the web. If the high-level policy is unaware of HTML, AJAX, JSP, JSF, or any of the rest of
  the alphabet soup of web development, then you don't need to decide which web system to use until much later in the
  project. Indeed, you don’t even have to decide if the system will be delivered over the web.
* It is not necessary to adopt REST early in development, because the high-level policy should be agnostic about the
  interface to the outside world. Nor is it necessary to adopt a micro-services framework, or a SOA framework. Again,
  the high-level policy should not care about these things.
* It is not necessary to adopt a dependency injection framework early in development, because the high-level policy
  should not care how dependencies are resolved.
  
The longer you leave options open, the more experiments you can run (e.g. applicabilities and performances of various
databases), the more things you can try, and the more information you will have when you reach the point at which those
decisions can no longer be deferred.

What if the decisions have already been made by someone else? What if your company has made a commitment to a certain
database, or a certain web server, or a certain framework? A good architect pretends that the decision has not been
made, and shapes the system such that those decisions can still be deferred or changed for as long as possible.

> A good architect maximizes the number of decisions not made.

### Conclusion

Good architects carefully separate details from policy, and then decouple the policy from the details so thoroughly that
the policy has no knowledge of the details and does not depend on the details in any way. Good architects design the
policy so that decisions about the details can be delayed and deferred for as long as possible
