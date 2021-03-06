---
layout: post
title: Platform-Independent Command Line Reference
tags: [Linux, Mac]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Remove Common Prefix of a Group of Files

    for file in prefix*; do mv "$file" "${file#prefix}"; done;
    
Here is an example to remove "bla_" form the following files:

    bla_1.txt
    bla_2.txt
    bla_3.txt
    blub.txt
    
Command

    for file in bla_*; do mv "$file" "${file#bla_}";done;
    
Result in file system:

    1.txt
    2.txt
    3.txt
    blub.txt

## Run or Repeat a Command Every X Seconds Forever

    while true; do echo -n "This is a test of while loop";date ; sleep 5; done

## Monitoring

### List All Background PID's in bash

Use `ps S`. For example:

```
$ vim &
[1] 8263
$ ipython &
[2] 8264
$ ps S
 PID TTY      STAT   TIME COMMAND
 3082 pts/0    Ss     0:00 bash
 3137 pts/0    Sl+    0:00 python /usr/bin/ipython
 8207 pts/2    Ss     0:00 bash
 8263 pts/2    T      0:00 vim
 8264 pts/2    Tl     0:00 python /usr/bin/ipython
 8284 pts/2    Tl     0:00 python /usr/bin/ipython
 8355 pts/2    R+     0:00 ps S
```

If you want get PIDs use below:

```
$ ps S | awk '{print  $  1 }' | grep -E '[0-9]'
3082
3137
8207
8263
8264
8284
8357
8358
835
```

## Search

### Search PDF by Pattern

    find /path -name '*.pdf' -exec sh -c 'pdftotext "{}" - | grep --with-filename --label="{}" --color "your pattern"' \;
    
In the above example, the `find` utility feeds the input of `xargs` with a long list of file names. `xargs` then splits
this list into sub-lists and calls rm once for every sub-list.

### Search Directory by Name

    find starting-directory -type d -name "search-keyword" -print
    
### Search File by Name

    find starting-directory -type f -name "search-keyword" -print

### Search for a Phrase in all Files That Exist under a Certain Path

    grep -rn "matching_phrase" /starting-path
    
## Decompress `.bz2`

    bunzip2 -cf <file>.bz
    
## Execute Multiple Commands in One Line

    A; B    # Run A and then B, regardless of success of A.
    A && B  # Run B if A succeeded.
    A || B  # Run B if A failed.
    A &     # Run A in background.
    
## Convert PDF to Images

    pdftoppm -rx 300 -ry 300 -png file.pdf prefix # 300 specifies resolution

## PDF

### Automate removing the first page of a PDF

I've done this using the [Coherent PDF Command Line Tools Community Release](http://community.coherentpdf.com/). The
syntax for removing the first page when the PDF file has 2 or more pages is:

    cpdf in.pdf 2-end -o out.pdf
