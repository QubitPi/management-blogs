---
layout: post
title: Docker cAdvisor
tags: [Docker]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/28-cover.png"
thumbnail: "assets/img/post-cover/28-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## [cAdvisor](https://github.com/google/cadvisor) - The `docker stats` UI

Although [cAdvisor](https://github.com/google/cadvisor) has some prelimilary (useful though) UI. It also offers

1. [RESTful API to query container stats](https://github.com/google/cadvisor/blob/master/docs/api.md)
2. [Export capability to common data storage, such as Elasticsearch](https://github.com/google/cadvisor/blob/master/docs/storage/README.md)

To pull the image and run it:

    sudo docker run   --volume=/:/rootfs:ro   --volume=/var/run/docker.sock:/var/run/docker.sock:rw  --volume=/sys:/sys:ro   --volume=/var/lib/docker/:/var/lib/docker:ro   --volume=/dev/disk/:/dev/disk:ro   --publish=8080:8080   --detach=true   --name=cadvisor   --privileged   --device=/dev/kmsg   gcr.io/cadvisor/cadvisor:v0.36.0

![cAdvisor Screenshot 1]({{ "/assets/img/cadvisor-1.png" | relative_url}})
![cAdvisor Screenshot 2]({{ "/assets/img/cadvisor-2.png" | relative_url}})

### [docker-container-stats](https://github.com/virtualzone/docker-container-stats)

[cAdvisor](https://github.com/google/cadvisor) is good for customizing container monitoring, but it's heavy. A
quick-and-lightweight option would be [docker-container-stats](https://github.com/virtualzone/docker-container-stats)

![docker-container-stats Screenshot]({{ "/assets/img/docker-container-stats.png" | relative_url}})
