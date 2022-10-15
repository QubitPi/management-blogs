---
layout: post
title: Computer Vision
tags: [Computer Vision]
category: FINALIZED
color: rgb(29, 114, 232)
feature-img: "assets/img/post-cover/32-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Concepts
--------

### Homogeneous Coordinates

Homogeneous coordinates are ubiquitous in computer graphics because they allow common vector operations such as 
translation, rotation, scaling and perspective projection to be represented as a matrix by which the vector is
multiplied.

Most of the time when working with 3D, we are thinking in terms of Euclidean geometry – that is, coordinates in three-dimensional space (𝑋, 𝑌, and 𝑍). However, there are certain situations where it is useful to think in terms of projective geometry instead. Projective geometry has an extra dimension, called 𝑊, in addition to the 𝑋, 𝑌, and 𝑍 dimensions. This four-dimensional space is called “projective space,” and coordinates in projective space are called “homogeneous coordinates.”