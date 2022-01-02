---
layout: post
title: Bean Validation Support
tags: [Validation, Bean, Hibernate, Constraint Annotations]
color: rgb(233, 84, 32)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

***Validation*** is a process of verifying that some data obeys one or more pre-defined constraints. This post describes
support for [Bean Validation](https://beanvalidation.org/) in Jersey in terms of the needed dependencies, configuration,
registration and usage. For more detailed description on how JAX-RS provides native support for validating resource
classes based on the Bean Validation refer to
[JAX-RS spec](https://github.com/QubitPi/Opinionated-JAX-RS-Spec).

## Bean Validation Dependencies

Bean Validation support in Jersey is provided as an extension module and needs to be mentioned explicitly in your
`pom.xml` file (in case of using Maven):

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-bean-validation</artifactId>
    <version>2.31</version>
</dependency>
```

> 📝 If you're not using Maven make sure to have also all the transitive dependencies (see
> [jersey-bean-validation](https://eclipse-ee4j.github.io/jersey.github.io/project-info/2.31/jersey/project/jersey-bean-validation/dependencies.html))
> on the classpath.

This module depends directly on ***[Hibernate Validator](http://hibernate.org/validator/)*** which provides a most
commonly used implementation of the Bean Validation API spec

If you want to use a different implementation of the Bean Validation API, use standard Maven mechanisms to exclude
Hibernate Validator from the modules dependencies and add a dependency of your own:

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext</groupId>
    <artifactId>jersey-bean-validation</artifactId>
    <version>2.31</version>
    <exclusions>
        <exclusion>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Enabling Bean Validation in Jersey

[Jersey Bean Validation is one of the modules where you don't need to explicitly register](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html#auto-discoverable-features)
its `Feature`s
(***[ValidationFeature](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/validation/ValidationFeature.html)***)
on the server as its features are automatically discovered and registered when you add the `jersey-bean-validation`
module to your classpath. There are three Jersey specific properties that could disable automatic discovery and
registration of Jersey Bean Validation integration module:

1. ***[CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/CommonProperties.html#FEATURE_AUTO_DISCOVERY_DISABLE)***
2. ***[ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#FEATURE_AUTO_DISCOVERY_DISABLE)***
3. ***[ServerProperties.BV_FEATURE_DISABLE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#BV_FEATURE_DISABLE)***

> 📝 Jersey does not support Bean Validation on the client at the moment.

## Configuring Bean Validation Support

Configuration of Bean Validation support in Jersey is twofold - there are few specific properties that affects Jersey
behaviour (e.g. sending validation error entities to the client) and then there is
***[ValidationConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/validation/ValidationConfig.html)***
class that configures
***[Validator](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/Validator.html)*** used for
validating resources in JAX-RS application.

To configure Jersey specific behaviour you can use the following properties:

* ***[ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK)*** -
  Disables [@ValidateOnExecution](#validateonexecution) check.
* ***[ServerProperties.BV_SEND_ERROR_IN_RESPONSE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#BV_SEND_ERROR_IN_RESPONSE)*** -
  Enables sending validation errors in response entity to the client. More on this in section
  [ValidationError](#validationerror)
  
```java
new ResourceConfig()
        .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
        .property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true)
        .register(...);
```

Customization of the `Validator` used in validation of resource classes/methods can be done using ***ValidationConfig***
class and exposing it via `ContextResolver<T>` mechanism as shown in the following snippet. You can set custom instances
for the following interfaces from the Bean Validation API:

* ***[MessageInterpolator](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/MessageInterpolator.html)*** -
  interpolates a given constraint violation message.
* ***[TraversableResolver](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/TraversableResolver.html)*** -
  determines if a property can be accessed by the Bean Validation provider.
* ***[ConstraintValidatorFactory](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/ConstraintValidatorFactory.html)*** -
  instantiates a `ConstraintValidator` instance based off its class. Note that by setting a custom
  `ConstraintValidatorFactory` you may loose injection of available resources/providers at the moment. See section
  [Injecting](#injecting) on how to handle this.
* ***[ParameterNameProvider](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/ParameterNameProvider.html)*** -
  provides names for method and constructor parameters.

```java
/**
 * Custom configuration of validation. This configuration defines custom:
 * <ul>
 *     <li>ConstraintValidationFactory - so that validators are able to inject Jersey providers/resources.</li>
 *     <li>ParameterNameProvider - if method input parameters are invalid, this class returns actual parameter names
 *     instead of the default ones ({@code arg0, arg1, ..})</li>
 * </ul>
 */
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {
 
    @Context
    private ResourceContext resourceContext;
 
    @Override
    public ValidationConfig getContext(final Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.setConstraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class));
        config.setParameterNameProvider(new CustomParameterNameProvider());
        return config;
    }
 
    /**
     * See ContactCardTest#testAddInvalidContact.
     */
    private class CustomParameterNameProvider implements ParameterNameProvider {
 
        private final ParameterNameProvider nameProvider;
 
        public CustomParameterNameProvider() {
            nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
        }
 
        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return nameProvider.getParameterNames(constructor);
        }
 
        @Override
        public List<String> getParameterNames(final Method method) {
            // See ContactCardTest#testAddInvalidContact.
            if ("addContact".equals(method.getName())) {
                return Arrays.asList("contact");
            }
            return nameProvider.getParameterNames(method);
        }
    }
}
```

Register this class in your app:

```java
final Application application = new ResourceConfig()
        .register(ValidationConfigurationContextResolver.class)
        .register(...);
```

## Validating JAX-RS Resources and Methods

JAX-RS specification states that constraint annotations are allowed in the same locations as the following annotations:

* ***@MatrixParam***
* ***@QueryParam***
* ***@PathParam***
* ***@CookieParam***
* ***@HeaderParam***
* ***@Context***

except in class constructors and property setters. Specifically, they are allowed in

* resource method parameters,
* fields
* property getters
* resource classes
* entity parameters
* resource methods (return values).

Jersey provides support for validation (see following sections) on annotated input parameters and return value of the
invoked resource method as well as validation of resource class (class constraints, field constraints) where this
resource method is placed. Jersey does not support, and doesn't validate, constraints placed on constructors and Bean
Validation groups (only Default group is supported at the moment).

### Constraint Annotations

The JAX-RS Server API provides support for extracting request values and mapping them into Java fields, properties and
parameters using annotations such as `@HeaderParam`, `@QueryParam`, etc. It also supports mapping of the request entity
bodies into Java objects via non-annotated parameters (i.e., parameters without any JAX-RS annotations).

***The Bean Validation specification supports the use of constraint annotations as a way of declaratively validating
beans, method parameters and method returned values***. For example, consider the following resource class augmented
with constraint annotations.

```java
@Path("/")
class MyResourceClass {
 
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public void registerUser(
            @NotNull @FormParam("firstName") String firstName,
            @NotNull @FormParam("lastName") String lastName,
            @Email @FormParam("email") String email
    ) {
        ...
    }
}
```

The annotations ***@NotNull*** and ***@Email*** impose additional constraints on the form parameters `firstName`,
`lastName` and `email`.

* The `@NotNull` constraint is built-in to the Bean Validation API;
* The `@Email` constraint is assumed to be user defined in the example above.

These constraint annotations are not restricted to method parameters, they can be used in any location in which JAX-RS
binding annotations are allowed with the exception of constructors and property setters. 

Rather than using method parameters, the `MyResourceClass` shown above could have been written as in example below

```java
@Path("/")
class MyResourceClass {
 
    @NotNull
    @FormParam("firstName")
    private String firstName;
 
    @NotNull
    @FormParam("lastName")
    private String lastName;
 
    private String email;
 
    @FormParam("email")
    public void setEmail(String email) {
        this.email = email;
    }
 
    @Email
    public String getEmail() {
        return email;
    }
 
    ...
}
```

Note that in this version, `firstName` and `lastName` are fields initialized via injection and `email` is a resource
class property. Constraint annotations on properties are specified in their corresponding getters. 

Constraint annotations are also allowed on resource classes. In addition to annotating fields and properties, an
annotation can be defined for the entire class. Let us assume that `@NonEmptyNames` validates that one of the two name
fields in `MyResourceClass` is provided. Using such an annotation, the example above can be extended to look like
example below

```java
@Path("/")
@NonEmptyNames
class MyResourceClass {
 
    @NotNull
    @FormParam("firstName")
    private String firstName;
 
    @NotNull
    @FormParam("lastName")
    private String lastName;
 
    private String email;
 
    ...
}
```

Constraint annotations on resource classes are useful for defining cross-field and cross-property constraints.

### Annotation Constraints and Validators

***Annotation constraints*** and ***validators*** are defined in accordance with the Bean Validation specification. The
`@Email` annotation is defined using the Bean Validation
***[@Constraint](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/Constraint.html)***
meta-annotation

```java
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface Email {
 
    String message() default "{com.example.validation.constraints.email}";
 
    Class<?>[] groups() default {
        ...
    }
 
    Class<? extends Payload>[] payload() default {
        ...
    }
}
```

**The `@Constraint` annotation must include a reference to the validator class that will be used to validate decorated
values**. The `EmailValidator` class must implement `ConstraintValidator<Email, T>` where `T` is the type of values
being validated, as described in this example:

```java
public class EmailValidator implements ConstraintValidator<Email, String> {
 
    public void initialize(Email email) {
        ...
    }
 
    public boolean isValid(String value, ConstraintValidatorContext context) {
        ...
    }
}
```

Thus, `EmailValidator` applies to values annotated with `@Email` that are of type `String`. Validators for other Java
types can be defined for the same constraint annotation.

### Entity Validation

Request entity bodies can be mapped to resource method parameters. There are two ways in which these entities can be
validated. If the request entity is mapped to a Java bean whose class is decorated with Bean Validation annotations,
then validation can be enabled using `@Valid`:

```java
@StandardUser
class User {
 
    @NotNull
    private String firstName;
 
    ...
}
```

```java
@Path("/")
class MyResourceClass {
 
    @POST
    @Consumes("application/xml")
    public void registerUser(@Valid User user) {
        ...
    }
}
```

In this case, the validator associated with `@StandardUser` (as well as those for non-class level constraints like
`@NotNull`) will be called to verify the request entity mapped to user.

Alternatively, a new annotation can be defined and used directly on the resource method parameter:

```java
@Path("/")
class MyResourceClass {
 
    @POST
    @Consumes("application/xml")
    public void registerUser(@PremiumUser User user) {
        ...
    }
}
```

In the example above, `@PremiumUser` rather than `@StandardUser` will be used to validate the request entity. These two
ways in which validation of entities can be triggered can also be combined by including `@Valid` in the list of
constraints. The presence of `@Valid` will trigger validation of all the constraint annotations decorating a Java bean
class.

Response entity bodies returned from resource methods can be validated in a similar manner by annotating the resource
method itself. To exemplify, assuming both `@StandardUser` and `@PremiumUser` are required to be checked before
returning a user, the `getUser` method can be annotated as

```java
@Path("/")
class MyResourceClass {
 
    @GET
    @Valid
    @PremiumUser
    @Path("{id}")
    @Produces("application/xml")
    public User getUser(@PathParam("id") String id) {
        User u = findUser(id);
        return u;
    }
 
    ...
}
```

Note that `@PremiumUser` is explicitly listed and `@StandardUser` is triggered by the presence of the `@Valid`
annotation - see definition of `User` class earlier in this section.

### Annotation Inheritance

The rules for inheritance of constraint annotation are defined in Bean Validation specification. It is worth noting that
***these rules are incompatible with those defined by JAX-RS***. Generally speaking, constraint annotations in Bean
Validation are cumulative (can be strengthen) across a given type hierarchy while JAX-RS annotations are inherited or,
overridden and ignored.

**For Bean Validation annotations Jersey follows the constraint annotation rules defined in the Bean Validation
specification**.

## `@ValidateOnExecution`

According to Bean Validation specification, validation is enabled by default only for the so-called ***constrained
methods***. Getter methods as defined by the Java Beans specification are not constrained methods, so they will not be
validated by default. The special annotation ***@ValidateOnExecution*** can be used to selectively enable and disable
validation. For example, you can enable validation on method `getEmail` shown in

```java
@Path("/")
class MyResourceClass {
 
    @Email
    @ValidateOnExecution
    public String getEmail() {
        return email;
    }
 
    ...
}
```

The default value for the type attribute of `@ValidateOnExecution` is `IMPLICIT` which results in method `getEmail`
being validated.

## Injecting

Jersey allows you to inject registered resources/providers into your
[ConstraintValidator](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/ConstraintValidator.html)
implementation and you can inject
[Configuration](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/Configuration.html),
[ValidatorFactory](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/ValidatorFactory.html),
and [Validator](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/Validator.html) as
required by Bean Validation spec.

> 📝 Injected
> [Configuration](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/Configuration.html),
> [ValidatorFactory](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/ValidatorFactory.html),
> and [Validator](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/Validator.html) do not
> inherit configuration provided by
> [ValidationConfig](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/validation/ValidationConfig.html)
> and need to be configured manually. 

Injection of JAX-RS components into `ConstraintValidators` is supported via a custom `ConstraintValidatorFactory`
provided by Jersey. An example is shown below

```java
public class EmailValidator implements ConstraintValidator<Email, String> {
 
    @Context
    private UriInfo uriInfo;
 
    public void initialize(Email email) {
        ...
    }
 
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Use UriInfo.
        ...
    }
}
```

Using a custom
[ConstraintValidatorFactory](https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/javax/validation/ConstraintValidatorFactory.html)
of your own disables registration of the one provided by Jersey and injection support for resources/providers (if
needed) has to be provided by this new implementation. For example,

```java
public class InjectingConstraintValidatorFactory implements ConstraintValidatorFactory {
 
    @Context
    private ResourceContext resourceContext;
 
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        return resourceContext.getResource(key);
    }
 
    @Override
    public void releaseInstance(final ConstraintValidator<?, ?> instance) {
        // NOOP
    }
}
```

> 📝 This behaviour is likely to change in one of the next version of Jersey to remove the need of manually providing
> support for injecting resources/providers from Jersey in your own `ConstraintValidatorFactory` implementation code.

## Error Reporting

Bean Validation specification defines a small hierarchy of exceptions (they all inherit from
***[ValidationException](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/ValidationException.html)***)
that could be thrown during initialization of validation engine or (for our case more importantly) during validation of
input/output values (
***[ConstraintViolationException](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/javax/validation/ConstraintViolationException.html)***
). If a thrown exception is a subclass of `ValidationException` except `ConstraintViolationException` then this
exception is mapped to a HTTP response with status code 500 (Internal Server Error). On the other hand, when a
`ConstraintViolationException` is throw, two different status code would be returned:

* **500 (Internal Server Error)** - If the exception was thrown while validating a method return type.
* **400 (Bad Request)** - Otherwise.

### `ValidationError`

By default, (during mapping `ConstraintViolationException`s) Jersey doesn't return any entities that would include
validation errors to the client. This default behaviour could be changed by enabling
[ServerProperties.BV_SEND_ERROR_IN_RESPONSE](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/ServerProperties.html#BV_SEND_ERROR_IN_RESPONSE)
property in your application. When this property is enabled then our custom `ExceptionMapper<E extends Throwable>` (that
is handling `ValidationException`s) would transform `ConstraintViolationException`(s) into
[ValidationError](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/validation/ValidationError.html)(s)
and set this object (collection) as the new response entity which Jersey is able to sent to the client. Four
`MediaTypes` are currently supported when sending `ValidationError`s to the client:

1. `text/plain`
2. `text/html`
3. `application/xml`
4. `application/json`

> 📝 You need to register one of the JSON (JAXB) providers (e.g.
> [MOXy](https://qubitpi.github.io/jersey-guide/2020/07/31/09-support-for-common-media-type-representations.html#jackson-1x-and-2x))
> to marshall validation errors to JSON.

Let's take a look at
[ValidationError](https://eclipse-ee4j.github.io/jersey.github.io/apidocs/snapshot/jersey/org/glassfish/jersey/server/validation/ValidationError.html)
class to see which properties are send to the client:

```java
@XmlRootElement
public final class ValidationError {
 
    private String message;
 
    private String messageTemplate;
 
    private String path;
 
    private String invalidValue;
 
    ...
}
``` 

The `message` property is the interpolated error message, `messageTemplate` represents a non-interpolated error message
(or key from your constraint definition e.g. `javax.validation.constraints.NotNull.message`), `path` contains information
about the path in the validated object graph to the property holding invalid value and `invalidValue` is the string
representation of the invalid value itself.

Here are few examples of `ValidationError` messages sent to client:

```
HTTP/1.1 500 Internal Server Error
Content-Length: 114
Content-Type: text/plain
Vary: Accept
Server: Jetty(6.1.24)

Contact with given ID does not exist. (path = ContactCardResource.getContact.<return value>, invalidValue = null)
```

```
HTTP/1.1 500 Internal Server Error
Content-Length: ...
Content-Type: text/plain
Vary: Accept
Server: Jetty(6.1.24)

<div class="validation-errors">
    <div class="validation-error">
        <span class="message">Contact with given ID does not exist.</span>
        (
        <span class="path">
            <strong>path</strong>
            = ContactCardResource.getContact.<return value>
        </span>
        ,
        <span class="invalid-value">
            <strong>invalidValue</strong>
            = null
        </span>
        )
    </div>
</div>
```

```
HTTP/1.1 500 Internal Server Error
Content-Length: ...
Content-Type: text/plain
Vary: Accept
Server: Jetty(6.1.24)

<?xml version="1.0" encoding="UTF-8"?>
<validationErrors>
    <validationError>
        <message>Contact with given ID does not exist.</message>
        <messageTemplate>{contact.does.not.exist}</messageTemplate>
        <path>ContactCardResource.getContact.&lt;return value&gt;</path>
    </validationError>
</validationErrors>
```

```
HTTP/1.1 500 Internal Server Error
Content-Length: 174
Content-Type: application/json
Vary: Accept
Server: Jetty(6.1.24)

[ {
   "message" : "Contact with given ID does not exist.",
   "messageTemplate" : "{contact.does.not.exist}",
   "path" : "ContactCardResource.getContact.<return value>"
} ]
```
