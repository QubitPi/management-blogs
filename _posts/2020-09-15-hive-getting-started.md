---
layout: post
title: Hive Getting Started
tags: [Hive, Hadoop]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/17-cover.png"
thumbnail: "assets/img/post-cover/17-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Overview

This post presents an overview of HDFS commands and a Hive command-line tutorial.

### Basic HDFS Shell Commands

Before you start using the Hive CLI, you should be familiar with some basic `hdfs` commands that you'll need to use
often when working on the grid. For a complete reference, see the
[File System Shell Guide](http://archive.cloudera.com/cdh/3/hadoop/file_system_shell.html)

#### Listing HDFS Files

To get a stat for files

    -bash-4.1$ hadoop fs -ls <args>

#### Copying Files

The following allows you to copy files from a source to a destination.

    -bash-4.1$ hadoop fs -cp URI [URI …] <dest>

You can also copy files from local to HDFS or from HDFS to local

    -bash-4.1$ hadoop fs -copyFromLocal <localsrc> URI 

    -bash-4.1$ hadoop fs -copyToLocal [-ignorecrc] [-crc] URI <localdst>

#### Making Directories

You can create HDFS directories by specifying paths or URIs

    -bash-4.1$ hadoop fs -mkdir <paths|URIs> 

#### Moving Files and Directories

You can use the following to move files from source to destination:

    -bash-4.1$ hadoop fs -mv URI [URI …] <dest>

The above command allows multiple sources as well, in which case `<dest>` needs to be directory. 
Note, moving files across file systems is not permitted. 

As with copying files, you can move local files to HDFS and vice versa with the following:

    -bash-4.1$ hadoop fs -moveToLocal [-crc] <src> <dst>
    -bash-4.1$ hadoop fs -moveFromLocal <localsrc> <dst>

#### Deleting Files/Directories

You can use the following to remove non-empty directories and files:

    -bash-4.1$ hadoop fs -rm [-skipTrash] URI [URI …]

To recursively delete files, use the following:

    -bash-4.1$ hadoop fs -rmr [-skipTrash] URI [URI …]

#### Viewing File Content

Using the following command, you view the content by copying the source to `stdout`:

    -bash-4.1$ hadoop fs -cat URI [URI …]

### Security Model

An organization contains sensitive data from many different properties. To preserve the integrity and confidentiality of
that data, users of the organization should be restricted by ACLs that define access permissions. For example, people
cannot create tables over the data that you do not have write permissions. For data that they do not have read
permissions, they will not be able to view the data or its metadata.

### Next Steps

Read the [Hive Language Manual](https://cwiki.apache.org/confluence/display/Hive/LanguageManual).
