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

<!--more-->

* TOC
{:toc}

ANN learning is well-suited to problems in which the training data corresponds to _noisy, complex sensor data, such as 
inputs from cameras and microphones_. It is also applicable to problems for which more symbolic representations are
often used, such as the decision tree learning tasks. In these cases ANN and decision tree learning often produce
results of comparable accuracy

![Error loading ann-deep-nn.png]({{ "/assets/img/ann-deep-nn.png" | relative_url}})

Perceptrons
-----------

One type of ANN system is based on a unit called a **perceptron**, illustrated in figure below:

![Error loading ann-a-perceptron.png]({{ "/assets/img/ann-a-perceptron.png" | relative_url}})

A perceptron takes a vector of real-valued inputs, calculates a linear combination of these inputs, then outputs a 1 if
the result is greater than some threshold and -1 otherwise. More precisely, given inputs $$\mathit{x}_1$$ through $$\mathit{x}_n$$, the
output $$o(\mathit{x}_1, ... , \mathit{x}_n)$$ computed by the perceptron is

$$
o(\mathit{x}_1, ..., \mathit{x}_n) = 
    \begin{cases}
        1 \ if \ \mathit{w}_0 + \mathit{w}_1\mathit{x}_1 + \mathit{w}_2\mathit{x}_2 + ... + \mathit{w}_n\mathit{x}_n > 0 \\
        -1 \ otherwise
    \end{cases}

$$

where each $$\mathit{w}_\mathit{i}$$ is a real-valued constant, or weight, that determines the contribution of input 
$$\mathit{x}_\mathit{i}$$ to the perceptron output. Notice the quantity ($$-\mathit{w}_O$$) is a threshold that the 
weighted combination of inputs $$\mathit{w}_1\mathit{x}_1 + ... + \mathit{w}_n\mathit{x}_n$$ must surpass in order for
the perceptron to output a 1.

If we add an additional constant input $$\mathit{x}_0 = 1$$, allowing us to write the equation above as
$$\sum_{i = 0}^{n} \mathit{w}_\mathit{i}\mathit{x}_\mathit{i} > 0$$ or, in vector form, as $$\vec{\mathit{w}} \cdot \vec{\mathit{x}} > 0$$, we will be able to write the perceptron function as

$$ o(\vec{\mathit{x}}) = sgn(\vec{\mathit{w}} \cdot \vec{\mathit{x}}) $$

where

$$
sgn(y) =
    \begin{cases}
        1 \ if \ y > 0 \\
        -1 \ otherwise
    \end{cases}

$$

Learning a perceptron involves choosing values for the weights $$\mathit{w}_0, ..., \mathit{w}_n$$. The space
$$\mathit{H}$$ of candidate hypotheses considered in perceptron learning, therefore, is the set of all possible 
real-values weight vectors

$$ \mathit{H} = \{\vec{\mathit{w}} | \vec{\mathit{w}} \in : \mathfrak{R}^{n + 1}\} $$

### Representational Power of Perceptrons

We can view the perceptron as representing a **hyperplane decision surface** in the n-dimensional space of instances
(i.e., points). The perceptron outputs a 1 for instances lying on one side of the hyperplane and outputs a -1 for
instances lying on the other side, as illustrated in figure below

![Error loading ann-n-dimensional-hyperplan-space.png]({{ "/assets/img/ann-n-dimensional-hyperplan-space.png" | relative_url}})

The equation for this decision hyperplane is $$\vec{\mathit{w}} \cdot \vec{\mathit{x}} = 0$$. Of course, some sets of positive and negative 
examples cannot be separated by any hyperplane. Those that can be separated are called **linearly separable** sets of 
examples.

#### Representing AND & OR Function

A single perceptron can be used to represent many boolean functions. For example, if we assume boolean values of 1
(true) and -1 (false), then one example way to use a two-dimensional input perceptron to implement the AND function is
to set the weights $$\mathit{w}_0 = -0.8$$, and $$\mathit{w}_1 = \mathit{w}_2 = 0.5$$:

| **$$\mathit{x}_1$$** | **$$\mathit{x}_2$$** | **$$y$$** | **$$syn(y)$$** |
|:--------------------:|:--------------------:|:---------:|:--------------:|
|          1           |          1           |    0.2    |       1        |
|          0           |          0           |   -0.8    |       -1       |
|          1           |          0           |   -0.3    |       -1       |
|          0           |          1           |   -0.3    |       -1       |

This perceptron can be made to represent the OR function instead by altering the threshold to $$\mathit{w}_0 = -0.3$$:

| **$$\mathit{x}_1$$** | **$$\mathit{x}_2$$** | **$$y$$** | **$$syn(y)$$** |
|:--------------------:|:--------------------:|:---------:|:--------------:|
|          1           |          1           |    0.7    |       1        |
|          0           |          0           |   -0.3    |       -1       |
|          1           |          0           |    0.2    |       1        |
|          0           |          1           |    0.2    |       1        |

> In fact, AND and OR can be viewed as special cases of m-of-n functions: that is, functions where at least m of the n 
> inputs to the perceptron must be true. The OR function corresponds to m = 1 and the AND function to m = n. Any m-of-n
> function is easily represented using a perceptron by setting all input weights to the same value (e.g., 0.5) and
> then setting the threshold $$\mathit{w}_0$$ accordingly

Perceptrons can represent all of the primitive boolean functions AND, OR, NAND ($$\neg AND$$), and NOR ($$\neg OR$$). 
Some boolean functions, however, cannot be represented by a single perceptron, such as the XOR function
whose value is 1 if and only if $$\mathit{x}_1 \ne \mathit{x}_2$$. Note the set of linearly nonseparable training
examples shown in figure(b) above corresponds to this XOR function

The ability of perceptrons to represent AND, OR, NAND, and NOR is important because _every_ boolean function can be 
represented by some network of interconnected units based on these primitives. In fact, every boolean function can
be represented by some network of perceptrons only two levels deep, in which the inputs are fed to multiple units, and
the outputs of these units are then input to a second, final stage. One way is to represent the boolean function in 
disjunctive normal form (i.e., as the disjunction (OR) of a set of conjunctions (ANDs) of the inputs and their
negations). Note that the input to an AND perceptron can be negated simply by changing the sign of the corresponding
input weight.

Because networks of threshold units can represent a rich variety of functions and because single units alone cannot, we 
will generally be interested in learning multilayer networks of threshold units.

### The Perceptron Training Rule

We begin by understanding how to learn the weights for a single perceptron using two fundamental algorithms that provide
basis for learning networks of many units:

1. the [**Perceptron Rule**](#perceptron-rule)
2. the [**Delta Rule**](#gradient-descent-and-the-delta-rule)

One way to learn an acceptable weight vector is to begin with random weights, then iteratively apply the perceptron to 
each training example, modifying the perceptron weights whenever it misclassifies an example. This process is repeated, 
iterating through the training examples as many times as needed until the perceptron classifies all training examples 
correctly. Weights are modified at each step according to the perceptron training rule, which revises the weight $$\mathit{w}_\mathit{i}$$
associated with input $$\mathit{x}_\mathit{i}$$ according to the rule

$$ \mathit{w}_\mathit{i} \leftarrow \mathit{w}_\mathit{i} + \Delta \mathit{w}_\mathit{i} $$

where

$$ \Delta \mathit{w}_\mathit{i} = \mathit{\eta} (t - o) \mathit{x}_\mathit{i} $$

Here $$\mathit{t}$$ is the target output for the current training example, $$o$$ is the output generated by the 
perceptron, and $$\mathit{\eta}$$ is a positive constant called the **learning rate**. The role of the learning rate is
to moderate the degree to which weights are changed at each step. It is usually set to some small value (e.g., 0.1) and
is sometimes made to decay as the number of weight-tuning iterations increases

> The above learning procedure can be proven to converge within a finite number of applications of the perceptron
> training rule to a weight vector that correctly classifies all training examples, provided _the training examples are 
> linearly separable_ and provided a sufficiently small $$\mathit{\eta}$$ is used. If the data are not linearly separable,
> convergence is not assured

### Gradient Descent and the Delta Rule

A second training rule, called the **delta rule**, is designed to overcome the difficulty of the
[Perceptron Rule](#perceptron-rule) when the training examples are not linearly separable. The key idea behind the delta 
rule is to use **gradient descent** to search the hypothesis space of possible weight vectors to find the weights that
best fit the training examples

The delta training rule is best understood by considering the task of training an **unthresholded perceptron**; that is,
a _linear_ unit for which the output $$o$$ is given by

$$ o(\vec{\mathit{x}}) = \vec{\mathit{w}} \cdot \vec{\mathit{x}} $$

In order to derive a weight learning rule for linear units, let us begin by specifying a measure for the **training
error** of a hypothesis (weight vector), relative to the training examples

$$ \mathit{E}(\vec{\mathit{w}}) \equiv \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} (t_d - o_d)^2 $$

where $$D$$ is the set of training examples; $$t_d$$ is the target output for training example $$d$$, and $$o_d$$ is the 
output of the linear unit for training example $$d$$. By this definition, $$\mathit{E}(\vec{\mathit{w}})$$ is simply
half the squared difference between the target output $$t_d$$ and the linear unit output $$o_d$$, summed over **all**
training examples

#### Visualizing the Hypothesis Space

To understand the gradient descent algorithm, it is helpful to visualize the entire hypothesis space of possible weight 
vectors and their associated $$\mathit{E}$$ values, as illustrated in this figure:

![Error loading ann-gradient-descent-hypothesis-space.png]({{ "/assets/img/ann-gradient-descent-hypothesis-space.png" | relative_url}})

The axes $$\mathit{w}_0$$ and $$\mathit{w}_1$$ represent possible values for the two weights of a simple linear unit.
The $$\mathit{w}_0, \mathit{w}_1$$ plane therefore represents the entire hypothesis space. The vertical axis indicates
the error $$\mathit{E}$$ relative to some fixed set of training examples. The error surface shown in the figure thus 
summarizes the desirability of every weight vector in the hypothesis space (we desire a hypothesis with minimum error). 
Given the way in which we chose to define $$\mathit{E}$$ (half the summation of the square difference between target and 
linear unit output), for linear units this error surface must always be parabolic with a single global minimum. The 
specific parabola will depend, of course, on the particular set of training examples.

Gradient descent search determines a weight vector that minimizes $$\mathit{E}$$ by starting with an arbitrary initial 
weight vector, then repeatedly modifying it in small steps. At each step, the weight vector is altered in the direction 
that produces [_the steepest descent_](#derivation-of-the-gradient-descent-rule) along the error surface depicted in
figure above. This process continues until the global minimum error is reached.

#### Derivation of the Gradient Descent Rule

The direction of the steepest descent along the error surface can be found by computing the derivative of $$\mathit{E}$$ 
with respect to each component of the vector $$\vec{\mathit{w}}$$. This vector derivative is called the **gradient of 
$$\mathit{E}$$ with respect to $$\vec{\mathit{w}}$$**, written as $$\nabla \mathit{E}(\vec{\mathit{w}})$$

$$

\nabla \mathit{E}(\vec{\mathit{w}}) \equiv \left[ \frac{\partial \mathit{E}}{\partial \mathit{w_0}}, \frac{\partial \mathit{E}}{\partial \mathit{w_1}}, ..., \frac{\partial \mathit{E}}{\partial \mathit{w}_\mathit{n}} \right]

$$

> Notice $$\nabla \mathit{E}(\vec{\mathit{w}})$$ is itself a vector, whose components are the partial derivatives of $$\mathit{E}$$ with respect to
> each of the $$\mathit{w}_\mathit{i}$$. **When interpreted as a vector in weight space, the gradient specifies the direction that
> produces the steepest increase in $$\mathit{E}$$. The negative of this vector therefore gives the direction of the steepest
> decrease**.

Since the gradient specifies the direction of steepest increase of $$\mathit{E}$$, the training rule for gradient
descent is

$$ \vec{\mathit{w}} \leftarrow \vec{\mathit{w}} + \Delta \vec{\mathit{w}} $$

where

$$ \Delta \vec{\mathit{w}} = -\mathit{\eta}\nabla \mathit{E}(\vec{\mathit{w}}) $$

In its component form:

$$ \mathit{w}_\mathit{i} \leftarrow \mathit{w}_\mathit{i} + \Delta \mathit{w}_\mathit{i} $$

where

$$ \Delta \mathit{w}_\mathit{i} = -\mathit{\eta}\frac{\partial \mathit{E}}{\partial \mathit{w}_\mathit{i}} $$

It is clear then that the steepest descent is achieved by altering each component
$$\mathit{w_i}$$, of $$\vec{\mathit{w}}$$ in proportion to $$\frac{\partial \mathit{E}}{\partial \mathit{w_i}}$$, which
can be caldulated as follows

$$
\frac{\partial \mathit{E}}{\partial \mathit{w_i}} = \frac{\partial}{\partial \mathit{w_i}} \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d})^2 = \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} \frac{\partial}{\partial \mathit{w_i}} (\mathit{t_d} - \mathit{o_d})^2 = \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} 2(\mathit{t_d} - \mathit{o_d}) \frac{\partial}{\partial \mathit{w_i}} (\mathit{t_d} - \mathit{o_d}) = \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d}) \frac{\partial}{\partial \mathit{w_i}} (\mathit{t_d} - \vec{\mathit{w}} \cdot \vec{\mathit{x_d}}) = \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d}) (-\mathit{x_{id}})
$$

$$ \Delta \mathit{w_i} = \mathit{\eta} \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d}) \mathit{x_{id}} $$

which gives us the following algorithm

> GRADIENT-DESCENT(training_examples, $$\mathit{\eta}$$)
>
> * _Each training example is a pair of the form $$\left< \vec{\mathit{x}}, \mathit{t} \right>$$, where
>   $$\vec{\mathit{x}}$$ is the vector of input values, and $$\mathit{t}$$ is the target output value. $$\mathit{\eta}$$ 
>   is the learning rate (e.g. 0.5)._
> 
> 1. Initialize each $$\mathit{w_i}$$ to some small random value
> 2. Until the termination condition is met, do
>    * Initialize each $$\Delta\mathit{w_i}$$ to zero
>    * For each $$\left< \vec{\mathit{x}}, \mathit{t} \right>$$ in training_examples, do
>      - Input the instance $$\vec{\mathit{x}}$$ to the unit and compute the output $$\mathit{o}$$
>      - For each linear unit weight $$\mathit{w_i}$$, calculate $$ \Delta\mathit{w_i} \leftarrow \Delta\mathit{w_i} + \mathit{\eta}(\mathit{t - o})\mathit{x_i} $$
>      - For each linear unit weight $$\mathit{w_i}$$, compute $$ \mathit{w_i} \leftarrow \mathit{w_i} + \Delta\mathit{w_i} $$

To summarize, the gradient descent algorithm for training linear units is starting by picking an initial random weight 
vector, then applying the linear unit to all training examples, then compute $$\Delta\mathit{w_i}$$ for each weight,
updating each weight $$\mathit{w_i}$$wi by adding $$\Delta\mathit{w_i}$$, then repeat this process

> Note that the learning rate $$\mathit{\eta}$$ has to be sufficiently small If it is too large, the gradient descent search runs 
> the risk of overstepping the minimum in the error surface rather than settling into it. For this reason, one common 
> modification to the algorithm is to gradually reduce the value of $$\mathit{\eta}$$ as the number of gradient descent steps
> grows.

#### Stochastic Approximation to Gradient Descent

The key practical difficulties in applying gradient
descent are

1. converging to a local minimum can sometimes be quite slow (i.e., it can require many thousands of gradient descent 
   steps), and
2. if there are multiple local minima in the error surface, then there is no guarantee that the procedure will find the 
   global minimum.

One common variation on gradient descent intended to alleviate these difficulties is called **Incremental Gradient 
Descent**, or alternatively **Stochastic Gradient Descent**.

The idea of Stochastic Gradient Descent is instead of computing weight updates after summing over _all_ the training 
examples in D, it approximates this gradient descent search by updating weights incrementally, following the calculation 
of the error for _each individual_ example. The weight update rule then changes:

$$ \Delta \mathit{w_i} = \mathit{\eta} \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d}) \mathit{x_{id}} \Rightarrow \Delta \mathit{w_i} = \mathit{\eta_d} (\mathit{t_d} - \mathit{o_d}) \mathit{x_{i}}$$

The modified algorithm becomes

> GRADIENT-DESCENT(training_examples, $$\mathit{\eta}$$)
>
> * _Each training example is a pair of the form $$\left< \vec{\mathit{x}}, \mathit{t} \right>$$, where
>   $$\vec{\mathit{x}}$$ is the vector of input values, and $$\mathit{t}$$ is the target output value. $$\mathit{\eta}$$ 
>   is the learning rate (e.g. 0.5)._
>
> 1. Initialize each $$\mathit{w_i}$$ to some small random value
> 2. Until the termination condition is met, do
>    * Initialize each $$\Delta\mathit{w_i}$$ to zero
>    * For each $$\left< \vec{\mathit{x}}, \mathit{t} \right>$$ in training_examples, do
>      - Input the instance $$\vec{\mathit{x}}$$ to the unit and compute the output $$\mathit{o}$$
>      - For each linear unit weight $$\mathit{w_i}$$, calculate $$ \mathit{w_i} \leftarrow \mathit{w_i} + \mathit{\eta_d} (\mathit{t_d} - \mathit{o_d}) \mathit{x_{i}} $$

One way to view this stochastic gradient descent is to consider a distinct error function $$\mathit{E_d(\vec{w})}$$ or
each individual training example $$\mathit{d}$$ as follows

$$ \mathit{E_d(\vec{w})} = \frac{1}{2}(\mathit{t_d} - \mathit{o_d})^2 $$

Stochastic gradient descent iterates over the training examples $$\mathit{d}$$ in $$\mathit{D}$$, _at each iteration_ 
altering the weights according to the gradient with respect to $$\mathit{E_d(\vec{w})}$$. The sequence of these weight 
updates, when iterated over all training examples, provides a reasonable approximation to descending the gradient with
respect to our original error function  $$\mathit{E(\vec{w})}$$. By making the value of $$\mathit{\eta}$$ sufficiently 
small, stochastic gradient descent can be made to approximate true gradient descent arbitrarily closely

> The key differences between standard gradient descent and stochastic gradient descent are:
>
> * In standard gradient descent, the error is summed over all examples before updating weights, whereas in stochastic 
>   gradient descent weights are updated upon examining each training example.
> * Summing over multiple examples in standard gradient descent requires more computation per weight update step. On the 
>   other hand, because it uses the true gradient, standard gradient descent is often used with a larger step size per 
>   weight update than stochastic gradient descent.
> * In cases where there are multiple local minima with respect to $$\mathit{E(\vec{w})}$$, stochastic gradient descent 
>   can sometimes avoid falling into these local minima because it uses the various $$\nabla\mathit{E_d(\vec{w})}$$
>   rather than $$\nabla\mathit{E(\vec{w})}$$ to guide its search.

Both stochastic and standard gradient descent methods are commonly used in practice.

### Remarks

The key difference between [Perceptron Training Rule](#the-perceptron-training-rule) and
[Gradient Descent and the Delta Rule](#gradient-descent-and-the-delta-rule) is that the former updates weights based on 
the error in the _thresholded_ perceptron output, whereas the latter updates weights based on the error in the
_unthresholded_ linear combination of inputs.


Multilayer Networks & The Backpropagation Algorithm
---------------------------------------------------

Single perceptrons, as we've discuessed so far, can only express linear decision surfaces. In contrast, the kind of 
multilayer networks learned by the Backpropagation algorithm are capable of expressing a rich variety of **nonlinear
decision surfaces**.

What type of unit shall we use as the basis for constructing multilayer networks? One solution is the **sigmoid unit** -
a unit very much like a perceptron, but based on a smoothed, differentiable threshold function

The sigmoid unit is illustrated in figure below

![Error loading ann-sigmoid-threshold-unit.png]({{ "/assets/img/ann-sigmoid-threshold-unit.png" | relative_url}})

Like the perceptron, the sigmoid unit first computes a linear combination of its inputs, then applies a threshold to
the result. In the case of the sigmoid unit, however, the threshold output is a continuous function of its input. More 
precisely, the sigmoid unit computes its output $$\mathit{o}$$ as

$$ \mathit{o} = \sigma(\mathit{\vec{w}} \cdot \mathit{\vec{x}}) $$

where

$$ \sigma(\mathit{y}) = \frac{1}{1 + \exp^{-\mathit{y}}} $$

$$\sigma$$ is often called the **sigmoid function** or, alternatively, the **logistic function**

> 📋 Note that the output of sigmoid function ranges between 0 and 1. Because it maps a very large input domain
> to a small range of outputs, it is often referred to as the **squashing function** of the unit

The sigmoid function doesn't always have to be in the form stated above as long as it keeps the property that its
derivative is easily expressed. For example, in this particular form, we can easily see

$$ \frac{\mathit{d\sigma(y)}}{\mathit{dy}} = \sigma(\mathit{y})(1 - \sigma(\mathit{y})) $$

Other differentiable functions, such as $$\frac{1}{1 + \exp^{-\mathit{ky}}}$$ where $$\mathit{k}$$ is some positive
constant, and $$tanh$$ are sometimes used instead

### The Backpropagation Algorithm

The Backpropagation algorithm learns the weights for a multilayer network, given a network with a fixed set of units and 
interconnections

Because we are considering networks with multiple output units rather than single units as before, we begin by
redefining $$\mathit{E}$$ to sum the errors over all of the network output unit

$$

\mathit{E(\vec{w})} \equiv \frac{1}{2}\sum_{\mathit{d \in D}}\sum_{\mathit{k \in \text{outputs}}}(\mathit{t_{kd} - o_{kd}})^2

$$

where "outputs" is the set of output units in the network, and $$\mathit{t_{kd}}$$ and $$\mathit{o_{kd}}$$ are the
target and output values associated with the _k_-th output unit and training example $$\mathit{d}$$.

The learning problem faced by Backpropagation Algorithm is to search a large hypothesis space defined by all possible 
weight values for all the units in the network

> Backpropagation(training_examples, $$\eta$$, $$\mathit{n_{in}}$$, $$\mathit{n_{out}}$$, $$\mathit{n_{hidden}}$$)
> 
> * Each training example is a pair of the form $$\left< \vec{\mathit{x}}, \mathit{\vec{t}} \right>$$, where
>   $$\vec{\mathit{x}}$$ is the vector of the network input values, and $$\vec{\mathit{t}}$$ the vector of target
>   network output values
> * $$\eta$$ is the learning rate (e.g. 0.5)
> * $$\mathit{n_{in}}$$ is the number of network inputs
> * $$\mathit{n_{out}}$$ is the number of output units
> * $$\mathit{n_{hidden}}$$ is the number of units in the hidden layer
> * The input from unit $$\mathit{i}$$ into unit $$\mathit{j}$$ is denoted as $$\mathit{x_{ji}}$$ and the weight from
>   unit $$\mathit{i}$$ to unit $$\mathit{j}$$ is denoted as $$\mathit{w_{ji}}$$
> 
> 1. Create a feed-forward network with $$\mathit{n_{in}}$$ number of inputs, $$\mathit{n_{hidden}}$$ number of hidden
>    units, and $$\mathit{n_{out}}$$ number of output units.
> 2. Initialize all network weights to small random numbers (e.g. between -0.5 to 0.5)
> 3. Until the termination condition is met, do
>    * For each $$\left< \vec{\mathit{x}}, \mathit{\vec{t}} \right>$$ in training_examples, do
>      - _Propagate the input forward through the network_:
>        1. Input the instance $$\vec{\mathit{x}}$$ to the network and compute the output $$\mathit{o_u}$$ of ever unit
>           $$\mathit{u}$$ in the network
>      - _Propagate the errors backward through the network_:
>        2. For each network output unit 