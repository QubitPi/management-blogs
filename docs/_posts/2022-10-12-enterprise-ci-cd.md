---
layout: post
title: Building Enterprise CI/CD
tags: [CD/CD]
category: FINALIZED
color: rgb(43, 164, 78)
feature-img: "assets/img/post-cover/31-cover.png"
thumbnail: "assets/img/post-cover/31-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Integrate Jenkins with GitHub through Webhooks
----------------------------------------------

This approach  for most enterprise GitHub repos (i.e. private repos).

* CI/CD logics doesn't shown 

### Configuring GitHub

**Step 1**: go to your GitHub repository and click on "**Settings**".

![Error loading jengit1.png]({{ "/assets/img/jengit1.png" | relative_url}})

**Step 2**: Click on Webhooks and then click on "**Add webhook**".

![Error loading jengit2.png]({{ "/assets/img/jengit2.png" | relative_url}})

**Step 3**: In the "**Payload URL**" field, paste your Jenkins environment URL. At the end of this URL add **/github-webhook/**. In the "Content type" select: "application/json" and leave the "Secret" field empty.

![Error loading jengit3.png]({{ "/assets/img/jengit3.png" | relative_url}})

**Step 4**: In the page "Which events would you like to trigger this webhook?" choose "_Let me select individual
events._" Then, check "Pull Requests" and "Pushes". At the end of this option, make sure that the "Active" option is 
checked and click on "**Add webhook**".

![Error loading jengit4.png]({{ "/assets/img/jengit4.png" | relative_url}})
![Error loading jengit5.png]({{ "/assets/img/jengit5.png" | relative_url}})

We're done with the configuration on GitHub’s side! Now let's move on to Jenkins.

### Configuring Jenkins

**Step 5**: In Jenkins, click on "**New Item**" to create a new project.

![Error loading jengit6.png]({{ "/assets/img/jengit6.png" | relative_url}})

**Step 6**: Give your project a name, then choose "**Freestyle project**" and finally, click on "OK".

![Error loading jengit7.png]({{ "/assets/img/jengit7.png" | relative_url}})

**Step 7**: Click on the "**Source Code Management**" tab.

![Error loading jengit8.png]({{ "/assets/img/jengit8.png" | relative_url}})

**Step 8**: Click on Git and paste your GitHub repository URL in the "**Repository URL**" field.

![Error loading jengit9.png]({{ "/assets/img/jengit9.png" | relative_url}})

**Step 9**: Click on the "**Build Triggers**" tab and then on the "_GitHub hook trigger for GITScm polling_". Or,
choose the trigger of your choice.

![Error loading jengit10.png]({{ "/assets/img/jengit10.png" | relative_url}})

That's it! Our GitHub repository has integrated with our Jenkins project. With this Jenkins GitHub integration, we can
now use any file found in the GitHub repository and trigger the Jenkins job to run with every code commit.
