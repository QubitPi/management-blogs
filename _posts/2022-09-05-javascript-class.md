---
layout: post
title: JavaScript Classes
tags: [JavaScript]
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

ECMAScript 2015, also known as ES6, introduced JavaScript Classes. JavaScript Classes are templates for JavaScript
Objects.

<!--more-->

* TOC
{:toc}


JavaScript Class Syntax
-----------------------

Use the keyword **class** to create a class. Always add a method named **constructor()**:

{% highlight javascript %}
class ClassName {
    constructor() { ... }
}
{% endhighlight %}

For example

{% highlight javascript %}
class Car {
    constructor(name, year) {
        this.name = name;
        this.year = year;
    }
}
{% endhighlight %}

The example above creates a class named "Car" with two initial properties: "name" and "year".


> A JavaScript class is not an object. It is a template for JavaScript objects

