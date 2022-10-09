---
layout: post
title: Designing Enterprise Authentication & Authorization Infrustructure
tags: [CD/CD]
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

identity-based policies
resource-based policies
permissions boundaries
Organizations SCPs
ACLs,
and session policies.

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