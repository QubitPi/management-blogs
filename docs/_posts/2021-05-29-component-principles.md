---
layout: post
title: Architecture - Component Principles
tags: [Architecture, Design]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/32-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

If the [SOLID principles](https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html) tell us how to
arrange the bricks into walls and rooms, then the component principles tell us how to arrange the rooms into buildings.
Large software systems, like large buildings, are built out of smaller components.

* TOC
{:toc}

**Components are the units of deployment. They are the smallest entities that can be deployed as part of a system. In
Java, they are jar files**.

Components can be linked together into a single executable. Or they can be aggregated together into a single archive,
such as a `.war` file. Or they can be independently deployed as separate dynamically loaded plugins, such as `.jar`.
Regardless of how they are eventually deployed, well-designed components always retain the ability to be
**independently deployable** and, therefore, **independently developable**.

## Component Cohesion

Component cohesion is all about **"which classes belong in which components?"**. In this section we will discuss three
principles of component cohesion:

1. [The Reuse/Release Equivalence Principle (REP)](#the-reuserelease-equivalence-principle) - "common theme"
2. [The Common Closure Principle (CCP)](#the-common-closure-principle) - "change together"
3. [The Common Reuse Principle (CRP)](#the-common-reuse-principle) - "depend on all"

### The Reuse/Release Equivalence Principle

> The granule of reuse is the granule of release.

From a software design and architecture point of view, this principle means that the classes and modules that are formed
into a component must belong to a cohesive group. The component cannot simply consist of a random hodgepodge of classes
and modules; instead, there **must be some overarching theme or purpose that those modules all share**.

### The Common Closure Principle

> Gather into components those classes that change for the same reasons and at the same times. Separate into different
> components those classes that change at different times and for different reasons.

This is the
[Single Responsibility Principle](https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html#single-responsibility-principle) restated for components. Just as the SRP
says that a class should not contain multiples reasons to change, so the Common Closure Principle (CCP) says that a
component should not have multiple reasons to change.

The CCP amplifies gather together into the same component those classes that are closed to the same types of changes.
Thus, when a change in requirements comes along, that change has a good chance of being restricted to a minimal number
of components.

The CCP is the component form of the
[SRP]((https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html#single-responsibility-principle)). The
SRP tells us to separate methods into different classes, if they change for different reasons. The CCP tells us to
separate classes into different components, if they change for different reasons. Both principles can be summarized by
the following sound bite

> **Gather together those things that change at the same times and for the same reasons. Separate those things that
> change at different times or for different reasons**.

### The Common Reuse Principle

> **Don’t force users of a component to depend on things they don't need.**

The Common Reuse Principle (CRP) is yet another principle that helps us to decide which classes and modules should be
placed into a component. It states that classes and modules that tend to be reused together belong in the same
component.

But the CRP tells us more than just which classes to put together into a component: It also tells us which classes not
to keep together in a component. When one component uses another, a dependency is created between the components.
Perhaps the using component uses only one class within the used component - but that still doesn't weaken the
dependency. The using component still depends on the used component.

Because of that dependency, every time the used component is changed, the using component will likely need corresponding
changes. Even if no changes are necessary to the using component, it will likely still need to be recompiled,
revalidated, and redeployed. This is true even if the using component doesn’t care about the change made in the used
component.

Thus when we depend on a component, we want to make sure we depend on every class in that component. Put another way,
we want to make sure that the classes that we put into a component are inseparable - that it is impossible to depend on
some and not on the others. Otherwise, we will be redeploying more components than is necessary, and wasting significant
effort.

The CRP is the generic version of the
[ISP](https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html#interface-segregation-principle). The ISP
advises us not to depend on classes that have methods we don't use. The CRP advises us not to depend on components that
have classes we don’t use.

All of this advice can be reduced to a single sound bite:

> **Don't depend on things you don't need.**

### The Tension Diagram for Component Cohesion

You may have already realized that the three cohesion principles tend to fight each other. The REP and CCP are inclusive
principles: Both tend to make components larger. The CRP is an exclusive principle, driving components to be smaller.
**It is the tension between these principles that good architects seek to resolve**.

The figure below is a tension that shows how the three principles of cohesion interact with each other. The edges of the
diagram describe the cost of abandoning the principle on the opposite vertex.

![Error loading tension-diagram.png]({{ "/assets/img/tension-diagram.png" | relative_url}})

The way to interpret the diagram above is like this: An architect who focuses on just the REP and CRP will find that
"too many components are impacted when simple changes are made" (edge between REP and CRP). In contrast, an architect
who focuses too strongly on the CCP and REP will cause "too many unneeded releases to be generated" (edge between CCP
and REP).

A good architect finds a position in that tension triangle that meets the current concerns of the development team, but
is also aware that those concerns will change over time. For example, early in the development of a project, the CCP is
much more important than the REP, because develop-ability is more important than reuse.

Generally, projects tend to start on the right hand side of the triangle, where the only sacrifice is reuse. As the
project matures, and other projects begin to draw from it, the project will slide over to the left. This means that the
component structure of a project can vary with time and maturity. It has more to do with the way that project is
developed and used, than with what the project actually does.

## Component Coupling

The next three principles deal with the relationships between components.

### The Acyclic Dependencies Principle

> **Allow no cycles in the component dependency graph.**

### The Stable Dependencies Principle

> **Depend in the direction of stability**

Any component that we expect to be volatile should not be depended on by a component that is difficult to change. It is
the perversity of software that a module that you have designed to be easy to change can be made difficult to change by
someone else who simply hangs a dependency on it. Not a line of source code in your module need change, yet your module
will suddenly become more challenging to change. By conforming to the Stable Dependencies Principle (SDP), we ensure
that modules that are intended to be easy to change are not depended on by modules that are harder to change.

#### Measuring Stability

One sure way to make a software component difficult to change, is to make lots of other software components depend on
it. A component with lots of incoming dependencies is very stable because it requires a great deal of work to reconcile
any changes with all the dependent components.

One way, therefore, to measure the stability is to count the number of dependencies that enter and leave that component.
These counts will allow us to calculate the **positional stability** of the component.

* **Fan-in - Incoming dependencies**. This metric identifies the number of classes outside this component that depend on
  classes within the component.
* **Fan-out - Outgoing depenencies**. This metric identifies the number of classes inside this component that depend on
  classes outside the component.
* ** Instability - I = Fan-out/(Fan-in + Fan-out)**. This metric has the range [0, 1]. I = 0 indicates a maximally
  stable component. I = 1 indicates a maximally unstable component.
  
#### Not All Components Should be Stable

**If all the components in a system were maximally stable, the system would be unchangeable**

##### Keep Stable being Stable

The diagram below shows how the The Stable Dependencies Principle can be violated:

![Error loading sdp-violated.png]({{ "/assets/img/sdp-violated.png" | relative_url}})

"Flexible" is a component that we have designed to be easy to change. We want "Flexible" to be unstable. However, some
developer, working in the component named "Stable", has hung a dependency on "Flexible". This violates the SDP **because
the I metric for "Stable" is much smaller than the I metric for "Flexible". As a result, "Flexible" will no longer be
easy to change. A change to "Flexible" will force us to deal with "Stable" and all its dependents.**

To fix this problem, we somehow have to break the dependence of "Stable" on "Flexible". Why does this dependency exist?
Let's assume that there is a class `C` within "Flexible" that another class `U` within "Stable" needs to use:

![Error loading assumption.png]({{ "/assets/img/assumption.png" | relative_url}})

We can fix this by employing the
[Dependency Inversion Principle](https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html#dependency-inversion-principle).
We create an interface class called `US` and put it in a component named `UServer`. We make sure that this interface
declares all the methods that `U` needs to use. We then make `C` implement this interface as shown below:

![Error loading solution.png]({{ "/assets/img/solution.png" | relative_url}})

This breaks the dependency of "Stable" on "Flexible", and forces both components to depend on "UServer".
"UServer" is very stable (I = 0), and "Flexible" retains its necessary instability (I = 1). **All the dependencies now
flow in the direction of decreasing I**.

###### Abstract Components

We notice that we just created a component ("UService") that contains nothing but an interface. Such a component
contains no executable code. This is very common and necessary in statically typed languages, such as Java.

These abstract components are very stable and, therefore, are ideal targets for less stable components to depend on.

When using dynamically typed languages like Ruby and Python, these abstract components don't exist at all, nor do the
dependencies that would have targeted them. Dependency structures in these languages are much simpler because dependency
inversion does not require either the declaration or the inheritance of interfaces.

### The Stable Abstraction Principle

> **A component should be as abstract as it is stable**

Some software in the system should not change very often. This software represents high-level architecture and policy
decisions. We don't want these business and architectural decisions to be volatile. Thus the software that encapsulates
the high-level policies of the system should be placed into stable components (I = 0). Unstable components (I = 1)
should contain only the software that is volatile - software that we want to be able to quickly and easily change.

However, if the high-level policies are placed into stable components, then the source code that represents those
policies will be difficult to change. This could make the overall architecture inflexible. How can a component that is
maximally stable (I = 0) be flexible enough to withstand change? The answer is found in the
[Open-Closed Principle](https://qubitpi.github.io/jersey-guide/2021/05/30/design-principles.html#open-closed-principle).
This principle tells us that it is possible and desirable to create classes that are flexible enough to be
extended without requiring modification. Which kind of classes conform to this principle? _Abstract classes_.

The _Stable Abstractions Principle_ sets up a relationship between stability and abstractness. On the one hand, it says
that a stable component should also be abstract so that its stability does not prevent it from being extended. On the
other hand, it says that an unstable component should be concrete since its instability allows the concrete code within
it to be easily changed.

To be continued...
