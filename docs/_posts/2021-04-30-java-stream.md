---
layout: post
title: Java 8 Stream
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/25-cover.png"
thumbnail: "assets/img/post-cover/25-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}
  
## Combining a Collection of Predicates

We chain a collection of Predicates by reducing them. In the following example, we have a list of predicates that we
combined using `Predicate.and()`:

```java
@Test
public void whenFilterListWithCollectionOfPredicatesUsingAnd_thenSuccess(){
    List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
    allPredicates.add(str -> str.startsWith("A"));
    allPredicates.add(str -> str.contains("d"));        
    allPredicates.add(str -> str.length() > 4);
    
    List<String> result = names.stream()
        .filter(allPredicates.stream().reduce(x->true, Predicate::and))
        .collect(Collectors.toList());
    
    assertEquals(1, result.size());
    assertThat(result, contains("Alexander"));
}
```

Note that we use our base identity as:

```java
x->true
```

That will be, however, different if we want to combine them using Predicate.or():

```java
@Test
public void whenFilterListWithCollectionOfPredicatesUsingOr_thenSuccess(){
    List<String> result = names.stream()
        .filter(allPredicates.stream().reduce(x->false, Predicate::or))
        .collect(Collectors.toList());
    
    assertEquals(2, result.size());
    assertThat(result, contains("Adam","Alexander"));
}
```

## Collecting Stream containing "null" to List 
 
```java
person.stream()
        .sorted(Comparator.comparingInt(Person::getDriverLicense))
        .collect(Collectors.toList());
```

If this stream contains a kid who doesn't know how to drive, the `Collectors.toList()` might see a `null` value, which
causes `NullPointerException`, because `Collectors.toList()` gives us a list implementation that doesn't permit `null`
elements. `ArrayList`, however, permits `null` elements according to its JavaDoc. The solution them would be

```java
person.stream()
        .sorted(Comparator.comparingInt(Person::getDriverLicense))
        .collect(Collectors.toCollection(ArrayList::new));
```

Note that the last line is changed so that the collect procedure returns an uer-specified list implementation, which is
`ArrayList`

## Sorting

```java
public class User {

    private final String name;
    private final int age;
}

final List<User> users = Arrays.asList(
        new User("C", 30), 
        new User("D", 40),
        new User("A", 10), 
        new User("B", 20), 
        new User("E", 50)
);

List<User> sortedList = users.stream()
            .sorted(Comparator.comparingInt(User::getAge))
            .collect(Collectors.toList());
```

## Remove Duplicates from a List of Objects based on Property

You can get a stream from the List and put in in the TreeSet from which you provide a custom comparator that compares
the property uniquely. Then if you really need a list you can put then back this collection into an ArrayList: 

```java
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

...
List<Employee> unique = employee.stream()
        .collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(comparingInt(Employee::getId))),
                        ArrayList::new
                )
        );
```

Given the example:

```java
List<Employee> employee = Arrays.asList(new Employee(1, "John"), new Employee(1, "Bob"), new Employee(2, "Alice"));
```

It will output:

```java
[Employee{id=1, name='John'}, Employee{id=2, name='Alice'}]
```

## Convert Iterable to Stream

```java
StreamSupport.stream(iterable.spliterator(), false)
        .filter(...)
        .moreStreamOps(...);
```

## Convert Two Dimensional Array to List

```java
List<Foo> collection = Arrays.stream(array)  //'array' is two-dimensional
        .flatMap(Arrays::stream)
        .collect(Collectors.toList());
```


## Preserve Order in Stream with collect

Say we would like to process a list such as ["blah", "blah", "yep"] and get ["blah (2 times)", "yep"], we will collect
them to a `LinkedHashMap` to get the expected result:

```java
return messages.stream()
        .collect(groupingBy(Function.identity(), LinkedHashMap::new, summingInt(e -> 1)))
        .entrySet()
        .stream()
        .map(e -> e.getKey()+(e.getValue() == 1 ? "" : " (" + e.getValue() +" times)"))
        .collect(toList());
```
