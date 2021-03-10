---
layout: post
title: Programming MySQL
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Shell

### Connecting to MySQL From the Command Line

    mysql -u USERNAME -pPASSWORD -h HOSTNAMEORIP DATABASENAME --default-character-set=utf8

The `--default-character-set=utf8` option allows the UTF-8 character to be displayed properly in console

### Find databases containing a particular table in MySQL

Let's say you would like to locate a table whose name is `foo`:

```
SELECT
    table_schema AS database_name
FROM
    information_schema.tables
WHERE
    table_type = 'BASE TABLE'
    AND table_name = 'foo'
ORDER BY
    table_schema;
```

### Search for a Column from Database

```
SELECT
    table_name,
    column_name
FROM
    information_schema.columns
WHERE
    column_name like '%SearchedColumn%'
```

### Save MySQL Query Output to File

Try executing the query from the your local client and redirect the output to a local file destination:

```
mysql -user -pass -e "select cols from table where cols not null" > /tmp/output
```

### [Conditional Insert](https://stackoverflow.com/a/913929)

Suppose we have `x_table` with `columns (instance, user, item)` where instance ID is unique. I want to insert a new row
only if the user already does not have a given item.

For example trying to insert `instance=919191 user=123 item=456`

```
Insert into x_table (instance, user, item) values (919191, 123, 456)
    ONLY IF there are no rows where user=123 and item=456 
```

If your DBMS does not impose limitations on which table you select from when you execute an insert, try:

```
INSERT INTO x_table(instance, user, item) 
    SELECT 919191, 123, 456
        FROM dual
        WHERE NOT EXISTS (SELECT * FROM x_table
                             WHERE user = 123 
                               AND item = 456)
```

In this, `dual` is a table with one row only (found originally in Oracle, now in mysql too). The logic is that the
`SELECT` statement generates a single row of data with the required values, but only when the values are not already
found.

Alternatively, look at the MERGE statement.

### Implement Paging

[From the MySQL documentation](https://dev.mysql.com/doc/refman/8.0/en/select.html#id1026131):

The `LIMIT` clause can be used to constrain the number of rows returned by the `SELECT` statement. `LIMIT` takes one or
two numeric arguments, which must both be non-negative integer constants, with these exceptions:

* Within prepared statements, LIMIT parameters can be specified using ? placeholder markers.
* Within stored programs, LIMIT parameters can be specified using integer-valued routine parameters or local variables.

With two arguments, the first argument specifies the offset of the first row to return, and the second specifies the
maximum number of rows to return. The offset of the initial row is 0 (not 1):

```
    SELECT * FROM tbl LIMIT 5,10;  # Retrieve rows 6-15
```

With one argument, the value specifies the number of rows to return from the beginning of the result set:

```
    SELECT * FROM tbl LIMIT 5;     # Retrieve first 5 rows
```
