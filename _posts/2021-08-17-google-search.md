---
layout: post
title: Why is Google so Much Faster than a Hard-drive Search?
tags: [Performance, Data]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/4-cover.png"
thumbnail: "assets/img/post-cover/4-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Overview 
  
Google is not searching the internet: it is searching an index. Google has huge server farms which are constantly
scanning and indexing the internet. This process takes a lot of time, just like the search of your unindexed hard drive.
In Windows 7, there is an option to index your hard drives. This process takes some time at first but once it is up and
running the results of a search will be instantaneous.

> Google: Our mission is to organize the world’s information and make it universally accessible and useful.

### Google's Approach to Search

Deliver the most relevant and reliable information available

### How Search Works

* **Finding Information by Crawling** - Most of our Search index is built through the work of software known as
  crawlers. These automatically visit publicly accessible webpages and follow links on those pages, much like you would
  if you were browsing content on the web. They go from page to page and store information about what they find on these
  pages and other publicly-accessible content in Google's Search index.
* **Organizing Information by Indexing** - When crawlers find a webpage, our systems render the content of the page,
  just as a browser does. We take note of key signals - from keywords to website freshness - and we keep track of it all 
  in the Search index
* **Constantly Crawling for New Info** - Because the web and other content is constantly changing, our crawling
  processes are always running to keep up. They learn how often content they've seen before seems to change and revisit
  as needed. They also discover new content as new links to those pages or information appear.
  
  Google also provides a free toolset called
  [Search Console](https://support.google.com/webmasters/answer/9128668?hl=en) that creators can use to help us better
  crawl their content. They can also make use of established standards like
  [sitemaps](https://developers.google.com/search/docs/advanced/sitemaps/overview?hl=en&visit_id=637533703645569991-3393536445&rd=1)
  or [robots.txt](https://developers.google.com/search/docs/advanced/robots/robots_meta_tag?hl=en) to indicate how often
  content should be visited or if it shouldn't be included in our Search index at all.

  Google never accepts payment to crawl a site more frequently — we provide the same tools to all websites to ensure the
  best possible results for our users.
  
* Our Search index contains more than just what's on the web, because helpful information can be located in other
  sources.

  In fact, we have multiple indexes of different types of information, which is gathered through crawling, through
  partnerships, through data feeds being sent to us and through our own encyclopedia of facts, the
  [Knowledge Graph](https://support.google.com/knowledgepanel/answer/9787176).
  
## Google's Knowledge Graph

Google's search results sometimes show information that comes from our Knowledge Graph, our database of billions of
facts about people, places, and things. The Knowledge Graph allows us to answer factual questions such as "How tall is
the Eiffel Tower?" or "Where were the 2016 Summer Olympics held." Our goal with the Knowledge Graph is for our systems
to discover and surface publicly known, factual information when it's determined to be useful.

### Where do Knowledge Graph facts come from?

Facts in the Knowledge Graph come from a variety of sources that compile factual information. In addition to public
sources, we license data to provide information such as sports scores, stock prices, and weather forecasts. **We also
receive factual information directly from content owners** in various ways, including from those who suggest changes to
knowledge panels they've claimed.

### How does Google Correct or Remove Knowledge Graph Information?

Google processes billions of searches per day. Automation is the only way to handle this many searches. This means the
best way to improve our results is to improve our automated systems, our
[search algorithms](#search-algorithms).
