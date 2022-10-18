---
layout: post
title: Machine Learning - 
tags: [Machine Learning, Artificial Neural Networks, ANN, CNN, ConvNet, Convolutional Neural Networks, Neural Networks]
category: FINALIZED
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/15-cover.png"
thumbnail: "assets/img/post-cover/15-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---



<!--more-->

* TOC
{:toc}
























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
