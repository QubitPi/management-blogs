---
layout: post
title: The Next Big Thing in Software Testing - Auto Test Generation
tags: [Java, Test, Maven]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/13-cover.png"
thumbnail: "assets/img/post-cover/13-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

How can you improve software quality while also increasing speed? Here's one idea that will play an increasingly
important role in the future of software testing: test generation. What I mean is that in the future, software testing
will include auto-generated tests that allow code to reach the testing stage faster while also improving coverage.

<!--more-->

* TOC
{:toc}

## Advantages of Auto-generate Tests

The advantages of auto-generated tests over manually developing automated tests greatly outweigh the disadvantages:

* Eliminates the need to manually write automated tests, which is a huge time saver
* Generated tests are written in a more uniform way across the codebase
* All the business rules are centralized, not scattered across test scripts
* Able to auto-generate tests from user analytic data; target only pull request (PR) changes; choice of selecting
  between the various combinations of data inputs; choosing one or multiple paths; and more
* Random test data generation
* Easier test maintenance
* Reduction in costs associated with the development time of automated tests
* Test generation will increase testing thoroughness and improve your test coverage
* Increases software testing efficiencies
* Improves the quality of life for the person responsible for authoring automated test manually; no longer a repetitive
  task
* Developers and testers can spend time on more important things 

## Test Generation Solutions already Exist

If this all sounds too futuristic to you, bear in mind that tools for generating tests already exist, even if they are
not as widely used as manual test-writing processes. Below are some good choices:

* [Diffblue](https://www.diffblue.com/)
* [EvoSuite](https://www.evosuite.org/)

### Diffblue v.s. EvoSuite

EvoSuite

* generates more stable tests that execute successfully in the first shot
* generates less test code

Diffblue

* reversed

### Diffblue

#### Install Diffblue

Diffblue has been integrated into IntelliJ and can be installed using plugin marketplace:

![Error loading diffblue-plugin.png!]({{ "/assets/img/diffblue-plugin.png" | relative_url}})

Note that there are "Community" version and "Paid" version. The difference is the that the former supports by-class file
test generations while the latter supports per-package and per-project(multiple packages) test generations

Using Diffblue is super simple by [installing maven plugin](https://www.diffblue.com/try-cover) and generating tests by
right-clicking though:

![Error loading diffblue-example.png!]({{ "/assets/img/diffblue-example.png" | relative_url}})

### EvoSuite

#### Maven integration

##### Setup the Test Project

We will be testing a simple library consisting of a couple of (dummy) container classes, including the Stack
implementation from part 1. Please download and extract the archive containing this example:

```bash
wget http://evosuite.org/files/tutorial/Tutorial_Maven.zip
unzip Tutorial_Maven.zip
cd Tutorial_Maven
```

> Note that this example project currently has one dependency, which is JUnit to execute the tests. This is declared in
> the `<dependency>` section of the project POM file:

> ```xml
> <project>
>     ....
>     <dependencies>
>         <dependency>
>             <groupId>junit</groupId>
>             <artifactId>junit</artifactId>
>             <version>4.12</version>
>             <scope>test</scope>
>         </dependency>
>     </dependencies>
> </project>
> ```

##### Generating EvoSuite Tests with Maven

Let's tell Maven that we would like to use EvoSuite as part of our build, so we need to declare a build dependency on
EvoSuite. Add the following snippet to the `pom.xml`:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.evosuite.plugins</groupId>
                <artifactId>evosuite-maven-plugin</artifactId>
                <version>1.0.6</version>
            </plugin>
        </plugins>
    </build>

    ...

    <pluginRepositories>
        <pluginRepository>
            <id>EvoSuite</id>
            <name>EvoSuite Repository</name>
            <url>http://www.evosuite.org/m2</url>
        </pluginRepository>
    </pluginRepositories>
```

Let's waste no more time and generate some tests! Type:

```bash
mvn evosuite:generate
```

If EvoSuite is set up correctly, you should see something similar to the following output:

```
[INFO] --- evosuite-maven-plugin:1.0.6:generate (default-cli) @ Tutorial_Maven ---
[INFO] Going to generate tests with EvoSuite
[INFO] Total memory: 800mb
[INFO] Time per class: 2 minutes
[INFO] Number of used cores: 1
[INFO] Target: /Users/gordon/Documents/Papers/evosuite-tutorial/CommandLine/Tutorial_Maven/target/classes
[INFO] Basedir: /Users/gordon/Documents/Papers/evosuite-tutorial/CommandLine/Tutorial_Maven
[INFO] Started spawn process manager on port 64917
[INFO] * EvoSuite 1.0.6
[INFO] Registered remote process from /127.0.0.1:64918
[INFO] Going to execute 4 jobs
[INFO] Estimated completion time: 8 minutes, by 2016-04-04T11:09:06.277
[INFO] Going to start job for: tutorial.LinkedList. Expected to end in 190 seconds, by 2016-04-04T11:04:16.336
```

The output tells us that EvoSuite will generate tests for 4 classes, which it estimates to take around 8 minutes. The
first job it started is the class `tutorial.LinkedList`. Now is the time to wait and get some coffee, until Evosuite has
finished testing all the classes.

#### Integrating generated tests into the source tree

We've got a test suite for each of our classes, but where are these test suites?

Right now, they are in a hidden directory `.evosuite`. This is a directory where EvoSuite keeps information, in order to
improve test generation over time. For example, if a class hasn't changed and we already have a test suite, we don't
want to invoke EvoSuite on it again. You will find the generated tests in `.evosuite/best-tests`:

```
ls .evosuite/best-tests/tutorial
```

Assuming you are happy with these test suites, we can integrate them into the source tree (`src/test/java`):

```
mvn evosuite:export
```

#### Executing EvoSuite tests with Maven

Add the following dependency

```xml
<dependency>
    <groupId>org.evosuite</groupId>
    <artifactId>evosuite-standalone-runtime</artifactId>
    <version>1.0.6</version>
    <scope>test</scope>
</dependency>
```

and execute

```
mvn test
```

If you set up everything correctly, you should see output similar to this one:

```
-------------------------------------------------------
T E S T S
-------------------------------------------------------
Running tutorial.LinkedList_ESTest
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.192 sec
Running tutorial.LinkedListIterator_ESTest
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.356 sec
Running tutorial.Node_ESTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.024 sec
Running tutorial.Stack_ESTest
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.051 sec
Running tutorial.StackTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec

Results :

Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
```

#### Troubleshooting

##### ERROR TestSuiteGenerator - Full stack: java.lang.IllegalArgumentException: null

This is probably because you are using an old version of EvoSuite, such as 1.0.6. EvoSuite has evolved and solved this
issue in new release, which, however, has not yet been release to Maven Central at the time of this writing. To get
the latest version:

```
git clone https://github.com/EvoSuite/evosuite.git
cd evosuite
mvn install -DskipTests=true
```

Next, change the version in POM from:

```xml
<evosuiteversion>1.0.6</evosuiteversion>
``` 

```xml
<evosuiteversion>1.1.1-SNAPSHOT</evosuiteversion>
```

Then re-execute the `mvn clean compile evosuite:generate` command.
