---
layout: post
title: Elasticsearch Basics
tags: [Elasticsearch]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

Thi post is part of Elasticsearch series:

* [Elasticsearch Basics](https://qubitpi.github.io/jersey-guide/2020/09/23/elasticsearch.html)
* [Elasticsearch Performance](https://qubitpi.github.io/jersey-guide/2021/10/01/elasticsearch-performance.html)


* TOC
{:toc}
  
## Elasticsearch Mapping

Each document is a collection of fields, which each have their own data type. When mapping your data, you create a mapping definition, which contains a list of fields that are pertinent to the document. A mapping definition also includes metadata fields, like the _source field, which customize how a document’s associated metadata is handled.

A schema in Elasticsearch is a mapping that describes the fields in the JSON documents along with their data type, as
well as how they should be indexed in the Lucene indexes that lie under the hood. Because of this, in Elasticsearch
terms, we usually call this schema a "mapping".

Conceptually, an Elasticsearch server contains zero or more indexes. An index is a container for zero or more types,
which in turn has zero or more documents. For example:

![Error Loading es-index-type-docs.png]({{ "/assets/img/es-index-type-docs.png" | relative_url}})

The type called `another_type` and the index called `another` is shown in order to emphasize that Elasticsearch is
multi-tenant, i.e. a single server can store multiple indexes and multiple types.

In the Elasticsearch documentation and related material, we often see the term "mapping type", which is actually the
name of the type inside the index, such as `my_type` and `another_type` in the figure above. When we talk about types in
Elasticsearch, it is usually this definition of type. It is not to be confused with the `type` key inside each mapping
definition that determines how the data inside the documents are handled by Elasticsearch.

Elasticsearch has the ability to be schema-less, which means that documents can be indexed without explicitly providing
a schema.

If you do not specify a mapping, Elasticsearch will by default generate one dynamically when detecting new fields in
documents during indexing. However, this dynamic mapping generation comes with a few caveats:

* Detected types might not be correct.
* May lead to unnecessary duplication. (The [_source field](#document-source-metadata-fields) and
  [_all field](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/mapping-all-field.html#custom-all-fields)
  especially.)
* Uses default analyzers and settings for indexing and searching.

For example, a timestamp is often represented in JSON as a `long`, but Elasticsearch will be unable to detect the field
as a date field, preventing date filters and facets such as
[the date histogram facet](https://www.elastic.co/guide/en/elasticsearch/reference/2.3/search-aggregations-bucket-datehistogram-aggregation.html)
from working properly.

By explicitly specifying the schema, we can avoid these problems.

### What Does a Mapping Look Like?

The mapping is usually provided to Elasticsearch as JSON, and is a hierarchically structured format where the root is
the name of the type the mapping applies to. For example

Document:

```json
{
    "name": {
        "first": "John"
    }
}
```

Mapping:

```json
{
    "my_type" : {
        "properties" : {
            "name" : {
                "properties" : {
                    "first" : {
                        "type" : "string"
                    }
                }
            }
        }
    }
}
```

#### The "type" Key

In the above example, we see that the document field `name.first` differs from the rest of the structure in that it
defines a **type**. The `type` key is used on the _leaf_ levels to tell Elasticsearch how to handle the field at the
given level in the document. If the `type` key is omitted, as in the case of non-leaf types, Elasticsearch assumes it is
of the object type.

The `string` type is one of the built-in
[core types](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html), and Elasticsearch
comes with support for many different types, such as
[geo_point](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-point.html) and ip, which can be used to
effectively index and search geographical locations and IPv4 addresses respectively. Using the
[multi_field](https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-fields.html) type, we can even index
a single document field into multiple virtual fields. We’ll elaborate on this in a future article.

### How to Provide a Mapping

There are two ways of providing a mapping to Elasticsearch. The most common way is during
[index creation](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html):

```
curl -XPOST ...:9200/my_index -d '{
    "settings" : {
        # .. index settings
    },
    "mappings" : {
        "my_type" : {
            # mapping for my_type
        }
    }
}'
```

> 📋 For more details about index settings, please refer to
> https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html#index-modules-settings

Another way of providing the mapping is using the [Put Mapping API](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html).

```
$ curl -XPUT 'http://localhost:9200/my_index/my_type/_mapping' -d '
{
    "my_type" : {
        # mapping for my_type
    }
}
'
```

> Note that the type (`my_type`) is duplicated in the request path and the request body.

This API enables us to update the mapping for an already existing index, but with some limitations with regards to
potential conflicts. New mapping definitions can be added to the existing mapping, and existing types may have their
configuration updated, but changing the types is considered a conflict and is not accepted. It is, however, possible to
pass `ignore_conflicts=true` as a parameter to the Mapping API, but doing so does not guarantee producing the expected
result, as already indexed documents are not re-indexed automatically with the new mapping.

Because of this, specifying the mapping during creation of the indexes is recommended over using the Put Mapping API in
most cases.

### Metadata Fields

As we discussed above, papping is the process of defining how a document, and the fields it contains, are stored and
indexed.

Each document is a collection of fields, which each have their own
[data type](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html). When mapping your data,
you create a mapping definition, which contains a list of fields that are pertinent to the document. A mapping
definition also includes metadata fields, like the `_source` field, which customize how a document's associated metadata
is handled.

Each document has metadata associated with it, such as the `_index`, mapping `_type`, and `_id` metadata fields. The
behavior of some of these metadata fields can be customized when a mapping type is created.

#### Identity Metadata Fields

##### "_index" - the index to which the document belongs.

When performing queries across multiple indexes, it is sometimes desirable to add query clauses that are associated with
documents of only certain indexes. The `_index` field allows matching on the index a document was indexed into. Its
value is accessible in certain queries and aggregations, and when sorting or scripting:

```
PUT index_1/_doc/1
{
    "text": "Document in index 1"
}

PUT index_2/_doc/2?refresh=true
{
    "text": "Document in index 2"
}

GET index_1,index_2/_search
{
    "query": {
        "terms": {
            "_index": ["index_1", "index_2"] 
        }
    },
    "aggs": {
        "indices": {
            "terms": {
                "field": "_index", 
                "size": 10
            }
        }
    },
    "sort": [
        {
            "_index": { 
                "order": "asc"
            }
        }
    ],
    "script_fields": {
        "index_name": {
            "script": {
                "lang": "painless",
                "source": "doc['_index']" 
            }
        }
    }
}
```

The `_index` field is exposed virtually - it is **not added to the Lucene index** as a real field. This means that you
can use the `_index` field in a `term` or `terms` query (or any query that is rewritten to a `term` query, such as the
`match`, `query_string` or `simple_query_string` query), as well as `prefix` and `wildcard` queries. However, it does
not support `regexp` and `fuzzy` queries.

Queries on the `_index` field accept index aliases in addition to concrete index names.

##### "_type" - the document's mapping type

> ⚠️ Deprecated in 6.0.0. See
> [Removal of mapping types](https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html)

Each document indexed is associated with a _type and an [_id](#_id---the-documents-id). The `_type` field is indexed in
order to make searching by type name fast.

The value of the `_type` field is accessible in queries, aggregations, scripts, and when sorting:

```
# Example documents

PUT my-index-000001/_doc/1?refresh=true
{
    "text": "Document with type 'doc'"
}

GET my-index-000001/_search
{
    "query": {
        "term": {
            "_type": "_doc"  
        }
    },
    "aggs": {
        "types": {
            "terms": {
                "field": "_type", 
                "size": 10
            }
        }
    },
    "sort": [
        {
            "_type": { 
                "order": "desc"
            }
        }
    ],
    "script_fields": {
        "type": {
            "script": {
                "lang": "painless",
                "source": "doc['_type']" 
            }
        }
    }
}
```

###### Alternatives to "_type"

The first alternative is to have an index per document type. Indices are completely independent of each other and so
there will be no conflict of field types between indices.

This approach has two benefits:

1. Data is more likely to be dense and so benefit from compression techniques used in Lucene.
2. The term statistics used for scoring in full text search are more likely to be accurate because all documents in the
   same index represent a single entity.

Each index can be sized appropriately for the number of documents it will contain: you can use a smaller number of
primary shards for index1 and a larger number of primary shards for index2.

Of course, there is a limit to how many primary shards can exist in a cluster so you may not want to waste an entire
shard for a collection of only a few thousand documents. In this case, you can implement your own custom `type` field
which will work in a similar way to the old `_type`.

Let's take the `user`/`tweet` example above. Originally, the workflow would have looked something like this:

```
PUT twitter
{
    "mappings": {
        "user": {
            "properties": {
                "name": { "type": "text" },
                "user_name": { "type": "keyword" },
                "email": { "type": "keyword" }
            }
        },
        "tweet": {
            "properties": {
                "content": { "type": "text" },
                "user_name": { "type": "keyword" },
                "tweeted_at": { "type": "date" }
            }
        }
    }
}

PUT twitter/user/kimchy
{
    "name": "Shay Banon",
    "user_name": "kimchy",
    "email": "shay@kimchy.com"
}

PUT twitter/tweet/1
{
    "user_name": "kimchy",
    "tweeted_at": "2017-10-24T09:00:00Z",
    "content": "Types are going away"
}

GET twitter/tweet/_search
{
    "query": {
        "match": {
            "user_name": "kimchy"
        }
    }
}
```

You can achieve the same thing by adding a custom type field as follows:

```
PUT twitter
{
    "mappings": {
        "_doc": {
            "properties": {
                "type": { "type": "keyword" }, 
                "name": { "type": "text" },
                "user_name": { "type": "keyword" },
                "email": { "type": "keyword" },
                "content": { "type": "text" },
                "tweeted_at": { "type": "date" }
            }
        }
    }
}

PUT twitter/_doc/user-kimchy
{
    "type": "user", 
    "name": "Shay Banon",
    "user_name": "kimchy",
    "email": "shay@kimchy.com"
}

PUT twitter/_doc/tweet-1
{
    "type": "tweet", 
    "user_name": "kimchy",
    "tweeted_at": "2017-10-24T09:00:00Z",
    "content": "Types are going away"
}

GET twitter/_search
{
    "query": {
        "bool": {
            "must": {
                "match": {
                    "user_name": "kimchy"
                }
            },
            "filter": {
                "match": {
                    "type": "tweet" 
                }
            }
        }
    }
}
```

##### "_id" - the document's ID.

Each document has an `_id` that uniquely identifies it, which is indexed so that documents can be looked up either with
the [GET API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html) or the
[ids query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html). The `_id` can
either be assigned at indexing time, or a unique `_id` can be generated by Elasticsearch. This field is not configurable
in the mappings.

The value of the `_id` field is accessible in queries such as `term`, `terms`, `match`, and `query_string`.

```
# Example documents
PUT my-index-000001/_doc/1
{
    "text": "Document with ID 1"
}

PUT my-index-000001/_doc/2?refresh=true
{
    "text": "Document with ID 2"
}

GET my-index-000001/_search
{
    "query": {
        "terms": {
            "_id": [ "1", "2" ] 
        }
    }
}
```

The `_id` field is restricted from use in aggregations, sorting, and scripting. In case sorting or aggregating on the
`_id` field is required, it is advised to duplicate the content of the `_id` field into another field that has
`doc_values` enabled.

> `_id` is limited to 512 bytes in size and larger values will be rejected.

#### Document Source Metadata Fields

##### "_source" - the original JSON representing the body of the document.

The `_source` field contains the original JSON document body that was passed at index time. The `_source` field itself
is not indexed (and thus is not searchable), but it is stored so that it can be returned when executing _fetch_
requests, like [get](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html) or
[search](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html).

###### Disabling the "_source" fieldedit

Though very handy to have around, the source field does incur storage overhead within the index. For this reason, it can
be disabled as follows:

```
PUT my-index-000001
{
    "mappings": {
        "_source": {
            "enabled": false
        }
    }
}
```

> ⚠️ **Think before disabling the `_source` field**
> 
> Users often disable the `_source` field without thinking about the consequences, and then live to regret it. If the
> `_source` field isn't available then a number of features are not supported:
> 
> * The [update](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html),
    [update_by_query](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update-by-query.html), and
    [reindex](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-reindex.html) APIs
> * On the fly [highlighting](https://www.elastic.co/guide/en/elasticsearch/reference/current/highlighting.html)
> * The ability to reindex from one Elasticsearch index to another, either to change mappings or analysis, or to upgrade
    an index to a new majorBrowsing Data version
> * The ability to debug queries or aggregations by viewing the original document used at index time
> * Potentially in the future, the ability to repair index corruption automatically

> 💡 If disk space is a concern, rather increase the
> [compression level](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html#index-codec)
> instead of disabling the `_source`.

###### Including/Excluding Fields from "_source"

An expert-only feature is the ability to prune the contents of the `_source` field after the document has been indexed,
but before the `_source` field is stored.

> ⚠️ Removing fields from the `_source` has similar downsides to disabling `_source`, especially the fact that you
> cannot reindex documents from one Elasticsearch index to another. Consider using
> [source filtering](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-fields.html#source-filtering)
> instead.

The `includes`/`excludes` parameters (which also accept wildcards) can be used as follows:

```
PUT logs
{
    "mappings": {
        "_source": {
            "includes": [
                "*.count",
                "meta.*"
            ],
            "excludes": [
                "meta.description",
                "meta.other.*"
            ]
        }
    }
}

PUT logs/_doc/1
{
    "requests": {
        "count": 10,
        "foo": "bar" 
    },
    "meta": {
        "name": "Some metric",
        "description": "Some metric description", 
        "other": {
            "foo": "one", 
            "baz": "two" 
        }
    }
}

GET logs/_search
{
    "query": {
        "match": {
            "meta.other.foo": "one" 
        }
    }
}
```

### Field Data Types

Each field has a **field data type**, or **field type**. This type indicates the kind of data the field contains, such
as strings or boolean values, and its intended use. For example, you can index strings to both **text** and **keyword**
fields. However, text field values are [analyzed](#text-analysis) for full-text search while keyword strings are left as-is for filtering
and sorting.

Field types are grouped by **family**. Types in the same family support the same search functionality but may have
different space usage or performance characteristics.

One type family is **keyword**, which consists of

* keyword
* constant_keyword, and
* wildcard field types.
  
Other type families have only a single field type. For example, the boolean type family consists of one field type:
boolean.

#### Text Type Family

#### Object Field Type

JSON documents are hierarchical in nature: the document may contain inner objects which, in turn, may contain inner
objects themselves:

```json
PUT my-index-000001/_doc/1
{ 
    "region": "US",
    "manager": { 
        "age": 30,
        "name": { 
            "first": "John",
            "last":  "Smith"
        }
    }
}
```

Internally, this document is indexed as a simple, flat list of key-value pairs, something like this:

```json
{
    "region":             "US",
    "manager.age":        30,
    "manager.name.first": "John",
    "manager.name.last":  "Smith"
}
```

An explicit mapping for the above document could look like this:

```json
PUT my-index-000001
{
    "mappings": {
        "properties": { 
            "region": {
                "type": "keyword"
            },
            "manager": { 
                "properties": {
                    "age": {
                        "type": "integer"
                    },
                    "name": { 
                        "properties": {
                            "first": {
                                "type": "text"
                            },
                            "last": {
                                "type": "text"
                            }
                        }
                    }
                }
            }
        }
    }
}
```

#### Parameters for `object` Fields

The following parameters are accepted by object fields:

### Nested Field Type

The **nested** type is a specialised version of the object data type that allows arrays of objects to be indexed in a way that they can be queried independently of each other.

### Mapping Parameters

The following mapping parameters are common to some or all field data types:

#### analyzer

> 📋 Only text fields support the analyzer mapping parameter.
 
The analyzer parameter specifies the analyzer used for text analysis when indexing or searching a text field.

## Text Analysis

**Text analysis** is the process of converting unstructured text, like the body of an email or a product description,
into a structured format that's optimized for search.

Elasticsearch performs text analysis when indexing or searching [text](#text-type-family) fields.

Text analysis enables Elasticsearch to perform full-text search, where the search returns all relevant results rather
than just exact matches.

If you search for "Quick fox jumps", you probably want the document that contains "A quick brown fox jumps over the lazy
dog", and you might also want documents that contain related words like "fast fox or foxes leap".

### Tokenization

Analysis makes full-text search possible through **tokenization**: breaking a text down into smaller chunks, called
**tokens**. In most cases, these tokens are individual words.

If you index the phrase "the quick brown fox jumps" as a single string and the user searches for "quick fox", it isn't
considered a match. However, if you tokenize the phrase and index each word separately, the terms in the query string
can be looked up individually. This means they can be matched by searches for "quick fox", "fox brown", or other
variations.

#### Normalization

Tokenization enables matching on individual terms, but each token is still matched literally. This means:

* A search for "Quick" would not match "quick", even though you likely want either term to match the other
* Although "fox" and "foxes" share the same root word, a search for foxes would not match fox or vice versa
* A search for "jumps" would not match "leaps". While they do not share a root word, they are synonyms and have a
  similar meaning.

To solve these problems, text analysis can **normalize** these tokens into a standard format. This allows you to match
tokens that are not exactly the same as the search terms, but similar enough to still be relevant. For example:

* "Quick" can be lowercased: "quick"
* "foxes" can be stemmed, or reduced to its root word: "fox".
* "jump" and "leap" are synonyms and can be indexed as a single word: "jump"

To ensure search terms match these words as intended, you can apply the same tokenization and normalization rules to the
query string. For example, a search for "Foxes leap" can be normalized to a search for "fox jump".

#### Customize Text Analysis

Text analysis is performed by an **[analyzer](#analyzer)**, a set of rules that govern the entire process.

Elasticsearch includes a default analyzer, called the **[standard analyzer](#standard-analyzer)**, which works well for most
use cases right out of the box.

If you want to tailor your search experience, you can choose a different [built-in analyzer](#analyzer) or even
configure a [custom one](#custom-analyzer). A custom analyzer gives you control over each step of the analysis process,
including:

* Changes to the text before tokenization
* How text is converted to tokens
* Normalization changes made to tokens before indexing or search

### Analyzer

An analyzer, whether built-in or custom, is just a package which contains three lower-level building blocks:

* [character filters](#character-filters)
* [tokenizers](#tokenizer), and
* [token filters](#token-filters)

The built-in analyzers pre-package these building blocks into analyzers suitable for different languages and types of
text. Elasticsearch also exposes the individual building blocks so that they can be combined to define new custom
analyzers.

#### Character Filters

A **character filter** receives the original text as a stream of characters and can transform the stream by adding,
removing, or changing characters.

An analyzer may have zero or more character filters, which are applied in order.

Elasticsearch has a number of built in character filters which can be used to build
[custom analyzers](#custom-analyzer).

##### HTML Strip Character Filter

Strips HTML elements from a text and replaces HTML entities with their decoded value (e.g, replaces `&amp;` with `&).

This filter delegates to [Lucene's HTMLStripCharFilter](https://lucene.apache.org/core/8_9_0/analyzers-common/org/apache/lucene/analysis/charfilter/HTMLStripCharFilter.html)

The following [analyze API](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-analyze.html)
request changes the text `<p>I&apos;m so <b>happy</b>!</p>` to `\nI'm so happy!\n`.

```json
GET /_analyze
{
  "tokenizer": "keyword",
  "char_filter": ["html_strip"],
  "text": "<p>I&apos;m so <b>happy</b>!</p>"
}
```

The following
[create index API](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html) request
configures a new [custom analyzer](#custom-analyzer)

```json
PUT /my-index-000001
{
    "settings": {
        "analysis": {
            "analyzer": {
                "my_analyzer": {
                    "tokenizer": "keyword",
                    "char_filter": ["html_strip"]
                }
            }
        }
    }
}
```

###### Customize

```json
PUT my-index-000001
{
    "settings": {
        "analysis": {
            "analyzer": {
                "my_analyzer": {
                    "tokenizer": "keyword",
                    "char_filter": ["my_custom_html_strip_char_filter"]
                }
            },
            "char_filter": {
                "my_custom_html_strip_char_filter": {
                    "type": "html_strip",
                    "escaped_tags": ["b"]
                }
            }
        }
    }
}
```

To customize the `html_strip` filter, duplicate it to create the basis for a new custom character filter. You can modify
the filter using its configurable parameter `escaped_tags`, which is an of HTML elements without enclosing angle
brackets (`<` `>`). The filter skips these HTML elements when stripping HTML from the text. For example, a value of
`["p"]` skips the `<p>` HTML element.

##### Mapping Character Filter

The **mapping character filter** accepts a map of keys and values. Whenever it encounters a string of characters that is
the same as a key, it replaces them with the value associated with that key.

Matching is greedy; the longest pattern matching at a given point wins. Replacements are allowed to be the empty string.

The mapping filter uses
[Lucene's MappingCharFilter](https://lucene.apache.org/core/8_9_0/analyzers-common/org/apache/lucene/analysis/charfilter/MappingCharFilter.html).

The following analyze API request uses the mapping filter to convert Hindu-Arabic numerals (٠١٢٣٤٥٦٧٨٩) into their
Arabic-Latin equivalents (0123456789), changing the text My license plate is ٢٥٠١٥ to My license plate is 25015.

```json
GET /_analyze
{
    "tokenizer": "keyword",
    "char_filter": [
        {
            "type": "mapping",
            "mappings": [
                "٠ => 0",
                "١ => 1",
                "٢ => 2",
                "٣ => 3",
                "٤ => 4",
                "٥ => 5",
                "٦ => 6",
                "٧ => 7",
                "٨ => 8",
                "٩ => 9"
            ]
        }
    ],
    "text": "My license plate is ٢٥٠١٥"
}
```

The filter produces the following text:

```json
[ My license plate is 25015 ]
```

#### Tokenizer

#### Token Filters

##### Standard Analyzer


##### Custom Analyzer

## Java API

The Java REST Client comes in 2 flavors:

1. [Java Low Level REST Client](#java-low-level-rest-client): the official low-level client for Elasticsearch. It allows
   to communicate with an Elasticsearch cluster through http. Leaves requests marshalling and responses un-marshalling
   to users. It is compatible with all Elasticsearch versions.
2. [Java High Level REST Client](#java-high-level-rest-client): the official high-level client for Elasticsearch. Based
   on the low-level client, it exposes API specific methods and takes care of requests marshalling and responses
   un-marshalling.

### Java Low Level REST Client

The low-level client's features include:

* minimal dependencies
* load balancing across all available nodes
* failover in case of node failures and upon specific response codes
* failed connection penalization (whether a failed node is retried depends on how many consecutive times it failed; the
  more failed attempts the longer the client will wait before trying that same node again)
* persistent connections
* trace logging of requests and responses
* optional automatic discovery of cluster nodes

> The low-level Java REST client internally uses the [Apache Http Async Client](https://hc.apache.org) to send http
> requests.

#### Initialization

A `RestClient` instance can be built through the corresponding `RestClientBuilder` . The only required argument is one
or more hosts that the client will communicate with, provided as instances of [`HttpHost`](https://hc.apache.org/) as
follows:

```java
RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"),
        new HttpHost("localhost", 9201, "http")
).build();
```

The `RestClient` class **is thread-safe** and **ideally** has the same lifecycle as the application that uses it. It is
important that it gets closed when no longer needed so that all the resources used by it get properly released, as well
as the underlying http client instance and its threads:

```java
restClient.close();
```

`RestClientBuilder` also allows to optionally set the following configuration parameters while building the `RestClient`
instance:

```java
RestClientBuilder builder = RestClient.builder(
        new HttpHost("localhost", 9200, "http")
);

// Set the default headers that need to be sent with each request, to prevent having to specify them with each single
// request
Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
builder.setDefaultHeaders(defaultHeaders);

// Set a listener that gets notified every time a node fails, in case actions need to be taken. Used internally when
// sniffing on failure is enabled.
builder.setFailureListener(
        new RestClient.FailureListener() {
            
            @Override
            public void onFailure(Node node) {
                // ...
            }
        }
);

// Set the node selector to be used to filter the nodes the client will send requests to among the ones that are set to
// the client itself. This is useful for instance to prevent sending requests to dedicated master nodes when sniffing is
// enabled. By default the client sends requests to every configured node.
builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);

// Set a callback that allows to modify the default request configuration (e.g. request timeouts, authentication, etc)
builder.setRequestConfigCallback(
        new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setSocketTimeout(10000);
            } 
        }
);

// Set a callback that allows to modify the http client configuration (e.g. encrypted communication over ssl)
builder.setHttpClientConfigCallback(
        new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder
            ) {
                return httpClientBuilder.setProxy(
                        new HttpHost("proxy", 9000, "http"));
            }
        }
);
```

#### Sending Request

Once the `RestClient` has been created, requests can be sent by calling

* `performRequest`, which is **synchronous** and will **block** the calling thread; it returns when the request is
  successful or throw an exception if it fails. For example
  
  ```java
  Request request = new Request("GET", "/");   
  Response response = restClient.performRequest(request);
  ```
  
* `performRequestAsync`, which is asynchronous and accepts a listener gets called when the request is successful or
  throws an Exception if it fails. For instance
  
  ```java
  Request request = new Request("GET", "/");
  Cancellable cancellable = restClient.performRequestAsync(
      request,
      new ResponseListener() {
  
          @Override
          public void onSuccess(Response response) {
              ...
          }
  
          @Override
          public void onFailure(Exception exception) {
              ...
          }
  });
  ```

You can add request parameters to request object:

```java
request.addParameter("pretty", "true");
```

You can set the body of request using `HttpEntity`:

```java
request.setEntity(
        new NStringEntity(
                "{\"json\":\"text\"}",
                ContentType.APPLICATION_JSON
        )
);
```

> ⚠️ The `ContentType` specified in the `HttpEntity` is important because it will be used to set the `Content-Type`
> header so that Elasticsearch can properly parse the content.

You can also set it to a string which will default to a `ContentType` of "application/json":

```
request.setJsonEntity("{\"json\":\"text\"}");
```

##### Request Options

The `RequestOptions` class holds parts of the request that should be shared between many requests in the same
application. You can make a singleton instance and share it between all requests:

```java
private static final RequestOptions COMMON_OPTIONS;

static {
    RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
    builder.addHeader("Authorization", "Bearer " + TOKEN);
    builder.setHttpAsyncResponseConsumerFactory(
            new HttpAsyncResponseConsumerFactory
                    .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)
    );
    COMMON_OPTIONS = builder.build();
}
```

Note that there is no need to set the "Content-Type" header because the client will automatically set that from the
`HttpEntity` attached to the request.

You can set the `NodeSelector` which controls which nodes will receive requests. `NodeSelector.SKIP_DEDICATED_MASTERS`
is a good choice.

You can also customize the response consumer used to buffer the asynchronous responses. The default consumer will buffer
up to 100MB of response on the JVM heap. If the response is larger then the request will fail. You could, for example,
lower the maximum size which might be useful if you are running in a heap constrained environment like the example
above.

Once you’ve created the singleton you can use it when making requests:

```java
request.setOptions(COMMON_OPTIONS);
```

You can also customize these options on a per request basis. For example, this adds an extra header:

```java
RequestOptions.Builder options = COMMON_OPTIONS.toBuilder();
options.addHeader("cats", "knock things off of other things");
request.setOptions(options);
```

##### Multiple Parallel Asynchronous Actions

The client is quite happy to execute many actions in parallel. The following example indexes many documents in parallel.
In a real world scenario you'd probably want to use the `_bulk` API instead, but the example is illustrative.

```java
final CountDownLatch latch = new CountDownLatch(documents.length);
for (int i = 0; i < documents.length; i++) {
    Request request = new Request("PUT", "/posts/doc/" + i);
    //let's assume that the documents are stored in an HttpEntity array
    request.setEntity(documents[i]);
    restClient.performRequestAsync(
            request,
            new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    latch.countDown();
                }

                @Override
                public void onFailure(Exception exception) {
                    latch.countDown();
                }
            }
    );
}
latch.await();
```

##### Cancelling Asynchronous Requests

The `performRequestAsync` method returns a `Cancellable` that exposes a single public method called `cancel`. Such
method can be called to cancel the on-going request. Cancelling a request will result in aborting the http request
through the underlying http client. On the server side, **this does not automatically translate to the execution of that
request being cancelled, which needs to be specifically implemented in the API itself**.

The use of the `Cancellable` instance is optional and you can safely ignore this if you don't need it. A typical usecase
for this would be using this together with frameworks like Rx Java. Cancelling no longer needed requests is a good way
to avoid putting unnecessary load on Elasticsearch.

```java
Request request = new Request("GET", "/posts/_search");
Cancellable cancellable = restClient.performRequestAsync(
    request,
    new ResponseListener() {
        @Override
        public void onSuccess(Response response) {
            ...
        }

        @Override
        public void onFailure(Exception exception) {
            ...
        }
    }
);
cancellable.cancel();
```

#### Reading Responses

The `Response` object, either returned by the synchronous `performRequest` methods or received as an argument in
`ResponseListener#onSuccess(Response)`, wraps the response object returned by the http client and exposes some
additional information.

```java
Response response = restClient.performRequest(new Request("GET", "/"));
RequestLine requestLine = response.getRequestLine(); // Information about the performed request
HttpHost host = response.getHost();
int statusCode = response.getStatusLine().getStatusCode();
Header[] headers = response.getHeaders();
String responseBody = EntityUtils.toString(response.getEntity());
```

### Java High Level REST Client

The Java High Level REST Client works on top of the Java Low Level REST client. Its main goal is to expose API specific
methods, that **accept request objects as an argument and return response objects (type-safe)**

Each API can be called synchronously or asynchronously. The synchronous methods return a response object, while the
asynchronous methods, whose names end with the `async` suffix, require a listener argument that is notified (on the
thread pool managed by the low level client) once a response or an error is received.

#### Example

```java
try (XContentParser parser = XContentFactory
        .xContent(XContentType.JSON)
        .createParser(
                new NamedXContentRegistry(
                        new SearchModule(
                                Settings.EMPTY,
                                false,
                                Collections.emptyList()
                        ).getNamedXContents()
                ),
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                query
        )
) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.parseXContent(parser);
    
    getEsRestClient()
            .getRestHighLevelClient(storeURL)
            .search(
                    new SearchRequest(index).source(searchSourceBuilder),
                    RequestOptions.DEFAULT
            );
} catch (IOException exception) {
    String message = String.format("Error on quering ES: %s", exception.getMessage());
    LOG.error(message, exception);
    throw new IllegalStateException(message, exception);
}
```

## REST API

#### Search

Returns search hits that match the query defined in the request.

##### Get All Data by Index

```
GET /my-index-000001/_search
```

#### Get Data by Query

Querying

```
POST /my-index-000001/_search?from=40&size=20
{
    "query": {
        "term": {
            "user.id": "kimchy"
        }
    }
}
```

Although Elasticsearch API also supports attaching query string as request body in GET, it is, however,
[not recommended](https://stackoverflow.com/questions/978061/http-get-with-request-body#comment53906725_983458)

> ⚠️ **Pay special attention to the `from` and `size` parameters**.
> [**By default, searches return the top 10 matching hits**](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#paginate-search-results).
> **Use `from` and `size` in order to page through a larger set of result**. We could also attach them in the JSON query
> 
> ```
> POST /my-index-000001/_search
> {
>     "from": 40,
>     "size": 20,
>     "query": {
>         "term": {
>             "user.id": "kimchy"
>         }
>     }
> }```

##### Get All Data

##### Query Parameters

###### from

(Optional, integer) Starting document offset. Defaults to 0.

By default, you cannot page through more than 10,000 hits using the `from` and [`size`](#size) parameters. To page
through more hits, use the
[search_after](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#search-after)
parameter.

###### size

(Optional, integer) Defines the number of hits to return. Defaults to **10**.

By default, you cannot page through more than 10,000 hits using the [`from`](#from) and `size` parameters. To page
through more hits, use the
[search_after](https://www.elastic.co/guide/en/elasticsearch/reference/current/paginate-search-results.html#search-after)
parameter.

