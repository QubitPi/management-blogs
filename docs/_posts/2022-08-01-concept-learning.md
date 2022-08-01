---
layout: post
title: Machine Learning - Concept Learning
tags: [Machine Learning, Concept Learning, Find-S]
color: rgb(0, 204, 0)
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

#### Find-S: Finding A Maximally Specific Hypothesis

How can we use the more-general-than partial ordering to organize the search for a hypothesis consistent with the
observed training examples? One way is to begin with the most specific possible hypothesis in H, then generalize this 
hypothesis each time it fails to cover an observed positive training example:

> 1. Initialize $$h$$ to the most specific hypothesis in $$H$$
> 
> $$ h \leftarrow {$$\emptyset$$, $$\emptyset$$, $$\emptyset$$, ..., $$\emptyset$$} $$
>
> 2. For each _positive_ training instance $$x$$:
   * For each attribute constraint $$a_i$$, in $$h$$:
     - If the constraint $$a_i$$, is satisfied by $$x$$, then do nothing
     - Else replace $$a_i$$, in $$h$$ by the next more general constraint that is satisfied by $$x$$
> 3. Output hypothesis $$h$$

The Find-S algorithm simply ignores every negative exampleIn the general case, as long as we assume that the hypothesis 
space $$H$$ contains a hypothesis that describes the true target concept $$c$$ and that the training data contains no 
errors, then the current hypothesis h can never require a revision in response to a negative example.

The Find-S algorithm illustrates one way in which the more-general-than partial ordering can be used to organize the 
search for an acceptable hypothesis. The search moves from hypothesis to hypothesis, searching from the most specific to 
progressively more general hypotheses along one chain of the partial ordering

> **Find-S is guaranteed to output the most specific hypothesis within H that is consistent with the positive training 
> examples**.
