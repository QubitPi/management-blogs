---
layout: post
title: Machine Learning - Concept Learning
tags: [Machine Learning, Concept Learning]
color: rgb(220, 36, 34)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Much of learning involves acquiring general concepts from specific training examples. Each concept can be thought of as
a boolean-valued function defined over a set of objects. Concept learning is a Machine Learning that approximates to
this bollean-valued function through labeled instances.

<!--more-->

* TOC
{:toc}

Definition
----------

> Concept learning is a learning of inferring a boolean-valued function from training examples of its input and output.
> 
> This is, given:
>
> * Instances set of instances, X, each described by the attributes
>   * A1, with possible values of {?, $$\emptyset$$, a specific value,}
>   * A2, with possible values of {?, $$\emptyset$$, a specific value,} 
>   * A3, with possible values of {?, $$\emptyset$$, a specific value,}
>   * ...
> 
> * and Hypotheses _H_: Each hypothesis is described by a conjunction of constraints on the attributes A1, A2, A3, ...,
>   each of the constraints may be "?" (any value is acceptable), $$\emptyset$$ (no value is acceptable), or a specific
>   value
> * Target concept $$c : X \rightarrow {0, 1}$$
> * Training examples _D_: Positive and negative examples of the target function (see Table 2.1).
>
> Determine hypothesis _h_ in _H_ such that $$h(x) = c(x)$$ for all x in X.

When learning the target concept, the learner is presented a set of training examples, each consisting of an instance
$$x$$ from $$X$$, along with its target concept value $$c(x)$$. Instances for which $$c(x) = 1$$ are called **positive 
examples**, or members of the target concept. Instances for which $$c(x) = 0$$ are called **negative examples**, or
nonmembers of the target concept.  We will often write the ordered pair $$\langle x, c(x) \rangle$$ to describe the 
training example consisting of the instance $$x$$ and its target concept value $$c(x)$$. We use the symbol $$D$$ to
denote the set of available training examples
