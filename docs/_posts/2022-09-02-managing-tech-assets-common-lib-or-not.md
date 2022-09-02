---
layout: post
title: Managing Tech Assets - Is a Common Library a Good Idea? No
tags: [OSS]
category: MANAGEMENT
color: rgb(244, 130, 36)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


A Story - A Person Created a Common Library and Then...
-------------------------------------------------------

"Embarrassingly I introduced a "common" library, named as such, in a team environment a couple of decades back.
I didn't really understand the dynamics back then of what could happen in a loosely-coordinated team setting in just a 
matter of months.

When I introduced it I thought I made it clear and also documented that it's for things we'd all agree we find useful on
a daily basis, that it's intended to be a minimalist library, and that the library should depend on nothing else besides 
the standard library so that it's as easy to deploy as possible in new projects. My thinking at the time was that it was 
our own little extension to the standard library for things that, in our particular domain, we found useful on a daily
basis.

And it started off well enough. We started off with a math library (`common/math*`) of routines which we all used on a 
daily basis, since we were working on computer graphics which was often heavy on the linear algebra. And since we were 
often interoping with C code, we agreed on some useful utility functions like find_index which, unlike std::find in C++, 
would return an index to an element found in a sequence instead of an iterator which mimicked how our C functions worked
-- things of this sort -- a little bit eclectic but minimalist and widely used enough to remain familiar and practical
to everyone, and instant familiarity is an extremely important criteria as I see it in trying to make anything that is 
"common" or "standard" since if it truly is "common", it should have that familiar quality about it as a result of its
wide adoption and daily usage.

But over time the design intentions of the library slipped out of my fingers as people started to add things they used 
personally that they merely thought might be of use to someone else, only to find no one else using it. And later
someone started adding functions that depended on OpenGL for common GL-related routines. Further on we adopted Qt and 
people started adding code that depended on Qt, so already the common library was dependent on two external libraries.
At some point someone added common shader routines which was dependent on our application-specific shader library, and
at that point you couldn't even deploy it in a new project without bringing in Qt, OGL, and our application-specific
shader library and writing a non-trivial build script for your project. So it turned into this eclectic, interdependent 
mess. Later on people even added GUI-dependent code to it.

But I've also found by debating what should and shouldn't go into this library that what is considered "common" can
easily turn into a very subjective idea if you don't set a very hard line rule that what's "common" is what everyone
tends to find useful on a daily basis. Any loosening of the standards and it quickly degrades from things everyone finds 
useful on a daily basis to something a single developer finds useful that might have the possibility of being beneficial
to someone else, and at that point the library degrades into an eclectic mess really fast.

But furthermore when you reach that point, some developers can start adding things for the simple reason that they don't 
like the programming language. They might not like the syntax of a for loop or a function call, at which point the
library is starting to get filled with things that's just fighting the fundamental syntax of the language, replacing a 
couple of lines of straightforward code which isn't really duplicating any logic down to a single terse line of exotic
code only familiar to the developer who introduced such a shorthand. Then such a developer might start adding more 
functionality to the common library implemented using such shorthands, at which point significant sections of the common 
library become interwoven with these exotic shorthands which might seem beautiful and intuitive to the developer who 
introduced it but ugly and foreign and hard to understand for everyone else. And at that point I think you know that any 
hope of making something truly "common" is lost, since "common" and "unfamiliar" are polar opposite ideas.

So there's all kinds of cans of worms there, at least in a loosely-coordinated team environment, with a library with 
ambitions as broad and as generalized as just "commonly-used stuff". And while the underlying problem might have been
the loose coordination above all else, at least multiple libraries intended to serve a more singular purpose, like a 
library intended to provide math routines and nothing else, probably wouldn't degrade as significantly in terms of its 
design purity and dependencies as a "common" library. So in retrospect I think it would be much better to err on the
side of libraries which have much more clear design intentions. I've also found over the years that narrow in purpose
and narrow in applicability are radically different ideas. Often the most widely applicable things are the narrowest and 
most singular in purpose, since you can then say, "aha, this is exactly what I need", as opposed to wading through an 
eclectic library of disparate functionality trying to see if it has something you need.

Also I'm admittedly at least a little bit impractical and care maybe a bit too much about aesthetics, but the way I tend
to perceive my idea of a library's quality (and maybe even "beauty") is judged more by its weakest link than its
strongest, in a similar way that if you presented me the most appetitizing food in the world but, on the same plate, put 
something rotting on there that smells really bad, I tend to want to reject the entire plate. And if you're like me in
that regard and make something that invites all sorts of additions as something called "common", you might find yourself 
looking at that analogical plate with something rotting on the side. So likewise I think it's good if a library is 
organized and named and documented in a way such that it doesn't invite more and more and more additions over time. And 
that can even apply to your personal creations, since I've certainly created some rotten stuff here and there, and it 
"taints" a lot less if it's not being added to the biggest plate. Separating things out into small, very singular
libraries has a tendency to better decouple code as well, if only by the sheer virtue that it becomes far less
convenient to start coupling everything.

> Code deduplication has been hammered into me over the years but I feel like I should try it this time around.

What I might suggest in your case is to start to take it easy on code deduplication. I'm not saying to copy and paste
big snippets of poorly-tested, error-prone code around or anything of this sort, or duplicating huge amounts of
non-trivial code that has a decent probability of requiring changes in the future.

But especially if you are of the mindset to create a "common" library, for which I assume your desire is to create 
something widely-applicable, highly reusable, and perhaps ideally something you find just as useful today as you do a 
decade from now, then sometimes you might even need or want some duplication to achieve this elusive quality. Because
the duplication might actually serve as a decoupling mechanism. It's like if you want to separate a video player from an 
MP3 player, then you at least have to duplicate some things like batteries and hard drives. They can't share these
things or else they're indivisibly coupled and cannot be used independently of each other, and at that point people
might not be interested in the device anymore if all they want to do is play MP3s. But some time after you split these
two devices apart, you might find that the MP3 player can benefit from a different battery design or smaller hard drive 
than the video player, at which point you're no longer duplicating anything; what initially started out as duplication
to allow this interdependent device to split into two separate, independent devices might later turn out to yield
designs and implementations that are no longer redundant at all.

It's worth considering things from the perspective of the one using a library. Would you actually want to use a library 
that minimizes code duplication? Chances are that you won't because one that does will naturally depend on other 
libraries. And those other libraries might depend on other libraries to avoid duplicating their code, and so on, until
you might need to import/link 50 different libraries to just to get some basic functionality like loading and playing an 
audio file, and that becomes very unwieldy. Meanwhile if such an audio library deliberately chose to duplicate some
things here and there to achieve its independence, it becomes so much easier to use in new projects, and chances are
that it won't need to be updated nearly as often since it won't need to change as a result of one its dependent external 
libraries changing which might be trying to fulfill a much more generalized purpose than what the audio library needs.

So sometimes it's worth deliberately choosing to duplicate a little bit (consciously, never out of laziness -- actually 
out of diligence) in order to decouple a library and make it independent because, through that independence, it achieves
a wider range of practical applicability and even stability (no more afferent couplings). If you want to design the most 
reusable libraries possible that will last you from one project to the next and over the years, then on top of narrowing 
its scope to the minimum, I would actually suggest considering duplicating a little bit here. And naturally write unit 
tests and make sure it's really thoroughly tested and reliable at what it's doing. This is only for the libraries that
you really want to take the time to generalize to a point that goes far beyond a single project."
