---
layout: post
title: Git - Working with Remotes
tags: [Git]
category: FINALIZED
color: rgb(246, 77, 39)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Working with Remotes
--------------------

To be able to collaborate on any Git project, we need to know how to manage our remote repositories. **Remote
repositories** are versions of our project that are hosted on the Internet or network somewhere. We can have several of 
them, each of which generally is either read-only or read/write for us. **Collaborating with others involves managing 
these remote repositories and pushing and pulling data to and from them when we need to share work**. Managing remote 
repositories includes knowing how to

* [add remote repositories](#adding-remote-repositories)
* [remove remotes](#renaming-and-removing-remotes) that are no longer valid
* manage various remote branches and define them as being tracked or not, and more.

* In this section, we’ll cover some of these remote-management skills.

### Showing Project Remotes

To see which remote servers we have configured, we can run the `git remote` command. It lists the shortnames of each
remote handle we've specified. If we've cloned our repository, we should at least see `origin`, which is the default 
name Git gives to the server we cloned from:

{% highlight bash %}
$ git clone https://github.com/schacon/ticgit
Cloning into 'ticgit'...
remote: Reusing existing pack: 1857, done.
remote: Total 1857 (delta 0), reused 0 (delta 0)
Receiving objects: 100% (1857/1857), 374.35 KiB | 268.00 KiB/s, done.
Resolving deltas: 100% (772/772), done.
Checking connectivity... done.
$ cd ticgit
$ git remote
origin
{% endhighlight %}

We can also specify `-v`, which shows us the URLs that Git has stored for the shortname to be used when reading and 
writing to that remote:

{% highlight bash %}
$ git remote -v
origin	https://github.com/schacon/ticgit (fetch)
origin	https://github.com/schacon/ticgit (push)
{% endhighlight %}

If we have more than one remote, the command lists them all. For example, a repository with multiple remotes for working 
with several collaborators might look something like this.

{% highlight bash %}
$ cd grit
$ git remote -v
bakkdoor  https://github.com/bakkdoor/grit (fetch)
bakkdoor  https://github.com/bakkdoor/grit (push)
cho45     https://github.com/cho45/grit (fetch)
cho45     https://github.com/cho45/grit (push)
defunkt   https://github.com/defunkt/grit (fetch)
defunkt   https://github.com/defunkt/grit (push)
koke      git://github.com/koke/grit.git (fetch)
koke      git://github.com/koke/grit.git (push)
origin    git@github.com:mojombo/grit.git (fetch)
origin    git@github.com:mojombo/grit.git (push)
{% endhighlight %}

This means we can pull contributions from any of these users pretty easily. We may additionally have permission to push
to one or more of these

### Adding Remote Repositories

We've mentioned and given some demonstrations of how the `git clone` command implicitly adds the `origin` remote for us. 
Here's how to add a new remote explicitly. To add a new remote Git repository as a shortname we can reference easily,
run `git remote add <shortname> <url>`:

{% highlight bash %}
$ git remote
origin
$ git remote add pb https://github.com/paulboone/ticgit
$ git remote -v
origin	https://github.com/schacon/ticgit (fetch)
origin	https://github.com/schacon/ticgit (push)
pb	https://github.com/paulboone/ticgit (fetch)
pb	https://github.com/paulboone/ticgit (push)
{% endhighlight %}

Now we can use the string `pb` on the command line in lieu of the whole URL. For example, if we want to fetch all the 
information that Paul has but that we don’t yet have in our repository, we can run `git fetch pb`:

{% highlight bash %}
$ git fetch pb
remote: Counting objects: 43, done.
remote: Compressing objects: 100% (36/36), done.
remote: Total 43 (delta 10), reused 31 (delta 5)
Unpacking objects: 100% (43/43), done.
From https://github.com/paulboone/ticgit
* [new branch]      master     -> pb/master
* [new branch]      ticgit     -> pb/ticgit
{% endhighlight %}

Paul's master branch is now accessible locally as `pb/master`. We can merge it into one of our branches, or we can check 
out a local branch at that point if we want to inspect it.

### Fetching and Pulling from Remotes

As we just saw, to get data from our remote projects, we can run:

{% highlight bash %}
$ git fetch <remote>
{% endhighlight %}

The command goes out to that remote project and pulls down all the data from that remote project that we don't have yet. 
After we do this, we should have references to all the branches from that remote, which we can merge in or inspect at
any time.

If we clone a repository, the command automatically adds that remote repository under the name "origin". So,
`git fetch origin` fetches any new work that has been pushed to that server since we cloned (or last fetched from) it. 


> It's important to note that the `git fetch` command only downloads the data to our local repository. It doesn't 
> automatically merge it with any of our work or modify what we're currently working on. We have to merge it manually 
> into our work when we're ready.

If our current branch is set up to track a remote branch, we can use the `git pull` command to automatically fetch and 
then merge that remote branch into our current branch. This may be an easier or more comfortable workflow for us; and
by default, the `git clone` command automatically sets up our local master branch to track the remote master branch (or 
whatever the default branch is called) on the server we cloned from. Running `git pull` generally fetches data from the 
server we originally cloned from and automatically tries to merge it into the code we're currently working on.

### Pushing to Remotes

When we have our project at a point that we would like to share, we have to push it upstream. The command for this is 
simple: `git push <remote> <branch>`. If we want to push our `master` branch to our `origin` server (again, cloning 
generally sets up both of those names for us automatically), then we can run this to push any commits we've done back up 
to the server:

{% highlight bash %}
$ git push origin master
{% endhighlight %}

This command works only if we cloned from a server to which we have write access and if nobody has pushed in the
meantime. If someone else cloned at the same time and they pushed upstream before we push upstream, our push will
rightly be rejected. We'll have to fetch their work first and incorporate it into ours before we'll be allowed to push.

### Inspecting a Remote

If we want to see more information about a particular remote, we can use the `git remote show <remote>` command. If we
run this command with a particular shortname, such as `origin`, we get something like this:

{% highlight bash %}
$ git remote show origin
* remote origin
  Fetch URL: https://github.com/schacon/ticgit
  Push  URL: https://github.com/schacon/ticgit
  HEAD branch: master
  Remote branches:
    master                               tracked
    dev-branch                           tracked
  Local branch configured for 'git pull':
    master merges with remote master
  Local ref configured for 'git push':
    master pushes to master (up to date)
{% endhighlight %}

It lists the URL for the remote repository as well as the tracking branch information. The command helpfully tells us
that if we're on the `master` branch and we run `git pull`, it will automatically merge the remote's `master` branch
into the local one after it has been fetched. It also lists all the remote references it has pulled down.

The example above is simple. When we're using Git more heavily, however, we may see much more information from
`git remote show`:

{% highlight bash %}
$ git remote show origin
* remote origin
  URL: https://github.com/my-org/complex-project
  Fetch URL: https://github.com/my-org/complex-project
  Push  URL: https://github.com/my-org/complex-project
  HEAD branch: master
  Remote branches:
    master                           tracked
    dev-branch                       tracked
    markdown-strip                   tracked
    issue-43                         new (next fetch will store in remotes/origin)
    issue-45                         new (next fetch will store in remotes/origin)
    refs/remotes/origin/issue-11     stale (use 'git remote prune' to remove)
  Local branches configured for 'git pull':
    dev-branch merges with remote dev-branch
    master     merges with remote master
  Local refs configured for 'git push':
    dev-branch                     pushes to dev-branch                     (up to date)
    markdown-strip                 pushes to markdown-strip                 (up to date)
    master                         pushes to master                         (up to date)
{% endhighlight %}

This command shows which branch is automatically pushed to when we run `git push` while on certain branches. It also
shows us which remote branches on the server we don't yet have, which remote branches we have that have been removed
from the server, and multiple local branches that are able to merge automatically with their remote-tracking branch when 
we run `git pull`.

### Renaming and Removing Remotes

We can run `git remote rename` to change a remote's shortname. For instance, if we would like to rename `pb` to `paul`, 
we can do so with

{% highlight bash %}
$ git remote rename pb paul
$ git remote
origin
paul
{% endhighlight %}

It's worth mentioning that this changes all our remote-tracking branch names, too. What used to be referenced at
`pb/master` is now at `paul/master`.

If we want to remove a remote for some reason - we've moved the server or are no longer using a particular mirror, or 
perhaps a contributor isn't contributing anymore - we can either use `git remote remove` or `git remote rm`:

{% highlight bash %}
$ git remote remove paul
$ git remote
origin
{% endhighlight %}

Once we delete the reference to a remote this way, all remote-tracking branches and configuration settings associated
with that remote are also deleted.
