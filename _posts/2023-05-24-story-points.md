---
layout: post
title: Story points and Estimation
tags: [Agile]
color: rgb(38, 131, 255)
feature-img: "assets/img/post-cover/1-cover.png"
thumbnail: "assets/img/post-cover/1-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---


Traditional software teams give estimates in a time format: days, weeks, months. Many agile teams, however, have
transitioned to story points. Story points are units of measure for expressing an estimate of the overall effort
required to fully implement a product backlog item or any other piece of work. Teams assign story points relative to
work complexity, the amount of work, and risk or uncertainty. Values are assigned to more effectively break down work
into smaller pieces, so they can address uncertainty. Over time, this helps teams understand how much they can achieve
in a period of time and builds consensus and commitment to the solution.  It may sound counter-intuitive, but this
abstraction is actually helpful because it pushes the team to make tougher decisions around the difficulty of work.

<!--more-->

* TOC
  {:toc}

Definition of Story Points
--------------------------

Story points define the effort in a time-box, so they do not change with time. For instance, in one hour an individual
can walk, run, or climb, but the effort expended is clearly different. The gap progression between the terms in the
**Fibonacci sequence** encourages the team to deliver carefully considered estimates. Estimates of 1, 2 or 3 imply
similar efforts (1 being trivial), but if the team estimates an 8 or 13 (or higher), the impact on both delivery and
budget can be significant. The value of using story points is that the team can reuse them by comparing similar work
from previous sprints, but it should be recognized that estimates are relative to that team. For example, an estimate
of 5 for one team could be a 2 for another composed of more experienced developers with higher capability.

The reason for using **Fibonacci sequence** is that the larger the story is, the more _uncertainty_ there is around it
and the less accurate the estimate will be. Using the Fibonacci sequence helps teams to recognise this uncertainty,
deliberately creating a lack of precision instead of wasting time trying to produce estimates that might also carry a
false degree of confidence.

How to Estimate Story Points
----------------------------

Typically, story points take into account 3 factors

1. Complexity
2. Risk
3. Experience on similar task

Given we use the Fibonacci sequence of 1, 2, 3, 5, 8, 13, we could initially estimate using the following story point
matrix:

![Error Loading story-point-matrix.png]({{ "/assets/img/story-point-matrix.png" | relative_url}})

_As the team accumulates experience, this matrix will be adjusted to reflect things more accurately_
