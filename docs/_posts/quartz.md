Quartz is a richly featured, open source job scheduling library that can be integrated within virtually any Java
application - from the smallest stand-alone application to the largest e-commerce system. Quartz can be used to create
simple or complex schedules for executing tens, hundreds, or even tens-of-thousands of jobs; jobs whose tasks are
defined as standard Java components that may execute virtually anything you may program them to do. The Quartz Scheduler
includes many enterprise-class features, such as support for JTA transactions and clustering.

## A Quick Look at Quartz

```java
SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();

Scheduler scheduler = schedulerFactory.getScheduler();

scheduler.start();

// define the job and tie it to our HelloJob class
JobDetail job = newJob(HelloJob.class)
        .withIdentity("myJob", "group1")
        .build();

// Trigger the job to run now, and then every 40 seconds
Trigger trigger = newTrigger()
        .withIdentity("myTrigger", "group1")
        .startNow()
        .withSchedule(
            simpleSchedule()
            .withIntervalInSeconds(40)
            .repeatForever()
        )
        .build();

// Tell quartz to schedule the job using our trigger
scheduler.scheduleJob(job, trigger);
```

## `Job`(type) & `JobDetail`(instance)

A `Job` is a class that implements the Job interface, which has only one simple method:

```java
package org.quartz;

public interface Job {

    void execute(JobExecutionContext context) throws JobExecutionException;
}
```

**A `JobDetail` is an instance of `Job` class**. Each `JobDetail` is associated with an unique ID called `JobKey`. Keys
are composed of both a name and group, and the name must be unique within the group. Quartz offers API to construct
`JobKey`: `JobKey.jobKey(String name, String group)`.

A `Job` instance, i.e. `JobDetail` can be re-executed. The instance can be retrieved at anytime by key using
`Scheduler.getJobDetail(JobKey)`

## Trigger

Trigger objects are used to trigger the execution of jobs. When you schedule a job, you instantiate a trigger and "tune"
its properties to provide the scheduling you wish to have. 

Note that a `JobDetail` can be triggered immediately via `Scheduler.triggerJob(JobKey)`;

### Misfire Instructions

An important property of a Trigger is its "misfire instruction". A misfire occurs if a persistent trigger "misses" its
firing time because of the scheduler being shutdown, or because there are no available threads in Quartz thread pool.
The different trigger types have different misfire instructions available to them. By default they use a "smart policy"
instruction - which has dynamic behavior based on trigger type and configuration. When the scheduler starts, it searches
for any persistent triggers that have misfired, and it then updates each of them based on their individually configured
misfire instructions. You should make yourself familiar with the misfire instructions that are defined on the given
trigger types, and explained in their JavaDoc.

All triggers have the `Trigger.MISFIRE_INSTRUCTION_SMART_POLICY` instruction set as default

### `SimpleTrigger`

`SimpleTrigger` should meet your scheduling needs if you need to have a job execute exactly once at a specific moment in
time, or at a specific moment in time followed by repeats at a specific interval. For example, if you want the trigger
to fire at exactly 11:23:54 AM on January 13, 2015, or if you want it to fire at that time, and then fire five more
times, every ten seconds.

#### Building `SimpleTrigger`

```java
// Build a trigger for a specific moment in time, with no repeats
Trigger trigger = newTrigger()
        .withIdentity("trigger1", "group1")
        .startAt(myStartTime)     // some Date
        .forJob("job1", "group1") // identify job with name, group strings
        .build();

// Build a trigger for a specific moment in time, then repeating every ten seconds ten times
trigger = newTrigger()
        .withIdentity("trigger3", "group1")
        .startAt(myTimeToStartFiring) // if a start time is not given (if this line were omitted), "now" is implied
        .withSchedule(
            simpleSchedule()
                    .withIntervalInSeconds(10)
                    .withRepeatCount(10)) // note that 10 repeats will give a total of 11 firings
        .forJob(myJob) // identify job with handle to its JobDetail itself                   
        .build();

// Build a trigger that will fire once after five minutes
trigger = newTrigger()
        .withIdentity("trigger5", "group1")
        .startAt(futureDate(5, IntervalUnit.MINUTE)) // use DateBuilder to create a date in the future
        .forJob(myJobKey) // identify job with its JobKey
        .build();

// Build a trigger that will fire now, then repeat every five minutes, until the hour 22:00
trigger = newTrigger()
        .withIdentity("trigger7", "group1")
        .withSchedule(
            simpleSchedule()
                    .withIntervalInMinutes(5)
                    .repeatForever()
        )
        .endAt(dateOf(22, 0, 0))
        .build();

// Build a trigger that will fire at the top of the next hour, then repeat every 2 hours, forever
trigger = newTrigger()
        .withIdentity("trigger8")    // because group is not specified, "trigger8" will be in the default group
        .startAt(evenHourDate(null)) // get the next even-hour (minutes and seconds zero ("00:00"))
        .withSchedule(
            simpleSchedule()
                    .withIntervalInHours(2)
                    .repeatForever()
        )
        // note that in this example, 'forJob(..)' is not called
        //  - which is valid if the trigger is passed to the scheduler along with the job  
        .build();
scheduler.scheduleJob(trigger, job);
```

> Note that `TriggerBuilder` (`SimpleTrigger` instances are built using `TriggerBuilder`) will generally choose a
> reasonable value for properties that you do not explicitly set. For examples: if you don't call one of the
> `withIdentity(...)` methods, then `TriggerBuilder` will generate a random name for your trigger; if you don't call
> `startAt(...)` then the current time (immediately) is assumed

#### `SimpleTrigger` Misfire Instructions

`SimpleTrigger` has several instructions that can be used to inform Quartz what it should do when a misfire occurs.
These instructions are defined as constants on `SimpleTrigger` itself:

```java
MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
MISFIRE_INSTRUCTION_FIRE_NOW
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT
```

When building `SimpleTriggers`, you specify the misfire instruction as part of the simple schedule (via
`SimpleSchedulerBuilder`):

```java
trigger = newTrigger()
        .withIdentity("trigger7", "group1")
        .withSchedule(
            simpleSchedule()
                    .withIntervalInMinutes(5)
                    .repeatForever()
                    .withMisfireHandlingInstructionNextWithExistingCount())
        .build();
```

### `CronTrigger`

With `CronTrigger`, you can specify firing-schedules such as "every Friday at noon", or "every weekday and 9:30 am", or
even "every 5 minutes between 9:00 am and 10:00 am on every Monday, Wednesday and Friday during January"

#### Building `CronTrigger`

```java
// Build a trigger that will fire every other minute, between 8am and 5pm, every day
trigger = newTrigger()
        .withIdentity("trigger3", "group1")
        .withSchedule(cronSchedule("0 0/2 8-17 * * ?"))
        .forJob("myJob", "group1")
        .build();

// Build a trigger that will fire daily at 10:42 am
trigger = newTrigger()
        .withIdentity("trigger3", "group1")
        .withSchedule(dailyAtHourAndMinute(10, 42))
        .forJob(myJobKey)
        .build();

// Build a trigger that will fire on Wednesdays at 10:42 am, in a TimeZone other than the system’s default
trigger = newTrigger()
        .withIdentity("trigger3", "group1")
        .withSchedule(weeklyOnDayAndHourAndMinute(DateBuilder.WEDNESDAY, 10, 42))
        .forJob(myJobKey)
        .inTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
        .build();
```

#### `CronTrigger` Misfire Instructions

```java
MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
MISFIRE_INSTRUCTION_DO_NOTHING
MISFIRE_INSTRUCTION_FIRE_NOW
```

The "smart policy" instruction is interpreted by `CronTrigger` as `MISFIRE_INSTRUCTION_FIRE_NOW`

When building CronTriggers, you specify the misfire instruction as part of the simple schedule (via
`CronSchedulerBuilder`):

```java
trigger = newTrigger()
        .withIdentity("trigger3", "group1")
        .withSchedule(
            cronSchedule("0 0/2 8-17 * * ?")
                    .withMisfireHandlingInstructionFireAndProceed()
        )
        .forJob("myJob", "group1")
        .build();
```

## `JobDataMap`

The `JobDataMap` can be used to hold any amount of (serializable) data objects which you wish to have made available to
the job instance when it executes. `JobDataMap` is an implementation of the Java `Map` interface, and has some added
convenience methods for storing and retrieving data of primitive types.

Here's some quick snippets of putting data into the `JobDataMap` while defining/building the `JobDetail`, prior to
adding the job to the scheduler:

```java
// define the job and tie it to our DumbJob class
JobDetail job = newJob(DumbJob.class)
    .withIdentity("myJob", "group1")         // name "myJob", group "group1"
    .usingJobData("jobSays", "Hello World!") // stored in JobDataMap
    .usingJobData("myFloatValue", 3.141f)    // stored in JobDataMap
    .build();
```

Here is a quick example of getting data from the JobDataMap during the job’s execution

```java
public class SomeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String jobSays = dataMap.getString("jobSays");
        float myFloatValue = dataMap.getFloat("myFloatValue");
    }
}
```

Triggers can also have `JobDataMaps` associated with them. This can be useful in the case where you have a Job that is
stored in the scheduler for regular/repeated use by multiple Triggers, yet with each independent triggering, you want to
supply the Job with different data inputs.

The `JobDataMap` that is found on the `JobExecutionContext`(as in the example above) during `Job` execution serves as a
convenience. It is a merge of the `JobDataMap` found on the `JobDetail` and the one found on the `Trigger`, with the
values in the latter overriding any same-named values in the former.

Here's a quick example of getting data from the `JobExecutionContext`'s merged `JobDataMap` during the job's execution:

```java
public class SomeJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();  // Note the difference from the previous example

        String jobSays = dataMap.getString("jobSays");
        float myFloatValue = dataMap.getFloat("myFloatValue");
        ArrayList state = (ArrayList) dataMap.get("myStateData");
        state.add(new Date());
    }
  }
```

## Job State and Concurrency

There are some annotations that can be added to your `Job` class that affect `Quartz` behavior:

### `@DisallowConcurrentExecution`

It tells Quartz not to execute multiple instances of a given job definition (that refers to the given job class)
concurrently. The constraint is based upon an instance definition (`JobDetail`), not on instances of the job class. For
example:

```java
@DisallowConcurrentExecution
public class SomeJob implements Job {

    ...
}
```

```java
JobDetail job1 = newJob(SomeJob.class)
        .withIdentity("job1", "group1")
        .build();
```

```java
JobDetail job2 = newJob(SomeJob.class)
        .withIdentity("job2", "group2")
        .build();
```

Only 1 instance of `job1` can be executed at a given time, it `job1` can be executed concurrently with `job2`.

### `@PersistJobDataAfterExecution`

Update the stored copy of the `JobDataMap` in `JobDetail` after the `execute()` method completes successfully (without
throwing an exception), such that the next execution of the same job (`JobDetail`) receives the updated values rather
than the originally stored values. Like the `@DisallowConcurrentExecution` annotation, this applies to a job definition
instance, not a job class instance

> Note: If you use the `@PersistJobDataAfterExecution` annotation, you should strongly consider also using the
> `@DisallowConcurrentExecution` annotation, in order to avoid possible race conditions

## Job Stores

`JobStore` are responsible for keeping track of all the information given to the scheduler: jobs, triggers, calendars,
etc. Selecting the appropriate `JobStore` for your Quartz scheduler instance is an important step. Luckily, the choice
should be a very easy one once you understand the differences between them. You declare which `JobStore` your scheduler
should use (and it's configuration settings) in the **properties file** (or **object**) that you provide to the
`SchedulerFactory`

> Note: Never use a `JobStore` instance directly in your code. `JobStore` is for behind-the-scenes use of Quartz itself.
> You have to tell Quartz (through configuration) which `JobStore` to use, but then you should only work with the
> `Scheduler` interface in your code

### `RAMJobStore`

`RAMJobStore` is the simplest `JobStore` to use, it is also the most performant (in terms of CPU time). `RAMJobStore`
gets its name in the obvious way: it keeps all of its data in RAM. This is why it's lightning-fast, and also why it's so
simple to configure. The drawback is that when your application ends (or crashes) all of the scheduling information is
lost

### `JDBCJobStore`

`JDBCJobStore` is also aptly named - it keeps all of its data in a database via JDBC. Because of this it is a bit more
complicated to configure than `RAMJobStore`, and it also is not as fast. However, the performance draw-back is not
terribly bad

`JDBCJobStore` works with nearly any database, it has been used widely with Oracle, PostgreSQL, MySQL,
[HSQLDB](http://hsqldb.org/), and DB2.

### `TerracottaJobStore`

TerracottaJobStore provides a means for scaling and robustness without the use of a database.

> Note: https://www.terracotta.org/
