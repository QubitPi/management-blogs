---
layout: post
title: Machine Learning - Concept Learning
tags: [Machine Learning, Concept Learning, FIND-S]
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

### FIND-S Search Algorithm

#### General-to-Specific Ordering of Hypotheses

> Definition: Let $$h_j$$ and $$h_k$$ be boolean-valued functions defined over $$X$$. Then $$h_j$$ is
> **more-general-than-or-equal-to** $$h_k$$ (written $$h_j \geq_g h_k$$) if and only if
> 
> $$ (\forall x \in X)[(h_k(x) = 1) \rightarrow (h_j(x) = 1)] $$

#### FIND-S: Finding A Maximally Specific Hypothesis

How can we use the more-general-than partial ordering to organize the search for a hypothesis consistent with the
observed training examples? One way is to begin with the most specific possible hypothesis in H, then generalize this 
hypothesis each time it fails to cover an observed positive training example:

> 1. Initialize $$h$$ to the most specific hypothesis in $$H$$
> 
> $$ h \leftarrow \langle\emptyset, \emptyset, \emptyset, ..., \emptyset\rangle $$
>
> 2. For each _positive_ training instance $$x$$:
   * For each attribute constraint $$a_i$$, in $$h$$:
     - If the constraint $$a_i$$, is satisfied by $$x$$, then do nothing
     - Else replace $$a_i$$, in $$h$$ by the next more general constraint that is satisfied by $$x$$
> 3. Output hypothesis $$h$$

The FIND-S algorithm simply ignores every negative exampleIn the general case, as long as we assume that the hypothesis 
space $$H$$ contains a hypothesis that describes the true target concept $$c$$ and that the training data contains no 
errors, then the current hypothesis $$h$$ can never require a revision in response to a negative example.

The FIND-S algorithm illustrates one way in which the more-general-than partial ordering can be used to organize the 
search for an acceptable hypothesis. The search moves from hypothesis to hypothesis, searching from the most specific to 
progressively more general hypotheses along one chain of the partial ordering

> **FIND-S is guaranteed to output the most specific hypothesis within $$H$$ that is consistent with the positive
> training examples**.

### Version Spaces and the Candidate-Elimination Algorithm

The Candidate-Elimination Algorithm addresses several of the limitations of FIND-S. Notice that although FIND-S outputs
a hypothesis from $$H$$, that is consistent with the training examples, this is just one of many hypotheses from $$H$$ 
that might fit the training data equally well. The key idea in the Candidate-Elimination Algorithm is to _output a 
description of the set of all hypotheses consistent with the training examples_

The Candidate-Elimination Algorithm represents the set of all hypotheses consistent with the observed training examples. 
This subset of all hypotheses is called the **version space** with respect to the hypothesis space $$H$$ and the
training examples $$D$$

#### The LIST-THEN-ELIMINATE Algorithm

> 1. VersionSpace $$\leftarrow$$ a list containing every hypothesis in $$H$$
> 2. For each training example, $$\langle x, c(x) \rangle$$
>    * remove from VersionSpace any hypothesis h for which h(x) # c(x)
> 3. Output the list of hypotheses in VersionSpace

In principle, the LIST-THEN-ELIMINATE Algorithm can be applied whenever the hypothesis space H is finite

#### The Candidate-Elimination Algorithm

The Candidate-Elimination Algorithm works on the same principle as the LIST-THEN-ELIMINATE Algorithm. However, it
employs a much more compact representation of the version space.

> 1. Initialize $$G$$ to the set of maximally general hypotheses in $$H$$
>    ($$ G_0 \leftarrow {\langle ?, ?, ..., ? \rangle} $$)
> 2. Initialize $$S$$ to the set of maximally specific hypotheses in $$H$$
>    ($$ S_0 \leftarrow {\langle \emptyset, \emptyset, ..., \emptyset \rangle} $$)
> For each training example $$d$$, do
>   * If $$d$$ is a positive example
>     - Remove from $$G$$ any hypothesis inconsistent with $$d$$,
>     - For each hypothesis $$s$$ in $$S$$ that is not consistent with $$d$$
>       * Remove $$s$$ from $$S$$
>       * Add to $$S$$ all minimal generalizations $$h$$ of $$s$$ such that $$h$$ is consistent with $$d$$, and some 
>         member of $$G$$ is more general than $$h$$
>       * Remove from $$S$$ any hypothesis that is more general than another hypothesis in $$S$$
>   * If $$d$$ is a negative example
>     - Remove from $$S$$ any hypothesis inconsistent with $$d$$
>     - For each hypothesis $$g$$ in $$G$$ that is not consistent with $$d$$
>       * Remove $$g$$ from $$G$$
>       * Add to $$G$$ all minimal specializations $$h$$ of $$g$$ such that $$h$$ is consistent with $$d$$, and some 
>         member of $$S$$ is more specific than $$h$$
>       * Remove from $$G$$ any hypothesis that is less general than another hypothesis in $$G$$


> Practical applications of the Candidate-Elimination Algorithm and FIND-S algorithms are limited by the fact that they
> both perform poorly when given noisy training data.