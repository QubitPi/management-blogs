---
layout: post
title: Linux Command Line Reference
tags: [Bash, Linux]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Must-Learn Commands

* [sed](https://www.gnu.org/software/sed/manual/sed.html)
* [A good reference for sed](http://sed.sourceforge.net/local/docs/An_introduction_to_sed.html)

## gawk (an implementation of awk)

* [Manual](../references/gawk.pdf)

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

## Check MD5 Hash Quickly

For example, if you want to check md5 sum of a downloaded package, you can do

    diff <(md5sum package.tar.gz | awk '{print $1}') <(echo "md5sum string")
    
## Check File Size in MB

    ls -l --block-size=MB
    
## Install Font

1. Go to the home directory and execute `mkdir .fonts`
2. Put font source file into `.fonts` and update font cache using `fc-cache -fv`

## System

### Check Linux Version (Distro)

    cat /etc/*-release
    cat /etc/os-release

### Display Certain LSB (LINUX STANDARD BASE) and Distribution-Specific Information

    lsb_release -a
    
### Get Kernel Version

    uname -a
    
or

    uname -mrs
    
Sample outputs:

    Linux 2.6.32-5-amd64 x86_64
    
* **Linux**: Kernel name
* **2.6.32-5-amd64**: Kernel version number
* **x86_64**: Machine hardware name (64 bit)

### See Kernel Version and GCC Version

    cat /proc/version
    
### RHEL - Check RAM Size

    cat /proc/meminfo
    
### Ubuntu - Install `.deb` Packages

    sudo dpkg -i DEB_PACKAGE
    
### Ubuntu - Delete `.deb` Packages

    sudo dpkg -r PACKAGE_NAME
    
### Download File on Remote Server to Local

[sz](https://docstore.mik.ua/orelly/linux/run/apph_02.htm)
