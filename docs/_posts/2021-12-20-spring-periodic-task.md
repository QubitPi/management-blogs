---
layout: post
title: Implementing Periodic Task for Webservice
tags: [Webservice, Design]
color: rgb(85, 85, 187)
feature-img: "assets/img/post-cover/15-cover.png"
thumbnail: "assets/img/post-cover/15-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

An application usually needs to deal with some "housekeeping" logic to make sure that the software runs stable. For
example, an online chatting system might keep the most recent chatting data in a local memory or a nearby datastore and
migrate those chatting history 2 years from now to a remote data-center for some Machine Learning processing.

A flush mechanism is, then, necessary to make sure the historical data doesn't overflow some disk space. This post deals
with the design of such scenario

Let's make a general migrating abstraction layer. The data being migrated could be any thing whose values decay over
time, for example, the chatting history, which people seldomly pick up when the chat is 2 - 3 years old.

## The Migration Needs to be Async

The migration must not block any business processing on the Webservice side and, thus, will run asynchronously. This is
why we will define the following interface first:

```java
@FunctionalInterface
public interface Migrator extends Runnable {

    void migrateData(@NotNull Date expiry);
}
```

The 'Migrator' is a sub-type of async `Runnable` so that an `Executor` could migrate the data asynchronously.

The `void migrateData(@NotNull Date expiry);` defines the whole migration process. The implementation could be a
migration from a local Redis to a remote HDFS. The parameter `expiry` defines the boundary before which the data is to
to flushed out and migrated. For example, when `expiry` is set to "2021-12-20", any data generated before that date will
be moved to HDFS after a call to this method.

## The Migration Needs to be Triggered Manually

This is when user/developer/business has some sort of emergency needs so they cannot wait until the next run. We could
make this happen through a **DELETE** Webservice endpoints. It is also recommended making the `expiry` a path variable
so that the request URL looks some like "http://192.168.1.152:8080/api/migration/2021-05-01", in which case the
`expiry = 2021-05-01`. With that, a controller can simply delegate this path parameter to the `Migrator.migrateData()`
cleanly

## The Migration Shall Run Periodically and Support Multiple Types of Data

An enterprise application often have more than one or two types of data, such as texting data, user session data, etc.
They are also probably stored differently. This means the migration will also differ. The
[DRY Principle](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself) forces us to bake inheritance into the
implementation of the `Migrator`, because different data must share some common logic such as "if there is no data in
redis, simply skip the migration":

```java
abstract class AbstractMigrator implements StaleDataTruncator {

    protected final int numPeriods;
    protected final String periodGranularity;

    AbstractStaleDataTruncator(final int numPeriods, @NotNull final String periodGranularity) {
        if (numPeriods <= 0) {
            log.warn(
                    "{}: 'numPeriods' must be positive. Schedule task won't successfully execute!",
                    this.getClass().getCanonicalName()
            );
        }

        this.numPeriods = numPeriods;
        this.periodGranularity = Objects.requireNonNull(periodGranularity, "periodGranularity");
    }
    
    @Override
    public void run() {
        migrateData(getExpiryDate());
    }

    @NotNull
    private Date getExpiryDate() {
        return new Date(
                LocalDateTime
                        .now()
                        .minus(numPeriods, ChronoUnit.valueOf(periodGranularity))
                        .toEpochSecond(
                                ZoneOffset.systemDefault().getRules().getOffset(Instant.now())
                        ) * SEC_TO_MS_MULTIPLIER
        );
    }
}
```
