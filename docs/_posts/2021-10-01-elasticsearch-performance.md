---
layout: post
title: Elasticsearch Performance
tags: [Elasticsearch, Performance]
color: rgb(240,78,35)
feature-img: "assets/img/post-cover/5-cover.png"
thumbnail: "assets/img/post-cover/5-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

##  Tune for Search Speed

#### Give Memory to the Filesystem Cache

Elasticsearch heavily relies on the filesystem cache in order to make search fast. In general, you should make sure that
at least half the available memory goes to the filesystem cache so that Elasticsearch can keep hot regions of the index
in physical memory.

##### General Filesystem Caching



#### Use Faster Hardware

If your search is I/O bound, you should investigate giving more memory to the filesystem cache (see above) or buying
faster drives. In particular **SSD drives** are known to perform better than spinning disks. **Always use local
storage**, remote filesystems such as NFS or SMB should be avoided. Also beware of virtualized storage such as Amazon's
Elastic Block Storage. Virtualized storage works very well with Elasticsearch, and it is appealing since it is so fast
and simple to set up, but it is also unfortunately inherently slower on an ongoing basis when compared to dedicated
local storage. If you put an index on EBS, be sure to use provisioned IOPS otherwise operations could be quickly
throttled.

If your search is CPU-bound, you should investigate buying faster CPUs.

#### Document Modeling

Documents Should be Modeled so that Search-time Operations are as Cheap as Possible.

In particular, **joins should be avoided**.
[nested](https://www.elastic.co/guide/en/elasticsearch/reference/current/nested.html) can make queries several times
slower and [parent-child](https://www.elastic.co/guide/en/elasticsearch/reference/current/parent-join.html) relations
can make queries hundreds of times slower. So if the same questions can be answered without joins by **denormalizing
documents**, significant speedups can be expected.

#### Search as Few Fields as Possible

The more fields a
[query_string](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html) or
[multi_match](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html) query
targets, the slower it is. A common technique to improve search speed over multiple fields is to copy their values into
a single field at index time, and then use this field at search time. This can be automated with the
[copy-to](https://www.elastic.co/guide/en/elasticsearch/reference/current/copy-to.html) directive of mappings without
having to change the source of documents. Here is an example of an index containing movies that optimizes queries that
search over both the name and the plot of the movie by indexing both values into the `name_and_plot` field.

```
PUT movies
{
    "mappings": {
        "properties": {
            "name_and_plot": {
                "type": "text"
            },
            "name": {
                "type": "text",
                "copy_to": "name_and_plot"
            },
            "plot": {
                "type": "text",
                "copy_to": "name_and_plot"
            }
        }
    }
}
```

#### Pre-Index Data

You should leverage patterns in your queries to optimize the way data is indexed. For instance, if all your documents
have a `price` field and most queries run
[range](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-range-aggregation.html)
aggregations on a fixed list of ranges, you could make this aggregation faster by pre-indexing the ranges into the index
and using a [terms](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html) aggregations.

For instance, if documents look like:

```
PUT index/_doc/1
{
    "designation": "spoon",
    "price": 13
}
```

and search requests look like:

```
GET index/_search
{
    "aggs": {
        "price_ranges": {
            "range": {
                "field": "price",
                "ranges": [
                    { "to": 10 },
                    { "from": 10, "to": 100 },
                    { "from": 100 }
                ]
            }
        }
    }
}
```

Then documents could be enriched by a `price_range` field at index time, which should be mapped as a keyword:

