---
layout: post
title: Machine Learning - Artificial Neural Networks
tags: [Machine Learning, Artificial Neural Networks, ANN]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/15-cover.png"
thumbnail: "assets/img/post-cover/15-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Information-processing abilities of biological neural systems have been thought to follow from highly parallel
processes operating on representations that are distributed over many neurons in human brain. One motivation for 
Artificial Neural Networks (ANNs) systems is to capture this kind of highly parallel computation based on distributed 
representations. ANN provide a general, practical method for learning real-valued, discrete-valued, and vector-valued 
functions from examples.

![Error loading ann-deep-nn.png]({{ "/assets/img/ann-deep-nn.png" | relative_url}})

<!--more-->

* TOC
{:toc}

ANN learning is well-suited to problems in which the training data corresponds to _noisy, complex sensor data, such as 
inputs from cameras and microphones_. It is also applicable to problems for which more symbolic representations are
often used, such as the decision tree learning tasks. In these cases ANN and decision tree learning often produce
results of comparable accuracy

Perceptrons
-----------

One type of ANN system is based on a unit called a perceptron, illustrated in figure below:

![Error loading ann-a-perceptron.png]({{ "/assets/img/ann-a-perceptron.png" | relative_url}})

A perceptron takes a vector of real-valued inputs, calculates a linear combination of these inputs, then outputs a 1 if
the result is greater than some threshold and -1 otherwise. More precisely, given inputs $$x_1$$ through $$x_n$$, the
output $$o(x_1, ... , x_n)$$ computed by the perceptron is

$$
o(x_1, ..., x_n) = 
\begin{cases}
    1 if w_0 + w_1x_1 + w_2x_2 + ... + w_nx_n > 0 \\
    -1 otherwise
\end{cases}

$$

where each $$w_i$$ is a real-valued constant, or weight, that determines the contribution of input $$x_i$$ to the 
perceptron output. Notice the quantity ($$-w_O$$) is a threshold that the weighted combination of inputs
$$w_1x_1 + ... + w_nx_n$$ must surpass in order for the perceptron to output a 1.

If we add an additional constant input $$x_0 = 1$$, allowing us to write the equation above as
$$\sum_{i = 0}^{n} w_ix_i > 0$$ or, in vector form, as $$\vec{w} \cdot \vec{x} > 0$$, we will be able to write the
perceptron function as

$$ o(\vec{x}) = sgn(\vec{w} \cdot \vec{x}) $$

where

$$
sgn(y) =
\begin{cases}
    1 if y > 0 \\
    -1 otherwise
\end{cases}

$$

Learning a perceptron involves choosing values fro the weights $$w_0, ..., w_n$$. The space $$\mathit{H}$$ of candidate
hypotheses considered in perceptron learning, therefore, is the set of all possible real-values weight vectors

$$ H = \{\vec{\mathbf{w}} | \vec{w} \in : \mathfrak{R}^{n + 1}\} $$

### Representational Power of Perceptrons

We can view the perceptron as representing a **hyperplane decision surface** in the n-dimensional space of instances
(i.e., points). The perceptron outputs a 1 for instances lying on one side of the hyperplane and outputs a -1 for
instances lying on the other side, as illustrated in figure below

![Error loading ann-n-dimensional-hyperplan-space.png]({{ "/assets/img/ann-n-dimensional-hyperplan-space.png" | relative_url}})

The equation for this decision hyperplane is $$\vec{w} \cdot \vec{x} = 0$$. Of course, some sets of positive and negative 
examples cannot be separated by any hyperplane. Those that can be separated are called **linearly separable** sets of 
examples.

#### Representing AND & OR Function

A single perceptron can be used to represent many boolean functions. For example, if we assume boolean values of 1
(true) and -1 (false), then one example way to use a two-dimensional input perceptron to implement the AND function is
to set the weights $$w_0 = -0.8$$, and $$w_1 = w_2 = 0.5$$:

<div align="center">

| **$$x_1$$** | **$$x_2$$** | **$$y$$** | **$$syn(y)$$** |
|:-----------:|:-----------:|:---------:|:--------------:|
|      1      |      1      |    0.2    |       1        |
|      0      |      0      |   -0.8    |       -1       |
|      1      |      0      |   -0.3    |       -1       |
|      0      |      1      |   -0.3    |       -1       |

</div>

This perceptron can be made to represent the OR function instead by altering the threshold to $$w_0 = -0.3$$:

<div align="center">

| **$$x_1$$** | **$$x_2$$** | **$$y$$** | **$$syn(y)$$** |
|:-----------:|:-----------:|:---------:|:--------------:|
|      1      |      1      |    0.7    |       1        |
|      0      |      0      |   -0.3    |       -1       |
|      1      |      0      |    0.2    |       1        |
|      0      |      1      |    0.2    |       1        |

</div>

> In fact, AND and OR can be viewed as special cases of m-of-n functions: that is, functions where at least m of the n 
> inputs to the perceptron must be true. The OR function corresponds to m = 1 and the AND function to m = n. Any m-of-n
> function is easily represented using a perceptron by setting all input weights to the same value (e.g., 0.5) and
> then setting the threshold $$w_0$$ accordingly

Perceptrons can represent all of the primitive boolean functions AND, OR, NAND ($$\neg AND$$), and NOR ($$\neg OR$$). 
Some boolean functions, however, cannot be represented by a single perceptron, such as the XOR function
whose value is 1 if and only if $$x_1 \ne x_2$$. Note the set of linearly nonseparable training examples shown in
figure(b) above corresponds to this XOR function

The ability of perceptrons to represent AND, OR, NAND, and NOR is important because _every_ boolean function can be 
represented by some network of interconnected units based on these primitives. In fact, every boolean function can
be represented by some network of perceptrons only two levels deep, in which the inputs are fed to multiple units, and
the outputs of these units are then input to a second, final stage. One way is to represent the boolean function in 
disjunctive normal form (i.e., as the disjunction (OR) of a set of conjunctions (ANDs) of the inputs and their
negations). Note that the input to an AND perceptron can be negated simply by changing the sign of the corresponding
input weight.

Because networks of threshold units can represent a rich variety of functions and because single units alone cannot, we 
will generally be interested in learning multilayer networks of threshold units.