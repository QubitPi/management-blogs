---
layout: post
title: Convolution
tags: [CNN, Mathematics]
color: rgb(255, 111, 0)
feature-img: "assets/img/post-cover/20-cover.png"
thumbnail: "assets/img/post-cover/20-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

In mathematics (in particular, [functional analysis](https://en.wikipedia.org/wiki/Functional_analysis)), convolution
is a mathematical operation on two functions ($$\mathit{f}$$ and $$\mathit{g}$$) that produces a third function (
$$\mathit{f \ast g}$$) that expresses how the shape of one is modified by the other. The term convolution refers to both
the result function and to the process of computing it. It is defined as the _integral of the product of the two 
functions after one is reflected about the y-axis and shifted_. The choice of which function is reflected and shifted 
before the integral does not change the integral result (see commutativity). The integral is evaluated for all values
of shift, producing the convolution function.

Definition
----------

The convolution of $$\mathit{f}$$ and $$\mathit{g}$$ is written $$\mathit{f \ast g}$$, denoting the operator with the symbol $$\ast$$ It is defined as the _integral of the product of the two functions after one is reflected about the y-axis and shifted_. As such, it is a particular kind of
[integral transform](https://en.wikipedia.org/wiki/Integral_transform):

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{\infty}{f(\tau)g(t - \tau)d\tau} }$$


An equivalent definition is (see [commutativity](https://en.wikipedia.org/wiki/Convolution#Properties)):

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{\infty}{f(t - \tau)g(\tau)d\tau} }$$

At each t, the convolution formula can be described as the area under the function f(τ) weighted by the function g(−τ) shifted by the amount t. As t changes, the weighting function g(t − τ) emphasizes different parts of the input function f(τ); 