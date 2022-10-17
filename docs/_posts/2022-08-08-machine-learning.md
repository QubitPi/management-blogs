---
layout: post
title: Machine Learning Basics
tags: [Machine Learning, Decision Tree]
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

> Given a hypothesis space $$\mathit{H}$$, a hypothesis $$\mathit{h \in H}$$ is said to **overfit** the training data if there exists some
> alternative hypothesis $$\mathit{h' \in H}$$, such that $$\mathit{h}$$ has smaller error than $$\mathit{h'}$$ over the training examples, but
> $$\mathit{h'}$$ has a smaller error than $$\mathit{h}$$ over the entire distribution of instances.

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

1. `[0, 0]`: $$A1_{v_1} = 0$$, $$A2_{v_1} = 0$$
2. `[1, 1]`: $$A1_{v_2} = 1$$, $$A2_{v_2} = 1$$

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


[DecisionTreeClassifier]: https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html#sklearn.tree.DecisionTreeClassifier
