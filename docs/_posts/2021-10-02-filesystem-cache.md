---
layout: post
title: Filesystem Cache
tags: [Linux, Performance]
color: rgb(240,78,35)
feature-img: "assets/img/post-cover/6-cover.png"
thumbnail: "assets/img/post-cover/6-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

FS-Cache is a persistent local cache that can be used by file systems to take data retrieved from over the network and
cache it on local disk. This helps minimize network traffic for users accessing data from a file system mounted over the network (for example, NFS).

The following diagram is a high-level illustration of how FS-Cache works:

![Error Loading fs-cache.png]({{ "/assets/img/fs-cache.png" | relative_url}})

FS-Cache is designed to be as transparent as possible to the users and administrators of a system. It allows a file
system on a server to interact directly with a client's local cache without creating an overmounted file system. With
NFS, a mount option instructs the client to mount the NFS share with FS-cache enabled.

FS-Cache does not alter the basic operation of a file system that works over the network - it merely provides that file
system with a persistent place in which it can cache data. For instance, a client can still mount an NFS share whether
or not FS-Cache is enabled. In addition, cached NFS can handle files that won't fit into the cache (whether individually
or collectively) as files can be partially cached and do not have to be read completely up front. FS-Cache also hides
all I/O errors that occur in the cache from the client file system driver.

To provide caching services, FS-Cache needs a **cache back-end**. A cache back-end is a storage driver configured to
provide caching services (i.e. **cachefiles**). In this case, FS-Cache requires a mounted block-based file system that
supports `bmap` and extended attributes (e.g. ext3) as its cache back-end.

FS-Cache cannot arbitrarily cache any file system, whether through the network or otherwise: the shared file system's
driver must be altered to allow interaction with FS-Cache, data storage/retrieval, and metadata set up and validation.
FS-Cache needs indexing keys and coherency data from the cached file system to support persistence: indexing keys to
match file system objects to cache objects, and coherency data to determine whether the cache objects are still valid.

### Performance Guarantee

FS-Cache does not guarantee increased performance, however it ensures consistent performance by avoiding network
congestion. Using a cache back-end incurs a performance penalty: for example, cached NFS shares add disk accesses to
cross-network lookups. While FS-Cache tries to be as asynchronous as possible, there are synchronous paths (e.g. reads)
where this isn't possible.

For example, using FS-Cache to cache an NFS share between two computers over an otherwise unladen GigE network will not
demonstrate any performance improvements on file access. Rather, NFS requests would be satisfied faster from server
memory rather than from local disk.

The use of FS-Cache, therefore, is a compromise between various factors. If FS-Cache is being used to cache NFS traffic, 
for instance, it may slow the client down a little, but massively reduce the network and server loading by satisfying
read requests locally without consuming network bandwidth.
