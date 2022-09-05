---
layout: post
title: JavaScript Classes
tags: [JavaScript]
category: FINALIZED
color: rgb(8, 169, 109)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
author: QubitPi
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


Using a Class
-------------

When you have a class, you can use the class to create objects:

{% highlight javascript %}
let myCar1 = new Car("Ford", 2014);
let myCar2 = new Car("Audi", 2019);
{% endhighlight %}

The example above uses the Car class to create two Car objects. The constructor method is called automatically when a
new object is created.


The Constructor Method
----------------------

The constructor method is a special method:

* It has to have the exact name "constructor"
* It is executed automatically when a new object is created
* It is used to initialize object properties

If you do not define a constructor method, JavaScript will add an empty constructor method.


Class Methods
-------------

{% highlight javascript %}
class ClassName {
    constructor() { ... }
    method_1() { ... }
    method_2() { ... }
    method_3() { ... }
}
{% endhighlight %}

For example, we create a Class method named "age", that returns the Car age below: 

{% highlight javascript %}
class Car {

    constructor(name, year) {
        this.name = name;
        this.year = year;
    }

    age() {
        let date = new Date();
        return date.getFullYear() - this.year;
    }
}

let myCar = new Car("Ford", 2014);
document.getElementById("demo").innerHTML =
"My car is " + myCar.age() + " years old.";
{% endhighlight %}

We can send parameters to Class methods:

{% highlight javascript %}
class Car {

    constructor(name, year) {
        this.name = name;
        this.year = year;
    }

    age(currentYear) {
        return currentYear - this.year;
    }
}

let date = new Date();
let year = date.getFullYear();

let myCar = new Car("Ford", 2014);
document.getElementById("demo").innerHTML=
"My car is " + myCar.age(year) + " years old.";
{% endhighlight %}
