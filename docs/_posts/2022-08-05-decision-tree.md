---
layout: post
title: Machine Learning - Decision Tree Learning
tags: [Machine Learning, Decision Tree]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/14-cover.png"
thumbnail: "assets/img/post-cover/14-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Decision tree learning is one of the most widely used and practical methods for inductive inference. It is a method for 
approximating discrete-valued functions that is robust to noisy data and capable of learning disjunctive expressions

<!--more-->

* TOC
{:toc}

Decision tree learning is a method for approximating discrete-valued target functions, in which the learned function is 
represented by a decision tree. Learned trees can also be re-represented as sets of if-then rules to improve human 
readability. These learning methods are among the most popular of inductive inference algorithms and have been
successfully applied to a broad range of tasks from learning to diagnose medical cases to learning to assess credit risk
of loan applicants.

Decision tree learning is generally best suited to problems (**Classification Problems**) with the following
characteristics

* Instances are represented by attribute-value pairs
* The target function has discrete output values
* Disjunctive descriptions may be required
* The training data may contain errors
* The training data may contain missing attribute values

ID3 Algorithm
-------------

ID3 algorithm learns decision trees by constructing them topdown, beginning with the question "which attribute should be 
tested at the root of the tree?" To answer this question, each instance attribute is evaluated using a statistical test
to determine how well it alone classifies the training examples. The best attribute is selected and used as the test at
the root node of the tree. A descendant of the root node is then created for each possible value of this attribute, and
the training examples are sorted to the appropriate descendant node (i.e., down the branch corresponding to the
example's value for this attribute). The entire process is then repeated using the training examples associated with
each descendant node to select the best attribute to test at that point in the tree. This forms a greedy search for an 
acceptable decision tree, in which the algorithm never backtracks to reconsider earlier choices. A simplified version of 
the algorithm, specialized to learning boolean-valued functions (i.e., concept learning), is described below

> ID3(examples, targe_tattribute, attributes) $$\rightarrow$$ a decision tree that correctly classifies the given
> "examples"
> 
> | Concept            | Definition                                                                 |
> |--------------------|----------------------------------------------------------------------------|
> | "examples"         | the training examples                                                      |
> | "targe_tattribute" | the attribute whose value is to be predicted by the tree                   |
> | "attributes"       | a list of other attributes that may be tested by the learned decision tree |
> 
> * Create a Root node for the tree
> * If all "examples" are positive, return the single-node tree Root, with label = `+`
> * If all "examples" are negative, return the single-node tree Root, with label = `-`
> * If "attributes" is empty, return the single-node tree Root, with label = most common value of "target_attribute" in
> * "examples"
> * Otherwise begin
>    1. $$A$$ $$\leftarrow$$ attribute from "attributes" that best* classifies "examples"
>    2. The decision attribute for Root $$\leftarrow$$ $$A$$
>    3. For each possible value, $$v_i$$, of $$A$$,
>       * Add a new tree branch below Root, corresponding to the test $$A$$ = $$v_i$$
>       * Let $$examples_{v_i}$$ be the subset of "examples" that have value $$v_i$$ for $$A$$
>       * If $$examples_{v_i}$$ is empty
>         - then below this new branch add a leaf node with label = most common value of "targe_tattribute" attribute in
>           "examples"
>         - else below this new branch add the subtree
>           ID3($$examples_{v_i}$$, "targe_tattribute", "attributes" - $${A}$$)
> * End
> * Return Root
\* The best attribute is the one with the highest [information gain](#which-attribute-is-the-best-classifier)

### Which Attribute Is the Best Classifier?

We will define a statistical property, called **Information Gain**, that measures how well a given attribute separates
the training examples according to their target classification. ID3 uses this information gain measure to select among
the candidate attributes at each step while growing the tree

#### Entropy Measures Homogeneity of Examples

In information theory, **Entropy** characterizes the (im)purity of an arbitrary collection of examples. Given a
collection $$S$$, containing positive and negative examples of some target concept, the entropy of S relative to this
boolean classification is

$$ Entropy(S) \equiv -p_\oplus \log_2 p_\oplus - p_\ominus \log_2 p_\ominus $$

where $$p_\oplus$$, is the proportion of positive examples in $$S$$ and p_\ominus is the proportion of negative examples
in $$S$$

If the target attribute can take on $$c$$ different values, then the entropy of $$S$$ relative to this c-wise 
classification is defined as

$$ Entropy(S) \equiv \sum_{i = 1}^{c} -p_i \log_2 p_i $$

where $$p_i$$ is the proportion of $$S$$ belonging to class $$i$$. 

> The logarithm is still base 2 because entropy is a measure of the expected encoding length measured in bits.
> If the target attribute can take on $$c$$ possible values, the entropy can be as large as $$\log_2 c$$.

> **INFORMATION GAIN MEASURES THE EXPECTED REDUCTION IN ENTROPY**

### Hypothesis Space Search in Decision Tree Learning

* ID3's hypothesis space of all decision trees is a complete space of finite discrete-valued functions, relative to the 
available attributes.
* ID3 maintains only a single current hypothesis

### Inductive Bias in Decision Tree Learning

> 📋 Inductive bias is the set of assumptions that, together with the training data, deductively justify the 
> classifications assigned by the learner to future instances

> **Approximate inductive bias of ID3**: _Shorter trees are preferred over larger trees_. Trees that place high 
> information gain attributes close to the root are preferred over those that do not.

### Issues in Decision Tree Learning

#### Avoiding Overfitting the Data

> Given a hypothesis space $$H$$, a hypothesis $$h \in H$$ is said to **overfit** the training data if there exists some
> alternative hypothesis $$h' \in H$$, such that $$h$$ has smaller error than $$h'$$ over the training examples, but 
> $$h'$$ has a smaller error than $$h$$ over the entire distribution of instances.

Overfitting is a significant practical difficulty for decision tree learning and many other learning methods. There are 
several approaches to avoiding overfitting in decision tree learning. These can be grouped into two classes

1. approaches that stop growing the tree earlier, before it reaches the point where it perfectly classifies the training 
   data
2. approaches that allow the tree to overfit the data, and then post-prune the tree

Although the first of these approaches might seem.more direct, the second approach of post-pruning overfit trees has
been found to be more successful in practice. This is due to the difficulty in the first approach of estimating
precisely when to stop growing the tree.

##### What Criterion is to be Used to Determine the Correct Final Tree Size

Approaches include:

1. Use a separate set of examples, distinct from the training examples, to evaluate the utility of post-pruning nodes
   from the tree (**training and validation set** approach).
2. Use all the available data for training, but apply a statistical test to estimate whether expanding (or pruning) a 
   particular node is likely to produce an improvement beyond the training set. 
3. Use an explicit measure of the complexity for encoding the training examples and the decision tree, halting growth of 
   the tree when this encoding size is minimized. This approach is based on a heuristic called the **Minimum Description 
   Length principle**
