---
layout: post
title: Introduction to ReactiveX Java - RxJava
tags: [Java, Observer, ReactiveX, Functional Programming, Async, RxJava]
color: rgb(216, 26, 96)
feature-img: "assets/img/post-cover/35-cover.png"
thumbnail: "assets/img/post-cover/35-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

ReactiveX Java is a library for composing asynchronous and event-based programs by using observable sequences.

<!--more-->

[ReactiveX](https://reactivex.io/) extends [the observer pattern](http://en.wikipedia.org/wiki/Observer_pattern) to 
support sequences of data and/or events and adds operators that allow you to compose sequences together declaratively 
while abstracting away concerns about things like low-level threading, synchronization, thread-safety, concurrent data 
structures, and non-blocking I/O.

> Observables fill the gap by being the ideal way to access asynchronous sequences of multiple items
>
> |                  | **single items**      | **multiple items**        |
> |:----------------:|:---------------------:|:-------------------------:|
> | **synchronous**  | `T getData()`         | `Iterable<T> getData()`   |
> | **asynchronous** | `Future<T> getData()` | `Observable<T> getData()` |

This post accompanies its discussions with "marble diagrams" Here is how marble diagrams represent
[Observables](#observable) and transformations of Observables:

![Error loading reactivex-marble-diagram.png!]({{ "/assets/img/reactivex-marble-diagram.png" | relative_url}})

* TOC
{:toc}


Observable
----------

In ReactiveX an _observer (or subscriber) subscribes to an Observable_. Then that observer reacts to whatever item or 
sequence of items the Observable _emits_ (i.e. An Observable emits items or sends notifications to its observers by
calling the observers' methods.). This pattern facilitates concurrent operations because it does not need to block while 
waiting for the Observable to emit objects, but instead it creates a sentry in the form of an observer that stands ready
to react appropriately at whatever future time the Observable does so.

### Background

In many software programming tasks, you more or less expect that the instructions you write will execute and complete 
incrementally, one-at-a-time, in order as you have written them. But in ReactiveX, many instructions may execute in 
parallel and their results are later captured, in arbitrary order, by “observers.” Rather than calling a method, you
define a mechanism for retrieving and transforming the data, in the form of an “Observable,” and then _subscribe_ an 
observer to it, at which point the previously-defined mechanism fires into action with the observer standing sentry to 
capture and respond to its emissions whenever they are ready.

An advantage of this approach is that when you have a bunch of tasks that are not dependent on each other, you can start 
them all at the same time rather than waiting for each one to finish before starting the next one — that way, your
entire bundle of tasks only takes as long to complete as the longest task in the bundle.

### Establishing Observers

In an ordinary method call - that is, not the sort of asynchronous, parallel calls typical in ReactiveX - the flow is 
something like this:

1. Call a method.
2. Store the return value from that method in a variable.
3. Use that variable and its new value to do something useful.

In the asynchronous model the flow goes more like this:

1. Define a method that does something useful with the return value from the asynchronous call; this method is part of
   the _observer_.
2. Define an asynchronous call, i.e. an _Observable_.
3. Attach the observer to that Observable by _subscribing_ it (this also initiates the actions of the Observable).
4. Go on with your business; whenever the call returns, the observer's method will begin to operate on its return value
   or values - the _items_ emitted by the Observable.

The basic operator `just` produces an Observable that emits a single generic instance before completing, the String 
"Hello". When we want to get information out of an Observable, we implement an observer interface and then call
subscribe on the desired Observable:

```java
Observable<String> observable = Observable.just("Hello");
observable.subscribe(s -> result = s);
 
assertTrue(result.equals("Hello"));
```

#### Subscribe - Operate upon the Emissions and Notifications from an Observable

The **subscribe** operator is the glue that connects an observer to an Observable. In order for an observer to see the 
items being emitted by an Observable, or to receive error or completed notifications from the Observable, it must first 
subscribe to that Observable with this operator.

The observer implements some subset of the following methods:

* **onNext** An Observable calls this method whenever the Observable emits an item. This method takes as a parameter the 
  item emitted by the Observable.
* **onError** An Observable calls this method to indicate that it has failed to generate the expected data or has 
  encountered some other error. It will not make further calls to `onNext` or onCompleted. The `onError` method takes as 
  its parameter an indication of what caused the error.
* **onCompleted** An Observable calls this method after it has called `onNext` for the final time, if it has not 
  encountered any errors.

```java
String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
Observable<String> observable = Observable.from(letters);
observable.subscribe(
    i -> result += i,  //OnNext
    Throwable::printStackTrace, //OnError
    () -> result += "_Completed" //OnCompleted
);
assertTrue(result.equals("abcdefg_Completed"));
```

### The Observable Contract

Having empirically studied the Observable, we shall present a formal definition of an Observable below, which we call
the "**The Observable Contract.**"

#### Notifications

An Observable communicates with its observers with the following notifications:

* **OnNext** conveys an _item_ that is _emitted_ by the Observable to the observer
* **OnCompleted** indicates that the Observable has completed successfully and that it will be emitting no further items
* **OnError** indicates that the Observable has terminated with a specified error condition and that it will be emitting 
   no further items
* **OnSubscribe (optional)** indicates that the Observable is ready to accept Request notifications from the observer
  (see _Backpressure_ below)

An observer communicates with its Observable by means of the following notifications:

* **Subscribe** indicates that the observer is ready to receive notifications from the Observable
* **Unsubscribe** indicates that the observer no longer wants to receive notifications from the Observable
* **Request (optional)** indicates that the observer wants no more than a particular number of additional OnNext 
  notifications from the Observable (see _Backpressure_ below)

#### The Contract Governing Notifications

An Observable may make zero or more OnNext notifications, each representing a single emitted item, and it may then
follow those emission notifications by either an OnCompleted or an OnError notification, but not both. Upon issuing an 
OnCompleted or OnError notification, it may not thereafter issue any further notifications.

An Observable may emit no items at all. An Observable may also never terminate with either an OnCompleted or an OnError 
notification. That is to say that it is proper for an Observable to issue no notifications, to issue only an OnCompleted 
or an OnError notification, or to issue only OnNext notifications.

Observables must issue notifications to observers serially (not in parallel). They may issue these notifications from 
different threads, but there must be a formal _happens-before_ relationship between the notifications.

#### Observable Termination

If an Observable has not issued an OnCompleted or OnError notification, an observer may consider it to be still active 
(even if it is not currently emitting items) and may issue it notifications (such as an Unsubscribe or Request 
notification). When an Observable does issue an OnCompleted or OnError notification, the Observable may release its 
resources and terminate, and its observers should not attempt to communicate with it any further.

An OnError notification must contain the cause of the error (that is to say, it is invalid to call OnError with a `null` 
value).

Before an Observable terminates it must first issue either an OnCompleted or OnError notification to all of the
observers that are subscribed to it.

#### Subscribing and Unsubscribing

An Observable may begin issuing notifications to an observer immediately after the Observable receives a Subscribe 
notification from the observer.

When an observer issues an Unsubscribe notification to an Observable, the Observable will attempt to stop issuing 
notifications to the observer. It is not guaranteed, however, that the Observable will issue no notifications to the 
observer after an observer issues it an Unsubscribe notification.

When an Observable issues an OnError or OnComplete notification to its observers, this ends the subscription. Observers
do not need to issue an Unsubscribe notification to end subscriptions that are ended by the Observable in this way.

#### Multiple Observers

If a second observer subscribes to an Observable that is already emitting items to a first observer, it is up to the 
Observable whether it will thenceforth emit the same items to each observer, or whether it will replay the complete 
sequence of items from the beginning to the second observer, or whether it will emit a wholly different sequence of
items to the second observer. There is no general guarantee that two observers of the same Observable will see the same 
sequence of items.

#### Backpressure

Backpressure is optional; not all ReactiveX implementations include backpressure, and in those that do, not all 
Observables or operators honor backpressure. An Observable may implement backpressure if it detects that its observer 
implements Request notifications and understands OnSubscribe notifications.

If an Observable implements backpressure and its observer employs backpressure, the Observable will not begin to emit 
items to the observer immediately upon subscription. Instead, it will issue an OnSubscribe notification to the observer.

At any time after it receives an OnSubscribe notification, an observer may issue a Request notification to the
Observable it has subscribed to. This notification requests a particular number of items. The Observable responds to
such a Request by emitting no more items to the observer than the number of items the observer requests. However the 
Observable may, in addition, issue an OnCompleted or OnError notification, and it may even issue such a notification 
before the observer requests any items at all.

An Observable that does not implement backpressure should respond to a Request notification from an observer by issuing an
OnError notification that indicates that backpressure is not supported.

Requests are cumulative. For example, if an observer issues three Request notifications to an Observable, for 3, 5, and
10 items respectively, that Observable may emit as many as 18 items to the observer, no matter when those Request 
notifications arrived relative to when the Observable emitted items in response.

If the Observable produces more items than the observer requests, it is up to the Observable whether it will discard the 
excess items, store them to emit at a later time, or use some other strategy to deal with the overflow.

### "Hot" and "Cold" Observables

When does an Observable begin emitting its sequence of items? It depends on the Observable. A **"hot" Observable** may 
begin emitting items as soon as it is created, and so any observer who later subscribes to that Observable may start 
observing the sequence somewhere in the middle. A **"cold" Observable**, on the other hand, waits until an observer 
subscribes to it before it begins to emit items, and so such an observer is guaranteed to see the whole sequence from
the beginning.

#### "Connectable" Observable

In some implementations of ReactiveX, such as RxJava, there is also something called a
[Connectable Observable][ConnectableObservable.java]. Such an Observable does not begin emitting items until its 
[Connect](#connect) method is called, whether or not any observers have subscribed to it.


Operators
---------

### Creating Observables

* [**Create**](#create) Create an Observable from scratch by means of a function

#### Create

You can create an Observable from scratch by using the **Create operator**. You pass this operator a function that
accepts the observer as its parameter. Write this function so that it behaves as an Observable - by calling the
observer's `onNext`, `onError`, and `onCompleted` methods appropriately.

![Error loading reactivex-operator-create.png!]({{ "/assets/img/reactivex-operator-create.png" | relative_url}})

A _well-formed finite_ Observable must attempt to call either the observer's `onCompleted` method exactly once or its 
`onError` method exactly once, and must not thereafter attempt to call any of the observer's other methods.

### Connectable Observable Operators

Specialty Observables that have more precisely-controlled subscription dynamics

* [**Connect**](#connect) Instruct a connectable Observable to begin emitting items to its subscribers

#### Connect

A [connectable Observable][ConnectableObservable.java] resembles an ordinary Observable, except that it does not begin 
emitting items when it is subscribed to, but only when the Connect operator is applied to it. In this way you can wait
for all intended observers to subscribe to the Observable before the Observable begins emitting items.

![Error loading reactivex-operator-connect.png!]({{ "/assets/img/reactivex-operator-connect.png" | relative_url}})


Subject
-------

A Subject is a sort of bridge or proxy that is available in some implementations of ReactiveX that acts both as an 
observer and as an Observable. Because it is an observer, it can subscribe to one or more Observables, and because it is 
an Observable, it can pass through the items it observes by reemitting them, and it can also emit new items.

Because a Subject subscribes to an Observable, it will trigger that Observable to begin emitting items (if that
Observable is ["cold"](#hot-and-cold-observables) - that is, if it waits for a subscription before it begins to emit 
items). This can have the effect of making the resulting Subject a "hot" Observable variant of the original "cold" 
Observable.

### Varieties of Subject

There are four varieties of Subject that are designed for particular use cases. Not all of these are available in all 
implementations, and some implementations use other naming conventions. The RxJava, however, supports all of the
following subject types

#### AsyncSubject

An **[AsyncSubject][AsyncSubject.java]** emits _the last_ value (and only the last value) emitted by the source 
Observable, and only after that source Observable completes. (If the source Observable does not emit any values, the 
AsyncSubject also completes without emitting any values.)

![Error loading reactivex-async-subject.png!]({{ "/assets/img/reactivex-async-subject.png" | relative_url}})

It will also emit this same final value to any subsequent observers. However, if the source Observable terminates with
an error, the AsyncSubject will not emit any items, but will simply pass along the error notification from the source 
Observable.

![Error loading reactivex-async-subject-error.png!]({{ "/assets/img/reactivex-async-subject-error.png" | relative_url}})

#### BehaviorSubject

When an observer subscribes to a **[BehaviorSubject][BehaviorSubject.java]**, it begins by emitting the item _most
recently_ emitted by the source Observable (or a seed/default value if none has yet been emitted) and then continues to 
emit any other items emitted later by the source Observable(s).

![Error loading reactivex-behavior-subject.png!]({{ "/assets/img/reactivex-behavior-subject.png" | relative_url}})

However, if the source Observable terminates with an error, the BehaviorSubject will not emit any items to subsequent 
observers, but will simply pass along the error notification from the source Observable.

![Error loading reactivex-behavior-subject-error.png!]({{ "/assets/img/reactivex-behavior-subject-error.png" | relative_url}})

#### PublishSubject

[PublishSubject][PublishSubject.java] emits to an observer only those items that are emitted by the source Observable(s) 
_subsequent to the time of the subscription_.

![Error reactivex-publish-subject.png!]({{ "/assets/img/reactivex-publish-subject.png" | relative_url}})

Note that a PublishSubject may begin emitting items immediately upon creation (unless you have taken steps to prevent 
this), and so there is a risk that one or more items may be lost between the time the Subject is created and the
observer subscribes to it. If you need to guarantee delivery of all items from the source Observable, you'll need either 
to form that Observable with [Create](#create) so that you can manually reintroduce "cold" Observable behavior (checking 
to see that all observers have subscribed before beginning to emit items), or switch to using a
[ReplaySubject](#replaysubject) instead.

If the source Observable terminates with an error, the PublishSubject will not emit any items to subsequent observers,
but will simply pass along the error notification from the source Observable.

![Error reactivex-publish-subject-error.png!]({{ "/assets/img/reactivex-publish-subject-error.png" | relative_url}})

#### ReplaySubject

[ReplaySubject][ReplaySubject.java] emits to any observer all of the items that were emitted by the source
Observable(s), regardless of when the observer subscribes.

![Error reactivex-relay-subject.png!]({{ "/assets/img/reactivex-relay-subject.png" | relative_url}})

There are also versions of ReplaySubject that will throw away old items once the replay buffer threatens to grow beyond
a certain size, or when a specified timespan has passed since the items were originally emitted.

If you use a ReplaySubject as an observer, take care not to call its `onNext` method (or its other on methods) from 
multiple threads, as this could lead to coincident (non-sequential) calls, which violates
[the Observable contract](#the-observable-contract) and creates an ambiguity in the resulting Subject as to which item
or notification should be replayed first.


[Observer.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/core/Observer.java
[ConnectableObservable.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/observables/ConnectableObservable.java
[AsyncSubject.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/subjects/AsyncSubject.java
[BehaviorSubject.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/subjects/BehaviorSubject.java
[PublishSubject.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/subjects/PublishSubject.java
[ReplaySubject.java]: https://github.com/ReactiveX/RxJava/blob/3.x/src/main/java/io/reactivex/rxjava3/subjects/ReplaySubject.java