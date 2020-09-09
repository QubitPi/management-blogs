---
layout: post
title: Entity Data Filtering
tags: [GraphQL, Filtering]
color: rgb(151, 127, 215)
author: QubitPi
feature-img: "assets/img/pexels/design-art/2020-08-11-20-entity-data-filtering/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-11-20-entity-data-filtering/cover.png"
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

Support for ***Entity Filtering*** in Jersey introduces a convenient facility for reducing the amount of data exchanged
over the wire between client and server without a need to create specialized data view components. The main idea behind
this feature is to give you APIs that will let you to selectively filter out any non-relevant data from the marshalled
object model before sending the data to the other party based on the context of the particular message exchange. This
way, only the necessary or relevant portion of the data is transferred over the network with each client request or
server response, without a need to create special facade models for transferring these limited subsets of the model
data. 

Entity filtering feature allows you to define your own entity-filtering rules for your entity classes based on the
current context (e.g. matched resource method) and keep these rules in one place (directly in your domain model). With
Jersey entity filtering facility it is also possible to assign security access rules to entity classes properties and
property accessors. 

## Enabling and Configuring Entity Filtering in Your Application

Entity Filtering support in Jersey is provided as an extension module and needs to be mentioned explicitly in your
`pom.xml` file (in case of using Maven):

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-entity-filtering</artifactId>
    <version>2.31</version>
</dependency>
```

> If you're not using Maven make sure to have also all the transitive dependencies (see
> [jersey-entity-filtering](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-entity-filtering/dependencies.html))
> on the classpath. 

The entity-filtering extension module provides three `Feature`s which you can register into server/client runtime in
prior to use Entity Filtering in an application:

1. ***[EntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/EntityFilteringFeature.html)*** -
   Filtering based on entity-filtering annotations (or i.e. external configuration file) created using
   ***[@EntityFiltering](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/EntityFiltering.html)***
   meta-annotation.
2. ***[SecurityEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SecurityEntityFilteringFeature.html)*** -
   Filtering based on security (`javax.annotation.security`) and entity-filtering annotations. 
3. ***[SelectableEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SelectableEntityFilteringFeature.html)*** -
   Filtering based on dynamic and configurable query parameters.
   
If you would like to use both entity-filtering annotations and security annotations for entity data filtering it is
enough to register `SecurityEntityFilteringFeature`, because this feature also registers `EntityFilteringFeature`.

Entity-filtering currently recognizes one property that can be passed into the
[Configuration](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/Configuration.html) instance
(client/server) which is
***[EntityFilteringFeature.ENTITY_FILTERING_SCOPE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/EntityFilteringFeature.html#ENTITY_FILTERING_SCOPE)*** -
`"jersey.config.entityFiltering.scope"`. This property defines one or more annotations that should be used as
entity-filtering scope when reading/writing an entity.

You can configure entity-filtering on server (basic + security examples) as follows:

```java
// Registering and configuring entity-filtering feature on server
new ResourceConfig()
        .property(EntityFilteringFeature.ENTITY_FILTERING_SCOPE, new Annotation[] {ProjectDetailedView.Factory.get()})
        .register(EntityFilteringFeature.class)
        .register( ... );
```

```java
// Registering and configuring entity-filtering feature with security annotations on server
new ResourceConfig()
        .property(
                EntityFilteringFeature.ENTITY_FILTERING_SCOPE,
                new Annotation[] {SecurityAnnotations.rolesAllowed("manager")}
        )
        .register(SecurityEntityFilteringFeature.class)
        .register( ... );
```

```java
// Registering and configuring entity-filtering feature based on dynamic and configurable query parameters
new ResourceConfig()
        .property(SelectableEntityFilteringFeature.QUERY_PARAM_NAME, "select")
        .register(SelectableEntityFilteringFeature.class)
        .register( ... );
```

Use similar steps to register entity-filtering on client:

```java
final ClientConfig config = new ClientConfig()
        .property(EntityFilteringFeature.ENTITY_FILTERING_SCOPE, new Annotation[] {ProjectDetailedView.Factory.get()})
        .register(EntityFilteringFeature.class)
        .register( ... );
 
// Create new client.
final Client client = ClientClientBuilder.newClient(config);
 
// Use the client.
```

## Components Used to Describe Entity Filtering Concepts

In the next section the entity-filtering features will be illustrated on a project-tracking application that contains
three classes in its domain model and few resources (only Project resource will be shown in this post). The full source
code for the example application can be found in Jersey
[Entity Filtering example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/entity-filtering).

Suppose there are three domain model classes (or entities) in our model

1. `Project`
2. `User`
3. `Task`

```java
public class Project {
 
    private Long id;
 
    private String name;
 
    private String description;
 
    private List<Task> tasks;
 
    private List<User> users;
 
    // getters and setters
}
```

```java
public class User {
 
    private Long id;
 
    private String name;
 
    private String email;
 
    private List<Project> projects;
 
    private List<Task> tasks;
 
    // getters and setters
}
```

```java
public class Task {
 
    private Long id;
 
    private String name;
 
    private String description;
 
    private Project project;
 
    private User user;
 
    // getters and setters
}
```

To retrieve the entities from server to client, we have created also a couple of JAX-RS resources as shown in
`ProjectsResource` below.

```java
@Path("projects")
@Produces("application/json")
public class ProjectsResource {
 
    @GET
    @Path("{id}")
    public Project getProject(@PathParam("id") final Long id) {
        return getDetailedProject(id);
    }
 
    @GET
    public List<Project> getProjects() {
        return getDetailedProjects();
    }
}
```

## Using Custom Annotations to Filter Entities

Entity filtering via annotations is based on an
[`@EntityFiltering`](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/EntityFiltering.html)
meta-annotation. This meta-annotation is used to identify entity-filtering annotations that can be then attached to

* domain model classes (supported on both, server and client sides), and
* resource methods/resource classes (only on server side)

An example of entity-filtering annotation applicable to a class, field or method can be seen below

```java
@Documented
@EntityFiltering
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ProjectDetailedView {
    // intentionally left blank
}
```

```java
/**
 * Factory class for creating instances of {@code ProjectDetailedView} annotation.
 */
class Factory extends AnnotationLiteral<ProjectDetailedView> implements ProjectDetailedView {

    private Factory() {
        // intentionally left blank
    }

    public static ProjectDetailedView get() {
        return new Factory();
    }
}
```

Since creating annotation instances directly in Java code is not trivial, it is a good practice to provide a `Factory`
class, through which new instances of the annotation can be created. The annotation factory class can be created by
extending the ***HK2 AnnotationLiteral*** class and implementing the annotation interface itself. It should also provide
a static factory method that will create and return a new instance of the `Factory` class when invoked. Such annotation
instances can be then passed to the client and server run-times to define or override entity-filtering scopes.

By placing an entity-filtering annotation on an entity (class, fields, getters or setters) we define a so-called
entity-filtering scope for the entity. The purpose of entity-filtering scope is to identify parts of the domain model
that should be processed when the model is to be sent over the wire in a particular entity-filtering scope. We
distinguish between:

* ***global entity-filtering scope*** (defined by placing filtering annotation on a **class** itself), and
* ***local entity-filtering scope*** (defined by placing filtering annotation on a **field**, **getter**, or **setter**)

***Unannotated members of a domain model class are automatically added to all existing global entity-filtering
scopes***. **If there is no explicit global entity-filtering scope defined on a class a default scope is created for this
class to group these members**.

Creating entity-filtering scopes using custom entity-filtering annotations in domain model classes is illustrated in the
following examples.

```java
public class Project {
 
    private Long id;
 
    private String name;
 
    private String description;
 
    @ProjectDetailedView
    private List<Task> tasks;
 
    @ProjectDetailedView
    private List<User> users;
 
    // getters and setters
}
```

```java
public class User {
 
    private Long id;
 
    private String name;
 
    private String email;
 
    @UserDetailedView
    private List<Project> projects;
 
    @UserDetailedView
    private List<Task> tasks;
 
    // getters and setters
}
```

```java
public class Task {
 
    private Long id;
 
    private String name;
 
    private String description;
 
    @TaskDetailedView
    private Project project;
 
    @TaskDetailedView
    private User user;
 
    // getters and setters
}
```

As you can see in the examples above, we have defined 3 separate scopes using `@ProjectDetailedView`,
`@UserDetailedView`, and `@TaskDetailedView` annotations and we have applied these scopes selectively to certain fields
in the domain model classes.

Once the entity-filtering scopes are applied to the parts of a domain model, the entity filtering facility (when
enabled) will check the active scopes when the model is being sent over the wire, and filter out all parts from the
model for which there is no active scope set in the given context. Therefore, we need a way of controlling the scopes
active in any given context in order to process the model data in a certain way (e.g. expose the detailed view). We need
to tell the server/client runtime which entity-filtering scopes we want to apply. There are 2 ways how to do this for
client-side and 3 ways for server-side:

1. Out-bound client request or server response programmatically created with entity-filtering annotations that identify
   the scopes to be applied (available on both, client and server)
2. Property identifying the applied scopes passed through
   [Configuration](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/Configuration.html)
   (available on both, client and server)
3. Entity-filtering annotations identifying the applied scopes attached to a resource method or class (server-side only)

**When the multiple approaches are combined, the priorities of calculating the applied scopes are as follows: 1 > 2 >
3**.

In a graph of domain model objects, the entity-filtering scopes are applied to the root node as well as transitively to
all the child nodes. **Fields and child nodes that do not match at least a single active scope are filtered out**. When
the scope matching is performed, annotations applied to the domain model classes and fields are used to compute the
scope for each particular component of the model. If there are no annotations on the class or its fields, the default
scope is assumed. During the filtering, first, the annotations on root model class and its fields are considered. For
all composite fields that have not been filtered out, the annotations on the referenced child class and its fields are
considered next, and so on.

### Server-side Entity Filtering

To pass entity-filtering annotations via
[Response](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/Response.html)
returned from a resource method you can leverage the
***[Response.ResponseBuilder#entity(Object, Annotation[])](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/Response.ResponseBuilder.html#entity(java.lang.Object,%20java.lang.annotation.Annotation[]))***
method. The next example illustrates this approach. You will also see why every custom entity-filtering annotation
should contain a factory for creating instances of the annotation.

```java
@Path("projects")
@Produces("application/json")
public class ProjectsResource {
 
    @GET
    public Response getProjects(@QueryParam("detailed") final boolean isDetailed) {
        return Response
                .ok()
                .entity(
                        new GenericEntity<List<Project>>(EntityStore.getProjects()) {},
                        isDetailed
                                ? new Annotation[]{ProjectDetailedView.Factory.get()}
                                : new Annotation[0]
                )
                .build();
    }
}
```

Annotating a resource method/class is typically easier although it is less flexible and may require more resource
methods to be created to cover all the alternative use case scenarios. For example:

```java
@Path("projects")
@Produces("application/json")
public class ProjectsResource {
 
    @GET
    public List<Project> getProjects() {
        return getDetailedProjects();
    }
 
    @GET
    @Path("detailed")
    @ProjectDetailedView
    public List<Project> getDetailedProjects() {
        return EntityStore.getProjects();
    }
}
```

When a `Project` model from the example above is requested in a scope represented by `@ProjectDetailedView`
entity-filtering annotation, the `Project` model data sent over the wire would contain:

* `Project - id, name, description, tasks, users`
* `Task - id, name, description`
* `User - id, name, email`

For the *default entity-filtering scope* the filtered model would look like:

* `Project - id, name, description`

### Client-side Entity Filtering

```java
ClientBuilder.newClient(config)
        .target(uri)
        .request()
        .post(Entity.entity(project, new Annotation[] {ProjectDetailedView.Factory.get()}));
```

## Role-Based Entity Filtering Using (javax.annotation.security) Annotations

Filtering the content sent to the client (or server) based on the authorized security roles is a commonly required use
case. By registering
[SecurityEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SecurityEntityFilteringFeature.html)
you can leverage the Jersey Entity Filtering facility in connection with standard
`javax.annotation.security annotations` exactly the same way as you would with custom entity-filtering annotations
described in previous chapters. Supported security annotations are:

* ***[@PermitAll](https://javaee.github.io/javaee-spec/javadocs/javax/annotation/security/PermitAll.html)***,

* ***[@RolesAllowed](https://javaee.github.io/javaee-spec/javadocs/javax/annotation/security/RolesAllowed.html)***, and

* ***[@DenyAll](https://javaee.github.io/javaee-spec/javadocs/javax/annotation/security/DenyAll.html)***

Although the mechanics of the Entity Data Filtering feature used for the security annotation-based filtering is the same
as with the entity-filtering annotations, the processing of security annotations differs in a few important aspects:

* Custom [SecurityContext](https://eclipse-ee4j.github.io/jaxrs-api/apidocs/2.1.6/javax/ws/rs/core/SecurityContext.html)
  should be set by a container request filter in order to use `@RolesAllowed` for role-based filtering of domain model
  data (server-side)
* There is no need to provide entity-filtering (or security) annotations on resource methods in order to define
  entity-filtering scopes for `@RolesAllowed` that is applied to the domain model components, as all the available roles
  for the current user are automatically determined using the information from the provided `SecurityContext`
  (server-side only). 

> 📋 Instances of security annotations (to be used for programmatically defined scopes either on client or server) can
> be created using one of the methods in the
> ***[SecurityAnnotations](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SecurityAnnotations.html)***
> factory class that is part of the Jersey Entity Filtering API.

## Entity Filtering Based on Dynamic and Configurable Query Parameters

Filtering the content sent to the client (or server) dynamically based on query parameters is another commonly required
use case. By registering
***[SelectableEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SelectableEntityFilteringFeature.html)***
you can leverage the Jersey Entity Filtering facility in connection with query parameters exactly the same way as you
would with custom entity-filtering annotations described in previous sections.

```java
@XmlRootElement
public class Address {
 
    private String streetAddress;
 
    private String region;
 
    private PhoneNumber phoneNumber;
}
```

Query parameters are supported in comma delimited "dot notation" style similar to `BeanInfo` objects and Spring path
expressions. As an example, the following URL:
`http://jersey.example.com/addresses/51234?select=region,streetAddress` may render only the address's region and street
address properties as in the following example: 

```json
{
   "region" : "CA",
   "streetAddress" : "1234 Fake St."
}
```

## Defining Custom Handling for Entity-Filtering Annotations

To create a custom entity-filtering annotation with special handling, i.e. an field aggregator annotation used to
annotate classes like the one below, it is, in most cases, sufficient to implement and register the following SPI
contracts:

* ***[EntityProcessor](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/EntityProcessor.html)*** -
  Implementations of this SPI are invoked to process entity class and its members. Custom implementations can extend from AbstractEntityProcessor.
* ***[ScopeResolver](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/ScopeResolver.html)*** -
  Implementations of this SPI are invoked to retrieve entity-filtering scopes from an array of provided annotations.
  
```java
@EntityFiltering
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilteringAggregator {
 
    /**
     * Entity-filtering scope to add given fields to.
     */
    Annotation filteringScope();
 
    /**
     * Fields to be a part of the entity-filtering scope.
     */
    String[] fields();
}
```

## Supporting Entity Data Filtering in Custom Entity Providers or Frameworks

To support Entity Data Filtering in custom entity providers, it is sufficient in most of the cases to implement and
register the following SPI contracts:

* ***[ObjectProvider](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/ObjectProvider.html)*** -
  To be able to obtain an instance of a filtering object model your provider understands and can act on. The
  implementations can extend
  ***[AbstractObjectProvider](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/AbstractObjectProvider.html)***.
* ***[ObjectGraphTransformer](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/ObjectGraphTransformer.html)*** -
  To transform a read-only generic representation of a domain object model graph to be processed into an
  entity-filtering object model your provider understands and can act on. The implementations can extend
  [AbstractObjectProvider](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/spi/AbstractObjectProvider.html).
  
```java
@Singleton
public class FilteringMoxyJsonProvider extends ConfigurableMoxyJsonProvider {
 
    @Inject
    private Provider<ObjectProvider<ObjectGraph>> provider;
 
    @Override
    protected void preWriteTo(
            final Object object,
            final Class<?> type,
            final Type genericType,
            final Annotation[] annotations,
            final MediaType mediaType,
            final MultivaluedMap<String, Object> httpHeaders,
            final Marshaller marshaller
    ) throws JAXBException {
        super.preWriteTo(object, type, genericType, annotations, mediaType, httpHeaders, marshaller);
 
        // Entity Filtering.
        if (marshaller.getProperty(MarshallerProperties.OBJECT_GRAPH) == null) {
            final Object objectGraph = provider.get().getFilteringObject(genericType, true, annotations);
 
            if (objectGraph != null) {
                marshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, objectGraph);
            }
        }
    }
 
    @Override
    protected void preReadFrom(
            final Class<Object> type,
            final Type genericType,
            final Annotation[] annotations,
            final MediaType mediaType,
            final MultivaluedMap<String, String> httpHeaders,
            final Unmarshaller unmarshaller
    ) throws JAXBException {
        super.preReadFrom(type, genericType, annotations, mediaType, httpHeaders, unmarshaller);
 
        // Entity Filtering.
        if (unmarshaller.getProperty(MarshallerProperties.OBJECT_GRAPH) == null) {
            final Object objectGraph = provider.get().getFilteringObject(genericType, false, annotations);
 
            if (objectGraph != null) {
                unmarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, objectGraph);
            }
        }
    }
}
```

## Modules with Support for Entity Data Filtering

2 modules from Jersey workspace that support Entity Filtering:

1. [MOXy](https://qubitpi.github.io/jersey-guide/2020/07/31/09-support-for-common-media-type-representations.html#moxy)
2. [Jackson (2.x)](https://qubitpi.github.io/jersey-guide/2020/07/31/09-support-for-common-media-type-representations.html#jackson-1x-and-2x)

In order to use Entity Filtering in the mentioned modules you need to **explicitly** register either
[EntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/EntityFilteringFeature.html),
[SecurityEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SecurityEntityFilteringFeature.html),
or
[SelectableEntityFilteringFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/message/filtering/SelectableEntityFilteringFeature.html)
to activate Entity Filtering for particular module.

## Examples

To see a complete working examples of entity-filtering feature refer to the: 

* [Entity Filtering example](https://github.com/eclipse-ee4j/jersey/tree/master/examples/entity-filtering)
* [Entity Filtering example (with security annotations)](https://github.com/eclipse-ee4j/jersey/tree/master/examples/entity-filtering-security)
* [Entity Filtering example (based on dynamic and configurable query parameters)](https://github.com/eclipse-ee4j/jersey/tree/master/examples/entity-filtering-selectable)
