---
layout: post
title: Setting Up Coverage Analysis through SonarCloud in Maven Project
tags: [CI, Maven, Software Release, SonarCloud]
color: rgb(243, 112, 42)
feature-img: "assets/img/post-cover/35-cover.png"
thumbnail: "assets/img/post-cover/35-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


SonarCloud supports the reporting of test coverage as part of the analysis of your Java project.

However, SonarCloud does not produce the coverage report itself. Instead, we must set up a third-party tool to produce
the report as part of your build process. You then need to configure your analysis to tell the SonarScanner where the 
report is located so that it can pick it up and send it to SonarCloud, where it will be displayed on your project
dashboard along with the other analysis metrics.

For Java projects, SonarCloud directly supports the JaCoCo coverage tool.

Add Coverage in a Single-module Maven Project
----------------------------------------------

To add coverage to your Maven project you need to use the
[jacoco-maven-plugin](https://mvnrepository.com/artifact/org.jacoco/jacoco-maven-plugin)

In the most basic case, we will need to execute two goals:

1. `jacoco:prepare-agent`, which allows coverage info to be collected during unit tests execution, and
2. `jacoco:report`, which uses data collected during unit test execution to generate a report.

By default, the tool generates XML, HTML, and CSV versions of the report. SonarCloud explicitly specify XML for its
need.

We begin by adding the following snippet to the project's `${project.basedir}/pom.xml`:

```xml
<project>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                </plugin>
                <plugin>
                    <groupId>org.sonarsource.scanner.maven</groupId>
                    <artifactId>sonar-maven-plugin</artifactId>
                    <version>3.7.0.1746</version>
                </plugin>
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>0.8.6</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

    <profiles>
        <profile>
            <id>coverage</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.jacoco</groupId>
                        <artifactId>jacoco-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>prepare-agent</id>
                                <goals>
                                    <goal>prepare-agent</goal>
                                </goals>
                            </execution>
                            <execution>
                                <id>report</id>
                                <goals>
                                    <goal>report</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
```

Usually we would want to create a specific profile, like the `coverage` profile in the example above, which executes
unit tests with the JaCoCo agent and creates a coverage report. This profile would then only be activated if coverage 
information is requested (e.g., in the CI pipeline).

By default the generated report will be saved under `target/site/jacoco/jacoco.xml`. This location will be checked 
automatically by the scanner.

Analyze Repository With GitHub Actions
--------------------------------------

To configure analysis of your project using GitHub Actions you should follow the in-product (i.e. your SonarCloud 
dashboard) tutorial. Under the _Configure_ tab of your project homepage in SonarCloud, simply click on **With GitHub 
Actions**. You can also access the tutorials by going to **Administration > Analysis Method**.

The tutorial will walk you through the precise steps to set up the analysis.

> It is very important that in order for SonarCloud to effectively collect test coverage stats, we must have "Build and 
> analyze" command, found in the tutorial, run as
> 
> ```bash
> mvn -B clean -Pcoverage verify sonar:sonar \
> org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
> -Dsonar.java.binaries=target/classes \
> -Dsonar.projectKey=QubitPi_circe
> ```

### Troubleshooting

To investigate issues with the import of test coverage information you can run Maven with the debug flag, `-X`:

    mvn -X clean verify sonar:sonar ...

In the logs, you will find the execution of different sensors for each module of the project. Typically you will have a 
log similar to the following one when XML report is processed.

    [INFO] 16:58:05.074 Sensor JaCoCo XML Report Importer [jacoco]
    [DEBUG] 16:58:05.082 Reading report 'C:\projects\sonar-scanning-examples\maven-multimodule\tests\target\site\jacoco-aggregate\jacoco.xml'
    [INFO] 16:58:05.093 Sensor JaCoCo XML Report Importer [jacoco] (done) | time=19ms

> In addition, coverage stats won't show up in PR and will only be available on master branch and SonarCloud project
> dashboard after the PR is merged.
