---
layout: post
title: 3 Most Common Authorization Designs for SaaS Products
tags: [Security, ACL, RBAC, ABAC, SaaS]
color: rgb(255, 185, 1)
feature-img: "assets/img/post-cover/8-cover.png"
thumbnail: "assets/img/post-cover/8-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

As with most things when it comes to software engineering, there are many ways to design and implement an authorization
system. Authorization systems add a level of security and validation to your application, allowing you to restrict
access to resources to make sure that only the users who are meant to see certain things can.

<!--more-->

* TOC
{:toc}

Clearly, authorization is a vital piece of core functionality in most systems, and it deserves due consideration when
the system is being designed. While nothing is stopping you from inventing your own design, you can save a lot of time
and effort by building upon the work already done by others.

There are a number of different authorization designs commonly used for SaaS products. This article looks at three in
closer detail. Each has its own strengths and weaknesses, so there's a time and place for all of them.

Which Pattern Should You Use?
=============================

It's important to note that none of these authorization models are set in stone. Any examples covered here represent
just one possible way to implement that kind of authorization.

Access Control List
===================

Perhaps the simplest of the authorization models, Access Control List (ACL) works by maintaining what is essentially a
list of users and keeping track of whether or not they are allowed to perform a specific action. ACLs are an effective
form of authorization for simple situations systems that do not require much granularity in controlling who can access
what.

The following diagram is an example of what part of your database schema might look like if you were to implement basic
ACL authorization:

![Error loading acl-db-relation.png]({{ "/assets/img/acl-db-relation.png" | relative_url}})

Note the labels Users and Permissions. A user can have zero or more permissions. With this design, permissions are
relatively arbitrary, but so is the connection between the permissions and the users.

ACL has some compelling benefits. First and foremost, it's simple. It would be reasonably quick and cheap to implement
and maintain in many systems. Systems with simple requirements and smaller scopes will benefit from adopting this design
if they don't need features offered by some of the more complex designs.

ACL is not without its drawbacks, however. The most obvious is the aforementioned simplicity—ironically also its
greatest strength. The simplicity of ACLs would likely become an issue as you grow and scale. Because the permissions
are directly related to specific users, onboarding new users can be a bit of effort, as you would need to manually
assign each permission as needed. The same is true if new permissions need to be added or removed from all existing
users. It can become quite a burdensome task.

If your system grows beyond the capabilities of simple ACLs, you will likely need to upgrade to a more complex design
pattern like RBAC.

Role-Based Access Control
=========================

Role-Based Access Control (RBAC) is more complex than ACL, but it's also more powerful, flexible, and better suited to
certain scenarios. With RBAC, you can assign roles to your users and then make decisions for what they should be allowed
to access based on these roles. For example, you might have a Finance role, which grants access to financial parts of
your system, and you might have a Support role that grants access to help desk-related things.

The specifics of how roles work is down to the particular implementation. It's possible to build things in a way where a
single user can have multiple roles, or you can restrict users to a single role if that's more appropriate for your use
case.

The following diagram is an example of what part of your database schema might look like if you were to implement RBAC:

![Error loading RBAC-db-relation.png]({{ "/assets/img/RBAC-db-relation.png" | relative_url}})

Here, there are Users, Roles, and Permissions, and pivot tables between each of them. With this design, a user could be
associated with zero or more roles, and each of those roles could subsequently be assigned zero or more permissions.
These permissions would likely be fairly generic in scope, like view_reporting or edit_users, rather than being granular
to the point of having permissions for individual resources (which would likely be more in the domain of ABAC, as seen
below).

RBAC has several benefits owing to its relatively simple implementation. While it's more complex than simple ACLs, it's
far less complicated than ABAC and some other advanced authorization designs. This means that it's easier to implement
and cheaper to support in the long run.

This simplicity also applies to RBAC's day-to-day use. Because all permissions are essentially bound to roles, you don't
need to remember complex rules in order to grant new users access to things. Generally, you can just set a user up with
the appropriate roles, and the rest should fall into place. This is in contrast to ACLs, where the rules themselves
would likely need to be modified, and ABAC, where the user would need to be configured with an assortment of attributes
to ensure correct access to necessarrelation-extendedy resources.

One of the biggest potential risks with RBAC is the possibility of a “role explosion.” This occurs when a need arises
for increasingly granular control, and the only viable solution is to create more and more roles. If your system allows
users to have multiple roles simultaneously, these new roles might only have a few permissions each. If not, it could
result in a lot of duplicate roles with slight variations.

Neither of these is an ideal scenario, and it highlights one of the big limitations of RBAC: it isn't the best at doing
granular permissions.

Attribute-Based Access Control
==============================

Of the authorization designs being discussed, Attribute-Based Access Control (ABAC) is the most complex and the most
powerful of the bunch. ABAC means that your system can make decisions by taking contextual information into account.
This information can be derived from the user attempting access, the roles or groups they belong to, the resources
they're trying to access, environmental data like time of day or geo-location, or just about any other data you have.

The following is a rough potential database schema that could be used to implement ABAC:

![Error loading ABAC-db-relation.png]({{ "/assets/img/ABAC-db-relation.png" | relative_url}})

ABAC can be very subjective. Because the authorization evaluations are based on attributes of the system, there is no
one-size-fits-all schema. In the above example, you can see there are Users with Roles, Regions, and Clients. There are
also clients with regions and data.

In this scenario, say you want to grant a user access to a client's data, but only if the following conditions are met:

The user has the role of administrator. OR
The user has the role of customer support.
The user belongs to the same region as the client who owns the data.
The data is not flagged as sensitive.
These conditions use attributes that belong to objects involved in the transaction rather than discrete access records
or permissions. This is the defining characteristic of ABAC. Although it often takes more effort to implement well, it's
very powerful and often necessary for large organizations with complex business logic.

The benefits of ABAC are abundant. By creating access policies based on assignable attributes, organizations have very
granular and flexible control over who is allowed to access what. It also scales well, as policies do not need to be
modified as new users come on board. Rather, the new users can simply be assigned the appropriate attributes. Access to
the resources they need to do their job should follow.

As with everything, there are some limitations as well.

When evaluating ABAC as your potential authorization design, the main thing to keep in mind is cost—not strictly
financial cost, but time, implementation effort, complexity, and ongoing maintenance.

ABAC can be a complicated design, and as such, it can be a lot more effort to implement than the more straightforward
designs mentioned above if you build everything yourself. Services that provides plug-and-play access control 
functionality that makes implementing ABAC a lot easier.

If you have complex business requirements that make ABAC look like it's the way to go, consider whether or not you need
to build it all yourself. An off-the-shelf solution can probably respond well to your use case.

Wrapping Up
===========

Here, you've seen three different models for implementing authorization in a SaaS application. Each of the three models
has its own strengths and weaknesses.

* ACLs are straightforward and a good fit for small systems but don't scale too well and can introduce a lot of overhead
when managing permissions.
* RBAC alleviates the problems of ACL by associating the permissions with roles rather than users. It's a good choice
  for small to medium-sized systems that have (or are likely to) outgrow the limitations of ACLs, but it falls short
  when it comes to more granular permissions.
* ABAC goes a step beyond RBAC by allowing you to configure access with vastly increased granularity, at the cost of
  implementation and maintenance effort, as well as overall complexity.
