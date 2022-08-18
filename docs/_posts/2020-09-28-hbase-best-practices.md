---
layout: post
title: HBase Best Practices
tags: [HBase, Hadoop]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/30-cover.png"
thumbnail: "assets/img/post-cover/30-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc} 

### Number of Column Families

Because HBase doesn't handle more than a few column families well, you are advised to keep the number of column families
in your schema low.

Why doesn't HBase handle many column families well? Because, in today's HBase, flushing and compactions are done on a
per Region basis, so if one column family is carrying the bulk of the data bringing on flushes, the adjacent families
will also be flushed though the amount of data they carry is small. Thus, flushing and compaction can cause needless I/O
loading although you can change both to work on a per column basis.

The best strategy though is to try to make do with one column family if possible. Only introduce a second and third
column family when data access is typically scoped by column. For example, if you generally only query one column family
or the other--not both at the one time.

### Length of Column Names, Row keys, and Attributes

In HBase, you use a combination of column name, row, and timestamps to map the coordinates of a value. This combination
of coordinates can be unnecessarily large if you do not minimize the sizes of the column name and row.

So, try to keep the column family names as small as possible, even to one character if possible. For example: "d" for
data/default.

Keeping row keys short is important, but you still want them to be functional to allow you to access data (e.g., Get vs.
Scan). A short key that is so short as to be unidentifiable is not better than a longer key with better get/scan
properties. Expect tradeoffs when designing row keys.

As with row keys, you might be tempted to use more descriptive names for attributes, but although a longer name is
easier to read, it's advisable to use a shorter (within reason) attribute name.

### Table Region Size

The region size can be set on a per-table basis using `setFileSize` on HTableDescriptor in the event where certain
tables require different region sizes than the configured default region size.

### Bloom Filters

Bloom filters, which can reduce disk reads, can be enabled for column families. Use
`HColumnDescriptor.setBloomFilterType(NONE | ROW | ROWCOL)` to enable blooms per column family. The default is
`NONE` for bloom filters. If `ROW`, the hash of the row will be added to the bloom on each insert. If `ROWCOL`,
the hash of the row, column family name, and column family qualifier will be added to the bloom on each key insert.

.. _schema_design-cf_block_size:

### Column Family Block Size

You can configure the block size for each column family in a table. This is useful if you cells have large values that
require block sizes. The default block size is 64k. Larger cell values require larger block sizes. Moreover, the larger
the block size, the smaller the store file indexes.  For example, if the blocksize is doubled then the resulting indexes
should be roughly halved.

### In-Memory Column Families

You can also define column families in memory, giving them higher priority in the
[block cache](http://hbase.apache.org/book/regionserver.arch.html#block.cache). Although data is still persisted to
disk, like any other column family, there is no guarantee that the entire table can be stored in memory.

### Compression

For production, you should use compression with column family definitions. HBase supports several compression formats.
Compression deflates data on disk. When it's in-memory (e.g., in the MemStore) or on the wire (e.g., transferring
between RegionServer and Client) it's inflated.

## Pre-Split Regions to Avoid Region Hotspots

As your HBase table size grows, it should be created with pre-split regions in order to avoid region hotspots. If
certain region servers get hammered by very intensive write/read operations, HBase may drop that region server because
the Zookeeper connection will timeout and  `YouAreDeadException` will be triggered. A better practice is to create a
fixed number of regions and evenly distribute those regions across all the region servers by estimating how big the
table will be and knowing the number of region servers you have. Of course, you also have to make sure your row keys are
well-distributed across all the regions.

For example, suppose you have 16 region servers and your table size is 1 TB. You can set the maximum file size of each
region to be 4GB (`hbase.hregion.max.filesize = 4294967296`), which will mean each of the 16 region servers will have
256 regions.

If you only have to pre-split regions once, you can use the HBase shell. For time series data, you should consider
creating tables monthly using automation or cron jobs. This will help with capacity planning, and you'll be able to add
more servers into the cluster as they are needed.

## Hashing Rows

The best method to use to distribute keys evenly across the regions is to hash it. If you need to recover the key or use
key scanning, consider hashing only part of the prefix or pad the key with a random string.

After you have the key prefix, you can start appending the key with some suffixes you would like to use for scanning
purposes. For time series data, it's not a bad idea to use a timestamp as the suffix if you want to do a time range
query for certain metrics identified by your row key prefix. HBase provides various filters for the rows that will make
your query easy.

## Column Scanning

Column scanning is not as fast as row scanning, but you can use a column to tag data for indexing. You can have as many
tags as you want and filter data based on the tags.

For example, suppose you are tracking log data. Your row key is `log_uid+timestamp`, and you want to index the IP
address in the log entries. After extracting the IP address from log entries, you can add a column named
`ip=xxx.xxx.xxx.xxx` for each row and write the offset of that IP address in the log entry into the cell. Using column
scanning or `ColumnFilter`, you can query IP addresses based on your log entries by looking up `ip=your_target_ip`.
You could also use `IP` as the column key and write the IP address into the cell, and create a Hive table to support a
SQL-like query from the client side.

If you want to find data marked with a timestamp, you could use your column as a time filter. Row scans won't work in
this case because we have to put timestamp at the beginning of the row key to make use of prefix scan. Typically, you
usually know a certain part of the row as the prefix and just want to find out all the matching rows within a certain
time range.
