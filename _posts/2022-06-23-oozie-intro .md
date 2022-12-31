---
layout: post
title: Apache Oozie User Guide
tags: [oozie]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

While I was working at Yahoo!, I realized that advertising pipelines at Yahoo! were extremely complex. Data was
processed in batches that ranged from 5 minutes to 30 days in length, with aggregates "graduating" in complex ways from
one time scale to another. In addition, these pipelines needed to detect and gracefully handle late data, missing data,
software bugs tickled by "black swan" event data, and software bugs introduced by recent software pushes. On top of all
of that, billions of dollars of revenue—and a good deal of the company's growth prospects—depended on these pipelines,
raising the stakes for data quality, security, and compliance. This was the reason that Apache Oozie remained as the
most sophisticated and powerful workflow scheduler for managing Apache Hadoop jobs at Yahoo!. Making use of its entire
usefulness requires guidance and advice of expert users. This is why I learn & practice Oozie comprehensively and make
this documentation on what I've learned.

<!--more-->

* TOC
{:toc}

* [Hadoop The Definitive Guide]({{ "/assets/pdf/apache-oozie-the-workflow-scheduler-for-hadoop.pdf" | relative_url}})
