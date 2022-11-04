---
layout: post
title: Infrastructure as Code (IaC)
tags: [IaC, CHEF]
category: FINALIZED
color: rgb(218, 25, 132)
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

Chef also works as a tool for implementing better DevOps. DevOps is a set of practices that combines software
development (Dev) and IT operations (Ops). DevOps is a fundamental aspect of collaboration between engineering and IT 
operations to deploy better code, faster, in an **automated** manner. DevOps helps to improve an organization's velocity 
to deliver apps and services. It's all about alignment -- alignment of engineering and IT ops via improved collaboration 
and communication.

![Error loading devops-lifecycle.png]({{ "/assets/img/devops-lifecycle.png" | relative_url}})

Open Source Software at Chef are

* [**Chef Infra**](https://community.chef.io/tools/chef-infra/), a powerful automation platform that transforms 
  infrastructure configuration into code. Whether we're operating in the cloud, on-premises, or in a hybrid environment, 
  Chef Infra _automates how infrastructure is configured, deployed, and managed across our network_.

  At the heart of this tool is the [Chef Infra Client](#chef-infra-client), which typically runs as an agent on the 
  systems managed by Chef. The client runs Chef libraries called [Cookbooks](#cookbooks), which declare the desired
  state of our system using infrastructure-as-code. The client then ensures our system is inline with the declared
  policy.
* [**Chef InSpec**](https://community.chef.io/tools/chef-inspec), which provides a language for describing security and 
  compliance rules that can be shared between software engineers, operators, and security engineers.

  > **Compliance Automation**
  >
  > Compliance automation refers to automatically ensuring our infrastructure complies with security standards set by 
  > authorities such as the [Center for Internet Security](https://www.cisecurity.org/) (CIS). Compliance automation
  > helps ensure our infrastructure is protected from malicious intrusions and other security issues.
  > 
  > ![Error loading chef-automateui.png]({{ "/assets/img/chef-automateui.png" | relative_url}})
  > 
  > The Chef Compliance solution can automatically scan our infrastructure to identify and report security compliance 
  > issues. Then we can use the Chef Infra to remediate such security issues.
  > 
  > Chef Compliance uses the Chef InSpec language to create and run compliance profiles, which contain the logic to scan 
  > for security issues.
  > 
  > Here is an example of the Chef InSpec language that tests a node for security compliance. In this example, InSpec is 
  > testing the node to ensure the ssh_config protocol should be 2. If the actual value from the node is not protocol 2,
  > a critical issue is reported and can be displayed in the Chef Automate UI shown above
  > 
  > ![Error loading chef-inspec.png]({{ "/assets/img/chef-inspec.png" | relative_url}})

  InSpec is a language used to declare security requirements, or tests, called "controls" that are packaged into groups 
  called [profiles](#profiles). These profiles can be used to describe the requirements for all the environments that
  need to be audited on a regular basis, such as production systems running business-critical applications.

* **Chef Habitat**, an open source automation solution for defining, packaging, and delivering applications to almost any environment regardless of operating system or platform.

  > **Application Automation**
  >
  > Application Automation refers to defining, packaging and delivering applications to almost any environment
  > regardless of operating system or deployment platform. At Chef, the Chef Habitat solution enables DevOps and 
  > application teams to:
  > 
  > * Build continuous delivery pipelines across all applications and all change events
  > * Create artifacts that can be deployed on-demand to bare-metal, VMs, or containers without any rewriting or
  >   refactoring
  > * Scale the adoption of agile delivery practices across development and operations

  With Chef Habitat, an application that is built and run in development will be exactly the same as what's deployed in 
  production environments. This is accomplished by declaring the build and runtime instructions for the application in a 
  Habitat Plan file. The application is then built in a cleanroom environment that bundles the application alongside its 
  deployment instructions into a single deployable Habitat artifact file (.HART). This artifact is then deployed by the 
  Habitat Supervisor, which monitors the application lifecycle, including deploying runtime configuration updates
  without having the rebuild the application.

  Habitat allows for application automation to live alongside the app's source code. This reduces misunderstandings 
  between developers and operators about how an app is built or deployed, since these groups are using the same 
  source-of-truth to define how an app works.

* **Chef Workstation** bundles together all the common software needed when building automation instructions for tools like Chef Infra and Chef InSpec. It also includes common debugging and testing tools for all our automation code. Chef Workstation includes:

  - _The Chef Workstation App_
  - _Chef Infra Client_
  - _Chef InSpec_
  - _Chef Command Line Tool_, which allows you to apply dynamic, repeatable configurations to your servers directly over
    SSH or WinRM via chef-run. This provides a quick way to apply config changes to the systems we manage whether or not 
    they're being actively managed by Chef Infra, without requiring any pre-installed software.
  - _Test Kitchen_, which can test cookbooks across any combination of platforms and test suites before we deploy those 
    cookbooks to actual infrastructure nodes
  - _Cookstyle_, which is a code linting tool that helps us write better Chef Infra cookbooks by detecting and automatically correcting style, syntax, and logic mistakes in our code
  - Plus various Test Kitchen and Knife plugins

* **Chef Automate**, an enterprise visibility and metrics tool that provides actionable insights for any systems that
  we manage using Chef tools and products. The dashboard and analytics tool enables cross-team collaboration with 
  actionable insights for configuration and compliance, such as a history of changes to environments to make audits
  simple and reliable. Chef Automate can be used with a number of Chef Software products and solutions, and segregates 
  information into separate dashboards for quick access and filtering.

  The goal of Chef Automate is to make infrastructure management, application delivery and continuous compliance
  realities by enabling cross-team collaboration using a single source-of-truth. All Chef OSS tools like Infra, InSpec
  and Habitat can be configured to report into Chef Automate to provide a window into the status and health of every 
  application and system in your organization.

  ![Error loading chef-automatenodes.png]({{ "/assets/img/chef-automatenodes.png" | relative_url}})

### Ruby Essentials

Ruby is a simple programming language. Chef uses Ruby as its reference language to define the patterns that are found in 
resources, recipes, and cookbooks. Chef also uses these patterns to configure, deploy, and manage nodes across the
network.

#### Instally Ruby

##### Mac OS

Ruby comes pre-installed on macOS. However, pre-installed Ruby might be a few versions behind. The latest version can be 
installed using a package manager like Homebrew, making it easy to install Ruby. Just run the following command.

```bash
brew install ruby
```

##### Linux

Linux and Ubuntu use the apt package manager for installation. Run the following command in the terminal to install
Ruby.

```bash
sudo apt-get install ruby-full
```

#### Ruby Syntax

##### Control Flow

Ruby's `if` statement takes an expression and executes the code based on the evaluation of that expression. If the 
expression evaluates to `true`, Ruby executes the block of code following the `if` statement. If the expression
evaluates to `false`, then it doesn't execute the code. For example,

```ruby
x = 10
if x > 7
    puts "x is greater than 7"
end
```

The `else` statement is the partner of the `if` statement. If the expression evaluates to `true`, then the statement 
following the condition is executed. If the expression evaluates to `false`, then the statement following the else 
statement is executed.

```ruby
x = 10

if x < 7
 puts "x is less than 7"
else
 puts "x is greater than 7"
end
```

The `if…else` structure keeps us restricted to two options. What if we want to have more options in our program? Here, 
`elsif` comes to the rescue and allows us to add alternatives to the traditional `if…else`.

```ruby
x = 10

if x < 7
 puts "x is less than 7"
elsif x > 7
 puts "x is greater than 7"
else
 puts "x is equal to 7"
end
```

### Chef Infra

#### Chef Infra Client

##### Cookbooks

### Chef InSpec

#### Profiles