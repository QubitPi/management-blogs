---
layout: post
title: Jenkins Reference Guide
tags: [Jenkins, CI/CD]
color: rgb(22, 139, 185)
feature-img: "assets/img/post-cover/20-cover.png"
thumbnail: "assets/img/post-cover/20-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Jenkins Concept

https://www.jenkins.io/doc/book/pipeline/#scripted-pipeline-fundamentals

## How to Configure a Jenkins Pipeline

### Let's Define 2 Jenkins Jobs

Suppose we need 2 jobs, first of which runs some "gatekeeper" tests and second of which deploys the software artifact
to a dev/prod environment.

#### Converting Conditional Build Steps to Jenkins Pipeline

Jenkins Pipeline enables users to implement their pipeline as code. Pipeline code can be written directly in the Jenkins
Web UI or in any text editor. It is a full-featured programming language, which gives users access to much broader set
of conditional statements without the restrictions of UI-based programming.

So, taking our case as an example, the Pipeline looks like the following:

```
pipeline {
    agent any
    parameters {
        choice(
            choices: ['dev' , 'prod'],
            description: 'The environment artifact will be deployed into',
            name: 'DEPLOYMENT_ENVIRONMENT')
    }

    stages {
        stage ('Gatekeeper Tests') {
            steps {
                build job: 'external-gatekeeper-jenkins-job',
                parameters: [
                    [
                        $class: 'StringParameterValue',
                        name: 'num_test_threads',
                        value: "${params.num_test_threads}"
                    ],
                    [
                        $class: 'BooleanParameterValue',
                        name: 'dry_run',
                        value: "${params.dry_run}"
                    ]
                ]
                echo "Gatekeeper tests finished!"
            }
        }
        stage ('Dev Deployment') {
            when {
                expression { params.DEPLOYMENT_ENVIRONMENT == 'dev' }
            }
            steps {
                build job: 'dev-deployment-jenkins-job',
                parameters: ...
            }
        }
        stage ('Prod Deployment') {
            when {
                expression { params.DEPLOYMENT_ENVIRONMENT == 'dev' }
            }
                build job: 'prod-deployment-jenkins-job',
                parameters: ...
            }
        }
    }
}
```

The parameters such as `num_test_threads` as defined through "This project is parameterized" config panel:

![Error loading jenkins-parameter-config.png]({{ "/assets/img/jenkins-parameter-config.png" | relative_url}})

> 📝 Note that `num_test_threads` will
> be added to both this pipeline and the `external-gatekeeper-jenkins-job` in the same way presente above
> 
> All available parameter types (`$class`) can be found at https://javadoc.jenkins.io/hudson/model/ParameterValue.html

If you have configured your pipeline (such as `dev-deployment-jenkins-job`) to accept parameters when it is built -
**Build with Parameters** - they are accessible as Groovy variables inside params. They are also accessible as
environment variables. For example: Using `isFoo` parameter defined as a boolean parameter (checkbox in the UI):

```
node {
sh "isFoo is ${params.isFoo}"
sh 'isFoo is ' + params.isFoo
if (params.isFoo) {
    // do something
}
```

## Troubleshooting

### Echo Off in Jenkins Console Output

By default, Jenkins launches Execute Shell script with `set -x`. This causes all commands to be echoed. We can type
`set +x` before any command to temporary override that behavior. Of course you will need `set -x` to start showing them
again.

You can override this behaviour for the whole script by putting the following at the top of the build step:

```bash
#!/bin/bash +x
```
