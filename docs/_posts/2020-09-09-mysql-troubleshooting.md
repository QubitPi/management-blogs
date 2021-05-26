---
layout: post
title: MySQL Troubleshooting
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/12-cover.png"
thumbnail: "assets/img/post-cover/12-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

### Loading Large File into Database Causes `MySQL Server has gone away`

According to https://dev.mysql.com/doc/refman/8.0/en/gone-away.html and personal experience, the most common reason is
that you send a query to the server that is incorrect or [too large](#large-packet). If MySQL Server receives a packet
that is too large or out of order, it assumes that something has gone wrong with the client and closes the connection.
If you need big queries (for example, if you are working with big BLOB columns), you can increase the query limit by
setting the server's `max_allowed_packet` variable, which has a default value of 64MB. You may also need to increase the
maximum packet size on the client end.

**Both the client and the server have their own `max_allowed_packet` variable, so if you want to handle big packets,
you must increase this variable both in the client and in the server**.

If you are using the mysql **client program**, its default `max_allowed_packet` variable is 16MB. To set a larger value,
start mysql like this:

    mysql --max_allowed_packet=32M

That sets the packet size to 32MB.

The server's default `max_allowed_packet` value is 64MB. You can increase this if the server needs to handle big
queries (for example, if you are working with big BLOB columns). For example, to set the variable to 128MB, start the
server like this:

    mysqld --max_allowed_packet=128M

You can also use an option file to set `max_allowed_packet`. For example, to set the size for the server to 128MB, add
the following lines in an option file:

    [mysqld]
    max_allowed_packet=128M

It is safe to increase the value of this variable because the extra memory is allocated only when needed. For example,
MySQL Server allocates more memory only when you issue a long query or when MySQL Server must return a large result row.
The small default value of the variable is a precaution to catch incorrect packets between the client and server and
also to ensure that you do not run out of memory by using large packets accidentally.

You can also get strange problems with large packets if you are using large BLOB values but have not given MySQL Sever
access to enough memory to handle the query. If you suspect this is the case, try adding `ulimit -d 256000` to the
beginning of the `mysqld_safe` script and restarting MySQL Server.

#### Large Packet

A communication packet is

* a single SQL statement sent to the MySQL server, or
* a single row that is sent to the client, or
* a binary log event sent from a master replication server to a slave.

When a MySQL client or the MySQL server receives a packet bigger than `max_allowed_packet` bytes, it issues an
`ER_NET_PACKET_TOO_LARGE` error and closes the connection.

### Illegal mix of collations (latin1_swedish_ci,IMPLICIT) and (utf8mb4_general_ci,COERCIBLE) for operation '='

Resolving this issues requires understanding of [MySQL collations](https://www.mysqltutorial.org/mysql-collation/).

Go to the database related to the issue and issue the following command

    mysql> show variables WHERE variable_name like "col%";
    +----------------------+-------------------+
    | Variable_name        | Value             |
    +----------------------+-------------------+
    | collation_connection | latin1_swedish_ci |
    | collation_database   | utf8_general_ci   |
    | collation_server     | latin1_general_ci |
    +----------------------+-------------------+
    3 rows in set (0.02 sec)

You see the database collation is for UTF8.

When you see the error "Illegal mix of collations...", it is complaining about a table whose collation is inconsistent
with what's configured for "collation_database" shown above.

Go to the table that throws the "Illegal mix of collations..." error and issue the following command:

    mysql> SELECT table_schema, table_name, column_name, character_set_name, collation_name
        -> FROM information_schema.columns
        -> WHERE table_name = 'xyz'
        -> ORDER BY table_schema, table_name,ordinal_position;

    +-------------------+------------+--------------+--------------------+-------------------+
    | table_schema      | table_name | column_name  | character_set_name | collation_name    |
    +-------------------+------------+--------------+--------------------+-------------------+
    | ...               | xyz        | id           | NULL               | NULL              |
    | ...               | xyz        | name         | latin1             | latin1_swedish_ci |
    | ...               | xyz        | gender       | NULL               | NULL              |
    | ...               | xyz        | title        | latin1             | latin1_swedish_ci |
    | ...               | xyz        | department   | latin1             | latin1_swedish_ci |
    | ...               | xyz        | salary       | NULL               | NULL              |
    | ...               | xyz        | email        | latin1             | latin1_swedish_ci |
    | ...               | xyz        | phone        | latin1             | latin1_swedish_ci |
    | ...               | xyz        | age          | NULL               | NULL              |
    | ...               | xyz        | birthday     | latin1             | latin1_swedish_ci |
    | ...               | xyz        | location     | latin1             | latin1_swedish_ci |
    +-------------------+------------+--------------+--------------------+-------------------+
    11 rows in set (0.39 sec)

You see the table collation has "latin1_swedish_ci", which is not UTF8. You will alter this table so that is aligns with
database level config like this:

    ALTER TABLE xyz CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_bin';

In case you would like to alter database level collation, you could try something like

    /* Set collations of system variables */
    SET @@collation_connection = UTF8MB4_GENERAL_CI;
    SET @@collation_database = UTF8MB4_GENERAL_CI;
    SET @@collation_server = UTF8MB4_GENERAL_CI;
