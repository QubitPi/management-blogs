---
layout: post
title: Customer-Driven Data System - Feature & Test
tags: [GraphQL, Data]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/31-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

In a data-driven system, one of the most important assets is data. Managing and retrieving data in a clean and efficient
manner almost tell the difference between a well-developed product and poorly-functioning product. 

A software is best developed and tested if the system behaves exactly what its stakeholders expect it to behave. This
post presents some of my thoughts on how to best align system functions with stakeholders' expectations.

![user-driven-data-system.png not loaded property]({{ "/assets/img/user-driven-data-system.png" | relative_url}})

* Stakeholders Provide Data Specification
  - The specification contains the metadata as well as the actual data. The size of the actual data should be able to
    range from sanity-check-size to massive-size. When the data size is large, developer could also use the data as
    the load test data. In addition, passing the load test this way should make the system behave better in the view
    of stakeholders
* Developers Convert Data Specification to the Actual Data
  - **Experiences have shown that different Data Specifications can all be converted to the actual Data using almost the
    same implementation. The variables only come from the "meta" data of the Data Specification;**
  - Given the fact above, the conversion can be auto-generated using a "meta" program. The benefits of it is that any
    changes in the Data
    Specification can be quickly consumed to re-generate the test data, it is the same effort as re-executing
    the "meta" program
  - **Another benefit of the "meta" program is that both the production code could share the feature and make the 
    codebase more consistent in terms of logic**. For example, suppose to have an database type enum written in the 
    "meta" program (or production code):

    ```java
    public enum DatabaseType {
    
        SQL,
        GRAPH_DATABASE,
        HBASE
    }
    ```

    This enum definition could be reused in production code (or "meta program") by bumping it to a common package
    ("Shared Code" in the diagram above). This makes it easier to maintain the code by keeping a single source of truth.
* Acceptance Testing (**To be continued**)...
