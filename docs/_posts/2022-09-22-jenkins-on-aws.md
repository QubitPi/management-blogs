---
layout: post
title: Deploying Jenkins to AWS
tags: [CI/CD]
category: FINALIZED
color: rgb(237, 114, 17)
feature-img: "assets/img/post-cover/26-cover.png"
thumbnail: "assets/img/post-cover/26-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Jenkins is an open-source automation server that integrates with a number of AWS Services, including: AWS CodeCommit,
AWS CodeDeploy, Amazon EC2 Spot, and Amazon EC2 Fleet. We can use Amazon Elastic Compute Cloud (Amazon EC2) to deploy a 
Jenkins application on AWS.

<!--more-->

This post documents the process of deploying a Jenkins application. We will launch an EC2 instance, install Jenkins on 
that instance, and configure Jenkins to automatically spin up Jenkins agents if build abilities need to be augmented on
the instance.

* TOC
{:toc}


Prerequisites
-------------

1. [Register an AWS account](https://portal.aws.amazon.com/billing/signup#/start), if not having one yet.
2. [An Amazon EC2 key pair](#creating-a-key-pair), if we don't have one yet


Creating a Key Pair
-------------------

Creating a key pair helps ensure that the correct form of authentication is used when we install Jenkins.

To create our key pair:

1. Open the [Amazon EC2 console](https://console.aws.amazon.com/ec2/) and sign in.
2. In the navigation pane, under **NETWORK & SECURITY**, select **Key Pairs**.
3. Select **Create key pair**.
4. For **Name**, enter a descriptive name for the key pair. Amazon EC2 associates the public key with the name that we 
   specify as the **key name**. A key name can include up to 255 ASCII characters. It cannot include leading or trailing 
   spaces.
5. For **File format**, select the format in which to save the private key.
   - For OpenSSH compatibility (Linux or Mac OS X), select pem. 
   - For PuTTY compatibility (Windows), select ppk.
6. Select **Create key pair**.
7. The private key file downloads automatically. The base file name is the name we specified as the name of our key
   pair, and the file name extension is determined by the file format we chose. Save the private key file in a safe
   place.

   > ⚠️ This is the only chance for us to save the private key file.

8. If we use an SSH client on a macOS or Linux computer to connect to our Linux instance, we would also run the
   following command to set the permissions of our private key file so that only we can read it, otherwise we won't be
   able to connect to our instance using this key pair. For more information, please refer to
   [Error: Unprotected private key file](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/TroubleshootingInstancesConnecting.html#troubleshoot-unprotected-key).

   ```bash
   $ chmod 400 <key_pair_name>.pem
   ```


Creating a Security Group
-------------------------

A security group acts as a firewall that controls the traffic allowed to reach one or more EC2 instances. When we launch
an instance, we can assign it one or more security groups. We add rules that control the traffic allowed to reach the 
instances in each security group. We can modify a security group's rules any time, and the new rules take effect
_immediately_.

To create and configure our security group:

1. Decide who may access our instance. For example, a single computer or all trusted computers on a network. For a
   single computer/user, we can use the public IP address of the computer/user. To find the IP address, use the
   [check IP service tool](http://checkip.amazonaws.com/) from AWS3 (this tool works for VPN as well) or search for the 
   phrase "what is my IP address" in any search engine.
2. Sign in to the [AWS Management Console](https://console.aws.amazon.com/ec2/).
3. Open the Amazon EC2 console by selecting **EC2** under **Compute**.

   ![Error loading ec2-service.png]({{ "/assets/img/ec2-service.png" | relative_url}})

4. In the left-hand navigation bar, select **Security Groups**, and then select **Create Security Group**.

   ![Error loading create-security-group.png]({{ "/assets/img/create-security-group.png" | relative_url}})

5. In **Security group name**, enter **WebServerSG** or any preferred name of our choice, and provide a description.
6. Select our VPC from the list. We can use the default VPC.
7. On the **Inbound tab**, add the rules as follows:
   - Select **Add Rule**, and then select **SSH** from the Type list. 
   - Under Source, select **Custom**, and in the text box, enter the IP address from step 1. 
   - Select **Add Rule**, and then select **HTTP** from the Type list. 
   - Select **Add Rule**, and then select **Custom TCP Rule** from the Type list. 
   - Under **Port Range**, enter **8080** (Jenkins UI port).
8. Select Create.

For more information, refer to
[Security Groups](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-network-security.html) in the Amazon EC2 User 
Guide for Linux Instances.


Launching an Amazon EC2 Instance
--------------------------------

Now that we have configured a key pair and security group, we can launch an EC2 instance.

