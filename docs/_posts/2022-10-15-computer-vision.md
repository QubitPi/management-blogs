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

Most of the time when working with 3D, we are thinking in terms of Euclidean geometry - that is, coordinates in 
three-dimensional space (𝑋, 𝑌, and 𝑍). However, there are certain situations where it is useful to think in terms of 
projective geometry instead. Projective geometry has an extra dimension, called 𝑊, in addition to the 𝑋, 𝑌, and 𝑍 
dimensions. This four-dimensional space is called "projective space", and coordinates in projective space are called 
"homogeneous coordinates."

#### Why Is There a Fourth Coordinate in 3D Space?

![Error loading cartesian-vs-homogeneous-coordinates.png]({{ "/assets/img/cartesian-vs-homogeneous-coordinates.png" | relative_url}})

Cartesian coordinates are just the first 3 numbers of homogeneous coordinates divided by the fourth. So if it is 1, then 
homogeneous coordinates is basically the same thing as Cartesian. But the smaller it gets, the further the point in 
Cartesian coordinates travels from the null. That's all rather simple until one moment. What if the fourth coordinate is
0? Intuition tells, that it should be further from the 0 than every other point. Every other point in Euclidean space
that is. Homogeneous coordinates indeed denote points not only in Euclidean or, more general, affine space, but in 
projective space that includes and expands affine one.

The [real projective plane](https://en.wikipedia.org/wiki/Projective_plane#Extended_Euclidean_plane) can be thought of
as the Euclidean plane with additional points added, which are called
[points at infinity](https://en.wikipedia.org/wiki/Point_at_infinity), and are considered to lie on a new line, the line
at infinity. There is a point at infinity corresponding to each direction (numerically given by the slope of a line), 
informally defined as the limit of a point that moves in that direction away from the origin. Parallel lines in the 
Euclidean plane are said to intersect at a point at infinity corresponding to their common direction. Given a point
$$\mathit{(x, y)}$$ on the Euclidean plane, for any non-zero real number $$\mathit{Z}$$, the triple
$$\mathit{(xZ, yZ, Z)}$$ is called a set of homogeneous coordinates for the point. By this definition, multiplying the 
three homogeneous coordinates by a common, non-zero factor gives a new set of homogeneous coordinates for the same
point. In particular, $$\mathit{(x, y, 1)}$$ is such a system of homogeneous coordinates for the point
$$\mathit{(x, y)}$$. For example, the Cartesian point (1, 2) can be represented in homogeneous coordinates as (1, 2, 1)
or (2, 4, 2). The original Cartesian coordinates are recovered by dividing the first two positions by the third. Thus 
unlike Cartesian coordinates, a single point can be represented by infinitely many homogeneous coordinates.
