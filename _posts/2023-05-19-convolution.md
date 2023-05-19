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

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{+\infty}{f(\tau)g(t - \tau)d\tau} }$$


An equivalent definition is (see [commutativity](https://en.wikipedia.org/wiki/Convolution#Properties)):

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{+\infty}{f(t - \tau)g(\tau)d\tau} }$$

At each $$\mathit{t}$$, the convolution formula can be described as the area under the function $$\mathit{f(\tau)}$$ 
weighted by the function $$\mathit{g(-\tau)}$$ shifted by the amount $$\mathit{t}$$. As $$\mathit{t}$$ changes, the 
weighting function $$\mathit{g(t - \tau)}$$ emphasizes different parts of the input function $$\mathit{f(\tau)}$$; If
$$\mathit{\tau}$$is a positive value, then $$\mathit{g(t - \tau)}$$ is equal to $$\mathit{g(-\tau)}$$ that slides or
is shifted along the $$\mathit{\tau}$$-axis toward the right (toward +∞) by the amount of $$\mathit{t}$$, while if
$$\mathit{t}$$is a negative value, then $$\mathit{g(t - \tau)}$$ is equal to $$\mathit{g(-\tau)}$$ that slides or is 
shifted toward the left (toward -∞) by the amount of $$\mathit{|t|}$$.

Discrete Convolution
------------------
