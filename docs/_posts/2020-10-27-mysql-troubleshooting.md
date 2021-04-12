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
