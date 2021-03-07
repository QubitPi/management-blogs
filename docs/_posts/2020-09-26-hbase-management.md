---
layout: post
title: HBase Management
tags: [HBase, Hadoop]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Run Modes

People can run HBase in two different modes - standalone and distributed. These modes represent the environment and the
filesystems that HBase is running against. In standalone mode, HBase runs against the local filesystem. In distributed
mode, HBase runs on top of
[Hadoop Distributed File System (HDFS)](http://en.wikipedia.org/wiki/HDFS#Hadoop_distributed_file_system).
 
To learn more about the two modes, see 
[HBase run modes: Standalone and Distributed](http://hbase.apache.org/book/standalone_dist.html).

## How Should an Organization On-Board HBase Projects

### Overview

There should be a dedicated team for maintaining Hadoop ecosystems and software/hardware provisioning. The team should
provide two different environments for using the HBase service. Users can onboard for the development/research
environment or onboard for the production environment. There should be separate on-boarding processes for the two
environments

### Development/Research On-Boarding

To on-board for the development/research environment, a project should file a "ticket" and provide the following
information:

* project information: design doc, any project-related links
* colo preference

The ticketing system should include:

* create-a-new-project portal
* Project Name
* Project Description
* Project links(type, URL)
* Contact Info
* all requests/ticktes submitted by a logged-in user

### Production On-Boarding

Production onboarding requires review and approval of the application usage (i.e., schema, access patterns, etc.) as
well as planning for capacity required to support the application. A ticket should include the following information:

* Table names and schema
* Design doc describing how the table will be used:
     - application-level schema 
     - row key schema
     - different Access patterns
* Throughput (and latency) requirements for the access patterns mentioned (for the next ~6 mos)
* Storage Requirements (for the next ~6 mos)
* Justification for resource requirements (i.e. customer usage estimates, workload simulation/performance runs)
* colo
* user/group access privileges

### Environments

To support the varying requirements during the course of a development cycle,  HBase provides a number of environments.
A service level is tied to a namespace that will be created on a given HBase instance.

#### Shared Sandbox

   - Development/Research tier
   - Namespace created in this level will be deployed on a set of region servers shared with other users
   - No resource isolation guarantees are provided
   - Loose guarantees on uptime and data storage
   - Namespace will have a quota on the number of tables (5) and number of total regions (200)
   - Users create and update their own table schemas with relatively little supervision

#### Development Environment

   - Development/Research tier
   - Unlike #1, uses a dedicated set of region servers
   - More generous table/region quotas
   - Capacity requirements need to be provided to HBase team
   - HBase team to review and approve usage and schema changes
   - Requires hardware-request approval

#### Perf Environment

   - Development/Research tier
   - A small testing environment 
   - Exclusive access to a set of region servers for a limited period of time
   - This is similar to #2 except that it is temporary and will be reclaimed after the performance test is complete
   - Lifetime of 1-3 months

#### Production

   - Production tier.
   - HBase capacity requirements needs to be provided
   - Dedicated servers and very high quotas
   - HBase team to review and approve usage and schema. 
   - Please engage with HBase team early to avoid any delays
   - Highest level of guarantees on uptime and data integrity
   - Requires hardware-request approval

## How Should an Organization's HBase Support Team Help with HBase Projects

### Online Chatting inside Org

This should be the quickest way to get assistance with minor questions. Chatting location should be available publicly

### Support E-mails

* It is recommended to have 2 emails: one for contacting HBase team and the other one for notifying everyone who uses
* HBase(under subscription)
* If you have a complicated question that is specific to your circumstances, ask the former list.
* If you have a general question about HBase, we recommend you ask the latter list first.
* HBase team monitors both lists.

### Announcement E-mails

Whenever there are upgrades or incidents that impact users, HBase team should will send announcements

### JIRA

* HBase team should have a ticketing system (e.g. JIRA) for users having problems with HBase environment, such as
* poor performance, unavailability, etc
* Users reporting the problem should provide all details, including:
    - what is not working for you
    - the affected colo, namespace, and table name
    - the approximate start time of the incident
    - links to failed job(s) and logs
    - links to any client-side ops-monitoring info
