---
layout: post
title: Machine Learning Basics
tags: [Machine Learning, Philosophy]
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Artificial Neural Networks
--------------------------










### Weight Initialization

Lets start with what we should not do. Note that we do not know what the final value of every weight should be in the
trained network, but with proper data normalization it is reasonable to assume that approximately half of the weights
will be positive and half of them will be negative. A reasonable-sounding idea then might be to set all the initial
weights to zero, which we expect to be the “best guess” in expectation. This turns out to be a mistake, because if every
neuron in the network computes the same output, then they will also all compute the same gradients during
backpropagation and undergo the exact same parameter updates. In other words, there is no source of asymmetry between
neurons if their weights are initialized to be the same.

Therefore, we still want the weights to be very close to zero, but as we have argued above, not identically zero. As a
solution, it is common to initialize the weights of the neurons to small numbers and refer to doing so as **symmetry
breaking**. The idea is that the neurons are all random and unique in the beginning, so they will compute distinct
updates and integrate themselves as diverse parts of the full network. The implementation for one weight matrix might
look like `W = 0.01* np.random.randn(D,H)`, where `randn` samples from a zero mean, unit standard deviation gaussian.
With this formulation, **every neuron's weight vector is initialized as a random vector sampled from a multi-dimensional
gaussian**, so the neurons point in random direction in the input space. It is also possible to use small numbers drawn
from a uniform distribution, but this seems to have relatively little impact on the final performance in practice.

> ⚠️ Itis not necessarily the case that smaller numbers will work strictly better. For example, a Neural Network layer
> that has very small weights will during backpropagation compute very small gradients on its data (since this gradient
> is proportional to the value of the weights). This could greatly diminish the “gradient signal” flowing backward
> through a network, and could become a concern for deep networks.

One problem with the multi-dimensional gaussian is that the distribution of the outputs from a randomly initialized
neuron has a variance that grows with the number of inputs. It turns out that we can normalize the variance of each
neuron's output to 1 by scaling its weight vector by the square root of its fan-in (i.e. its number of inputs). That is,
the recommended heuristic is to initialize each neuron’s weight vector as: `w = np.random.randn(n) / sqrt(n)`, where `n`
is the number of its inputs. This ensures that all neurons in the network initially have approximately the same output
distribution and empirically improves the rate of convergence.

The sketch of the derivation is as follows: Consider the inner product $$\mathit{s = \sum_i^n w_i x_i}$$ between the
weights $$\mathit{w}$$ and input $$\mathit{x}$$, which gives the raw activation of a neuron before the non-linearity. We
can examine the variance of $$\mathit{s}$$:

![Error loading ann-scaling-variance.png]({{ "/assets/img/ann-scaling-variance.png" | relative_url}})

where in the first 2 steps we have used [properties of variance](http://en.wikipedia.org/wiki/Variance). In third step
we assumed _zero mean inputs and weights_, so $$\mathit{E[x_i] = E[w_i] = 0}$$. Note that this is not generally the
case: For example ReLU units will have a positive mean. In the last step we assumed that all $$\mathit{w_i, x_i}$$ are
identically distributed. From this derivation we can see that if we want $$\mathit{s}$$ to have the same variance as all
of its inputs $$\mathit{x}$$, then during initialization we should make sure that the variance of every weight
$$\mathit{w}$$ is $$\mathit{\frac{1}{n}}$$. And since $$\mathit{\text{Var}(aX) = a^2\text{Var}(X)}$$ for a random
variable $$\mathit{X}$$ and a scalar $$\mathit{a}$$, this implies that we should draw from unit gaussian and then scale
it by $$\mathit{a = \sqrt{\frac{1}{n}}}$$, to make its variance $$\mathit{\frac{1}{n}}$$. This gives the initialization
`w = np.random.randn(n) / sqrt(n)`.

A more recent paper on this topic,
[Delving Deep into Rectifiers: Surpassing Human-Level Performance on ImageNet Classification](http://arxiv-web3.library.cornell.edu/abs/1502.01852)
by He et al., derives an initialization specifically for ReLU neurons, reaching the conclusion that the variance of
neurons in the network should be $$\mathit{\frac{2.0}{n}}$$. This gives the initialization
`w = np.random.randn(n) * sqrt(2.0/n)`, and is the current recommendation for use in practice in the specific case of
neural networks with ReLU neurons.

#### Sparse Initialization.

Another way to address the uncalibrated variances problem is to set all weight matrices to zero, but to break symmetry
every neuron is randomly connected (with weights sampled from a small gaussian as above) to a fixed number of neurons
below it. A typical number of neurons to connect to may be as small as 10.

#### Initializing the Biases.

It is possible and common to initialize the biases to be zero, since the asymmetry breaking is provided by the small
random numbers in the weights. For ReLU non-linearities, some people like to use small constant value such as 0.01 for
all biases because this ensures that all ReLU units fire in the beginning and therefore obtain and propagate some
gradient. However, it is not clear if this provides a consistent improvement (in fact some results seem to indicate that
this performs worse) and it is more common to simply use 0 bias initialization.

In practice, the recommendation is to use ReLU units and use the `w = np.random.randn(n) * sqrt(2.0/n)`, as discussed in
He et al..

#### Batch Normalization

A recently developed technique by Ioffe and Szegedy called [Batch Normalization](http://arxiv.org/abs/1502.03167)
alleviates a lot of headaches with properly initializing neural networks by explicitly forcing the activations
throughout a network to take on a unit gaussian distribution at the beginning of the training. The core observation is
that this is possible because normalization is a simple differentiable operation. In the implementation, applying this
technique usually amounts to insert the BatchNorm layer immediately after fully connected layers (or convolutional
layers), and before non-linearities. **It has become a very common practice to use Batch Normalization in neural
networks**. In practice networks that use Batch Normalization are significantly more robust to bad initialization.
Additionally, batch normalization can be interpreted as doing preprocessing at every layer of the network, but integrated
into the network itself in a differentiable manner. Neat!

### Regularization

There are several ways of controlling the capacity of Neural Networks to prevent overfitting:

#### L2 Regularization

L2 regularization is perhaps the most common form of regularization. It can be implemented by penalizing the squared
magnitude of all parameters directly in the objective. That is, for every weight $$\mathit{w}$$ in the network, we add
the term $$\mathit{\frac{1}{2} \lambda w^2}$$ to the objective, where $$\lambda$$ is the regularization strength. It is
common to see the factor of $$\frac{1}{2}$$ in front because then the gradient of this term with respect to the
parameter $$\mathit{w}$$ is simply $$\mathit{\lambda w}$$ instead of $$\mathit{2\ \lambda w}$$. The L2 regularization
has the intuitive interpretation of heavily penalizing peaky weight vectors and preferring diffuse weight vectors. Due
to multiplicative interactions between weights and inputs this has the appealing property of encouraging the network to
use all of its inputs a little rather than some of its inputs a lot. Lastly, notice that during gradient descent
parameter update, using the L2 regularization ultimately means that every weight is decayed linearly: `W += -lambda * W`
towards zero.

#### L1 Regularization

L1 regularization is another relatively common form of regularization, where for each weight $$\mathit{w}$$ we add the
term $$\mathit{ \lambda |w| }$$ to the objective. It is possible to combine the L1 regularization with the L2
regularization: $$\mathit{ \lambda_1 |w| + \lambda_2 w^2 }$$ (this is called **Elastic Net Regularization**). The L1
regularization has the intriguing property that it leads the weight vectors to become sparse during optimization (i.e.
very close to exactly zero). In other words, neurons with L1 regularization end up using only a sparse subset of their
most important inputs and become nearly invariant to the "noisy" inputs. In comparison, final weight vectors from L2
regularization are usually diffuse, small numbers. In practice, if you are not concerned with explicit feature
selection, L2 regularization can be expected to give superior performance over L1.

#### Max Norm Constraints

Another form of regularization is to enforce an absolute upper bound on the magnitude of the weight vector for every
neuron and use projected gradient descent to enforce the constraint. In practice, this corresponds to performing the
parameter update as normal, and then enforcing the constraint by clamping the weight vector $$\mathit{\vec{w}}$$ of
every neuron to satisfy $$\mathit{\Vert \vec{w} \Vert_2 < c}$$. Typical values of $$\mathit{c}$$ are on orders of 3
or 4. Some people report improvements when using this form of regularization. One of its appealing properties is that
network cannot "explode" even when the learning rates are set too high because the updates are always bounded.

#### Dropout

[Dropout](http://www.cs.toronto.edu/~rsalakhu/papers/srivastava14a.pdf) is an extremely effective, simple and recently
introduced regularization technique. It complements the other methods (L1, L2, maxnorm). While training, dropout is
implemented by only keeping a neuron active with some probability $$\mathit{p}$$ (a hyperparameter), or setting it to
zero otherwise.

![Error loading ann-dropout.png]({{ "/assets/img/ann-dropout.png" | relative_url}})

During training, Dropout can be interpreted as sampling a neural network within the full neural network, and only
updating the parameters of the sampled network based on the input data. (However, the exponential number of possible
sampled networks are not independent because they share the parameters.) During testing there is no dropout applied,
with the interpretation of evaluating an averaged prediction across the exponentially-sized ensemble of all
sub-networks.

Vanilla dropout in an example 3-layer neural network would be implemented as follows:

```python
""" Vanilla Dropout: Not recommended implementation (see notes below) """

p = 0.5 # probability of keeping a unit active. higher = less dropout

def train_step(X):
    """ X contains the data """
    
    # forward pass for example 3-layer neural network
    H1 = np.maximum(0, np.dot(W1, X) + b1)
    U1 = np.random.rand(*H1.shape) < p # first dropout mask
    H1 *= U1 # drop!
    H2 = np.maximum(0, np.dot(W2, H1) + b2)
    U2 = np.random.rand(*H2.shape) < p # second dropout mask
    H2 *= U2 # drop!
    out = np.dot(W3, H2) + b3
    
    # backward pass: compute gradients... (not shown)
    # perform parameter update... (not shown)
  
def predict(X):
    # ensembled forward pass
    H1 = np.maximum(0, np.dot(W1, X) + b1) * p # NOTE: scale the activations
    H2 = np.maximum(0, np.dot(W2, H1) + b2) * p # NOTE: scale the activations
    out = np.dot(W3, H2) + b3
```

In the code above, inside the `train_step` function we have performed dropout twice: on the first hidden layer and on
the second hidden layer. It is also possible to perform dropout right on the input layer, in which case we would also
create a binary mask for the input `X`. The backward pass remains unchanged, but of course has to take into account the
generated masks `U1,U2`.

Crucially, note that in the `predict` function we are not dropping anymore, but we are performing a scaling of both
hidden layer outputs by $$\mathit{p}$$. This is important because at test time all neurons see all their inputs, so we
want the outputs of neurons at test time to be identical to their expected outputs at training time. For example, in
case of $$\mathit{p = 0.5}$$, the neurons must halve their outputs at test time to have the same output as they had
during training time (in expectation). To see this, consider an output of a neuron $$\mathit{x}$$ (before dropout). With
dropout, the expected output from this neuron will become $$\mathit{ px + (1-p)0 }$$, because the neuro's output will be
set to zero with probability $$\mathit{ 1 - p }$$. At test time, when we keep the neuron always active, we must adjust
$$\mathit{ x \rightarrow px }$$ to keep the same expected output. It can also be shown that performing this attenuation
at test time can be related to the process of iterating over all the possible binary masks (and therefore all the
exponentially many sub-networks) and computing their ensemble prediction.

The undesirable property of the scheme presented above is that we must scale the activations by $$\mathit{p}$$ at test
time. Since test-time performance is so critical, it is always preferable to use inverted dropout, which performs the
scaling at train time, leaving the forward pass at test time untouched. Additionally, this has the appealing property
that the prediction code can remain untouched when you decide to tweak where you apply dropout, or if at all. Inverted
dropout looks as follows:

```python
""" 
Inverted Dropout: Recommended implementation example.
We drop and scale at train time and don't do anything at test time.
"""

p = 0.5 # probability of keeping a unit active. higher = less dropout

def train_step(X):
    # forward pass for example 3-layer neural network
    H1 = np.maximum(0, np.dot(W1, X) + b1)
    U1 = (np.random.rand(*H1.shape) < p) / p # first dropout mask. Notice /p!
    H1 *= U1 # drop!
    H2 = np.maximum(0, np.dot(W2, H1) + b2)
    U2 = (np.random.rand(*H2.shape) < p) / p # second dropout mask. Notice /p!
    H2 *= U2 # drop!
    out = np.dot(W3, H2) + b3
  
  # backward pass: compute gradients... (not shown)
  # perform parameter update... (not shown)
  
def predict(X):
    # ensembled forward pass
    H1 = np.maximum(0, np.dot(W1, X) + b1) # no scaling necessary
    H2 = np.maximum(0, np.dot(W2, H1) + b2)
    out = np.dot(W3, H2) + b3
```

There has a been a large amount of research after the first introduction of dropout that tries to understand the source
of its power in practice, and its relation to the other regularization techniques. Recommended further reading for an
interested reader includes:

* [Dropout paper by Srivastava et al. 2014](http://www.cs.toronto.edu/~rsalakhu/papers/srivastava14a.pdf).
* [Dropout Training as Adaptive Regularization](https://proceedings.neurips.cc/paper/2013/file/38db3aed920cf82ab059bfccbd02be6a-Paper.pdf):
  "we show that the dropout regularizer is first-order equivalent to an L2 regularizer applied after scaling the
  features by an estimate of the inverse diagonal Fisher information matrix".

#### Theme of Noise in Forward Pass.

Dropout falls into a more general category of methods that introduce stochastic behavior in the forward pass of the
network. During testing, the noise is marginalized over analytically (as is the case with dropout when multiplying by
$$\mathit{p}$$), or numerically (e.g. via sampling, by performing several forward passes with different random decisions
and then averaging over them). An example of other research in this direction includes **DropConnect**, where a random
set of weights is instead set to zero during forward pass. As foreshadowing, Convolutional Neural Networks also take
advantage of this theme with methods such as stochastic pooling, fractional pooling, and data augmentation.

#### Bias Regularization.

It is not common to regularize the bias parameters because they do not interact with the data through multiplicative
interactions, and therefore do not have the interpretation of controlling the influence of a data dimension on the final
objective. However, in practical applications (and with proper data preprocessing) regularizing the bias rarely leads to
significantly worse performance. This is likely because there are very few bias terms compared to all the weights, so the
classifier can "afford to" use the biases if it needs them to obtain a better data loss.

#### Per-layer Regularization.

It is not very common to regularize different layers to different amounts (except perhaps the output layer). Relatively
few results regarding this idea have been published in the literature.

In practice, it is most common to use a single, global L2 regularization strength that is cross-validated. It is also
common to combine this with dropout applied after all layers. The value of $$\mathit{p = 0.5}$$ is a reasonable default,
but this can be tuned on validation data.

### Perceptrons

> **[Perceptron is a neural network with a single neuron](https://stats.stackexchange.com/a/419776/365124)**.

One type of ANN system is based on a unit called a **perceptron**, illustrated in figure below:

![Error loading ann-a-perceptron.png]({{ "/assets/img/ann-a-perceptron.png" | relative_url}})

A perceptron takes a vector of real-valued inputs, calculates a linear combination of these inputs, then outputs a 1 if
the result is greater than some threshold and -1 otherwise. More precisely, given inputs $$\mathit{x}_1$$ through 
$$\mathit{x}_n$$, the output $$o(\mathit{x}_1, ... , \mathit{x}_n)$$ computed by the perceptron is

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
$$\sum_{i = 0}^{n} \mathit{w}_\mathit{i}\mathit{x}_\mathit{i} > 0$$ or, in vector form, as
$$\vec{\mathit{w}} \cdot \vec{\mathit{x}} > 0$$, we will be able to write the perceptron function as

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

#### Representational Power of Perceptrons

We can view the perceptron as representing a **hyperplane decision surface** in the n-dimensional space of instances
(i.e., points). The perceptron outputs a 1 for instances lying on one side of the hyperplane and outputs a -1 for
instances lying on the other side, as illustrated in figure below

![Error loading ann-n-dimensional-hyperplan-space.png]({{ "/assets/img/ann-n-dimensional-hyperplan-space.png" | relative_url}})

The equation for this decision hyperplane is $$\vec{\mathit{w}} \cdot \vec{\mathit{x}} = 0$$. Of course, some sets of 
positive and negative examples cannot be separated by any hyperplane. Those that can be separated are called **linearly 
separable** sets of examples.

##### Representing AND & OR Function

A single perceptron can be used to represent many boolean functions. For example, if we assume boolean values of 1
(true) and -1 (false), then one example way to use a two-dimensional input perceptron to implement the AND function is
to set the weights $$\mathit{w}_0 = -0.8$$, and $$\mathit{w}_1 = \mathit{w}_2 = 0.5$$:

| **$$\mathit{x}_1$$** | **$$\mathit{x}_2$$** | **$$y$$** | **$$sgn(y)$$** |
|:--------------------:|:--------------------:|:---------:|:--------------:|
|          1           |          1           |    0.2    |       1        |
|          0           |          0           |   -0.8    |       -1       |
|          1           |          0           |   -0.3    |       -1       |
|          0           |          1           |   -0.3    |       -1       |

This perceptron can be made to represent the OR function instead by altering the threshold to $$\mathit{w}_0 = -0.3$$:

| **$$\mathit{x}_1$$** | **$$\mathit{x}_2$$** | **$$y$$** | **$$sgn(y)$$** |
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

#### The Perceptron Training Rule

We begin by understanding how to learn the weights for a single perceptron using two fundamental algorithms that provide
basis for learning networks of many units:

1. the [**Perceptron Rule**](#perceptron-rule)
2. the [**Delta Rule**](#gradient-descent-and-the-delta-rule)

One way to learn an acceptable weight vector is to begin with random weights, then iteratively apply the perceptron to
each training example, modifying the perceptron weights whenever it misclassifies an example. This process is repeated,
iterating through the training examples as many times as needed until the perceptron classifies all training examples
correctly. Weights are modified at each step according to the perceptron training rule, which revises the weight
$$\mathit{w}_\mathit{i}$$ associated with input $$\mathit{x}_\mathit{i}$$ according to the rule

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

#### Gradient Descent and the Delta Rule

A second training rule, called the **delta rule**, is designed to overcome the difficulty of the
[Perceptron Rule](#perceptron-rule) when the training examples are not linearly separable. The key idea behind the delta
rule is to use **gradient descent** to search the hypothesis space of possible weight vectors to find the weights that
best fit the training examples

The delta training rule is best understood by considering the task of training an **unthresholded perceptron**; that is,
a _linear_ unit for which the output $$o$$ is given by

$$ o(\vec{\mathit{x}}) = \vec{\mathit{w}} \cdot \vec{\mathit{x}} $$

In order to derive a weight learning rule for linear units, let us begin by specifying a measure for the **training
error** of a hypothesis (weight vector), relative to the training examples

$$ \mathit{E}(\vec{\mathit{w}}) \equiv \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d})^2 $$

where $$D$$ is the set of training examples; $$t_d$$ is the target output for training example $$d$$, and $$o_d$$ is the
output of the linear unit for training example $$d$$. By this definition, $$\mathit{E}(\vec{\mathit{w}})$$ is simply
half the squared difference between the target output $$t_d$$ and the linear unit output $$o_d$$, summed over **all**
training examples

##### Visualizing the Hypothesis Space

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

##### Stochastic Approximation to Gradient Descent

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

#### Remarks

The key difference between [Perceptron Training Rule](#the-perceptron-training-rule) and
[Gradient Descent and the Delta Rule](#gradient-descent-and-the-delta-rule) is that the former updates weights based on
the error in the _thresholded_ perceptron output, whereas the latter updates weights based on the error in the
_unthresholded_ linear combination of inputs.

### Multilayer Networks & The Backpropagation Algorithm

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

> 📋 Note that the output of sigmoid function ranges between 0 and 1. Because it maps a very large input domain to a 
> small range of outputs, it is often referred to as the **squashing function** of the unit

The sigmoid function doesn't always have to be in the form stated above as long as it keeps the property that its
derivative is easily expressed. For example, in this particular form, we can easily see

$$ \frac{\mathit{d\sigma(y)}}{\mathit{dy}} = \sigma(\mathit{y})(1 - \sigma(\mathit{y})) $$

Other differentiable functions, such as $$\frac{1}{1 + \exp^{-\mathit{ky}}}$$ where $$\mathit{k}$$ is some positive
constant, and $$tanh$$ are sometimes used instead

#### The Backpropagation Algorithm

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
weight values for all the units in the network. The algorithm below outlines the stochastic gradient descent version of
the Backpropagation algorithm for feedforward networks containing **two** layers of sigmoid units, with _units at each
layer connected to all units from the preceding layer_.

The algorithm begins by constructing a network with the desired number of hidden and output units and initializing all
network weights to small random values. Given this fixed network structure, the main loop of the algorithm then
repeatedly iterates over the training examples. For each training example, it applies the network to the example,
calculates the error of the network output for this example, computes the gradient with respect to the error on this
example, then updates all weights in the network. This gradient descent step is iterated (often thousands of times,
using the same training examples multiple times) until the network performs acceptably well

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
>        2. For each network output unit $$\mathit{k}$$, calculate its error term $$\delta_\mathit{k}$$
>
>           $$ \delta_\mathit{k} \leftarrow \mathit{o_k(1 - o_k)(t_k - o_l)}$$
>
>        3. For each hidden unit $$\mathit{h}$$, calculate its error term $$\delta_\mathit{h}$$
>
>           $$ \delta_\mathit{h} \leftarrow \mathit{o_h(1 - o_h)}\sum_{\mathit{k \in \text{outputs}}}\mathit{w_{kh}\delta_k} $$
>
>        4. Update each network weight $$\mathit{w_{ji}}$$
>
>           $$ \mathit{w_{ji}} \leftarrow \mathit{w_{ji}} + \Delta\mathit{w_{ji}} $$
>
>           where
>
>           $$ \mathit{w_{ji}} = \eta\delta_{\mathit{j}}\mathit{x_{ji}} $$

> 📋 The algorithm updates weights incrementally, following the presentation of each training example. This corresponds
> to a stochastic approximation to gradient descent

The weight-update loop in Backpropagation maybe e iterated thousands of times in a typical application. A variety of
termination conditions can be used to halt the procedure. One may choose to halt after a fixed number of iterations
through the loop, or once the error on the training examples falls below some threshold, or once the error on a separate
validation set of examples meets some criterion. The choice of termination criterion is an important one, because too
few iterations can fail to reduce error sufficiently, and too many can lead to overfitting the training data

One major difference in the case of multilayer networks is that the error surface can have multiple local minima, in
contrast to the single-minimum parabolic error surface, which means that gradient descent, in this case, is guaranteed
only to converge toward some local minimum

##### Variations

###### Updating Weight with Momentum

Because Backpropagation is such a widely used algorithm, many variations have been developed. Perhaps the most common is
to alter the weight-update rule. For example, one approach is making the weight update on the _n_-th iteration depend
partially on the update that occurred during the _(n - 1)_-th iteration:

$$ \Delta\mathit{w_{ji}(n)} = \mathit{\eta\delta_j x_{ji} + \alpha\Delta w_{ji}(n - 1)} $$

where $$0 < \alpha < 1$$ is a constant called **momentum**. The second term on the right of the equation is called the
**momentum term**. To see the effect of this momentum term, consider that the gradient descent search trajectory is
analogous to that of a (momentumless) ball rolling down the error surface. The effect of $$\alpha$$ is to add momentum
that tends to keep the ball rolling in the same direction from one iteration to the next. This can sometimes have the
effect of keeping the ball rolling through small local minima in the error surface, or along flat regions in the surface
where the ball would stop if there were no momentum. It also has the effect of gradually increasing the step size of the
search in regions where the gradient is unchanging, thereby speeding convergence.

##### Learning in Arbitrary Acyclic Networks

To generalize the [two-layer network backpropagation algorithm](#the-backpropagation-algorithm) to feedforward
networks of _arbitrary depth_, we only need to change the procedure for computing $$\delta$$. In general, the
$$\delta_{\mathit{r}}$$ value for a unit $$\mathit{r}$$ in layer $$\mathit{m}$$ is computed from the $$\delta$$ value
at the next deeper layer $$\mathit{m} + 1$$:

$$ \delta_{\mathit{r}} = \mathit{o_r(1 - o_r)}\sum_{\mathit{s \in \text{layer} (m + 1)}} \mathit{w_{sr}\delta_s} $$

> 📋 Note that this is the generalization of
> $$ \delta_\mathit{h} \leftarrow \mathit{o_h(1 - o_h)}\sum_{\mathit{k \in \text{outputs}}}\mathit{w_{kh}\delta_k} $$
> in the two-layer algorithm

It is equally straightforward to generalize the algorithm to any _directed acyclic graph_, regardless of whether the
network units are arranged in uniform layers as we have assumed up to now. In the case that they are not, the rule for
calculating $$\delta$$ for any internal unit (i.e., any unit that is not an output) is

$$ \delta_{\mathit{r}} = \mathit{o_r(1 - o_r)}\sum_{\mathit{s \in \text{Downstream}(r)}} \mathit{w_{sr}\delta_s} $$

where Downstream(r) is the set of units immediately downstream from unit $$\mathit{r}$$ in the network: that is, all
units whose inputs include the output of unit $$\mathit{r}$$

###### Derivation of the Backpropagation Rule for Arbitrary Acyclic Networks

#### Remarks of the Backpropagation Algorithm

##### Convergence and Local Minima

As shown above, the Backpropagation Algorithm implements a gradient descent search through the space of possible network
weights, iteratively reducing the error $$\mathit{E}$$ between the training example target values and the network
outputs. Because the error surface for multilayer networks may contain many different local minima, gradient descent can
become trapped in any of these. As a result, Backpropagation Algorithm ultilayer networks is only guaranteed to converge
toward some local minimum in E and not necessarily to the global minimum error.

Despite the lack of assured convergence to the global minimum error, Backpropagation is a highly effective function
approximation method in practice. In many practical applications the problem of local minima has not been found to
be as severe as one might fear.

To develop some intuition here, consider that networks with large numbers of weights correspond to error surfaces in
very high dimensional spaces (one dimension per weight). When gradient descent falls into a local minimum with respect
to one of these weights, it will not necessarily be in a local minimum with respect to the other weights. In fact, the
more weights in the network, the more dimensions that might provide "escape routes" for gradient descent to fall away
from the local minimum with respect to this single weight.

A second perspective on local minima can be gained by considering the manner in which network weights evolve as the
number of training iterations increases. Notice that if network weights are initialized to values near zero, then during
early gradient descent steps the network will represent a very smooth function that is approximately linear in its
inputs. This is because the sigmoid threshold function itself is approximately linear when the weights are close to
zero. Only after the weights have had time to grow will they reach a point where they can represent highly nonlinear
network functions. One might expect more local minima to exist in the region of the weight space that represents these
more complex functions. One hopes that by the time the weights reach this point they have already moved close enough to
the global minimum that even local minima in this region are acceptable.

Common heuristics to attempt to alleviate the problem of local minima include:

* Add a [momentum](#updating-weight-with-momentum) term to the weight-update rule. Momentum can sometimes carry the
  gradient descent procedure through narrow local minima (though in principle it can also carry it through narrow global
  minima into other local minima!).
* Use stochastic gradient descent rather than true gradient descent. The stochastic approximation to gradient descent
  effectively descends a different error surface for each training example, relying on the average of these to
  approximate the gradient with respect to the full training set. These different error surfaces typically will have
  different local minima, making it less likely that the process will get stuck in any one of them.
* Train multiple networks using the same data, but initializing each network with different random weights. If the
  different training efforts lead to different local minima, then the network with the best performance over a separate
  validation data set can be selected. Alternatively, all networks can be retained and treated as a "committee" of
  networks whose output is the (possibly weighted) average of the individual network outputs.

Knowledge Graph Embeddings (KGE)
--------------------------------

In representation learning, **knowledge graph embedding** (**KGE**), also referred to as **knowledge representation 
learning** (**KRL**), or **multi-relation learning**, is a machine learning task of learning a low-dimensional 
representation of a knowledge graph's entities and relations while preserving their semantic meaning. Leveraging their 
embedded representation, knowledge graphs (KGs) can be used for various applications such as link prediction, triple 
classification, entity recognition, clustering, and relation extraction.

### Knowledge Graph

Let's start with a quick introduction and we need to define what a knowledge graph is first:

![Error loading kge-what-is-a-knowledge-graph.png]({{ "/assets/img/kge-what-is-a-knowledge-graph.png" | relative_url}})

It's a graph-based data representation modality where we have binary relations and labeled edges. We also have directed
edges. They're very important because **relations in knowledge graphs have a subject and an object**. For example, we
have Mike born in Liverpool and the direction of the edge is important for the semantic of the predicate.

Something interesting to point out is that knowledge graphs can have multiple relation types so multiple nodes such as
Mike and Liverpool can be connected with edges labeled with different semantics. Knowledge graphs are quite interesting
for a number of applicative use cases ranging from social network to
[web-based collaborative knowledge bases](https://lod-cloud.net/) or in [healthcare](https://www.ebi.ac.uk/) when trying
to model protein interaction networks and genetic information so there's a wealth of knowledge graphs adopted in
literature and there's a wealth of knowledge graphs available online and they can be domain specific or general purpose
such as [yago](https://yago-knowledge.org/), [Wikidata](https://wikidata.org/), and [DBpedia](https://www.dbpedia.org/).
These are knowledge graphs which are automatically generated in some cases from text from mining web pages like
[GDELT](https://www.gdeltproject.org/) or they are the result of crowd-sourced operations

![Error loading kge-datasets.png]({{ "/assets/img/kge-datasets.png" | relative_url}})

### Open World Assumption

One problem is that these datasets or graphs can be the result of automatic generation and craft sourcing so keep in
mind they may have missing edges or they may not include all the facts and they may not be entirely comprehensive.
That means knowledge graphs operate under this regime which is known as the **Open World Assumption**, which says that
if a fact is not in the knowledge graph, it is not necessarily false; we simply don't know whether it's false or not.
For example Acme Inc. is the workplace of George. But do we know Acme Inc is based in Liverpool? We don't. That doesn't
necessarily mean that the absence of a fact means that fact is false in this context

![Error loading kge-assumption.png]({{ "/assets/img/kge-assumption.png" | relative_url}})

### Statistical Relational Learning

When talking about Machine Learning and knowledge graphs, I'd like to introduce the the family of models that belong to
the so-called area of **Statistical Relational Learning**. It's a term used in a different context and applying machine
learning techniques to graphs is quite useful.

Large knowledge graphs have a lot of varied information in them. We can think about tasks of predicting links or similar
tasks as **Triple Classification** which is used to complete a graph or to recommend content
or for question answering.

We could also think about something called **Collective Node Classification** or another similar task called
**Link-Based Clustering**. This is useful for **customer segmentation**

![Error loading kge-srl.png]({{ "/assets/img/kge-srl.png" | relative_url}})

We could think about other tasks such as **Matching Entities**. For example, we have "Alec Guinness" as an actor and
"Arthur Guinness" as the founder of the beer Guinness. When we see an entity called "A. Guinness", is this "A. Guinness"
Arthur or Alec? By looking at the topology of the graph and by processing the graph we can associate and merge
"A. Guinness" with "Alec Guinness".

#### Link Prediction

A number of things can be done with Machine Learning on graphs. Link Prediction and Triple Classifications are the two
most popular tasks that can be carried out. In this post we'll focus mostly on Link Prediction, which is the task of
assigning a score to a triple, to a fact, such that the higher the score the higher the chances that fact is true.

There is a similar path which is a binary classification task called Triple Classification that is used to decide again
in a binary classification setting whether a link is a missing link is true or false.

![Error loading kge-link-prediction.png]({{ "/assets/img/kge-link-prediction.png" | relative_url}})

The area of statistical relational learning is quite an established field. There have been a number of
techniques proposed in the past few years that are radically different from what you're going to see in this post -
techniques such as  logic programming. But it's important to know these methods:

![Error loading kge-traditional.png]({{ "/assets/img/kge-traditional.png" | relative_url}})

The problem with these techniques is mostly that they're limited in scalability. When it comes to knowledge graphs
we need methods that scale a bit better given the size of the graphs that we are processing. Some of them also have
limited modeling power compared to what is feasible with knowledge graph embeddings.

Around 10 years ago people started considering the paradigm of **Representation Learning** for graphs and this has
lately been called **Graph Representation Learning** which is an area where we apply Machine Learning on graphs but we
sort of avoid extracting features manually because that is really hard and time-consuming on graphs. Instead of
hand-designing features, we apply the representation learning paradigm which is learning features or learning
representations of nodes and edges automatically. This is what graph representation learning
does. It could be carried out using a number of ways right using a number of uh tools. we could use existing mainstream
architectures such as convolutional neural networks or RNNs.

But the problem is that graphs are definitely more complex than these models are designed to handle. This is why the
community came up with graph representation learning. It's an area that includes a number of methods models that learn
representations of nodes and edges.

![Error loading kge-grl.png]({{ "/assets/img/kge-grl.png" | relative_url}})

We turn nodes into vector representations and relation types into the vector counterparts. This is done so that with
these learned weights, or learned embeddings, we can carry out a downstream task which can predict a link and classify a
node. We know how to handle vectors much better than the nodes and edges because vectors can be processed with neural
architectures they can be processed by GPU units. It's a well established paradigm of modeling and processing
information.

Graph representation learning is obviously quite a broad area of research and there are a lot of families of models.
**Knowledge Graph Embedding (KGE)**, also referred to as **Knowledge Representation Learning (KRL)**, is a model that,
by leveraging supervised learning, a machine learning task of learning a low-dimensional representation of a knowledge
graph's entities and relations while preserving their semantic meaning. Leveraging their embedded representation,
knowledge graphs (KGs) can be used for various applications such as link prediction, triple classification, entity
recognition, clustering, and relation extraction.

We start from a graph made of nodes and edges; then we move to a vector space where each point represent a concept and
the position in the space of each point is semantically meaningful.

### Formal Definition of Knowledge Graph Embeddings (KGE)

With the informal discussion above, we could present KGE in a rigorous definition

#### Definition

A knowledge graph $${\mathcal {G}}=\{E,R,F\}$$ is a collection of entities $$E$$, relations $$R$$, and facts $$F$$. A
_fact_ is a triple $$(h,r,t)\in F$$ that denotes a link $$r\in R$$ between the head $$h\in E$$ and the tail $$t\in E$$ of
the triple. Another notation that is often used in the literature to represent a triple (or fact) is $$<head,relation,tail>$$. This notation is called **resource description framework (RDF)**. A knowledge graph represents the knowledge related to a specific domain; leveraging this structured representation, it is possible to **infer a piece of new knowledge** from it after some refinement steps. However, nowadays, people have to deal with the sparsity of data and the computational inefficiency to use them in a real-world application.

The embedding of a knowledge graph translates each entity and relation of a knowledge graph, $${\mathcal {G}}$$ into a
vector of a given dimension $$d$$, called **embedding dimension**. In the general case, we can have different embedding
dimensions for the entities $$d$$ and the relations $$k$$. The collection of embedding vectors for all the entities and
relations in the knowledge graph are a more dense and efficient representation of the domain that can more easily be
used for many different tasks.

![Error loading kge-kge.png]({{ "/assets/img/kge-kge.png" | relative_url}})

A knowledge graph embedding is characterized by four different aspects:

1. **Representation Space** The low-dimensional space in which the entities and relations are represented.
2. **Scoring Function** A measure of the goodness of a triple embedded representation.
3. **Encoding Models** The modality in which the embedded representation of the entities and relations interact with each  other.
4. **Additional Information** Any additional information coming from the knowledge graph that can enrich the embedded
   representation. Usually, an ad hoc scoring function is integrated into the general scoring function for each
   additional information.

##### Embedding procedure

All the different knowledge graph embedding models follow roughly the same procedure to learn the semantic meaning of
the facts. First of all, to learn an embedded representation of a knowledge graph, the embedding vectors of the entities
and relations are initialized to random values. Then, starting from a training set until a stop condition is reached,
the algorithm continuously optimizes the embeddings. Usually, the stop condition is given by the overfitting over the
training set. Each iteration samples a batch of size $$b$$ from the training set, and for each triple of the batch, a
random corrupted fact i.e., a triple that does not represent a true fact in the knowledge graph. The corruption of a
triple involves substituting the head or the tail (or both) of the triple with another entity that makes the fact false.
The original triple and the corrupted triple are added in the training batch, and then the embeddings are updated,
optimizing a scoring function. At the end of the algorithm, the learned embeddings should have extracted the semantic
meaning from the triples and should correctly unseen true facts in the knowledge graph.

### RDF Concepts

RDF uses the following key concepts:

* [Graph data model](#graph-data-model)
* [URI-based vocabulary](#uri-based-vocabulary-and-node-identification)
* Datatypes
* Literals
* XML serialization syntax
* Expression of simple facts
* Entailment

#### Graph Data Model

The underlying structure of any expression in RDF is a collection of **triple**s, each consisting of a **subject**, a
**predicate** and an **object**. A set of such triples is called an **RDF graph**. This can be illustrated by a node
and directed-arc diagram, in which each triple is represented as a node-arc-node link (hence the term "graph").

![Error loading rdf-triple.png!]({{ "/assets/img/rdf-triple.png" | relative_url}})

Each triple represents a statement of a relationship between the things denoted by the nodes that it links. Each triple has three parts:

1. a subject,
2. an object, and
3. a predicate (also called a property) that denotes a relationship.

_The direction of the arc is significant: it always points toward the object._

The nodes of an RDF graph are its subjects and objects.

**The assertion of an RDF triple says that some relationship, indicated by the predicate, holds between the things
denoted by subject and object of the triple. The assertion of an RDF graph amounts to asserting all the triples in it,
so the meaning of an RDF graph is the conjunction (logical AND) of the statements corresponding to all the triples it
contains**. A formal account of the meaning of RDF graphs is given in [RDF-SEMANTICS].

#### URI-based Vocabulary and Node Identification

### Best Python Packages (Tools) for Knowledge Graphs

A Knowledge Graph is a reusable data layer that is used to answer sophisticated queries across multiple data silos. With
contextualized data displayed and organized in the form of tables and graphs, they achieve pinnacle connectivity. They
can quickly accept new information, classifications, and criteria since they were designed to capture the ever-changing
nature of the data. There are different libraries for performing knowledge graphs in Python. Let's check out a few of
them.

#### Pykg2vec

[Pykg2vec](https://github.com/Sujit-O/pykg2vec) is a Python package that implements knowledge graph embedding algorithms
and flexible embedding pipeline building elements. This library seeks to assist academics and programmers in fast
testing algorithms with their knowledge base, or adapting the package for their algorithms using modular blocks.

Pykg2vec was built using TensorFlow, but because more authors utilized Pytorch to create their KGE models, it was
switched with Pytorch. The TF version is still available in the tf2-master branch. In addition to the primary model
training procedure, pykg2vec uses multi-processing to generate mini-batches and conduct an assessment to minimize the
overall completion time.

#### PyKEEN

[PyKEEN (Python Knowledge Embeddings)](https://github.com/pykeen/pykeen) is a Python library that builds and evaluates
knowledge graphs and embedding models. In PyKEEN 1.0, we can estimate the aggregation measures directly for all frequent
rank categories. Such as mean, optimistic, and pessimistic, allowing comparison of their differences.

It can identify instances where the model precisely forecasts identical scores for various triples, which is typically
undesirable behavior. The PyTorch module is used to implement it for Python 3.7+. It includes a set of comprehensive
testing processes performed with PyTest and Tox. You can execute in Travis-continuous CI's integration environment.

#### AmpliGraph

Knowledge graph embeddings can be used for various tasks, including knowledge graph completion, information retrieval,
and link-based categorization, to name a few. [AmpliGraph](https://github.com/Accenture/AmpliGraph/) is the first
open-source toolkit to democratize graph representation learning, allowing for **discovering whole new knowledge from
existing graphs**.

The AmpliGraph package includes machine learning models that can generate knowledge graph embeddings (KGEs), low-level
vector representations of the items, and relationships that make up a knowledge graph.

These models use low-dimensional vectors to encode nodes and relationships of a graph. As a result, subsequent systems
that depend on those graphs, such as question-answering software, improve efficiency.

It reduces the entry barriers for knowledge graph embeddings, making such models available to even the most unskilled
users and establishing a community of professionals who can benefit from the freeware API for learning on knowledge
graphs.

#### LibKGE

[LibKGE](https://github.com/uma-pi1/kge)'s primary purpose is to promote repeatable study into KGE models and training
techniques. The training approach and hyperparameters selected significantly impact simulation results than the model
class alone.

The goal of LibKGE is to provide simple training, hyperparameter optimization, and assessment procedures that can be
used with any model. Every possible knob or heuristic in the platform is available explicitly through well-documented
configuration files. The most common KGE models are included in LibKGE, and you can introduce new models. A thorough
logging mechanism and equipment facilitate in-depth examination.


Understanding the Philosophy of Learning through Kant's Critique of Pure Reason
-------------------------------------------------------------------------------

Machine learning draws on ideas from a diverse set of disciplines, including artificial intelligence, probability and
statistics, computational complexity, information theory, psychology and neurobiology, control theory, and
**Philosophy**. For example, in Philosophy:

* Occam's razor, suggests that the simplest hypothesis is the best.
* Analysis of the justification for generalizing beyond observed data.

**This post is purely my exploration of philosophical disciplines on the design and implementation of ML & AI
algorithms**, with an exclusive scope on [**Immanuel Kant**](https://en.wikipedia.org/wiki/Immanuel_Kant) Philosophy. I
chose this topic because I believe any successful ML & AI applications in the world of business must come from a
_profound_ design and implementation of some intelligent system which is not possible without some rigorous
justifications. AI & ML, in my opinion, is about the philosophy of learning applied to machines (I came to this
conclusion after my [university course on Machine Learning](https://courses.engr.illinois.edu/cs446/sp2015/index.html)).
The accuracy of an intelligent system pretty much depend on how we frame and bias a learning algorithm. The approach to
that requires a solid understanding of learning theory, which is why it has been stated at the beginning of this post
that Machine Learning draws from the discipline of Philosophy, and hence, I assembled this blog post.

How I ended up studying Kant's Philosophy for ML & AI (and life in general) is probably out of interests of most
readers, but what I do need to tell is that I found Kant's approach to human learning strategy works pretty well in many
quotidian tasks in our daily lives. I believe his rigorous treatise on learning theory will work for machine as well and
help us design better ML & AI algorithms.

[_The Critique of Pure Reason_](https://trello.com/c/E3Cwohv5) is a book by Kant, in which he seeks to determine the
limits and scope of [metaphysics](https://en.wikipedia.org/wiki/Metaphysics). Also referred to as Kant's
"First Critique", it was followed by his Critique of Practical Reason (1788) and Critique of Judgment (1790).

### I. Transcendental Doctrine of Elements

#### _a priori_

_Kant argues that our mathematical, physical, and quotidian knowledge of nature requires certain judgments that are
"synthetic"_ rather than "analytic," that is, going beyond what can be known solely in virtue of the contents of the
concepts involved in them and the application of the logical principles of identity and contradiction to these concepts,
and yet also knowable **_a priori_**, that is, independently of any particular experience since no particular experience
could ever be sufficient to establish the universal and necessary validity of these judgments.

Kant agrees with Locke that we have no innate knowledge, that is, no knowledge of any particular propositions implanted
in us by God or nature prior to the commencement of our individual experience. But experience is the product both of
_external objects_ affecting our sensibility and of the _operation_ of our cognitive faculties in response to this
effect, and Kant's claim is that **we can have "pure" or _a priori_ cognition of the contributions to experience made by
the operation of these faculties themselves, rather than of the effect of external objects on us in experience**.

Kant divides our cognitive capacities into

1. our receptivity to the effects of external objects acting on us and giving us sensations, through which these objects
   are given to us in empirical intuition, and
2. our active faculty for relating the data of intuition by thinking them under concepts, which is called understanding,
   and forming judgments about them

This division is the basis for Kant's division of the "Transcendental Doctrine of Elements" into

1. the "Transcendental Aesthetic," which deals with sensibility and its pure form, and
2. the "Transcendental Logic," which deals with the operations of the understanding and judgment as well as both the
   spurious and the legitimate activities of theoretical reason

> _a priori_ is the Initial Parameters Set in ML & AI Algorithms
>
> Recall from the
> [Backpropagation algorithm in artificial neural networks](https://qubitpi.github.io/jersey-guide/2022/08/09/artificial-neural-networks.html#the-backpropagation-algorithm),
> that we gave some very small initial weights which later were adjusted in accordance with the learning data set.
>
> **The initial weights mirrors the concept of "_a priori_"**. It is important, during the design of algorithm, to keep
> drawing from how "_a priori_" works. **The analogy of "_a priori_" should guide our initial assignments to those
> weight values**.
>
> A pure _a priori_ with **no innate knowledge** corresponds to (approximately) the
> [random initialization with small values](https://qubitpi.github.io/jersey-guide/2022/08/09/artificial-neural-networks.html#weight-initialization). Fundamentally,
>
> **No knowledge** is mathematically defined by **0**.
>
> In that sense, since initial weights are assigned only close to 0, **artificial neural networks is doomed to be an
> approximation to the world**. The accuracy of it denotes to how close that approximation ends up with.

More discussion of "_a priori_" coming...

#### "Transcendental Aesthetic": Space, Time, and Transcendental Idealism

> [In Kantian philosophy](https://www.merriam-webster.com/dictionary/transcendental), **transcendental** is
>
> 1. of or relating to experience as determined by the mind's makeup
> 2. [transcending](https://www.merriam-webster.com/dictionary/transcending) experience but not human knowledge

Kant attempts to distinguish the contribution to cognition made by our receptive faculty of sensibility from that made
solely by the objects that affect us, and argues that **space and time are pure forms of all intuition contributed by
our own faculty of sensibility, and therefore forms of which we can have _a priori_ knowledge**

Space and time are neither subsistent beings nor inherent in things as they are in themselves, but are rather only forms
of our sensibility, hence conditions under which objects of experience can be given at all and the fundamental principle
of their representation and individuation

Kant's thesis that space and time are pure forms of intuition leads him to the paradoxical conclusion that although
space and time are _empirically real_, they are _transcendentally ideal_, and so are the objects given in them. Although
the precise meaning of this claim remains subject to debate, in general terms it is the claim that _it is only from the
human standpoint that we can speak of space, time, and the spatiotemporality of the objects of experience, thus that we
cognize these things not as they are in themselves but only as they appear under the conditions of our sensibility_.
This is Kant's famous doctrine of **Transcendental Idealism**, _which can be employed throughout the learning process of
a machine_

#### "Transcendental Analytic": the Metaphysical and Transcendental Deductions

##### Analytic of Concepts

In the "Analytic of Concepts," Kant presents the understanding as the source of certain concepts that are _a priori_ and
are conditions of the possibility of any experience whatever. These twelve basic concepts, which Kant calls the
**categories**, are fundamental concepts of an object in general, or the forms for any particular concepts of objects,
and in conjunction with the _a priori_ forms of intuition are the basis of all synthetic _a priori_ cognition.

Kant derives the twelve categories from a table of the twelve logical functions or forms of judgments, the logically
significant aspects of all judgments. Kant's idea is that just as there are certain essential features of all judgments,
so there must be certain corresponding ways in which we form the concepts of objects so that judgments may be about
objects.

There are 4 main logical features of judgments:

1. their quantity, or the scope of their subject-terms;
2. the quality of their predicate-terms, whose contents are realities and negations;
3. their relation, or whether they assert a relation just between a subject and predicate or between two or more
   subject-predicate judgments; and
4. their modality, or whether they assert a possible, actual, or necessary truth

Under each of these four headings there are supposed to be three different options:

![Error loading kant-12-categories.png]({{ "/assets/img/kant-12-categories.png" | relative_url}})

Kant holds there to be twelve fundamental categories for conceiving of the quantity, quality, relation, and modality of
objects

#### PART II. The Transcendental Analytic

The machine cognition arises from two fundamental sources

1. the first of which is the reception of representations, or instances
2. the second the faculty for cognizing an object by means of these representations

Through the former an training instance is **given** to machine, through the latter it is **computed** in relation to
that representation of instance (as a mere determination of the algorithm). Intuition and concepts therefore constitute
the elements of all machines' cognition, so that neither concepts without intuition corresponding to them in some way
nor intuition without concepts can yield a cognition. Both are either pure or empirical. **Empirical**, if sensation is
contained therein; but **pure** if no sensation is mixed into the representation. One can call the latter the matter
of sensible cognition. Thus pure intuition contains merely the form under which something is intuited, and pure concept
only the form of thinking of an object in general.

**Only pure intuitions or concepts alone are possible _a priori_, empirical ones only _a posteriori_**.

If we will call the **receptivity** of machine to receive instances insofar as it is affected in some way
**sensibility**, then on the contrary the faculty for computing, adjusting, and classifying instances, or the
**spontaneity** of cognition, is the **understanding**. **Intuition** can never be other than **sensible**, i.e., that
it contains only the way in which we are affected by objects. The faculty for thinking
of objects of sensible intuition, on the contrary, is the understanding.
Neither of these properties is to be preferred to the other.

Without sensibility no object would be given to machine, and without understanding via computing none would be thought
or trained. Training without instances are non-existing; intuitions (initial-weight) without concepts (adjustments) are
blind. It is thus just as necessary to make the hypothesis sensible (i.e., to add an instance to them in intuition) as
it is to make its intuitions understandable (i.e., to bring them under concepts). Further, these two faculties or
capacities cannot exchange
their functions.

The training through instances is an unrelated process of initial weight assignment, and the initialization are not in
any way determining the resulting training model. Only from their unification can trained model arise. But on this
account one must not mix up their roles, rather one has great cause to separate them carefully from each other and
distinguish them. Hence we distinguish the science of the rules of algorithm initialization in general, i.e., aesthetic,
from the science of the rules of training in general, i.e., logic


TensorFlow 2
------------

### Install TensorFlow 2

Install TensorFlow with Python's pip package manager.

```bash
# Requires the latest pip
pip3 install --upgrade pip

# Current stable release for CPU and GPU
pip3 install tensorflow
```

> 💡 In case we are running low on memory, `pip3 install tensorflow` might get "killed":
> 
> ```
> $ pip3 install tensorflow
> Defaulting to user installation because normal site-packages is not writeable
> Collecting tensorflow
> Downloading tensorflow-2.10.0-cp310-cp310-manylinux_2_17_x86_64.manylinux2014_x86_64.whl (578.0 MB)
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╸ 578.0/578.0 MB 10.8 MB/s eta 0:00:01Killed
> ```
> 
> Note the last word in the last line.
> 
> We could try instead with
> 
> ```bash
> pip3 install tensorflow --no-cache-dir
> ```

Alternatively, the [TensorFlow Docker images](https://hub.docker.com/r/tensorflow/tensorflow/) are already configured to 
run TensorFlow. A Docker container runs in a virtual environment and is the easiest way to set up GPU support.

```bash
docker pull tensorflow/tensorflow:latest  # Download latest stable image
docker run -it -p 8888:8888 tensorflow/tensorflow:latest-jupyter  # Start Jupyter server 
```


Additional Resources
--------------------

* [deeplearning.net tutorial](http://www.deeplearning.net/tutorial/mlp.html) with Theano
* [ConvNetJS](http://cs.stanford.edu/people/karpathy/convnetjs/) demos for intuitions
* [Michael Nielsen's tutorials](http://neuralnetworksanddeeplearning.com/chap1.html)

### Free Datasets

* [CIFAR-10](https://www.cs.toronto.edu/~kriz/cifar.html)


[DecisionTreeClassifier]: https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html#sklearn.tree.DecisionTreeClassifier
