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

Artificial Neural Networks
--------------------------

Information-processing abilities of biological neural systems have been thought to follow from highly parallel
processes operating on representations that are distributed over many neurons in human brain. One motivation for
Artificial Neural Networks (ANNs) systems is to capture this kind of highly parallel computation based on distributed
representations. ANN provide a general, practical method for learning real-valued, discrete-valued, and vector-valued
functions from examples.

ANN learning is well-suited to problems in which the training data corresponds to _noisy, complex sensor data, such as
inputs from cameras and microphones_. It is also applicable to problems for which more symbolic representations are
often used, such as the decision tree learning tasks. In these cases ANN and decision tree learning often produce
results of comparable accuracy

![Error loading ann-deep-nn.png]({{ "/assets/img/ann-deep-nn.png" | relative_url}})

### Neurons

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

#### Feed-forward Neural Network

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

#### Commonly Used Activation Functions

Every activation function (or **non-linearity**) takes a single number and performs a certain fixed mathematical operation
on it. There are several activation functions we may employ in practice

##### Sigmoid

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

##### Tanh

The tanh non-linearity is shown on the image above on the right. It squashes a real-valued number to the range `[-1, 1]`.
Like the sigmoid neuron, its activations saturate, but unlike the sigmoid neuron its output is zero-centered. Therefore,
in practice the _tanh non-linearity is always preferred to the sigmoid nonlinearity_. Also note that the tanh neuron is
simply a scaled sigmoid neuron, in particular the following holds: tanh(x)=2σ(2x) - 1.

##### ReLU

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

##### Leaky ReLU

Leaky ReLUs are one attempt to fix the "dying ReLU" problem. Instead of the function being zero when $$\mathit{x < 0}$$,
a leaky ReLU will instead have a small positive slope (of 0.01, or so). Some people report success with this form of
activation function, but the results are not always consistent. The slope in the negative region can also be made into a
parameter of each neuron, as seen in **PReLU** neurons, introduced in [Delving Deep into Rectifiers](https://arxiv.org/abs/1502.01852). However, the consistency of the benefit across tasks is unclear at the moment.

##### Maxout

Other types of units have been proposed that _do not have the functional form $$\mathit{f\ (w^Tx + b)}$$, in which a
non-linearity is applied on the dot product between the weights and the data_. One relatively popular choice is the
Maxout neuron (introduced recently by [Goodfellow et al.](https://arxiv.org/abs/1302.4389)) that generalizes the ReLU
and its leaky version. The Maxout neuron computes the function
$$\max(\mathit{f\ (w_1^Tx + b_1)}, \mathit{f\ (w_2^Tx + b_2)})$$. Notice that both ReLU and Leaky ReLU are a special
case of this form. The Maxout neuron therefore enjoys all the benefits of a ReLU unit (linear regime of operation, no
saturation) and does not have its drawbacks (dying ReLU). However, unlike the ReLU neurons it doubles the number of
parameters for every single neuron, leading to a high total number of parameters.

#### Single Neuron as a Linear Classifier

TBA

### Neural Network Architectures

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

#### Example feed-forward computation

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

#### Representational Power

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

#### Setting Number of Layers and Their Sizes

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

### Data Preprocessing

There are three common forms of data preprocessing a data matrix $$\mathit{X}$$, where we will assume that
$$\mathit{X}$$ is of size $$\mathit{[N \times D]}$$ ($$\mathit{N}$$ is the number of data, $$\mathit{D}$$ is their
dimensionality).

#### 1. Mean Subtraction

Mean subtraction is the most common form of preprocessing. It involves subtracting the mean across every individual
feature in the data, and has the geometric interpretation of _centering the cloud of data around the origin along every
dimension_. In numpy, this operation would be implemented as: `X -= np.mean(X, axis = 0)`. With images specifically, for
convenience it can be common to subtract a single value from all pixels (e.g. `X -= np.mean(X)`), or to do so separately
across the three color channels.

![Error loading ann-preprocessing-mean-norm.png]({{ "/assets/img/ann-preprocessing-mean-norm.png" | relative_url}})

#### 2. Normalization

Normalization refers to normalizing the data dimensions so that they are of approximately the same scale. There are two
common normalization approaches. One is to divide each dimension by its standard deviation once it has been
zero-centered: (`X /= np.std(X, axis = 0)`). Another form of this preprocessing normalizes each dimension so that the
min and max along the dimension is -1 and 1 respectively. It only makes sense to apply this preprocessing if you have a
reason to believe that different input features have different scales (or units), but they should be of approximately
equal importance to the learning algorithm. In case of images, the relative scales of pixels are already approximately
equal (and in range from 0 to 255), so it is not strictly necessary to perform this additional preprocessing step.

#### 3. PCA & Whitening

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

Then we compute the [SVD factorization](#singular-value-decomposition) of the covariance matrix:

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
