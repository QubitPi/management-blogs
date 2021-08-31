---
layout: post
title: Command Line Reference
tags: [Bash, Linux, Mac]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/23-cover.png"
thumbnail: "assets/img/post-cover/23-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Mac

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

### Pretty Pring JSON

    echo '{"name":"Tom"}' | python -m json.tool

or

    echo '{"name":"Tom"}' | jq --indent 4
  
## Linux

### Dealing with "command not found" Error

* [command-not-found.com](https://command-not-found.com/)
  - Example: [shasum](https://command-not-found.com/shasum)

### Must-Learn Commands

* [sed](https://www.gnu.org/software/sed/manual/sed.html)
* [A good reference for sed](http://sed.sourceforge.net/local/docs/An_introduction_to_sed.html)

### gawk (an implementation of awk)

> [Apache Oozie]({{ "/assets/pdf/apache-oozie-the-workflow-scheduler-for-hadoop.pdf" | relative_url}})

### Graphviz

#### Generate .png from .dot Source Code File

    dot -Tpng input.dot > output.png
    
#### Assign Color to Nodes and Edges

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

### Check MD5 Hash Quickly

For example, if you want to check md5 sum of a downloaded package, you can do

    diff <(md5sum package.tar.gz | awk '{print $1}') <(echo "md5sum string")
    
### Check File Size in MB

    ls -l --block-size=MB
    
### Install Font

1. Go to the home directory and execute `mkdir .fonts`
2. Put font source file into `.fonts` and update font cache using `fc-cache -fv`

### System

#### Check Linux Version (Distro)

    cat /etc/*-release
    cat /etc/os-release

#### Display Certain LSB (LINUX STANDARD BASE) and Distribution-Specific Information

    lsb_release -a
    
#### Get Kernel Version

    uname -a
    
or

    uname -mrs
    
Sample outputs:

    Linux 2.6.32-5-amd64 x86_64
    
* **Linux**: Kernel name
* **2.6.32-5-amd64**: Kernel version number
* **x86_64**: Machine hardware name (64 bit)

#### See Kernel Version and GCC Version

    cat /proc/version
    
#### RHEL - Check RAM Size

    cat /proc/meminfo
    
#### Ubuntu - Install `.deb` Packages

    sudo dpkg -i DEB_PACKAGE
    
#### Ubuntu - Delete `.deb` Packages

    sudo dpkg -r PACKAGE_NAME
    
#### Download File on Remote Server to Local

[sz](https://docstore.mik.ua/orelly/linux/run/apph_02.htm)
