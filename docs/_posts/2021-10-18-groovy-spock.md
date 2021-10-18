---
layout: post
title: Groovy Spock
tags: [Groovy, Groovy Spock, Software Testing, Unit Test, Integration Test, Functional Test, JVM, Java, Testing]
color: rgb(237, 127, 34)
feature-img: "assets/img/post-cover/8-cover.png"
thumbnail: "assets/img/post-cover/8-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

## Partial Mocks for Java 8 Interfaces with Default Methods

When Spock 1.1 was released, it included a series of new features and improvements. One of them was the ability to
**create partial mocks for Java 8 interfaces that contain default methods**.

Let's see how it works:

```java
public interface ISquare {
    double getLength();

    default double getArea() {
        return getLength() * getLength();
    }
}
```

```groovy
import spock.lang.Specification

class PartialMockingInterfacesWithDefaultMethods extends Specification {
    
    def "ISquare with stubbed getLength()"() {
        given:
        ISquare square = Spy() {
            2 * getLength() >> 3
        }
        
        when:
        def area = square.area
        
        then:
        area == 9
    }
}
```

By **creating a spy** for the `ISquare` interface and mocking the `getLength()` method, we are able to test the default
implementation of the `getArea()` method.

Nothing surprising here. It's probably exactly what you expected. But **it didn't work with Spock version 1.0 or
earlier**. A version that works is `2.0-M1-groovy-2.5` combined with, if needed, `3.0.7` `groovy-all`:

```xml
    <dependencies>
        <!-- Testing -->
        <dependency>
            <groupId>org.spockframework</groupId>
            <artifactId>spock-core</artifactId>
            <version>2.0-M1-groovy-2.5</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-all</artifactId>
            <version>3.0.7</version>
        </dependency>
        <dependency> <!-- Enable mocking of non-interface types -->
            <groupId>cglib</groupId>
            <artifactId>cglib-nodep</artifactId>
            <version>3.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.objenesis</groupId>
            <artifactId>objenesis</artifactId>
            <version>3.0.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Groovy Spock -->
            <plugin>
                <groupId>org.codehaus.gmavenplus</groupId>
                <artifactId>gmavenplus-plugin</artifactId>
                <version>1.6</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>addSources</goal>
                            <goal>addTestSources</goal>
                            <goal>generateStubs</goal>
                            <goal>compile</goal>
                            <goal>generateTestStubs</goal>
                            <goal>compileTests</goal>
                            <goal>removeStubs</goal>
                            <goal>removeTestStubs</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Groovy checkstyle -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>codenarc-maven-plugin</artifactId>
                <version>0.22-1</version>
                <configuration>
                    <sourceDirectory>${project.basedir}</sourceDirectory>
                    <maxPriority1Violations>0</maxPriority1Violations>
                    <maxPriority2Violations>0</maxPriority2Violations>
                    <maxPriority3Violations>0</maxPriority3Violations>
                </configuration>
                <executions>
                    <execution>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>codenarc</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Unite Test -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.16</version>
                <configuration>
                    <systemPropertyVariables>
                        <java.awt.headless>true</java.awt.headless>
                    </systemPropertyVariables>
                    <includes>
                        <include>%regex[.*Spec.*]</include>
                    </includes>
                    <excludes>
                        <exclude>%regex[.*ITSpec.*]</exclude>
                    </excludes>
                </configuration>
            </plugin>

            <!-- Integration Test -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>3.0.0-M4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <includes>
                        <include>**/*ITSpec.*</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>

```