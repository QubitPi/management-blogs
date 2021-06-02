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
Regardless of how they are eventually deployed, well-designed components always
retain the ability to be independently deployable and, therefore, independently
developable.