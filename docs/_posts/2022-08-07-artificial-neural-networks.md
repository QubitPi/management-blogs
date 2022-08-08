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

Perceptrons
-----------

One type of ANN system is based on a unit called a perceptron, illustrated in figure below:

![Error loading ann-a-perceptron.png]({{ "/assets/img/ann-a-perceptron.png" | relative_url}})

A perceptron takes a vector of real-valued inputs, calculates a linear combination of these inputs, then outputs a 1 if
the result is greater than some threshold and -1 otherwise. More precisely, given inputs $$x_1$$ through $$x_n$$, the
output $$o(x_1, ... , x_n)$$ computed by the perceptron is

$$

\begin{cases}
c \colon \{1, \dots, n\} \rightarrow \{1, \dots, n\} \text{ such that}\\      
c(a_i) = a_{i+1}  \text{ for $1\le i<l$}\\
c(a_l) = a_1
\end{cases}

$$

where each $$w_i$$ is a real-valued constant, or weight, that determines the contribution of input $$x_i$$ to the 
perceptron output. Notice the quantity ($$-w_O$$) is a threshold that the weighted combination of inputs
$$w_1x_1 + ... + w_nx_n$$ must surpass in order for the perceptron to output a 1.

