---
layout: post
title: Bit Manipulation
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/36-cover.png"
thumbnail: "assets/img/post-cover/36-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Some of the most basic operations on bits is shifting in the form of a shift left and a shift right. In Java, the
operators are << and >>. Here is what they do:

```java
/* 00000001 << 1 = 00000010 */
1 << 1 == 2

/* 00000001 << 3 = 00001000 */
1 << 3 == 8

/* 11111111 11111111 11111111 11110000 >> 4 = 11111111 11111111 11111111 11111111 */
0xFFFFFFF0 >> 4 == 0xFFFFFFFF

/* 00001111 11111111 11111111 11111111 >> 4 = 00000000 11111111 11111111 11111111 */
0x0FFFFFFF >> 4 == 0x00FFFFFF
```

Note that the right shift operator is signed. Java, as with many languages, uses the most significant bit as a "sign"
bit. A negative number's most significant bit is always '1' in Java. A signed shift-right will shift in the value of the
sign. So, a binary number that begins with '1' will shift in '1's. A binary number that begins with '0' will shift in
'0's. Java does bitwise operators on integers, so be aware!

You can use a third shift operator called the "unsigned shift right" operator: >>> for always shifting in a "0"
regardless of the sign:

```java
/* 10000000 00000000 00000000 00000000 >>> 1 = 01000000 00000000 00000000 00000000 */
0x80000000 >>> 1 == 0x40000000

/* 10000000 00000000 00000000 00000000 >> 1 = 11000000 00000000 00000000 00000000 */
0x80000000 >> 1  == 0xC0000000
```

One of the uses for in creating quick powers of 2. Shifting "1" by 1 is 2, by 2 is 4, by 4 is 8, etc.. Similarly, a
quick shift right will divide a number by two.

This is also useful for creating masks. A bitmask is used for isolating or altering a specific part of a binary number.
This is described in detail in the next section. For now, assume that we want to create the bitmask 00001000. Then the
code is just:

```java
int bitmask = 1 << 3;
```

You can create more complicated bit masks using binary arithmetic operators which are also described in the next
section.

A very convenient way to work with binary numbers in your code is to use the `Integer.parseInt()` command.
`Integer.parseInt("101",2)` creates an integer with the binary value of 101 (decimal 5). This means that you can even
do a for loop with binary in this manner:

```java
/* loops from 5 up to and including 15 */
for (int b = Integer.parseInt("0101",2); b <= Integer.parseInt("1111",2); b++) {
    /* do stuff here */
}
```

### Binary "BitWise" Operations

Here are four common bit operators in Java.

- `~`: The unary complement
- `&`: Bitwise and
- `^`: Bitwise exclusive or
- `|`: Bitwise inclusive or

Here are some examples of their use (just showing the binary for simplicity):

```java
1010 & 0101 == 0000
1100 & 0110 == 0100

1010 | 0101 == 1111
1100 | 0110 == 1110

~1111 == 0000
~0011 == 1100

1010 ^ 0101 == 1111
1100 ^ 0110 == 1010
```

You can "set" a bit in a number by "or"-ing with another number with that bit (and only that bit) set to 1.

```java
10000001 | 00100000 = 10100001 /* turned on bit 5 */
10000001 | 1 << 5 = 10100001 /* did the same thing
00000000 | 1 << 2 | 1 << 5 = 00100100
```

There are other methods for setting bits that do not require branching which are documented at
http://graphics.stanford.edu/~seander/bithacks.html

You can turn off a bit by anding with a binary number of all 1's, except for the bit to be set.

```java
01010101 & ~(1<<2) == 01010101 & 11111011 == 01010001
```
