---
layout: post
title: Concurrency & Multi-threading
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Java Concurrency is a term that covers multithreading, concurrency, and parallelism. That includes the Java concurrency
tools, problems and solutions.

<!--more-->

* TOC
{:toc}

## What is Multithreading?

**Multithreading means that you have multiple threads of execution inside the same application**. A thread is like a
separate CPU executing your application. Thus, a multithreaded application is like an application that has multiple CPUs
executing different parts of the code at the same time.

![Introduction]({{ "/assets/img/introduction-1.png" | relative_url}})

A thread is not equal to a CPU though. Usually a single CPU will share its execution time among multiple threads,
switching between executing each of the threads for a given amount of time. It is also possible to have the threads of
an application be executed by different CPUs.

![Introduction]({{ "/assets/img/introduction-2.png" | relative_url}})

## Why Multithreading?

There are several reasons as to why one would use multithreading in an application. Some of the most common reasons for
multithreading are:

* [Better utilization of a single CPU](#better-utilization-of-a-single-cpu)
* [Better utilization of multiple CPUs or CPU cores](#better-utilization-of-multiple-cpus-or-cpu-cores)
* [Better user experience with regards to responsiveness](#better-user-experience-with-regards-to-responsiveness)
* [Better user experience with regards to fairness](#better-user-experience-with-regards-to-fairness)

### Better Utilization of a Single CPU

One of the most common reasons is to be able to better utilize the resources in the computer. For instance, if one
thread is waiting for the response to a request sent over the network, then another thread could use the CPU in the
meantime to do something else. Additionally, if the computer has multiple CPUs, or if the CPU has multiple execution
cores, then multithreading can also help your application utilize these extra CPU cores.

Imagine an application that reads and processes files from the local file system. Lets say that reading af file from
disk takes 5 seconds and processing it takes 2 seconds. Processing two files then takes

```
  5 seconds reading file A
  2 seconds processing file A
  5 seconds reading file B
  2 seconds processing file B
-----------------------
 14 seconds total
```

When reading the file from disk most of the CPU time is spent waiting for the disk to read the data. The CPU is pretty
much idle during that time. It could be doing something else. By changing the order of the operations, the CPU could be
better utilized. Look at this ordering:

```
  5 seconds reading file A
  5 seconds reading file B + 2 seconds processing file A
  2 seconds processing file B
-----------------------
 12 seconds total
```

The CPU waits for the first file to be read. Then it starts the read of the second file. While the second file is being
read in by the IO components of the computer, the CPU processes the first file. Remember, while waiting for the file to
be read from disk, the CPU is mostly idle.

In general, the CPU can be doing other things while waiting for IO. It doesn't have to be disk IO. It can be network IO
as well, or input from a user at the machine. Network and disk IO is often a lot slower than CPU's and memory IO.

### Better Utilization of Multiple CPUs or CPU Cores

If a computer contains multiple CPUs or the CPU contains multiple execution cores, then you need to use multiple threads
for your application to be able to utilize all of the CPUs or CPU cores. A single thread can at most utilize a single
CPU, and as I mentioned above, sometimes not even completely utilize a single CPU.

### Better User Experience with Regards to Responsiveness

Another reason to use multithreading is to provide a better user experience. For instance, if you click on a button in a
GUI and this results in a request being sent over the network, then it matters which thread performs this request. If
you use the same thread that is also updating the GUI, then the user might experience the GUI "hanging" while the GUI
thread is waiting for the response for the request. Instead, such a request could be performed by a background thread so
the GUI thread is free to respond to other user requests in the meantime.

### Better User Experience with Regards to Fairness

A fourth reason is to share resources of a computer more fairly among users. As example imagine a server that receives
requests from clients, and only has one thread to execute these requests. If a client sends a requests that takes a long
time to process, then all other client's requests would have to wait until that one request has finished. By having each
client's request executed by its own thread then no single task can monopolize the CPU completely.

## Multithreading vs. Multitasking

Back in the old days a computer had a single CPU, and was only capable of executing a single program at a time. Most
smaller computers were not really powerful enough to execute multiple programs at the same time, so it was not
attempted. To be fair, many mainframe systems have been able to execute multiple programs at a time for many more years
than personal computers.

### Multitasking

Later came multitasking which meant that computers could execute multiple programs (AKA tasks or processes) at the same
time. It wasn't really "at the same time" though. The single CPU was shared between the programs. The operating system
would switch between the programs running, executing each of them for a little while before switching.

Along with multitasking came new challenges for software developers. Programs can no longer assume to have all the CPU
time available, nor all memory or any other computer resources. **A "good citizen" program should release all resources
it is no longer using, so other programs can use them**.

### Multithreading

Later yet came multithreading which mean that you could have multiple threads of execution inside the same program. A
thread of execution can be thought of as a CPU executing the program. When you have multiple threads executing the same
program, it is like having multiple CPUs execute within the same program.

## Multithreading is Hard

Multithreading can be a great way to increase the performance of some types of programs. However, mulithreading is even
more challenging than multitasking. The threads are executing within the same program and are hence reading and writing
the same memory simultaneously. This can result in errors not seen in a singlethreaded program. Some of these errors may
not be seen on single CPU machines, because two threads never really execute "simultaneously". Modern computers, though,
come with multi core CPUs, and even with multiple CPUs too. This means that separate threads can be executed by separate
cores or CPUs simultaneously.

![Java Concurrency Introduction]({{ "/assets/img/java-concurrency-introduction-1.png" | relative_url}})

If a thread reads a memory location while another thread writes to it, what value will the first thread end up reading?
The old value? The value written by the second thread? Or a value that is a mix between the two? Or, if two threads are
writing to the same memory location simultaneously, what value will be left when they are done? The value written by the
first thread? The value written by the second thread? Or a mix of the two values written?

Without proper precautions any of these outcomes are possible. The behaviour would not even be predictable. The outcome
could change from time to time. Therefore it is important as a developer to know how to take the right precautions -
meaning learning to control how threads access shared resources like memory, files, databases etc.

## Multithreading Costs

Going from a singlethreaded to a multithreaded application doesn't just provide benefits. It also has some costs. Don't
just multithread-enable an application just because you can. You should have a good idea that the benefits gained by
doing so, are larger than the costs. When in doubt, try measuring the performance or responsiveness of the application,
instead of just guessing.

### More complex design

Though some parts of a multithreaded applications is simpler than a singlethreaded application, other parts are more
complex. Code executed by multiple threads accessing shared data need special attention. Thread interaction is far from
always simple. Errors arising from incorrect thread synchronization can be very hard to detect, reproduce and fix.

### Context Switching Overhead

When a CPU switches from executing one thread to executing another, the CPU needs to save the local data, program
pointer etc. of the current thread, and load the local data, program pointer etc. of the next thread to execute. This
switch is called a "context switch". The CPU switches from executing in the context of one thread to executing in the
context of another.

Context switching isn't cheap. You don't want to switch between threads more than necessary.

### Increased Resource Consumption

A thread needs some resources from the computer in order to run. Besides CPU time a thread needs some memory to keep its
local stack. It may also take up some resources inside the operating system needed to manage the thread. Try creating a
program that creates 100 threads that does nothing but wait, and see how much memory the application takes when running.

## Concurrency Models

The first Java concurrency model assumed that multiple threads executing within the same application would also share
objects. This type of concurrency model is typically referred to as a "shared state concurrency model". A lot of the
concurrency language constructs and utilities are designed to support this concurrency model.

However, a lot has happened in the world of concurrent architecture and design.

The shared state concurrency model causes a lot of concurrency problems which can be hard to solve elegantly. Therefore,
an alternative concurrency model referred to as "shared nothing" or "separate state" has gained popularity. In the
separate state concurrency model the threads do not share any objects or data. This avoids a lot of the concurrent
access problems of the shared state concurrency model.

New, asynchronous "separate state" platforms and toolkits like Netty, Vert.x and Play/Akka and Qbit have emerged. New
non-blocking concurrency algorithms have been published, and new non-blocking tools like the LMax Disrupter have been
added to our toolkits. New functional programming parallelism has been introduced with the Fork and Join framework in
Java 7, and the collection streams API in Java 8.
