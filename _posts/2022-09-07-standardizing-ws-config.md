---
layout: post
title: (WIP) Standardizing Backend Software Configuration
tags: [Management]
color: rgb(255, 105, 132)
feature-img: "assets/img/post-cover/17-cover.png"
thumbnail: "assets/img/post-cover/17-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Why Standards?
--------------

The main purpose of conforming to standards is to ensure that products, systems and organisations are safe, reliable and
good for the environment. Standards help make systems interoperable. Standards are important in effective communication 
between disparate systems. 

Systems that are developed to standards also make them more credible. Software that is developed to a set of standards
can be more cost-effective because it becomes easier to implement and to learn for those who have been exposed to the 
standards previously.

In my team, we make every home-brewed backend system component adopt
[aeonbits configuration](http://owner.aeonbits.org/), which has been used, as my early years working at Yahoo, by one of
a crucial team in the company's ad pipeline backend system.

We will define two interfaces

1. **Configuration**
2. **Configuration Access Layer**


Standard of Defining Configuration
----------------------------------

In our system, **configuration** is defined as _one or set of ".properties" files residing on the JVM classpath_

### .properties

We define ".properties" as a file extension for files mainly used in our Java-based sub-systems to store the
configurable parameters of an application. They are also used for storing strings for
[Internationalization and localization](https://en.wikipedia.org/wiki/Internationalization_and_localization); these are 
known as PropertyResource Bundles.

Each parameter is stored as a pair of strings, one storing the name of the parameter (called the **key**), and the other 
storing the **value**. For example

```properties
port=80
hostname=foobar.com
maxThreads=100
```

Unlike many popular file formats, there is no [RFC](https://en.wikipedia.org/wiki/Request_for_Comments) for
".properties" files, simply due to the simplicity of the format.

#### Format Standard

Each line in a ".properties" file normally stores a single property. Several formats are possible for each line,
including

* `key=value`
* `key = value`
* `key:value`
* `key value`

Single-quotes or double-quotes are considered part of the string. Trailing space is significant

Comment lines in ".properties" files are denoted by the number sign (`#`) or the exclamation mark (`!`) as the first non 
blank character, in which all remaining text on that line is ignored. The backwards slash is used to escape a character.
An example of a properties file is provided below.

```properties
# You are reading a comment in ".properties" file.
! The exclamation mark can also be used for comments.
# Lines with "properties" contain a key and a value separated by a delimiting character.
# There are 3 delimiting characters: '=' (equal), ':' (colon) and whitespace (space, \t and \f).
website = https://en.wikipedia.org/
language : English
topic .properties files
# A word on a line will just create a key with no value.
empty
# White space that appears between the key, the value and the delimiter is ignored.
# This means that the following are equivalent (other than for readability).
hello=hello
hello = hello
# Keys with the same name will be overwritten by the key that is the furthest in a file.
# For example the final value for "duplicateKey" will be "second".
duplicateKey = first
duplicateKey = second
# To use the delimiter characters inside a key, you need to escape them with a \.
# However, there is no need to do this in the value.
delimiterCharacters\:\=\ = This is the value for the key "delimiterCharacters\:\=\ "
# Adding a \ at the end of a line means that the value continues to the next line.
multiline = This line \
continues
# If you want your value to include a \, it should be escaped by another \.
path = c:\\wiki\\templates
# This means that if the number of \ at the end of the line is even, the next line is not included in the value. 
# In the following example, the value for "evenKey" is "This is on one line\".
evenKey = This is on one line\\
# This line is a normal comment and is not included in the value for "evenKey"
# If the number of \ is odd, then the next line is included in the value.
# In the following example, the value for "oddKey" is "This is line one and\#This is line two".
oddKey = This is line one and\\\
# This is line two
# White space characters are removed before each line.
# Make sure to add your spaces before your \ if you need them on the next line.
# In the following example, the value for "welcome" is "Welcome to Wikipedia!".
welcome = Welcome to \
          Wikipedia!
# If you need to add newlines and carriage returns, they need to be escaped using \n and \r respectively.
# You can also optionally escape tabs with \t for readability purposes.
valueWithEscapes = This is a newline\n and a carriage return\r and a tab\t.
# You can also use Unicode escape characters (maximum of four hexadecimal digits).
# In the following example, the value for "encodedHelloInJapanese" is "こんにちは".
encodedHelloInJapanese = \u3053\u3093\u306b\u3061\u306f
# But with more modern file encodings like UTF-8, you can directly use supported characters.
helloInJapanese = こんにちは
```






