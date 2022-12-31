---
layout: post
title: Same Threading
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/16-cover.png"
thumbnail: "assets/img/post-cover/16-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Thread Pools are useful when you need to **limit the number of threads running in your application at the same time**.
There is a performance overhead associated with starting a new thread, and each thread is also allocated some memory for
its stack etc.

Instead of starting a new thread for every task to execute concurrently, the task can be passed to a thread pool. As
soon as the pool has any idle threads the task is assigned to one of them and executed. Internally the tasks are
inserted into a Blocking Queue which the threads in the pool are dequeuing from. When a new task is inserted into the
queue one of the idle threads will dequeue it successfully and execute it. The rest of the idle threads in the pool will
be blocked waiting to dequeue tasks.

Thread pools are often used in multi threaded servers. Each connection arriving at the server via the network is wrapped
as a task and passed on to a thread pool. The threads in the thread pool will process the requests on the connections
concurrently.

Java 5 comes with built in thread pools in the `java.util.concurrent` package, so you don't have to implement your own
thread pool.

## Why Single-Threaded Systems?

You might be wondering why anyone would design single-threaded system today. Single-threaded systems have gained
popularity because their concurrency models are much simpler than multi-threaded systems. Single-threaded systems do not
share any state (objects/data) with other threads. This enables the single thread to use non-concurrent data structures,
and utilize the CPU and CPU caches better.

Unfortunately, single-threaded systems do not fully utilize modern CPUs. A modern CPU often comes with 2, 4, 6, 8 more
cores. Each core functions as an individual CPU. A single-threaded system can only utilize one of the cores, as
illustrated here:

![diagram]({{ "/assets/img/same-threading-0.png" | relative_url}})

## Same-Threading: Single-Threading Scaled Out

In order to utilize all the cores in the CPU, a single-threaded system can be scaled out to utilize the whole computer.

### One Thread Per CPU

Same-threaded systems usually has 1 thread running per CPU in the computer. If a computer contains 4 CPUs, or a CPU with
4 cores, then it would be normal to run 4 instances of the same-threaded system (4 single-threaded systems). The
illustration below shows this picture:

![diagram]({{ "/assets/img/same-threading-0-1.png" | relative_url}})

## No Shared State

A same-threaded system looks similar to a traditional multi-threaded system, since a same-threaded system has multiple
threads running inside it. But there is a subtle difference.

The difference between a same-threaded and a traditional multi-threaded system is that the threads in a same-threaded
system do not share state. There is no shared memory which the threads access concurrently. No concurrent data
structures etc. via which the threads share data. This difference is illustrated here:

![diagram]({{ "/assets/img/same-threading-4.png" | relative_url}})

The lack of shared state is what makes each thread behave as it if was a single-threaded system. However, since a
same-threaded system can contain more than a single thread - it is not really a "single-threaded system". In lack of a
better name, I found it more precise to call such a system a same-threaded system, rather than a "multi-threaded system
with a single-threaded design". Same-threaded is easier to say, and easier to understand.

Same-threaded basically means that data processing stays within the same thread, and that no threads in a same-threaded
system share data concurrently. Sometimes this is also referred to just as no shared state concurrency, or separate
state concurrency.

## Load Distribution

Obviously, a same-threaded system needs to share the work load between the single-threaded instances running. If only
one thread gets any work, the system would in effect be single-threaded.

Exactly how you distribute the load over the different threads depend on the design of your system.

### Single-threaded Microservices

If your system consists of multiple microservices, each microservice can run in single-threaded mode. When you deploy
multiple single-threaded microservices to the same machine, each microservice can run a single thread on a sigle CPU.

Microservices do not share any data by nature, so **microservices is a good use case for a same-threaded system**.

### Services With Sharded Data

If your system does actually need to share data, or at least a database, you may be able to shard the database. Sharding
means that the data is divided among multiple databases. The data is typically divided so that all data related to each
other is located together in the same database. For instance, all data belonging to some "owner" entity will be inserted
into the same database.

## Thread Communication

If the threads in a same-threaded system need to communicate, they do so by message passing. If Thread A wants to send a
message to Thread B, Thread A can do so by generating a message (a byte sequence). Thread B can then copy that message
(byte sequence) and read it. By copying the message Thread B makes sure that Thread A cannot modify the message while
Thread B reads it. Once copied, the message copy is inaccessible for Thread A.

Thread communication via messaging is illustrated here:

![diagram]({{ "/assets/img/same-threading-5.png" | relative_url}})

The thread communication can take place via queues, pipes, unix sockets, TCP sockets etc. Whatever fits your system.

## Simpler Concurrency Model

Each system running in its own thread in same-threaded system can be implemented as if it was single-threaded. This
means that the internal concurrency model becomes much simpler than if the threads shared state. You do not have to
worry about concurrent data structures and all the concurrency problems such data structures can result in.

## Illustrations

Here are illustrations of a single-threaded, multi-threaded and same-threaded system, so you can easier get an overview
of the difference between them.

The first illustration shows a single-threaded system.

![diagram]({{ "/assets/img/same-threading-1.png" | relative_url}})

The second illustration shows a multi-threaded system where the threads share data.

![diagram]({{ "/assets/img/same-threading-2.png" | relative_url}})

The third illustration shows a same-threaded system with 2 threads with separate data, communicating by passing
messages to each other.

![diagram]({{ "/assets/img/same-threading-3.png" | relative_url}})
