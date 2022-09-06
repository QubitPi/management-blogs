---
layout: post
title: GraphQL Resolvers
tags: [GraphQL]
category: FINALIZED
color: rgb(255, 105, 132)
feature-img: "assets/img/post-cover/14-cover.png"
thumbnail: "assets/img/post-cover/14-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


What are GraphQL Resolvers?
---------------------------

After being validated, a GraphQL query is executed by a GraphQL server which returns a result that mirrors the shape of
the requested query, typically as JSON.

GraphQL cannot execute a query without a type system, let's use an example type system to illustrate executing a query:

{% highlight graphql %}
type Query {
    human(id: ID!): Human
}

type Human {
    name: String
    appearsIn: [Episode]
    starships: [Starship]
}

enum Episode {
    NEWHOPE
    EMPIRE
    JEDI
}

type Starship {
    name: String
}
{% endhighlight %}

In order to describe what happens when a query is executed, let's use an example to walk through.


<table>
<tr>
<th>Query</th>
<th>Result</th>
</tr>
<tr>
<td>

{% highlight graphql %}
{
    human(id: 1002) {
        name
        appearsIn
        starships {
            name
        }
    }
}
{% endhighlight %}

</td>
<td>

{% highlight json %}
{
    "data": {
        "human": {
            "name": "Han Solo",
            "appearsIn": [
                "NEWHOPE",
                "EMPIRE",
                "JEDI"
            ],
            "starships": [
                {
                    "name": "Millenium Falcon"
                },
                {
                    "name": "Imperial shuttle"
                }
            ]
        }
    }
}
{% endhighlight %}

</td>
</tr>
</table>

We can think of **each field in a GraphQL query as a function or method of the previous type which returns the next 
type**. In fact, this is exactly how GraphQL works. Each field on each type is backed by a function called the 
**resolver** which is provided by the GraphQL server developer. When a field is executed, the corresponding resolver is 
called to produce the next value.

If a field produces a scalar value like a string or number, then the execution completes. If, however, a field produces
an object value then the query will contain another selection of fields which apply to that object. This continues until 
scalar values are reached. _GraphQL queries always end at scalar values_.


Root Fields & Resolvers
-----------------------

At the top level of every GraphQL server is a type that represents all possible entry points into the GraphQL API, it's 
often called the _Root type_ or the **Query type**.

In this example, our Query type provides a field called `human` which accepts the argument `id`. The resolver function
for this field likely accesses a database and then constructs and returns a `Human` object.

{% highlight javascript %}
Query: {
    human(obj, args, context, info) {
        return context.db.loadHumanByID(args.id).then(
            userData => new Human(userData)
        )
    }
}
{% endhighlight %}

> 📋️ This example is written in JavaScript

A resolver function receives four arguments:

1. **obj** The previous object, which for a field on the root Query type is often not used.
2. **args** The arguments provided to the field in the GraphQL query.
3. **context** A value which is provided to every resolver and holds important contextual information like the currently 
   logged in user, or access to a database.
4. **info** A value which holds field-specific information relevant to the current query as well as the schema details, 
   also refer to [type GraphQLResolveInfo for more details](https://graphql.org/graphql-js/type/#graphqlobjecttype).


Producing the Result
--------------------

As each field is resolved, the resulting value is placed into a key-value map with the field name (or alias) as the key 
and the resolved value as the value. This continues from the bottom leaf fields of the query all the way back up to the 
original field on the root Query type. Collectively these produce a structure that mirrors the original query which can 
then be sent (typically as JSON) to the client which requested it.
