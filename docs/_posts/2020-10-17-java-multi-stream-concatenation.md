---
layout: post
title: Multi Stream Concatenation
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/12-cover.png"
thumbnail: "assets/img/post-cover/12-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

I want to combine the elements of multiple `Stream` instances into a single `Stream`. What's the best way to do this?

This post compares a few different solutions. 

## `Stream.concat(a, b)`

The JDK provides
[`Stream.concat(a, b)`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#concat-java.util.stream.Stream-java.util.stream.Stream-)
for concatenating two streams. 

```java
void exampleConcatTwo() {
    Stream<String> a = Stream.of("one", "two");
    Stream<String> b = Stream.of("three", "four");

    Stream<String> result = Stream.concat(a, b);

    result.forEach(System.out::println);

    // Output:
    // one
    // two
    // three
    // four
}
```

What if we have more than two streams? 

We could use `Stream.concat(a, b)` multiple times. With three streams we could write
`Stream.concat(Stream.concat(a, b), c)`, although this approach is depressing at three streams, and it rapidly gets
worse as we add more streams.

## Reduce

Alternatively, we can use
[`reduce`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#reduce-java.util.function.BinaryOperator-)
to perform the multiple incantations of `Stream.concat(a, b)` for us. It adapts elegantly to handle any number of input
streams. 

```java
void exampleReduce() {
    Stream<String> a = Stream.of("one", "two");
    Stream<String> b = Stream.of("three", "four");
    Stream<String> c = Stream.of("five", "six");

    Stream<String> result = Stream.of(a, b, c)
            .reduce(Stream::concat)
            .orElseGet(Stream::empty);

    result.forEach(System.out::println);

    // Output:
    // one
    // two
    // three
    // four
    // five
    // six
}
```

Note the `Stream::concat` was invoked in the example above and be careful using this pattern! Note the warning in the
documentation of `Stream.concat(a, b)`:

Use caution when constructing streams from repeated concatenation. Accessing an element of a deeply concatenated stream
can result in deep call chains, or even `StackOverflowError`.
{: .notice--primary}

It takes quite a few input streams to trigger this problem, but it is trivial to demonstrate: 

```java
void exampleStackOverflow() {
    List<Stream<String>> inputs = new AbstractList<Stream<String>>() {
        @Override
        public Stream<String> get(int index) {
            return Stream.of("one", "two");
        }

        @Override
        public int size() {
            return 1000000;
        }
    };

    Stream<String> result = inputs.stream()
            .reduce(Stream::concat)
            .orElseGet(Stream::empty);
    
    long count = result.count(); // probably throws
    
    System.out.println("count: " + count); // probably never reached
}
```

On my workstation, this method throws `StackOverflowError` after several seconds of churning.

What's going on here? We can think of the calls to `Stream.concat(a, b)` as forming a binary tree. At the root is the
concatenation of all the input streams. At the leaves are the individual input streams. Let's look at the trees for up
to five input streams as formed by our reduce operation.

![Stream deep concat problem illustration]({{ "/assets/img/stream-deep-concat-problem.png" | relative_url}})

The trees are perfectly unbalanced! Each additional input stream adds one layer of depth to the tree and one layer of
indirection to reach all the other streams. This can have a noticeable negative impact on performance. With enough
layers of indirection we'll see a `StackOverflowError`.

## Balance

If we're worried that we'll concatenate a large number of streams and run into the aforementioned problems, we can
balance the tree. This is as if we're optimizing a O(n) algorithm into a O(logn) one. We won't totally eliminate the
possibility of `StackOverflowError`, and there may be other approaches that perform even better, but this should be
quite an improvement over the previous solution. 

```java
void exampleBalance() {
    Stream<String> a = Stream.of("one", "two");
    Stream<String> b = Stream.of("three", "four");
    Stream<String> c = Stream.of("five", "six");
    Stream<String> result = concat(a, b, c);

    result.forEach(System.out::println);

    // Output:
    // one
    // two
    // three
    // four
    // five
    // six
}

@SafeVarargs
static <T> Stream<T> concat(Stream<T>... in) {
    return concat(in, 0, in.length);
}

static <T> Stream<T> concat(Stream<T>[] in, int low, int high) {
    switch (high - low) {
        case 0:
            return Stream.empty();
        case 1:
            return in[low];
        default:
            int mid = (low + high) >>> 1;
            Stream<T> left = concat(in, low, mid);
            Stream<T> right = concat(in, mid, high);
            return Stream.concat(left, right);
    }
}
```

## `Flatmap`

There is another way to concatenate streams that is built into the JDK, and it does not involve `Stream.concat(a, b)` at
all. It is
[`flatMap`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#flatMap-java.util.function.Function-).

```java
void exampleFlatMap() {
    Stream<String> a = Stream.of("one", "two");
    Stream<String> b = Stream.of("three", "four");
    Stream<String> c = Stream.of("five", "six");

    Stream<String> result = Stream.of(a, b, c)
            .flatMap(Function.identity());

    result.forEach(System.out::println);

    // Output:
    // one
    // two
    // three
    // four
    // five
    // six
}
```

**This generally outperforms the solutions based on `Stream.concat(a, b)` when each input stream contains fewer than 32
elements. As we increase the element count past 32, `flatMap` performs comparatively worse and worse as the element
count rises.**

`flatMap` avoids the `StackOverflowError` issue but it comes with its own set of quirks. For example, it interacts
poorly with infinite streams. Calling `findAny` on the concatenated stream may cause the program to enter an infinite
loop, whereas the other solutions would terminate almost immediately. 

```java
void exampleInfiniteLoop() {
    Stream<String> a = Stream.generate(() -> "one");
    Stream<String> b = Stream.generate(() -> "two");
    Stream<String> c = Stream.generate(() -> "three");

    Stream<String> combined = Stream.of(a, b, c)
            .flatMap(Function.identity());

    Optional<String> any = combined.findAny(); // infinite loop
    System.out.println(any); // never reached
}
```

(The infinite loop is an implementation detail. This could be fixed in the JDK without changing the contract of
`flatMap`.)

Also, **`flatMap` forces its input streams into sequential mode even if they were originally parallel**. The outermost
concatenated stream can still be made parallel, and we will be able to process elements from distinct input streams in
parallel, but the elements of each individual input stream must all be processed sequentially.
