---
layout: post
title: Programming MySQL
tags: [MySQL, Database]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/10-cover.png"
thumbnail: "assets/img/post-cover/10-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## References

* [MySQL Tutorial](https://www.mysqltutorial.org/)

## Shell

### Adjust Display Settings of MySQL Command Line?

You may notice that command line result of mysql is not displaying results properly. Some columns of table are in the
first line and some are in the second line. Output is also broken into two rows. How do we adjust these settings so that
it properly display results?

We can use the `\G` command (instead of the `;`) at the end of our SQL queries:

```sql
SELECT * FROM USER \G
```

It will display your table in row form instead of column form.

### JOIN

![Error loading Visual_SQL_JOINS_V2.png!]({{ "/assets/img/Visual_SQL_JOINS_V2.png" | relative_url}})

#### Self Join

We know how to join a table to the other tables using "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", or "CROSS JOIN" clause.
However, there is a special case that you need to join a table to itself, which is known as a "self join".

The self join is often used to query hierarchical data or to compare a row with other rows within the same table.

To perform a self join, you must use table aliases to not repeat the same table name twice in a single query. Note that
referencing a table twice or more in a query without using table aliases will cause an error.

##### Self Join Examples

Let's take a look at the employees table:

![Error loading employees.png]({{ "/assets/img/employees.png" | relative_url}})

The table employees stores not only employees data but also the organization structure data. The `reportsto` column is
used to determine the manager id of an employee.

###### Self Join using INNER JOIN Clause

To get the whole organization structure, you can join the employees table to itself using the `employeeNumber` and
`reportsTo` columns. The employees table has two roles: one is the Manager and the other is Direct Reports.

```sql
SELECT 
    CONCAT(manager.lastName, ', ', manager.firstName) AS Manager,
    CONCAT(report.lastName, ', ', report.firstName) AS 'Direct report'
FROM
    employees report
INNER JOIN employees manager ON 
    manager.employeeNumber = report.reportsTo
ORDER BY 
    Manager;
```

![Error loading report.png]({{ "/assets/img/report.png" | relative_url}})

The output only shows the employees who have a manager. However, you don't see the President because his name is
filtered out due to the INNER JOIN clause.

###### Self Join using LEFT JOIN Clause

The President is the employee who does not have any manager to report to, i.e. the value in the `reportsTo` column is
`NULL`.

The following statement uses the LEFT JOIN clause instead of INNER JOIN to include the President:

```sql
SELECT 
    IFNULL(CONCAT(manager.lastname, ', ', manager.firstname), 'Top Manager') AS 'Manager',
    CONCAT(report.lastname, ', ', report.firstname) AS 'Direct report'
FROM
    employees report
LEFT JOIN employees manager ON 
    manager.employeeNumber = report.reportsto
ORDER BY 
    manager DESC;
```

![Error loading MySQL-Self-Join-with-LEFT-JOIN-technique.png]({{ "/assets/img/MySQL-Self-Join-with-LEFT-JOIN-technique.png" | relative_url}})

###### Using Self Join to Compare Successive Rows

By using the MySQL self join, you can display a list of customers who locate in the same city by joining the customers
table to itself.

```sql
SELECT 
    c1.city, 
    c1.customerName, 
    c2.customerName
FROM
    customers c1
INNER JOIN customers c2 ON 
    c1.city = c2.city
    AND c1.customername > c2.customerName
ORDER BY 
    c1.city;
```

![Error loading MySQL-Self-Join-cutomers-located-in-the-same-city.png]({{ "/assets/img/MySQL-Self-Join-cutomers-located-in-the-same-city.png" | relative_url}})

In this example, the table customers is joined to itself using the following join conditions:

* `c1.city = c2.city` makes sure that both customers have the same city.
* `c.customerName > c2.customerName` ensures that no same customer is included.

### Connecting to MySQL From the Command Line

    mysql -u USERNAME -pPASSWORD -h HOSTNAMEORIP DATABASENAME --default-character-set=utf8

The `--default-character-set=utf8` option allows the UTF-8 character to be displayed properly in console

#### How to Pass Password to mysql Command Line in a More "Secure" Way

[Putting passwords on the command line](#connecting-to-mysql-from-the-command-line) is in-secure, because anyone with
access to `/proc` can trivially read them as long as the program is running.

The safest way would be to create a new [config file](https://dev.mysql.com/doc/refman/8.0/en/option-file-options.html)
and pass it to `mysql` using either the `--defaults-file=` or `--defaults-extra-file=` command line option. The
difference between the two is that the latter is read in addition to the default config files whereas with the former
only the one file passed as the argument is used. The additional configuration file should contain something similar to:

```
[client]
user=foo
password=P@55w0rd
```

> 📋 Make sure that you secure this file.

Then run:

    mysql --defaults-extra-file=<path to the new config file> --default-character-set=utf8 -h HOSTNAMEORIP DATABASENAME

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

### Listing Tables

### Listing Tables by Size

```sql
SELECT 
    table_name AS `Table`, 
    round(((data_length + index_length) / 1024 / 1024), 2) AS `Size in MB` 
FROM information_schema.TABLES 
WHERE table_schema = "$DB_NAME"
ORDER BY `Size in MB`; 
```

where `$DB_NAME` is the name of the database whose tables are to be listed

[To check the size of a single table (`$TABLE_NAME`) of a database (`$DB_NAME`)](https://stackoverflow.com/a/9620273):

```sql
SELECT 
    table_name AS `Table`, 
    round(((data_length + index_length) / 1024 / 1024), 2) AS `Size in MB` 
FROM information_schema.TABLES 
WHERE table_schema = "$DB_NAME" AND table_name = "$TABLE_NAME";
ORDER BY `Size in MB` 
```

### Delete Duplicate Rows

MySQL provides [`DELETE JOIN`](https://www.mysqltutorial.org/mysql-delete-join/) statement that allows you to remove
duplicate rows quickly.

The following statement deletes duplicate rows and keeps the highest id:

```sql
DELETE t1 FROM contacts t1
INNER JOIN contacts t2 
WHERE 
    t1.id < t2.id AND 
    t1.email = t2.email;
```

You can execute the query that find duplicate emails again to verify the delete:

```sql
SELECT 
    email, 
    COUNT(email)
FROM
    contacts
GROUP BY 
    email
HAVING 
    COUNT(email) > 1;
```

The query returns an empty set, which means that the duplicate rows have been deleted.

### SELECT INTO Variable

To store query result in one or more variables, you use the `SELECT INTO variable` syntax:

```sql
SELECT 
    c1, c2, c3, ...
INTO 
    @v1, @v2, @v3,...
FROM 
    table_name
WHERE 
    condition;
```

In this syntax:

* `c1`, `c2`, and `c3` are columns or expressions that you want to select and store into the variables.
* `@v1`, `@v2`, and `@v3` are the variables which store the values from `c1`, `c2`, and `c3`, respectively.

The number of variables must be the same as the number of columns or expressions in the select list. In addition, the
query must returns zero or one row.

If the query return no rows, MySQL issues a warning of no data and the value of the variables remain unchanged.

In case the query returns multiple rows, MySQL issues an error like the following:

```
Error Code: 1172. Result consisted of more than one row
```

To ensure that the query always returns maximum one row, you use the `LIMIT 1` clause to limit the result set to a
single row.

### Copy a Table from One Database to Another?

```sql
CREATE TABLE db1.table1 SELECT * FROM db2.table1
```

## Scripting

### Calling SQL Script File from Other SQL Script File

You can use `source` command. So your script will be something like:

```sql
use your_db;
source script/s1.sql;
source script/s2.sql;
-- so on, so forth
```

### User-Defined Variables

https://dev.mysql.com/doc/refman/8.0/en/user-variables.html

User Defined Varibles can be used across scrips like this:

`main.sql`:

```sql
-- User-Defined Variables
SET @tom_id = 1;
SET @jack_id = 2;

source add_data_to_person_table.sql;
```

`person.sql`:

```sql
INSERT INTO
    Person (id, name)
VALUES
    (@tom_id, "TOME"),
    (@jack_id, "JACK");
```

## MySQL Functions

MySQL has many built-in functions.

### IF

Returns a value if a condition is `TRUE`, or another value if a condition is `FALSE`. For example, Return "YES" if the
condition is TRUE, or "NO" if the condition is FALSE:

```sql
SELECT IF(500 < 1000, "YES", "NO");
```

This function is useful if we would like to replicate a table and update some column values on the flight:

```sql
INSERT INTO some_table(column1, column2, column3)
SELECT column1, column2, IF(column3 = "Blue", "Dark Blue", "Dark Color")
FROM some_table;
```

In the example above, all rows with column value "Blue" will have "Dark Blue" as the new value for that column; all
other rows will be changed to "Dark Color".

We could have richer modifications, other than 2-branch modification, using
[CASE](https://www.w3schools.com/mysql/func_mysql_case.asp)

## Migration

### Copying Tables or Databases from One MySQL Server to Another

We could use `mysqldump` to export the data from one MySQL instance and `mysql` command line utility to load it into
another.

Suppose we have a `person` table in "prod" database hosted in "192.168.1.100" and we would like to migrate this table
data into a dev instance ("192.168.1.101") for testing purposes.

    mysqldump --column-statistics=0 -u prod-user-name -pprodPassword -h 192.168.1.100 prod-database person --default-character-set=utf8 > dump.sql
    mysql -u dev-user-name -pdevPwssword -h 192.168.1.101 --port=32228 dev-database < dump.sql

> 📋 The `--column-statistics=0` is set so that it prevents runtime error of
> 
> ```
> mysqldump: Couldn't execute. Unknown table 'column_statistics' in information_schema
> ```
