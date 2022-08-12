---
layout: post
title: Machine Learning - Philosophy of Learning
tags: [Machine Learning, Philosophy, Immanuel Kant]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

Machine learning draws on ideas from a diverse set of disciplines, including artificial intelligence, probability and 
statistics, computational complexity, information theory, psychology and neurobiology, control theory, and
**Philosophy**. For example, in Philosophy:

* Occam's razor, suggests that the simplest hypothesis is the best.
* Analysis of the justification for generalizing beyond observed data.

**This post is purely my exploration of philosophical disciplines on the design and implementation of ML & AI
algorithms**, with an exclusive scope on **Immanuel Kant** Philosophy. I chose this topic because I believe any
successful ML & AI applications in the world of business must come from a _profound_ design and implementation of
some intelligent system which is not possible without some rigorous justifications. AI & ML, in my opinion, is about the
philosophy of learning applied to machines (I came to this conclusion after my
[university course on Machine Learning](https://courses.engr.illinois.edu/cs446/sp2015/index.html)). The accuracy of an 
intelligent system pretty much depend on how we frame and bias a learning algorithm. The approach to that requires a
solid understanding of learning theory, which is why it has been stated at the beginning of this post that Machine 
Learning draws from the discipline of Philosophy, and hence, I assembled this blog post.

How I ended up studying Kant's Philosophy for ML & AI is probably out of interests of most readers, but what I can tell
is that I found Kant's approach to human learning strategy works pretty well in many quotidian tasks in our daily lives.
I believe his rigorous treatise on learning theory will work for machine as well and help us design better ML & AI 
algorithms.

* TOC
{:toc}

Critique of Pure Reason
-----------------------

The Critique of Pure Reason is a book by Kant, in which he seeks to determine the limits and scope of
[metaphysics](https://en.wikipedia.org/wiki/Metaphysics). Also referred to as Kant's "First Critique", it was followed
by his Critique of Practical Reason (1788) and Critique of Judgment (1790). 

### a priori

_Kant argues that our mathematical, physical, and quotidian knowledge of nature requires certain judgments that are 
"synthetic"_ rather than "analytic," that is, going beyond what can be known solely in virtue of the contents of the 
concepts involved in them and the application of the logical principles of identity and contradiction to these concepts, 
and yet also knowable **a priori**, that is, independently of any particular experience since no particular experience
could ever be sufficient to establish the universal and necessary validity of these judgments.

Kant agrees with Locke that we have no innate knowledge, that is, no knowledge of any particular propositions implanted
in us by God or nature prior to the commencement of our individual experience. But experience is the product both of 
_external objects_ affecting our sensibility and of the _operation_ of our cognitive faculties in response to this
effect, and Kant's claim is that **we can have "pure" or _a priori_ cognition of the contributions to experience made by 
the operation of these faculties themselves, rather than of the effect of external objects on us in experience**.

Kant divides our cognitive capacities into

1. our receptivity to the effects of external objects acting on us and giving us sensations, through which these objects 
   are given to us in empirical intuition, and
2. our active faculty for relating the data of intuition by thinking them under concepts, which is called understanding, 
   and forming judgments about them

This division is the basis for Kant's division of the "Transcendental Doctrine of Elements" into

1. the "Transcendental Aesthetic," which deals with sensibility and its pure form, and
2. the "Transcendental Logic," which deals with the operations of the understanding and judgment as well as both the
   spurious and the legitimate activities of theoretical reason

> a priori is the Initial Parameters Set in ML & AI Algorithms
>
> Recall from the
> [Backpropagation algorithm in artificial neural networks](https://qubitpi.github.io/jersey-guide/2022/08/09/artificial-neural-networks.html#the-backpropagation-algorithm),
> that we gave some very small initial weights which later were adjusted in accordance with the learning data set. 
> 
> **Assigning initial weights mirrors the concept of "a priori"**. It is important to, during the design of algorithm,
> keep drawing from how "a priori" works. **The analogy of "a priori" should guide our initial assignments to those
> weight values**.

More discussion of "a priori" coming...

### "Transcendental Aesthetic": Space, Time, and Transcendental Idealism

> [In Kantian philosophy](https://www.merriam-webster.com/dictionary/transcendental), **transcendental** is
>
> 1. of or relating to experience as determined by the mind's makeup
> 2. [transcending](https://www.merriam-webster.com/dictionary/transcending) experience but not human knowledge

Kant attempts to distinguish the contribution to cognition made by our receptive faculty of sensibility from that made 
solely by the objects that affect us, and argues that **space and time are pure forms of all intuition contributed by
our own faculty of sensibility, and therefore forms of which we can have a priori knowledge**

Space and time are neither subsistent beings nor inherent in things as they are in themselves, but are rather only forms
of our sensibility, hence conditions under which objects of experience can be given at all and the fundamental principle
of their representation and individuation

Kant's thesis that space and time are pure forms of intuition leads him to the paradoxical conclusion that although
space and time are _empirically real_, they are _transcendentally ideal_, and so are the objects given in them. Although 
the precise meaning of this claim remains subject to debate, in general terms it is the claim that _it is only from the 
human standpoint that we can speak of space, time, and the spatiotemporality of the objects of experience, thus that we 
cognize these things not as they are in themselves but only as they appear under the conditions of our sensibility_.
This is Kant's famous doctrine of **Transcendental Idealism**, _which can be employed throughout the learning process of
a machine_

### "Transcendental Analytic": the Metaphysical and Transcendental Deductions

#### Analytic of Concepts

In the "Analytic of Concepts," Kant presents the understanding as the source of certain concepts that are _a priori_ and 
are conditions of the possibility of any experience whatever. These twelve basic concepts, which Kant calls the 
**categories**, are fundamental concepts of an object in general, or the forms for any particular concepts of objects,
and in conjunction with the _a priori_ forms of intuition are the basis of all synthetic _a priori_ cognition.

Kant derives the twelve categories from a table of the twelve logical functions or forms of judgments, the logically 
significant aspects of all judgments. Kant's idea is that just as there are certain essential features of all judgments, 
so there must be certain corresponding ways in which we form the concepts of objects so that judgments may be about
objects.

There are 4 main logical features of judgments:

1. their quantity, or the scope of their subject-terms;
2. the quality of their predicate-terms, whose contents are realities and negations;
3. their relation, or whether they assert a relation just between a subject and predicate or between two or more 
   subject-predicate judgments; and
4. their modality, or whether they assert a possible, actual, or necessary truth

Under each of these four headings there are supposed to be three different options:

![Error loading kant-12-categories.png]({{ "/assets/img/kant-12-categories.png" | relative_url}})

Kant holds there to be twelve fundamental categories for conceiving of the quantity, quality, relation, and modality of 
objects

To be continued ...