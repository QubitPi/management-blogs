---
layout: post
title: Create Distributed Hardware via Virtualbox
tags: [ImageMagick]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Large enterprise applications usually requires distributed data storage to meet their massive data requirements. It is
important for developers to have great understanding of distributed systems. One great way to learn them is setup a
distributed environment locally and get hands dirty by playing with them. Virtualbox provides a great solution with a
**very user-friendly UI for configuring network among VMs and hosts**.

<!--more-->

* TOC
{:toc}

## Set up a VM Cluster

Each VM needs to have 2 network adaptors/interfaces.

1. **NAT**: allow the VM to communicate with the outside world through host network connection.
2. **Host-only**: allows host-guest and guest-guest interactions. 

The advantage of having two such interfaces is VM can access world internet while the outside world cannot see the VM
(This is made possible via "NAT"). Host, however, can access VM through "host-only" interface, making the VM very secure
as a personal VM to host users.

NAT is the default interface for each newly created VM. So you don't need to configure anything for NAT.

## Create a Host-only Connection in VirtualBox

Start by going to the "Global Tools" on the upper right corner of VirtualBox UI. Choose an adaptor, such as "vboxnet0".
Modify that interface:

* In "DHCP Server" disable DHCP.
* Make a note of the IP address("Adapter"). Feel free to set the IP address as well, if you like. Lets say you put the
  following configs, which we will be referring to in the next sections
  
        IPv4 Address: 192.168.99.1
        
* IPv4 Network Mask: 255.255.255.0

## Configure VM Settings

Next, assign this host-only adapter to the virtual machine. Select the VM and press "Settings". Go to the "Network" tab,
and select "Adpater 2". Enable the adapter, set it to a "Host-only Adapter", and select the adpater you created above

## Configure VM after installation

Boot VM and configure VM network file.

## Scan Available Interfaces

First, you will need to find your available interfaces by

    ip link
    
This will show you the interfaces you have, like so:

    1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT group default qlen 1
        link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    2: enp0s3: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000
        link/ether 08:00:27:1d:bd:93 brd ff:ff:ff:ff:ff:ff
    3: enp0s8: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000
        link/ether 08:00:27:c2:c1:92 brd ff:ff:ff:ff:ff:ff
        
## Add Host-Only Interface to Config

Lets say you run

    ifconfig
    
and you see the following 2 adapters coming up

1. `lo`
2. `enp0s3`

Then your host-only interface will be `enp0s8`. Now add this interface to the interfaces(in this case `enp0s8`) by
modifying `/etc/network/interfaces`:

    # On GUEST
    sudo nano /etc/network/interfaces
    
Add the following to configure adaptor/interface to use a static IP address and come up when the system starts.

    # The host-only network interface
    auto enp0s8
    iface enp0s8 inet static
    address 192.168.99.101
    netmask 255.255.255.0
    network 192.168.99.0
    broadcast 192.168.99.255
    
Note that `192.168.56.101` will be the static IP assigned to this VM. Make sure this address is different from
`192.168.99.1`.

Finally, you'll probably want to add some names to your host machine's `/etc/hosts` file. Make sure to add each hostname
you will use on your guest.

    # On HOST: edit /etc/hosts
    sudo nano /etc/hosts
    
Add a line that starts with the static IP address for the guest, followed by a tab, then each of the hostnames that should point to your guest.

    # Inside /etc/hosts on your HOST
    192.168.99.101    myguest.home.local www.mycoolsite.com anyother.fakesite.com
    
Now Reboot VM and you should be able to access the VM from your host via `ssh`.

## Setup Password-less SSH Login to VM (Optional)

1. From host

        ssh-keygen
        
2. Then you'll need to copy the new key to VM

        ssh-copy-id user@host

3. If your server uses custom port number, then

        ssh-copy-id "user@host -p 1234"
        
4. After the key is copied, ssh into the machine as normal

        ssh user@root
        
## Manage VM's

Start VM from command line

    VBoxManage startvm <vmName> --type headless
    
Stop virtual machine

    VBoxManage controlvm vm_name poweroff
    
Stanup VM as a HTTP server

    python -m SimpleHTTPServer 8000
    
Then you can access webpages in VM in your host web browser - VM_IP:8000
