---
layout: post
title: Machine Learning Basics
tags: [Machine Learning]
category: FINALIZED
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Concept Learning
----------------

Much of learning involves acquiring general concepts from specific training examples. Each concept can be thought of as
a boolean-valued function defined over a set of objects. Concept learning is a Machine Learning that approximates to
this boolean-valued function through labeled instances.

> Concept learning is a learning of inferring a boolean-valued function from training examples of its input and output.
>
> That is, given:
>
> * A set of instances, **$$\mathit{X}$$**, each described by the attributes
>   * $$\mathit{a_1}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * $$\mathit{a_2}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * $$\mathit{a_3}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * ...
>
> * and Hypotheses $$\mathit{H}$$: Each hypothesis is described by a conjunction of constraints on the attributes
>   $$\mathit{a_1}$$, $$\mathit{a_2}$$, $$\mathit{a_3}$$, ..., each of the constraints may be "?" (any value is
>   acceptable), $$\mathit{\emptyset}$$ (no value is acceptable), or a specific value
> * Target concept $$\mathit{c : X \rightarrow {0, 1}}$$
> * Training examples $$\mathit{D}$$: Positive and negative examples of the target function
>
> Determine (i.e. Learn) hypothesis $$\mathit{h}$$ in $$\mathit{H}$$ such that $$\mathit{h(x) = c(x)}$$ for all
> $$\mathit{x}$$ in $$\mathit{X}$$.

When learning the target concept, the learner is presented a set of training examples, each consisting of an instance
$$\mathit{x}$$ from $$\mathit{X}$$, along with its target concept value $$\mathit{c(x)}$$. Instances for which
$$\mathit{c(x) = 1}$$ are called **positive examples**, or members of the target concept. Instances for which
$$\mathit{c(x) = 0}$$ are called **negative examples**, or nonmembers of the target concept. We will often write the
ordered pair $$\mathit{\langle x, c(x) \rangle}$$ to describe the training example consisting of the instance
$$\mathit{x}$$ and its target concept value $$\mathit{c(x)}$$. We use the symbol $$\mathit{D}$$ to denote the set of
available training examples

The best hypothesis learned regarding unseen instances is the hypothesis that best fits the observed training
data. This is the **fundamental assumption of inductive learning**

> **The Inductive Learning Hypothesis**: Any hypothesis found to approximate the target function well over a
> sufficiently large set of training examples will also approximate the target function well over other unobserved
> examples.

Concept learning can be viewed as the task of searching through a large space of hypotheses implicitly defined by the
hypothesis representation. Most practical learning tasks involve very large, sometimes infinite, hypothesis spaces.
Machine Learning, therefore, is interested in being capable of efficiently searching very large or infinite hypothesis
spaces, to find the hypotheses that best fit the training data, which we shall discuss next

### FIND-S Search Algorithm - Finding A Maximally Specific Hypothesis

> **General-to-Specific Ordering of Hypotheses**
>
> Definition: Let $$\mathit{h_j}$$ and $$\mathit{h_k}$$ be boolean-valued functions defined over $$\mathit{X}$$. Then 
> $$\mathit{h_j}$$ is **more-general-than-or-equal-to** $$\mathit{h_k}$$ (written as $$\mathit{h_j \geq_g h_k}$$) if and 
> only if
>
> $$ \mathit{(\forall x \in X)[(h_k(x) = 1) \rightarrow (h_j(x) = 1)]} $$

How can we use the more-general-than partial ordering to organize the search for a hypothesis consistent with the
observed training examples? One way is to begin with the most specific possible hypothesis in H, then generalize this
hypothesis each time it fails to cover an observed positive training example:

> 1. Initialize $$\mathit{h}$$ to the most specific hypothesis in $$\mathit{H}$$
>
> $$\mathit{ h \leftarrow \langle\emptyset, \emptyset, \emptyset, ..., \emptyset\rangle }$$
>
> 2. For each _positive_ training instance $$\mathit{x}$$:
>    * For each attribute constraint $$\mathit{a_i}$$, in $$\mathit{h}$$:
>      - If the constraint $$\mathit{a_i}$$, is satisfied by $$\mathit{x}$$, then do nothing
>      - Else replace $$\mathit{a_i}$$, in $$\mathit{h}$$ by the next more general constraint that is satisfied by
>        $$\mathit{x}$$
> 3. Output hypothesis $$\mathit{h}$$

The FIND-S algorithm simply ignores every negative example. In the general case, as long as we assume that the
hypothesis space $$\mathit{H}$$ contains a hypothesis that describes the true target concept $$\mathit{c}$$ and that the 
training data contains no errors, then the current hypothesis $$\mathit{h}$$ can never require a revision in response to
a negative example.

The FIND-S algorithm illustrates one way in which the more-general-than partial ordering can be used to organize the
search for an acceptable hypothesis. The search moves from hypothesis to hypothesis, searching from the most specific to
progressively more general hypotheses along one chain of the partial ordering

FIND-S is guaranteed to output the most specific hypothesis within $$\mathit{H}$$ that is consistent with the positive 
training examples.

### The Candidate-Elimination Algorithm

The Candidate-Elimination Algorithm addresses several of the limitations of FIND-S. Notice that although FIND-S outputs
a hypothesis from $$\mathit{H}$$, that is consistent with the training examples, this is just one of many hypotheses
from $$\mathit{H}$$ that might fit the training data equally well. The key idea in the Candidate-Elimination Algorithm
is to _output a description of the set of all hypotheses consistent with the training examples_

The Candidate-Elimination Algorithm represents the set of all hypotheses consistent with the observed training examples.
This subset of all hypotheses is called the **version space** with respect to the hypothesis space $$\mathit{H}$$ and
the training examples $$\mathit{D}$$

> **The LIST-THEN-ELIMINATE Algorithm**
>
> 1. VersionSpace $$\leftarrow$$ a list containing every hypothesis in $$\mathit{H}$$
> 2. For each training example, $$\mathit{\langle x, c(x) \rangle}$$
>    * remove from VersionSpace any hypothesis h for which $$\mathit{h(x) != c(x)}$$
> 3. Output the list of hypotheses in VersionSpace

In principle, the LIST-THEN-ELIMINATE Algorithm can be applied whenever the hypothesis space $$\mathit{H}$$ is finite

The Candidate-Elimination Algorithm works on the same principle as the LIST-THEN-ELIMINATE Algorithm. However, it
employs a much more compact representation of the version space.

> 1. Initialize $$\mathit{G}$$ to the set of maximally general hypotheses in $$\mathit{H}$$
>    ($$ \mathit{G_0 \leftarrow {\langle ?, ?, ..., ? \rangle}} $$)
> 2. Initialize $$\mathit{S}$$ to the set of maximally specific hypotheses in $$\mathit{H}$$
>    ($$ \mathit{S_0 \leftarrow {\langle \emptyset, \emptyset, ..., \emptyset \rangle}} $$)
>
> For each training example $$\mathit{d}$$, do
>   * If $$\mathit{d}$$ is a positive example
>     - Remove from $$\mathit{G}$$ any hypothesis inconsistent with $$\mathit{d}$$,
>     - For each hypothesis $$\mathit{s}$$ in $$\mathit{S}$$ that is not consistent with $$\mathit{d}$$
>       * Remove $$\mathit{s}$$ from $$\mathit{S}$$
>       * Add to $$\mathit{S}$$ all minimal generalizations $$\mathit{h}$$ of $$\mathit{s}$$ such that $$\mathit{h}$$ is 
>         consistent with $$\mathit{d}$$, and some member of $$\mathit{G}$$ is more general than $$\mathit{h}$$
>       * Remove from $$\mathit{S}$$ any hypothesis that is more general than another hypothesis in $$\mathit{S}$$
>   * If $$\mathit{d}$$ is a negative example
>     - Remove from $$\mathit{S}$$ any hypothesis inconsistent with $$\mathit{d}$$
>     - For each hypothesis $$\mathit{g}$$ in $$\mathit{G}$$ that is not consistent with $$\mathit{d}$$
>       * Remove $$\mathit{g}$$ from $$\mathit{G}$$
>       * Add to $$\mathit{G}$$ all minimal specializations $$\mathit{h}$$ of $$\mathit{g}$$ such that $$\mathit{h}$$ is 
>         consistent with $$\mathit{d}$$, and some member of $$\mathit{S}$$ is more specific than $$\mathit{h}$$
>       * Remove from $$\mathit{G}$$ any hypothesis that is less general than another hypothesis in $$\mathit{G}$$

Practical applications of the Candidate-Elimination Algorithm and FIND-S algorithms are limited by the fact that they
both perform poorly when given noisy training data.


Decision Tree Learning
----------------------

Decision tree learning is one of the most widely used and practical methods for inductive inference. It is a method for
approximating discrete-valued functions that is robust to noisy data and capable of learning disjunctive expressions

Decision tree learning is a method for approximating discrete-valued target functions, in which the learned function is
represented by a decision tree. Learned trees can also be re-represented as sets of if-then rules to improve human
readability. These learning methods are among the most popular of inductive inference algorithms and have been
successfully applied to a broad range of tasks from learning to diagnose medical cases to learning to assess credit risk
of loan applicants.

Decision tree learning is generally best suited to problems (**Classification Problems**) with the following
characteristics

* Instances are represented by attribute-value pairs
* The target function has discrete output values
* Disjunctive descriptions may be required
* The training data may contain errors
* The training data may contain missing attribute values