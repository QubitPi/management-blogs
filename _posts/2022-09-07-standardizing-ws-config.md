---
layout: post
title: (WIP) Standardizing Backend Software Configuration
tags: [Management]
color: rgb(255, 105, 132)
feature-img: "assets/img/post-cover/17-cover.png"
thumbnail: "assets/img/post-cover/17-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Why Standards?
--------------

The main purpose of conforming to standards is to ensure that products, systems and organisations are safe, reliable and
good for the environment. Standards help make systems interoperable. Standards are important in effective communication 
between disparate systems. 

Systems that are developed to standards also make them more credible. Software that is developed to a set of standards
can be more cost-effective because it becomes easier to implement and to learn for those who have been exposed to the 
standards previously.

In my team, we make every home-brewed backend system component adopt
[aeonbits configuration](http://owner.aeonbits.org/), which has been used, as my early years working at Yahoo, by one of
a crucial team in the company's ad pipeline backend system.

We will define two interfaces

1. **Configuration**
2. **Configuration Access Layer**

