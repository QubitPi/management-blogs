---
layout: post
title: React - The GraphQL Frontend
tags: [React, JavaScript]
category: FINALIZED
color: rgb(4, 170, 109)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

[React](https://reactjs.org/), sometimes referred to as a frontend JavaScript framework, is a JavaScript library created 
by Facebook. React is a tool for building UI components.

[Relay](#relay-baics) is a JavaScript framework for fetching and managing GraphQL data in React applications that 
emphasizes maintainability, type safety and runtime performance. Relay achieves this by combining declarative data 
fetching and a static build step. With declarative data fetching, components declare what data they need, and Relay 
figures out how to efficiently fetch it. During the static build step, Relay validates and optimizes queries, and 
pre-computes artifacts to achieve faster runtime performance.

* TOC
{:toc}


React Basics
------------

Instead of manipulating the browser's DOM directly, React creates a virtual DOM in memory, where it does all the
necessary manipulating, before making the changes in the browser DOM.

### React Render HTML

React's goal is in many ways to render HTML in a web page by using a function called **ReactDOM.render()**.

#### The Render Function

The `ReactDOM.render()` function takes two arguments

1. HTML code
2. an HTML element

The purpose of the function is to display the specified HTML code inside the specified HTML element.

But render where?

There is another folder in the root directory of your React project, named "public". In this folder, there is an
**index.html** file.

You'll notice a single `<div>` in the body of this file. This is where our React application will be rendered.

### React ES6

ES6 stands for ECMAScript 6. **ECMAScript** was created to standardize JavaScript, and ES6 is the 6th version of
ECMAScript. It is also known as ECMAScript 2015 since it was published in 2015. We discuss ES6 here because React uses
ES6.

#### Destructuring

To illustrate destructuring, let's make a sandwich. Do you take everything out of the refrigerator to make your
sandwich? No, you only take out the items you would like to use on your sandwich.

Destructuring is exactly the same. We may have an array or object we are working with, but we only need some of the
items contained in these. Destructuring makes it easy to extract only what is needed.

##### Destructuring Arrays

Here is the old-fashioned way of assigning array items to variables:

{% highlight react %}
const vehicles = ['mustang', 'f-150', 'expedition'];

// old way
const car = vehicles[0];
const truck = vehicles[1];
const suv = vehicles[2];
{% endhighlight %}

With destructuring, we can write it as

{% highlight react %}
const vehicles = ['mustang', 'f-150', 'expedition'];

const [car, truck, suv] = vehicles;
{% endhighlight %}

> When destructuring array, the order that variables are declared is important

If we want the car and SUV only we can simply leave out the truck but keep the comma:

{% highlight react %}
const vehicles = ['mustang', 'f-150', 'expedition'];

const [car,, suv] = vehicles;
{% endhighlight %}

Destructuring comes in handy when a function returns an array:

{% highlight react %}
function calculate(a, b) {
const add = a + b;
const subtract = a - b;
const multiply = a * b;
const divide = a / b;

return [add, subtract, multiply, divide];
}

const [add, subtract, multiply, divide] = calculate(4, 7);
{% endhighlight %}

##### Destructuring Objects

Here is the old way of using an object inside a function:

{% highlight react %}
const vehicleOne = {
    brand: 'Ford',
    model: 'Mustang',
    type: 'car',
    year: 2021,
    color: 'red'
}

myVehicle(vehicleOne);

// old way
function myVehicle(vehicle) {
    const message = 'My ' + vehicle.type + ' is a ' + vehicle.color + ' ' + vehicle.brand + ' ' + vehicle.model + '.';
}
{% endhighlight %}

With destructuring

{% highlight react %}
const vehicleOne = {
    brand: 'Ford',
    model: 'Mustang',
    type: 'car',
    year: 2021,
    color: 'red'
}

myVehicle(vehicleOne);

function myVehicle({type, color, brand, model}) {
    const message = 'My ' + type + ' is a ' + color + ' ' + brand + ' ' + model + '.';
}
{% endhighlight %}

> 📋 The object properties do not have to be declared in a specific order.

We can even destructure deeply nested objects by referencing the nested object then using a colon and curly braces to 
again destructure the items needed from the nested object:

{% highlight react %}
const vehicleOne = {
    brand: 'Ford',
    model: 'Mustang',
    type: 'car',
    year: 2021,
    color: 'red',
    registration: {
        city: 'Houston',
        state: 'Texas',
        country: 'USA'
    }
}

myVehicle(vehicleOne)

function myVehicle({ model, registration: { state } }) {
    const message = 'My ' + model + ' is registered in ' + state + '.';
}
{% endhighlight %}

#### Modules

JaveScript modules allow us to break up code into separate files. This makes it easier to maitain the code-base.

#### Export

We can export a function or variable from any file through either named export or default export.

We can create named export in two ways:

1. In-line
   {% highlight react %}
   export const name = "Jesse"
   export const age = 40
   {% endhighlight %}
2. All at once at the bottom of a .js file
   {% highlight react %}
   const name = "Jesse"
   const age = 40

   export { name, age }
   {% endhighlight %}

### React Components

> **React is all about re-using code, which is why component is a very crucial concept in this context**

Components are **independent and reusable bits of code**. They serve the same purpose as JavaScript functions, but work
in isolation and return HTML.

Components come in two types, Class components and Function components. Class components are rarely used today. We will,
therefore, focus on the other throughout this post.

#### Create a Component

A component's name MUST start with an upper case letter. A Function component also returns HTML. For example, let's
create a Function component called "Car"

{% highlight react %}
function Car() {
    return <h2>Hi, I am a Car!</h2>;
}
{% endhighlight %}

#### Rendering a Component

Now our React application has a component called `Car`, which returns an `<h2>` element. To use this component
application, use similar syntax as normal HTML: `<Car />`

{% highlight react %}
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

#### Importing a Component

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

#### Props

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

##### Passing Data

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

#### React State

> ⚠️ In order React code bases, class components were primarily used. The React state was initially intended to be used
> in class components. Since React 16.8, it was suggested to use function components along with [Hooks](#react-hooks).
> Thus this section serves as preliminary concept discussion with no expected hands-on applications

[React components](#react-components) has a built-in **state object**, where we store property values that _belongs to
the component_. When the state object changes, the component re-renders.

The state object is initialized in the constructor:

{% highlight react %}
class Car extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            brand: "Ford",
            model: "Mustang",
            color: "red",
            year: 1964
        };
    }

    render() {
        return (
            <div>
                <h1>My Car</h1>
            </div>
        );
    }
}
{% endhighlight %}

Refer to the state object anywhere in the component by using `this.state.propertyName` syntax:

{% highlight react %}
<h1>My {this.state.brand}</h1>
{% endhighlight %}

##### Creating the state Object

The state object is initialized in constructor:

### React Hooks

Hooks allow function components to have access to state and other React features. Because of this, class components are
generally no longer needed.

> Hook Rules:
> 
> * Hooks can only be called inside React function components
> * Hooks can only be called at the top level of a component
> * Hooks cannot be conditional

To use Hooks, we must import them from react first. For example

{% highlight react %}
import React, { useState } from "react";
{% endhighlight %}

Here er are using the [`useState` Hook](#usestate-hook) to keep track of the application state.

#### useState Hook

The **useState** Hook enables us to track state in a function component.

#### Import useState Hook

To use the useState Hook, we first need to import it into a component

{% highlight react %}
import { useState } from "react";
{% endhighlight %}



### React JSX

Consider this variable declaration:

{% highlight react %}
const element = <h1>Hello, world!</h1>;
{% endhighlight %}

This funny tag syntax is neither a string nor HTML.

It is called **JSX**, and it is a syntax extension to JavaScript. It is recommended to use it with React to describe
what the UI should look like. JSX may remind us of a template language, but it comes with the full power of JavaScript.

#### Why JSX?

React embraces the fact that _rendering logic is inherently coupled with other UI logic_: how events are handled, how the
state changes over time, and how the data is prepared for display.

Instead of artificially separating technologies by putting markup and logic in separate files, React separates concerns 
with loosely coupled units, i.e. "[components](#react-components)" that contain both.

React doesn't require using JSX, but most people find it helpful as a visual aid when working with UI inside the 
JavaScript code. It also allows React to show more useful error and warning messages.

#### Embedding Expressions in JSX

In the example below, we declare a variable called "name" and then use it inside JSX by wrapping it in curly braces:

{% highlight react %}
const name = 'Josh Perez';
const element = <h1>Hello, {name}</h1>;
{% endhighlight %}

You can put any valid JavaScript expression inside the curly braces in JSX. For example, `2 + 2`, `user.firstName`, or 
`formatName(user)` are all valid JavaScript expressions. In the example below, we embed the result of calling a
JavaScript function, `formatName(user)`, into an `<h1>` element.

{% highlight react %}
function formatName(user) {
    return user.firstName + ' ' + user.lastName;
}

const user = {
    firstName: 'Harper',
    lastName: 'Perez'
};

const element = (
    <h1>Hello, {formatName(user)}!</h1>
);
{% endhighlight %}

After compilation, JSX expressions become regular JavaScript function calls and evaluate to JavaScript objects.

This means that you can use JSX inside of if statements and for loops, assign it to variables, accept it as arguments,
and return it from functions. For example

{% highlight react %}
function getGreeting(user) {
    if (user) {
        return <h1>Hello, {formatName(user)}!</h1>;
    }
    return <h1>Hello, Stranger.</h1>;
}
{% endhighlight %}

#### Specifying Attributes with JSX

You may use quotes to specify string literals as attributes:

{% highlight react %}
const element = <a href="https://www.reactjs.org"> link </a>;
{% endhighlight %}

You may also use curly braces to embed a JavaScript expression in an attribute:

{% highlight react %}
const element = <img src={user.avatarUrl}></img>;
{% endhighlight %}

> ⚠️ Don't put quotes around curly braces when embedding a JavaScript expression in an attribute. You should either use 
> quotes (for string values) or curly braces (for expressions), but not both in the same attribute.
> 
> In addition, since JSX is closer to JavaScript than to HTML, React DOM uses camelCase property naming convention
> instead of HTML attribute names. For example, `class` becomes `className` in JSX, and `tabindex` becomes `tabIndex`.

#### Specifying Children with JSX

If a tag is empty, you may close it immediately with `/>`, like XML:

{% highlight react %}
const element = <img src={user.avatarUrl} />;
{% endhighlight %}

JSX tags may contain children:

{% highlight react %}
const element = (
    <div>
        <h1>Hello!</h1>
        <h2>Good to see you here.</h2>
    </div>
);
{% endhighlight %}

#### JSX Prevents Injection Attacks

It is safe to embed user input in JSX:

{% highlight react %}
const title = response.potentiallyMaliciousInput;
// This is safe:
const element = <h1>{title}</h1>;
{% endhighlight %}

By default, React DOM [escapes](https://stackoverflow.com/questions/7381974/which-characters-need-to-be-escaped-on-html) 
any values embedded in JSX before rendering them. Thus it ensures that you can never inject anything that's not
explicitly written in your application. Everything is converted to a string before being rendered. This helps prevent
[XSS (cross-site-scripting)](https://en.wikipedia.org/wiki/Cross-site_scripting) attacks.

#### JSX Represents Objects

[Babel](https://babeljs.io/) compiles JSX down to `React.createElement()` calls.

These two examples are identical:

<table>
<tr>
<th>JSX</th>
<th>React</th>
</tr>
<tr>
<td>

{% highlight react %}
const element = (
    <h1 className="greeting">
        Hello, world!
    </h1>
);
{% endhighlight %}

</td>
<td>

{% highlight react %}
const element = React.createElement(
    'h1',
    {className: 'greeting'},
    'Hello, world!'
);
{% endhighlight %}

</td>
</tr>
</table>

`React.createElement()` performs a few checks to help you write bug-free code but essentially it creates an object like 
this:

{% highlight react %}
// Note: this structure is simplified
const element = {
    type: 'h1',
    props: {
        className: 'greeting',
        children: 'Hello, world!'
    }
};
{% endhighlight %}

These objects are called "React elements". You can think of them as descriptions of what you want to see on the screen. 
React reads these objects and uses them to construct the DOM and keep it up to date.

### React Events

Just like HTML DOM events, React can perform actions based on user events. React has the same events as HTML: click, 
change, mouseover etc.

#### Adding Events

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

#### Passing Arguments

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

#### React Event Object

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

### Conditional Rendering

In React, you can conditionally render components. There are several ways to do this.

#### "if" Statement

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

#### Logical "&&" Operator

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

#### Ternary Operator

Another way to conditionally render elements is by using a ternary operator.

condition ? true : false

### TypeScript

[TypeScript](https://www.typescriptlang.org/) is a programming language developed by Microsoft. It is a typed superset
of JavaScript, and includes its own compiler. Being a typed language, TypeScript can catch errors and bugs at build
time, long before your app goes live.

#### File Extensions

In React, you most likely write your components in a **.js** file. In TypeScript we have 2 file extensions:

1. **.ts** is the default file extension
2. **.tsx** is a special extension used for files which contain JSX.

> `.ts` file extension is used when you are creating functions, classes, reducers, etc. that do not require the use of
> JSX syntax and elements, whereas the `.tsx` file extension is used when you create a React component and use JSX 
> elements and syntax.

### FAQ

#### What is "package-lock.json"?

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

### Troubleshooting

#### Change Node Version

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

#### "npm install" Error

##### GitHub Operation Times Out

    git config --global url."https://".insteadOf git://

This will change all of your urls so that they all start with "https://" which shall be working for you.

##### node-sass Version Issue

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


Relay Baics
-----------

Relay is a framework for managing and declaratively fetching GraphQL data. It allows developers to declare what data
each component needs via GraphQL, and then aggregate these dependencies and efficiently fetch the data in fewer round 
trips. In this section we'll introduce the key concepts for using Relay in a React app one at a time.