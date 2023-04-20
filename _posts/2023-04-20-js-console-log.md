---
layout: post
title: Javascript - Template Strings Don't Pretty Print Objects
tags: [JavaScript]
color: rgb(4, 170, 109)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

When we want to use ES6 template strings to pretty print javascript objects, we can do through

```javascript
const description = 'App opened';
const properties = { key1: 'val1', blah: 123 };
console.log('Description: ', description, '. Properties: ', properties);
```

outputs

![Error loading javascript-console-log-correct-output.png]({{ "/assets/img/javascript-console-log-correct-output.png" | relative_url}})

When we attempt to do the same thing with
[template string](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals), however

```javascript
// Same description and properties
const logString = `Description: ${description}. Properties: ${properties}`;
console.log(logString);
```

we get

![Error loading javascript-console-log-wrong-output.png]({{ "/assets/img/javascript-console-log-wrong-output.png" | relative_url}})

This is because our first example does not actually output a string to the console. Notice how `properties` is passed as
a separate parameter argument (as it is surrounded by commas `,` and not string-concatenation operators `+`).

When we pass an object (or any JavaScript value) to console as a discrete argument it can display it how it wishes -
including as an interactive formatted display, which it does in our first example.

In our second example, we're using templated-strings, but it's (generally) equivalent to this:

```javascript
logString = "Description: " + description.toString() + ". Properties: " + properties.toString()";
```

And `Object.prototype.toString()` returns `"[object Object]"` by default. Note that this is a string value which is not
particularly useful.

In order to get a JSON (_literally_ **J**avaScript **O**bject **N**otation) representation of an object used in a
templated string use `JSON.stringify`:

```javascript
logString = `Description: ${ description }. Properties: ${ JSON.stringify( properties ) }.`
```

Or consider extending `toString` for our own types:

```javascript
myPropertiesConstructor.prototype.toString = function() {
    return JSON.stringify( this );
};
```
