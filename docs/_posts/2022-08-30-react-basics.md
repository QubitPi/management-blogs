---
layout: post
title: React Basics
tags: [React, JavaScript]
category: FINALIZED
color: rgb(4, 170, 109)
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


Conditional Rendering
---------------------

In React, you can conditionally render components. There are several ways to do this.

### "if" Statement

We can use the `if` JavaScript operator to decide which component to render. As an example, we'll use these two
components:

{% highlight react %}
function MissedGoal() {
    return <h1>MISSED!</h1>;
}

function MadeGoal() {
    return <h1>Goal!</h1>;
}
{% endhighlight %}

Now, we'll create another component that chooses which component to render based on a condition:

{% highlight react %}
function Goal(props) {
    const isGoal = props.isGoal;
    if (isGoal) {
        return <MadeGoal/>;
    }
    return <MissedGoal/>;
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Goal isGoal={false} />);
{% endhighlight %}

### Logical "&&" Operator

Another way to conditionally render a React component is by using the `&&` operator. We can embed JavaScript expressions 
in JSX by using curly braces:

{% highlight react %}
function Garage(props) {
    const cars = props.cars;
    return (
        <>
            <h1>Garage</h1>
            {cars.length > 0 &&
            <h2>
            You have {cars.length} cars in your garage.
            </h2>
            }
        </>
    );
}

const cars = ['Ford', 'BMW', 'Audi'];
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Garage cars={cars} />);
{% endhighlight %}

If `cars.length` is equates to true, the expression after `&&` will render.

### Ternary Operator

Another way to conditionally render elements is by using a ternary operator.

condition ? true : false


FAQ
---

### What is "package-lock.json"?

In version 5, npm introduced the package-lock.json file.

What's that? You probably know about the package.json file, which is much more common and has been around for much
longer.

The goal of package-lock.json file is to keep track of the **exact version** of every package that is installed so that
a product is 100% reproducible in the same way even if packages are updated by their maintainers.

This solves a very specific problem that package.json left unsolved. In package.json you can set which versions you want 
to upgrade to (patch or minor), using the semver notation, for example:

* if you write ~0.13.0, you want to only update patch releases: 0.13.1 is ok, but 0.14.0 is not.
* if you write ^0.13.0, you want to get updates that do not change the leftmost non-zero number: 0.13.1, 0.13.2 and so
  on.
* If you write ^1.13.0, you will get patch and minor releases: 1.13.1, 1.14.0 and so on up to 2.0.0 but not 2.0.0.
* if you write 0.13.0, that is the exact version that will be used, always

You don't commit to Git your node_modules folder, which is generally huge, and when you try to replicate the project on 
another machine by using the `npm install` command, if you specified the `~` syntax and a patch release of a package has 
been released, that one is going to be installed. Same for `^` and minor releases.

> If you specify exact versions, like 0.13.0 in the example, you are not affected by this problem.

It could be you, or another person trying to initialize the project on the other side of the world by running `npm
install`, which could lead to some different versions being installed there

The **package-lock.json** sets your currently installed version of each package **in stone**, and npm will use those
exact versions when running `npm ci`.

_The package-lock.json file needs to be committed to your Git repository_, so it can be fetched by other people, if the 
project is public or you have collaborators, or if you use Git as a source for deployments.

The dependencies versions will be updated in the package-lock.json file when you run `npm update`.


Troubleshooting
---------------

### Change Node Version

> ⚠️ Warning: This answer does not support Windows OS

Suppose we would like to down-grade version from 18 to 14, then we can use `n` for node's version management like this. 
[There](https://www.npmjs.com/package/n) is a simple intro for `n`.

```bash
$ npm install -g n
$ n 6.10.3
```

This is very easy to use. then you can show your node version:

```bash
$ node -v
v6.10.3
```

The available node versions can be found on Node's [release page](https://nodejs.org/en/about/releases/)

For windows [nvm](https://github.com/coreybutler/nvm-windows) is a well-received tool.

### "npm install" Error

#### GitHub Operation Times Out

    git config --global url."https://".insteadOf git://

This will change all of your urls so that they all start with "https://" which shall be working for you.

#### node-sass Version Issue

Running `npm install` gives

```
.node-gyp/18.7.0/include/node/v8-internal.h:646:38: error: no template named 'remove_cv_t' in namespace 'std'; did you mean 'remove_cv'?
```

What you're seeing is an error during compilation of node-sass. That's a package processing your Sass/SCSS styles, which 
is written in C++ and only re-packaged as a JavaScript library. The fact it's written in C++ means it needs to be
compiled on your device during installation (this is internally done by a tool called node-gyp, which you can spot in
your error output, too).

**The problem is node-sass with the specified version in package.json doesn't support Node version installed on the 
machine**. The [node-sass community](https://github.com/sass/node-sass) needs time to catch up to support it (and that's 
fair, as it's a volunteer-driven project).

Case-by-case soulutions would be either upgrading sass versions or [downgrading Node](#change-node-version)
