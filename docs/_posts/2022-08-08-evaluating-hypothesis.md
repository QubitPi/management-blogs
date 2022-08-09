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

Estimating the accuracy of a hypothesis is relatively straightforward when data is plentiful. However, when we must
learn a hypothesis and estimate its future accuracy given only a limited set of data, two key difficulties arise

1. (Bias in the estimate) _The observed accuracy of the learned hypothesis over the training examples is often a poor 
   estimator of its accuracy over future examples_. This is especially likely when the learner considers a very rich 
   hypothesis space, enabling it to overfit the training examples. To obtain an unbiased estimate of future accuracy, we 
   typically test the hypothesis on some set of test examples chosen independently of the training examples and the 
   hypothesis.
2. (Variance in the estimate) The measured accuracy can vary from the true accuracy, depending on the makeup of the 
   particular set of test examples. The smaller the set of test examples, the greater the expected variance

The literature on statistical tests for hypotheses is very large. This post provides an _introductory overview_ that
focuses only on the issues most directly relevant to learning, evaluating, and comparing hypotheses. The rest may be
dedicated to future posts.

* TOC
{:toc}

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
> distribution $$\mathcal{D}$$, and who then forwards the instance $$\mathit{x}$$ along with its correct target value 
> $$\mathit{f(x)}$$ to the learner.

Within this general setting we are interested in the following two questions:

1. **Given a hypothesis $$\mathit{h}$$ and a data sample containing $$\mathit{n}$$ examples drawn at random according to
   the distribution $$\mathcal{D}$$, what is the best estimate of the accuracy of $$\mathit{h}$$ over future instances
   drawn from the same distribution?**
2. **What is the probable error in this accuracy estimate?**

### Sample Error and True Error

To answer these questions, we need to distinguish carefully between two notions of accuracy or, equivalently, error.

1. The error rate of the hypothesis over the sample of data that is available, namely the **sample error**
2. The error rate of the hypothesis over the entire unknown distribution $$\mathcal{D}$$ of examples, which is called the
   **true error**

The _sample error_ of a hypothesis with respect to some sample $$\mathit{S}$$ of instances drawn from $$\mathit{X}$$ is
the fraction of $$\mathit{S}$$ that it misclassifies:

> **Definition**
> 
> The **sample error**, denoted as $$\text{error}_\mathit{S}\mathit{(h)}$$, of hypothesis $$\mathit{h}$$ with respect to
> target function $$\mathit{f(x)}$$ and data sample $$\mathit{S}$$ is
> 
> $$ \text{error}_\mathit{S}\mathit{(h)} \equiv \frac{1}{n}\sum_{\mathit{x \in S}}\delta(\mathit{f(x)}, \mathit{h(x)}) $$
> 
> where $$\mathit{x}$$ is the number of examples in $$\mathit{S}$$, and the quantity
> $$\delta(\mathit{f(x)}, \mathit{h(x)})$$ is 1 if $$\mathit{f(x)} \ne \mathit{h(x)}$$, and 0 otherwise

The **true error** of a hypothesis is the probability that it will misclassify a instance randomly drawn from the
distribution $$\mathcal{D}$$

> **Definition**
> 
> The **true error** (denoted as $$\text{error}_{\mathcal{D}}\mathit{(h)}$$) of hypothesis $$\mathit{h}$$
> with respect to target function $$\mathit{f(x)}$$ and distribution $$\mathcal{D}$$ is the probability that
> $$\mathit{h}$$ will misclassify an instance drawn at random according to $$\mathcal{D}$$
> 
> $$ \text{error}_{\mathcal{D}}\mathit{(h)} \equiv \underset{\mathit{x \in \mathcal{D}}}{\text{Pr}} \left[ \mathit{f(x)} \ne \mathit{h(x)} \right] $$
> 
> where $$\underset{\mathit{x \in \mathcal{D}}}{\text{Pr}}$$ means that the probability is taken over the instance
> distribution \mathcal{D}

**What we usually wish to know is the true error $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ of the hypothesis, because
this is the error we can expect when applying the hypothesis to future examples. All we can measure, however, is the
sample error $$\text{error}_\mathit{S}\mathit{(h)}$$ of the hypothesis for the data sample $$\mathit{S}$$ that we happen
to have in hand. The main question is "How good an estimate of $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ is provided by
$$\text{error}_\mathit{S}\mathit{(h)}$$?"

### True Error for Discrete-Valued Hypotheses

We can answer the question above for $$\mathit{h}$$ that is a discrete-valued hypothesis based on its observed sample
error over a sample $$\mathit{S}$$ where

* the sample $$\mathit{S}$$ contains $$\mathit{n}$$ examples drawn independent of one another, and independent of 
  $$\mathit{h}$$, according to the probability distribution $$\mathcal{D}$$
* $$\mathit{n} \ge 30$$
* hypothesis $$\mathit{h}$$ commits $$\mathit{r}$$ errors over these $$\mathit{n}$$ examples (i.e., $$\text{error}_\mathit{S}\mathit{(h)} = \mathit{\frac{r}{n}}$$).

Under these conditions, statistical theory allows us to make the following assertions:

1. Given no other information, the most probable value of $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ is
   $$\text{error}_\mathit{S}\mathit{(h)}$$
2. With approximately 95% probability, the true error $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ lies in the interval

   $$ \text{error}_{\mathit{S}}\mathit{(h)} \pm 1.96\sqrt{\frac{\text{error}_\mathit{S}\mathit{(h)}\left( 1 - \text{error}_\mathit{S}\mathit{(h)} \right)}{\mathit{n}}} $$

The expression above for the 95% confidence interval can be generalized to any desired confidence level. The constant
1.96 is used in case we desire a 95% confidence interval. A different constant, $$\mathit{z_N}$$, is used to calculate
the _N_% confidence interval. The general expression for approximate _N_% confidence intervals
for $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ is

$$ \text{error}_{\mathit{S}}\mathit{(h)} \pm \mathit{z_N}\sqrt{\frac{\text{error}_\mathit{S}\mathit{(h)}\left( 1 - \text{error}_\mathit{S}\mathit{(h)} \right)}{\mathit{n}}} $$

where the constant $$\mathit{z_N}$$ is chosen depending on the desired confidence level, using the values of
$$\mathit{z_N}$$ given in table below

| **Confidence Level _N_%**     | 50%  | 68%  | 80%  | 90%  | 95%  | 98%  | 99%  |
|:-----------------------------:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
| **Constant $$\mathit{z_N}$$** | 0.67 | 1.00 | 1.28 | 1.64 | 1.96 | 2.33 | 2.58 |

> ⚠️ It is important to keep in mind that the generalized equation above applies only to discrete-valued hypotheses,
> that it assumes the sample $$\mathit{S}$$ is drawn at random using the same distribution from which future data will
> be drawn, and that it assumes the data is independent of the hypothesis being tested. We should also keep in mind that
> the expression provides only an approximate confidence interval, though the approximation is quite good when the
> sample contains at least 30 examples, and $$\text{error}_\mathit{S}\mathit{(h)}$$ is NOT too close to 0 or 1. A more 
> accurate rule of thumb is that the above approximation works well when
> 
> $$ \mathit{n} \times \text{error}_\mathit{S}\mathit{(h)}\left( 1 - \text{error}_\mathit{S}\mathit{(h)} \right) \ge 5 $$
