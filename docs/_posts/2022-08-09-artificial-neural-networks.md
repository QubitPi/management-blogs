---
layout: post
title: (Updating...) Machine Learning - Artificial Neural Networks
tags: [Machine Learning, Artificial Neural Networks, ANN, CNN, ConvNet, Convolutional Neural Networks, Neural Networks]
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


Neurons
-------

The basic unit of computation in a neural network is the **neuron**, often called a **node** or **unit**. It receives 
inputs from some other units, or from an external source and computes an output. Each input has an associated
**weight**, which is assigned _on the basis of its relative importance to other inputs_. The unit applies an
**activation function** to the weighted sum of its inputs. The purpose of the activation function is to introduce 
non-linearity into the output of a neuron. This is important because most real world data is non-linear and we would
like neurons to learn these non-linear representations.

The basic computational unit of the brain is a neuron. Approximately 86 billion neurons can be found in the human
nervous system and they are connected with approximately $$10^{14}$$ ~ $$10^{15}$$ **synapses**.

![Error loading ann-biological-neuron-vs-computational-neuron.png]({{ "/assets/img/ann-biological-neuron-vs-computational-neuron.png" | relative_url}})

The diagram above shows a cartoon drawing of a biological neuron (left) and a common mathematical model (right). Each 
neuron receives input signals from its dendrites and produces output signals along its (single) **axon**. The axon 
eventually branches out and connects via synapses to dendrites of other neurons. In the computational model of a neuron, 
the signals that travel along the axons (e.g. $$\mathit{x_0}$$) interact multiplicatively (e.g. $$\mathit{w_0x_0}$$)
with the dendrites of the other neuron based on the synaptic strength at that synapse (e.g. $$\mathit{w_0}$$). The idea
is that the synaptic strengths (the weights $$\mathit{w}$$) are learnable and control the strength of influence (and its 
direction: excitory (positive weight) or inhibitory (negative weight)) of one neuron on another. In the basic model, the 
dendrites carry the signal to the cell body where they all get summed. If the final sum is above a certain threshold,
the neuron can fire, sending a spike along its axon. In the computational model, we assume that the **precise timings of 
the spikes do not matter, and that only the frequency of the firing communicates information**. Based on this rate code 
interpretation, **we model the firing rate of the neuron with the activation function $$\mathit{f}$$**, which represents 
the frequency of the spikes along the axon. Historically, a _common choice of activation function is the **sigmoid 
function σ**_, since it takes a real-valued input (the signal strength after the sum) and squashes it to range between 0 
and 1. We will see details of these activation functions later in this section.

An example code for a single forward-propagating neuron might look as follows:

```python
class Neuron(object):
      # ... 
      def forward(self, inputs):
            """ assume inputs and weights are 1-D numpy arrays and bias is a number """
            cell_body_sum = np.sum(inputs * self.weights) + self.bias
            firing_rate = 1.0 / (1.0 + math.exp(-cell_body_sum)) # sigmoid activation function
            return firing_rate
```

> ⚠️ It it important to stress that this model of a biological neuron is very coarse. for example, there are many
> different types of neurons, each with different properties. The dendrites in biological neurons perform complex 
> nonlinear computations. The synapses are not just a single weight, they are a complex non-linear dynamical system. The 
> exact timing of the output spikes in many systems is known to be important, suggesting that the rate code
> approximation may not hold. Due to all these and many other simplifications, be prepared to hear groaning sounds from 
> anyone with some neuroscience background if you draw analogies between Neural Networks and real brains. See this
> [review](https://physics.ucsd.edu/neurophysics/courses/physics_171/annurev.neuro.28.061604.135703.pdf), or more
> recently this [review](http://www.sciencedirect.com/science/article/pii/S0959438814000130) if you are interested.

### Feed-forward Neural Network

The feedforward neural network was the first and the simplest type of artificial neural network. It contains multiple 
neurons (nodes) arranged in **layers**. Units from adjacent layers have connections or edges between them. All these 
connections have weights associated with them. For example.

![Error loading ann-exmaple-feedforward.png]({{ "/assets/img/ann-exmaple-feedforward.png" | relative_url}})

A feedforward neural network can consist of three types of units:

1. **Input Units** The input units provide information from the outside world to the network and are together referred
   to as the "input layer"
2. **Hidden Units** The hidden units have no direct connection with the outside world (hence the name "hidden"). They 
   perform computations and transfer information from the input units to the output units. A collection of hidden units
   forms a "hidden layer". A feedforward network, however, will have an input layer and a single output layer only
3. **Output Units** The output units are collectively referred to as the "output layer" and are responsible for 
   computations and transferring information from the network to the outside world.

In a feedforward network, the information moves in only one direction from the input nodes, through the hidden nodes (if 
any) and to the output nodes. There are no cycles or loops in the network (this property of feedforward networks is 
different from Recurrent Neural Networks in which the connections between the nodes form a cycle).

### Commonly Used Activation Functions

Every activation function (or **non-linearity**) takes a single number and performs a certain fixed mathematical operation
on it. There are several activation functions we may employ in practice

#### Sigmoid

The sigmoid non-linearity has the mathematical form $$σ(x)=1/(1+e^{-x})$$ and is shown in the image below on the left.
It takes a real-valued number and "squashes" it into range between 0 and 1. In particular, large negative numbers become
0 and large positive numbers become 1. The sigmoid function has seen frequent use historically since it has a nice 
interpretation as the firing rate of a neuron: from not firing at all (0) to fully-saturated firing at an assumed
maximum frequency. In practice, however, the sigmoid non-linearity has recently fallen out of favor and it is rarely
ever used, because it has two major drawbacks:

1. Sigmoids saturate and kill gradients. A very undesirable property of the sigmoid neuron is that when the neuron's 
   activation saturates at either tail of 0 or 1, the gradient at these regions is almost zero. During backpropagation, 
   this (local) gradient will be multiplied to the gradient of this gate's output for the whole objective. Therefore, if 
   the local gradient is very small, it will effectively "kill" the gradient and almost no signal will flow through the 
   neuron to its weights and recursively to its data. Additionally, one must pay extra caution when initializing the 
   weights of sigmoid neurons to prevent saturation. For example, if the initial weights are too large then most neurons 
   would become saturated and the network will barely learn.
2. Sigmoid outputs are not zero-centered. This is undesirable since neurons in later layers of processing in a neural 
   network would be receiving data that is not zero-centered. This has implications on the dynamics during gradient 
   descent, because if the data coming into a neuron is always positive, then the gradient on the weights will, during 
   backpropagation, become either all be positive, or all negative (depending on the gradient of the whole expression 
   $$\mathit{f}$$ ). This could introduce undesirable zig-zagging dynamics in the gradient updates for the weights. 
   However, notice that once these gradients are added up across a batch of data the final update for the weights can
   have variable signs, somewhat mitigating this issue. Therefore, this is an inconvenience but it has less severe 
   consequences compared to the saturated activation problem above.

![Error loading ann-sigmoid-tanh.png]({{ "/assets/img/ann-sigmoid-tanh.png" | relative_url}})

#### Tanh

The tanh non-linearity is shown on the image above on the right. It squashes a real-valued number to the range `[-1, 1]`. 
Like the sigmoid neuron, its activations saturate, but unlike the sigmoid neuron its output is zero-centered. Therefore, 
in practice the _tanh non-linearity is always preferred to the sigmoid nonlinearity_. Also note that the tanh neuron is 
simply a scaled sigmoid neuron, in particular the following holds: tanh(x)=2σ(2x) - 1.

#### ReLU

![Error loading ann-relu.png]({{ "/assets/img/ann-relu.png" | relative_url}})

The Rectified Linear Unit has become very popular in the last few years. It computes the function
$$\mathit{f(x) = \max (0, x)}$$. In other words, the activation is simply thresholded at zero. There are several pros
and cons to using the ReLUs:

* ✅ It was found to greatly accelerate (a factor of 6 in
  [Krizhevsky et al](http://www.cs.toronto.edu/~fritz/absps/imagenet.pdf)) the convergence of stochastic gradient
  descent compared to the sigmoid/tanh functions. It is argued that this is due to its linear, non-saturating form.
* ✅ Compared to tanh/sigmoid neurons that involve expensive operations (exponentials, etc.), the ReLU can be
  implemented by simply thresholding a matrix of activations at zero.
* ❌ Unfortunately, ReLU units can be fragile during training and can "die". For example, a large gradient flowing
  through a ReLU neuron could cause the weights to update in such a way that the neuron will never activate on any 
  datapoint again. If this happens, then the gradient flowing through the unit will forever be zero from that point on. 
  That is, the ReLU units can irreversibly die during training since they can get knocked off the data manifold. For 
  instance, you may find that as much as 40% of your network can be "dead" (i.e. neurons that never activate across the 
  entire training dataset) if the learning rate is set too high. With a proper setting of the learning rate this is less 
  frequently an issue.

#### Leaky ReLU

Leaky ReLUs are one attempt to fix the "dying ReLU" problem. Instead of the function being zero when $$\mathit{x < 0}$$,
a leaky ReLU will instead have a small positive slope (of 0.01, or so). Some people report success with this form of 
activation function, but the results are not always consistent. The slope in the negative region can also be made into a 
parameter of each neuron, as seen in **PReLU** neurons, introduced in [Delving Deep into Rectifiers](https://arxiv.org/abs/1502.01852). However, the consistency of the benefit across tasks is unclear at the moment.

#### Maxout

Other types of units have been proposed that _do not have the functional form $$\mathit{f\ (w^Tx + b)}$$, in which a 
non-linearity is applied on the dot product between the weights and the data_. One relatively popular choice is the
Maxout neuron (introduced recently by [Goodfellow et al.](https://arxiv.org/abs/1302.4389)) that generalizes the ReLU
and its leaky version. The Maxout neuron computes the function
$$\max(\mathit{f\ (w_1^Tx + b_1)}, \mathit{f\ (w_2^Tx + b_2)})$$. Notice that both ReLU and Leaky ReLU are a special 
case of this form. The Maxout neuron therefore enjoys all the benefits of a ReLU unit (linear regime of operation, no 
saturation) and does not have its drawbacks (dying ReLU). However, unlike the ReLU neurons it doubles the number of 
parameters for every single neuron, leading to a high total number of parameters.

### Single Neuron as a Linear Classifier

TBA


Neural Network Architectures
----------------------------

**Neural Networks as neurons in graphs**. Neural Networks are modeled as collections of neurons that are connected in an 
acyclic graph. In other words, the outputs of some neurons can become inputs to other neurons. Instead of an amorphous 
blobs of connected neurons, Neural Network models are often organized into distinct layers of neurons. For regular
neural networks, the most common layer type is the **fully-connected layer** in which neurons between two adjacent
layers are fully pairwise connected, but neurons within a single layer share no connections. Below are two example
neural network topologies that use a stack of fully-connected layers:

![Error loading ann-eg-neural-network.png]({{ "/assets/img/ann-eg-neural-network.png" | relative_url}})

> 📋 **Naming conventions**
> 
> when we say "N-layer" neural network, we do not count the input layer. Therefore, a single-layer neural network
> describes a network with no hidden layers (input directly mapped to output). In that sense, you can sometimes hear 
> people say that logistic regression or SVMs are simply a special case of single-layer Neural Networks. You may also 
> hear these networks interchangeably referred to as "Artificial Neural Networks" (ANN) or "Multi-Layer Perceptrons" 
> (MLP). Many people do not like the analogies between Neural Networks and real brains and prefer to refer to neurons as 
> units.

Unlike all layers in a neural network, the output layer neurons most commonly do not have an activation function (or you
can think of them as having a linear identity activation function). This is because the last output layer is usually 
taken to represent the class scores (e.g. in classification), which are arbitrary real-valued numbers, or some kind of 
real-valued target (e.g. in regression).

The two metrics that people commonly use to measure the size of neural networks are the number of neurons, or more commonly the **number of parameters**. Working with the two example networks in the above picture:

* The first network (left) has 4 + 2 = 6 neurons (not counting the inputs), (3 x 4) + (4 x 2) = 20 weights and 4 + 2 = 6 
  biases, for a total of 26 learnable parameters.
* The second network (right) has 4 + 4 + 1 = 9 neurons, (3 x 4) + (4 x 4) + (4 x 1) = 12 + 16 + 4 = 32 weights and
  4 + 4 + 1 = 9 biases, for a total of 41 learnable parameters.

Modern [convolutional networks](#convolutional-neural-network-cnnconvnets) contain on orders of 100 million parameters 
and are usually made up of approximately 10-20 layers (hence **deep learning**). 

### Example feed-forward computation

Working with the example three-layer neural network in the diagram above

![Error loading ann-3-layer-network-eg.png]({{ "/assets/img/ann-3-layer-network-eg.png" | relative_url}})

Each neuron from the input layer outputs a number, forming a 3 x 1 matrix

$$

\vec{\mathit{x}} =
    \begin{bmatrix}
    \mathit{x_1} \\
    \mathit{x_2} \\
    \mathit{x_3}
    \end{bmatrix}

$$

When each input neron connects to another in hidden layer 1, every connection implies a **connection strength**,
$$\mathit{w_{jk}^l}$$, empirically, denoting the weights between _k_-th unit from layer $$\mathit{(l - 1)}$$ and _j_-th 
nunit from layer $$\mathit{l}$$. The strength of all connections is encoded in a 4 x 3 matrix:

$$

\mathit{W_1} = 
    \begin{bmatrix}
        \mathit{w_{11}^{\text{hidden layer 1}}} & \mathit{w_{21}^{\text{hidden layer 1}}} & \mathit{w_{31}^{\text{hidden layer 1}}} \\
        \mathit{w_{12}^{\text{hidden layer 1}}} & \mathit{w_{22}^{\text{hidden layer 1}}} & \mathit{w_{32}^{\text{hidden layer 1}}} \\
        \mathit{w_{13}^{\text{hidden layer 1}}} & \mathit{w_{23}^{\text{hidden layer 1}}} & \mathit{w_{33}^{\text{hidden layer 1}}} \\
        \mathit{w_{14}^{\text{hidden layer 1}}} & \mathit{w_{24}^{\text{hidden layer 1}}} & \mathit{w_{34}^{\text{hidden layer 1}}}
    \end{bmatrix}

$$

Every single neuron has its weights in a row of $$\mathit{W_1}$$

The bias matrix for hidden layer 1 is a 4 x 1 matrix

$$

\vec{\mathit{b_1}} = 
    \begin{bmatrix}
        \mathit{b_{11}} \\
        \mathit{b_{12}} \\
        \mathit{b_{13}}
    \end{bmatrix}

$$

According to the definition of [matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication):

![Error loading ann-matrix-multiplication.png]({{ "/assets/img/ann-matrix-multiplication.png" | relative_url}})

`np.dot(W1,x)` evaluates the activations of all neurons in that layer, i.e. \[4 x 3\] x \[3 x 1\] => \[4 x 1\], which
forms the dimensionality of input to the next hidden layer 2:

$$

\mathit{W_1}\vec{\mathit{x}} =

\begin{bmatrix}
\mathit{w_{11}^{\text{hidden layer 1}}} & \mathit{w_{21}^{\text{hidden layer 1}}} & \mathit{w_{31}^{\text{hidden layer 1}}} \\
\mathit{w_{12}^{\text{hidden layer 1}}} & \mathit{w_{22}^{\text{hidden layer 1}}} & \mathit{w_{32}^{\text{hidden layer 1}}} \\
\mathit{w_{13}^{\text{hidden layer 1}}} & \mathit{w_{23}^{\text{hidden layer 1}}} & \mathit{w_{33}^{\text{hidden layer 1}}} \\
\mathit{w_{14}^{\text{hidden layer 1}}} & \mathit{w_{24}^{\text{hidden layer 1}}} & \mathit{w_{34}^{\text{hidden layer 1}}}
\end{bmatrix}
\begin{bmatrix}
\mathit{x_1} \\
\mathit{x_2} \\
\mathit{x_3}
\end{bmatrix} =

\begin{bmatrix}
\mathit{w_{11}^{\text{hidden layer 1}}}\mathit{x_1} + \mathit{w_{21}^{\text{hidden layer 1}}}\mathit{x_2} + \mathit{w_{31}^{\text{hidden layer 1}}}\mathit{x_3} \\
\mathit{w_{12}^{\text{hidden layer 1}}}\mathit{x_1} + \mathit{w_{22}^{\text{hidden layer 1}}}\mathit{x_2} + \mathit{w_{32}^{\text{hidden layer 1}}}\mathit{x_3} \\
\mathit{w_{13}^{\text{hidden layer 1}}}\mathit{x_1} + \mathit{w_{23}^{\text{hidden layer 1}}}\mathit{x_2} + \mathit{w_{33}^{\text{hidden layer 1}}}\mathit{x_3} \\
\mathit{w_{14}^{\text{hidden layer 1}}}\mathit{x_1} + \mathit{w_{24}^{\text{hidden layer 1}}}\mathit{x_2} + \mathit{w_{34}^{\text{hidden layer 1}}}\mathit{x_3}
\end{bmatrix}

$$


Similarly, $$\mathit{W_2}$$ would be a 4 x 4 matrix that stores the connections of the second hidden layer, and 
$$\mathit{W_3}$$ a 1 x 4 matrix for the last (output) layer. The full forward pass of this 3-layer neural network is
then simply three matrix multiplications, interwoven with the application of the activation function:

```python
# forward-pass of a 3-layer neural network:
f = lambda x: 1.0/(1.0 + np.exp(-x)) # activation function (use sigmoid)
x = np.random.randn(3, 1) # random input vector of three numbers (3 rows x 1 col)
h1 = f(np.dot(W1, x) + b1) # calculate first hidden layer activations (4 x 1)
h2 = f(np.dot(W2, h1) + b2) # calculate second hidden layer activations (4 x 1)
out = np.dot(W3, h2) + b3 # output neuron (1x1)
```

> Note that the forward pass of a fully-connected layer corresponds to one matrix multiplication followed by a bias 
> offset and an activation function.

As we have seen, _feed-forward computation is essentially repeated matrix multiplications interwoven with activation 
function_. One of the primary reasons that neural networks are organized into layers is that this structure makes it very 
simple and efficient to evaluate neural networks using matrix operations.

### Representational Power

One way to look at neural networks with fully-connected layers is that they define a family of functions that are 
parameterized by the weights of the network. A natural question that arises is: What is the representational power of 
this family of functions? In particular, are there functions that cannot be modeled with a Neural Network?

It turns out that neural networks with at least one hidden layer are universal approximators. That is, it can be shown 
(Approximation by Superpositions of Sigmoidal Function, 1989, or this
[intuitive explanation](http://neuralnetworksanddeeplearning.com/chap4.html) from Michael Nielsen) that given any 
continuous function $$\mathit{f\ (x)}$$ and some ϵ > 0, there exists a neural network $$\mathit{g(x)}$$ with one hidden 
layer (with a reasonable choice of non-linearity, e.g. sigmoid) such that ∀x,∣f(x) - g(x)∣ < ϵ. In other words, the 
neural network can approximate any continuous function.

As an aside, in practice it is often the case that 3-layer neural networks will outperform 2-layer nets, but going even 
deeper (4,5,6-layer) rarely helps much more. This is in stark contrast to Convolutional Networks, where depth has been 
found to be an extremely important component for a good recognition system (e.g. on order of 10 learnable layers). One 
argument for this observation is that images contain hierarchical structure (e.g. faces are made up of eyes, which are 
made up of edges, etc.), so several layers of processing make intuitive sense for this data domain.

The full story is, of course, much more involved and a topic of much recent research. If you are interested in these 
topics we recommend for further reading:

* [Deep Learning](http://www.deeplearningbook.org/) book in press by Bengio, Goodfellow, Courville, in particular
  [Chapter 6.4](http://www.deeplearningbook.org/contents/mlp.html).
* [Do Deep Nets Really Need to be Deep?](http://arxiv.org/abs/1312.6184)
* [FitNets: Hints for Thin Deep Nets](http://arxiv.org/abs/1412.6550)

### Setting Number of Layers and Their Sizes

How do we decide on what architecture to use when faced with a practical problem? Should we use no hidden layers? One 
hidden layer? Two hidden layers? How large should each layer be? First, note that as we increase the size and number of 
layers in a Neural Network, the capacity of the network increases. That is, the space of representable functions grows 
since the neurons can collaborate to express many different functions. For example, suppose we had a binary 
classification problem in two dimensions. We could train three separate neural networks, each with one hidden layer of 
some size and obtain the following classifiers:

![Error loading ann-tunning-layer-num-and-size.png]({{ "/assets/img/ann-tunning-layer-num-and-size.png" | relative_url}})

In the diagram above, we can see that neural networks with more neurons can express more complicated functions. However, 
this is both a blessing (since we can learn to classify more complicated data) and a curse (since it is easier to
overfit the training data). **Overfitting** occurs when a model with high capacity fits the noise in the data instead of 
the (assumed) underlying relationship. For example, the model with 20 hidden neurons fits all the training data but at 
the cost of segmenting the space into many disjoint red and green decision regions. The model with 3 hidden neurons only 
has the representational power to classify the data in broad strokes. It models the data as two blobs and interprets the 
few red points inside the green cluster as outliers (noise). In practice, this could lead to better generalization on
the test set.

The subtle reason behind this is that smaller networks are harder to train with local methods such as Gradient Descent: 
It's clear that their loss functions have relatively few local minima, but it turns out that many of these minima are 
easier to converge to, and that they are bad (i.e. with high loss). Conversely, bigger neural networks contain 
significantly more local minima, but these minima turn out to be much better in terms of their actual loss. Since Neural 
Networks are non-convex, it is hard to study these properties mathematically, but some attempts to understand these 
objective functions have been made, e.g. in a recent paper
[The Loss Surfaces of Multilayer Networks](http://arxiv.org/abs/1412.0233). In practice, what you find is that if you 
train a small network the final loss can display a good amount of variance - in some cases you get lucky and converge to 
a good place but in some cases you get trapped in one of the bad minima. On the other hand, if you train a large network 
you’ll start to find many different solutions, but the variance in the final achieved loss will be much smaller. In
other words, all solutions are about equally as good, and rely less on the luck of random initialization.

To reiterate, the regularization strength is the preferred way to control the overfitting of a neural network. We can 
look at the results achieved by three different settings:

![Error loading ann-regularization-strength.png]({{ "/assets/img/ann-regularization-strength.png" | relative_url}})

The effects of regularization strength: Each neural network above has 20 hidden neurons, but changing the regularization 
strength makes its final decision regions smoother with a higher regularization. You can play with these examples in
this [ConvNetsJS demo](http://cs.stanford.edu/people/karpathy/convnetjs/demo/classify2d.html).

The takeaway is that you should not be using smaller networks because you are afraid of overfitting. Instead, you should 
use as big of a neural network as your computational budget allows, and use other regularization techniques to control 
overfitting.


Data Preprocessing
------------------

There are three common forms of data preprocessing a data matrix $$\mathit{X}$$, where we will assume that
$$\mathit{X}$$ is of size $$\mathit{[N \times D]}$$ ($$\mathit{N}$$ is the number of data, $$\mathit{D}$$ is their 
dimensionality).

### 1. Mean Subtraction

Mean subtraction is the most common form of preprocessing. It involves subtracting the mean across every individual 
feature in the data, and has the geometric interpretation of _centering the cloud of data around the origin along every 
dimension_. In numpy, this operation would be implemented as: `X -= np.mean(X, axis = 0)`. With images specifically, for 
convenience it can be common to subtract a single value from all pixels (e.g. `X -= np.mean(X)`), or to do so separately 
across the three color channels.

![Error loading ann-preprocessing-mean-norm.png]({{ "/assets/img/ann-preprocessing-mean-norm.png" | relative_url}})

### 2. Normalization

Normalization refers to normalizing the data dimensions so that they are of approximately the same scale. There are two 
common normalization approaches. One is to divide each dimension by its standard deviation once it has been 
zero-centered: (`X /= np.std(X, axis = 0)`). Another form of this preprocessing normalizes each dimension so that the
min and max along the dimension is -1 and 1 respectively. It only makes sense to apply this preprocessing if you have a 
reason to believe that different input features have different scales (or units), but they should be of approximately 
equal importance to the learning algorithm. In case of images, the relative scales of pixels are already approximately 
equal (and in range from 0 to 255), so it is not strictly necessary to perform this additional preprocessing step.

### 3. PCA & Whitening

In this process, the data is first centered as described above. Then, we can compute the
[covariance](https://en.wikipedia.org/wiki/Covariance) matrix which tells us about the correlation of the data inside
matrix:

```python
# Assume input data matrix X of size [N x D]
X -= np.mean(X, axis = 0) # zero-center the data (important)
cov = np.dot(X.T, X) / X.shape[0] # get the data covariance matrix
```

The $$\mathit{(i, j)}$$ element of the covariance matrix contains the covariance between _i_-th and _j_-th dimension of 
the data. In particular, the diagonal of this matrix contains the variances. Furthermore, the covariance matrix is 
symmetric and
[positive semi-definite](http://en.wikipedia.org/wiki/Positive-definite_matrix#Negative-definite.2C_semidefinite_and_indefinite_matrices).

Then we compute the
[SVD factorization](https://qubitpi.github.io/jersey-guide/2022/08/11/linear-algebra.html#singular-value-decomposition)
of the covariance matrix:

```python
U,S,V = np.linalg.svd(cov)
```

where the columns of `U` are the eigenvectors and `S` is a 1-D array of the singular values. To decorrelate the data, we 
project the original (but zero-centered) data into the eigenbasis:

```python
Xrot = np.dot(X, U) # decorrelate the data
```

Notice that the columns of `U` are a set of orthonormal vectors (norm of 1, and orthogonal to each other), so they can be regarded as basis vectors. The projection therefore corresponds to a rotation of the data in `X` so that the new axes are the eigenvectors. If we were to compute the covariance matrix of `Xrot`, we would see that it is now diagonal. A nice property of `np.linalg.svd` is that in its returned value `U`, the eigenvector columns are sorted by their eigenvalues. We can use this to reduce the dimensionality of the data by only using the top few eigenvectors, and discarding the dimensions along which the data has no variance. This is also sometimes referred to as Principal Component Analysis (PCA) dimensionality reduction:

```python
Xrot_reduced = np.dot(X, U[:,:100]) # Xrot_reduced becomes [N x 100]
```

After this operation, we would have reduced the original dataset of size `[N x D]` to one of size `[N x 100]`, keeping 
the 100 dimensions of the data that contain the most variance. It is very often the case that you can get very good 
performance by training linear classifiers or neural networks on the PCA-reduced datasets, _obtaining savings in both 
space and time_.

![Error loading ann-pca-and-whitening.png]({{ "/assets/img/ann-pca-and-whitening.png" | relative_url}})

The last transformation you may see in practice is **whitening**. The whitening operation takes the data in the 
eigenbasis and divides every dimension by the eigenvalue to normalize the scale. The geometric interpretation of this 
transformation is that if the input data is a multivariable gaussian, then the whitened data will be a gaussian with
zero mean and identity covariance matrix. This step would take the form:

```python
# whiten the data:
# divide by the eigenvalues (which are square roots of the singular values)
Xwhite = Xrot / np.sqrt(S + 1e-5)
```

> ⚠️ Note that we are adding 1e-5 (or a small constant) to prevent division by zero. One weakness of this transformation 
> is that it can greatly exaggerate the noise in the data, since it stretches all dimensions (including the irrelevant 
> dimensions of tiny variance that are mostly noise) to be of equal size in the input. This can in practice be mitigated 
> by stronger smoothing (i.e. increasing 1e-5 to be a larger number).

We can also try to visualize these transformations with CIFAR-10 images. The training set of CIFAR-10 is of size
50,000 $$\times$$ 3072, where every image is stretched out into a 3072-dimensional row vector. We can then compute the 
`[3072 x 3072]` covariance matrix and compute its SVD decomposition (which can be relatively expensive). What do the 
computed eigenvectors look like visually? An image might help:

![Error loading ann-preprocessing-visual.png]({{ "/assets/img/ann-preprocessing-visual.png" | relative_url}})

> ⚠️ An important point to make about the preprocessing is that any preprocessing statistics (e.g. the data mean) must 
> only be computed on the training data, and then applied to the validation / test data. E.g. computing the mean and 
> subtracting it from every image across the entire dataset and then splitting the data into train/val/test splits would 
> be a mistake. Instead, the mean must be computed only over the training data and then subtracted equally from all 
> splits (train/val/test).


Weight Initialization
---------------------

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

### Sparse Initialization.

Another way to address the uncalibrated variances problem is to set all weight matrices to zero, but to break symmetry 
every neuron is randomly connected (with weights sampled from a small gaussian as above) to a fixed number of neurons 
below it. A typical number of neurons to connect to may be as small as 10.

### Initializing the Biases.

It is possible and common to initialize the biases to be zero, since the asymmetry breaking is provided by the small 
random numbers in the weights. For ReLU non-linearities, some people like to use small constant value such as 0.01 for 
all biases because this ensures that all ReLU units fire in the beginning and therefore obtain and propagate some 
gradient. However, it is not clear if this provides a consistent improvement (in fact some results seem to indicate that 
this performs worse) and it is more common to simply use 0 bias initialization.

In practice, the recommendation is to use ReLU units and use the `w = np.random.randn(n) * sqrt(2.0/n)`, as discussed in 
He et al..

### Batch Normalization

A recently developed technique by Ioffe and Szegedy called [Batch Normalization](http://arxiv.org/abs/1502.03167) 
alleviates a lot of headaches with properly initializing neural networks by explicitly forcing the activations
throughout a network to take on a unit gaussian distribution at the beginning of the training. The core observation is 
that this is possible because normalization is a simple differentiable operation. In the implementation, applying this 
technique usually amounts to insert the BatchNorm layer immediately after fully connected layers (or convolutional
layers), and before non-linearities. **It has become a very common practice to use Batch Normalization in neural 
networks**. In practice networks that use Batch Normalization are significantly more robust to bad initialization. 
Additionally, batch normalization can be interpreted as doing preprocessing at every layer of the network, but integrated
into the network itself in a differentiable manner. Neat!


Regularization
--------------

There are several ways of controlling the capacity of Neural Networks to prevent overfitting:

### L2 Regularization

L2 regularization is perhaps the most common form of regularization. It can be implemented by penalizing the squared 
magnitude of all parameters directly in the objective. That is, for every weight $$\mathit{w}$$ in the network, we add
the term $$\mathit{\frac{1}{2} \lambda w^2}$$ to the objective, where $$\lambda$$ is the regularization strength. It is 
common to see the factor of $$\frac{1}{2}$$ in front because then the gradient of this term with respect to the
parameter $$\mathit{w}$$ is simply $$\mathit{\lambda w}$$ instead of $$\mathit{2 \lambda w}$$. The L2 regularization has 
the intuitive interpretation of heavily penalizing peaky weight vectors and preferring diffuse weight vectors. Due to 
multiplicative interactions between weights and inputs this has the appealing property of encouraging the network to use 
all of its inputs a little rather than some of its inputs a lot. Lastly, notice that during gradient descent parameter 
update, using the L2 regularization ultimately means that every weight is decayed linearly: `W += -lambda * W` towards 
zero.

### L1 Regularization

L1 regularization is another relatively common form of regularization, where for each weight $$\mathit{w}$$ we add the 
term $$\mathit{ \lambda \mid w \mid }$$ to the objective. It is possible to combine the L1 regularization with the L2 
regularization: $$\mathit{ \lambda_1 \mid w \mid + \lambda_2 w^2 }$$ (this is called **Elastic Net Regularization**).
The L1 regularization has the intriguing property that it leads the weight vectors to become sparse during optimization 
(i.e. very close to exactly zero). In other words, neurons with L1 regularization end up using only a sparse subset of 
their most important inputs and become nearly invariant to the "noisy" inputs. In comparison, final weight vectors from 
L2 regularization are usually diffuse, small numbers. In practice, if you are not concerned with explicit feature 
selection, L2 regularization can be expected to give superior performance over L1.

### Max Norm Constraints

Another form of regularization is to enforce an absolute upper bound on the magnitude of the weight vector for every 
neuron and use projected gradient descent to enforce the constraint. In practice, this corresponds to performing the 
parameter update as normal, and then enforcing the constraint by clamping the weight vector $$\mathit{\vec{w}}$$ of
every neuron to satisfy $$\mathit{\Vert \vec{w} \Vert_2 < c}$$. Typical values of $$\mathit{c}$$ are on orders of 3
or 4. Some people report improvements when using this form of regularization. One of its appealing properties is that 
network cannot "explode" even when the learning rates are set too high because the updates are always bounded.

### Dropout

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

### Theme of Noise in Forward Pass.

Dropout falls into a more general category of methods that introduce stochastic behavior in the forward pass of the 
network. During testing, the noise is marginalized over analytically (as is the case with dropout when multiplying by 
$$\mathit{p}$$), or numerically (e.g. via sampling, by performing several forward passes with different random decisions 
and then averaging over them). An example of other research in this direction includes **DropConnect**, where a random 
set of weights is instead set to zero during forward pass. As foreshadowing, Convolutional Neural Networks also take 
advantage of this theme with methods such as stochastic pooling, fractional pooling, and data augmentation. 

### Bias Regularization.

It is not common to regularize the bias parameters because they do not interact with the data through multiplicative 
interactions, and therefore do not have the interpretation of controlling the influence of a data dimension on the final 
objective. However, in practical applications (and with proper data preprocessing) regularizing the bias rarely leads to 
significantly worse performance. This is likely because there are very few bias terms compared to all the weights, so the
classifier can "afford to" use the biases if it needs them to obtain a better data loss.

### Per-layer Regularization.

It is not very common to regularize different layers to different amounts (except perhaps the output layer). Relatively 
few results regarding this idea have been published in the literature.

In practice, it is most common to use a single, global L2 regularization strength that is cross-validated. It is also 
common to combine this with dropout applied after all layers. The value of $$\mathit{p = 0.5}$$ is a reasonable default, 
but this can be tuned on validation data.


Perceptrons
-----------

> **[Perceptron is a neural network with a single neuron](https://stats.stackexchange.com/a/419776/365124)**.

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

### The Perceptron Training Rule

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

$$ \mathit{E}(\vec{\mathit{w}}) \equiv \frac{1}{2} \sum_{\mathit{d} \in \mathit{D}} (\mathit{t_d} - \mathit{o_d})^2 $$

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

#### Variations

##### Updating Weight with Momentum

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

#### Learning in Arbitrary Acyclic Networks

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

##### Derivation of the Backpropagation Rule for Arbitrary Acyclic Networks

### Remarks of the Backpropagation Algorithm

#### Convergence and Local Minima

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


ANN with TensorFlow
-------------------


Convolutional Neural Network (CNN/ConvNets)
-------------------------------------------

ConvNet architectures make the explicit assumption that the inputs are images, which allows us to encode certain 
properties into the architecture. These then make the forward function more efficient to implement and vastly reduce the 
amount of parameters in the network.

A typical CNN looks like this

![Error loading cnn-simple-example.png]({{ "/assets/img/cnn-simple-example.png" | relative_url}})

The structure classifies an input image into four categories: dog, cat, boat or bird. For example, on receiving a boat 
image as input, the network correctly assigns the highest probability for boat (0.94) among all four categories.

There are four main operations in the ConvNet shown in figure above, which we will being discussing next:

1. [Convolution](#the-convolution-step)
2. Non Linearity ([ReLU](#non-linearity-relu)
3. [Pooling](#the-pooling-step) or Sub Sampling
4. Classification ([Fully Connected Layer](#fully-connected-layer))

These operations are the basic building blocks of _every_ Convolutional Neural Network, so understanding how these work
is an important step to developing a sound understanding of ConvNets. We will try to understand the intuition behind
each of these operations below.

### An Image is a Matrix of Pixel Values

Essentially, every image can be represented as a matrix of pixel values:

![Error loading cnn-every-img-is-a-matrix-of-px-values.gif]({{ "/assets/img/cnn-every-img-is-a-matrix-of-px-values.gif" | relative_url}})

**[Channel](https://en.wikipedia.org/wiki/Channel_(digital_image))** is a conventional term used to refer to a certain 
component of an image. An image from a standard digital camera will have three channels - red, green and blue - you can 
imagine those as three 2D-matrices stacked over each other (one for each color), each having pixel values in the range 0 
to 255.

A **[grayscale](https://en.wikipedia.org/wiki/Grayscale) image**, on the other hand, has just one channel. For now, we 
will only consider grayscale images, so we will have a single 2D matrix representing an image. The value of each pixel
in the matrix will range from 0 to 255, with zero indicating black and 255 indicating white.

### The Convolution Step

ConvNets derive their name from the **[convolution](https://en.wikipedia.org/wiki/Convolution) operator**. **The primary 
purpose of convolution in case of a ConvNet is to extract features from the input image**. Convolution preserves the 
spatial relationship between pixels by learning image features using small squares of input data. We will not go into the 
mathematical details of convolution here, but will try to understand how it works over images.

As we discussed above, every image can be considered as a matrix of pixel values. Consider a 5 x 5 image whose pixel 
values are only 0 and 1 (note that for a grayscale image, pixel values range from 0 to 255, the green matrix below is a 
special case where pixel values are only 0 and 1):

![Error loading cnn-example-image.png]({{ "/assets/img/cnn-example-image.png" | relative_url}})

Also, consider another 3 x 3 matrix as shown below:

![Error loading cnn-3-by-3-matrix.png]({{ "/assets/img/cnn-3-by-3-matrix.png" | relative_url}})

Then, the convolution of the 5 x 5 image and the 3 x 3 matrix can be computed as shown in the animation in figure below:

![Error loading cnn-convolution.gif]({{ "/assets/img/cnn-convolution.gif" | relative_url}})

Take a moment to understand how the computation above is being done. We slide the orange matrix over our original image 
(green) by 1 pixel (also called "stride") and for every position, we compute element wise multiplication (between the
two matrices) and add the multiplication outputs to get the final integer which forms a single element of the output 
matrix (pink). Note that the 3×3 matrix "sees" only a part of the input image in each stride.

In CNN terminology, the 3×3 matrix is called a **filter** or **kernel** or **feature detector** and the matrix formed by 
sliding the filter over the image and computing the dot product is called the **Convolved Feature** or **Activation
Map** or the **Feature Map**. It is important to note that filters acts as feature detectors from the original input
image.

It is evident from the animation above that different values of the filter matrix will produce different feature maps
for the same input image. As an example, consider the following input image:

![Error loading cnn-example-base-image.png]({{ "/assets/img/cnn-example-base-image.png" | relative_url}})

In the table below, we can see the effects of convolution of the above image with different filters. As shown, we can 
perform operations such as Edge Detection, Sharpen and Blur just by changing the numeric values of our filter matrix 
before the [convolution operation](https://en.wikipedia.org/wiki/Kernel_(image_processing)) - this means that
**different filters can detect different features from an image**

![Error loading cnn-different-filter.png]({{ "/assets/img/cnn-different-filter.png" | relative_url}})

Another good way to understand the convolution operation is by looking at the animation below:

![Error loading cnn-city-example.gif]({{ "/assets/img/cnn-city-example.gif" | relative_url}})

A filter (with red outline) slides over the input image (convolution operation) to produce a feature map. The
convolution of another filter (with the green outline), over the same image gives a different feature map as shown. It
is important to note that the convolution operation captures the local dependencies in the original image. Also notice
how these two different filters generate different feature maps from the same original image. Remember that the image
and the two filters above are just numeric matrices as we have discussed above.

In practice, a **CNN learns the values of these filters on its own during the training process** (although we still need 
to specify parameters such as number of filters, filter size, architecture of the network etc. before the training 
process). The more number of filters we have, the more image features get extracted and the better our network becomes
at recognizing patterns in unseen images.

The size of the feature map (convolved feature) is controlled by three parameters what we need to decide before the 
convolution step is performed:

* **Depth**: Depth corresponds to the number of filters we use for the convolution operation. In the network shown in Figure 7, we are performing convolution of the original boat image using three distinct filters, thus producing three different feature maps as shown. You can think of these three feature maps as stacked 2d matrices, so, the ‘depth’ of the feature map would be three.

  ![Error loading cnn-depth-example.png]({{ "/assets/img/cnn-depth-example.png" | relative_url}})

* **Stride**: Stride is the number of pixels by which we slide our filter matrix over the input matrix. When the stride
  is 1 then we move the filters one pixel at a time. When the stride is 2, then the filters jump 2 pixels at a time as
  we slide them around. Having a larger stride will produce smaller feature maps.
* **Zero-padding**: Sometimes, it is convenient to pad the input matrix with zeros around the border, so that we can
  apply the filter to bordering elements of our input image matrix. A nice feature of zero padding is that it allows us
  to control the size of the feature maps. Adding zero-padding is also called **wide convolution**, and not using
  zero-padding would be a **narrow convolution**.

### Non Linearity (ReLU)

An additional operation called **ReLU** is used after every convolution operation . ReLU stands for **Rectified Linear 
Unit** and is a _non-linear operation_. Its output is given by:

![Error loading cnn-ReLU.png]({{ "/assets/img/cnn-ReLU.png" | relative_url}})

ReLU is an element wise operation (applied per pixel) and replaces all negative pixel values in the feature map by zero. 
**The purpose of ReLU is to introduce non-linearity** in ConvNet, since most of the real-world data we would want our 
ConvNet to learn would be non-linear and convolution, however, is a linear operation - element wise matrix
multiplication and addition, so we account for non-linearity by introducing a non-linear function like ReLU).

The ReLU operation can be visualized the picture below. It shows the ReLU operation applied to one feature map. The
output feature map here is also referred to as the **rectified feature map**.

![Error loading cnn-ReLu-example.png]({{ "/assets/img/cnn-ReLu-example.png" | relative_url}})

Other non linear functions such as **tanh** or **sigmoid** can also be used instead of ReLU, but ReLU has been found to 
perform better in most situations.

### The Pooling Step

**Spatial Pooling** (also called **subsampling** or **downsampling**) reduces the dimensionality of each feature map but 
retains the most important information. Spatial Pooling can be of different types: Max, Average, Sum etc.

In case of Max Pooling, we define a spatial neighborhood (for example, a 2×2 window) and take the largest element from
the rectified feature map within that window. Instead of taking the largest element we could also take the average 
(Average Pooling) or sum of all elements in that window. In practice, Max Pooling has been shown to work better.

The figure below shows an example of Max Pooling operation on a rectified feature map (obtained after convolution + ReLU 
operation) by using a 2×2 window

![Error loading cnn-pooling-example.png]({{ "/assets/img/cnn-pooling-example.png" | relative_url}})

We slide our 2 x 2 window by 2 strides and take the maximum value in each region, which reduces the dimensionality of
our feature map.

In the network shown below, pooling operation is applied separately to each feature map (notice that we get three output 
maps from three input maps this time).

![Error loading cnn-group-pooling-example.png]({{ "/assets/img/cnn-group-pooling-example.png" | relative_url}})

This picture shows the visual effect of pooling on a rectified feature map:

![Error loading cnn-pooling-visual-example.png]({{ "/assets/img/cnn-pooling-visual-example.png" | relative_url}})

**The purpose of pooling is to progressively reduce the spatial size of the input representation**. In particular,
pooling

* makes the input representations (feature dimension) smaller and more manageable
* reduces the number of parameters and computations in the network, therefore, controlling overfitting
* makes the network invariant to small transformations, distortions and translations in the input image (a small 
  distortion in input will not change the output of pooling - since we take the maximum / average value in a local 
  neighborhood).
* helps us arrive at an almost scale invariant representation of our image (the exact term is "equivariant"). This is
  very powerful since we can detect objects in an image no matter where they are located

### Fully Connected Layer

The Fully Connected layer is a traditional Multi Layer Perceptron that uses a softmax activation function in the output 
layer (other classifiers like SVM can also be used, but will stick to softmax for now). The term "Fully Connected"
implies the fact that every neuron in the previous layer is connected to every neuron on the next layer.

The output from the convolutional and pooling layers represent high-level features of the input image. **The purpose of 
the Fully Connected layer is to use these features for classifying the input image into various classes based on the 
training dataset**. For example, the image classification task we set out to perform has four possible outputs as shown
in figure below:

![Error loading cnn-fully-connected-layer-examp.png]({{ "/assets/img/cnn-fully-connected-layer-examp.png" | relative_url}})

Apart from classification, adding a fully-connected layer is also a (usually) cheap way of learning non-linear 
combinations of these features. Most of the features from convolutional and pooling layers may be good for the
classification task, but combinations of those features might be even better.

The sum of output probabilities from the fully connected layer is 1. This is ensured by using the Softmax as the 
activation function.

### Putting It All together - Training Using Backpropagation

As discussed above, **the convolution + pooling layers act as feature extractors from the input image while fully
connected layer acts as a classifier**.

To start with Backpropagation algorithm, let's take an image of boat as an example, the target probability is 1 for Boat 
class and 0 for other three classes, i.e.

* Input Image = Boat
* Target Vector = [0, 0, 1, 0]

![Error loading cnn-boat-classification-example.png]({{ "/assets/img/cnn-boat-classification-example.png" | relative_url}})

The overall training process of the convolution neural network may be summarized **empirically** as below:

> 1. Initialize all filters and parameters/weights with random values
> 2. The network takes a training image as input, goes through the forward propagation step (convolution, ReLU, and 
>    pooling operations along with forward propagation in the fully connected layer) and finds the output probabilities 
>    for each class.
>    * Let's say the output probabilities for the boat image above are `[0.2, 0.4, 0.1, 0.3]`
>    * Since weights are randomly assigned for the first training example, output probabilities are also random.
> 3. Calculate the total error at the output layer (summation over all 4 classes): Total Error =
>    ∑ ½ (target probability - output probability)²
> 4. Use Backpropagation to calculate the gradients of the error with respect to all weights in the network and use 
>    gradient descent to update all filter values/weights and parameter values to minimize the output error
>    * The weights are adjusted in proportion to their contribution to the total error.
>    * When the same image is input again, output probabilities becomes, for example, `[0.1, 0.1, 0.7, 0.1]`, which is 
>      closer to the target vector `[0, 0, 1, 0]`.
>    * This means that the network has _learnt_ to classify this particular image correctly by adjusting its 
>      weights/filters such that the output error is reduced
>    * Parameters like number of filters, filter sizes, architecture of the network etc. have all been _fixed_ and do
>      not change during training process; only the values of the filter matrix and connection weights get updated.
> 5. Repeat steps 2-4 with all images in the training set.

The steps above train the ConvNet - this essentially means that all the weights and parameters of the ConvNet have now 
been optimized to correctly classify images from the training set.

When a new (unseen) image is input into the ConvNet, the network would go through the forward propagation step and
output a probability for each class (for a new image, the output probabilities are calculated using the weights which
have been optimized to correctly classify all the previous training examples). If our training set is large enough, the 
network will (hopefully) generalize well to new images and classify them into correct categories.

**In general, the more convolution steps we have, the more complicated features our network will be able to learn to 
recognize**. For example, in image classification a ConvNet may learn to detect edges from raw pixels in the first
layer, then use the edges to detect simple shapes in the second layer, and then use these shapes to find higher-level 
features, such as facial shapes in higher layers:

![Error loading cnn-facial-detection-example.png]({{ "/assets/img/cnn-facial-detection-example.png" | relative_url}})

### Visualizing Convolutional Neural Networks

Let's take a look at how CNN works for an input of "8". Please note that the following visualizations do not show the
ReLU operation.

![Error loading cnn-8-visualization-example.png]({{ "/assets/img/cnn-8-visualization-example.png" | relative_url}})

The input image contains 1024 pixels (32x32 image) and the first convolution layer is formed by convolution of six 
unique 5x5 (stride 1) filters with the input image (Using six different filters produces a feature map of depth six).

Convolutional layer 1 is followed by pooling layer 1 that performs 2×2 max pooling (with stride 2) over the six feature 
maps from convolution layer 1.

Pooling layer 1 is followed by 16 5×5 (stride 1) convolutional filters, which are next followed by pooling layer 2 with
2×2 max pooling (with stride 2). 

We then have three fully-connected layers:

1. 120 neurons in the first layer
2. 100 neurons in the second layer
3. 10 neurons in the third layer (the **output layer**) corresponding to the 10 digits

### Other ConvNet Architectures

The structure presented above is the famous **LeNet Architecture** (1990s), which is one of the very first convolutional 
neural networks that propelled the field of Deep Learning. This pioneering
[work by Yann LeCun](http://yann.lecun.com/exdb/publis/pdf/lecun-01a.pdf) was used mainly for character recognition
tasks such as reading zip codes, digits, etc.

There have been several new architectures proposed in the recent years which are improvements over the LeNet, but they
all use the main concepts.

* **AlexNet (2012)** In 2012, Alex Krizhevsky (and others) released
  [AlexNet]({{ "/assets/pdf/AlexNet.pdf" | relative_url}}) which was a deeper and much wider version of the LeNet. It
  was a significant breakthrough with respect to the previous approaches and the current widespread application of CNNs 
  can be attributed to this work.
* [**ZF Net (2013)**](https://arxiv.org/abs/1311.2901) An improvement on AlexNet by tweaking the architecture 
  hyperparameters.
* [**GoogLeNet (2014)**](https://arxiv.org/abs/1409.4842) A Convolutional Network from Szegedy et al. at Google. Its
  main contribution was the development of an Inception Module that dramatically reduced the number of parameters in the 
  network (4M, compared to AlexNet with 60M).
* [**VGGNet (2014)**](http://www.robots.ox.ac.uk/~vgg/research/very_deep/)
* [**ResNets (2015)**](https://arxiv.org/abs/1512.03385) The state of the art CNN models and are the default choice for 
  using ConvNets in practice (as of May 2016).
* [**DenseNet (August 2016)**](https://arxiv.org/abs/1608.06993) The DenseNet has been shown to obtain significant improvements over previous state-of-the-art architectures on five highly competitive object recognition benchmark tasks. Check out the Torch implementation [here](https://github.com/liuzhuang13/DenseNet).


Additional Resources
--------------------

* [deeplearning.net tutorial](http://www.deeplearning.net/tutorial/mlp.html) with Theano
* [ConvNetJS](http://cs.stanford.edu/people/karpathy/convnetjs/) demos for intuitions
* [Michael Nielsen's tutorials](http://neuralnetworksanddeeplearning.com/chap1.html)

### Free Datasets

* [CIFAR-10](https://www.cs.toronto.edu/~kriz/cifar.html)
