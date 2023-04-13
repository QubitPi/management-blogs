---
layout: post
title: Setting Up a Minimized GraphQL Server
tags: [GraphQL, Data]
color: rgb(255, 105, 132)
feature-img: "assets/img/post-cover/14-cover.png"
thumbnail: "assets/img/post-cover/14-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Setting Up a Minimized GraphQL Server
-------------------------------------

### Running Express + GraphQL

#### Running an Express GraphQL Server

The simplest way to run a GraphQL API server is to use [Express](https://expressjs.com/), a popular web application
framework for Node.js. You will need to install two additional dependencies:

    npm install express express-graphql graphql --save

##### [Setup Nodejs Project](https://closebrace.com/tutorials/2017-03-02/the-dead-simple-step-by-step-guide-for-front-end-developers-to-getting-up-and-running-with-nodejs-express-and-mongodb)

    npm install -g express-generator
    express --view="ejs" example

###### Config - package.json

```json
"dependencies": {
    ...
    "express": "~4.16.1",
    "graphql": "*",
    "express-graphql": "*",
    ...
}
```

###### Config - app.js

We can use the 'express' module to run a webserver, and instead of executing a query directly with the graphql function,
we can use the `express-graphql` library to mount a GraphQL API server on the "/graphql" HTTP endpoint:

```javascript
var express = require('express');
var { graphqlHTTP } = require('express-graphql');
var { buildSchema } = require('graphql');

// Construct a schema, using GraphQL schema language
var schema = buildSchema(`
    type Query {
        hello: String
    }
`);

// The root provides a resolver function for each API endpoint
var root = {
    hello: () => {
        return 'Hello world!';
    },
};

var app = express();
app.use(
    '/graphql',
    graphqlHTTP({
        schema: schema,
        rootValue: root,
        graphiql: true,
    })
);

app.listen(4000);
console.log('Running a GraphQL API server at http://localhost:4000/graphql');

module.exports = app;
```

Run the server using

    npm install
    npm start

If everything is setup correctly, you should be able to see the following output:

    > graph-list@0.0.0 start
    > node ./bin/www
    
    Running a GraphQL API server at http://localhost:4000/graphql

Since we configured `graphqlHTTP` with `graphiql: true`, you can use the GraphiQL tool to manually issue GraphQL
queries. If you navigate in a web browser to [`http://localhost:4000/graphql`](http://localhost:4000/graphql), you
should be able to see an interface that lets you enter queries. It should look like:

![hello-graphql-server.png not loaded property]({{ "/assets/img/hello-graphql-server.png" | relative_url}})

This screen shot shows the GraphQL query `{ hello }` being issued and giving a result of
`{ data: { hello: 'Hello world!' } }`. GraphiQL is a great tool for debugging and inspecting a server, so we recommend
running it whenever your application is in development mode.

This is how we would run a GraphQL server and to use GraphiQL interface to issue queries.

We can also do this from the command line with `curl`. If you paste this into a terminal:

```bash
curl -X POST \
-H "Content-Type: application/json" \
-d '{"query": "{ hello }"}' \
http://localhost:4000/graphql
```

You should see the output returned as JSON:

```json
{"data":{"hello":"Hello world!"}}
```
