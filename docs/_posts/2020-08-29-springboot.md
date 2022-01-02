---
layout: post
title: Spring Boot References
tags: [Spring Boot, Spring]
color: rgb(0, 210, 0)
feature-img: "assets/img/post-cover/1-cover.png"
thumbnail: "assets/img/post-cover/1-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Configuration

### Swagger Integration

* [Basics](https://devwithus.com/documenting-spring-boot-rest-api-with-swagger/)

### Binding Mechanism in Spring Boot

The basic binding in Spring Boot is through the combination of `@Configuration` and `@Bean`:
https://howtodoinjava.com/spring-core/spring-configuration-annotation/

#### Autowiring an Interface with Multiple Implementations

```java
public interface MyService {
    
    ...
}
```

```java
public class FirstService implements MyService {
    
    ...
}
```

```java
public class SecondService implements MyService {
    
    ...
}
```

You can make it work by giving it the name of the implementation.

```java
@Autowired
MyService firstService;

@Autowired
MyService secondService;
```

> Reference - https://stackoverflow.com/a/57248283

#### @Value

`@Value` can be used for injecting values into fields in Spring-managed beans, and it **can be applied at the field or
constructor/method parameter level**.

A **property file** is needed in order to define the values we want to inject with the `@Value` annotation. We annotate
a Spring config class with **`@PropertySource`**, with the properties file name, to specify the path of the property
file. For example:

```java
@Component
@PropertySource("classpath:values.properties")
public class PriorityProvider {
 
    private String priority;
 
    @Autowired
    public PriorityProvider(@Value("${property.qualifier.prefix.property}") String priority) {
        this.priority = priority;
    }
 
    // standard getter
}
```

!!! note "`application.properties` - the Default Property File"
    Boot applies its typical convention over configuration approach to property files. This means that we can simply put
    an application.properties file in our `src/main/resources directory`, and it will be auto-detected. We can then
    inject any loaded properties from it as normal.
    
    Using this default file, we don't have to explicitly register a `PropertySource` or even provide a path to a
    property file.
    
##### @Value with Enum

We can use enum with `@Value` annotation. A field or constructor parameter can be injected with enum value using
`@Value` annotation. For example, suppose we have an Enum class of

```java
public enum Fruit {
    APPLE,
    ORANGE,
    BANANA
}
```

and a property being picked up at runtime: 

```properties
market.fruit.bestseller = BANANA
```

We could inject value to an enum field using

```java
@Component
public class Foo {
    
    private final Fruit fruit;
    
    @Autowired
    public Foo(@Value("${market.fruit.bestseller}") Fruit fruit) {
        this.fruit = fruit;
    }
}
```

#### Conditional Beans with Spring Boot

##### @ConditionalOnProperty

The `@ConditionalOnProperty` annotation is probably the most commonly used conditional annotation in Spring Boot
projects. It allows us to load beans conditionally depending on a certain environment property:

```java
@Configuration
@ConditionalOnProperty(
        value="module.enabled", 
        havingValue = "true", 
        matchIfMissing = true
)
class CrossCuttingConcernModule {
    
    ...
}
```

The `CrossCuttingConcernModule` is loaded only if the `module.enabled` property has the value set to `true`. If the
property is not set at all, it will still be loaded, because we have defined `matchIfMissing` as `true`. This way, we
have created a module that is loaded **by default** until we decide otherwise.

In the same way we might create other modules for cross-cutting concerns like security or scheduling that we might want
to disable in a certain (test) environment.

##### @ConditionalOnExpression

If we have a more complex condition based on multiple properties, we can use `@ConditionalOnExpression`

```java
@Configuration
@ConditionalOnExpression("${module.enabled:true} and ${module.submodule.enabled:true}")
class SubModule {
    
    ...
}
```

The `SubModule` is loaded only if both properties `module.enabled` and `module.submodule.enabled` have the value set to
`true`. By appending `:true` to the properties we tell Spring to use `true` as a default value in the case the
properties have not been set. We can use the full extend of the
[Spring Expression Language](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions).

This way we can, for instance, create sub modules that should be disabled if the parent module is disabled, but can also
be disabled if the parent module is enabled.

##### @ConditionalOnBean

Sometimes, we might want to load a bean only if a certain other bean is available in the application context:

```java
@Configuration
@ConditionalOnBean(OtherModule.class)
class DependantModule {
    
    ...
}
```

The `DependantModule` is loaded only if there is a bean of class `OtherModule` in the application context. We could also
define the bean name instead of the bean class.

This way, we can define dependencies between certain modules, for example. One module is only loaded if a certain bean
of another module is available.

##### @ConditionalOnMissingBean

Similarly, we can use `@ConditionalOnMissingBean` if we want to load a bean only if a certain other bean is not in the
application context:

```java
@Configuration
class OnMissingBeanModule {
    
    @Bean
    @ConditionalOnMissingBean
    DataSource dataSource() {
        return new InMemoryDataSource();
    }
}
```

In this example, we inject an in-memory datasource into the application context if there is not already a datasource
available. This is very similar to what Spring Boot does internally to provide an in-memory database in a test context.

##### @ConditionalOnResource

If we want to load a bean depending on the fact that a certain resource is available on the class path, we can use
`@ConditionalOnResource`:

```java
@Configuration
@ConditionalOnResource(resources = "/logback.xml")
class LogbackModule {
    
    ...
}
```

The `LogbackModule` is only loaded if the logback configuration file was found on the classpath. This way, we might
create similar modules that are only loaded if their respective configuration file has been found.

##### @ConditionalOnClass

Load a bean only if a certain class is on the classpath:

```java
@Configuration
@ConditionalOnClass(name = "this.clazz.does.not.Exist")
class OnClassModule {
    
    ...
}
```

##### @ConditionalOnMissingClass

Load a bean only if a certain class is not on the classpath:

```java
@Configuration
@ConditionalOnMissingClass(value = "this.clazz.does.not.Exist")
class OnMissingClassModule {
    
    ...
}
```

##### @ConditionalOnJndi

Load a bean only if a certain resource is available via JNDI:

```java
@Configuration
@ConditionalOnJndi("java:comp/env/foo")
class OnJndiModule {
    
    ...
}
```

##### @ConditionalOnJava

Load a bean only if running a certain version of Java:

```java
@Configuration
@ConditionalOnJava(JavaVersion.EIGHT)
class OnJavaModule {
    
    ...
}
```

##### @ConditionalOnSingleCandidate

Similar to `@ConditionalOnBean`, but will only load a bean if a single candidate for the given bean class has been
determined

```java
@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
class OnSingleCandidateModule {
    
    ...
}
```

##### @ConditionalOnWebApplication

Load a bean only if we're running inside a web application:

```java
@Configuration
@ConditionalOnWebApplication
class OnWebApplicationModule {
    
    ...
}
```

##### @ConditionalOnNotWebApplication

Load a bean only if we're not running inside a web application:

```java
@Configuration
@ConditionalOnNotWebApplication
class OnNotWebApplicationModule {
    
    ...
}
```

##### @ConditionalOnCloudPlatform

Load a bean only if we’re running on a certain cloud platform:

```java
@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
class OnCloudPlatformModule {
    
    ...
}
```

##### Custom Conditions

Aside from the conditional annotations, we can create our own and combine multiple conditions with logical operators.

###### Defining a Custom Condition

Imagine we have some Spring beans that talk to the operating system natively. These beans should only be loaded if we're
running the application on the respective operating system.

Let's implement a condition that loads beans only if we're running the code on a unix machine. For this, we implement
Spring's
[`Condition`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Condition.html)
interface:

```java
class OnUnixCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return SystemUtils.IS_OS_LINUX;
    }
}
```

We simply use Apache Commons' `SystemUtils` class to determine if we're running on a unix-like system. If needed, we
could include more sophisticated logic that uses information about the current application context (`ConditionContext`)
or about the annotated class (`AnnotatedTypeMetadata`).

The condition is now ready to be used in combination with Spring's `@Conditional` annotation:

```java
@Bean
@Conditional(OnUnixCondition.class)
UnixBean unixBean() {
    return new UnixBean();
}
```

###### Combining Conditions with "OR"

If we want to combine multiple conditions into a single condition with the logical "OR" operator, we can extend
`AnyNestedCondition`

```java
class OnWindowsOrUnixCondition extends AnyNestedCondition {

    OnWindowsOrUnixCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @Conditional(OnWindowsCondition.class)
    static class OnWindows { }

    @Conditional(OnUnixCondition.class)
    static class OnUnix { }
}
```

Here, we have created a condition that is satisfied if the application runs on windows or unix.

The `AnyNestedCondition` parent class will evaluate the `@Conditional` annotations on the methods and combine them using
the OR operator.

We can use this condition just like any other condition:

```java
@Bean
@Conditional(OnWindowsOrUnixCondition.class)
WindowsOrUnixBean windowsOrUnixBean() {
    return new WindowsOrUnixBean();
}
```

> ⚠️ Is your `AnyNestedCondition` or `AllNestedConditions` not working?
>
> Check the `ConfigurationPhase` parameter passed into `super()`. If you want to apply your combined condition to
> `@Configuration` beans, use the value `PARSE_CONFIGURATION`. If you want to apply the condition to simple beans, use
> `REGISTER_BEAN` as shown in the example above. Spring Boot needs to make this distinction so it can apply the
> conditions at the right time during application context startup.

###### Combining Conditions with AND

If we want to combine conditions with "AND" logic, we can simply use multiple `@Conditional...` annotations on a single
bean. They will automatically be combined with the logical "AND" operator so that if at least one condition fails, the
bean will not be loaded:

```java
@Bean
@ConditionalOnUnix
@Conditional(OnWindowsCondition.class)
WindowsAndUnixBean windowsAndUnixBean() {
    return new WindowsAndUnixBean();
}
```

This bean should never load, unless someone has created a Windows / Unix hybrid that I'm not aware of.

Note that the `@Conditional` annotation cannot be used more than once on a single method or class. So, if we want to
combine multiple annotations this way, we have to use custom `@ConditionalOn...` annotations, which do not have this
restriction.

Alternatively, if we want to combine conditions with AND into a single `@Conditional` annotation, we can extend Spring
Boot's `AllNestedConditions` class which works exactly the same as `AnyNestedConditions` described above.

###### Combining Conditions with NOT

Similar to `AnyNestedCondition` and `AllNestedConditions`, we can extend `NoneNestedCondition` to only load beans if
NONE of the combined conditions match.

###### Defining a Custom @ConditionalOn… Annotation

We can create a custom annotation for any condition. We simply need to meta-annotate this annotation with
`@Conditional`:

```java
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnLinuxCondition.class)
public @interface ConditionalOnUnix {
    
    // intentionally left blank
}
```

Spring will evaluate this meta annotation when we annotate a bean with our new annotation:

```java
@Bean
@ConditionalOnUnix
LinuxBean linuxBean(){
    return new LinuxBean();
}
```

### What is applicationContext.xml file?

`applicationContext.xml` is effectively the same thing as `web.xml`. This file should be placed as
`/WEB-INF/applicationContext.xml` under `resource` directory.

Spring 3 MVC accessing HttpRequest from controller

Spring MVC will give you the HttpRequest if you just add it to your controller method signature:

For instance:

```java
/**
 * Generate a PDF report...
 */
@RequestMapping(value = "/report/{objectId}", method = RequestMethod.GET)
public @ResponseBody void generateReport(
        @PathVariable("objectId") Long objectId,
        HttpServletRequest request,
        HttpServletResponse response
) {

    // ...
    // Here you can use the request and response objects like:
    // response.setContentType("application/pdf");
    // response.getOutputStream().write(...);

}
```

As you see, simply adding the `HttpServletRequest` and `HttpServletResponse` objects to the signature makes Spring
MVC to pass those objects to your controller method. You'll want the `HttpSession` object too.

It is strongly recommended to have a look at the list of supported arguments that Spring MVC is able to auto-magically
inject to your handler methods:
https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments

### Logging

#### Use Default Logging Pattern

Once you have included the default configuration, you can use its values in your own logback-spring.xml configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
    <!-- use Spring default values -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>
    …
</configuration>
```

### @Scheduled

#### Enable Support for Scheduling

To enable support for scheduling tasks and the @Scheduled annotation in Spring, we can use the Java enable-style
annotation:

```java
@Configuration
@EnableScheduling
public class SpringConfig {
    ...
}
```

#### Schedule a Task at Fixed Delay

Let's start by configuring a task to run after a fixed delay:

```java
@Scheduled(fixedDelay = 1000)
public void scheduleFixedDelayTask() {
    System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
}
```

In this case, the duration between the end of the last execution and the start of the next execution is fixed. **The
task always waits until the previous one is finished**.

#### Schedule a Task at a Fixed Rate

Let's now execute a task at a fixed interval of time:

```java
@Scheduled(fixedRate = 1000)
public void scheduleFixedRateTask() {
    System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
}
```

This option should be used when each execution of the task is independent.

Note that scheduled tasks don't run in parallel by default. So even if we used fixedRate, the next task won't be invoked
until the previous one is done.

**If we want to support parallel behavior in scheduled tasks, we need to add the `@Async` annotation**:

```java
@EnableAsync
public class ScheduledFixedRateExample {
    
    @Async
    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTaskAsync() throws InterruptedException {
        System.out.println("Fixed rate task async - " + System.currentTimeMillis() / 1000);
        Thread.sleep(2000);
    }

}
```

Now this asynchronous task will be invoked each second, even if the previous task isn't done.

#### Schedule a Task With Initial Delay

Next, let's schedule a task with a delay (in milliseconds):

```java
@Scheduled(fixedDelay = 1000, initialDelay = 1000)
public void scheduleFixedRateWithInitialDelayTask() {
 
    long now = System.currentTimeMillis() / 1000;
    System.out.println("Fixed rate task with one second initial delay - " + now);
}
```

This option is convenient when the task has a setup that needs to be completed.

#### Schedule a Task Using Cron Expressions

Sometimes delays and rates are not enough, and we need the flexibility of a cron expression to control the schedule of
our tasks:

```java
@Scheduled(cron = "0 15 10 15 * ?")
public void scheduleTaskUsingCronExpression() {
 
    long now = System.currentTimeMillis() / 1000;
    System.out.println("schedule tasks using cron jobs - " + now);
}
```

Note that in this example, we're scheduling a task to be executed at 10:15 AM on the 15th day of every month.

By default, Spring will use the server's local time zone for the cron expression. However, we can use the zone attribute
to change this timezone:

```java
@Scheduled(cron = "0 15 10 15 * ?", zone = "Europe/Paris")
```

#### Parameterizing the Schedule

Hardcoding these schedules is simple, but we usually need to be able to control the schedule without re-compiling and
re-deploying the entire app.

We'll make use of Spring Expressions to externalize the configuration of the tasks, and we'll store these in properties
files.

* A fixedDelay task:

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")

* A fixedRate task:

    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")

* A cron expression based task:

    @Scheduled(cron = "${cron.expression}")

#### Setting Delay or Rate Dynamically at Runtime

Normally, all the properties of the `@Scheduled` annotation are resolved and initialized only once at Spring context
startup. Therefore, it is not possbile to change the fixedDelay or fixedRate values at runtime when we use `@Scheduled`
annotation in Spring.

There is, however, a workaround. Using Spring's `SchedulingConfigurer` provides a more customizable way to give us the
opportunity of setting the delay or rate dynamically.

```java
@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {
    
    private final TickService tickService;

    @Autowired
    public DynamicSchedulingConfig(TickService tickService) {
        this.tickService = tickService;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        
        taskRegistrar.addTriggerTask(
          new Runnable() {
              @Override
              public void run() {
                  tickService.tick();
              }
          },
          new Trigger() {
              @Override
              public Date nextExecutionTime(TriggerContext context) {
                  Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
                  Instant nextExecutionTime = lastCompletionTime
                          .orElseGet(Date::new).toInstant()
                          .plusMillis(tickService.getDelay());
                  return Date.from(nextExecutionTime);
              }
          }
        );
    }
}
```

Note that we annotate our `DynamicSchedulingConfig` with `@EnableScheduling` to make the scheduling work.

As a result, we scheduled the `TickService#tick` method to run it after each amount of delay, which is determined
dynamically at runtime by the `getDelay()` method.

### How to Specify Validation Group for @Valid?

Suppose we have parameters in a controller `@RequestMapping` method:

```java
@ModelAttribute("myCandidate") @Valid Candidate myCandidate,
BindingResult result
```

How do we explicit specify validation group for `myCandidate`?

The standard java `@Valid` annotation doesn't support groups. However, Spring now includes an **`@Validated`**
annotation which does the same job as `@Valid`, but allows you to specify which groups to use in the validation:

```java
@ModelAttribute("myCandidate") @Validated(UpdateGroup.class) Candidate myCandidate
```

Note that this annotation is only available in Spring 3.1 and newer.

## Spring Data JPA

### Understanding Transaction in Spring

Spring supports RDMBS transations by 2 approaches:

1. **Programmatic Transactions** Transaction management code needs to be explicitly written so as to commit when
   everything is successful and rolling back if anything goes wrong. The transaction management code is tightly bound to
   the business logic in this case.
2. **Declarative Transactions** Transaction management is separate from business logic. Spring supports declarative
   transactions using transaction advice (using AOP) via XML configuration in the spring context or with
   [`@Transactional` annotation](#transactional-annotation)

#### @Transactional annotation

At a high level, when a class declares `@Transactional` on itself or its members, Spring creates a proxy that implements
the same interface(s) as the class you're annotating. In other words, Spring wraps the bean in the proxy and the bean
itself has no knowledge of it. A proxy provides a way for Spring to inject behaviors before, after, or around method
calls into the object being proxied.

The generated proxy object is supplied with a "**TransactionInterceptor**", which is created by Spring. So when the
`@Transactional` method is called from client code, the `TransactionInterceptor` gets invoked first from the proxy
object, which begins the transaction and eventually invokes the method on the target bean. When the invocation finishes,
the TransactionInterceptor **commits/rolls back** the transaction accordingly.

> ⚠️ Note that **only calls from "outside" the target bean go through the proxy**.

##### Understanding "readOnly" And "propagate" Attributes


###### "readOnly"

If you don't explicitly set `readOnly` attribute to `true`, you will have read/write transactions.

> 📋 It is always better to explicitly specify the `readOnly` attribute, as we have noticed some massive performance
> improvements with Hibernate because of this.

###### "propagate"

Spring allows you to control the behavior of logical and physical transactions via transaction propagation mechanisms.
There are **seven** types of transaction propagation mechanisms that you can set in a Spring application via
`org.springframework.transaction.annotation.Propagation`

By default, the only exceptions that cause a transaction to roll back are the unchecked exceptions (like
`RuntimeException`). Nevertheless, you can control this aspect via the `noRollbackFor`, `noRollbackForClassName`,
`rollbackFor`, and `rollbackForClassName` elements of `@Transactional`.

1. **Propagation.REQUIRED** The default setting of a `@Transactional` annotation. The `REQUIRED` propagation can be
   interpreted as follows:

    - If there is no existing physical transaction, then the Spring container will create one.
    - If there is an existing physical transaction, then the methods annotated with `REQUIRE` will participate in this
      physical transaction.
    - Each method annotated with REQUIRED demarcates a logical transaction and these logical transactions participate
      in the same physical transaction.
    - Each logical transaction has its own scope, but, in case of this propagation mechanism, all these scopes are
      mapped to the same physical transaction.

   Because all the scopes of the logical transactions are mapped to the same physical transaction, when one of these logical transactions is rolled back, all the logical transactions of the current physical transaction are rolled back.
2. **Propagation.REQUIRES_NEW** instructs the Spring container to always create a new physical transaction. Such
   transactions can also declare their own timeouts, read-only, and isolation level settings and not inherit an outer
   physical transaction's characteristics.

   Pay attention to how you handle this aspect since each physical transaction needs its own database connection. So, an
   outer physical transaction will have its own database connection, while `REQUIRES_NEW` will create the inner physical
   transaction and will bound a new database connection to it. In a synchronous execution, while the inner physical
   transaction is running, the outer physical transaction is suspended and its database connection remains open. After
   the inner physical transaction commits, the outer physical transaction is resumed, continuing to run and
   commit/rollback.

   If the inner physical transaction is rolled back, it may or may not affect the outer physical transaction.
3. **Propagation.NESTED** acts like `REQUIRED`, only it uses savepoints between nested invocations. In other words,
   inner logical transactions may roll back independently of outer logical transactions.

   > ⚠️ Trying to use `NESTED` with Hibernate JPA will result in a Spring exception as follows:
   >
   > ```
   > NestedTransactionNotSupportedException: JpaDialect does not support savepoints
   > - check your JPA provider capabilities
   > ```
   >
   > This is happening because Hibernate JPA doesn’t support nested transactions.
4. **Propagation.MANDATORY** requires an existing physical transaction or will cause an exception
5. **Propagation.NEVER** states that no physical transaction should exist. If a physical transaction is found, then
   `NEVER` will cause an exception. The code inside `NEVER`, though, can open physical transactions with no problem.
6. **Propagation.NOT_SUPPORTED** states that if a physical transaction exists, then it will be suspended before
   continuing. This physical transaction will be automatically resumed at the end. After this transaction is resumed,
   it can be rolled back (in case of a failure) or committed. If a contained transactions thows `RuntimeException`, the
   exception will propagate to this transaction and this logical transaction is rolled back
7. **Propagation.SUPPORTS** states that if a physical transaction exists, then it will execute the demarcated method as
   a logical transaction in the context of this physical transaction(i.e. rolling back comes together). Otherwise, it
   will execute this method outside of a physical transaction
   
### Interface-based Projection

Suppose we need to need to search for a person's profile as well as the number of vehicles that person owns. Those
information would normally be kept in 2 tables, A "Person" table and a "Vehicle" table. A JOIN would be required. We
could

* Issue a single query and parse the *un-typed* result in memory, or
* Issue 2 queries to the "Person" and "Vehicle" tables and JOIN the *typed* query results in memory

The trade-off between type-safe and single-roundtrip query is obvious here. Is there an approach that allows us to
obtain typed query result in a single roundtrip? Yes, and the answer is interface-based projection:

#### Step 1 - Define Custom DB Query Result Type

```java
public class PersonWithNumVehicles {

    Long getSsn();
    Date getBirthDate();
    int getVehicleCount();
}
```

#### Step 2 - Write Annotated Spring Data JPA Query

```java
@NotNull
@Query(
        value = "SELECT person.ssn AS ssn, "
                    + "person.name AS name, "
                    + "person.birthDate AS birthDate, "
                    + "COUNT(Vehicle.vinNumber) AS vehicleCount "
                + "FROM Person person, "
                    + "Vehicle vehicle "
                + "WHERE person.ssn = vehicle.ownerId AND "
                    + "person.ssn = 345215674 AND "
                + "GROUP BY person.ssn"
)
PersonWithNumVehicles getPersonWithVehicleInfoBySsn(
        @NotNull @Param("ssn") Long ssn,
        @NotNull Pageable pagination
);
```

> 📋 Note that `person` is the physical table name.

> ⚠️ The return type of `PersonWithNumVehicles.getVehicleCount()` must be `int` because it is calculated using SQL
`COUNT()` function. Using other types, such as `Long` will silently fail the query.

> ⚠️ The projection aliases (`AS ssn`, `AS name`, ..) are required in order to map the columns from DB results to the
> corresponding getters of the projection bean

### [@Query](https://www.baeldung.com/spring-data-jpa-query)

Spring Data provides many ways to define a query that we can execute. One of these is the` @Query` annotation. In this
section, we'll demonstrate how to use the `@Query` annotation in Spring Data JPA to execute both JPQL and native SQL
queries. We'll also show how to build a dynamic query when the @Query annotation is not enough.
         
In order to define SQL to execute for a Spring Data repository method, we can annotate the method with the `@Query`
annotation - its `value` attribute contains the JPQL or SQL to execute.

The `@Query` annotation takes precedence over named queries, which are annotated with `@NamedQuery` or defined in an
`orm.xml` file. (It's a good approach to place a query definition just above the method inside the repository rather
than inside our domain model as named queries. The repository is responsible for persistence, so it's a better place to
store these definitions.)

#### JPQL

**By default, the query definition uses JPQL.**

Let's look at a simple repository method that returns active User entities from the database:

```java
@Query("SELECT u FROM User u WHERE u.status = 1")
Collection<User> findAllActiveUsers();
```

#### Native

We can use also native SQL to define our query. All we have to do is set the value of the nativeQuery attribute to true
and define the native SQL query in the value attribute of the annotation:

```java
@Query(
        nativeQuery = true,
        value = "SELECT * FROM USERS u WHERE u.status = 1"
)
Collection<User> findAllActiveUsersNative();
```

#### Define Order in a Query

We can pass an additional parameter of type `Sort` to a Spring Data method declaration that has the `@Query` annotation.
It'll be translated into the ORDER BY clause that gets passed to the database.

##### Sorting for JPA Provided and Derived Methods

For the methods we get out of the box such as `findAll(Sort)` or the ones that are generated by parsing method
signatures, we can only use object properties to define our sort:

```java
userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
```

Now imagine that we want to sort by the length of a name property:

```java
userRepository.findAll(Sort.by("LENGTH(name)"));
```

When we execute the above code, we'll receive an exception:

```
org.springframework.data.mapping.PropertyReferenceException: No property LENGTH(name) found for type User!
```

##### JPQL

When we use JPQL for a query definition, then Spring Data can handle sorting without any problem - all we have to do is
add a method parameter of type Sort:

```java
@Query(value = "SELECT user FROM User user")
List<User> findAllUsers(Sort sort);
```

We can call this method and pass a `Sort` parameter, which will order the result by the name property of the `User`
object:

```java
userRepository.findAllUsers(Sort.by("name"));
```

And because we used the `@Query` annotation, we can use the same method to get the sorted list of `Users` by the length
of their names:

```
userRepository.findAllUsers(JpaSort.unsafe("LENGTH(name)"));
```

> ⚠️ It's crucial that we use `JpaSort.unsafe()` to create a `Sort` object instance. When we use
> `Sort.by("LENGTH(name)");` then we'll receive exactly the same exception as we saw above for the `findAll()` method.
> When Spring Data discovers the unsafe `Sort` order for a method that uses the `@Query` annotation, then it just
> appends the sort clause to the query - it skips checking whether the property to sort by belongs to the domain model.
    
##### Native

When the `@Query` annotation uses native SQL, then it's not possible to define a `Sort`. If we do, we'll receive an
exception:

```
org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException: 
Cannot use native queries with dynamic sorting and/or pagination
```

As the exception says, the sort isn't supported for native queries. The error message gives us a hint that pagination
will cause an exception too.                                                                              

However, there is a [workaround that enables pagination](#native-2)

#### Pagination

Pagination allows us to return just a subset of a whole result in a Page. This is useful, for example, when navigating
through several pages of data on a web page.

Another advantage of pagination is that the amount of data sent from server to client is minimized. By sending smaller
pieces of data, we can generally see an improvement in performance.

##### JPQL

Using pagination in the JPQL query definition is straightforward:

```java
@Query(value = "SELECT u FROM User u ORDER BY id")
Page<User> findAllUsersWithPagination(Pageable pageable);
```

We can pass a `PageRequest` parameter to get a page of data.

Pagination is also supported for native queries but requires a little bit of additional work.

##### Native

We can enable pagination for native queries by declaring an additional attribute `countQuery`.

This defines the SQL to execute to count the number of rows in the whole result:

```java
@Query(
    value = "SELECT * FROM Users ORDER BY id", 
    countQuery = "SELECT count(*) FROM Users", 
    nativeQuery = true
)
Page<User> findAllUsersWithPagination(Pageable pageable);
```

#### Query Parameters

##### JPQL

We use the `@Param` annotation in the method declaration to match parameters defined by name in JPQL with parameters
from the method declaration:

```java
@Query("SELECT user FROM User user WHERE user.status = :status and user.name = :name")
User findUserByStatusAndNameNamedParams(
    @Param("status") Integer status, 
    @Param("name") String name
);
```

Note that in the above example, we defined our SQL query and method parameters to have the same names, but it's not
required as long as the value strings are the same:

```java
@Query("SELECT user FROM User user WHERE user.status = :status and user.name = :name")
User findUserByUserStatusAndUserName(
    @Param("status") Integer userStatus, 
    @Param("name") String userName
);
```

> ⚠️ When parameter is used for defining `LIKE` constraint, the string quoting, i.e. `'...'`, transforms the parameter
> value to a plane parameter name, which is incorrect. For example
> 
> `SELECT user FROM User user WHERE user.name LIKE '%:searchKey%'` is in correct as it will search for names containing the
> plane string ":searchKey".
> 
> Instead, use `LIKE CONCAT('%', :searchKey, '%')`, i.e.
> 
> `SELECT user FROM User user WHERE user.name LIKE CONCAT('%', :searchKey, '%')`

##### Native

For the native query definition, there is no difference in how we pass a parameter via the name to the query in
comparison to JPQL - we use the @Param annotation:

```java
@Query(value = "SELECT * FROM Users u WHERE u.status = :status and u.name = :name", 
  nativeQuery = true)
User findUserByStatusAndNameNamedParamsNative(
  @Param("status") Integer status, @Param("name") String name);
```

#### Collection Parameter

Let's consider the case when the where clause of our JPQL or SQL query contains the `IN` (or `NOT IN`) keyword:

```java
SELECT u FROM User u WHERE u.name IN :names
```

In this case, we can define a query method that takes Collection as a parameter:

```java
@Query(value = "SELECT u FROM User u WHERE u.name IN :names")
List<User> findUserByNameList(@Param("names") Collection<String> names);
```

As the parameter is a Collection, it can be used with List, HashSet, etc.

#### Update Queries With @Modifying

We can use the `@Query` annotation to modify the state of the database by also adding the `@Modifying` annotation to the
repository method.

##### JPQL

The repository method that modifies the data has two differences in comparison to the select query - it has the
`@Modifying` annotation and, of course, the JPQL query uses update instead of select:

```java
@Modifying
@Query("update User u set u.status = :status where u.name = :name")
int updateUserSetStatusForName(
    @Param("status") Integer status, 
    @Param("name") String name
);
```

The return value defines how many rows the execution of the query updated. Both indexed and named parameters can be used
inside update queries.

##### Inserts

To perform an insert operation, we have to both apply `@Modifying` and use a **native query** since `INSERT` is not a
part of the JPA interface:

```java
@Modifying
@Query(
    value = "insert into Users (name, age, email, status) values (:name, :age, :email, :status)",
    nativeQuery = true
)
void insertUser(
    @Param("name") String name,
    @Param("age") Integer age, 
    @Param("status") Integer status,
    @Param("email") String email);
```

### Pagination

https://stackoverflow.com/a/47616648
[Pageable](https://www.logicbig.com/tutorials/spring-framework/spring-data/web-support-with-pageable-argument-resolver.html)

#### Generate a Creation Date-Time Automatically

##### JPA

There isn't anything as convenient as annotating the Timestamp field directly, but you could use the `@PrePersist`,
`@PreUpdate` annotations and with little effort achieve the same results.

##### Hibernate

[`@CreationTimestamp`](http://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#mapping-generated-CreationTimestamp)
[`@UpdateTimestamp`](http://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#mapping-generated-UpdateTimestamp)

##### Spring Data JPA

[`@CreatedDate`](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing.annotations)
[`@LastModifiedDate`](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing.annotations)

### [Map the Native Query Result to Non-Entity POJO](https://stackoverflow.com/a/48296588)

Let's say we have a TODO app with a database that keeps information about TODO task (`todo_task` table) and person
(`person`). We would like to get a list of Person's ongoing TODO's; the results should have at least 2 columns - task
name and person's name. We could use the following query:

```
SELECT todo_task.id, todo_task.task_name, person.name
FROM todo_task
LEFT JOIN person ON todo_task.person_id = person.id
WHERE todo_task.task_status NOT IN ('DONE', 'TO DO')
```

We notice that the result comes with 3 columns that map to 2 separate JPA entities, i.e. `todo_task` & `person`

In order to map the database result to a strongly typed programmable object, the easiest way is to use so called
[projection](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections). It can map query results
to interfaces. Using `SqlResultSetMapping` is inconvienient and makes your code ugly :).

```java
@Query(
        nativeQuery = true,
        value = "SELECT todo_task.id AS id, todo_task.task_name AS taskName, person.name AS name " +
                "FROM todo_task " +
                "LEFT JOIN person ON todo_task.person_id = person.id " +
                "WHERE todo_task.task_status NOT IN ('Finished', 'Canceled')"
)
@NotNull
List<Task> getAllTasks(@NotNull Pageable pagination);
```

```java
public interface Task {

    Long getId();

    String getTaskName();
    
    String getName();
}
```

> ⚠️ If you use `SELECT table.column ...` notation always define aliases matching names from entity(`Task`). For example
> this won't work properly (projection will return nulls(`NullPointerException: null` at runtime) for each getter):
> `SELECT todo_task.id, todo_task.task_name, person.name FROM ...` But this works fine:
> `SELECT todo_task.id AS id, todo_task.task_name AS taskName, person.name AS name FROM ...

### Delete Records Older than a Given Date

For example, if you have following Entity and if you want to delete all the comments older than 7 year, we could make
it happen like this

```java
package com.kalliphant.samples.springdata.jpa;
 
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
 
public interface CommentRepository extends CrudRepository<Comment , Long> {
 
       /**
        * This methods deletes all the records whose 'createdOn' date is less than 'expiryDate'
        */
       @Modifying
       @Transactional // Make sure to import org.springframework.transaction.annotation.Transactional
       public void deleteByCreatedOnBefore(Date expiryDate);
}
```

#### Query Creation

Generally, the query creation mechanism for JPA works as described in
[Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods). The
following example shows what a JPA query method translates into:

```java
public interface UserRepository extends Repository<User, Long> {

    List<User> findByEmailAddressAndLastname(String emailAddress, String lastname);
}
```

We create a query using the JPA criteria API from this, but, essentially, this translates into the following query

```sql
SELECT user
FROM User user
where user.emailAddress = ?1 and user.lastname = ?2
```

Spring Data JPA does a property check and traverses nested properties, as described in
[Property Expressions](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-property-expressions).

The following table describes the keywords supported for JPA and what a method containing that keyword translates to:

| Keyword            | Example                                                   | JPQL snippet                                                              |
|--------------------|-----------------------------------------------------------|---------------------------------------------------------------------------|
| Distinct           | findDistinctByLastnameAndFirstname                        | `select distinct … where x.lastname = ?1 and x.firstname = ?2`            |
| And                | findByLastnameAndFirstname                                | `… where x.lastname = ?1 and x.firstname = ?2`                            |
| Or                 | findByLastnameOrFirstname                                 | `… where x.lastname = ?1 or x.firstname = ?2`                             |
| Is, Equals         | findByFirstname, findByFirstnameIs, findByFirstnameEquals | `… where x.firstname = ?1`                                                |
| Between            | findByStartDateBetween                                    | `… where x.startDate between ?1 and ?2`                                   |
| LessThan           | findByAgeLessThan                                         | `… where x.age < ?1`                                                      |
| LessThanEqual      | findByAgeLessThanEqual                                    | `… where x.age <= ?1`                                                     |
| GreaterThan        | findByAgeGreaterThan                                      | `… where x.age > ?1`                                                      |
| GreaterThanEqual   | findByAgeGreaterThanEqual                                 | `… where x.age >= ?1`                                                     |
| After              | findByStartDateAfter                                      | `… where x.startDate > ?1`                                                |
| Before             | findByStartDateBefore                                     | `… where x.startDate < ?1`                                                |
| IsNull, Null       | findByAge(Is)Null                                         | `… where x.age is null`                                                   |
| IsNotNull, NotNull | findByAge(Is)NotNull                                      | `… where x.age not null`                                                  |
| Like               | findByFirstnameLike                                       | `… where x.firstname like ?1`                                             |
| NotLike            | findByFirstnameNotLike                                    | `… where x.firstname not like ?1`                                         |
| StartingWith       | findByFirstnameStartingWith                               | `… where x.firstname like ?1` (**parameter bound with appended `%`**)     |
| EndingWith         | findByFirstnameEndingWith                                 | `… where x.firstname like ?1` (** (parameter bound with prepended `%`)**) |
| Containing         | findByFirstnameContaining                                 | `… where x.firstname like ?1` (**parameter bound wrapped in `%`**)        |
| OrderBy            | findByAgeOrderByLastnameDesc                              | `… where x.age = ?1 order by x.lastname desc`                             |
| Not                | findByLastnameNot                                         | `… where x.lastname <> ?1`                                                |
| In                 | findByAgeIn(Collection<Age> ages)                         | `… where x.age in ?1`                                                     |
| NotIn              | findByAgeNotIn(Collection<Age> ages)                      | `… where x.age not in ?1`                                                 |
| True               | findByActiveTrue()                                        | `… where x.active = true`                                                 |
| False              | findByActiveFalse()                                       | `… where x.active = false`                                                |
| IgnoreCase         | findByFirstnameIgnoreCase                                 | … where UPPER(x.firstname) = UPPER(?1)                                    |

> 📋 `In` and `NotIn` also take any subclass of `Collection` as a parameter as well as arrays or varargs. For other
> syntactical versions of the same logical operator, check
> [Repository query keywords](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords).

## Testing

### Testing MVC Web Controllers with Spring Boot and @WebMvcTest

#### Dependencies

We're going to use [JUnit Jupiter](https://junit.org/junit5/) (JUnit 5) as the testing framework, Mockito for mocking,
AssertJ for creating assertions and Lombok to reduce boilerplate code:

```
dependencies {
  compile('org.springframework.boot:spring-boot-starter-web')
  compileOnly('org.projectlombok:lombok')
  testCompile('org.springframework.boot:spring-boot-starter-test')
  testCompile 'org.junit.jupiter:junit-jupiter-engine:5.2.0'
  testCompile('org.mockito:mockito-junit-jupiter:2.23.0')
}
```

AssertJ and Mockito automatically come with the dependency to `spring-boot-starter-test`.

#### Responsibilities of a Web Controller

Let's start by looking at a typical REST controller:

```java
@RestController
@RequiredArgsConstructor
class RegisterRestController {
    
    private final RegisterUseCase registerUseCase;

    @PostMapping("/forums/{forumId}/register")
    UserResource register(
            @PathVariable("forumId") Long forumId,
            @Valid @RequestBody UserResource userResource,
            @RequestParam("sendWelcomeMail") boolean sendWelcomeMail
    ) {
      User user = new User(userResource.getName(), userResource.getEmail());
      Long userId = registerUseCase.registerUser(user, sendWelcomeMail);

      return new UserResource(userId, user.getName(), user.getEmail());
    }
}
```

The controller method is annotated with `@PostMapping` to define the URL, HTTP method and content type it should listen
to.

It takes input via parameters annotated with `@PathVariable`, `@RequestBody`, and `@RequestParam` which are
automatically filled from the incoming HTTP request.

Parameters my be annotated with `@Valid` to indicate that Spring should perform
[bean validation](https://reflectoring.io/bean-validation-with-spring-boot/) on them.

The controller then works with those parameters, calling the business logic before returning a plain Java object, which
is automatically mapped into JSON and written into the HTTP response body by default.

There's a lot of Spring magic going on here. In summary, for each request, a controller usually does the following
steps:

| # | Responsibility          | Description                                                                                                                                                                                      |
|---|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | Listen to HTTP Requests | The controller should respond to certain URLs, HTTP methods and content types.                                                                                                                   |
| 2 | Deserialize Input       | The controller should parse the incoming HTTP request and create Java objects from variables in the URL, HTTP request parameters and the request body so that we can work with them in the code. |
| 3 | Validate Input          | The controller is the first line of defense against bad input, so it’s a place where we can validate the input.                                                                                  |
| 4 | Call the Business Logic | Having parsed the input, the controller must transform the input into the model expected by the business logic and pass it on to the business logic.                                             |
| 5 | Serialize the Output    | The controller takes the output of the business logic and serializes it into an HTTP response.                                                                                                   |
| 6 | Translate Exceptions    | If an exception occurs somewhere on the way, the controller should translate it into a meaningful error message and HTTP status for the user.                                                    |

A controller apparently has a lot to do. We should take care not to add even more responsibilities like performing
business logic. Otherwise, our controller tests will become fat and unmaintainable.

How are we going to write meaningful tests that cover all of those responsibilities?

#### Unit or Integration Test?

Do we write unit tests? Or integration tests? What’s the difference, anyways? Let's discuss both approaches and decide
for one.

In a unit test, we would test the controller in isolation. That means we would instantiate a controller object, [mocking
away the business logic](https://reflectoring.io/unit-testing-spring-boot/#using-mockito-to-mock-dependencies), and then
call the controller's methods and verify the response.

Would that work in our case? Let's check which of the 6 responsibilities we have identified above we can cover in an
isolated unit test:

| # | Responsibility          | Covered in a Unit Test?                                                                                                                                                                                 |
|---|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | Listen to HTTP Requests | ❌ No, because the unit test would not evaluate the `@PostMapping` annotation and similar annotations specifying the properties of a HTTP request.                                                         |
| 2 | Deserialize Input       | ❌ No, because annotations like `@RequestParam` and `@PathVariable` would not be evaluated. Instead we would provide the input as Java objects, effectively skipping deserialization from an HTTP request. |
| 3 | Validate Input          | ❌ Not when depending on bean validation, because the `@Valid` annotation would not be evaluated.                                                                                                          |
| 4 | Call the Business Logic | ✅ Yes, because we can verify if the mocked business logic has been called with the expected arguments.                                                                                                    |
| 5 | Serialize the Output    | ❌ No, because we can only verify the Java version of the output, and not the HTTP response that would be generated.                                                                                       |
| 6 | Translate Exceptions    | ❌ No. We could check if a certain exception was raised, but not that it was translated to a certain JSON response or HTTP status code.                                                                    |

In summary, **a simple unit test will not cover the HTTP layer**. So, we need to introduce Spring to our test to do the
HTTP magic for us. Thus, we're building an integration test that tests the integration between our controller code and
the components Spring provides for HTTP support.

An integration test with Spring fires up a Spring application context that contains all the beans we need. This includes
framework beans that are responsible for listening to certain URLs, serializing and deserializing to and from JSON and
translating exceptions to HTTP. These beans will evaluate the annotations that would be ignored by a simple unit test.

So, how do we do it?

#### Verifying Controller Responsibilities with @WebMvcTest

Spring Boot provides the `@WebMvcTest` annotation to fire up an application context that contains only the beans needed
for testing a web controller:

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RegisterRestController.class)
class RegisterRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUseCase registerUseCase;

    @Test
    void whenValidInput_thenReturns200() throws Exception {
      mockMvc.perform(...);
    }
}
```

> 📋 `@ExtendWith`
>
> The @ExtendWith annotation to tells JUnit 5 to enable Spring support. As of Spring Boot 2.1, we no longer need to load
> the SpringExtension because it's included as a meta annotation in the Spring Boot test annotations like
> `@DataJpaTest`, `@WebMvcTest`, and `@SpringBootTest`.

We can now `@Autowire` all the beans we need from the application context. Spring Boot automatically provides beans
like an `ObjectMapper` to map to and from JSON and a `MockMvc` instance to simulate HTTP requests.

We use [`@MockBean`](https://reflectoring.io/spring-boot-mock/) to mock away the business logic, since we don't want to test integration between controller and
business logic, but between controller and the HTTP layer. `@MockBean` automatically replaces the bean of the same type
in the application context with a Mockito mock.

> **Use `@WebMvcTest` with or without the controllers parameter?**
> 
> By setting the controllers parameter to RegisterRestController.class in the example above, we're telling Spring Boot
> to restrict the application context created for this test to the given controller bean and some framework beans needed
> for Spring Web MVC. All other beans we might need have to be included separately or mocked away with `@MockBean`.

Let's go through each of the responsibilities and see how we can use MockMvc to verify each of them in order build the
best integration test we can.

##### 1. Verifying HTTP Request Matching

Verifying that a controller listens to a certain HTTP request is pretty straightforward. We simply call the `perform()`
method of `MockMvc` and provide the URL we want to test:

```java
mockMvc.perform(post("/forums/42/register")
        .contentType("application/json"))
        .andExpect(status().isOk());
```

Aside from verifying that the controller responds to a certain URL, this test also verifies the correct HTTP method
(POST in our case) and the correct request content type. The controller we have seen above would reject any requests
with a different HTTP method or content type.

Note that this test would still fail, yet, since our controller expects some input parameters.

More options to match HTTP requests can be found in the Javadoc of
[MockHttpServletRequestBuilder](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/web/servlet/request/MockHttpServletRequestBuilder.html).

##### 2. Verifying Input Serialization

To verify that the input is successfully serialized into Java objects, we have to provide it in the test request. Input
can be either the JSON content of the request body (`@RequestBody`), a variable within the URL path (`@PathVariable`),
or an HTTP request parameter (`@RequestParam`):

```java
@Test
void whenValidInput_thenReturns200() throws Exception {
    UserResource user = new UserResource("Zaphod", "zaphod@galaxy.net");

    mockMvc.perform(post("/forums/{forumId}/register", 42L)
            .contentType("application/json")
            .param("sendWelcomeMail", "true")
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());
}
```

We now provide the path variable `forumId`, the request parameter `sendWelcomeMail` and the request body that are
expected by the controller. The request body is generated using the `ObjectMapper` provided by Spring Boot, serializing
a `UserResource` object to a JSON string.

If the test is green, we now know that the controller's `register()` method has received those parameters as Java
objects and that they have been successfully parsed from the HTTP request.

##### 3. Verifying Input Validation

Let's say the `UserResource` uses the `@NotNull` annotation to deny null values:

```java
@Value
public class UserResource {

    @NotNull
    private final String name;

    @NotNull
    private final String email;
}
```

Bean validation is triggered automatically when we
[add the @Valid annotation to a method parameter](https://reflectoring.io/bean-validation-with-spring-boot/#validating-input-to-a-spring-mvc-controller)
like we did with the userResource parameter in our controller. So, for the happy path (i.e. when the validation
succeeds), the test we created in the previous section is enough.

If we want to test if the validation fails as expected, we need to add a test case in which we send an invalid
`UserResource` JSON object to the controller. We then expect the controller to return HTTP status 400 (Bad Request):

```java
@Test
void whenNullValue_thenReturns400() throws Exception {
    UserResource user = new UserResource(null, "zaphod@galaxy.net");
  
    mockMvc.perform(post("/forums/{forumId}/register", 42L)
            ...
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest());
}
```

Depending on how important the validation is for the application, we might add a test case like this for each invalid
value that is possible. This can quickly add up to a lot of test cases, though, so you should talk to your team about
how you want to handle validation tests in your project.

##### 4. Verifying Business Logic Calls

Next, we want to verify that the business logic is called as expected. In our case, the business logic is provided by
the `RegisterUseCase` interface and expects a `User` object and a `boolean` as input:

```java
interface RegisterUseCase {
    
    Long registerUser(User user, boolean sendWelcomeMail);
}
```

We expect the controller to transform the incoming `UserResource` object into a `User` and to pass this object into the
`registerUser()` method.

```java
@Test
void whenValidInput_thenMapsToBusinessModel() throws Exception {
    UserResource user = new UserResource("Zaphod", "zaphod@galaxy.net");
    mockMvc.perform(...);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(registerUseCase, times(1)).registerUser(userCaptor.capture(), eq(true));
    assertThat(userCaptor.getValue().getName()).isEqualTo("Zaphod");
    assertThat(userCaptor.getValue().getEmail()).isEqualTo("zaphod@galaxy.net");
}
```

After the call to the controller has been performed, we use an `ArgumentCaptor` to capture the `User` object that was
passed to the `RegisterUseCase.registerUser()` and assert that it contains the expected values.

The `verify` call checks that `registerUser()` has been called exactly once.

>  📋️ If we do a lot of assertions on `User` objects, we can create
> [our own custom Mockito assertion methods](https://reflectoring.io/unit-testing-spring-boot/#creating-readable-assertions-with-assertj)
> for better readability.

##### 5. Verifying Output Serialization

After the business logic has been called, we expect the controller to map the result into a JSON string and include it
in the HTTP response. In our case, we expect the HTTP response body to contain a valid `UserResource` object in JSON
form:

```java
@Test
void whenValidInput_thenReturnsUserResource() throws Exception {
    MvcResult mvcResult = mockMvc.perform(...)
            ...
            .andReturn();

    UserResource expectedResponseBody = ...;
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expectedResponseBody));
}
```

To do assertions on the response body, we need to store the result of the HTTP interaction in a variable of type
`MvcResult` using the `andReturn()` method.

We can then read the JSON string from the response body and compare it to the expected string using
`isEqualToIgnoringWhitespace()`. We can build the expected JSON string from a Java object using the `ObjectMapper`
provided by Spring Boot.

##### 6. Verifying Exception Handling

Usually, if an exception occurs, the controller should return a certain HTTP status. 400, if something is wrong with the
request, 500, if an exception bubbles up, and so on.

Spring takes care of most of these cases by default. However, if we have a custom exception handling, we want to test
it. Let's say we want to return a structured JSON error response with a field name and error message for each field that
was invalid in the request. We’d create a `@ControllerAdvice` like this:

```java
@ControllerAdvice
class ControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ErrorResult errorResult = new ErrorResult();
    
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorResult
                    .getFieldErrors()
                    .add(new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
    
        return errorResult;
    }

    @Getter
    @NoArgsConstructor
    static class ErrorResult {
        
        private final List<FieldValidationError> fieldErrors = new ArrayList<>();
        
        ErrorResult(String field, String message) {
            this.fieldErrors.add(new FieldValidationError(field, message));
        }
    }

    @Getter
    @AllArgsConstructor
    static class FieldValidationError {
        
        private String field;
        private String message;
    }
  
}
```

If bean validation fails, Spring throws an `MethodArgumentNotValidException`. We handle this exception by mapping
Spring's `FieldError` objects into our own `ErrorResult` data structure. The exception handler causes all controllers to
return HTTP status 400 in this case and puts the `ErrorResult` object into the response body as a JSON string.

To verify that this actually happens, we expand on our earlier test for failing validations:

```java
@Test
void whenNullValue_thenReturns400AndErrorResult() throws Exception {
    UserResource user = new UserResource(null, "zaphod@galaxy.net");

    MvcResult mvcResult = mockMvc
            .perform(...)
            .contentType("application/json")
            .param("sendWelcomeMail", "true")
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest())
            .andReturn();

    ErrorResult expectedErrorResponse = new ErrorResult("name", "must not be null");
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);
    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
}
```

Again, we read the JSON string from the response body and compare it against an expected JSON string. Additionally, we
check that the response status is 400.

#### Creating Custom ResultMatchers

Certain assertions are rather hard to write and, more importantly, hard to read. Especially when we want to compare the
JSON string from the HTTP response to an expected value it takes a lot of code, as we have seen in the last two
examples.

Luckily, we can create custom `ResultMatchers` that we can use within the fluent API of `MockMvc`. Let's see how we can
do this for our use cases.

##### Matching JSON Output

Wouldn't it be nice to use the following code to verify if the HTTP response body contains a JSON representation of a
certain Java object?

```java
@Test
void whenValidInput_thenReturnsUserResource_withFluentApi() throws Exception {
    UserResource user = ...;
    UserResource expected = ...;

    mockMvc.perform(...)
            ...
            .andExpect(responseBody().containsObjectAsJson(expected, UserResource.class));
}
```

No need to manually compare JSON strings anymore. And it’s much better readable. In fact, the code is so
self-explanatory that I'm going to stop explaining here.

To be able to use the above code, we create a custom `ResultMatcher`:

```java
public class ResponseBodyMatchers {
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject).isEqualToComparingFieldByField(expectedObject);
        };
    }
    
    static ResponseBodyMatchers responseBody(){
        return new ResponseBodyMatchers();
    }
}
```

The static method `responseBody()` serves as the entrypoint for our fluent API. It returns the actual `ResultMatcher`
that parses the JSON from the HTTP response body and compares it field by field with the expected object that is passed
in.

##### Matching Expected Validation Errors

We can even go a step further to simplify our exception handling test: 

```java
@Test
void whenNullValue_thenReturns400AndErrorResult_withFluentApi() throws Exception {
    UserResource user = new UserResource(null, "zaphod@galaxy.net");

    mockMvc.perform(...)
            ...
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest())
            .andExpect(responseBody().containsError("name", "must not be null"));
}
```

Again, the code is self-explanatory.

To enable this fluent API, we must add the method `containsErrorMessageForField()` to our `ResponseBodyMatchers` class:

```java
public class ResponseBodyMatchers {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    public ResultMatcher containsError(String expectedFieldName, String expectedMessage) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResult errorResult = objectMapper.readValue(json, ErrorResult.class);
            List<FieldValidationError> fieldErrors = errorResult.getFieldErrors().stream()
                    .filter(fieldError -> fieldError.getField().equals(expectedFieldName))
                    .filter(fieldError -> fieldError.getMessage().equals(expectedMessage))
                    .collect(Collectors.toList());

            assertThat(fieldErrors)
                    .hasSize(1)
                    .withFailMessage("expecting exactly 1 error message"
                                    + "with field name '%s' and message '%s'",
                            expectedFieldName,
                            expectedMessage);
        };
    }

    static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }
}
```

All the ugly code is hidden within this helper class and we can happily write clean assertions in our integration tests.

### Testing JPA Queries with Spring Boot and @DataJpaTest

Aside from unit tests, integration tests play a vital role in producing quality software. A special kind of integration
test deals with the integration between our code and the database.

With the `@DataJpaTest` annotation, Spring Boot provides a convenient way to set up an environment with an embedded
database to test our database queries against.

#### Dependencies

Aside from the usual Spring Boot dependencies, we're using JUnit Jupiter as our testing framework and H2 as an in-memory
database.

```
dependencies {
  compile('org.springframework.boot:spring-boot-starter-data-jpa')
  compile('org.springframework.boot:spring-boot-starter-web')
  runtime('com.h2database:h2')
  testCompile('org.springframework.boot:spring-boot-starter-test')
  testCompile('org.junit.jupiter:junit-jupiter-engine:5.2.0')
}
```

#### What to Test?

The first question to answer to ourselves is what we need to test. Let's consider a Spring Data repository responsible
for `UserEntity` objects:

```java
interface UserRepository extends CrudRepository<UserEntity, Long> {

    // query methods
}
```

We have different options to create queries. Let's look at some of those in detail to determine if we should cover them
with tests.

##### Inferred Queries

The first option is to create an inferred query:

```java
UserEntity findByName(String name);
```

We don't need to tell Spring Data what to do, since it automatically infers the SQL query from the name of the method
name.

What's nice about this feature is that **Spring Data also automatically checks if the query is valid at spring boot
applicaiton startup**. If we renamed the method to `findByFoo()` and the `UserEntity` does not have a property `foo`,
Spring Data will point that out to us with an exception:

```
org.springframework.data.mapping.PropertyReferenceException: 
  No property foo found for type UserEntity!
```

**So, as long as we have at least one test that tries to start up the Spring application context in our code base, we do
not need to write an extra test for our inferred query**.

_Note that this is not true for queries inferred from long method names like
`findByNameAndRegistrationDateBeforeAndEmailIsNotNull(). This method name is hard to grasp and easy to get wrong, so we
should test if it really does what we intended_.

Having said this, it's a good practice to rename such method with a shorter, more meaningful name and add a `@Query`
annotation to provide a custom JPQL query

##### Custom JPQL Queries with @Query

If queries become more complex, it makes sense to provide a custom JPQL query:

```java
@Query("select u from UserEntity u where u.name = :name")
UserEntity findByNameCustomQuery(@Param("name") String name);
```

Similar to inferred queries, we get a validity check for those JPQL queries for free. Using Hibernate as our JPA provider, we’ll get a QuerySyntaxException on startup if it found an invalid query:

### Integration Tests with @SpringBootTest

With the `@SpringBootTest` annotation, Spring Boot provides a convenient way to start up an application context to be
used in a test.

### Integration Tests vs. Unit Tests

Before we start into integration tests with Spring Boot, let's define what sets an integration test apart from a unit
test.

A unit test covers a single "unit", where a unit commonly is a single class, but can also be a cluster of cohesive
classes that is tested in combination.

An integration test can be any of the following:

- a test that covers multiple "units". It tests the interaction between two or more clusters of cohesive classes.
- a test that covers multiple layers. This is actually a specialization of the first case and might cover the
  interaction between a business service and the persistence layer, for instance.
- a test that covers the whole path through the application. In these tests, we send a request to the application and
  check that it responds correctly and has changed the database state according to our expectations.

Spring Boot provides the `@SpringBootTest` annotation which we can use to create an application context containing all
the objects we need for all of the above test types. Note, however, that overusing `@SpringBootTest` might lead to
very long-running test suites.

So, for simple tests that cover multiple units we should rather create plain tests, very similar to unit tests, in which
we manually create the object graph needed for the test and mock away the rest. This way, Spring doesn't fire up a whole
application context each time the test is started.

For tests that cover integration with the web layer or persistence layer, we can use
[@WebMvcTest](https://reflectoring.io/spring-boot-web-controller-test/) or
[@DataJpaTest](https://reflectoring.io/spring-boot-data-jpa-test/) instead. For integration with other layers, have a
look at Spring Boot's other
[test slice annotations](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-test-auto-configuration.html).
Note that these test slices will also take some time to boot up, though.

Finally, for tests that cover the whole Spring Boot application from incoming request to database, or tests that cover
certain parts of the application that are hard to set up manually, we can and should use `@SpringBootTest`

### Creating an ApplicationContext with @SpringBootTest

`@SpringBootTest` by default starts searching in the current package of the test class and then searches upwards through
the package structure, looking for a class annotated with `@SpringBootConfiguration` from which it then reads the
configuration to create an application context. This class is usually our main application class since the
`@SpringBootApplication` annotation includes the `@SpringBootConfiguration` annotation. It then creates an application
context very similar to the one that would be started in a production environment.

Because we have a full application context, including web controllers, Spring Data repositories, and data sources,
`@SpringBootTest` is very convenient for integration tests that go through all layers of the application:

```java
@SpringBootTest
@AutoConfigureMockMvc
class RegisterUseCaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registrationWorksThroughAllLayers() throws Exception {
        UserResource user = new UserResource("Zaphod", "zaphod@galaxy.net");

        mockMvc.perform(post("/forums/{forumId}/register", 42L)
                .contentType("application/json")
                .param("sendWelcomeMail", "true")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        UserEntity userEntity = userRepository.findByName("Zaphod");
        assertThat(userEntity.getEmail()).isEqualTo("zaphod@galaxy.net");
    }

}
```

Here, we additionally use `@AutoConfigureMockMvc` to add a `MockMvc` instance to the application context.

We use this `MockMvc` object to perform a `POST` request to our application and to verify that it responds as expected.

We then use the `UserRepository` from the application context to verify that the request has lead to an expected change
in the state of the database.

### Customizing the Application Context

We can turn a lot of knobs to customize the application context created by `@SpringBootTest`.

> ⚠️ Each customization of the application context is one more thing that makes it different from the "real" application
> context that is started up in a production setting. So, in order to make our tests as close to production as we can,
> **we should only customize what's really necessary to get the tests running**!

#### Setting Custom Configuration Properties

Often, in tests it's necessary to set some configuration properties to a value that's different from the value in a
production setting:

```java
@SpringBootTest(properties = "foo=bar")
class SpringBootPropertiesTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

If the property `foo` exists in the default setting, it will be overridden by the value `bar` for this test.

### Externalizing Properties with @ActiveProfiles

If many of our tests need the same set of properties, we can create a configuration file
`application-<profile>.properties` or `application-<profile>.yml` and load the properties from that file by
activating a certain profile::

    ## application-test.yml
    foo: bar

```java
@SpringBootTest
@ActiveProfiles("test")
class SpringBootProfileTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

One can also use
[@PropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html)
annotation to achieve the same goal.

Yet another way to customize a whole set of properties is with the `@TestPropertySource` annotation::

    ## src/test/resources/foo.properties
    foo=bar

```java
@SpringBootTest
@TestPropertySource(locations = "/foo.properties")
class SpringBootPropertySourceTest {

    @Value("${foo}")
    String foo;

    @Test
    void test(){
        assertThat(foo).isEqualTo("bar");
    }
}
```

In addition, we can also use
[@ContextConfiguration](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/ContextConfiguration.html),
which is a very similar mechanism for specifying test class properties.

#### Injecting Mocks with @MockBean

If we only want to test a certain part of the application instead of the whole path from incoming request to database,
we can replace certain beans in the application context by using `@MockBean`:

```java
@SpringBootTest
class MockBeanTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private RegisterUseCase registerUseCase;

    @Test
    void testRegister(){
        // given
        User user = new User("Zaphod", "zaphod@galaxy.net");
        boolean sendWelcomeMail = true;
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity(1L));

        // when
        Long userId = registerUseCase.registerUser(user, sendWelcomeMail);

        // then
        assertThat(userId).isEqualTo(1L);
    }
}
```

In this case, we have replaced the `UserRepository` bean with a mock. Using Mockito's `when` method, we have
specified the expected behavior for this mock in order to test a class that uses this repository.

#### Adding Beans with @Import

If certain beans are not included in the default application context, but we need them in a test, we can import them
using the `@Import` annotation:

```java
package other.namespace;

@Component
public class Foo {

}
```

```java
@SpringBootTest
@Import(other.namespace.Foo.class)
class SpringBootImportTest {

    @Autowired
    Foo foo;

    @Test
    void test() {
        assertThat(foo).isNotNull();
    }
}
```

#### Creating a Custom @SpringBootApplication

We can even create a whole custom Spring Boot application to start up in tests. If this application class is in the same
package as the real application class, but in the test sources rather than the production sources, `@SpringBootTest`
will find it before the actual application class and load the application context from this application instead.

Alternatively, we can tell Spring Boot which application class to use to create an application context:

```java
@SpringBootTest(classes = CustomApplication.class)
class CustomApplicationTest {

}
```

When doing this, however, we are **testing an application context that may be completely different from the production
environment**, so this should be a last resort only when the production application cannot be started in a test
environment. Usually, there are better ways, though, such as to make the real application context configurable to
exclude beans that will not start in a test environment. Let's look at this in an example.

Let's say we use the `@EnableScheduling` annotation on our application class. Each time the application context is
started (even in tests), all `@Scheduled` jobs will be started and may conflict with our tests. We usually don't want
the jobs to run in tests, so we can create a second application class without the `@EnabledScheduling` annotation and
use this in the tests. However, the better solution would be to create a configuration class that can be toggled with a
property:

```java
@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "io.reflectoring.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SchedulingConfiguration {

}
```

We have moved the `@EnableScheduling` annotation from our application class to this special confgiuration class.
Setting the property `io.reflectoring.scheduling.enabled` to `false` will cause this class not to be loaded as part
of the application context:

```java
@SpringBootTest(properties = "io.reflectoring.scheduling.enabled=false")
class SchedulingTest {

    @Autowired(required = false)
    private SchedulingConfiguration schedulingConfiguration;

    @Test
    void test() {
        assertThat(schedulingConfiguration).isNull();
    }
}
```

We have now successfully deactivated the scheduled jobs in the tests.

### IT Design

A code base with a lot of `@SpringBootTest`-annotated tests may take quite some time to run. The Spring test support
is smart enough to only create an application context once and re-use it in following tests, but if different tests need
different application contexts, it will still create a separate context for each test, which takes some time for each
test.

All of the customizing options described above will cause Spring to create a new application context. So, we might want
to create one single configuration and use it for all tests so that the application context can be re-used.

#### Pre-configure End-Object in End-to-End Integration Test

The name of this section might sound confusing to you, so let me explian what I meant by starting with an example:

You wrote a Spring Boot application. This app has a `MusicController` that gives you all information about your
personal music library. The `MusicController` is, by convension, backed by a `MusicService`, to which
`MusicController` delegates all user requests.

In order to improve the performance, you add a "caching layer" behind the `MusicService`; let's call the cache
"`MusicCache`". The facilitate our discussion, we will assume the cache is simply a Java `Map` whose key is
`musicId` and value is a `Music` Object.

```java
@RestController
@RequestMapping("/")
public class MetaManagerController {

    @Autowired
    private MusicService musicService;

    @RequestMapping(value="music/{musicId}", method=RequestMethod.GET)
    public List<Music> getMusicInfo(@PathVariable("musicId") String musicId) {
        return getMusicService().getById(musicId);
    }

    private MusicService getMusicService() {
        return musicService;
    }
}
```

For **demonstration** purpose, let's put `MusicCache` as a public field in `MusicService` (don't do this in
production!):

```java
public class MusicService {

    @Autowired
    private MusicCache musicCache;

    ...

    public getById(String musicId) {
        return getMusicCache().get(musicId);
    }

    private MusicCache getMusicCache() {
        return cache;
    }
}
```

Now you are writing integration test against `MusicController` and you would like to pre-insert some cache. The
approach is the following (In your test code):

```java
ServletContext servletContext = webApplicationContext.getServletContext();

MusicCache musicCache = webApplicationContext.getBean("musicCache");
musicCache.put("id1", testMusicObject1);
musicCache.put("id2", testMusicObject2);

String responseData = mockMvc.perform(get("/music/id1"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse().getContentAsString();

// veriry "responseData" has expected testMusicObject1
```

## FAQ

### Why Does My Spring Boot App Always Shutdown Immediately After Starting?

Suppose you have a spring boot project that packages to a JAR called `app.jar`. Running

    java -jar .../app.jar
    
succeeds but terminates immediately so that no apps are ending up running. This means the app is not a webapp because it
doesn't have an embedded container (e.g. Tomcat) on the classpath. Adding one fixed it. If you are using Maven, then add
this in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Inject Headers Info

#### Accessing HTTP Headers

##### Individual Headers

If we need access to a specific header, we can configure `@RequestHeader` with the header name:

```java
@GetMapping("/greeting")
public ResponseEntity<String> greeting(@RequestHeader("accept-language") String language) {
    ...
}
```

Then, we can access the value using the variable passed into our method. If a header named "accept-language" isn't found
in the request, the method returns a "400 Bad Request" error.

Our headers don't have to be strings. For example, if we know our header is a number, we can declare our variable as a
numeric type:

```java
@GetMapping("/double")
public ResponseEntity<String> doubleNumber(@RequestHeader("my-number") int myNumber) {
    return new ResponseEntity<String>(String.format("%d * 2 = %d", myNumber, (myNumber * 2)), HttpStatus.OK);
}
```

##### All Headers

If we're not sure which headers will be present, or we need more of them than we want in our method's signature, we can
use the `@RequestHeader` annotation without a specific name.

We have a few choices for our variable type:

* `Map`
* `MultiValueMap`
* `HttpHeaders`

First, let's get the request headers as a Map:

```java
@GetMapping("/listHeaders")
public ResponseEntity<String> listAllHeaders(@RequestHeader Map<String, String> headers) {
    ...
}
```

If we use a `Map` and one of the headers has more than one value, we'll get only the first value. This is the equivalent
of using the `getFirst` method on a `MultiValueMap`.

If our headers may have multiple values, we can get them as a `MultiValueMap`:

```java
@GetMapping("/multiValue")
public ResponseEntity<String> multiValue(
  @RequestHeader MultiValueMap<String, String> headers) {
    headers.forEach((key, value) -> {
        LOG.info(String.format(
          "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
    });
        
    return new ResponseEntity<String>(
      String.format("Listed %d headers", headers.size()), HttpStatus.OK);
}
```