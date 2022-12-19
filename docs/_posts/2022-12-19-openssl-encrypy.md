---
layout: post
title: Using OpenSSL to encrypt messages and files on Linux
tags: [Security, OpenSSL]
color: rgb(43, 164, 78)
feature-img: "assets/img/post-cover/32-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
authors: [linuxconfig.org, QubitPi]
excerpt_separator: <!--more-->
---

OpenSSL is a powerful cryptography toolkit. Many of us have already used OpenSSL for creating RSA Private Keys or CSR 
(Certificate Signing Request). However, did you know that we can use OpenSSL to benchmark our computer speed or that we
can also encrypt files or messages? This post will provide you with some simple to follow tips on how to encrypt
messages and files using OpenSSL.

<!--more-->

* TOC
{:toc}


Encrypt and Decrypt Messages
----------------------------

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
**echo "V2VsY29tZSB0byBMaW51eENhcmVlci5jb20K" | openssl enc -base64 -d**, which essentially does nothing about "hiding
secrets" at all_.

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

If we wish to store OpenSSL's output to a file instead of STDOUT simply use STDOUT redirection `>`. When storing
encrypted output to a file we can also omit the `-a` option as we no longer need the output to be ASCII text based:

```bash
echo "LinuxCareer.com" | openssl enc -aes-256-cbc > openssl.dat
```

```bash
$ echo "LinuxCareer.com" | openssl enc -aes-256-cbc > openssl.dat
enter aes-256-cbc encryption password:
Verifying - enter aes-256-cbc encryption password:
$ file openssl.dat 
openssl.dat: openssl enc'd data with salted password
```

To decrypt the "openssl.dat" file back to its original message use:

```bash
openssl enc -aes-256-cbc -d -in openssl.dat 
```

```bash
$ openssl enc -aes-256-cbc -d -in openssl.dat 
enter aes-256-cbc decryption password:
LinuxCareer.com
```


Encrypt and Decrypt File
------------------------

To encrypt files with OpenSSL is as simple as encrypting messages. The only difference is that instead of the echo
command we use the **-in** option with the actual file we would like to encrypt and **-out** option, which will instruct 
OpenSSL to store the encrypted file under a given name:

> ⚠️ Ensure that the encrypted output file is given a different filename than the original plain input file. It is also 
> recommended to do few encrypt/decrypt test runs on dummy data before encrypting important content.

```bash
openssl enc -aes-256-cbc -in /etc/services -out services.dat
```

To decrypt back our services file use:

```bash
$ openssl enc -aes-256-cbc -d -in services.dat > services.txt
enter aes-256-cbc decryption password:
```


Encrypt and Decrypt Directory
-----------------------------

In case that we needed to use OpenSSL to encrypt an entire directory we would, first need to create gzip **tarball** and
then encrypt the tarball with the above method or we can do both at the same time by using pipe:

```bash
$ tar cz /etc | openssl enc -aes-256-cbc -out etc.tar.gz.dat
tar: Removing leading `/' from member names
enter aes-256-cbc encryption password:
Verifying - enter aes-256-cbc encryption password:
```

To decrypt and extract the entire etc/ directory to you current working directory use:

```bash
$ openssl enc -aes-256-cbc -d -in etc.tar.gz.dat | tar xz
enter aes-256-cbc decryption password:
```

**The method above can be quite useful for automated encrypted backups**.


Using Public and Private keys
-----------------------------

In this section we will show how to encrypt and decrypt files using public and private keys. First we need to generate 
private and public keys. This can simply be done by:

```bash
openssl genrsa -out private_key.pem 1024
```


From the private key we can then generate public key:

```bash
openssl rsa -in private_key.pem -out public_key.pem -outform PEM -pubout
```

At this point we should have both private and public key available in our current working directory.

```bash
$ ls
private_key.pem  public_key.pem
```

Next, we create some sample file called "encrypt.txt" with any arbitrary text:

```bash
$ echo "Welcome to LinuxCareer.com" > encrypt.txt
$ cat encrypt.txt 
Welcome to LinuxCareer.com
```

Now we are ready to encrypt this file with public key:

```bash
openssl rsautl -encrypt -inkey public_key.pem -pubin -in encrypt.txt -out encrypt.dat 
```

i.e.

```bash
$ openssl rsautl -encrypt -inkey public_key.pem -pubin -in encrypt.txt -out encrypt.dat 
$ ls
encrypt.dat  encrypt.txt  private_key.pem  public_key.pem
$ file encrypt.dat 
encrypt.dat: openssl enc'd data with salted password
```

As you can see our new encrypt.dat file is no longer text files. To decrypt this file we need to use private key:

```bash
openssl rsautl -decrypt -inkey private_key.pem -in encrypt.dat -out new_encrypt.txt
```

i.e.

```bash
$ openssl rsautl -decrypt -inkey private_key.pem -in encrypt.dat -out new_encrypt.txt
$ cat new_encrypt.txt
Welcome to LinuxCareer.com
```

The syntax above is quite intuitive. As you can see we have decrypted a file encrypt.dat to its original form and save
it as new_encrypt.txt. You can for example combine this syntax with encrypting directories example above to create 
automated encrypted backup script.
