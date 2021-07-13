---
layout: post
title: Bash Reference
tags: [Bash, Linux, Mac]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/22-cover.png"
thumbnail: "assets/img/post-cover/22-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}
  
## sed

### Applications

#### Pattern for Getting String Between Two Character Sequence

For example, if we would like to extract email address from a string of
`<some text> from=someuser@somedomain.com, <some text>`, which gives "someuser@somedomain.com", we could use

```bash
sed 's/.*from=\(.*\),.*/\1/' <<< "$s"
someuser@somedomain.com
```

## Testing Using Bash

You can do asserts in Bash. Check out this from the Advanced Bash-Scripting Guide:
http://tldp.org/LDP/abs/html/debugging.html#ASSERT :

### Testing a condition with an assert

```shell script
#!/bin/bash
# assert.sh

#######################################################################
assert ()                 #  If condition false,
{                         #+ exit from script
                          #+ with appropriate error message.
  E_PARAM_ERR=98
  E_ASSERT_FAILED=99


  if [ -z "$2" ]          #  Not enough parameters passed
  then                    #+ to assert() function.
    return $E_PARAM_ERR   #  No damage done.
  fi

  lineno=$2

  if [ ! $1 ] 
  then
    echo "Assertion failed:  \"$1\""
    echo "File \"$0\", line $lineno"    # Give name of file and line number.
    exit $E_ASSERT_FAILED
  # else
  #   return
  #   and continue executing the script.
  fi  
} # Insert a similar assert() function into a script you need to debug.    
#######################################################################


a=5
b=4
condition="$a -lt $b"     #  Error message and exit from script.
                          #  Try setting "condition" to something else
                          #+ and see what happens.

assert "$condition" $LINENO
# The remainder of the script executes only if the "assert" does not fail.


# Some commands.
# Some more commands . . .
echo "This statement echoes only if the \"assert\" does not fail."
# . . .
# More commands . . .

exit $?
```

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
