---
layout: post
title: Programmatic API for Building Resources
tags: [API, Resources, Programmatic]
color: rbg(136, 176, 75)
feature-img: "assets/img/post-cover/14-cover.png"
thumbnail: "assets/img/post-cover/14-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

A standard approach of developing JAX-RS application is to implement resource classes annotated with `@Path` with
resource methods annotated with HTTP method annotations like `@GET` or `@POST` and implement needed functionality. This
approach is described in the
[JAX-RS Application, Resources and Sub-Resources](https://qubitpi.github.io/jersey-guide/2020/07/25/3-jax-rs-application-resources-and-sub-resources.html). Besides this
standard JAX-RS approach, Jersey offers an API for constructing resources programmatically.

Imagine a situation where a deployed JAX-RS application consists of many resource classes. These resources implement
standard HTTP methods like `@GET`, `@POST`, `@DELETE`. In some situations it would be useful to add an `@OPTIONS`
method which would return some kind of meta data about the deployed resource. Ideally, you would want to expose the
functionality as an additional feature and you want to decide at the deploy time whether you want to add your custom
`OPTIONS` method. However, when custom `OPTIONS` method are not enabled you would like to be `OPTIONS` requests handled
in the standard way by JAX-RS runtime. To achieve this you would need to modify the code to add or remove custom
`OPTIONS` methods before deployment. Another way would be to use programmatic API to build resource according to your
needs.

Another use case of programmatic resource builder API is when you build any generic RESTful interface which depends on
lot of configuration parameters or for example database structure. Your resource classes would need to have different
methods, different structure for every new application deploy. You could use more solutions including approaches where
your resource classes would be built using Java byte code manipulation. However, this is exactly the case when you can
solve the problem cleanly with the programmatic resource builder API. Let's have a closer look at how the API can be
utilized. 

## Programmatic Hello World example

Jersey Programmatic API was designed to fully support JAX-RS resource model. In other words, every resource that can be
designed using standard JAX-RS approach via annotated resource classes can be also modelled using Jersey programmatic
API. Let's try to build simple hello world resource using standard approach first and then using the Jersey programmatic
resource builder API.

The following example shows standard JAX-RS "Hello world!" resource class. 

```java
@Path("helloworld")
public class HelloWorldResource {
 
    @GET
    @Produces("text/plain")
    public String getHello() {
        return "Hello World!";
    }
}
```

This is just a simple resource class with one GET method returning "Hello World!" string that will be serialized as
text/plain media type. 

Now we will design this simple resource using programmatic API.

```java
package org.glassfish.jersey.examples.helloworld;
 
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

public static class MyResourceConfig extends ResourceConfig {
 
    public MyResourceConfig() {
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path("helloworld");
 
        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(MediaType.TEXT_PLAIN_TYPE)
                .handledBy(new Inflector<ContainerRequestContext, String>() {
 
            @Override
            public String apply(ContainerRequestContext containerRequestContext) {
                return "Hello World!";
            }
        });
 
        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }
}
```

First, focus on the content of the `MyResourceConfig` constructor in the example. The Jersey programmatic resource model
is constructed from `Resource`s that contain `ResourceMethod`s. In the example, a single resource would be constructed
from a `Resource` containing one `GET` resource method that returns "Hello World!". The main entry point for building
programmatic resources in Jersey is the `Resource.Builder` class. `Resource.Builder` contains just a few methods like
the `path` method used in the example, which sets the name of the path. Another useful method is a
`addMethod(String path)` which adds a new method to the resource builder and returns a resource method builder for the
new method. Resource method builder contains methods which set consumed and produced media type, define name bindings,
timeout for asynchronous executions, etc. It is always necessary to define a resource method handler (i.e. the code of
the resource method that will return "Hello World!"). There are more options how a resource method can be handled. In
the example the implementation of `Inflector` is used. The Jersey `Inflector` interface defines a simple contract for
transformation of a request into a response. An `inflector` can either return a `Response` or directly an entity object,
the way it is shown in the example. Another option is to setup a Java method handler using
`handledBy(Class<?> handlerClass, Method method)` and pass it the chosen `java.lang.reflect.Method` instance together
with the enclosing class. 

A resource method model construction can be explicitly completed by invoking `build()` on the resource method builder.
It is however not necessary to do so as the new resource method model will be built automatically once the parent
resource is built. Once a resource model is built, it is registered into the resource config at the last line of the
`MyResourceConfig` constructor in the example. 

### Deployment of programmatic resources

Let's now look at how a programmatic resources are deployed. ***The easiest way to setup your application as well as
register any programmatic resources in Jersey is to use a Jersey `ResourceConfig` utility class, an extension of
`Application` class***. If you deploy your Jersey application into a Servlet container you will need to provide an
`Application` subclass that will be used for configuration. You may use a `web.xml` where you would define an
`org.glassfish.jersey.servlet.ServletContainer` Servlet entry there and specify initial parameter
`javax.ws.rs.Application` with the class name of your JAX-RS Application that you wish to deploy. In the example above,
this application will be `MyResourceConfig` class. This is the reason why `MyResourceConfig` extends the
`ResourceConfig` (which, if you remember extends the `javax.ws.rs.Application`). 

The following example shows a fragment of `web.xml` that can be used to deploy the `ResourceConfig` JAX-RS application.

```xml
...

<servlet>
    <servlet-name>org.glassfish.jersey.examples.helloworld.MyApplication</servlet-name>
    <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
    <init-param>
        <param-name>javax.ws.rs.Application</param-name>
        <param-value>org.glassfish.jersey.examples.helloworld.MyResourceConfig</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>org.glassfish.jersey.examples.helloworld.MyApplication</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>

...
```

If you use another deployment options and you have accessible instance of `ResourceConfig` which you use for
configuration, you can register programmatic resources directly by `registerResources()` method called on the
`ResourceConfig`. Please note that the method `registerResources()` replaces all the previously registered resources.

Because Jersey programmatic API is not a standard JAX-RS feature the `ResourceConfig` must be used to register
programmatic resources as shown above. See [deployment chapter](TODO) for more information. 

## Additional examples

```java
final Resource.Builder resourceBuilder = Resource.builder(HelloWorldResource.class);

resourceBuilder.addMethod("OPTIONS")
    .handledBy(new Inflector<ContainerRequestContext, Response>() {
        @Override
        public Response apply(ContainerRequestContext containerRequestContext) {
            return Response.ok("This is a response to an OPTIONS method.").build();
        }
    });

final Resource resource = resourceBuilder.build();
```

In the example above the `Resource` is built from a `HelloWorldResource` resource class. The resource model built this
way contains a `GET` method producing `'text/plain'` responses with "Hello World!" entity. This is quite important as it
allows you to whatever `Resource` objects based on introspecting existing JAX-RS resources and use builder API to
enhance such these standard resources. In the example we used already implemented `HelloWorldResource` resource class
and enhanced it by `OPTIONS` method. The `OPTIONS` method is handled by an `Inflector` which returns `Response`.

The following sample shows how to define sub-resource methods (methods that contains sub-path). 

```java
final Resource.Builder resourceBuilder = Resource.builder("helloworld");
 
final Resource.Builder childResource = resourceBuilder.addChildResource("subresource");
childResource.addMethod("GET").handledBy(new GetInflector());
 
final Resource resource = resourceBuilder.build();
```

Sub-resource methods are defined using so called ***child resource models***. Child resource models (or child
resources) are programmatic resources build in the same way as any other programmatic resource but they are registered
as a child resource of a parent resource. The child resource in the example is build directly from the parent builder
using method `addChildResource(String path)`. However, there is also an option to build a child resource model
separately as a standard resource and then add it as a child resource to a selected parent resource. This means that
child resource objects can be reused to define child resources in different parent resources (you just build a single
child resource and then register it in multiple parent resources). Each child resource groups methods with the same
sub-resource path. Note that a child resource cannot have any child resources as *there is nothing like sub-sub-resource
method concept in JAX-RS*. For example if a sub resource method contains more path segments in its path (e.g.
"/root/sub/resource/method" where "root" is a path of the resource and "sub/resource/method" is the sub resource method
path) then parent resource will have path "root" and child resource will have path "sub/resource/method" (so, there will
not be any separate nested sub-resources for "sub", "resource" and "method"). 

## Model processors

Jersey gives you an option to register so called ***model processor providers***. These providers are able to modify or
enhance the application resource model during application deployment. This is an advanced feature and will not be needed
in most use cases. However, imagine you would like to add `OPTIONS` resource method to each deployed resource as it is
described at the top of this chapter. You would want to do it for every programmatic resource that is registered as well
as for all standard JAX-RS resources. 

To do that, you first need to register a model processor provider in your application, which implements
`org.glassfish.jersey.server.model.ModelProcessor` extension contract. An example of a model processor implementation is
shown here: 

```java
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
 
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
 
@Provider
public static class MyOptionsModelProcessor implements ModelProcessor {
 
    @Override
    public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
        // we get the resource model and we want to enhance each resource by OPTIONS method
        ResourceModel.Builder newResourceModelBuilder = new ResourceModel.Builder(false);
        for (final Resource resource : resourceModel.getResources()) {
            // for each resource in the resource model we create a new builder which is based on the old resource
            final Resource.Builder resourceBuilder = Resource.builder(resource);
 
            // we add a new OPTIONS method to each resource
            // note that we should check whether the method does not yet exist to avoid failures
            resourceBuilder.addMethod("OPTIONS")
                .handledBy(new Inflector<ContainerRequestContext, String>() {
                    @Override
                    public String apply(ContainerRequestContext containerRequestContext) {
                        return "On this path the resource with " + resource.getResourceMethods().size()
                            + " methods is deployed.";
                    }
                    }).produces(MediaType.TEXT_PLAIN)
                      .extended(true); // extended means: not part of core RESTful API
 
            // we add to the model new resource which is a combination of the old resource enhanced
            // by the OPTIONS method
            newResourceModelBuilder.addResource(resourceBuilder.build());
        }
 
        final ResourceModel newResourceModel = newResourceModelBuilder.build();
        // and we return new model
        return newResourceModel;
    };
 
    @Override
    public ResourceModel processSubResource(ResourceModel subResourceModel, Configuration configuration) {
        // we just return the original subResourceModel which means we do not want to do any modification
        return subResourceModel;
    }
}
```

The `MyOptionsModelProcessor` from the example is a relatively simple model processor which iterates over all registered
resources and for all of them builds a new resource that is equal to the old resource except it is enhanced with a new
`OPTIONS` method.

Note that you only need to register such a ModelProcessor as your custom extension provider in the same way as you would
register any standard JAX-RS extension provider. During an application deployment, Jersey will look for any registered
model processor and execute them. As you can seem, model processors are very powerful as they can do whatever
manipulation with the resource model they like. A model processor can even, for example, completely ignore the old
resource model and return a new custom resource model with a single "Hello world!" resource, which would result in only
the "Hello world!" resource being deployed in your application. Of course, it would not not make much sense to implement
such model processor, but the scenario demonstrates how powerful the model processor concept is. A better, real-life use
case scenario would, for example, be to always add some custom new resource to each application that might return
additional metadata about the deployed application. Or, you might want to filter out particular resources or resource
methods, which is another situation where a model processor could be used successfully. 

Also note that `processSubResource(ResourceModel subResourceModel, Configuration configuration)` method is executed for
each sub resource returned from the sub resource locator. The example is simplified and does not do any manipulation but
probably in such a case you would want to enhance all sub resources with a new `OPTIONS` method handlers too.

It is important to remember that any model processor must always return valid resource model. As you might have already
noticed, in the example above this important rule is not followed. If any of the resources in the original resource
model would already have an `OPTIONS` method handler defined, adding another handler would cause the application fail
during the deployment in the resource model validation phase. In order to retain the consistency of the final model, a
model processor implementation would have to be more robust than what is shown in the example. 
