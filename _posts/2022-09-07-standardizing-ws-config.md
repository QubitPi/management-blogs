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


Standards of Defining Configuration Access Layer
------------------------------------------------

> **The Access Layer is defined by [OWNER API](http://owner.aeonbits.org/), which defines a Java interface associated to
> a properties file.

For example, suppose a properties file "**ServerConfig.properties**" is defined as follows:

```properties
port=80
hostname=foobar.com
maxThreads=100
```

To access this properties file programmatically, one shall define a **Properties Mapping Interface**,
**ServerConfig.java**, in the same package (For instance, if the mapping interface is called
`com.foo.bar.ServerConfig`, OWNER will try to associate it to `com.foo.bar.ServerConfig.properties`, loading from the 
classpath.):

```java
import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    
    int port();
    
    String hostname();
    
    @DefaultValue("42")
    int maxThreads();
}
```

Notice that the interface above extends from **Config**, that is a marker interface recognized by OWNER as valid to work 
with.

The properties names defined in the properties file will be associated to the methods in the Java class having the same 
name. For instance, the property "port" defined in the properties file will be associated to the method `int port()` in 
the Java class, the property "hostname" will be associated to the method `String hostname()` and the appropriate type 
conversion will apply automatically, so the method `port()` will return an integer while the method `hostname()` will 
return a string, since the interface is defined in this way.


Standards of Using Configuration Access Layer
---------------------------------------------

At this point, one shall create the **ServerConfig object** and use it:

```java
ServerConfig config = ConfigFactory.create(ServerConfig.class);
cfg.hostname();
config.port();
...
```


Standards of Interaction Between Configuration Definition and Configuration Access Layer
----------------------------------------------------------------------------------------

### Default Configuration

When a property is missing from [configuration definition](#standard-of-defining-configuration), the default value must
be defined at compile-time. This is accomplished through **@DefaultValue** annotation.

For example,

```java
import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    
    int port();
    
    String hostname();
    
    @DefaultValue("42")
    int maxThreads();
}
```

In this case, the "maxThread" key is missing from the properties file. The @DefaultValue annotation automatically
converts a default value of "42" into integer, since `maxThreads()` returns an int.

### Key Mapping

It is not required to have the key name in config definition and its corresponding accessor name in access layer to be
the same. The implementation of this option is through the @Key annotation, with which we can customize the property
keys:

```properties
# Example of property file 'ServerConfig.properties'
server.http.port=80
server.host.name=foobar.com
server.max.threads=100
```

We then need to map this property name to the associated method using the `@Key` annotation.

```java
/*
 * Example of ServerConfig.java interface mapping the previous 
 * properties file.
 */
public interface ServerConfig extends Config {
    
    @Key("server.http.port")
    int port();

    @Key("server.host.name")
    String hostname();

    @Key("server.max.threads");
    @DefaultValue("42")
    int maxThreads();
}
```

### Undefined Properties
