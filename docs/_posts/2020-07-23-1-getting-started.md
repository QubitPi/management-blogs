---
layout: post
title: Your First Jersey Application
tags: [Application, Resources, Sub-Resources]
color: green
author: QubitPi
excerpt_separator: <!--more-->
---

This is a quick introduction on how to get started building RESTful services using Jersey. The example described here
uses the lightweight [Grizzly](https://javaee.github.io/grizzly/) HTTP server.

<!--more-->

## Creating a New Project from Maven Archetype

Jersey project is built using Apache Maven software project build and management tool. All modules produced as part of
Jersey project build are pushed to the Central Maven Repository. Therefore it is very convenient to work with Jersey for
any Maven-based project as all the released (non-SNAPSHOT) Jersey dependencies are readily available without a need to
configure a special maven repository to consume the Jersey modules. 

Since starting from a Maven project is the most convenient way for working with Jersey, let's now have a look at this
approach. We will now create a new Jersey project that runs on top of a [Grizzly](https://javaee.github.io/grizzly/)
container. We will use a Jersey-provided maven archetype. To create the project, execute the following Maven command in
the directory where the new project should reside: 

    mvn archetype:generate -DarchetypeGroupId=org.glassfish.jersey.archetypes -DarchetypeArtifactId=jersey-quickstart-grizzly2 -DarchetypeVersion=2.31

The `groupId`, `package`, and `artifactId` of this project are set to `com.github.QubitPi.jersey`, `simple-service` ,
and `com.github.QubitPi.jersey`, respectively.

## Exploring the Newly Created Project

Once the project generation from a Jersey maven archetype is successfully finished, you should see the new
`simple-service` project directory created in your current location. The directory contains a standard Maven project
structure: 

* Project build and management configuration is described in the `pom.xml` located in the project root directory.
* Project sources are located under `src/main/java`.
* Project test sources are located under `src/test/java`.

There are 2 classes in the project source directory in the `com.github.QubitPi.jersey` package. The
[`Main`](./src/main/java/com/github/QubitPi/jersey/Main.java) class is responsible for bootstrapping the Grizzly
container as well as configuring and deploying the project's JAX-RS application to the container. Another class in the
same package is [`MyResource`](./src/main/java/com/github/QubitPi/jersey/MyResource.java) class, that contains
implementation of a simple JAX-RS resource. The simplest form of it looks like this: 

```java
/*
 * Copyright 刘珈奇
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.QubitPi.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests.
     * <p>
     * The returned object will be sent to the client as "text/plain" media type.
     *
     * @return string that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
}
```

A JAX-RS resource is an annotated POJO that provides so-called _resource methods_ that are able to handle HTTP requests
for URI paths that the resource is bound to. See
[JAX-RS Application, Resources and Sub-Resources](#jax-rs-application-resources-and-sub-resources) for a complete guide
to JAX-RS resources. In our case, the resource exposes a single resource method that is able to handle HTTP `GET`
requests, is bound to `/myresource` URI path and can produce responses with response message content represented in
`"text/plain"` media type. In this version, the resource returns the same `"Got it!"` response to all client requests. 

The last piece of code that has been generated in this skeleton project is a `MyResourceTest` unit test class that is
located in the same `com.github.QubitPi.jersey` package as the `MyResource` class, however, this unit test class is
placed into the maven project test source directory `src/test/java`:

```java
/*
 * Copyright 刘珈奇
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.QubitPi.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        // start the server
        server = Main.startServer();
        // create the client
        final Client client = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // client.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = client.target(Main.BASE_URI);
    }

    @After
    public void tearDown() {
        server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        final String responseMsg = target.path("myresource").request().get(String.class);
        assertEquals("Got it!", responseMsg);
    }
}
```

In this unit test, a [Grizzly](https://javaee.github.io/grizzly/) container is first started and server application is
deployed in the test `setUp()` method by a static call to `Main.startServer()`. Next, JAX-RS client components are
created in the same test set-up method. First a new JAX-RS Client instance `client` is built and then a JAX-RS web
target component pointing to the context root of our application deployed at `http://localhost:8080/myapp/` (a value of
`Main.BASE_URI` constant) is stored into a target field of the unit test class. This field is then used in the actual
unit test method (`testGetIt()`). 

In the `testGetIt()` method a fluent JAX-RS Client API is used to connect to and send a HTTP GET request to the
`MyResource` JAX-RS resource class listening on `/myresource` URI. As part of the same fluent JAX-RS API method
invocation chain, a response is read as a Java `String` type. On the second line in the test method, the response
content string returned from the server is compared with the expected phrase in the test assertion. To learn more about
using JAX-RS Client API, please see the [Client API](#client-api) below. 

## Running the Project

Now that we have seen the content of the project, let's try to test-run it. To do this, we need to invoke following
command on the command line:

    mvn clean test
    
This will compile the project and run the project unit tests. We should see a similar output that informs about a
successful build once the build is finished: 

    Results :
    
    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
    
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 34.527s
    [INFO] Finished at: Sun May 26 19:26:24 CEST 2013
    [INFO] Final Memory: 17M/490M
    [INFO] ------------------------------------------------------------------------
    
Now that we have verified that the project compiles and that the unit test passes, we can execute the application in a
standalone mode. To do this, run the following maven command: 

    mvn exec:java 

The application starts and you should soon see the following notification in your console:

    May 26, 2013 8:08:45 PM org.glassfish.grizzly.http.server.NetworkListener start
    INFO: Started listener bound to [localhost:8080]
    May 26, 2013 8:08:45 PM org.glassfish.grizzly.http.server.HttpServer start
    INFO: [HttpServer] Started.
    Jersey app started with WADL available at http://localhost:8080/myapp/application.wadl
    Hit enter to stop it...
    
This informs you that the application has been started and it's WADL descriptor is available at
`http://localhost:8080/myapp/application.wadl` URL. You can retrieve the WADL content by executing a
`curl http://localhost:8080/myapp/application.wadl` command in your console or by typing the WADL URL into your favorite
browser. You should get back an XML document describing your deployed RESTful application in a WADL format. To learn
more about working with WADL, check out [WADL Support](#wadl-support) section. 

The last thing we should try is to see if we can communicate with our resource deployed at `/myresource` path. We can
again either type the resource URL in the browser or we can use curl: 

    $ curl http://localhost:8080/myapp/myresource
    Got it!
    
As we can see, the `curl` command returned with the `Got it!` message that was sent by our resource. We can also ask
`curl` to provide more information about the response, for example we can let it display all response headers by using
the `-i` switch: 

    curl -i http://localhost:8080/myapp/myresource
    HTTP/1.1 200 OK
    Content-Type: text/plain
    Date: Sun, 26 May 2013 18:27:19 GMT
    Content-Length: 7
    
    Got it!
    
Here we see the whole content of the response message that our Jersey/JAX-RS application returned, including all the
HTTP headers. Notice the `Content-Type: text/plain` header that was derived from the value of `@Produces` annotation
attached to the `MyResource` class. 

In case you want to see even more details about the communication between our `curl` client and our resource running on
Jersey in a [Grizzly](https://javaee.github.io/grizzly/) I/O container, feel free to try other various options and
switches that `curl` provides. For example, this last command will make `curl` output a lot of additional information
about the whole communication: 

    $ curl -v http://localhost:8080/myapp/myresource
    * About to connect() to localhost port 8080 (#0)
    *   Trying ::1...
    * Connection refused
    *   Trying 127.0.0.1...
    * connected
    * Connected to localhost (127.0.0.1) port 8080 (#0)
    > GET /myapp/myresource HTTP/1.1
    > User-Agent: curl/7.25.0 (x86_64-apple-darwin11.3.0) libcurl/7.25.0 OpenSSL/1.0.1e zlib/1.2.7 libidn/1.22
    > Host: localhost:8080
    > Accept: */*
    >
    < HTTP/1.1 200 OK
    < Content-Type: text/plain
    < Date: Sun, 26 May 2013 18:29:18 GMT
    < Content-Length: 7
    <
    * Connection #0 to host localhost left intact
    Got it!* Closing connection #0