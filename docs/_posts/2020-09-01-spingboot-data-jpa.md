---
layout: post
title: Spring Data JPA
tags: [Spring Boot, Spring]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

[Show Hibernate SQL query](https://mkyong.com/spring-boot/spring-boot-show-hibernate-sql-query/?__cf_chl_captcha_tk__=651ebbda874cdc687791cc51d8c9a255e78cbcf2-1606966482-0-ARO__MuYuZ7N6tiqlvRANDFD9F2xSEhUjEegpQTvLdBgAv7XHnOBR_RdygL79lB6zNxDrftmAqlEF4GQybvma1BlaZTi5EZGzet4AMsb-TMVuOAtjaUVe0gZvkDb0tX9SMwMHmf6XGC9fGF8iknOTGyPk2IIHKtfVYQ160maHOwHSz3WJ1iSXXJwisHfi6vzhfHEga_Ys84NqOhql9iXpZ11mKpSFeQmEn7bkbR3ZjeeaNMfjECQjTqw-SzAMWZC_lBvEq7A3lKR9fwj-67K1nXiXt-tz3ST_mZIQwwrfgHi412mKFeKTBYevGf1aOW1J15EfZJICKpLIw77SLlY0mbnLA3WYmdclDFEHolpd-kjrA5Xfx8E5eayOBcfUbBWIq5kF-VNddpNnzweisr_E50q9THAL4t7MNQc9maHSch3PVb5ZxYbgkMcnuQ7QnAjGDBPspc7urGXgfN6ExCIrhhJKm-8z9OKEkf8xN-9Q5tOnApDi9N7rVnaIXQUS18udEUEOj_tlnMg87OcG6_R2XRwZlbsvYJNfW7VwjrGcGs4FcsDQOtrSqPh-wsEUOG9QQ0BGQ2OjdZvBqzKIQDIAIiqCFXBphUfgOOqtayhzxeMyFEehYRciYN-t-5DI-cqT7pr6WpJxsM-LcAxzmjHd_I)

## Pagination

https://stackoverflow.com/a/47616648
[Pageable](https://www.logicbig.com/tutorials/spring-framework/spring-data/web-support-with-pageable-argument-resolver.html)

### Generate a Creation Date-Time Automatically

#### JPA

There isn't anything as convenient as annotating the Timestamp field directly, but you could use the `@PrePersist`,
`@PreUpdate` annotations and with little effort achieve the same results.

#### Hibernate

[`@CreationTimestamp`](http://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#mapping-generated-CreationTimestamp)
[`@UpdateTimestamp`](http://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#mapping-generated-UpdateTimestamp)

#### Spring Data JPA

[`@CreatedDate`](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing.annotations)
[`@LastModifiedDate`](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing.annotations)

## [Map the Native Query Result to Non-Entity POJO](https://stackoverflow.com/a/48296588)

Let's say we have a TODO app with a database that keeps information about TODO task (`todo_task` table) and person
(`person`). We would like to get a list of Person's ongoing TODO's; the results should have at least 2 columns - task
name and person's name. We could use the following query:

```
SELECT todo_task.id, todo_task.task_name, person.name
FROM todo_task
LEFT JOIN person ON todo_task.person_id = person.id
WHERE todo_task.task_status NOT IN ('DONE', 'TO DO')
```

We notice that the result comes with 3 columns that map to 2 separate JPA entities, i.e. `todo_task` & `person`

In order to map the database result to a strongly typed programmable object, the easiest way is to use so called
[projection](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections). It can map query results
to interfaces. Using `SqlResultSetMapping` is inconvienient and makes your code ugly :).

```java
@Query(
        nativeQuery = true,
        value = "SELECT todo_task.id AS id, todo_task.task_name AS taskName, person.name AS name " +
                "FROM todo_task " +
                "LEFT JOIN person ON todo_task.person_id = person.id " +
                "WHERE todo_task.task_status NOT IN ('Finished', 'Canceled')"
)
@NotNull
List<Task> getAllTasks(@NotNull Pageable pagination);
```

```java
public interface Task {

    Long getId();

    String getTaskName();
    
    String getName();
}
```

> ⚠️ If you use `SELECT table.column ...` notation always define aliases matching names from entity(`Task`). For example
> this won't work properly (projection will return nulls(`NullPointerException: null` at runtime) for each getter):
> `SELECT todo_task.id, todo_task.task_name, person.name FROM ...` But this works fine:
> `SELECT todo_task.id AS id, todo_task.task_name AS taskName, person.name AS name FROM ...`
