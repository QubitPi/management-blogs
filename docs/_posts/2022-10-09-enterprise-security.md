---
layout: post
title: Building Enterprise Security
tags: [Security, VPN, Entrepreneurship]
category: FINALIZED
color: rgb(27, 185, 115)
feature-img: "assets/img/post-cover/30-cover.png"
thumbnail: "assets/img/post-cover/30-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


OpenVPN
-------

It is necessary to secure and protect the data transmitted on a wide network of Internet. One use case is that more and
more enterprises offer their customers or business partners a protected access to relevant data for their business
relations such as ordering formulae or stock data. Virtual Private Networks (VPNs) were created out of a greater need
for such secured communication across an otherwise unprotected infrastructure, such as internet, over private channels,
where the community at large isn't able to eavesdrop

we have three typical scenarios for VPN solutions in modern enterprises as follows:

1. An intranet spanning over several locations of a company
2. Access for home or field workers with changing IPs, mobile devices, and centralized protection
3. An extranet for customers or business partners

Each of these typical scenarios requires special security considerations and setups. The external home workers will need
different access to servers in the company than the customers and business partners. In fact, access for business
partners and customers must be restricted severely.

### Computer Networks Basics

To understand VPN, some basic network concepts need to be understood.

All data exchange in computer networks is based on **protocols**. Protocols are like languages or rituals that must be
used between communication partners in networks. Without the correct use of the correct protocol, communication fails.

#### Protocols and Layers

There are a large number of protocols involved in any action you take when you access the Internet or a PC in your local
network. Your **Network Interface Card (NIC)** will communicate with a hub, a switch, or a router. Your application will
communicate with its partner on a server on another PC, and many more protocol-based communication procedures are
necessary to exchange data.

Because of this, the **Open Systems Interconnection (OSI) specification** was created. Every protocol used in today's
networks can be classified by this scheme. The OSI specification defines seven numbered layers of data exchange which
start at layer 1 (the physical layer) of the underlying network media (electrical, optical, or radio signals) and span
up to layer 7 (the application layer), where applications on PCs communicate with each other.

The layers of the OSI model are as follows:

1. **Physical layer**: Sending and receiving through the hardware
2. **Data link layer**: Encoding and decoding data packets into bits
3. **Network layer**: Switching, routing, addressing, error handling, and so on
4. **Transport layer**: End-to-end error recovery and flow control
5. **Session layer**: Establishing connections and sessions between applications
6. **Presentation layer**: Translating between application data formats and network formats
7. **Application layer**: Application-specific protocols

_In the Internet, however, a slightly different approach is used_. The Internet is mainly based on the **Internet
Protocol (IP)**.

The layers of the IP model are as follows:

1. **Link layer**: A concatenation of OSI layers 1 and 2 (the physical and data link layers).
2. **Network layer**: Comprising the network layer of the OSI model
3. **Transport layer**: Comprising protocols, such as **Transmission Control Protocol (TCP)** and **User Datagram
   Protocol (UDP)**, which are the basis for protocols of the application layer.
4. **Application layer**: Concatenation of OSI layers 5 through 7 (the session, presentation, and application layers).

A TCP/IP network packet consists of two parts - header and data. The header is a sort of label containing metadata on
sender, recipient, and administrative information for the transfer. On the networking level of an Ethernet network these
packets are called frames. In the context of the Internet Protocol these packets are called datagrams, Internet datagrams,
IP datagrams, or simply **packets**

So what do VPNs do? **VPN software takes IP packets or Ethernet frames and wraps them into another packet**.
Specifically:

* Whole network packets (frames, datagrams) consisting of header and data are _wrapped_ into new packets
* All data, including metadata, such as recipient and sender, are encrypted
* The new packets are labeled with new headers containing meta-information about the VPN and are addressed to the VPN
  partner

All VPN software systems differ only in the special way of _wrapping_ and _locking_ the data.

#### Tunneling and Overhead

VPN technology is often called **tunneling** because the data in a VPN connection is protected from the Internet, as the
walls of a road or rail tunnel protect the traffic in the tunnel from the weight of stone of the mountain above. Let's
now have a closer look at how the VPN software does this.

![Error loading vpn-tunneling-and-overhead.png]({{ "/assets/img/vpn-tunneling-and-overhead.png" | relative_url}})

The VPN software in the locations A and B encrypts and decrypts the data and sends it through the tunnel. Like cars or
trains in a tunnel, the data cannot go anywhere else but to the other tunnel endpoint (if they are properly routed).

The following are put together and wrapped into one new package:

* Tunnel information (such as the address of the other endpoint)
* Encryption data and methods
* The original IP packet (or network frame)

The new package is then sent to the other tunnel endpoint. The payload of this package now holds the complete IP packet
(or network frame), but in an encrypted form. It is, therefore, not readable to anyone who does not possess the right
key. The new header of the packet simply contains the addresses of the sender, recipient, and other metadata that is
necessary for and provided by the VPN software that is used.

![Error loading vpn-wrapping.png]({{ "/assets/img/vpn-wrapping.png" | relative_url}})

Perhaps you have noticed that the amount of data that is sent grows during the process of 'wrapping'. Depending on the
VPN software used, this so-called **overhead** can become a very important factor. The overhead is the difference
between the net data that is sent to the tunnel software and the gross data that is sent through the tunnel by the VPN
software. If a file of 1MB is sent from user A to user B, and this file causes 1.5MB traffic in the tunnel, then the
overhead would be 50%, a very high level indeed (note that every protocol that is used causes overhead, so not all of
that 50% might be the fault of the VPN solution.). The overhead caused by the VPN software depends on the amount of
organizational (meta-) data and the encryption used. Whereas the first depends only on the VPN software used, the latter
is simply a matter of choice between security and speed. In other words, the better the cipher you use for encryption,
the more overhead you will produce. _Speed versus security is your choice_.

#### VPN Concepts

##### A Proposed Standard for Tunneling

In principle, tunneling can be done on almost all layers of the OSI model. The **General Routing Encapsulation (GRE)**
provides a standard for tunneling data, which was defined in 1994 in Request for Comments (**RFCs**) 1701 and 1702, and
later in RFCs 2784 and 2890. Perhaps because this definition is not a protocol definition, but more or less a standard
proposal on how to tunnel data, this implementation has found its way into many devices and has become the basis for
other protocols.

##### Protocols Implemented on OSI Layer 2

VPN technologies residing in layer 2 can theoretically tunnel any kind of packet. In most cases a virtual
**Point-to-Point Protocol (PPP)** device is established, which is used to connect to the other tunnel endpoint. A PPP
device is normally used for modem or DSL connections.

4 well known layer-2 VPN technologies, which are defined by RFCs, use encryption methods and provide user
authentication, as follows:

1. The **Point to Point Tunneling Protocol (PPTP)**, RFC 2637, which was developed with the help of Microsoft, is an
   expansion of the PPP. It is integrated in all newer Microsoft operating systems. PPTP uses GRE for encapsulation and
   can tunnel IP, IPX, and other protocols over the Internet. The main disadvantage is the restriction that there can
   only be one tunnel at a time between communication partners.
2. The **Layer 2 Forwarding (L2F)**, RFC 2341, was developed almost at the same time by other companies, including
   Cisco, and offers more possibilities than PPTP, especially regarding tunneling of network frames and multiple
   simultaneous tunnels.
3. The **Layer 2 Tunneling Protocol (L2TP)**, RFC 2661, is accepted as an industry standard and is being widely used by
   Cisco and other manufacturers. Its success is based on the fact that it combines the advantages of L2F and PPTP
   without suffering their drawbacks. Even though it does not provide its own security mechanisms, it can be combined
   with technologies offering such mechanisms, such as IPsec (see the section
   [_Protocols Implemented on OSI Layer 3_](#protocols-implemented-on-osi-layer-3)).
4. The **Layer 2 Security Protocol (L2Sec)**, RFC 2716, was developed to provide a solution to the security flaws of
   IPsec. Even though its overhead is rather big, the security mechanisms that are used are secure, because mainly
   SSL/TLS is used

##### Protocols Implemented on OSI Layer 3

**IPsec (Internet Protocol Security)** is the most widespread tunneling technology. In fact it is a more complex set of
protocols, standards, and mechanisms than a single technology.

IPsec was developed as an Internet Security Standard on layer 3 and has been standardized by the Internet Engineering
Task Force (IETF) since 1995. IPsec can be used to encapsulate any traffic of application layers, but no traffic of
lower network layers. Network frames, IPX packets, and broadcast messages cannot be transferred, and network address
translation is only possible with restrictions.

Nevertheless IPsec can use a variety of encryption mechanisms, authentication protocols, and other security
associations. IPsec software exists for almost every platform.

The main advantage of IPsec is the fact that it is being used everywhere. An administrator can choose from a large
number of hardware devices, software implementations, and administration frontends to provide networks with a secure
tunnel.

There are two methods that IPsec uses:

1. **Tunnel mode**: All IP packets are encapsulated in a new packet and sent to the other tunnel endpoint, where the VPN
   software unpacks them and forwards them to the recipient. In this way the IP addresses of sender and recipient and
   all other metadata are protected.
2. **Transport mode**: In transport mode, only the payload of the data section is encrypted and encapsulated. In this
   way the overhead becomes significantly smaller than in tunnel mode, but an attacker can easily read the metadata and
   find out who is communicating with whom. However the data is encrypted and therefore protected, which makes IPsec a
   real 'private' VPN solution.

##### Protocols Implemented on OSI Layer 4

### What is a VPN?

Simply put, **a VPN allows an administrator to create a "local" network between multiple computers on varying network
segments**. In some instances, those machines can be on the same LAN, they can be distant from each other across the
vast Internet, or they can even be connected across a multitude of connection media such as wireless uplinks, satellite,
dial-up-networking, and so on. The P in VPN comes from the added protection to make that virtual network private.
Network traffic that is flowing over a VPN is often referred to as **inside the (VPN) tunnel**, compared to all the
other traffic that is **outside the tunnel**.

In the following figure, network traffic is shown as it traditionally traverses across multiple network segments and the
general Internet. Here, this traffic is relatively open to inspection and analysis, which is insecure:

![Error loading vpn-insecure-network.png]({{ "/assets/img/vpn-insecure-network.png" | relative_url}})

When a VPN is used, the traffic inside the tunnel is no longer identifiable. Here is an example of the traffic within a
VPN. While the VPN itself is routed across the Internet like in the preceding figure, devices along the network path
only see VPN traffic; those devices are completely unaware of what is being transmitted inside the private tunnel.
Protected protocols, such as HTTPS and SSH, will still be protected inside the tunnel from other VPN users, but will be
additionally unidentifiable from outside the tunnel. A VPN not only encrypts the traffic within, it hides and protects
individual data streams from those outside the tunnel.

![Error loading vpn-secure.png]({{ "/assets/img/vpn-secure.png" | relative_url}})

#### OpenVPN

OpenVPN is often called an SSL-based VPN, as it uses the SSL/TLS protocol to secure the connection. However, OpenVPN
also uses HMAC in combination with a digest (or hashing) algorithm for ensuring the integrity of the packets delivered.
It can be configured to use pre-shared keys as well as X.509 certificates. These features are not typically offered by
other SSL-based VPNs.

Furthermore, OpenVPN uses a **virtual network adapter (a tun or tap device)** as an interface between the user-level
OpenVPN software and the operating system. In general, any operating system that has support for a tun/tap device can
run OpenVPN. This currently includes Linux, Free/Open/NetBSD, Solaris, AIX, Windows, and Mac OS, as well as iOS/Android
devices. For all these platforms, client software needs to be installed, which sets OpenVPN apart from client-less
or web-based VPNs.

OpenVPN has the notion of a control channel and a data channel, both of which are encrypted and secured differently.
However, all traffic passes over a single UDP or TCP connection. The control channel is encrypted and secured using
SSL/TLS, the data channel is encrypted using a custom encryption protocol.

##### OpenVPN Packages

There are several OpenVPN packages available on the Internet:

* The open source or community version of OpenVPN
* OpenVPN Access Server, the closed-source commercial offering by OpenVPN Inc.
* The mobile platform versions of OpenVPN for both Android and iOS (part of the code is closed-source, as a requirement
  of Apple)

##### OpenVPN Internals

###### The Tun/Tap Driver

One of the basic building blocks of OpenVPN is the tun/tap driver. The concept of the tun/tap driver comes from the
Unix/Linux world, where it is often natively available as part of the operating system. This is a virtual network
adapter that is treated by the operating system as either a point-to-point adapter (tun-style) for IP-only traffic or as
a full virtual Ethernet adapter for all types of traffic (tap-style). At the backend of this adapter is an application,
such as OpenVPN, to process the incoming and outgoing traffic. Linux, Free/Open/NetBSD, Solaris and Mac OS include a tun
kernel driver, which is capable of both tun-style and tap-style operations.

![Error loading vpn-tun-tap.png]({{ "/assets/img/vpn-tun-tap.png" | relative_url}})

The flow of traffic from a user application via OpenVPN is depicted in the preceding diagram. In the diagram, the
application is sending traffic to an address that is reachable via the OpenVPN tunnel. The steps are as follows:

1. The application hands over the packet to the operating system.
2. The OS decides using normal routing rules that the packet needs to be routed via the VPN.
3. The packet is then forwarded to the kernel tun device.
4. The kernel tun device forwards the packets to the (user-space) OpenVPN process.
5. The OpenVPN process encrypts and signs the packet, fragments it if necessary, and then hands it over to the kernel
   again to send it to the address of the remote VPN endpoint.
6. The kernel picks up the encrypted packet and forwards it to the remote VPN endpoint, where the same process is
   reversed. It can also be seen in this diagram that the performance of OpenVPN will always be less than that of a
   regular network connection. For most applications, the performance loss is minimal and/or acceptable. However, for
   speeds greater than 1GBps, there is a performance bottleneck, both in terms of bandwidth and latency.

### Additional OpenVPN Resources

* [Free Profile](https://www.vpnbook.com/) - OpenVPN tab (need VPN access first)
* [Bypass OpenVPN for particular IP](https://serverfault.com/a/487471)

#### Config File

When people talk about "openvpn client config", they are referring to the `.ovpn` files that you drop to the
"Import Profiles" section of OpenVPN. Add those lines to `.ovpn` file after, for example, "remote pl226.vpnbook.com 80"
line

#### Contingency Plan

* [Lantern](https://github.com/getlantern/download)
* [Free VPN for Chrome - VPN Proxy VeePN](https://chrome.google.com/webstore/detail/free-vpn-for-chrome-vpn-p/majdfhpaihoncoakbjgbdhglocklcgno?hl=en)


AWS Identity and Access Management (IAM)
----------------------------------------

IAM provides the infrastructure necessary to control authentication and authorization for a user's account. The IAM
infrastructure includes the following elements:

![Error loading intro-diagram-policies-800.png]({{ "/assets/img/intro-diagram-policies-800.png" | relative_url}})

* **IAM Resources** The user, group, role, policy, and identity provider objects that are stored in IAM. As with other
  AWS services, we can add, edit, and remove resources from IAM. A resource is an object that exists within a service.
  Examples include an Amazon EC2 instance, an IAM user, and an Amazon S3 bucket. **The service defines a set of actions
  that can be performed on each resource**. If you create a request to perform an unrelated action on a resource, that
  request is denied. For example, if you request to delete an IAM role but provide an IAM group resource, the request
  fails.
* **IAM Identities** The IAM resource objects that are used to identify and group. We can attach a policy to an IAM
  identity. These include users, groups, and roles.
* **IAM Entities** The IAM resource objects that AWS uses for authentication. These include IAM users and roles.
* **Principals** A person or application that can make a request for an action or operation on an AWS resource. The
  principal is authenticated as the AWS account root user or an IAM entity to make requests to AWS. As a best practice,
  do not use root user credentials for daily work. Instead, create IAM entities (users and roles). We can also support
  federated users or programmatic access to allow an application to access our AWS account.

  When a principal tries to use the AWS Management Console, the AWS API, or the AWS CLI, that principal sends a request
  to AWS. The request includes the following information

    - **Actions or operations** The actions or operations that the principal wants to perform. This can be an action in
      the AWS Management Console, or an operation in the AWS CLI or AWS API.
    - **Resources** The AWS resource object upon which the actions or operations are performed.
    - **Principal** The person or application that used an entity (user or role) to send the request. Information about
      the principal includes the policies that are associated with the entity that the principal used to sign in.
    - **Environment data** Information about the IP address, user agent, SSL enabled status, or the time of day.
    - **Resource data** Data related to the resource that is being requested. This can include information such as a
      DynamoDB table name or a tag on an Amazon EC2 instance.

  AWS gathers the request information into a request context, which is used to evaluate and authorize the request.
* **Authentication**  A principal must be authenticated (signed in to AWS) using their credentials to send a request to
  AWS. Some services, such as Amazon S3 and AWS STS, allow a few requests from anonymous users. However, they are the
  exception to the rule.

  To authenticate from the console as a root user, we must sign in with our email address and password. As an IAM user,
  provide our account ID or alias, and then our user name and password. To authenticate from the API or AWS CLI, we must
  provide our access key and secret key. We might also be required to provide additional security information. For
  example, AWS recommends that we use multi-factor authentication (MFA) to increase the security of our account.
* **Authorization** We must also be authorized (allowed) to complete our request. During authorization, AWS uses values
  from the request context to check for policies that apply to the request. It then uses the policies to determine whether
  to allow or deny the request. **Most policies are stored in AWS as JSON documents** and specify the permissions for
  principal entities. There are several types of policies that can affect whether a request is authorized. _To provide
  our users with permissions to access the AWS resources in their own account, we need only identity-based policies_.
  Resource-based policies are popular for granting cross-account access. The other policy types are advanced features
  and should be used carefully.

  AWS checks each policy that applies to the context of a request. If a single permissions policy includes a denied
  action, AWS denies the entire request and stops evaluating. This is called an **explicit deny**. Because requests are
  denied by default, AWS authorizes a request only if every part of the request is allowed by the applicable permissions
  policies.

### Create IAM Admin User and User Group

As a best practice, do not use the AWS account root user for any task where it's not required. Instead,
[create a new IAM user for each person that requires administrator access][create IAM admin]. Then make those users
administrators by placing the users into an "Administrators" user group to which you attach the AdministratorAccess
managed policy.

> ⚠️ **Safeguard our root user credentials and don't use them for everyday tasks** ⚠️
>
> When we create an AWS account you establish a root username and password to sign in to the AWS Management Console.
> Safeguard our root user credentials the same way we would protect other sensitive personal information. We can do
> this by configuring MFA for our root user credentials. It is not recommended to generate access keys for our root
> user, because they allow full access to all our resources for all AWS services, including our billing information.
> Don't use our root user for everyday tasks. Use the root user to complete the tasks that only the root user can
> perform. For the complete list of these tasks, see [Tasks that require root user credentials][root user tasks] in the
> _AWS General Reference_.

### Identities

#### User Groups

An IAM user group is a collection of IAM users. User groups let you specify permissions for multiple users, which can
make it easier to manage the permissions for those users. For example, you could have a user group called Admins and
give that user group typical administrator permissions. Any user in that user group automatically has Admins group
permissions. If a new user joins your organization and needs administrator privileges you can assign the appropriate
permissions by adding the user to the Admins user group. If a person changes jobs in your organization, instead of
editing that user's permissions you can remove him or her from the old user groups and add him or her to the appropriate
new user groups.

Here are some important characteristics of user groups:

* A user group can contain many users, and a user can belong to multiple user groups.
* User groups can't be nested; they can contain only users, not other user groups.
* There is no default user group that automatically includes all users in the AWS account. If you want to have a user
  group like that, you must create it and assign each new user to it.
* The number and size of IAM resources in an AWS account, such as the number of groups, and the number of groups that a user can be a member of, are limited. For more information, see
  [IAM and AWS STS quotas, name requirements, and character limits](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_iam-quotas.html).

The following diagram shows a simple example of a small company. The company owner creates an **Admins** user group for
users to create and manage other users as the company grows. The Admins user group creates a Developers user group and a
Test user group. Each of these user groups consists of users (humans and applications) that interact with AWS (Jim,
Brad, DevApp1, and so on). Each user has an individual set of security credentials. In this example, each user belongs
to a single user group. However, users can belong to multiple user groups.

![Error loading relationship-between-entities-example-diagram.png]({{ "/assets/img/relationship-between-entities-example-diagram.png" | relative_url}})

References

* [Creating IAM user groups](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_create.html)
* [Managing IAM user groups](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage.html)

### Access Management

We manage access in AWS by creating policies and attaching them to IAM [identities](#identities) (users, groups of
users, or roles) or AWS resources. A policy is an object in AWS that, when associated with an identity or resource,
defines their permissions. AWS evaluates these policies when an IAM principal (user or role) makes a request.
Permissions in the policies determine whether the request is allowed or denied. Most policies are stored in AWS as JSON
documents. AWS supports six types of policies:

1. [identity-based policies](#identity-based-policies)
2. [resource-based policies](#resource-based-policies)
3. permissions boundaries
4. Organizations SCPs
5. ACLs,
6. and session policies.

#### Identity-Based Policies

Identity-based policies are JSON permissions policy documents that control what actions an identity (users, groups of
users, and roles) can perform, on which resources, and under what conditions. Identity-based policies can be further
categorized:

* **Managed policies** - Standalone identity-based policies that you can attach to multiple users, groups, and roles in
  your AWS account. There are two types of managed policies:
    - **AWS managed policies** - Managed policies that are created and managed by AWS.
    - **Customer managed policies** - Managed policies that you create and manage in your AWS account. Customer managed
      policies provide more precise control over your policies than AWS managed policies.
* **Inline policies** - Policies that you add directly to a single user, group, or role. Inline policies maintain a
  strict one-to-one relationship between a policy and an identity. They are deleted when you delete the identity.

To learn how to choose between managed and inline policies, see
[Choosing between managed policies and inline policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_managed-vs-inline.html#choosing-managed-or-inline).

#### Resource-Based Policies

Resource-based policies are JSON policy documents that we attach to a resource such as an Amazon S3 bucket. The policies
grant the specified principal permission to perform specific actions on that resource and defines under what conditions
this applies. _Resource-based policies are inline policies_; there are no managed resource-based policies.

To enable cross-account access, we can specify an entire account or IAM entities in another account as the principal in
a resource-based policy. Adding a cross-account principal to a resource-based policy, however, is only half of
establishing the trust relationship. When the principal and the resource are in separate AWS account, we must also use
an identity-based policy to grant the principal access to the resource. However, if a resource-based policy grants
access to a principal in the same account, no additional identity-based policy is required.  For step-by step
instructions for granting cross-account access, see
[IAM tutorial: Delegate access across AWS accounts using IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/tutorial_cross-account-with-roles.html).

The IAM service supports only one type of resource-based policy called **role trust policy**, which is attached to an
IAM role. _An IAM role is both an identity and a resource that supports resource-based policies_. For that reason, we
must attach both a trust policy and an identity-based policy to an IAM role. Trust policies define which principal 
entities (accounts, users, roles, and federated users) can assume the role. To learn how IAM roles are different from 
other resource-based policies, see
[How IAM roles differ from resource-based policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_compare-resource-policies.html).

To see which other services support resource-based policies, see
[AWS services that work with IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_aws-services-that-work-with-iam.html). To learn more about resource-based policies, see [Identity-based policies and resource-based policies](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_identity-vs-resource.html). To learn whether principals in accounts outside of your zone of trust (trusted organization or account) have access to assume your roles, see
[What is IAM Access Analyzer?](https://docs.aws.amazon.com/IAM/latest/UserGuide/what-is-access-analyzer.html).
