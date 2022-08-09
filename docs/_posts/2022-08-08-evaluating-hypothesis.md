---
layout: post
title: Machine Learning - Evaluating Hypothesis
tags: [Machine Learning]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Estimating the accuracy of a hypothesis is relatively straightforward when data is plentiful. However, when we must
learn a hypothesis and estimate its future accuracy given only a limited set of data, two key difficulties arise

1. (Bias) _The observed accuracy of the learned hypothesis over the training examples is often a poor estimator of its accuracy
   over future examples_. This is especially likely when the learner considers a very rich hypothesis space, enabling it
   to overfit the training examples. To obtain an unbiased estimate of future accuracy, we typically test the hypothesis
   on some set of test examples chosen independently of the training examples and the hypothesis.
2. (Variance) The measured accuracy can vary from the true accuracy, depending on the makeup of the particular set of
   test examples. The smaller the set of test examples, the greater the expected variance

The literature on statistical tests for hypotheses is very large. This post provides an _introductory overview_ that
focuses only on the issues most directly relevant to learning, evaluating, and comparing hypotheses. The rest may be
dedicated to future posts.


Estimating Hypothesis Accuracy
------------------------------

> **Context**
> 
> There is some space of possible instances $$\mathit{X}$$ over which various target functions may be defined. We assume
> that different instances in $$\mathit{X}$$ may be encountered with different frequencies. A convenient way to model
> this is to assume there is some unknown probability distribution $$\mathcal{D}$$ that defines the probability of 
> encountering each instance in $$\mathit{X}$$. The learning task is to learn the target concept or target function
> $$\mathit{f}$$ by considering a space $$\mathit{H}$$ of possible hypotheses. Training examples of the target function 
> $$\mathit{f}$$ are provided to the learner by a trainer who draws each instance independently, according to the 
> distribution $$\mathcal{D}$$, and who then forwards the instance $\mathit{x}$ along with its correct target value 
> $$\mathit{f(x)}$$ to the learner.

Within this general setting we are interested in the following two questions:

1. Given a hypothesis $$\mathit{h}$$ and a data sample containing $$\mathit{n}$$ examples drawn at random according to
   the distribution $$\mathcal{D}$$, what is the best estimate of the accuracy of $$\mathit{h}$$ over future instances
   drawn from the same distribution?
2. What is the probable error in this accuracy estimate?

### Sample Error and True Error

To answer these questions, we need to distinguish carefully between two notions of accuracy or, equivalently, error.

1. The error rate of the hypothesis over the sample of data that is available, namely the **sample error**
2. The error rate of the hypothesis over the entire unknown distribution $$\mathcal{D}$$ of examples, which is called the
   **true error**

The _sample error_ of a hypothesis with respect to some sample $$\mathit{S}$$ of instances drawn from $$\mathit{X}$$ is
the fraction of $$\mathit{S}$$ that it misclassifies:

> **Definition**
> 
> The **sample error**, denoted as $$\text{error}_\mathit{s}\mathit{(h)}$$, of hypothesis $$\mathit{h}$$ with respect to
> target function $$\mathit{f(x)}$$ and data sample $$\mathit{S}$$ is
> 
> $$ \text{error}_\mathit{s}\mathit{(h)} \equiv \frac{1}{n}\sum_{\mathit{x \in S}}\delta(\mathit{f(x)}, \mathit{h(x)}) $$
> 
> where $$\mathit{x}$$ is the number of examples in $$\mathit{S}$$, and the quantity
> $$\delta(\mathit{f(x)}, \mathit{h(x)})$$ is 1 if $$\mathit{f(x)} \neq \mathit{h(x)}$$, and 0 otherwise
