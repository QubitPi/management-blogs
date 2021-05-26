---
layout: post
title: Install Airflow
tags: [Apache Airflow]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/27-cover.png"
thumbnail: "assets/img/post-cover/27-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Installation

### Mac

The prerequisite is Python 3 and pip. Follow this instruction to install them:
https://docs.python-guide.org/starting/install3/osx/

    # airflow needs a home, ~/airflow is the default,
    # but you can lay foundation somewhere else if you prefer
    # (optional)
    export AIRFLOW_HOME=~/airflow
    
    # install from pypi using pip
    pip install apache-airflow
    pip install 'apache-airflow[all]'
    
    # initialize the database
    airflow initdb
    
    # start the web server, default port is 8080
    airflow webserver -p 8080
    
    # start the scheduler
    airflow scheduler
    
    # visit localhost:8080 in the browser and enable the example dag in the home page
