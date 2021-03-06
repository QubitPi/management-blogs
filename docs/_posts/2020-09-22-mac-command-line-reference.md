---
layout: post
title: Mac Command Line Reference
tags: [Mac]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

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
