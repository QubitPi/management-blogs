---
layout: post
title: Machine Learning Basics
tags: [Machine Learning]
category: FINALIZED
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Concept Learning
----------------

Much of learning involves acquiring general concepts from specific training examples. Each concept can be thought of as
a boolean-valued function defined over a set of objects. Concept learning is a Machine Learning that approximates to
this boolean-valued function through labeled instances.

> Concept learning is a learning of inferring a boolean-valued function from training examples of its input and output.
>
> That is, given:
>
> * A set of instances, **$$\mathit{X}$$**, each described by the attributes
>   * $$\mathit{a_1}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * $$\mathit{a_2}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * $$\mathit{a_3}$$, with possible values of {$$?$$, $$\mathit{\emptyset}$$, a specific value}
>   * ...
>
> * and Hypotheses $$\mathit{H}$$: Each hypothesis is described by a conjunction of constraints on the attributes
>   $$\mathit{a_1}$$, $$\mathit{a_2}$$, $$\mathit{a_3}$$, ..., each of the constraints may be "?" (any value is
>   acceptable), $$\mathit{\emptyset}$$ (no value is acceptable), or a specific value
> * Target concept $$\mathit{c : X \rightarrow {0, 1}}$$
> * Training examples $$\mathit{D}$$: Positive and negative examples of the target function
>
> Determine (i.e. Learn) hypothesis $$\mathit{h}$$ in $$\mathit{H}$$ such that $$\mathit{h(x) = c(x)}$$ for all
> $$\mathit{x}$$ in $$\mathit{X}$$.

When learning the target concept, the learner is presented a set of training examples, each consisting of an instance
$$\mathit{x}$$ from $$\mathit{X}$$, along with its target concept value $$\mathit{c(x)}$$. Instances for which
$$\mathit{c(x) = 1}$$ are called **positive examples**, or members of the target concept. Instances for which
$$\mathit{c(x) = 0}$$ are called **negative examples**, or nonmembers of the target concept. We will often write the
ordered pair $$\mathit{\langle x, c(x) \rangle}$$ to describe the training example consisting of the instance
$$\mathit{x}$$ and its target concept value $$\mathit{c(x)}$$. We use the symbol $$\mathit{D}$$ to denote the set of
available training examples

The best hypothesis learned regarding unseen instances is the hypothesis that best fits the observed training
data. This is the **fundamental assumption of inductive learning**

> **The Inductive Learning Hypothesis**: Any hypothesis found to approximate the target function well over a
> sufficiently large set of training examples will also approximate the target function well over other unobserved
> examples.

Concept learning can be viewed as the task of searching through a large space of hypotheses implicitly defined by the
hypothesis representation. Most practical learning tasks involve very large, sometimes infinite, hypothesis spaces.
Machine Learning, therefore, is interested in being capable of efficiently searching very large or infinite hypothesis
spaces, to find the hypotheses that best fit the training data, which we shall discuss next

### FIND-S Search Algorithm - Finding A Maximally Specific Hypothesis

> **General-to-Specific Ordering of Hypotheses**
>
> Definition: Let $$\mathit{h_j}$$ and $$\mathit{h_k}$$ be boolean-valued functions defined over $$\mathit{X}$$. Then 
> $$\mathit{h_j}$$ is **more-general-than-or-equal-to** $$\mathit{h_k}$$ (written as $$\mathit{h_j \geq_g h_k}$$) if and 
> only if
>
> $$ \mathit{(\forall x \in X)[(h_k(x) = 1) \rightarrow (h_j(x) = 1)]} $$

How can we use the more-general-than partial ordering to organize the search for a hypothesis consistent with the
observed training examples? One way is to begin with the most specific possible hypothesis in H, then generalize this
hypothesis each time it fails to cover an observed positive training example:

> 1. Initialize $$\mathit{h}$$ to the most specific hypothesis in $$\mathit{H}$$
>
> $$\mathit{ h \leftarrow \langle\emptyset, \emptyset, \emptyset, ..., \emptyset\rangle }$$
>
> 2. For each _positive_ training instance $$\mathit{x}$$:
>    * For each attribute constraint $$\mathit{a_i}$$, in $$\mathit{h}$$:
>      - If the constraint $$\mathit{a_i}$$, is satisfied by $$\mathit{x}$$, then do nothing
>      - Else replace $$\mathit{a_i}$$, in $$\mathit{h}$$ by the next more general constraint that is satisfied by
>        $$\mathit{x}$$
> 3. Output hypothesis $$\mathit{h}$$

The FIND-S algorithm simply ignores every negative example. In the general case, as long as we assume that the
hypothesis space $$\mathit{H}$$ contains a hypothesis that describes the true target concept $$\mathit{c}$$ and that the 
training data contains no errors, then the current hypothesis $$\mathit{h}$$ can never require a revision in response to
a negative example.

The FIND-S algorithm illustrates one way in which the more-general-than partial ordering can be used to organize the
search for an acceptable hypothesis. The search moves from hypothesis to hypothesis, searching from the most specific to
progressively more general hypotheses along one chain of the partial ordering

FIND-S is guaranteed to output the most specific hypothesis within $$\mathit{H}$$ that is consistent with the positive 
training examples.

### The Candidate-Elimination Algorithm

The Candidate-Elimination Algorithm addresses several of the limitations of FIND-S. Notice that although FIND-S outputs
a hypothesis from $$\mathit{H}$$, that is consistent with the training examples, this is just one of many hypotheses
from $$\mathit{H}$$ that might fit the training data equally well. The key idea in the Candidate-Elimination Algorithm
is to _output a description of the set of all hypotheses consistent with the training examples_

The Candidate-Elimination Algorithm represents the set of all hypotheses consistent with the observed training examples.
This subset of all hypotheses is called the **version space** with respect to the hypothesis space $$\mathit{H}$$ and
the training examples $$\mathit{D}$$

> **The LIST-THEN-ELIMINATE Algorithm**
>
> 1. VersionSpace $$\leftarrow$$ a list containing every hypothesis in $$\mathit{H}$$
> 2. For each training example, $$\mathit{\langle x, c(x) \rangle}$$
>    * remove from VersionSpace any hypothesis h for which $$\mathit{h(x) != c(x)}$$
> 3. Output the list of hypotheses in VersionSpace

In principle, the LIST-THEN-ELIMINATE Algorithm can be applied whenever the hypothesis space $$\mathit{H}$$ is finite

The Candidate-Elimination Algorithm works on the same principle as the LIST-THEN-ELIMINATE Algorithm. However, it
employs a much more compact representation of the version space.

> 1. Initialize $$\mathit{G}$$ to the set of maximally general hypotheses in $$\mathit{H}$$
>    ($$ \mathit{G_0 \leftarrow {\langle ?, ?, ..., ? \rangle}} $$)
> 2. Initialize $$\mathit{S}$$ to the set of maximally specific hypotheses in $$\mathit{H}$$
>    ($$ \mathit{S_0 \leftarrow {\langle \emptyset, \emptyset, ..., \emptyset \rangle}} $$)
>
> For each training example $$\mathit{d}$$, do
>   * If $$\mathit{d}$$ is a positive example
>     - Remove from $$\mathit{G}$$ any hypothesis inconsistent with $$\mathit{d}$$,
>     - For each hypothesis $$\mathit{s}$$ in $$\mathit{S}$$ that is not consistent with $$\mathit{d}$$
>       * Remove $$\mathit{s}$$ from $$\mathit{S}$$
>       * Add to $$\mathit{S}$$ all minimal generalizations $$\mathit{h}$$ of $$\mathit{s}$$ such that $$\mathit{h}$$ is 
>         consistent with $$\mathit{d}$$, and some member of $$\mathit{G}$$ is more general than $$\mathit{h}$$
>       * Remove from $$\mathit{S}$$ any hypothesis that is more general than another hypothesis in $$\mathit{S}$$
>   * If $$\mathit{d}$$ is a negative example
>     - Remove from $$\mathit{S}$$ any hypothesis inconsistent with $$\mathit{d}$$
>     - For each hypothesis $$\mathit{g}$$ in $$\mathit{G}$$ that is not consistent with $$\mathit{d}$$
>       * Remove $$\mathit{g}$$ from $$\mathit{G}$$
>       * Add to $$\mathit{G}$$ all minimal specializations $$\mathit{h}$$ of $$\mathit{g}$$ such that $$\mathit{h}$$ is 
>         consistent with $$\mathit{d}$$, and some member of $$\mathit{S}$$ is more specific than $$\mathit{h}$$
>       * Remove from $$\mathit{G}$$ any hypothesis that is less general than another hypothesis in $$\mathit{G}$$

Practical applications of the Candidate-Elimination Algorithm and FIND-S algorithms are limited by the fact that they
both perform poorly when given noisy training data.


Decision Tree Learning
----------------------

Decision tree learning is one of the most widely used and practical methods for inductive inference. It is a method for
approximating discrete-valued functions that is robust to noisy data and capable of learning disjunctive expressions

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

### ID3 Algorithm

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
>    1. $$\mathit{A}$$ $$\leftarrow$$ attribute from "attributes" that best* classifies "examples"
>    2. The decision attribute for Root $$\leftarrow$$ $$\mathit{A}$$
>    3. For each possible value, $$\mathit{v_i}$$, of $$\mathit{A}$$,
>       * Add a new tree branch below Root, corresponding to the test $$\mathit{A}$$ = $$v_i$$
>       * Let $$examples_{\mathit{v_i}}$$ be the subset of "examples" that have value $$\mathit{v_i}$$ for
>         $$\mathit{A}$$
>       * If $$examples_{\mathit{v_i}}$$ is empty
>         - then below this new branch add a leaf node with label = most common value of "targe_tattribute" attribute in
>           "examples"
>         - else below this new branch add the subtree
>           ID3($$examples_{\mathit{v_i}}$$, "targe_tattribute", "attributes" - $${\mathit{A}}$$)
> * End
> * Return Root

\* The best attribute is the one with the highest [information gain](#which-attribute-is-the-best-classifier)

### Which Attribute Is the Best Classifier?

We will define a statistical property, called **Information Gain**, that measures how well a given attribute separates
the training examples according to their target classification. ID3 uses this information gain measure to select among
the candidate attributes at each step while growing the tree

#### Entropy Measures Homogeneity of Examples

In information theory, **Entropy** characterizes the (im)purity of an arbitrary collection of examples. Given a
collection $$\mathit{S}$$, containing positive and negative examples of some target concept, the entropy of S relative to this
boolean classification is

$$ Entropy(\mathit{S}) \equiv \mathit{-p_\oplus \log_2 p_\oplus - p_\ominus \log_2 p_\ominus} $$

where $$\mathit{p_\oplus}$$, is the proportion of positive examples in $$\mathit{S}$$ and $$\mathit{p_\ominus}$$ is the 
proportion of negative examples in $$\mathit{S}$$

If the target attribute can take on $$\mathit{c}$$ different values, then the entropy of $$\mathit{S}$$ relative to this 
c-wise classification is defined as

$$ Entropy(\mathit{S}) \equiv \mathit{\sum_{i = 1}^{c} -p_i \log_2 p_i} $$

where $$\mathit{p_i}$$ is the proportion of $$\mathit{S}$$ belonging to class $$\mathit{i}$$.

> The logarithm is still base 2 because entropy is a measure of the expected encoding length measured in bits.
> If the target attribute can take on $$\mathit{c}$$ possible values, the entropy can be as large as
> $$\mathit{\log_2 c}$$.

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

> Given a hypothesis space $$\mathit{H}$$, a hypothesis $$\mathit{h \in H}$$ is said to **overfit** the training data if 
> there exists some alternative hypothesis $$\mathit{h' \in H}$$, such that $$\mathit{h}$$ has smaller error than 
> $$\mathit{h'}$$ over the training examples, but $$\mathit{h'}$$ has a smaller error than $$\mathit{h}$$ over the
> entire distribution of instances.

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

### Training Decision Trees Using [scikit-learn](https://scikit-learn.org/stable/modules/tree.html) in Python

#### Prerequisites (Mac OS)

```bash
pip3 install graphviz
brew install Graphviz
```

#### Example

![Error loading iris-decision-tree.png]({{ "/assets/img/iris-decision-tree.png" | relative_url}})

Using the [Iris dataset](https://en.wikipedia.org/wiki/Iris_flower_data_set), we can construct a tree as follows:

```python
from sklearn.datasets import load_iris
from sklearn import tree
import graphviz

iris = load_iris()
x, y = iris.data, iris.target
classifier = tree.DecisionTreeClassifier()
classifier = classifier.fit(x, y)

dot_data = tree.export_graphviz(
   classifier,
   out_file=None,
   feature_names=iris.feature_names,
   class_names=iris.target_names,
   filled=True,
   rounded=True,
   special_characters=True
)
graph = graphviz.Source(dot_data)
graph.render("iris")
```

As with other classifiers, [DecisionTreeClassifier][DecisionTreeClassifier] takes as input two arrays:

1. an array `x` holding `n` training samples, each of which is a vector of `m` feature values
2. an array `y` of integer values holding `n` class labels for the training samples

For example

```python
from sklearn import tree
X = [[0, 0], [1, 1]]
Y = [0, 1]
classifier = tree.DecisionTreeClassifier()
classifier = classifier.fit(X, Y)
```

we have a training dataset of 2 training examples, each of which has 2 feature values:

1. `[0, 0]`: $$\mathit{A1_{v_1} = 0}$$, $$\mathit{A2_{v_1} = 0}$$
2. `[1, 1]`: $$\mathit{A1_{v_2} = 1}$$, $$\mathit{A2_{v_2} = 1}$$

and a vector of labels `y` which says `[0, 0]` is classified as 0 and `[1, 1]` is classified as 1.

After being fitted, the model can then be used to predict the class of samples:

```python
classifier.predict([[2., 2.]])
```

As an alternative to outputting a specific class, the probability of each class can be predicted (in case of
classification tie), which is the fraction of training samples of the class in a leaf:

```python
classifier.predict_proba([[2., 2.]])
```


Evaluating Hypothesis
---------------------

In many cases it is important to evaluate the performance of learned hypotheses as precisely as possible. One reason is
simply to understand whether to use the hypothesis. For instance, when learning from a limited-size database indicating
the effectiveness of different medical treatments, it is important to understand as precisely as possible the accuracy
of the learned hypotheses. A second reason is that evaluating hypotheses is an integral component of many learning
methods. For example, in post-pruning decision trees to avoid overfitting, we must evaluate the impact of possible
pruning steps on the accuracy of the resulting decision tree. Therefore it is important to understand the likely errors
inherent in estimating the accuracy of the pruned and unpruned tree.

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

### Estimating Hypothesis Accuracy

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

#### Sample Error and True Error

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

#### True Error for Discrete-Valued Hypotheses

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

### Basics of Sampling Theory

> Here is a well-studies problem in statistics:
>
> Estimating the proportion of a population that exhibits some property, given the observed proportion over some random
> sample of the population

In our case, the property of interest is that $$\mathit{h}$$ misclassifies the example.

#### Random Variable

We collect a random sample $$\mathit{S}$$ of $$\mathit{n}$$ independently drawn instances from the distribution
$$\mathcal{D}$$, and then measure the sample error $$\text{error}_\mathit{S}\mathit{(h)}$$. If we were to repeat this
experiment many times, each time drawing a different random sample $$\mathit{S_i}$$ of size $$\mathit{n}$$, we would
expect to observe different values for the various $$\text{error}_\mathit{S}\mathit{(h)}$$, depending on random
differences in the makeup of the various $$\mathit{S_i}$$. We say in such cases that
$$\text{error}_\mathit{S}\mathit{(h)}$$, the outcome of the _i_-th such experiment, is a **random variable**. The value
of random variable is the observed outcome of a random experiment

#### Binomial Distribution

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


Artificial Neural Networks - Linear Algebra Basics
--------------------------------------------------

### Vector Spaces

A **vector space** is denoted by $\mathbf{R^n}$, which is a space that consists of all column vectors with
$\mathit{n}$ components.

Within all vector spaces, we can add any two vectors, and we can multiply all vectors by scalars. A **real vector
space** is a set of vectors together with rules for vector addition and multiplication by _real numbers_

#### Subspace

A **subspace** of a vector space is a nonempty subset that satisfies the requirements for a vector space: **Linear
combinations stay in the subspace**

1. If we add any vectors x and y in the subspace, x + y is in the subspace.
2. If we multiply any vector x in the subspace by any scalar c, cx is in the subspace.

A subspace is a subset that is "**closed**" under _addition_ and _scalar multiplication_. Those operations follow the
rules of the host space, keeping us inside the subspace.

### Triangular Factors and Row Exchanges

Any system (or set) of linear equations can be written in the form of

$$\mathit{ Ax = b }$$

Elimination can transform the above into

$$\mathit{ Ux = c }$$

where matrix $$\mathit{U}$$ is upper triangular (i.e. all entries below the diagonal are zero) and is derived from
$$\mathit{A}$$ via relationship

$$ \mathit{GFEA = U} $$

where $$\mathit{E}$$, $$\mathit{F}$$, $$\mathit{G}$$ are called **elementary matrices**

To be continued...

### Eigenvectors

Given that any eigenvalue equation

$$Ax = \lambda x$$

The number $$\lambda$$is an **eigenvalue** of the matrix $$\mathit{A}$$, and the vector $$\mathit{x}$$ is the associated
**eigenvector**. The goal is to find the eigenvalues and eigenvectors, $$\mathit{A}$$'s and $$\mathit{x}$$'s, and to use
them.

For example, an **eigenvalue problem** of

$$\mathit{ 4y - 5z = \lambda y }$$

$$\mathit{ 2y - 3z = \lambda z }$$

is in the form of $$Ax = \lambda x$$

To be continued...

### Singular Value Decomposition

> Any $$\mathit{m} \times \mathit{n}$$ matrix $$\mathit{A}$$ can be factored into
>
> $$ \mathit{ A = U \Sigma V^T = (\text{orthogonal})(\text{diagonal})(\text{orthogonal}) } $$
>
> where the columns of the $$\mathit{m \times m}$$ $$\mathit{U}$$ are eigenvectors of $$\mathit{AA^T}$$ and the columns
> of the $$\mathit{n \times n}$$ $$\mathit{V}$$ are eigenvectors of $$\mathit{A^TA}$$. The $$\mathit{r}$$ singular
> values on the diagonal of the $$\mathit{m \times n}$$ $$\Sigma$$ are the square roots of the nonzero eigenvalues of
> both $$\mathit{AA^T}$$ and $$\mathit{A^TA}$$

To be continued...

TensorFlow 2
------------

### Install TensorFlow 2

Install TensorFlow with Python's pip package manager.

```bash
# Requires the latest pip
pip install --upgrade pip

# Current stable release for CPU and GPU
pip install tensorflow

# Or try the preview build (unstable)
pip install tf-nightly
```

Alternatively, the [TensorFlow Docker images](https://hub.docker.com/r/tensorflow/tensorflow/) are already configured to 
run TensorFlow. A Docker container runs in a virtual environment and is the easiest way to set up GPU support.

```bash
docker pull tensorflow/tensorflow:latest  # Download latest stable image
docker run -it -p 8888:8888 tensorflow/tensorflow:latest-jupyter  # Start Jupyter server 
```


[DecisionTreeClassifier]: https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html#sklearn.tree.DecisionTreeClassifier
