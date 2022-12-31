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

## [SQL Style Guide](https://www.sqlstyle.guide/#naming-conventions)

### General

#### Do

* Use consistent and descriptive identifiers and names.
* Make judicious use of white space and indentation to make code easier to read.
* Store [ISO 8601][iso-8601] compliant time and date information
  (`YYYY-MM-DD HH:MM:SS.SSSSS`).
* Try to only use standard SQL functions instead of vendor-specific functions for
  reasons of portability.
* Keep code succinct and devoid of redundant SQL—such as unnecessary quoting or
  parentheses or `WHERE` clauses that can otherwise be derived.
* Include comments in SQL code where necessary. Use the C style opening `/*` and
  closing `*/` where possible otherwise precede comments with `--` and finish
  them with a new line.

```sql
SELECT file_hash  -- stored ssdeep hash
  FROM file_system
 WHERE file_name = '.vimrc';
```
```sql
/* Updating the file record after writing to the file */
UPDATE file_system
   SET file_modified_date = '1980-02-22 13:19:01.00000',
       file_size = 209732
 WHERE file_name = '.vimrc';
```

#### Avoid

* CamelCase—it is difficult to scan quickly.
* Descriptive prefixes or Hungarian notation such as `sp_` or `tbl`.
* Plurals—use the more natural collective term where possible instead. For example
  `staff` instead of `employees` or `people` instead of `individuals`.
* Quoted identifiers—if you must use them then stick to SQL-92 double quotes for
  portability (you may need to configure your SQL server to support this depending
  on vendor).
* Object-oriented design principles should not be applied to SQL or database
  structures.

### Naming conventions

#### General

* Ensure the name is unique and does not exist as a
  [reserved keyword][reserved-keywords].
* Keep the length to a maximum of 30 bytes—in practice this is 30 characters
  unless you are using a multi-byte character set.
* Names must begin with a letter and may not end with an underscore.
* Only use letters, numbers and underscores in names.
* Avoid the use of multiple consecutive underscores—these can be hard to read.
* Use underscores where you would naturally include a space in the name (first
  name becomes `first_name`).
* Avoid abbreviations and if you have to use them make sure they are commonly
  understood.

```sql
SELECT first_name
  FROM staff;
```

#### Tables

* Use a collective name or, less ideally, a plural form. For example (in order of
  preference) `staff` and `employees`.
* Do not prefix with `tbl` or any other such descriptive prefix or Hungarian
  notation.
* Never give a table the same name as one of its columns and vice versa.
* Avoid, where possible, concatenating two table names together to create the name
  of a relationship table. Rather than `cars_mechanics` prefer `services`.

#### Columns

* Always use the singular name.
* Where possible avoid simply using `id` as the primary identifier for the table.
* Do not add a column with the same name as its table and vice versa.
* Always use lowercase except where it may make sense not to such as proper nouns.

#### Aliasing or correlations

* Should relate in some way to the object or expression they are aliasing.
* As a rule of thumb the correlation name should be the first letter of each word
  in the object's name.
* If there is already a correlation with the same name then append a number.
* Always include the `AS` keyword—makes it easier to read as it is explicit.
* For computed data (`SUM()` or `AVG()`) use the name you would give it were it
  a column defined in the schema.

```sql
SELECT first_name AS fn
  FROM staff AS s1
  JOIN students AS s2
    ON s2.mentor_id = s1.staff_num;
```
```sql
SELECT SUM(s.monitor_tally) AS monitor_total
  FROM staff AS s;
```

#### Stored procedures

* The name must contain a verb.
* Do not prefix with `sp_` or any other such descriptive prefix or Hungarian
  notation.

#### Uniform suffixes

The following suffixes have a universal meaning ensuring the columns can be read
and understood easily from SQL code. Use the correct suffix where appropriate.

* `_id`—a unique identifier such as a column that is a primary key.
* `_status`—flag value or some other status of any type such as
  `publication_status`.
* `_total`—the total or sum of a collection of values.
* `_num`—denotes the field contains any kind of number.
* `_name`—signifies a name such as `first_name`.
* `_seq`—contains a contiguous sequence of values.
* `_date`—denotes a column that contains the date of something.
* `_tally`—a count.
* `_size`—the size of something such as a file size or clothing.
* `_addr`—an address for the record could be physical or intangible such as
  `ip_addr`.

### Query syntax

#### Reserved words

Always use uppercase for the [reserved keywords][reserved-keywords]
like `SELECT` and `WHERE`.

It is best to avoid the abbreviated keywords and use the full length ones where
available (prefer `ABSOLUTE` to `ABS`).

Do not use database server specific keywords where an ANSI SQL keyword already
exists performing the same function. This helps to make the code more portable.

```sql
SELECT model_num
  FROM phones AS p
 WHERE p.release_date > '2014-09-30';
```

#### White space

To make the code easier to read it is important that the correct complement of
spacing is used. Do not crowd code or remove natural language spaces.

##### Spaces

Spaces should be used to line up the code so that the root keywords all end on
the same character boundary. This forms a river down the middle making it easy for
the readers eye to scan over the code and separate the keywords from the
implementation detail. Rivers are [bad in typography][rivers], but helpful here.

```sql
(SELECT f.species_name,
        AVG(f.height) AS average_height, AVG(f.diameter) AS average_diameter
   FROM flora AS f
  WHERE f.species_name = 'Banksia'
     OR f.species_name = 'Sheoak'
     OR f.species_name = 'Wattle'
  GROUP BY f.species_name, f.observation_date)

  UNION ALL

(SELECT b.species_name,
        AVG(b.height) AS average_height, AVG(b.diameter) AS average_diameter
   FROM botanic_garden_flora AS b
  WHERE b.species_name = 'Banksia'
     OR b.species_name = 'Sheoak'
     OR b.species_name = 'Wattle'
  GROUP BY b.species_name, b.observation_date);
```

Notice that `SELECT`, `FROM`, etc. are all right aligned while the actual column
names and implementation-specific details are left aligned.

Although not exhaustive always include spaces:

* before and after equals (`=`)
* after commas (`,`)
* surrounding apostrophes (`'`) where not within parentheses or with a trailing
  comma or semicolon.

```sql
SELECT a.title, a.release_date, a.recording_date
  FROM albums AS a
 WHERE a.title = 'Charcoal Lane'
    OR a.title = 'The New Danger';
```

##### Line spacing

Always include newlines/vertical space:

* before `AND` or `OR`
* after semicolons to separate queries for easier reading
* after each keyword definition
* after a comma when separating multiple columns into logical groups
* to separate code into related sections, which helps to ease the readability of
  large chunks of code.

Keeping all the keywords aligned to the righthand side and the values left aligned
creates a uniform gap down the middle of the query. It also makes it much easier to
to quickly scan over the query definition.

```sql
INSERT INTO albums (title, release_date, recording_date)
VALUES ('Charcoal Lane', '1990-01-01 01:01:01.00000', '1990-01-01 01:01:01.00000'),
       ('The New Danger', '2008-01-01 01:01:01.00000', '1990-01-01 01:01:01.00000');
```

```sql
UPDATE albums
   SET release_date = '1990-01-01 01:01:01.00000'
 WHERE title = 'The New Danger';
```

```sql
SELECT a.title,
       a.release_date, a.recording_date, a.production_date -- grouped dates together
  FROM albums AS a
 WHERE a.title = 'Charcoal Lane'
    OR a.title = 'The New Danger';
```

#### Indentation

To ensure that SQL is readable it is important that standards of indentation
are followed.

##### Joins

Joins should be indented to the other side of the river and grouped with a new
line where necessary.

```sql
SELECT r.last_name
  FROM riders AS r
       INNER JOIN bikes AS b
       ON r.bike_vin_num = b.vin_num
          AND b.engine_tally > 2

       INNER JOIN crew AS c
       ON r.crew_chief_last_name = c.last_name
          AND c.chief = 'Y';
```

##### Subqueries

Subqueries should also be aligned to the right side of the river and then laid
out using the same style as any other query. Sometimes it will make sense to have
the closing parenthesis on a new line at the same character position as its
opening partner—this is especially true where you have nested subqueries.

```sql
SELECT r.last_name,
       (SELECT MAX(YEAR(championship_date))
          FROM champions AS c
         WHERE c.last_name = r.last_name
           AND c.confirmed = 'Y') AS last_championship_year
  FROM riders AS r
 WHERE r.last_name IN
       (SELECT c.last_name
          FROM champions AS c
         WHERE YEAR(championship_date) > '2008'
           AND c.confirmed = 'Y');
```

#### Preferred formalisms

* Make use of `BETWEEN` where possible instead of combining multiple statements
  with `AND`.
* Similarly use `IN()` instead of multiple `OR` clauses.
* Where a value needs to be interpreted before leaving the database use the `CASE`
  expression. `CASE` statements can be nested to form more complex logical structures.
* Avoid the use of `UNION` clauses and temporary tables where possible. If the
  schema can be optimised to remove the reliance on these features then it most
  likely should be.

```sql
SELECT CASE postcode
       WHEN 'BN1' THEN 'Brighton'
       WHEN 'EH1' THEN 'Edinburgh'
       END AS city
  FROM office_locations
 WHERE country = 'United Kingdom'
   AND opening_time BETWEEN 8 AND 9
   AND postcode IN ('EH1', 'BN1', 'NN1', 'KW1');
```

### Create syntax

When declaring schema information it is also important to maintain human-readable
code. To facilitate this ensure that the column definitions are ordered and
grouped together where it makes sense to do so.

Indent column definitions by four (4) spaces within the `CREATE` definition.

#### Choosing data types

* Where possible do not use vendor-specific data types—these are not portable and
  may not be available in older versions of the same vendor's software.
* Only use `REAL` or `FLOAT` types where it is strictly necessary for floating
  point mathematics otherwise prefer `NUMERIC` and `DECIMAL` at all times. Floating
  point rounding errors are a nuisance!

#### Specifying default values

* The default value must be the same type as the column—if a column is declared
  a `DECIMAL` do not provide an `INTEGER` default value.
* Default values must follow the data type declaration and come before any
  `NOT NULL` statement.

#### Constraints and keys

Constraints and their subset, keys, are a very important component of any
database definition. They can quickly become very difficult to read and reason
about though so it is important that a standard set of guidelines are followed.

##### Choosing keys

Deciding the column(s) that will form the keys in the definition should be a
carefully considered activity as it will effect performance and data integrity.

1. The key should be unique to some degree.
2. Consistency in terms of data type for the value across the schema and a lower
   likelihood of this changing in the future.
3. Can the value be validated against a standard format (such as one published by
   ISO)? Encouraging conformity to point 2.
4. Keeping the key as simple as possible whilst not being scared to use compound
   keys where necessary.

It is a reasoned and considered balancing act to be performed at the definition
of a database. Should requirements evolve in the future it is possible to make
changes to the definitions to keep them up to date.

##### Defining constraints

Once the keys are decided it is possible to define them in the system using
constraints along with field value validation.

###### General

* Tables must have at least one key to be complete and useful.
* Constraints should be given a custom name excepting `UNIQUE`, `PRIMARY KEY`
  and `FOREIGN KEY` where the database vendor will generally supply sufficiently
  intelligible names automatically.

###### Layout and order

* Specify the primary key first right after the `CREATE TABLE` statement.
* Constraints should be defined directly beneath the column they correspond to.
  Indent the constraint so that it aligns to the right of the column name.
* If it is a multi-column constraint then consider putting it as close to both
  column definitions as possible and where this is difficult as a last resort
  include them at the end of the `CREATE TABLE` definition.
* If it is a table-level constraint that applies to the entire table then it
  should also appear at the end.
* Use alphabetical order where `ON DELETE` comes before `ON UPDATE`.
* If it make senses to do so align each aspect of the query on the same character
  position. For example all `NOT NULL` definitions could start at the same
  character position. This is not hard and fast, but it certainly makes the code
  much easier to scan and read.

###### Validation

* Use `LIKE` and `SIMILAR TO` constraints to ensure the integrity of strings
  where the format is known.
* Where the ultimate range of a numerical value is known it must be written as a
  range `CHECK()` to prevent incorrect values entering the database or the silent
  truncation of data too large to fit the column definition. In the least it
  should check that the value is greater than zero in most cases.
* `CHECK()` constraints should be kept in separate clauses to ease debugging.

###### Example

```sql
CREATE TABLE staff (
    PRIMARY KEY (staff_num),
    staff_num      INT(5)       NOT NULL,
    first_name     VARCHAR(100) NOT NULL,
    pens_in_drawer INT(2)       NOT NULL,
                   CONSTRAINT pens_in_drawer_range
                   CHECK(pens_in_drawer BETWEEN 1 AND 99)
);
```

#### Designs to avoid

* Object-oriented design principles do not effectively translate to relational
  database designs—avoid this pitfall.
* Placing the value in one column and the units in another column. The column
  should make the units self-evident to prevent the requirement to combine
  columns again later in the application. Use `CHECK()` to ensure valid data is
  inserted into the column.
* [Entity–Attribute–Value][eav] (EAV) tables—use a specialist product intended for
  handling such schema-less data instead.
* Splitting up data that should be in one table across many tables because of
  arbitrary concerns such as time-based archiving or location in a multinational
  organisation. Later queries must then work across multiple tables with `UNION`
  rather than just simply querying one table.


### Appendix

#### Reserved keyword reference

A list of ANSI SQL (92, 99 and 2003), MySQL 3 to 5.x, PostgreSQL 8.1, MS SQL Server 2000, MS ODBC and Oracle 10.2 reserved keywords.

```sql
A
ABORT
ABS
ABSOLUTE
ACCESS
ACTION
ADA
ADD
ADMIN
AFTER
AGGREGATE
ALIAS
ALL
ALLOCATE
ALSO
ALTER
ALWAYS
ANALYSE
ANALYZE
AND
ANY
ARE
ARRAY
AS
ASC
ASENSITIVE
ASSERTION
ASSIGNMENT
ASYMMETRIC
AT
ATOMIC
ATTRIBUTE
ATTRIBUTES
AUDIT
AUTHORIZATION
AUTO_INCREMENT
AVG
AVG_ROW_LENGTH
BACKUP
BACKWARD
BEFORE
BEGIN
BERNOULLI
BETWEEN
BIGINT
BINARY
BIT
BIT_LENGTH
BITVAR
BLOB
BOOL
BOOLEAN
BOTH
BREADTH
BREAK
BROWSE
BULK
BY
C
CACHE
CALL
CALLED
CARDINALITY
CASCADE
CASCADED
CASE
CAST
CATALOG
CATALOG_NAME
CEIL
CEILING
CHAIN
CHANGE
CHAR
CHAR_LENGTH
CHARACTER
CHARACTER_LENGTH
CHARACTER_SET_CATALOG
CHARACTER_SET_NAME
CHARACTER_SET_SCHEMA
CHARACTERISTICS
CHARACTERS
CHECK
CHECKED
CHECKPOINT
CHECKSUM
CLASS
CLASS_ORIGIN
CLOB
CLOSE
CLUSTER
CLUSTERED
COALESCE
COBOL
COLLATE
COLLATION
COLLATION_CATALOG
COLLATION_NAME
COLLATION_SCHEMA
COLLECT
COLUMN
COLUMN_NAME
COLUMNS
COMMAND_FUNCTION
COMMAND_FUNCTION_CODE
COMMENT
COMMIT
COMMITTED
COMPLETION
COMPRESS
COMPUTE
CONDITION
CONDITION_NUMBER
CONNECT
CONNECTION
CONNECTION_NAME
CONSTRAINT
CONSTRAINT_CATALOG
CONSTRAINT_NAME
CONSTRAINT_SCHEMA
CONSTRAINTS
CONSTRUCTOR
CONTAINS
CONTAINSTABLE
CONTINUE
CONVERSION
CONVERT
COPY
CORR
CORRESPONDING
COUNT
COVAR_POP
COVAR_SAMP
CREATE
CREATEDB
CREATEROLE
CREATEUSER
CROSS
CSV
CUBE
CUME_DIST
CURRENT
CURRENT_DATE
CURRENT_DEFAULT_TRANSFORM_GROUP
CURRENT_PATH
CURRENT_ROLE
CURRENT_TIME
CURRENT_TIMESTAMP
CURRENT_TRANSFORM_GROUP_FOR_TYPE
CURRENT_USER
CURSOR
CURSOR_NAME
CYCLE
DATA
DATABASE
DATABASES
DATE
DATETIME
DATETIME_INTERVAL_CODE
DATETIME_INTERVAL_PRECISION
DAY
DAY_HOUR
DAY_MICROSECOND
DAY_MINUTE
DAY_SECOND
DAYOFMONTH
DAYOFWEEK
DAYOFYEAR
DBCC
DEALLOCATE
DEC
DECIMAL
DECLARE
DEFAULT
DEFAULTS
DEFERRABLE
DEFERRED
DEFINED
DEFINER
DEGREE
DELAY_KEY_WRITE
DELAYED
DELETE
DELIMITER
DELIMITERS
DENSE_RANK
DENY
DEPTH
DEREF
DERIVED
DESC
DESCRIBE
DESCRIPTOR
DESTROY
DESTRUCTOR
DETERMINISTIC
DIAGNOSTICS
DICTIONARY
DISABLE
DISCONNECT
DISK
DISPATCH
DISTINCT
DISTINCTROW
DISTRIBUTED
DIV
DO
DOMAIN
DOUBLE
DROP
DUAL
DUMMY
DUMP
DYNAMIC
DYNAMIC_FUNCTION
DYNAMIC_FUNCTION_CODE
EACH
ELEMENT
ELSE
ELSEIF
ENABLE
ENCLOSED
ENCODING
ENCRYPTED
END
END-EXEC
ENUM
EQUALS
ERRLVL
ESCAPE
ESCAPED
EVERY
EXCEPT
EXCEPTION
EXCLUDE
EXCLUDING
EXCLUSIVE
EXEC
EXECUTE
EXISTING
EXISTS
EXIT
EXP
EXPLAIN
EXTERNAL
EXTRACT
FALSE
FETCH
FIELDS
FILE
FILLFACTOR
FILTER
FINAL
FIRST
FLOAT
FLOAT4
FLOAT8
FLOOR
FLUSH
FOLLOWING
FOR
FORCE
FOREIGN
FORTRAN
FORWARD
FOUND
FREE
FREETEXT
FREETEXTTABLE
FREEZE
FROM
FULL
FULLTEXT
FUNCTION
FUSION
G
GENERAL
GENERATED
GET
GLOBAL
GO
GOTO
GRANT
GRANTED
GRANTS
GREATEST
GROUP
GROUPING
HANDLER
HAVING
HEADER
HEAP
HIERARCHY
HIGH_PRIORITY
HOLD
HOLDLOCK
HOST
HOSTS
HOUR
HOUR_MICROSECOND
HOUR_MINUTE
HOUR_SECOND
IDENTIFIED
IDENTITY
IDENTITY_INSERT
IDENTITYCOL
IF
IGNORE
ILIKE
IMMEDIATE
IMMUTABLE
IMPLEMENTATION
IMPLICIT
IN
INCLUDE
INCLUDING
INCREMENT
INDEX
INDICATOR
INFILE
INFIX
INHERIT
INHERITS
INITIAL
INITIALIZE
INITIALLY
INNER
INOUT
INPUT
INSENSITIVE
INSERT
INSERT_ID
INSTANCE
INSTANTIABLE
INSTEAD
INT
INT1
INT2
INT3
INT4
INT8
INTEGER
INTERSECT
INTERSECTION
INTERVAL
INTO
INVOKER
IS
ISAM
ISNULL
ISOLATION
ITERATE
JOIN
K
KEY
KEY_MEMBER
KEY_TYPE
KEYS
KILL
LANCOMPILER
LANGUAGE
LARGE
LAST
LAST_INSERT_ID
LATERAL
LEADING
LEAST
LEAVE
LEFT
LENGTH
LESS
LEVEL
LIKE
LIMIT
LINENO
LINES
LISTEN
LN
LOAD
LOCAL
LOCALTIME
LOCALTIMESTAMP
LOCATION
LOCATOR
LOCK
LOGIN
LOGS
LONG
LONGBLOB
LONGTEXT
LOOP
LOW_PRIORITY
LOWER
M
MAP
MATCH
MATCHED
MAX
MAX_ROWS
MAXEXTENTS
MAXVALUE
MEDIUMBLOB
MEDIUMINT
MEDIUMTEXT
MEMBER
MERGE
MESSAGE_LENGTH
MESSAGE_OCTET_LENGTH
MESSAGE_TEXT
METHOD
MIDDLEINT
MIN
MIN_ROWS
MINUS
MINUTE
MINUTE_MICROSECOND
MINUTE_SECOND
MINVALUE
MLSLABEL
MOD
MODE
MODIFIES
MODIFY
MODULE
MONTH
MONTHNAME
MORE
MOVE
MULTISET
MUMPS
MYISAM
NAME
NAMES
NATIONAL
NATURAL
NCHAR
NCLOB
NESTING
NEW
NEXT
NO
NO_WRITE_TO_BINLOG
NOAUDIT
NOCHECK
NOCOMPRESS
NOCREATEDB
NOCREATEROLE
NOCREATEUSER
NOINHERIT
NOLOGIN
NONCLUSTERED
NONE
NORMALIZE
NORMALIZED
NOSUPERUSER
NOT
NOTHING
NOTIFY
NOTNULL
NOWAIT
NULL
NULLABLE
NULLIF
NULLS
NUMBER
NUMERIC
OBJECT
OCTET_LENGTH
OCTETS
OF
OFF
OFFLINE
OFFSET
OFFSETS
OIDS
OLD
ON
ONLINE
ONLY
OPEN
OPENDATASOURCE
OPENQUERY
OPENROWSET
OPENXML
OPERATION
OPERATOR
OPTIMIZE
OPTION
OPTIONALLY
OPTIONS
OR
ORDER
ORDERING
ORDINALITY
OTHERS
OUT
OUTER
OUTFILE
OUTPUT
OVER
OVERLAPS
OVERLAY
OVERRIDING
OWNER
PACK_KEYS
PAD
PARAMETER
PARAMETER_MODE
PARAMETER_NAME
PARAMETER_ORDINAL_POSITION
PARAMETER_SPECIFIC_CATALOG
PARAMETER_SPECIFIC_NAME
PARAMETER_SPECIFIC_SCHEMA
PARAMETERS
PARTIAL
PARTITION
PASCAL
PASSWORD
PATH
PCTFREE
PERCENT
PERCENT_RANK
PERCENTILE_CONT
PERCENTILE_DISC
PLACING
PLAN
PLI
POSITION
POSTFIX
POWER
PRECEDING
PRECISION
PREFIX
PREORDER
PREPARE
PREPARED
PRESERVE
PRIMARY
PRINT
PRIOR
PRIVILEGES
PROC
PROCEDURAL
PROCEDURE
PROCESS
PROCESSLIST
PUBLIC
PURGE
QUOTE
RAID0
RAISERROR
RANGE
RANK
RAW
READ
READS
READTEXT
REAL
RECHECK
RECONFIGURE
RECURSIVE
REF
REFERENCES
REFERENCING
REGEXP
REGR_AVGX
REGR_AVGY
REGR_COUNT
REGR_INTERCEPT
REGR_R2
REGR_SLOPE
REGR_SXX
REGR_SXY
REGR_SYY
REINDEX
RELATIVE
RELEASE
RELOAD
RENAME
REPEAT
REPEATABLE
REPLACE
REPLICATION
REQUIRE
RESET
RESIGNAL
RESOURCE
RESTART
RESTORE
RESTRICT
RESULT
RETURN
RETURNED_CARDINALITY
RETURNED_LENGTH
RETURNED_OCTET_LENGTH
RETURNED_SQLSTATE
RETURNS
REVOKE
RIGHT
RLIKE
ROLE
ROLLBACK
ROLLUP
ROUTINE
ROUTINE_CATALOG
ROUTINE_NAME
ROUTINE_SCHEMA
ROW
ROW_COUNT
ROW_NUMBER
ROWCOUNT
ROWGUIDCOL
ROWID
ROWNUM
ROWS
RULE
SAVE
SAVEPOINT
SCALE
SCHEMA
SCHEMA_NAME
SCHEMAS
SCOPE
SCOPE_CATALOG
SCOPE_NAME
SCOPE_SCHEMA
SCROLL
SEARCH
SECOND
SECOND_MICROSECOND
SECTION
SECURITY
SELECT
SELF
SENSITIVE
SEPARATOR
SEQUENCE
SERIALIZABLE
SERVER_NAME
SESSION
SESSION_USER
SET
SETOF
SETS
SETUSER
SHARE
SHOW
SHUTDOWN
SIGNAL
SIMILAR
SIMPLE
SIZE
SMALLINT
SOME
SONAME
SOURCE
SPACE
SPATIAL
SPECIFIC
SPECIFIC_NAME
SPECIFICTYPE
SQL
SQL_BIG_RESULT
SQL_BIG_SELECTS
SQL_BIG_TABLES
SQL_CALC_FOUND_ROWS
SQL_LOG_OFF
SQL_LOG_UPDATE
SQL_LOW_PRIORITY_UPDATES
SQL_SELECT_LIMIT
SQL_SMALL_RESULT
SQL_WARNINGS
SQLCA
SQLCODE
SQLERROR
SQLEXCEPTION
SQLSTATE
SQLWARNING
SQRT
SSL
STABLE
START
STARTING
STATE
STATEMENT
STATIC
STATISTICS
STATUS
STDDEV_POP
STDDEV_SAMP
STDIN
STDOUT
STORAGE
STRAIGHT_JOIN
STRICT
STRING
STRUCTURE
STYLE
SUBCLASS_ORIGIN
SUBLIST
SUBMULTISET
SUBSTRING
SUCCESSFUL
SUM
SUPERUSER
SYMMETRIC
SYNONYM
SYSDATE
SYSID
SYSTEM
SYSTEM_USER
TABLE
TABLE_NAME
TABLES
TABLESAMPLE
TABLESPACE
TEMP
TEMPLATE
TEMPORARY
TERMINATE
TERMINATED
TEXT
TEXTSIZE
THAN
THEN
TIES
TIME
TIMESTAMP
TIMEZONE_HOUR
TIMEZONE_MINUTE
TINYBLOB
TINYINT
TINYTEXT
TO
TOAST
TOP
TOP_LEVEL_COUNT
TRAILING
TRAN
TRANSACTION
TRANSACTION_ACTIVE
TRANSACTIONS_COMMITTED
TRANSACTIONS_ROLLED_BACK
TRANSFORM
TRANSFORMS
TRANSLATE
TRANSLATION
TREAT
TRIGGER
TRIGGER_CATALOG
TRIGGER_NAME
TRIGGER_SCHEMA
TRIM
TRUE
TRUNCATE
TRUSTED
TSEQUAL
TYPE
UESCAPE
UID
UNBOUNDED
UNCOMMITTED
UNDER
UNDO
UNENCRYPTED
UNION
UNIQUE
UNKNOWN
UNLISTEN
UNLOCK
UNNAMED
UNNEST
UNSIGNED
UNTIL
UPDATE
UPDATETEXT
UPPER
USAGE
USE
USER
USER_DEFINED_TYPE_CATALOG
USER_DEFINED_TYPE_CODE
USER_DEFINED_TYPE_NAME
USER_DEFINED_TYPE_SCHEMA
USING
UTC_DATE
UTC_TIME
UTC_TIMESTAMP
VACUUM
VALID
VALIDATE
VALIDATOR
VALUE
VALUES
VAR_POP
VAR_SAMP
VARBINARY
VARCHAR
VARCHAR2
VARCHARACTER
VARIABLE
VARIABLES
VARYING
VERBOSE
VIEW
VOLATILE
WAITFOR
WHEN
WHENEVER
WHERE
WHILE
WIDTH_BUCKET
WINDOW
WITH
WITHIN
WITHOUT
WORK
WRITE
WRITETEXT
X509
XOR
YEAR
YEAR_MONTH
ZEROFILL
ZONE
```

#### Column data types

These are some suggested column data types to use for maximum compatibility between database engines.

##### Character types:

* CHAR
* CLOB
* VARCHAR

##### Numeric types

* Exact numeric types
    * BIGINT
    * DECIMAL
    * DECFLOAT
    * INTEGER
    * NUMERIC
    * SMALLINT
* Approximate numeric types
    * DOUBLE PRECISION
    * FLOAT
    * REAL

##### Datetime types

* DATE
* TIME
* TIMESTAMP

##### Binary types:

* BINARY
* BLOB
* VARBINARY

##### Additional types

* BOOLEAN
* INTERVAL
* XML

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
