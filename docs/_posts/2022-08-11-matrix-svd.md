---
layout: post
title: Artificial Neural Networks - Singular Value Decomposition
tags: [Machine Learning, Artificial Neural Networks, ANN, Data Preprocessing]
color: rgb(0, 204, 0)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

> **Singular Value Decomposition**
> 
> Any $$\mathit{m} \times \mathit{n}$$ matrix $$\mathit{A}$$ can be factored into
> 
> $$ \mathit{ A = U\SigmaV^T = (\text{orthogonal})(\text{diagonal})(\text{orthogonal}) } $$
> 
> where the columns of the $$\mathit{m \times m}$$ $$\mathit{U}$$ are eigenvectors of $$\mathit{AA^T}$$ and the columns
> of the $$\mathit{n \times n}$$ $$\mathit{V}$$ are eigenvectors of $$\mathit{A^TA}$$. The $$\mathit{r}$$ singular
> values on the diagonal of the $$\mathit{m \times n}$$ $$\Sigma$$ are the square roots of the nonzero eigenvalues of
> both $$\mathit{AA^T}$$ and $$\mathit{A^TA}$$


Eigenvectors
------------

