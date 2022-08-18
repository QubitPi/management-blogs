---
layout: post
title: (Updating...) Artificial Neural Networks - Linear Algebra Basics
tags: [Machine Learning, Artificial Neural Networks, ANN, Data Preprocessing]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/18-cover.png"
thumbnail: "assets/img/post-cover/18-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Triangular Factors and Row Exchanges
------------------------------------

Any system (or set) of linear equations can be written in the form of

$$\mathit{ Ax = b }$$

Elimination can transform the above into 

$$\mathit{ Ux = c }$$

where matrix $$\mathit{U}$$ is upper triangular (i.e. all entries below the diagonal are zero) and is derived from
$$\mathit{A}$$ via relationship

$$ \mathit{GFEA = U} $$

where $$\mathit{E}$$, $$\mathit{F}$$, $$\mathit{G}$$ are called **elementary matrices**

To be continued...


Eigenvectors
------------

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


Singular Value Decomposition
----------------------------

> Any $$\mathit{m} \times \mathit{n}$$ matrix $$\mathit{A}$$ can be factored into
> 
> $$ \mathit{ A = U \Sigma V^T = (\text{orthogonal})(\text{diagonal})(\text{orthogonal}) } $$
> 
> where the columns of the $$\mathit{m \times m}$$ $$\mathit{U}$$ are eigenvectors of $$\mathit{AA^T}$$ and the columns
> of the $$\mathit{n \times n}$$ $$\mathit{V}$$ are eigenvectors of $$\mathit{A^TA}$$. The $$\mathit{r}$$ singular
> values on the diagonal of the $$\mathit{m \times n}$$ $$\Sigma$$ are the square roots of the nonzero eigenvalues of
> both $$\mathit{AA^T}$$ and $$\mathit{A^TA}$$

To be continued...
