---
layout: post
title: MySQL Troubleshooting
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/21-cover.png"
thumbnail: "assets/img/post-cover/21-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}
  
#### I Forgot Password

Let's say you want to login to MySQL shell as root, whose password is lost. You could do

Stop and start mysql with --skip-grant-tables

    service mysql.server stop
    service mysql.server start --skip-grant-tables

then connect to your mysqld without username/password

    mysql

Drop the user (e.g. `root`). After deleting the user, flush the mysql privileges. then create the user. Assuming we want
to create the user root @ localhost, these would be the commands:

    drop user admin@localhost;
    flush privileges;
    create user admin@localhost identified by 'foo'

then we can login to mysql with the new password

```bash
$ mysql -h localhost -u root -pfoo
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 12
Server version: 8.0.23 Homebrew

Copyright (c) 2000, 2021, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```

### [Implicit Default Handling](https://dev.mysql.com/doc/refman/8.0/en/data-type-defaults.html)

For data entry into a `NOT NULL` column that has no explicit `DEFAULT` clause, if an `INSERT` or `REPLACE` statement
includes no value for the column, or an `UPDATE` statement sets the column to `NULL`, MySQL handles the column according
to the **SQL mode** in effect at the time:

* If strict SQL mode is enabled, an error occurs for transactional tables and the statement is rolled back. For
  nontransactional tables, an error occurs, but if this happens for the second or subsequent row of a multiple-row
  statement, the preceding rows are inserted.
* If strict mode is not enabled, MySQL sets the column to the **implicit default value** for the column data type.
  
[Running through a quick check](https://dba.stackexchange.com/a/194135):

```sql
SET sql_mode='';
CREATE TABLE foo ( a int NOT NULL );
-- all of these insert the value 0
INSERT INTO foo (a) VALUES (DEFAULT);
INSERT INTO foo (a) VALUES (a);
INSERT INTO foo () VALUES ();
```

Whipe it and set strict mode,

```sql
TRUNCATE TABLE foo;
SET sql_mode='strict_all_tables';
```

Retry,

```sql
INSERT INTO foo (a) VALUES (DEFAULT);
ERROR 1364 (HY000): Field 'a' doesn't have a default value

-- still inserts "implicit default", doesn't gaf about strict mode.
INSERT INTO foo (a) VALUES (a);
Query OK, 1 row affected (0.01 sec)

INSERT INTO foo () VALUES ();
ERROR 1364 (HY000): Field 'a' doesn't have a default value
```

You'll see now only one row in the table which is a result of the
[goofy syntax](https://dba.stackexchange.com/q/194120/2639) that is still permitted with "strict mode".

### [Left Join Comes Out with Duplicate Rows](https://stackoverflow.com/questions/22769641/left-join-without-duplicate-rows-from-left-table)

The cause of the duplicate rows is that the left table has rows that have more than 1 associated rows in the right
table.

The solution is to `GROUP BY` the results.

For example, take a look at the following query:

```
tbl_contents

content_id  content_title    content_text
10002   New case Study   New case Study
10003   New case Study   New case Study
10004   New case Study   New case Study
10005   New case Study   New case Study
10006   New case Study   New case Study
10007   New case Study   New case Study
10008   New case Study   New case Study
10009   New case Study   New case Study
10010   SEO News Title   SEO News Text
10011   SEO News Title   SEO News Text
10012   Publish Contents SEO News Text
```

```
tbl_media

media_id    media_title  content_id
1000    New case Study   10012
1001    SEO News Title   10010
1002    SEO News Title   10011
1003    Publish Contents 10012
```

```sql
SELECT
    C.content_id, C.content_title, M.media_id
FROM
    tbl_contents C LEFT JOIN tbl_media M ON M.content_id = C.content_id 
ORDER BY C.Content_DatePublished ASC
```

```
10002   New case Study  2014-03-31 13:39:29.280 NULL
10003   New case Study  2014-03-31 14:23:06.727 NULL
10004   New case Study  2014-03-31 14:25:53.143 NULL
10005   New case Study  2014-03-31 14:26:06.993 NULL
10006   New case Study  2014-03-31 14:30:18.153 NULL
10007   New case Study  2014-03-31 14:30:42.513 NULL
10008   New case Study  2014-03-31 14:31:56.830 NULL
10009   New case Study  2014-03-31 14:35:18.040 NULL
10010   SEO News Title  2014-03-31 15:22:15.983 1001
10011   SEO News Title  2014-03-31 15:22:30.333 1002
10012   Publish         2014-03-31 15:25:11.753 1000
10012   Publish         2014-03-31 15:25:11.753 1003
```

We see that `10012` are coming up twice..., because some rows in tbl_contents has more than 1 associated rows in
tbl_media. To eliminate the duplicate:

```sql
SELECT 
    C.content_id,
    C.content_title,
    C.Content_DatePublished,
    M.media_id
FROM 
    tbl_contents C LEFT JOIN tbl_media M ON M.content_id = C.content_id 
GROUP BY
    C.content_id
ORDER BY
    C.Content_DatePublished ASC
```

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
