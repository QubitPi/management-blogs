---
layout: post
title: Fixing the Homebrew 128 Operation Timed Out Problem
tags: [Mac]
category: FINALIZED
color: rgb(43, 164, 78)
feature-img: "assets/img/post-cover/29-cover.png"
thumbnail: "assets/img/post-cover/29-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


A common homebrew issue is when we execute `brew install` or `brew tap`, we receive the error like the following:

```bash
brew tap weaveworks/tap
Running `brew update --auto-update`...
==> Tapping weaveworks/tap
Cloning into '/.../weaveworks/homebrew-tap'...
fatal: unable to connect to github.com:
github.com[0: 20.205.243.166]: errno=Operation timed out

Error: Failure while executing; `git clone https://github.com/weaveworks/homebrew-tap /.../weaveworks/homebrew-tap --origin=origin --template=` exited with 128.
```

First we should try to download an arbitrary big file to confirm our network is working. In fact, we could also manually
clone the git repo related to the error and see if that works. In this example:

```bash
git clone git@github.com:weaveworks/homebrew-tap.git
```

If this works as well, try switching from http scheme to ssh using 

```bash
git config --global url.ssh://git@github.com/.insteadOf https://github.com/
```
