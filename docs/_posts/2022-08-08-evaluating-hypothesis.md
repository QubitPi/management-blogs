---
layout: post
title: Machine Learning - Evaluating Hypothesis
tags: [Machine Learning]
category: WIP
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

In many cases it is important to evaluate the performance of learned hypotheses as precisely as possible. One reason is 
simply to understand whether to use the hypothesis. For instance, when learning from a limited-size database indicating
the effectiveness of different medical treatments, it is important to understand as precisely as possible the accuracy
of the learned hypotheses. A second reason is that evaluating hypotheses is an integral component of many learning
methods. For example, in post-pruning decision trees to avoid overfitting, we must evaluate the impact of possible
pruning steps on the accuracy of the resulting decision tree. Therefore it is important to understand the likely errors 
inherent in estimating the accuracy of the pruned and unpruned tree.

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
> $$\delta(\mathit{f(x)}, \mathit{h(x)})$$ is 1 if $$\mathit{[f\ (x) ⍯ h(x)]}$$, and 0 otherwise

The **true error** of a hypothesis is the probability that it will misclassify a instance randomly drawn from the
distribution $$\mathcal{D}$$

> **Definition**
> 
> The **true error** (denoted as $$\text{error}_{\mathcal{D}}\mathit{(h)}$$) of hypothesis $$\mathit{h}$$
> with respect to target function $$\mathit{f(x)}$$ and distribution $$\mathcal{D}$$ is the probability that
> $$\mathit{h}$$ will misclassify an instance drawn at random according to $$\mathcal{D}$$
> 
> $$ \text{error}_{\mathcal{D}}\mathit{(h)} \equiv \underset{\mathit{x \in \mathcal{D}}}{\text{Pr}} \mathit{[f\ (x) ⍯ h(x)]} $$
> 
> where $$\underset{\mathit{x \in \mathcal{D}}}{\text{Pr}}$$ means that the probability is taken over the instance
> distribution $$\mathcal{D}$$

**What we usually wish to know is the true error $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ of the hypothesis**, because
this is the error we can expect when applying the hypothesis to future examples. All we can measure, however, is the
sample error $$\text{error}_\mathit{S}\mathit{(h)}$$ of the hypothesis for the data sample $$\mathit{S}$$ that we happen
to have in hand. The main question is "**How good an estimate of $$\text{error}_{\mathcal{D}}\mathit{(h)}$$ is provided
by $$\text{error}_\mathit{S}\mathit{(h)}$$?**"

### True Error for Discrete-Valued Hypotheses

We can answer the question above for $$\mathit{h}$$ that is a discrete-valued hypothesis based on its observed sample
error over a sample $$\mathit{S}$$ where

* the sample $$\mathit{S}$$ contains $$\mathit{n}$$ examples drawn independent of one another, and independent of 
  $$\mathit{h}$$, according to the probability distribution $$\mathcal{D}$$
* $$\mathit{n} \ge 30$$ for approximation to look good
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


Basics of Sampling Theory
-------------------------

> Here is a well-studies problem in statistics:
> 
> Estimating the proportion of a population that exhibits some property, given the observed proportion over some random
> sample of the population

In our case, the property of interest is that $$\mathit{h}$$ misclassifies the example.

### Random Variable

We collect a random sample $$\mathit{S}$$ of $$\mathit{n}$$ independently drawn instances from the distribution
$$\mathcal{D}$$, and then measure the sample error $$\text{error}_\mathit{S}\mathit{(h)}$$. If we were to repeat this 
experiment many times, each time drawing a different random sample $$\mathit{S_i}$$ of size $$\mathit{n}$$, we would 
expect to observe different values for the various $$\text{error}_\mathit{S}\mathit{(h)}$$, depending on random 
differences in the makeup of the various $$\mathit{S_i}$$. We say in such cases that
$$\text{error}_\mathit{S}\mathit{(h)}$$, the outcome of the _i_-th such experiment, is a **random variable**. The value
of random variable is the observed outcome of a random experiment

### Binomial Distribution

Imagine that we were to run $$\mathit{k}$$ such random experiments, measuring the random variables
$$\text{error}_\mathit{S_1}\mathit{(h)}$$, $$\text{error}_\mathit{S_2}\mathit{(h)}$$, ...,
$$\text{error}_\mathit{S_k}\mathit{(h)}$$. Imagine further that we then plotted a histogram displaying the frequency
with which we observed each possible error value. As $$\mathit{k}$$ grows, the histogram might approach the form
of the distribution shown below. This table describes a particular probability distribution called the **Binomial 
distribution**.

![Error loading binomial-distribution-eg.png]({{ "/assets/img/binomial-distribution-eg.png" | relative_url}})

A Binomial distribution gives the probability of observing $$\mathit{r}$$ errors in a sample of $$\mathit{n}$$
randomly drawn instances, when the probability of error is $$\mathit{p} = \text{error}_{\mathcal{D}}\mathit{(h)}$$. It
is defined by the probability function

$$\mathit{P(r) = \frac{n!}{r!(n - r)!}p^r(1 - p)^{n - r}}$$

If the random variable $$\mathit{X}$$ follows a binomial distribution, then

* the probability $$Pr(X = r)$$ that $$\mathit{X}$$ will take on the value $$\mathir{r}$$ is given by $$P(r)$$
* $$\mathit{E[X]}$$, which is the expected or mean value of $$\mathit{X}$$, is
  $$\mathit{E[X] = np}$$
* the variance of $$\mathit{X}$$, $$\mathit{Var\left( X \right)}$$, is $$\mathit{Var\left( X \right) = np(1 - p)}$$
* the standard deviation of $$\mathit{X}$$, $$\mathit{\sigma_X}$$, is $$\mathit{\sigma_X = \sqrt{np(1 - p)}}$$

The general setting to which the Binomial distribution applies is:

1. There is a base, or underlying, experiment whose outcome can be described by a random variable, $$\mathit{Y}$$. The 
   random variable $$\mathit{Y}$$ can take on two possible values, 1 or 0
2. The probability that $$\mathit{Y} = 1$$ on any single trial of the underlying experiment is given by some constant 
   $$\mathit{p}$$, independent of the outcome of any other experiment. The probability that $$\mathit{Y} = 0$$ is 
   therefore $$(1 - p)$$. Typically, **$$\mathit{p} = \text{error}_{\mathcal{D}}\mathit{(h)}$$ is not known in advance, 
   and the goal is to estimate it**
3. A series of $$\mathit{n}$$ independent trials of the underlying experiment is performed, producing the sequence of 
   independent random variables $$\mathit{Y_1}$$, $$\mathit{Y_2}$$, ..., $$\mathit{Y_n}$$. Let $$\mathit{R}$$ denote the
   number of trials for which $$\mathit{Y_i} = 1$$ in this series of $$\mathit{n}$$ experiments, i.e.
   $$\mathit{R \equiv \sum_{i = 1}^{n}Y_i}$$
4. The probability that the random variable $$\mathit{R}$$ will take on a specific value $$\mathit{r}$$ (the probability 
   of observing exactly $$\mathit{r}$$ errors) is given by the binomial distribution
