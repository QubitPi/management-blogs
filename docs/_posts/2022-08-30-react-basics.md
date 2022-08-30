---
layout: post
title: React Basics
tags: [React, JavaScript]
category: FINALIZED
color: rgb(97, 218, 251)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


React Render HTML
-----------------

React's goal is in many ways to render HTML in a web page by using a function called **ReactDOM.render()**.

### The Render Function

The `ReactDOM.render()` function takes two arguments

1. HTML code
2. an HTML element

The purpose of the function is to display the specified HTML code inside the specified HTML element.

But render where?

There is another folder in the root directory of your React project, named "public". In this folder, there is an
**index.html** file.

You'll notice a single `<div>` in the body of this file. This is where our React application will be rendered.


React Components
----------------

> **React is all about re-using code, which is why component is a very crucial concept in this context**

Components are **independent and reusable bits of code**. They serve the same purpose as JavaScript functions, but work
in isolation and return HTML.

Components come in two types, Class components and Function components. Class components are rarely used today. We will,
therefore, focus on the other throughout this post.

### Create a Component

A component's name MUST start with an upper case letter. A Function component also returns HTML. For example, let's
create a Function component called "Car"

{% highlight react %}
function Car() {
    return <h2>Hi, I am a Car!</h2>;
}
{% endhighlight %}

### Rendering a Component

Now our React application has a component called `Car`, which returns an `<h2>` element. To use this component
application, use similar syntax as normal HTML: `<Car />`

{% highlight react %}
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

### Importing a Component

React is all about re-using code, and it is recommended to split your components into separate files. To do that, create
a new file named `Car.js` and put the code inside it:

{% highlight react %}
function Car() {
    return <h2>Hi, I am a Car!</h2>;
}

export default Car;
{% endhighlight %}

> ⚠️ Note that the filename must start with an uppercase character.

To be able to use the Car component, you have to _import the file_ in our application.

{% highlight react %}
import React from 'react';
import ReactDOM from 'react-dom/client';
import Car from './Car.js';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

### Props

Props ("properties") are arguments passed into React components via HTML attributes.

React Props are like function arguments in JavaScript and attributes in HTML. To send props into a component, use the
same syntax as HTML attributes. For example, to add a "brand" attribute to the Car element:

{% highlight react %}
const myElement = <Car brand="Ford" />;
{% endhighlight %}

The component receives the argument as a `props` object:

{% highlight react %}
function Car(props) {
    return <h2>I am a { props.brand }!</h2>;
}
{% endhighlight %}

#### Passing Data

Props are also how you pass data from one component to another, as parameters.

{% highlight react %}
function Car(props) {
    return <h2>I am a { props.brand }!</h2>;
}

function Garage() {
    return (
        <>
            <h1>Who lives in my garage?</h1>
            <Car brand="Ford" />
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Garage />);
{% endhighlight %}

If we have a variable to send, and not a string as in the example above, simply put the variable name inside curly
brackets:

{% highlight react %}
function Car(props) {
    return <h2>I am a { props.brand }!</h2>;
}

function Garage() {
    const carName = "Ford";
    return (
        <>
            <h1>Who lives in my garage?</h1>
            <Car brand={ carName } />
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Garage />);
{% endhighlight %}

Or if it was an object:

{% highlight react %}
function Car(props) {
    return <h2>I am a { props.brand.model }!</h2>;
}

function Garage() {
    const carInfo = { name: "Ford", model: "Mustang" };
    return (
        <>
            <h1>Who lives in my garage?</h1>
            <Car brand={ carInfo } />
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Garage />);
{% endhighlight %}

> ⚠️ React Props are read-only! You will get an error if you try to change their value.


React Events
------------

Just like HTML DOM events, React can perform actions based on user events. React has the same events as HTML: click, 
change, mouseover etc.

### Adding Events

React events are written in camelCase syntax:

`onClick` instead of `onclick`.

React event handlers are written inside curly braces:

`onClick={shoot}` instead of `onClick="shoot()"`.

For example

{% highlight react %}
<button onClick={shoot}>Take the Shot!</button>
{% endhighlight %}

To put the `shoot` function inside the `Football` component:

{% highlight react %}
function Football() {
    const shoot = () => {
        alert("Great Shot!");
    }
    
    return (
        <button onClick={shoot}>Take the shot!</button>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Football />);
{% endhighlight %}

### Passing Arguments

To pass an argument to an event handler, use an arrow function. For instance, to send "Goal!" as a parameter to the
`shoot` function

{% highlight react %}
function Football() {
    const shoot = (a) => {
        alert(a);
    }

    return (
        <button onClick={() => shoot("Goal!")}>Take the shot!</button>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Football />);
{% endhighlight %}

### React Event Object

Event handlers have access to the React event that triggered the function. In our example the event is the "click"
event.

{% highlight react %}
function Football() {
    const shoot = (a, b) => {
        alert(b.type);
        /*
        'b' represents the React event that triggered the function,
        in this case the 'click' event
        */
    }

    return (
        <button onClick={(event) => shoot("Goal!", event)}>Take the shot!</button>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Football />);
{% endhighlight %}