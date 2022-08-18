---
layout: post
title: Reading and Writing Avro Files from the Command Line
tags: [Apache Avro]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/26-cover.png"
thumbnail: "assets/img/post-cover/26-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Reading and Writing Avro Files from the Command Line

[Apache Avro](http://avro.apache.org/) is becoming one of the most popular data serialization formats nowadays, and this
holds true particularly for Hadoop-based big data platforms because tools like Pig, Hive and of course Hadoop itself
natively support reading and writing data in Avro format. Many users seem to enjoy Avro but I have heard many complaints
about not being able to conveniently read or write Avro files with command line tools – "Avro is nice, but why do I have
to write Java or Python code just to quickly see what's in a binary Avro file, or discover at least its Avro schema?"

To those users it comes as a surprise that Avro actually ships with exactly such command line tools but apparently they
are not prominently advertised or documented as such. In this short article I will show a few hands-on examples on how
to read, write, compress and convert data from and to binary Avro using Avro Tools 1.7.4.

## What We Want To Do

Here is an overview of what we want to do:

* We will start with an example Avro schema and a corresponding data file in plain-text JSON format.
* We will use Avro Tools to convert the JSON file into binary Avro, without and with compression (Snappy), and from
  binary Avro back to JSON.
  
## Getting Avro Tools

You can get a copy of the latest stable Avro Tools jar file from the
[Avro Releases](http://avro.apache.org/releases.html#Download) page. The actual file is in the `java` subdirectory of a
given Avro release version. Here is a direct link to
[avro-tools-1.7.4.jar](http://www.us.apache.org/dist/avro/avro-1.7.4/java/avro-tools-1.7.4.jar) (11 MB) on the US Apache
mirror site.

Save `avro-tools-1.7.4.jar` to a directory of your choice. I will use `~/avro-tools-1.7.4.jar` for the examples shown
below.

## Tools Included in Avro Tools

Just run Avro Tools without any parameters to see what's included:

```
$ java -jar ~/avro-tools-1.7.4.jar
[...snip...]
Available tools:
      compile  Generates Java code for the given schema.
       concat  Concatenates avro files without re-compressing.
   fragtojson  Renders a binary-encoded Avro datum as JSON.
     fromjson  Reads JSON records and writes an Avro data file.
     fromtext  Imports a text file into an avro data file.
      getmeta  Prints out the metadata of an Avro data file.
    getschema  Prints out schema of an Avro data file.
          idl  Generates a JSON schema from an Avro IDL file
       induce  Induce schema/protocol from Java class/interface via reflection.
   jsontofrag  Renders a JSON-encoded Avro datum as binary.
      recodec  Alters the codec of a data file.
  rpcprotocol  Output the protocol of a RPC service
   rpcreceive  Opens an RPC Server and listens for one message.
      rpcsend  Sends a single RPC message.
       tether  Run a tethered mapreduce job.
       tojson  Dumps an Avro data file as JSON, one record per line.
       totext  Converts an Avro data file to a text file.
  trevni_meta  Dumps a Trevni file's metadata as JSON.
trevni_random  Create a Trevni file filled with random instances of a schema.
trevni_tojson  Dumps a Trevni file as JSON.
```

Likewise run any particular tool without parameters to see its usage/help output. For example, here is the help of the
`fromjson` tool:

```
$ java -jar ~/avro-tools-1.7.4.jar fromjson
Expected 1 arg: input_file
Option                                  Description
------                                  -----------
--codec                                 Compression codec (default: null)
--schema                                Schema
--schema-file                           Schema File
```

Note that most of the tools write to `STDOUT`, so normally you would like to pipe their output to a file via the Bash
`>` redirection operator (particularly when working with large files).

## Example Data

In the next sections I will use the following example data to demonstrate Avro Tools. You can also download the example
files from https://github.com/miguno/avro-cli-examples.

### Avro Schema

The schema below defines a tuple of `(username, tweet, timestamp)` as the format of our example data records

File: `twitter.avsc`:

```
{
  "type" : "record",
  "name" : "twitter_schema",
  "namespace" : "com.miguno.avro",
  "fields" : [ {
    "name" : "username",
    "type" : "string",
    "doc"  : "Name of the user account on Twitter.com"
  }, {
    "name" : "tweet",
    "type" : "string",
    "doc"  : "The content of the user's Twitter message"
  }, {
    "name" : "timestamp",
    "type" : "long",
    "doc"  : "Unix epoch time in seconds"
  } ],
  "doc:" : "A basic schema for storing Twitter messages"
}
```

### Data records in JSON format

Here is some corresponding example data with two records that follow the schema defined in the previous section. We
store this data in the file `twitter.json`.

Example data in `twitter.json` in JSON format:

```json
{"username":"miguno","tweet":"Rock: Nerf paper, scissors is fine.","timestamp": 1366150681}
{"username":"BlizzardCS","tweet":"Works as intended.  Terran is IMBA.","timestamp": 1366154481}
```

## Converting To and From Binary Avro

### JSON to Binary Avro

Without compression:

    java -jar ~/avro-tools-1.7.4.jar fromjson --schema-file twitter.avsc twitter.json > twitter.avro
    
With Snappy compression:

    java -jar ~/avro-tools-1.7.4.jar fromjson --codec snappy --schema-file twitter.avsc twitter.json > twitter.snappy.avro

### Binary Avro to JSON

The same command will work on both uncompressed and compressed data.

    java -jar ~/avro-tools-1.7.4.jar tojson twitter.avro > twitter.json
    java -jar ~/avro-tools-1.7.4.jar tojson twitter.snappy.avro > twitter.json
    
### Retrieve Avro Schema from Binary Avro

The same command will work on both uncompressed and compressed data.

    java -jar ~/avro-tools-1.7.4.jar getschema twitter.avro > twitter.avsc
    java -jar ~/avro-tools-1.7.4.jar getschema twitter.snappy.avro > twitter.avsc
