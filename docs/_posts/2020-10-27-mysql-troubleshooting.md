---
layout: post
title: MySQL Troubleshooting
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/dark-radio.png"
thumbnail: "assets/img/pexels/design-art/dark-radio.png"
author: QubitPi
excerpt_separator: <!--more-->
---


<!--more-->

* TOC
{:toc}

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