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

#### Spread Operator

The JavaScript **spread operator** (`...`) allows us to quickly copy all or part of an existing array or object into 
another array or object.

{% highlight react %}
const numbersOne = [1, 2, 3];
const numbersTwo = [4, 5, 6];
const numbersCombined = [...numbersOne, ...numbersTwo]; // numbersCombined = [1,2,3,4,5,6]
{% endhighlight %}

The spread operator is often used in combination with destructuring.

{% highlight react %}
const numbers = [1, 2, 3, 4, 5, 6];

const [one, two, ...rest] = numbers; // one = 1; two = 2; rest = [3, 4, 5, 6]
{% endhighlight %}

We can use the spread operator with objects too:

{% highlight react %}
const myVehicle = {
    brand: 'Ford',
    model: 'Mustang',
    color: 'red'
}

const updateMyVehicle = {
    type: 'car',
    year: 2021,
    color: 'yellow'
}

const myUpdatedVehicle = {...myVehicle, ...updateMyVehicle}
{% endhighlight %}

The resulting `myUpdatedVehicle` contains

```json
{
    "brand": "Ford",
    "model": "Mustang",
    "color": "yellow",
    "type": "car",
    "year": 2021
}
```

#### Modules

JaveScript modules allow us to break up code into separate files. This makes it easier to maitain the code-base.

##### Export

We can export a function or variable from any file through either named export or default export.

We can create named export in two ways:

* In-line

  {% highlight react %}
  export const name = "Jesse"
  export const age = 40
  {% endhighlight %}

* All at once at the bottom of a .js file

  {% highlight react %}
  const name = "Jesse"
  const age = 40

  export { name, age }
  {% endhighlight %}

We can, however, have only one default export in a .js file

{% highlight react %}
const message = () => {
    const name = "Jesse";
    const age = 40;
    return name + ' is ' + age + 'years old.';
};

export default message;
{% endhighlight %}

##### Import

We can import modules into a file in two ways, based on if they are named exports or default exports. _Named exports
must be destructured using curly braces. Default exports do not_.

Import from named exports

{% highlight react %}
import { name, age } from "./person.js";
{% endhighlight %}

Import from default exports

{% highlight react %}
import message from "./message.js";
{% endhighlight %}

> The "default" export gets its name because, unlike named exports, we do not need to destructure it

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

Here we are using the [`useState` Hook](#usestate-hook) to keep track of the application state.

#### useState Hook

The **useState** Hook enables us to track state in a function component.

##### Import useState

To use the useState Hook, we first need to import it into a component

{% highlight react %}
import { useState } from "react";
{% endhighlight %}

> Note that we are [destructuring](#destructuring) useState from react as it is a [named export](#export).

##### Initialize useState

We initialize our state by calling `useState` in our function component. `useState` accepts an initial state and returns
two values:

1. the current state, and
2. a function that updates the state

For example, to initialize state at the top of the function component:

{% highlight react %}
import { useState } from "react";

function FavoriteColor() {
    const [color, setColor] = useState("");
}
{% endhighlight %}

Again, we are [destructuring](#destructuring) the returned values from `useState`. The first value, `color`, is our
current state. The second value, setColor, is the function that is use dot update our state. 

> These variables can be named anything we would like

In the example above, we set the initial state to an empty string using `useState("")`

##### Read State

We can now refer our state anywhere in our component

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";

function FavoriteColor() {
    const [color, setColor] = useState("red");
    
    return <h1>My favorite color is {color}!</h1>
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<FavoriteColor />);
{% endhighlight %}

##### Update State

To update our state, we use our state updater function.

> ⚠️ We should never directly update state. Ex: `color = "red"` is not allowed.

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";

function FavoriteColor() {
    const [color, setColor] = useState("red");
    
    return (
        <>
            <h1>My favorite color is {color}!</h1>
            <button type="button" onClick={() => setColor("blue")}>Blue</button>
        </>
    )
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<FavoriteColor />);
{% endhighlight %}

##### What Can State Hold

The `useState` Hook can be used to keep track of strings, numbers, booleans, arrays, objects, and any combination of
these. We also could create multiple state Hooks to track individual values. For example

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";

function Car() {
    const [brand, setBrand] = useState("Ford");
    const [model, setModel] = useState("Mustang");
    const [year, setYear] = useState("1964");
    const [color, setColor] = useState("red");
    
    return (
        <>
        <h1>My {brand}</h1>
        <p>
            It is a {color} {model} from {year}.
        </p>
        </>
    )
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

Or, we can just use one state and include an object instead

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";

function Car() {
    const [car, setCar] = useState({
        brand: "Ford",
        model: "Mustang",
        year: "1964",
        color: "red"
    });
    
    return (
        <>
            <h1>My {car.brand}</h1>
            <p>
                It is a {car.color} {car.model} from {car.year}.
            </p>
        </>
    )
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

> Since in this case we are now tracking a single object, we need to reference that object and then the property of that 
> object when rendering the component. (e.g. `car.brand`)

##### Updating Objects and Arrays in State

It should be noted that when state is updated, the entire state gets overwritten. When if we would like to update
part of the states? For example, if we only want to update the car color and only called `setCar({color: "blue"})`,
this would remove the brand, model, and year from our state.

Instead, we can use the JavaScript [spread operator](#spread-operator) to help us.

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";

function Car() {
    const [car, setCar] = useState({
        brand: "Ford",
        model: "Mustang",
        year: "1964",
        color: "red"
    });

    const updateColor = () => {
        setCar(previousState => {
            return { ...previousState, color: "blue" }
        });
    }

    return (
        <>
            <h1>My {car.brand}</h1>
            <p>
                It is a {car.color} {car.model} from {car.year}.
            </p>
            <button type="button" onClick={updateColor}>Blue</button>
        </>
    )
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Car />);
{% endhighlight %}

#### useEffect Hook

The **useEffect** Hook allows you to perform **side effects** in your components. Some examples of side effects are: 

* fetching data
* directly updating the DOM
* timers.

`useEffect` accepts two arguments: **`useEffect(<function>, <dependency>)`**, where the second argument is optional.

Let's use timer as an example:

{% highlight react %}
import { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";

function Timer() {
    const [count, setCount] = useState(0);
    
    useEffect(() => {
        setTimeout(() => { setCount((count) => count + 1); }, 1000);
    });
    
    return <h1>I've rendered {count} times!</h1>;
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Timer />);
{% endhighlight %}

**`useEffect` runs on every render**. That means that when the count changes, a render happens, which then triggers 
another effect. This is not what we want, because it keeps counting forever even though it should only count once. i.e. 
the following

{% highlight react %}
useEffect(() => { //Runs on every render });
{% endhighlight %}

There are several ways to control when side effects run:

{% highlight react %}
useEffect(() => { //Runs only on the first render }, []);
{% endhighlight %}

{% highlight react %}
useEffect(() => { //Runs on the first render or on any time any dependency value changes }, [prop, state]);
{% endhighlight %}

##### Effect Cleanup

Some effects require cleanup to reduce memory leaks. Timeouts, subscriptions, event listeners, and other effects that
are no longer needed should be disposed. We do this by including a return function at the end of the `useEffect` Hook:

{% highlight react %}
useEffect(() => {
    let timer = setTimeout(() => { setCount((count) => count + 1); }, 1000);
    return () => clearTimeout(timer)
}, []);
{% endhighlight %}

#### useContext Hook

React **Context** is a way of managing state globally. It can be used together with the [useState Hook](#usestate-hook)
to share states between deeply nested components more easily

##### The Prop Drilling Problem

_State should be held by the highest parent component in the stack that requires access to the state._ When we have
deeply nested components and the component at the bottom of the stack need access to a state from the top component,
we will, without Context, need to pass the state as "props" through each nested component. This is called "**prop 
drilling**"

The problem with that is even though the "middle" components do not need the state, they still have to pass the state
along in order for the state to reach the bottom component. 

##### The Solution - Create Context

To create context, you must import **createContext** and initialize it:

{% highlight react %}
import { useState, createContext } from "react";
import ReactDOM from "react-dom/client";

const UserContext = createContext()
{% endhighlight %}

Next, we shall use a **Context Provider** to wrap the tree of components that need the state context. 

{% highlight react %}
function Component1() {
    const [user, setUser] = useState("Jesse Hall");
    
    return (
        <UserContext.Provider value={user}>
            <h1>{`Hello ${user}!`}</h1>
            <Component2 user={user} />
        </UserContext.Provider>
    );
}
{% endhighlight %}

Now, all components in this tree will have access to the user Context. 

###### Use Context

In order to use the Context in a child component, we need to access it using the **useContext** Hook. First, include
the `useContext` in the import statement:

{% highlight react %}
import { useState, createContext, useContext } from "react";
{% endhighlight %}

Then we can access the user Context in all components:

{% highlight react %}
function Component5() {
    const user = useContext(UserContext);
    
    return (
        <>
        <h1>Component 5</h1>
        <h2>{`Hello ${user} again!`}</h2>
        </>
    );
}
{% endhighlight %}

#### useRef Hook

The **useRef** Hook allows you to persist values between renders while they do not cause a re-render when updated.

In the [previous section](#useeffect-hook), if we tried to count how many times our application renders, we would be
caught in an infinite loop since the Hook there causes a re-render. To avoid this with `useRef` Hook:

{% highlight react %}
import { useState, useEffect, useRef } from "react";
import ReactDOM from "react-dom/client";

function App() {
    const [inputValue, setInputValue] = useState("");
    const count = useRef(0);
    
    useEffect(() => {
        count.current = count.current + 1;
    });
    
    return (
        <>
        <input type="text" value={inputValue} onChange={(e) => setInputValue(e.target.value)} />
        <h1>Render Count: {count.current}</h1>
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
{% endhighlight %}

When we initialize `useRef` we set the initial value: `useRef(0)`. `useRef()` only returns one Object called current.
It's like having `const count = {current: 0}`. We can access the count by using `count.current`.

##### Tracking State Changes

`useRef` Hook can also be used to track previous state values. This is because we are able to persist values between
renders with it. For example: 

{% highlight react %}
import { useState, useEffect, useRef } from "react";
import ReactDOM from "react-dom/client";

function App() {
    const [inputValue, setInputValue] = useState("");
    const previousInputValue = useRef("");
    
    useEffect(() => { previousInputValue.current = inputValue; }, [inputValue]);
    
    return (
        <>
            <input type="text" value={inputValue} onChange={(event) => setInputValue(event.target.value)}/>
            <h2>Current Value: {inputValue}</h2>
            <h2>Previous Value: {previousInputValue.current}</h2>
        </>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
{% endhighlight %}

#### useReducer Hook

The **useReducer** Hook is an alternative to [useState](#usestate-hook) and allows for custom state logic. It is usually 
preferable to [useState](#usestate-hook) when you have complex state logic that involves multiple sub-values or when the 
next state depends on the previous one. `useReducer` also lets you optimize performance for components that trigger deep 
updates because you can _pass dispatch down instead of callbacks_.

The hook signature is `const [state, dispatch] = useReducer(reducer, initialArg, init);`.

It accepts a reducer of type `(state, action) => newState`, and returns the current state paired with a **dispatch
method**.

> **How to avoid passing callbacks down?**
> 
> It has been found that most people don't enjoy manually passing callbacks through every level of a component tree.
> Even though it is more explicit, it can feel like a lot of "plumbing".
> 
> In large component trees, an alternative we recommend is to pass down a dispatch function from `useReducer` via
> context:
> 
> ```javascript
> const TodosDispatch = React.createContext(null);
> 
> function TodosApp() {
>     // Note: `dispatch` won't change between re-renders
>     const [todos, dispatch] = useReducer(todosReducer);
> 
>     return (
>         <TodosDispatch.Provider value={dispatch}>
>             <DeepTree todos={todos} />
>         </TodosDispatch.Provider>
>     );
> }
> ```
> 
> Any child in the tree inside `TodosApp` can use the dispatch function to pass actions up to `TodosApp`:
>
> ```javadcript
> function DeepChild(props) {
>     // If we want to perform an action, we can get dispatch from context.
>     const dispatch = useContext(TodosDispatch);
>     
>     function handleClick() {
>         dispatch({ type: 'add', text: 'hello' });
>     }
>     
>     return (
>         <button onClick={handleClick}>Add todo</button>
>     );
> }
> ```

Here is a "counter" example using reducer:

{% highlight react %}
const initialState = {count: 0};

function reducer(state, action) {
    switch (action.type) {
        case 'increment':
            return {count: state.count + 1};
        case 'decrement':
            return {count: state.count - 1};
        default:
            throw new Error();
    }
}

function Counter() {
    const [state, dispatch] = useReducer(reducer, initialState);

    return (
        <>
            Count: {state.count}
            <button onClick={() => dispatch({type: 'decrement'})}>-</button>
            <button onClick={() => dispatch({type: 'increment'})}>+</button>
        </>
    );
}
{% endhighlight %}

##### Lazy Initialization

You can also create the initial state lazily. To do this, you can pass an **init function** as the third argument. The 
initial state will be set to `init(initialArg)`.

It lets you extract the logic for calculating the initial state outside the reducer. This is also handy for resetting
the state later in response to an action:

{% highlight react %}
function init(initialCount) {
    return {count: initialCount};
}

function reducer(state, action) {
    switch (action.type) {
        case 'increment':
            return {count: state.count + 1};
        case 'decrement':
            return {count: state.count - 1};
        case 'reset':
            return init(action.payload);
        default:
            throw new Error();
    }
}

function Counter({initialCount}) {
    const [state, dispatch] = useReducer(reducer, initialCount, init);

    return (
        <>
        Count: {state.count}

        <button onClick={() => dispatch({type: 'reset', payload: initialCount})}>Reset</button>

        <button onClick={() => dispatch({type: 'decrement'})}>-</button>
        <button onClick={() => dispatch({type: 'increment'})}>+</button>
        </>
    );
}
{% endhighlight %}

##### Bailing Out of A Dispatch

If you return the same value from a Reducer Hook as the current state, React will bail out without rendering the
children or firing effects. (React uses the
[Object.is comparison algorithm](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/is#Description).)

Note that React may still need to render that specific component again before bailing out. That shouldn't be a concern 
because React won't unnecessarily go "deeper" into the tree. If you're doing expensive calculations while rendering, you 
can optimize them with [useMemo](#usememo-hook).

#### useCallback Hook

Let's look at a problematic TODO app first, which shall help us see why useCallback exists

{% highlight react %}
import { useState } from "react";
import ReactDOM from "react-dom/client";
import Todos from "./Todos"; // see below

const App = () => {
    const [count, setCount] = useState(0);
    const [todos, setTodos] = useState([]);

    const increment = () => { setCount((c) => c + 1); };

    const addTodo = () => { setTodos((t) => [...t, "New Todo"]); };

    return (
        <>
            <Todos todos={todos} addTodo={addTodo} />
            <hr />
            <div>
                Count: {count}
                <button onClick={increment}>+</button>
            </div>
        </>
    );
};

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
{% endhighlight %}

The definition of `Todos.js` used above:

{% highlight react %}
import { memo } from "react";

const Todos = ({ todos, addTodo }) => {
    console.log("child render");

    return (
        <>
        <h2>My Todos</h2>

        {todos.map((todo, index) => { return <p key={index}>{todo}</p>; })}

        <button onClick={addTodo}>Add Todo</button>
        </>
    );
};

export default memo(Todos);
{% endhighlight %}

> Note that we use the
> [`map()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map) function to take
> an array of todo's and [mount](#what-is-mounting-in-react-js) each of them iteratively

In this example, we might think that the `Todos` component will not re-render unless the `todos` change. When we try 
running this and click the count increment button, we will notice that the `Todos` component re-renders even when the 
`todos` do not change.

Notice that we are using memo (see below), so the `Todos` component should not re-render since neither the _todos_ state 
nor the `addTodo` function are changing when the count is incremented.

This is because _every time a component re-renders, its functions get recreated_. Because of this, the `addTodo`
function has actually changed.

> **React.memo**
> 
> ```javascript
> const MyComponent = React.memo(function MyComponent(props) {
>     /* render using props */
> });
> ```
> 
> `React.memo` is a higher order component for better page performance. If our component renders the same result given
> the same props, we can wrap it in a call to `React.memo` for a performance boost in some cases by memoizing the
> result. This means that React will skip rendering the component, and reuse the last rendered result.
> 
> Note that `React.memo` only checks for prop changes. If your function component wrapped in `React.memo` has
> [useState](#usestate-hook), [userReducer](#usereducer-hook), or [useContext](#usecontext-hook) Hook in its
> implementation, it will still render when state or context change.
> 
> **This method only exists as a [performance optimization](#performance-optimization). Do not rely on it to "prevent" a
> render, as this can lead to bugs.**

To fix the problem above, we can use the `useCallback` hook to prevent the function from being recreated unless
necessary. To do that, we simply change the implementation of `addTodo` function: 

{% highlight react %}
const addTodo = useCallback(() => {
    setTodos((t) => [...t, "New Todo"]);
}, [todos]);
{% endhighlight %}

Now the `Todos` component will only re-render when the `todos` prop changes.

#### useMemo Hook

The useMemo and [useCallback](#usecallback-hook) are similar. The main difference is that `useMemo` returns a memoized
_value_ while `useCallback` returns a memoized _function_.

The `useMemo` Hook can be used to keep expensive, resource intensive functions from needlessly running. For example,
suppose we have a computation-intensive function of

{% highlight react %}
const result = expensiveCalculation(count);
{% endhighlight %}

This function runs on every render. But when we have `useMemo` Hook to memoize the `expensiveCalculation`, the function
only runs when needed:

{% highlight react %}
import { useMemo } from "react";

...

const result = useMemo(() => expensiveCalculation(count), [count]);
{% endhighlight %}

In the example above, the expensive function will only run when `count` is changed

#### Custom Hook

Hooks are reusable functions. When we have component logic that needs to be shared by multiple components, we can
extract that logic to make a custom Hook. **Custom Hooks function start with "use" in its name**. For example,
`useFetch`.

In the following example, we are fetching data in our `Home` component and displaying it.

> * We will use the [JSONPlaceholder](https://jsonplaceholder.typicode.com/) service to fetch fake data. This service is 
>   great for testing applications when there is no existing data.
> * We utilize[ JavaScript Fetch API](https://www.w3schools.com/js/js_api_fetch.asp) to make HTTP request for JSON.

{% highlight react %}
import { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";

const Home = () => {
    const [data, setData] = useState(null);
    
    useEffect(() => {
        fetch("https://jsonplaceholder.typicode.com/todos")
                .then((res) => res.json())
                .then((data) => setData(data));
    }, []);
    
    return (
        <>
            {data && data.map((item) => { return <p key={item.id}>{item.title}</p>; })}
        </>
    );
};

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Home />);
{% endhighlight %}

The fetch logic may be needed in other components as well, so we will extract that into a custom Hook by moving the
logic to a new file called "useFetch.js"

{% highlight react %}
import { useState, useEffect } from "react";

const useFetch = (url) => {
    const [data, setData] = useState(null);

    useEffect(() => {
        fetch(url)
                .then((res) => res.json())
                .then((data) => setData(data));
    }, [url]);
    
    return [data];
};

export default useFetch;
{% endhighlight %}

Now the logic can be imported as `useFetch` Hook:

{% highlight react %}
import ReactDOM from "react-dom/client";
import useFetch from "./useFetch";

const Home = () => {
    const [data] = useFetch("https://jsonplaceholder.typicode.com/todos");

    return (
        <>
            {data && data.map((item) => { return <p key={item.id}>{item.title}</p>; })}
        </>
    );
};

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<Home />);
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

### Higher-Order Components

A **higher-order component** (**HOC**) is an advanced technique in React for reusing component logic. HOCs are not part 
of the React API, per se. They are a pattern that emerges from React's compositional nature.

**A higher-order component is, namely, a function that takes a component and returns a new component.**

{% highlight react %}
const EnhancedComponent = higherOrderComponent(WrappedComponent);
{% endhighlight %}

HOCs are common in third-party React libraries.

### React Top-Level API

#### Performance Optimization

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

#### What is "Mounting" in React js?

The main job of React is to figure out how to modify the DOM to match what the components want to be rendered on the
screen. React does so by "mounting" (adding nodes to the DOM), "unmounting" (removing them from the DOM), and "updating" 
(making changes to nodes already in the DOM).

How a React tnode is represented as a DOM node and where and when it appears in the DOM tree is managed by the
[top-level API](#react-top-level-api). To get a better idea about what's going on, let's look at the most simple example
possible:

{% highlight react %}
// JSX version: let foo = <FooComponent />;
let foo = React.createElement(FooComponent);
{% endhighlight %}

What is `foo` and what can you do with it? `Foo`, at the moment, is a plain JavaScript object that looks roughly like
this (simplified): 

{% highlight react %}
{
    type: FooComponent,
    props: {}
}
{% endhighlight %}

It's currently not anywhere on a webpage, i.e. it is not a DOM element, doesn't exist anywhere in the DOM tree and,
aside from being React element node, has no other meaningful representation in the document. It just tells React what
needs to be on the screen if this React element gets rendered. It's not "mounted" yet.

We can tell React to "mount" it into a DOM container by calling:

{% highlight react %}
ReactDOM.render(foo, domContainer);
{% endhighlight %}

This tells React it's time to show `foo` on the web page. React will create an instance of the `FooComponent` class and
call its `render` method. Let's say it renders a `<div />`; in that case React will create a `div` DOM node for it, and
insert it into the DOM container.

**This process of creating instances and DOM nodes corresponding to React components, and inserting them into the DOM,
is called mounting**

Note that normally we'd only call `ReactDOM.render()` to mount the root component(s). We do not need to manually "mount"
the child components. 

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

#### Image Path in css for React.js Project

Suppose we have a project folder structure with top-level directories: **node_modules**, **public**, **src**.

The css file is in public folder **public/styles.css** and images are in **src/images/icon.png**

What should be the correct image path for this image for any class in css file which kept in public folder in localhost 
eg: `.icon { background: url(../src/images/icon.png) no-repeat left top;`

Answer. This file path points to a file in the images folder located at the root of the current web

{% highlight react %}
.icon { background: url(/src/images/icon.png) no-repeat left top;}
{% endhighlight %}

This file path points to a file in the images folder located in the current folder.

{% highlight react %}
.icon { background: url(src/images/icon.png) no-repeat left top;}
{% endhighlight %}

This file path points to a file in the images folder located in the folder one level above the current folder.

{% highlight react %}
.icon { background: url(../src/images/icon.png) no-repeat left top;}
{% endhighlight %}

#### Switch Node.js Versions with NVM

##### What is Node Version Manager (NVM)?

[Node Version Manager](https://github.com/nvm-sh/nvm) is a tool that helps us manage Node versions and is a convenient 
way to install Node. Think of it as npm or Yarn that helps manage Node packages, but instead of packages, NVM manages 
Node versions.

This also means you can install multiple Node versions onto your machine at the same time and switch among them if
needed.

##### Displaying a List of Node.js Versions

We can now view all the versions we downloaded so far with 

```bash
nvm ls
```

The list then appears:

![Error loading node-versions.png]({{ "/assets/img/node-versions.png" | relative_url}})

The first three lines show the list of Node versions with the arrow pointing to the 14.18.1 version that is currently in 
use; when a version is used, it displays as green.

##### Switching Among Node.js Versions

The best feature about NVM is the ability to easily switch between different Node versions. Say we must use version 
16.13.0 and then switch to 12.22.7; we can simply run either `nvm use 12.22.7` or `nvm use 16.13.0` to easily switch
into either version we need.

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