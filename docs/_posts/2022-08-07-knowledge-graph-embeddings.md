---
layout: post
title: (Updating...) Introduction to Knowledge Graph Embeddings (KGE)
tags: [Knowledge Graph, KGE, KRL, Python, Pytorch, KGE]
category: WIP
color: rgb(20, 150, 91)
feature-img: "assets/img/post-cover/12-cover.png"
thumbnail: "assets/img/post-cover/12-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Knowledge Graph
---------------

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

Open World Assumption
---------------------

One problem is that these datasets or graphs can be the result of automatic generation and craft sourcing so keep in
mind they may have missing edges or they may not include all the facts and they may not be entirely comprehensive.
That means knowledge graphs operate under this regime which is known as the **Open World Assumption**, which says that
if a fact is not in the knowledge graph, it is not necessarily false; we simply don't know whether it's false or not.
For example Acme Inc. is the workplace of George. But do we know Acme Inc is based in Liverpool? We don't. That doesn't 
necessarily mean that the absence of a fact means that fact is false in this context

![Error loading kge-assumption.png]({{ "/assets/img/kge-assumption.png" | relative_url}})

Statistical Relational Learning
-------------------------------

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

### Link Prediction

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

Formal Definition of Knowledge Graph Embeddings (KGE)
-----------------------------------------------------

With the informal discussion above, we could present KGE in a rigorous definition

### Definition

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

#### Embedding procedure

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


Best Python Packages (Tools) for Knowledge Graphs
-------------------------------------------------

A Knowledge Graph is a reusable data layer that is used to answer sophisticated queries across multiple data silos. With
contextualized data displayed and organized in the form of tables and graphs, they achieve pinnacle connectivity. They
can quickly accept new information, classifications, and criteria since they were designed to capture the ever-changing
nature of the data. There are different libraries for performing knowledge graphs in Python. Let's check out a few of
them.

### Pykg2vec

[Pykg2vec](https://github.com/Sujit-O/pykg2vec) is a Python package that implements knowledge graph embedding algorithms
and flexible embedding pipeline building elements. This library seeks to assist academics and programmers in fast
testing algorithms with their knowledge base, or adapting the package for their algorithms using modular blocks.

Pykg2vec was built using TensorFlow, but because more authors utilized Pytorch to create their KGE models, it was
switched with Pytorch. The TF version is still available in the tf2-master branch. In addition to the primary model
training procedure, pykg2vec uses multi-processing to generate mini-batches and conduct an assessment to minimize the
overall completion time.

#### Features

* Bayesian hyperparameter optimization
* Inspection techniques for the learned embeddings
* Support cutting-edge KGE model variants as well as evaluation datasets
* Allow for the export of learned embeddings in TSV or Pandas-compatible formats
* KPI overview visualization depending on TSNE (mean rank, hit ratio) in multiple formats

#### Benefits

* Interactive visualizations
* Personalized datasets

### PyKEEN

[PyKEEN (Python Knowledge Embeddings)](https://github.com/pykeen/pykeen) is a Python library that builds and evaluates
knowledge graphs and embedding models. In PyKEEN 1.0, we can estimate the aggregation measures directly for all frequent
rank categories. Such as mean, optimistic, and pessimistic, allowing comparison of their differences.

It can identify instances where the model precisely forecasts identical scores for various triples, which is typically
undesirable behavior. The PyTorch module is used to implement it for Python 3.7+. It includes a set of comprehensive
testing processes performed with PyTest and Tox. You can execute in Travis-continuous CI's integration environment.

#### Features

* Training Approaches: LCWA and sLCWA
* Uniform and Bernoulli negative samplers
* Optimization of hyper-parameters using optuna
* Early stopping
* Evaluation metrics: adjusted mean rank, mean rank, ROC-AUC score

#### Benefits

* It is the only library that uses automatic memory optimization to verify that memory limits are not surpassed during
  testing and training.
* Users can replicate and maintain graphs due to several community-driven tools.

### AmpliGraph

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

#### Features

* **Customization** You can enhance AmpliGraph-based estimators to create your custom knowledge graph embeddings framework.
* **Support** It can run on both CPUs and GPUs to accelerate the training procedure.
* **Less Code** Its APIs cut down on the code needed to anticipate code in knowledge graphs.

#### Benefits

* Open Source API
* It can **predict the missing relationships between graphs**.
* The curation of graphs produced automatically from text, which are typically messy and imprecise, is also considerably
  improved by link prediction.

### LibKGE

[LibKGE](https://github.com/uma-pi1/kge)'s primary purpose is to promote repeatable study into KGE models and training
techniques. The training approach and hyperparameters selected significantly impact simulation results than the model
class alone.

The goal of LibKGE is to provide simple training, hyperparameter optimization, and assessment procedures that can be
used with any model. Every possible knob or heuristic in the platform is available explicitly through well-documented
configuration files. The most common KGE models are included in LibKGE, and you can introduce new models. A thorough
logging mechanism and equipment facilitate in-depth examination.

#### Features

* Early termination
* Checkpointing
* High parallelism potential
* You can pause and restart at any moment
* With or without mutual interactions, all models can be employed.
* Automated Memory management for huge batch sizes

#### Benefits

* LIBKGE is well-structured. Individual modules can be combined and matched, and additional components can be incorporated
  quickly.
* The present configuration of the test is saved alongside the model to increase evaluation and consistency.
* During tests, LIBKGE logs a lot of data and keeps track of performance measures like runtime, memory utilization,
  training attrition, and evaluation methods.
