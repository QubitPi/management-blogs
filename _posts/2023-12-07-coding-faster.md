---
layout: post
title: Code Faster
tags: [Efficiency]
color: rgb(9, 102, 194)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
authors: [steven-lowe]
excerpt_separator: <!--more-->
---

Want to know how to program faster so that you can deliver software faster? Sure, who doesn't? The internet is full of 
tips  for developers - hundreds, thousands, perhaps even millions of them. The problem is, there are far more out there 
than anyone has time to read, so I've boiled them down for you.

Here are 53 tips representing the very best advice I've found out there. But, before I tell you what I found, I need to 
explain what I mean by "programming faster," and "tips."

<!--more-->

* TOC
{:toc}

The Problem with "Faster"
-------------------------

To code faster, one has to be efficient; that is, **no wasted effort** or motion. This can mean everything from typing
to tools to thinking. But most of our work as programmers isn't typing, or compiling - it's thinking. To think faster,
we have to **learn more patterns and relationships**. This is the knowledge and wisdom that experience builds. What we 
need to go faster will change over time.

The Problem with "Tips"
-----------------------

Most of the tips I read only apply at certain points along my journey, and don't necessarily apply to everyone. Many of 
these fall into the "personal journey" or "what worked for me" categories. But my path is probably not others' path. 
While some of the mechanical things that work for me will probably work for others, many of the domain and pattern 
choices I made may be of no use.

The mechanical stuff is pretty easy to optimize; the options are limited. But the learning stuff has no limits. No one 
will ever know it all. We must make strategic and tactical choices, and be prepared to take advantage of opportunities 
when they arise.

The utility of tips falls off as a function of specificity. The more specific tips don't apply to everyone, but general 
tips are too, well, general. They're much more difficult to turn into action. So what do we really want when we say we 
want to "go faster?"

What We Want is Flow
--------------------

What every programmer wants [**Flow state**](https://en.wikipedia.org/wiki/Flow_(psychology)#History), which maximizes 
throughput and increases enjoyment by incorporating just the right level of challenge; one stays fully engaged in the 
moment and in the work. Sustaining flow state requires a suitable environment and frictionless process.

Flow state when pairing is like each of you having an extra brain. Unfortunately, many developer environments, such as 
open offices, are unfriendly to flow. So constant interruptions, uncomfortable circumstances, and endless meetings 
that discourage flow by

- making sure that we are in an environment that will not distract us; make it impossible for distractions to interrupt 
  us.
- Know ourselves, and work during our peak time - not someone else's.

Our Options May be Limited
--------------------------

When we find something sub-optimal about our process, or ourselves, the choices of how to address such constraints are 
limited:

- Ignore it:  Maybe it will get better on its own.
- Avoid it: Is it really necessary?
- Automate it: Make the machine do it.
- Delegate it. Rarely possible, this is passing the buck. But it is a legit option when available.
- Grind it down. We all have to do this from time to time (daily). Some jobs are larger than others.

If typing is slower, take a little time and level up. If integrated development environment is confusing and unhelpful 
(or perhaps too helpful), try something different or simpler. If we can't get away from it, learn more about it; we may 
find another way, or at least learn the limits.

There are numerous ways to learn. Google is our friend, as are books, videos, blog posts, Stack Overflow questions, and, 
of course, other people. Some things we want to learn may be hidden; others may be larger than they appear. Balance 
benefit with effort and be patient with ourselves. Celebrate every achievement and keep moving.

Architecture is a Secondary Effect
----------------------------------

> Source: http://butunclebob.com/ArticleS.UncleBob.ArchitectureIsaSecondaryEffect

This may seem a bitter pill to swallow. And many architects may reject it out of hand. However, nobody is suggesting 
that architecture is no important, or that we should throw it away in favor of others. Quite the contrary, it is the 
tests that give us the fearlessness to continuously improve the architecture of the system. It is not that tests 
supercede architecture; rather they enable it!

Automated tests give us a reliable way to know that our systems are working properly. We are no longer afraid that an 
architectural change will break them. This makes it much easier to enact those architectural changes that will improve 
the system. The presence of tests mean that there is much less impediment to continuous improvement of the structure, 
design, and architecture of a system.

Without tests, architecture was an initial guess that was difficult to change once significant development started. But 
with automated tests architecture remains malleable. The risk (and therefore the cost) of change is so greatly reduced 
that architecture becomes a variable that we can fiddle with as our system evolves.

Hene:

1. The main goal of architecture is flexibility, maintainability, and scalability.
2. But the kind of unit tests and acceptance tests produced by the discipline of Test Driven Development are much more 
   important to flexibility, maintainability, and scalability.
3. Write the code that would actually make a product first, no matter how silly or small that product is.

Tips for Programming Faster
---------------------------

- _Reflect_. What do you want, what do you actually do; includes measuring and optimization.
- _Learn the fundamentals_: languages, tools, patterns, practices, etc., from everyone (especially those willing to 
  teach); learn how we learn, and learn continuously.
- _Teach_. Teach others. Having to explain things forces simplification, and the transformation from thoughts to verbal 
  or visual expressions produces insights.
- _Express and explore_. Look outside our normal duties; draw, write, blog, go to meetups, attend and give
  presentations. Not everything you might want to know is in your office or on the Internet.
- _Practice, practice, practice_
- _Understand the user_. Understand their problem, the real problem; and then solve it. Knowledge of the domain helps 
  immensely. Talk to colleagues and domain experts about the problem, solution, and design.
- _Make bugs impossible by design_, such as using strong typing and immutable data
- Follow an agile approach to development.
- Pull out abstractions only if they make sense and would actually be reused.
- Stand on the shoulders of giants; use open source libraries, third party solutions, and so on.

And I strongly agree with Robert C. "Uncle Bob" Martin's statement on
[vehement mediocrity](http://butunclebob.com/ArticleS.UncleBob.VehementMediocrity):

> The only way to go fast is to go well. Every time you yield to the temptation to trade quality for speed, you slow 
> down. Every time.

### Keep it Healthy

Dead people write no code. Sick people write bad code. Take care of yourself.

- Know the value of stepping away from your code.
- Get more sleep, eat better, and work fewer hours.
