---
layout: post
title: Object Storage Abstraction
tags: [Architecture, Object Storage]
color: rgb(224, 1, 152)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Object storage (also known as object-based storage) is a computer data storage architecture that manages data as
objects, as opposed to other storage architectures like file systems which manages data as a file hierarchy, and block
storage which manages data as blocks within sectors and tracks.

Each object typically includes:

* **the data itself**,
* **a variable amount of metadata**, and
* **a globally unique identifier**

Object storage systems allow retention of massive amounts of unstructured data in which data is _written once and read
once (or many times)_. Object storage is used for purposes such as storing objects like videos and photos on Facebook,
songs on Spotify, or files in online collaboration services, such as Dropbox. One of the limitations with object storage
is that it is not intended for transactional data, as object storage was not designed to replace NAS file access and
sharing; _it does not support the locking and sharing mechanisms needed to maintain a single, accurately updated version
of a file_.

## Architecture

### Abstraction of storage

**One of the design principles of object storage is to abstract some of the lower layers of storage away from the
administrators and applications**. Thus, data is exposed and managed as objects instead of files or blocks. Objects
contain additional descriptive properties which can be used for better indexing or management. Administrators do not
have to perform lower-level storage functions like constructing and managing logical volumes to utilize disk capacity or
setting RAID levels to deal with disk failure.

Object storage also allows the addressing and identification of individual objects by more than just file name and file
path. Object storage adds **a unique identifier** across the entire system, to support much larger namespaces and
eliminate name collisions.

## Inclusion of Rich Custom Metadata within the Object

Object storage explicitly **separates file metadata from data** to support additional capabilities. As opposed to fixed
metadata in file systems (filename, creation date, type, etc.), object storage provides for full function, custom,
object-level metadata in order to:

* Capture application-specific or user-specific information for better indexing purposes
* Support data-management policies (e.g. a policy to drive object movement from one storage tier to another)
* Centralize management of storage across many individual nodes and clusters
* Optimize metadata storage (e.g. encapsulated, database or key value storage) and caching/indexing (when authoritative
  metadata is encapsulated with the metadata inside the object) independently from the data storage (e.g. unstructured
  binary storage)

## Programmatic Data Management

Object storage provides programmatic interfaces to allow applications to manipulate data. At the base level, this
includes create, read, update and delete (CRUD) functions for basic read, write and delete operations. Some object
storage implementations go further, supporting additional functionality like object versioning, object replication,
life-cycle management and movement of objects between different tiers and types of storage. Most API implementations are
REST-based, allowing the use of many standard HTTP calls.

## Implementation

### Cloud Storage

The vast majority of cloud storage available in the market leverages is an object-storage architecture. Some notable
examples are Amazon Web Services S3 and Rackspace Files (whose code was donated in 2010 to Openstack project and
released as [OpenStack Swift](https://docs.openstack.org/swift/latest/))

> **Cloud storage** is a model of computer data storage in which the digital data is stored in logical
> [pools](https://en.wikipedia.org/wiki/Pool_(computer_science)), said to be on "the cloud". The physical storage spans
> multiple servers (sometimes in multiple locations), and the physical environment is typically owned and managed by a
> hosting company. These cloud storage providers are responsible for keeping the data available and accessible, and the
> physical environment secured, protected, and running. People and organizations buy or lease storage capacity from the
> providers to store user, organization, or application data.
