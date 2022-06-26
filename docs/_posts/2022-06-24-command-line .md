---
layout: post
title: Command Line Reference
tags: [Bash, Linux, Mac]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/26-cover.png"
thumbnail: "assets/img/post-cover/26-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## [sed](https://www.gnu.org/software/sed/manual/sed.html)

> [A good reference for sed](http://sed.sourceforge.net/local/docs/An_introduction_to_sed.html)

### sed Pattern for Getting String Between Two Character Sequence

For example, if we would like to extract email address from a string of
`<some text> from=someuser@somedomain.com, <some text>`, which gives "someuser@somedomain.com", we could use

```bash
sed 's/.*from=\(.*\),.*/\1/' <<< "$s"
someuser@somedomain.com
```

## Graphviz

### Generate .png from .dot Source Code File

    dot -Tpng input.dot > output.png

### Assign Color to Nodes and Edges

Example:

    digraph D {
        5 -> 8 [color = green]
        5 -> 20
    
        8 -> 30
        8 -> 6 [color = green]
    
        6 -> 7 [color = green]
        6 -> 9
    
        7 -> 1 [color = green]
    
        20 -> 15
        20 -> 25
    
        5 [fillcolor=green, style=filled]
        8 [fillcolor=green, style=filled]
        6 [fillcolor=green, style=filled]
        7 [fillcolor=green, style=filled]
        1 [fillcolor=green, style=filled]
    }

![example]({{ "/assets/img/tree.png" | relative_url}})

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

## Pretty Printing JSON

    echo '{"name":"Tom"}' | python -m json.tool

or

    echo '{"name":"Tom"}' | jq --indent 4

## Check MD5 Hash Quickly

For example, if you want to check md5 sum of a downloaded package, you can do

    diff <(md5sum package.tar.gz | awk '{print $1}') <(echo "md5sum string")

## Check File Size in MB

    ls -l --block-size=MB

## Install Font

1. Go to the home directory and execute `mkdir .fonts`
2. Put font source file into `.fonts` and update font cache using `fc-cache -fv`

## Check Linux Version (Distro)

    cat /etc/*-release
    cat /etc/os-release

## Display Certain LSB (LINUX STANDARD BASE) and Distribution-Specific Information

    lsb_release -a

## Get Kernel Version

    uname -a

or

    uname -mrs

Sample outputs:

    Linux 2.6.32-5-amd64 x86_64

* **Linux**: Kernel name
* **2.6.32-5-amd64**: Kernel version number
* **x86_64**: Machine hardware name (64 bit)

## See Kernel Version and GCC Version

    cat /proc/version

## RHEL - Check RAM Size

    cat /proc/meminfo

## Ubuntu - Install `.deb` Packages

    sudo dpkg -i DEB_PACKAGE

## Ubuntu - Delete `.deb` Packages

    sudo dpkg -r PACKAGE_NAME

## Download File on Remote Server to Local

[sz](https://docstore.mik.ua/orelly/linux/run/apph_02.htm)

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

## Mac Specific

### Dealing with "command not found" Error

* [command-not-found.com](https://command-not-found.com/)
    - Example: [shasum](https://command-not-found.com/shasum)

### Homebrew Error - fatal: unable to access 'https://github.com/xxx/xxx': LibreSSL SSL_connect: SSL_ERROR_SYSCALL in connection to github.com:443

    git config --global http.sslVerify false

### Dealing with "command not found" Error

* [command-not-found.com](https://command-not-found.com/)
    - Example: [shasum](https://command-not-found.com/shasum)

### [Keep Wi-Fi Connected While Being Locked](https://apple.stackexchange.com/a/97047)

Find out what the network interface is for your wifi. Take "en1" as an example (I have obfuscated my MAC addresses with
"00")

```
ifconfig
en1: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500  
ether 00:00:00:00:00:00  
inet6 0000::000:0000:0000:0000%en1 prefixlen 64 scopeid 0x5
inet 10.0.1.16 netmask 0xffffff00 broadcast 10.0.1.255
media: autoselect
status: active
```

Then, you need to set up the airport util for your wireless card to tell it not to shut off:

```
cd /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources
sudo ./airport en1 prefs DisconnectOnLogout=NO
```

### Mac check ports in use and kill processes that bind to those ports

    sudo lsof -PiTCP -sTCP:LISTEN
    npx kill-port 3000 8080 8081

### Change the Format of the OSX Screen Shot File Name

1. Get rid of the datetime portion of the naming: `defaults write com.apple.screencapture "include-date" 0`
2. Change the default name prefix of the naming: `defaults write com.apple.screencapture name "page"`
3. Optionally adjust the location of the screenshot taken
   `defaults write com.apple.screencapture location "~/Library/Mobile\ Documents/com\~apple\~CloudDocs/screenshots"`

### Check File Size in MB

    ls -rS -lh

### `gcsplit`

[csplit](https://www.gnu.org/software/coreutils/manual/html_node/csplit-invocation.html#csplit-invocation) allows us to
split file by pattern. For OS X users, however, the version of `csplit` that comes with the OS doesn't work. You'll want
the version in coreutils (installable via Homebrew), which is called gcsplit.

#### Install `gcsplit`

    brew install coreutils

#### Use `gcsplit`

Suppose you have a file(`t.txt`) that looks like the following:

    ### header ###
    data1
    data2
    ### header ###
    data3
    data4
    ### header ###
    data5
    data6

Running

    cat t.txt | gcsplit - '/^##/' {*}

Will generate 4 files:

1. xx00

2. xx01

        ### header ###
        data1
        data2

3. xx02

        ### header ###
        data3
        data4

4. xx03

        ### header ###
        data5
        data6