---
layout: post
title: Using OpenSSL to encrypt messages and files on Linux
tags: [Security, OpenSSL]
color: rgb(43, 164, 78)
feature-img: "assets/img/post-cover/31-cover.png"
thumbnail: "assets/img/post-cover/31-cover.png"
author: linuxconfig.org
excerpt_separator: <!--more-->
---

OpenSSL is a powerful cryptography toolkit. Many of us have already used OpenSSL for creating RSA Private Keys or CSR 
(Certificate Signing Request). However, did you know that we can use OpenSSL to benchmark our computer speed or that we
can also encrypt files or messages? This post will provide you with some simple to follow tips on how to encrypt
messages and files using OpenSSL.

<!--more-->

* TOC
{:toc}

## Encrypt and Decrypt Messages

First we can start by encrypting simple messages. The following linux command will encrypt a message “Welcome to 
LinuxCareer.com” using Base64 Encoding:
