---
layout: post
title: Reading Notes - Evolutionary Database Design
tags: [Database, Design]
color: rgb(242, 146, 33)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Why Evolutionary Database Development?

Instead of trying to design your database schema up front early in the project, you instead build it up throughout the
life of a project to reflect the changing requirements defined by your stakeholders. Like it or not, requirements change
as your project progresses. Traditional approaches have denied this fundamental reality and have tried to "manage
change," a euphemism for preventing change, through various means. Practitioners of modern development techniques
instead choose to embrace change and follow techniques that enable them to evolve their work in step with evolving
requirements. Programmers have adopted techniques such as TDD, refactoring, and
[AMDD](http://www.agilemodeling.com/essays/amdd.htm) and have built new development tools to make this easy. It has also
been realized that we also need techniques and tools to support evolutionary database development.

Advantages to an evolutionary approach to database development include the following:

## Database Refactoring

You need a systematic way to refactor your code, including **good tools** and **techniques** to do so. _Most modern
integrated development environments (IDEs) now support code refactoring to some extent_, which is a good start. However,
to make refactoring work in practice, you also need to develop an **up-to-date regression-testing suite** that validates
that your code still works

You should refactor your code mercilessly because you are most productive when you are working on high-quality source
code.

A **database refactoring** is a simple change to a database schema that improves its design while retaining both its
behavioral and informational semantics. In other words, you cannot add new functionality or break existing
functionality, nor can you add new data or change the meaning of existing data. A database schema includes both 
**structural aspects**, such as table and view definitions, and **functional aspects**, such as stored procedures and
triggers.

### Single-Application Database Environments

Let's start by working through an example of moving a column from one table to another within a single-application
database environment. This is the simplest situation that you will ever be in, because you have complete control over
both the database schema and the application source code that accesses it. The implication is that you can refactor both
your database schema and your application code simultaneously. You do not need to support both the original and new
database schemas in parallel because only the one application accesses your database.

In this scenario, we suggest that two people work together as a pair; one person should have application programming
skills, and the other database development skills, and ideally both people have both sets of skills.

To put database refactoring into context, let's step through a quick example. You have been working on a banking
application for a few weeks and have noticed something strange about the Customer and Account tables depicted in figure
below:

![Error loading database-evolution-old-bank-schema.png]({{ "/assets/img/database-evolution-old-bank-schema.png" | relative_url}})

Does it really make sense that the Balance column be part of the Customer table? No, so let's apply the Move Column
refactoring to improve our database design.

To apply the "Move Column" refactoring in the development sandbox:

Step 1. The pair first runs all the tests to see that they pass

Step 2. They write a test because they are taking a Test-Driven Development (TDD) approach. A likely test is to access a
value in the Account.Balance column.

Step 3. After running the tests and seeing them fail, they introduce the Account.Balance column.

Step 4. They rerun the tests and see that the tests now pass.

Step 5. They then refactor the existing tests, which verify that customer deposits work properly with the
Account.Balance column rather than the Customer.Balance column.

Step 6. They see that these tests fail.

Step 7. and therefore rework the deposit functionality to work with Account.Balance.

Step 8. They make similar changes to other code within the tests suite and the application, such as withdrawal logic,
that currently works with Customer.Balance.

Step 9. After the application is running again, they then back up the data in Customer.Balance, for safety purposes

Step 10. Copy the data from Customer.Balance into the appropriate row of Account.Balance

Step 11. They rerun their tests to verify that the data migration has safely occurred

Step 12. To complete the schema changes, they drop the Customer.Balance column and then rerun all tests and fix anything
as necessary

### Multi-Application Database Environments

This situation is more difficult because the individual applications have new releases deployed at different times over
the next year and a half. To implement this database refactoring, you do the same sort of work that you did for the
single-application database environment, except that you **do not delete the Customer.Balance column right away**.
Instead, you run both columns in parallel during a "transition period" of at least 1.5 years to give the development
teams time to update and redeploy all of their applications. It is very important to add two triggers,
**SynchronizeCustomerBalance** and **SynchronizeAccountBalance**, which are run in production during the transition
period to keep the two columns in sync.

After the transition period, you remove the original column plus the trigger(s), resulting in the final database schema.
You remove these things only after sufficient testing to ensure that it is safe to do so. At this point, your
refactoring is complete.

### Maintaining Semantics

When you refactor a database schema, you must maintain both the **informational and behavioral semantics**. You should
neither add anything nor take anything away. Informational semantics refers to the meaning of the information within the
database from the point of view of the users of that information.

Similarly, with respect to behavioral semantics, the goal is to keep the black-box functionality

### Database Smells

Fowler (1997) introduced the concept of a "code smell," a common category of problem in your code that indicates the
need to refactor it. Common code smells include switch statements, long methods, duplicated code, and feature envy.
Similarly, there are common database smells:

* **Multipurpose Column** - If a column is being used for several purposes, it is likely that extra code exists to ensure
  that the source data is being used the "right way," often by checking the values of one or more other columns. An
  example is a column used to store either someone's birth date if he or she is a customer or the start date if that
  person is an employee. Worse yet, you are likely constrained in the functionality that you can now supportfor example,
  how would you store the birth date of an employee?
* **Multipurpose Table** - Similarly, when a table is being used to store several types of entities, there is likely a
  design flaw. An example is a generic Customer table that is used to store information about both people and
  corporations. The problem with this approach is that data structures for people and corporations differ. People have a
  first, middle, and last name, for example; whereas a corporation simply has a legal name. A generic Customer table
  would have columns that are NULL for some kinds of customers but not others.
* **Redundant Data** - Redundant data is a serious problem in _operational databases_ because when data is stored in
  several places, the opportunity for inconsistency occurs
* **Tables With Too Many Columns** - When a table has many columns, it is indicative that the table lacks cohesion that
  it is trying to store data from several entities. Perhaps your Customer table contains columns to store three
  different addresses (shipping, billing, seasonal) or several phone numbers (home, work, cell, and so on). You likely
  need to normalize this structure by adding Address and PhoneNumber tables.
* **Tables With Too Many Rows** - Large tables are indicative of performance problems. For example, it is time-consuming
  to search a table with millions of rows. You may want to split the table vertically by moving some columns into
  another table, or split it horizontally by moving some rows into another table. Both strategies reduce the size of the
  table, potentially improving performance.
* **"Smart" Columns** - A smart column is one in which different positions within the data represent different concepts.
  For example, if the first four digits of the client ID indicate the client's home branch, then client ID is a smart
  column because you can parse it to discover more granular information (for example, home branch ID). Another example
  includes a text column used to store XML data structures; clearly, you can parse the XML data structure for smaller
  data fields. Smart columns often need to be reorganized into their constituent data fields at some point so that the
  database can easily deal with them as separate elements.
* **Fear of Change** - If you are afraid to change your database schema because you are afraid to break something. Fear
  of change is a good indication that you have a serious technical risk on your hands, one that will only get worse over
  time.
  
### Making It Easier to Refactor Your Database Schema

An effective way to decrease the coupling that your database is involved with is to encapsulate access to it. You do
this by having external programs access your database via persistence layers

## The Process of Database Refactoring

### Verify That a Database Refactoring Is Appropriate

> 📋 A Software Dev team often comes with a separate DBA team. The following DB refactoring is discussed with the DBA
> team is present

* **Does the refactoring make sense?** Perhaps the existing table structure is correct. It is common for developers to
  either disagree with, or to simply misunderstand, the existing design of a database. This misunderstanding could lead
  them to believe that the design needs to change when it really does not. The DBA should have a good knowledge of the
  project team's database, other corporate databases, and will know whom to contact about issues such as this.
  Therefore, they will be in a better position to determine whether the existing schema is the best one. Furthermore,
  the DBA often understands the bigger picture of the overall enterprise, providing important insight that may not be
  apparent when you look at it from the point of view of the single project. However, in our example, it appears that
  the schema needs to change.
* **Is the change actually needed now?** DBA, should b


This is usually a "gut call" based on her previous experience with the application developer. Does Eddy have a good reason for making the schema change? Can Eddy explain the business requirement that the change supports? Does the requirement feel right? Has Eddy suggested good changes in the past? Has Eddy changed his mind several days later, requiring Beverley to back out of the change? Depending on this assessment,Beverley may suggest that Eddy think the change through some more or may decide to continue working with him, but will wait for a longer period of time before they actually apply the change in the project-integration environment (Chapter 4) if they believe the change will need to be reversed.