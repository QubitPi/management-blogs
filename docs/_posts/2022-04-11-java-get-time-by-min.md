---
layout: post
title: Get Current Time With Minute Precision Exclusively
tags: [HTTP, HTTPS]
color: rgb(0, 196, 0)
feature-img: "assets/img/post-cover/30-cover.png"
thumbnail: "assets/img/post-cover/30-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Let's say the system time is 13:24:44 of the day and we would like to ge a time instance of 13:24:00, i.e. truncating
second precision of the time, we could use

```java
private static final int SEC_TO_MS_MULTIPLIER = 1000;

public static Date getCurrentTimeWithMinutePrecision() {
    return new Date(
            LocalDateTime
                    .now()
                    .truncatedTo(ChronoUnit.MINUTES) // 13:24:44 -> 13:24:00
                    .toEpochSecond(
                            ZoneOffset.systemDefault().getRules().getOffset(Instant.now())
                    ) * SEC_TO_MS_MULTIPLIER
    );
}
```

If we, however, want to get an exclusive boundary, we could add a line of `.minus(1, ChronoUnit.SECONDS)`, for example:

```java
public static Date getCurrentTimeWithMinutePrecisionExclusively() {
    return new Date(
            LocalDateTime
                    .now()
                    .truncatedTo(ChronoUnit.MINUTES) // 13:24:44 -> 13:24:00
                    .minus(1, ChronoUnit.SECONDS)    // 13:24:00 -> 12:23:59
                    .toEpochSecond(
                            ZoneOffset.systemDefault().getRules().getOffset(Instant.now())
                    ) * SEC_TO_MS_MULTIPLIER
    );
}
```
