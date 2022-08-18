---
layout: post
title: InputStream
tags: [Java]
category: FINALIZED
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/7-cover.png"
thumbnail: "assets/img/post-cover/7-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

The Java InputStream class, `java.io.InputStream`, represents an ordered stream of bytes. In other words, you can read
data from a Java InputStream as an ordered sequence of bytes. This is useful when reading data from a file, or received
over the network. A Java `InputStream` is typically connected to some data source, like a file, network connection,
pipe etc.

<!--more-->

* TOC
{:toc}

## API

### `read()`

The `read()` method of an InputStream returns an `int` which contains the byte value of the byte read. Here is an
example:

```java
int data = inputstream.read();
```

To read all bytes in a Java `InputStream` you must keep reading until the value -1 is returned. This value means that
there are no more bytes to read from the `InputStream`. Here is an example of reading all bytes from a Java
`InputStream`

```java
int data = inputStream.read();
while(data != -1) {
    // do something with data variable

    data = inputStream.read(); // read next byte
}
```

Subclasses of `InputStream` may have alternative `read()` methods. For instance, the `DataInputStream` allows you to
read Java primitives like `int`, `long`, `float`, `double`, `boolean` etc. with its corresponding methods
`readBoolean()`, `readDouble()` etc.

### `read(byte[])`

The `InputStream` class also contains two `read()` methods which can read data from the `InputStream`'s source into a
byte array. These methods are:

* `int read(byte[])`
* `int read(byte[], int offset, int length)`

The `read(byte[])` method will attempt to read as many bytes into the byte array given as parameter as the array has
space for. The `read(byte[])` method returns an `int` telling how many bytes were actually read. In case less bytes
could be read from the `InputStream` than the byte array has space for, the rest of the byte array will contain the same
data as it did before the read started, so please remember to inspect the returned `int` to see how many bytes were
actually read into the `byte` array.

The `read(byte[], int offset, int length)` method also reads bytes into a byte array, but starts at `offset` bytes into
the array, and reads a maximum of `length` bytes into the array from that position. Again, the
`read(byte[], int offset, int length)` method returns an int telling how many bytes were actually read into the array,
so remember to check this value before processing the read bytes.

For both methods, if the end of stream has been reached, the method returns -1 as the number of bytes read.

Here is an example of how it could look to use the InputStream's read(byte[]) method:

```java
InputStream inputstream = new FileInputStream("c:\\data\\input-text.txt");

byte[] data = new byte[1024];
int bytesRead = inputstream.read(data);

while(bytesRead != -1) {
    doSomethingWithData(data, bytesRead);
    bytesRead = inputstream.read(data);
}
inputstream.close();
```

### Read Performance

Reading an array of bytes at a time is faster than reading a single byte at a time from an `InputStream`. The
difference can easily be a factor 10 or more in performance increase, by reading an array of bytes rather than reading a
single byte at a time.

The exact speedup gained depends on the size of the byte array you read, and the OS, hardware etc. of the computer you
are running the code on. You should study the hard disk buffer sizes etc. of the target system before deciding. Buffer
sizes of 8KB and up will give a good speedup. However, once your byte array exceeds the capacity of the underlying OS
and hardware, you won't get a bigger speedup from a bigger byte array.

You will probably have to experiment with different byte array size and measure read performance, to find the optimal
byte array size.

#### Read Performance - Transparent Buffering via BufferedInputStream

You can add transparent, automatic reading and buffering of an array of bytes from an `InputStream` using a
`BufferedInputStream` . The `BufferedInputStream` reads a chunk of bytes into a byte array from the underlying
`InputStream`. You can then read the bytes one by one from the `BufferedInputStream` and still get a lot of the
speedup that comes from reading an array of bytes rather than one byte at a time. Here is an example of wrapping an
`InputStream` in a `BufferedInputStream`:

```java
InputStream input = new BufferedInputStream(
        new FileInputStream(
            "c:\\data\\input-file.txt"),
            1024 * 1024 /* buffer size */
        )
);
```

### Closing an InputStream

When you are done with an `InputStream` you must close it. You close an `InputStream` by calling its `close()`
method.

```java
InputStream inputstream = new FileInputStream("c:\\data\\input-text.txt");

int data = inputstream.read();
while(data != -1) {
    data = inputstream.read();
}

inputstream.close();
```

The code above is not 100% robust. If an exception is thrown while reading data from the `InputStream`, the `close()`
method is never called. To make the code more robust, you will have to use the try-with-resources construct:

```java
try(InputStream inputstream = new FileInputStream("file.txt")) {
    int data = inputstream.read();
    while(data != -1){
        data = inputstream.read();
    }
}
```

## Miscellaneous

### Convert InputStream to String

A nice way to do this is using [Apache Commons](http://commons.apache.org/)
[IOUtils](https://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/IOUtils.html) to
copy the `InputStream` into a `StringWriter`:

```java
StringWriter writer = new StringWriter();
IOUtils.copy(inputStream, writer, encoding);
String theString = writer.toString();
```

or even

```java
// NB: does not close inputStream, you'll have to use try-with-resources for that
String theString = IOUtils.toString(inputStream, encoding);
```
