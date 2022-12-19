---
layout: post
title: Using OpenSSL to encrypt messages and files on Linux
tags: [Security, OpenSSL]
color: rgb(43, 164, 78)
feature-img: "assets/img/post-cover/32-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
author:
  - linuxconfig.org
  - QubitPi
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

First we can start by encrypting simple messages. The following linux command will encrypt a message "Welcome to
LinuxCareer.com" using Base64 Encoding:

```bash
$ echo "Welcome to LinuxCareer.com" | openssl enc -base64
V2VsY29tZSB0byBMaW51eENhcmVlci5jb20K
```

The output of the above command is an encrypted string containing encoded message "Welcome to LinuxCareer.com". To
decrypt encoded string back to its original message we need to reverse the order and attach -d option for decryption:

```bash
$ echo "V2VsY29tZSB0byBMaW51eENhcmVlci5jb20K" | openssl enc -base64 -d
Welcome to LinuxCareer.com
```

The encryption above is simple to use, however, it lacks an important feature of a password, which should be used for 
encryption. _The procedure above simply exposes the original password in another plain text in the form of of
**echo "V2VsY29tZSB0byBMaW51eENhcmVlci5jb20K" | openssl enc -base64 -d**, which essentially does nothing about "hiding"
at all_.

What will do next is, instead of decrypting using `openssl enc -base64 -d`, decrypting with a **password**. Try to
decrypt the following string with a password "pass":

```bash
echo "U2FsdGVkX181xscMhkpIA6J0qd76N/nSjjTc9NrDUC0CBSLpZQxQ2Db7ipd7kexj" | openssl enc -aes-256-cbc -d -a
```

i.e.

```bash
$ echo "U2FsdGVkX181xscMhkpIA6J0qd76N/nSjjTc9NrDUC0CBSLpZQxQ2Db7ipd7kexj" | openssl enc -aes-256-cbc -d -a
enter aes-256-cbc decryption password: pass
LinuxCareer.com
```

We see that the original message "LinuxCareer.com" got decrypted with the password "pass". To create an encrypted
message with a password as the one above we can use the following linux command:

```bash
echo "LinuxCareer.com" | openssl enc -aes-256-cbc -a
```

i.e.

```bash
$ echo "LinuxCareer.com" | openssl enc -aes-256-cbc -a
enter aes-256-cbc encryption password:
Verifying - enter aes-256-cbc encryption password:
U2FsdGVkX185E3H2me2D+qmCfkEsXDTn8nCn/4sblr8=
```

If you wish to store OpenSSL’s output to a file instead of STDOUT simply use STDOUT redirection “>”. When storing encrypted output to a file you can also omit -a option as you no longer need the output to be ASCII text based: