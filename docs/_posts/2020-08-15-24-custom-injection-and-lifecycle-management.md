---
layout: post
title: Custom Injection and Lifecycle Management
tags: [HK2, Injection, Annotation, Lifecycle]
color: rgb(254, 132, 14)
author: QubitPi
excerpt_separator: <!--more-->
---

Since version 2.0, Jersey uses ***[HK2](https://javaee.github.io/hk2/)*** library for component life cycle management
and dependency injection. Rather than spending a lot of effort in maintaining Jersey specific API (as it used to be
before Jersey 2.0 version), Jersey defines several extension points where end-user application can directly manipulate
Jersey HK2 bindings using the HK2 public API to customize life cycle management and dependency injection of application
components. 

<!--more-->

* TOC
{:toc}

This guide can by no means supply an exhaustive documentation of HK2 API in its entire scope. This post only
points out the most common scenarios related to dependency injection in Jersey and suggests possible options to
implement these scenarios. It is highly recommended to check out the [HK2](https://javaee.github.io/hk2/) website and
read HK2 documentation in order to get better understanding of suggested approaches. HK2 documentation should also help
in resolving use cases that are not discussed in this writing.

There are typically three main use cases, where your application may consider dealing with HK2 APIs exposed in Jersey: 

1. Implementing a custom injection provider that allows an application to define additional types to be injectable into
   Jersey-managed JAX-RS components.
2. Defining a custom injection annotation (other than
   ***[@Inject](https://javaee.github.io/javaee-spec/javadocs/javax/inject/package-summary.html)*** or
   ***[@Context](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/Context.html)***) to mark
   application injection points.
3. Specifying a custom component life cycle management for your application components.

Relying on Servlet HTTP session concept is not very RESTful. It turns the originally state-less HTTP communication
schema into a state-full manner. However, it could serve as a good example that will help us demonstrate implementation
of the use cases described above. The following examples should work on top of Jersey Servlet integration module. The
approach that will be demonstrated could be further generalized. Below we will show how to make actual Servlet
[HttpSession](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpSession.html) injectable into JAX-RS
components and how to make this injection work with a custom inject annotation type. Finally, we will demonstrate how
you can write `HttpSession`-scoped JAX-RS resources. 

###  Implementing Custom Injection Provider

**Jersey implementation allows you to directly inject
*[HttpServletRequest](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpServletRequest.html)*
instance into your JAX-RS components**. It is quite straight forward to get the appropriate `HttpSession` instance out
of the injected request instance. Let say, you want to get `HttpSession` instance directly injected into your JAX-RS
types like in the code snippet below.

```java
@Path("di-resource")
public class MyDiResource {
 
    @Inject HttpSession httpSession;
 
    ...
 
}
```

To make the above injection work, you will need to define an additional HK2 binding in your application
[ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html).
Let's start with a custom ***HK2 [Factory](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/Factory.html)***
implementation that knows how to extract `HttpSession` out of given `HttpServletRequest`.

```java
import org.glassfish.hk2.api.Factory;
...
 
public class HttpSessionFactory implements Factory<HttpSession> {
 
    private final HttpServletRequest request;
 
    @Inject
    public HttpSessionFactory(HttpServletRequest request) {
        this.request = request;
    }
 
    @Override
    public HttpSession provide() {
       return request.getSession();
    }
 
    @Override
    public void dispose(HttpSession t) {
        ...
    }
}
``` 

Please note that the factory implementation itself relies on having the actual `HttpServletRequest` instance injected.
In your implementation, you can of course depend on other types (and inject them conveniently) as long as these other
types are bound to the actual HK2 service locator by Jersey or by your application. The key notion to remember here is
that your HK2 `Factory` implementation is responsible for implementing the `provide()` method that is used by HK2
runtime to retrieve the injected instance. Those of you who worked with [Guice](https://github.com/google/guice) binding
API in the past will most likely find this concept very familiar.

Once implemented, the factory can be used in a custom HK2 `Binder` to define the new injection binding for
`HttpSession`. Finally, the implemented binder can be registered in your
[ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html):

```java
import org.glassfish.hk2.utilities.binding.AbstractBinder;
...
 
public class MyApplication extends ResourceConfig {
 
    public MyApplication() {
 
        ...
 
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(HttpSessionFactory.class)
                        .to(HttpSession.class)
                        .proxy(true)
                        .proxyForSameScope(false)
                        .in(RequestScoped.class);
            }
        });
    }
}
``` 

Note that we did not define any explicit injection scope for the new injection binding. By default, HK2 factories are
bound in a HK2 [PerLookup](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/PerLookup.html) scope, which is
in most cases a good choice and it is suitable also in our example.

To summarize the approach described above, here is a list of steps to follow when implementing custom injection provider
in your Jersey application:

1. Implement your own HK2 `Factory` to provide the injectable instances.
2. Use the HK2 `Factory` to define an injection binding for the injected instance via custom HK2 `Binder`.
3. Register the custom HK2 `Binder` in your application `ResourceConfig`.

While the `Factory`-based approach is quite straight-forward and should help you to quickly prototype or even implement
final solutions, you should bear in mind, that your implementation does not need to be based on factories. You can for
instance bind your own types directly, while still taking advantage of HK2 provided dependency injection. Also, in your
implementation **you may want to pay more attention to defining or managing injection binding scopes for the sake of
performance or correctness of your custom injection extension**.

> ⚠️ While the individual injection binding implementations vary and depend on your use case, to enable your custom
> injection extension in Jersey, you must register your custom HK2
> [Binder](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/utilities/Binder.html) implementation in your
> application
> [ResourceConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ResourceConfig.html) 

## Defining Custom Injection Annotation

Java annotations are a convenient way for attaching metadata to various elements of Java code. Sometimes you may even
decide to combine the metadata with additional functionality, such as ability to automatically inject the instances
based on the annotation-provided metadata. The described scenario is one of the use cases where having means of
defining a custom injection annotation in your Jersey application may prove to be useful. Obviously, this use case
applies also to re-used existing, 3rd-party annotation types.

In the following example, we will describe how a custom injection annotation can be supported. Let's start with defining
a new custom `SessionInject` injection annotation that we will specifically use to inject instances of
[HttpSession](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpSession.html) (similarly to the
previous example):

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionInject {
    // intentionally left blank
}
```

The above `@SessionInject` annotation should be then used as follows:

```java
@Path("di-resource")
public class MyDiResource {
 
    @SessionInject
    HttpSession httpSession;
 
    ...
}
```

Again, the semantics remains the same as in the example described in the previous section. You want to have the actual
HTTP Servlet session instance injected into your `MyDiResource` instance. This time however, you expect that the
`httpSession` field to be injected must be annotated with a custom `@SessionInject` annotation. Obviously, in this
simplistic case the use of a custom injection annotation is an overkill, however, the simplicity of the use case will
help us to avoid use case specific distractions and allow us better focus on the important aspects of the job of
defining a custom injection annotation.

If you remember from the previous section, to make the injection in the code snippet above work, you first need to
implement the injection provider (HK2
[Factory](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/Factory.html)) as well as define the injection
binding for the `HttpSession` type. That part we have already done in the previous section. We will now focus on what
needs to be done to inform the HK2 runtime about our `@SessionInject` annotation type that we want to support as a new
injection point marker annotation. To do that, we need to implement our own ***HK2
[InjectionResolver](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/InjectionResolver.html)*** for the
annotation as demonstrated in the following listing:

```java
import javax.inject.Inject;
import javax.inject.Named;
 
import javax.servlet.http.HttpSession;
 
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
 
...
 
public class SessionInjectResolver implements InjectionResolver<SessionInject> {
 
    @Inject
    @Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
    InjectionResolver<Inject> systemInjectionResolver;
 
    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> handle) {
        if (HttpSession.class == injectee.getRequiredType()) {
            return systemInjectionResolver.resolve(injectee, handle);
        }
 
        return null;
    }
 
    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }
 
    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }
}
```

The `SessionInjectResolver` above just delegates to the default ***HK2 system injection resolver*** to do the actual
work.

You again need to register your injection resolver with your Jersey application, and you can do it the same was as in
the previous case. Following listing includes HK2 binder that registers both, the injection provider from the previous
step as well as the new HK2 inject resolver with Jersey application `ResourceConfig`. Note that in this case we're
explicitly binding the `SessionInjectResolver` to a
[@Singleton](https://javaee.github.io/javaee-spec/javadocs/javax/ejb/Singleton.html) scope to avoid the unnecessary
proliferation of `SessionInjectResolver` instances in the application:

```java
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
 
import javax.inject.Singleton;
 
...
 
public class MyApplication extends ResourceConfig {
 
    public MyApplication() {
 
        ...
 
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(HttpSessionFactory.class).to(HttpSession.class);
 
                bind(SessionInjectResolver.class)
                    .to(new TypeLiteral<InjectionResolver<SessionInject>>(){})
                    .in(Singleton.class);
            }
        });
    }
}
```

## Custom Life Cycle Management

The last use case discussed in this post will cover managing custom-scoped components within a Jersey application. ***If
not configured otherwise, then all JAX-RS resources are by default managed on a per-request basis***. A new instance of
given resource class will be created for each incoming request that should be handled by that resource class. Let say
you want to have your resource class managed in a per-session manner. It means a new instance of your resource class
should be created only when a new Servlet
[HttpSession](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/HttpSession.html) is established. (As
with previous examples in the post, **this example assumes the deployment of your application to a Servlet container**.)

Following is an example of such a resource class that builds on the support for `HttpSession` injection from the earlier
examples described in this post. The `PerSessionResource` class allows you to count the number of requests made within a
single client session and provides you a handy sub-resource method to obtain the number via a HTTP GET method call:

```java
@Path("session")
public class PerSessionResource {
 
    @SessionInject
    HttpSession httpSession;
 
    AtomicInteger counter = new AtomicInteger();
 
    @GET
    @Path("id")
    public String getSession() {
        counter.incrementAndGet();
        return httpSession.getId();
    }
 
    @GET
    @Path("count")
    public int getSessionRequestCount() {
        return counter.incrementAndGet();
    }
}
```

Should the above resource be per-request scoped (default option), you would never be able to obtain any other number but
1 from it's `getSessionRequestCount` sub-resource method, because then for each request a new instance of our
`PerSessionResource` class would get created with a fresh instance counter field set to 0. The value of this field would
get incremented to 1 in the the `getSessionRequestCount` method before this value is returned. In order to achieve what
we want, we have to find a way how to bind the instances of our `PerSessionResource` class to `HttpSession` instances
and then reuse those bound instances whenever new request bound to the same HTTP client session arrives. Let's see how
to achieve this.

To get better control over your Jersey component instantiation and life cycle, you need to implement a custom Jersey
***[ComponentProvider](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/spi/ComponentProvider.html)***
SPI, that would manage your custom components. Although it might seem quite complex to implement such a thing, the
component provider concept in Jersey is in fact very simple. It allows you to define your own HK2 injection bindings for
the types that you are interested in, while informing the Jersey runtime at the same time that it should back out and
leave the component management to your provider in such a case. By default, if there is no custom component provider
found for any given component type, Jersey runtime assumes the role of the default component provider and automatically
defines the default HK2 binding for the component type.

Following example shows a simple `ComponentProvider` implementation, for our use case. Some comments on the code follow.

```java
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
...
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.BindingBuilderFactory;
import org.glassfish.jersey.server.spi.ComponentProvider;import java.util.Objects;
 
@javax.ws.rs.ext.Provider
public class PerSessionComponentProvider implements ComponentProvider {
 
    private ServiceLocator locator;
 
    static class PerSessionFactory implements Factory<PerSessionResource> {

        static ConcurrentHashMap<String, PerSessionResource> PER_SESSION_MAP =
                new ConcurrentHashMap<String, PerSessionResource>();
 
 
        private final Provider<HttpServletRequest> requestProvider;
        private final ServiceLocator locator;
 
        @Inject
        public PerSessionFactory(Provider<HttpServletRequest> request, ServiceLocator locator) {
            this.requestProvider = Objects.requireNonNull(request, "request");
            this.locator = Objects.requireNonNull(locator, "locator");
        }
 
        @Override
        @PerLookup
        public PerSessionResource provide() {
            final HttpSession session = requestProvider.get().getSession();
 
            if (session.isNew()) {
                PerSessionResource newInstance = createNewPerSessionResource();
                PER_SESSION_MAP.put(session.getId(), newInstance);
 
                return newInstance;
            } else {
                return PER_SESSION_MAP.get(session.getId());
            }
        }
 
        @Override
        public void dispose(PerSessionResource r) {
            // intentionally left blank
        }
 
        private PerSessionResource createNewPerSessionResource() {
            final PerSessionResource perSessionResource = new PerSessionResource();
            locator.inject(perSessionResource);
            return perSessionResource;
        }
    }
 
    @Override
    public void initialize(ServiceLocator locator) {
        this.locator = locator;
    }
 
    @Override
    public boolean bind(Class<?> component, Set<Class<?>> providerContracts) {
        if (component == PerSessionResource.class) {
            final DynamicConfigurationService dynamicConfigService =
                    locator.getService(DynamicConfigurationService.class);
            final DynamicConfiguration dynamicConfiguration = dynamicConfigService.createDynamicConfiguration();
 
            BindingBuilderFactory
                    .addBinding(BindingBuilderFactory.newFactoryBinder(PerSessionFactory.class)
                    .to(PerSessionResource.class), dynamicConfiguration);
 
            dynamicConfiguration.commit();
 
            return true;
        }

        return false;
    }
 
    @Override
    public void done() {
        // intentionally left blank
    }
}
``` 

The first and very important aspect of writing your own `ComponentProvider` in Jersey is to store the actual ***HK2
[ServiceLocator](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/ServiceLocator.html)*** instance that will
be passed to you as the only argument of the provider initialize method. Your component provider instance will not get
injected at all so this is more or less your only chance to get access to the HK2 runtime of your application. Please
bear in mind, that at the time when your component provider methods get invoked, the `ServiceLocator` is not fully
configured yet. This limitation applies to all component provider methods, as the main goal of any component provider is
to take part in configuring the application's `ServiceLocator`.

Now let's examine the bind method, which is where your provider tells the HK2 how to bind your component. Jersey will
invoke this method multiple times, once for each type that is registered with the actual application. Every time the
bind method is invoked, your component provider needs to decide if it is taking control over the component or not. In
our case we know exactly which Java type we are interested in (`PerSessionResource` class), so the logic in our bind
method is quite straightforward. If we see our `PerSessionResource` class it is our turn to provide our custom binding
for the class, otherwise we just return `false` to make Jersey poll other providers and, if no provider kicks in,
eventually provide the default HK2 binding for the component. Please, refer to the HK2 documentation for the details of
the concrete HK2 APIs used in the bind method implementation above. The main idea behind the code is that we register a
new HK2 [Factory](https://javaee.github.io/hk2/apidocs//org/glassfish/hk2/api/Factory.html) (`PerSessionFactory`), to
provide the `PerSessionResource` instances to HK2.

The implementation of the `PerSessionFactory` is also included above. Please note that as opposed to a component
provider implementation that should never itself rely on an injection support, the factory bound by our component
provider would get injected just fine, since it is only instantiated later, once the Jersey runtime for the application
is fully initialized including the fully configured HK2 runtime. Whenever a new session is seen, the factory
instantiates and injects a new `PerSessionResource instance. The instance is then stored in the `perSessionMap` for
later use (for future calls).

In a real life scenario, you would want to pay more attention to possible synchronization issues. Also, we do not
consider a mechanism that would clean-up any obsolete resources for closed, expired or otherwise invalidated HTTP client
sessions. We have omitted those considerations here for the sake of brevity of our example.
