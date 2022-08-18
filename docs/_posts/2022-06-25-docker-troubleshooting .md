---
layout: post
title: Docker Troubleshooting
tags: [Docker]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/27-cover.png"
thumbnail: "assets/img/post-cover/27-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Proxy

```bash
% docker pull ubuntu:latest
Error response from daemon: Get https://registry-1.docker.io/v2/: net/http: request canceled while waiting for
connection (Client.Timeout exceeded while awaiting headers)
```

This usually means you need to setup proxy on your local machine. Ignoring all details about proxy, you need to make
the following Docker Desktop configuration. To do it(on Mac), click the little Docker logo on the top bar of your
screen. Click "Resources" and then hit "PROXIES" in the dropdown menu. You should see a configuration window like the
following:

![Error loading docker-proxy-config.png]({{ "/assets/img/docker-proxy-config.png" | relative_url}})

Note that configuring proxy in `~/.bashrc` is not the same thing as doing the config above. Docker Desktop doesn't look
at the `~/.bashrc` for proxy settings.
