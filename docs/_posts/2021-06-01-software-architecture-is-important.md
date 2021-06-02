---
layout: post
title: The Importance of Enforcing Software Architecture
tags: [Software, Architecture]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/35-cover.png"
thumbnail: "assets/img/post-cover/35-cover.png"
author: QubitPi
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
