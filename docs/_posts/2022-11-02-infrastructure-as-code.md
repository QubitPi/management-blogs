---
layout: post
title: Infrastructure as Code (IaC)
tags: [IaC, CHEF]
category: FINALIZED
color: rgb(255, 163, 0)
feature-img: "assets/img/post-cover/33-cover.png"
thumbnail: "assets/img/post-cover/33-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Infrastructure as code (IaC) is the process of managing and provisioning computer data centers through machine-readable 
definition files, rather than physical hardware configuration or interactive configuration tools. The IT infrastructure 
managed by this process comprises both physical equipment, such as bare-metal servers, as well as virtual machines, and 
configuration resources. The definitions may be in a version control system. The code in the definition files may use 
either scripts or declarative definitions, rather than maintaining the code through manual processes, but IaC more often 
employs declarative approaches.

<!--more-->

* TOC
{:toc}


The Need for Automation
-----------------------

Automation is the act of building a process that will operate without human intervention. It means automating the 
configuration and management of cloud-based or on-premises computing infrastructure.

### What is Infrastructure?

When we speak of infrastructure, we are referring to the **physical and/or virtual machines that run businesses**. For
example, a major retailer may require a large number of web servers, load balancers, and database servers to run their 
retail websites.

These machines may be located in an on-premises data center, or very often, as virtual machines in "the cloud" such as 
Amazon Web Services (AWS), Microsoft Azure, or Google Cloud Platform (GCP).

![Error loading chef-infrstrucure2.png]({{ "/assets/img/chef-infrstrucure2.png" | relative_url}})

**One thing all these virtual or real machines require is server management. Management such as installing and updating 
software, initial configuration, applying security measures, and periodic server content changes. Such management can be 
labor-intensive and tedious without automation.**

### What is Automation?

As mentioned above, automation is the act of building a process that will operate without human intervention. But what
does this mean in reality?

It's about creating a system that will take care of repetitive tasks, with consistency and reliability.  It is not about 
replacing human operators, but instead freeing their time to work on more complex problems that require intelligent 
insight, rather than simple rules.

In addition, the use of automation, and the promise of consistency and reliability helps provide trust in systems, which
in turn allows for greater innovation across the company.

### Infrastructure Automation

Infrastructure Automation refers to ensuring every system is configured correctly and consistently in any cloud, VM,
and/or physical infrastructure, in an automated fashion.


Chef
----

Chef was founded in 2008. Its first product was called _Chef_ (now called _Chef Infra_) which is a set of tools that
automate the configuration of our cloud-based or on-prem server infrastructure.

Chef can automate how we build, deploy, and manage our infrastructure. For example, say a large retailer needs to deploy 
and configure 50 servers for an upcoming sale. They could use Chef Infra to automate that infrastructure deployment.

> In October 2020, Chef Software was acquired by Progress Software and Chef Software operates as a business unit of 
> Progress Software. Chef Software has been and still is a leader in DevOps and DevSecOps.

Chef Software users consist of hundreds of small businesses to large enterprises:

![Error loading chef-customers2.png]({{ "/assets/img/chef-customers2.png" | relative_url}})

With Chef, [infrastructure automation](#infrastructure-automation) typically begins with using the Chef Infra and its
tools.

In the example below, the user is uploading a set of Chef cookbooks to the Chef Infra server. We can think of Chef
cookbooks as a set of configuration files, called **recipes**, that will instantiate, configure, and maintain our
infrastructure nodes in an automated fashion. (A node is a physical or virtual machine.)

![Error loading chefinfra.png]({{ "/assets/img/chefinfra.png" | relative_url}})

The Chef Infra server in turn loads those cookbooks to the correct nodes. Chef Infra can do this on a massive scale
thus eliminating the tedious task of manually configuring your infrastructure nodes individually.

The [Chef Infra](https://github.com/chef/chef) is an open source technology that uses Ruby to develop basic building
blocks like Chef recipes and cookbooks that configure and maintain our infrastructure. Chef Infra helps in **reducing
manual and repetitive tasks for infrastructure management**.

Here is an example of a Chef Infra recipe. This little bit of code, when applied to a node in our infrastructure, will:

* install an Apache web server package (httpd)
* create a file on that node called "/var/www/html/index.html"
* enable and start the Apache web server

```
package 'httpd'

template '/var/www/html/index.html' do
  source 'index.html.erb'
end

service 'httpd' do
  action [:enable, :start]
end
```

