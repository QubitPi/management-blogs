---
layout: post
title: Apollo Client
tags: [GraphQL]
color: rgb(127, 91, 213)
feature-img: "assets/img/post-cover/21-cover.png"
thumbnail: "assets/img/post-cover/21-cover.png"
authors: [apollo-client]
excerpt_separator: <!--more-->
---

Apollo Client is a comprehensive state management library for JavaScript that enables you to manage both local and
remote data with GraphQL. Use it to fetch, cache, and modify application data, all while automatically updating your UI.

<!--more-->

* TOC
{:toc}

Installing Dependencies
-----------------------

Applications that use Apollo Client require two top-level NPM dependencies:

- **@apollo/client**: This single package contains virtually everything you need to set up Apollo Client. It includes the in-memory cache, local state management, error handling, and a React-based view layer.
- **graphql**: This package provides logic for parsing GraphQL queries.

Run the following command to install both of these packages:

```bash
yarn add @apollo/client graphql
```

Initialize ApolloClient
-----------------------

With our dependencies set up, the next step is to initialize an **ApolloClient** instance. Let's first import the
symbols we need from `@apollo/client`:

```javascript
import { ApolloClient, InMemoryCache, ApolloProvider, gql } from '@apollo/client';
```

Next we'll initialize an `ApolloClient`, passing its constructor a configuration object with the **uri** and **cache** 
fields:

```javascript
const client = new ApolloClient({
  uri: 'https://graphql-server.com/',
  cache: new InMemoryCache(),
});
```

- `uri` specifies the URL of our GraphQL server.
- `cache` is an instance of **InMemoryCache**, which Apollo Client uses to cache query results after fetching them.

Apollo Link
-----------

The **Apollo Link** library helps us customize the flow of data between Apollo Client and our GraphQL server. We can
define our client's network behavior as a chain of link objects that execute in a sequence:

![Error loading apollo-link-overview.png]({{ "/assets/img/apollo-link-overview.png" | relative_url}})

Each link should represent either a self-contained modification to a GraphQL operation or a side effect (such as
logging). In the diagram above, for example,

- The first link might log the details of the operation for debugging purposes.
- The second link might add an HTTP header to the outgoing operation request for authentication purposes.
- The final (terminating) link sends the operation to its destination (usually a GraphQL server over HTTP).
- The server's response is passed back up each link in reverse order, enabling links to modify the response or take
  other actions before the data is cached.

By default, Apollo Client uses Apollo Link's **HttpLink** to send GraphQL operations to a remote server over HTTP.
Apollo Client takes care of creating this default link, and it covers many use cases without requiring additional 
customization.

To extend or replace this default networking behavior, you can define _custom links_ and specify their order of
execution in the `ApolloClient` constructor.
