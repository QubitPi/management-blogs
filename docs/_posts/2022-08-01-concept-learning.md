---
layout: post
title: Machine Learning - Concept Learning
tags: [Machine Learning, Concept Learning, Find-S]
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
> * Instances set of instances, **$$X$$**, each described by the attributes
>   * $$a_1$$, with possible values of {$$?$$, $$\emptyset$$, a specific value}
>   * $$a_2$$, with possible values of {$$?$$, $$\emptyset$$, a specific value} 
>   * $$a_3$$, with possible values of {$$?$$, $$\emptyset$$, a specific value}
>   * ...
> 
> * and Hypotheses _$$H$$_: Each hypothesis is described by a conjunction of constraints on the attributes $$a_1$$,
>   $$a_2$$, $$a_3$$, ..., each of the constraints may be "?" (any value is acceptable), $$\emptyset$$ (no value is 
>   acceptable), or a specific value
> * Target concept $$c : X \rightarrow {0, 1}$$
> * Training examples $$D$$: Positive and negative examples of the target function
>
> Determine (i.e. Learn) hypothesis $$h$$ in $$H$$ such that $$h(x) = c(x)$$ for all $$x$$ in $$X$$.

When learning the target concept, the learner is presented a set of training examples, each consisting of an instance
$$x$$ from $$X$$, along with its target concept value $$c(x)$$. Instances for which $$c(x) = 1$$ are called **positive 
examples**, or members of the target concept. Instances for which $$c(x) = 0$$ are called **negative examples**, or
nonmembers of the target concept.  We will often write the ordered pair $$\langle x, c(x) \rangle$$ to describe the 
training example consisting of the instance $$x$$ and its target concept value $$c(x)$$. We use the symbol $$D$$ to
denote the set of available training examples


The Inductive Learning Hypothesis
---------------------------------

Informaly, the best hypothesis learned regarding unseen instances is the hypothesis that best fits the observed training 
data. This is the **fundamental assumption of inductive learning**

> The Inductive Learning Hypothesis: Any hypothesis found to approximate the target function well over a sufficiently
> large set of training examples will also approximate the target function well over other unobserved examples.


Concept Learning as Search
--------------------------

Concept learning can be viewed as the task of searching through a large space of hypotheses implicitly defined by the 
hypothesis representation. Most practical learning tasks involve very large, sometimes infinite, hypothesis spaces.

Machine Learning, hence, is interested in being capable of efficiently searching very large or infinite hypothesis
spaces, to find the hypotheses that best fit the training data, which we shall discuss next


### Find-S Search Algorithm

#### General-to-Specific Ordering of Hypotheses

> Definition: Let $$h_j$$ and $$h_k$$ be boolean-valued functions defined over $$X$$. Then $$h_j$$ is
> more-general-than-or-equal-to $$h_k$$ (written $$h_j \geq_g h_k$$) if and only if
> 
> $$ (\forall x \in X)[(h_k(x) = 1) \rightarrow (h_j(x) = 1)] $$