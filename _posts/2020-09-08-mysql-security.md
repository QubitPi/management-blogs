---
layout: post
title: MySQL Security
tags: [MySQL, Database, Security]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/11-cover.png"
thumbnail: "assets/img/post-cover/11-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

### SQL Injection and How to Prevent It?

Despite being one of the best-known vulnerabilities, SQL Injection continues to rank on the top spot of the infamous
[OWASP Top 10's list](https://owasp.org/www-project-top-ten/) – now part of the more general Injection class.

#### How Applications Become Vulnerable to SQL Injection?

Injection attacks work because, for many applications, the only way to execute a given computation is to dynamically
generate code that is in turn run by another system or component. If in the process of generating this code we use
untrusted data without proper sanitization, we leave an open door for hackers to exploit

This statement may sound a bit abstract, so let's take look at how this happens in practice with an example:

```java
public List<AccountDTO> unsafeFindAccountsByCustomerId(String customerId) throws SQLException {
    // UNSAFE !!! DON'T DO THIS !!!
    String sql = "select "
            + "customer_id,acc_number,branch_id,balance "
            + "from Accounts where customer_id = '"
            + customerId
            + "'";
    Connection c = dataSource.getConnection();
    ResultSet rs = c.createStatement().executeQuery(sql);
    // ...
}
```

The problem with this code is obvious: we've put the customerId‘s value into the query with no validation at all.
Nothing bad will happen if we're sure that this value will only come from trusted sources, but can we?

Let's imagine that this function is used in a REST API implementation for an account resource. Exploiting this code is
trivial: all we have to do is to send a value that, when concatenated with the fixed part of the query, change its
intended behavior::

    curl -X GET 'http://localhost:8080/accounts?customerId=abc%27%20or%20%271%27=%271'

Assuming the customerId parameter value goes unchecked until it reaches our function, here's what we'd receive::

    abc' or '1' = '1

When we join this value with the fixed part, we get the final SQL statement that will be executed:

    SELECT
        customer_id,
        acc_number,
        branch_id,
        balance
    FROM
        Accounts
    WHERE
        customerId = 'abc' OR '1' = '1'

Probably not what we've wanted...

#### I'm using JPA. I'm Safe, Right?

This is a common misconception. JPA and other ORMs relieves us from creating hand-coded SQL statements, but they won't
prevent us from writing vulnerable code.

Let's see how the JPA version of the previous example looks:

```java
public List<AccountDTO> unsafeJpaFindAccountsByCustomerId(String customerId) {
    String jql = "from Account where customerId = '" + customerId + "'";
    TypedQuery<Account> q = em.createQuery(jql, Account.class);
    return q.getResultList()
            .stream()
            .map(this::toAccountDTO)
            .collect(Collectors.toList());
}
```

The same issue we've pointed before is also present here: we're using unvalidated input to create a JPA query, so we're
exposed to the same kind of exploit here.

#### Prevention Techniques

Now that we know what a SQL injection is, let's see how we can protect our code from this kind of attack. Here we're
focusing on a couple of very effective techniques available in Java and other JVM languages, but similar concepts are
available to other environments, such as PHP, .Net, Ruby and so forth.

For those looking for a complete list of available techniques, including database-specific ones, the
[OWASP](https://www.owasp.org/) Project maintains a
[SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html),
which is a good place to learn more about the subject.

##### Parameterized Queries

This technique consists of using prepared statements with the question mark placeholder ("`?`") in our queries
whenever we need to insert a user-supplied value. This is very effective and, unless there's a bug in the JDBC driver's
implementation, immune to exploits.

Let's rewrite our example function to use this technique:

```java
public List<AccountDTO> safeFindAccountsByCustomerId(String customerId) throws Exception {
    String sql = "select "
            + "customer_id, acc_number, branch_id, balance from Accounts"
            + "where customer_id = ?";

    Connection c = dataSource.getConnection();
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, customerId);
    ResultSet rs = p.executeQuery(sql));
    // omitted - process rows and return an account list
}
```

Here we've used the `prepareStatement()` method available in the `Connection` instance to get a
`PreparedStatement`. This interface extends the regular `Statement` interface with several methods that allow us to
safely insert user-supplied values in a query before executing it. For JPA, we have a similar feature:

```java
String jql = "FROM Account WHERE customerId = :customerId";
TypedQuery<Account> query = em.createQuery(jql, Account.class)
        .setParameter("customerId", customerId);
// Execute query and return mapped results (omitted)
```

**As a bonus, this approach usually results in a better performing query, since most databases can cache the query plan
associated with a prepared statement.**

Please note that this approach only works for placeholders used as values. For instance, we can't use placeholders to
dynamically change the name of a table:

```java
// This WILL NOT WORK !!!
PreparedStatement p = c.prepareStatement("select count(*) from ?");
p.setString(1, tableName);
```

Here, JPA won't help either:

```java
// This WILL NOT WORK EITHER !!!
entityManager
    .createQuery("SELECT COUNT(*) FROM :tableName", Long.class)
    .setParameter("tableName", tableName);
    .getSingleResult();
```

In both cases, we'll get a runtime error.

The main reason behind this is the very nature of a prepared statement: database servers use them to cache the query
plan required to pull the result set, which usually is the same for any possible value. This is not true for table names
and other constructs available in the SQL language such as columns used in an order by clause.
