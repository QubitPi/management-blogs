---
layout: post
title: How to Set a BASE URL for Sphinx Documentation
tags: [Sphinx, Software Documentation]
color: rgb(11, 80, 122)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Add this line in conf.py:

```python
html_baseurl = '/docs/'
```

ref: [sphinx doc](https://www.sphinx-doc.org/en/master/usage/configuration.html)
