---
layout: post
title: Single Responsibility Principle - What Defines a Reason to Change?
tags: [Software Engineering]
color: rgb(0, 136, 0)
feature-img: "assets/img/post-cover/22-cover.png"
thumbnail: "assets/img/post-cover/22-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


In 1972 David L. Parnas published a classic paper entitled
[_On the Criteria To Be Used in Decomposing Systems into Modules_]({{ "/assets/pdf/On the Criteria To Be Used in Decomposing Systems into  Modules.pdf" | relative_url}}).
It appeared in the [December issue of the Communications of the ACM, Volume 15, Number 12](https://dl.acm.org/doi/10.1145/361598.361623).

In this paper, Parnas compared two different strategies for decomposing and separating the logic in a simple algorithm.
The paper is fascinating reading, and we strongly urge you to study it. His conclusion, in part, is as follows:

> "We have tried to demonstrate by these examples that it is almost always incorrect to begin the decomposition of a
> system into modules on the basis of a flowchart. We propose instead that one begins with a list of difficult design 
> decisions or design decisions _which are likely to change_. Each module is then designed to hide such a decision from
> the others."

We added the emphasis in the second to last sentence. Parnas; conclusion was that modules should be separated based, at 
lease in part, on the way that they might change.

Two years later, Edsger Dijkstra wrote another classic paper entitled
[_On the role of scientific thought_]({{ "/assets/pdf/On the role of scientific thought.PDF" | relative_url}}). in which
he introduced the term: _The Separation of Concerns_.

The 1970s and 1980s were a fertile time for principles of software architecture. _Structured Programming and Design_
were all the rage. During that time the notions of _Coupling_ and _Cohesion_ were introduced by Larry Constantine, and 
amplified by Tom DeMarco, Meilir Page-Jones and many others.

In the late 1990s Robert C. Martin attempted to consolidate these notions into a principle, which he called: _The Single 
Responsibility Principle_, which states that each software module should have one and only one reason to change. This 
sounds good, and seems to align with Parnas' formulation. However it begs the question: **What defines a reason to 
change?**

Some folks have wondered whether a bug-fix qualifies as a reason to change. Others have wondered whether refactorings
are reasons to change. These questions can be answered by pointing out the coupling between the term "reason to change"
and "responsibility".

Certainly the code is not responsible for bug fixes or refactoring. Those things are the responsibility of the
programmer, not of the program. But if that is the case, what is the program responsible for? Or, perhaps **a better
question is: _who_ is the program responsible to? Better yet: _who_ must the design of the program _respond to_?**

Imagine a typical business organization. There is a CEO at the top. Reporting to that CEO are the
[C-level executives](https://resources.workable.com/hr-terms/c-level-executive#): the CFO, COO, and CTO among others.
The CFO is responsible for controlling the finances of the company. The COO is responsible for managing the operations
of the company. And the CTO is responsible for the technology infrastructure and development within the company.

Now consider this bit of Java code:

```java
public class Employee {
    public Money calculatePay();
    public void save();
    public String reportHours();
}
```

* The `calculatePay` method implements the algorithms that determine how much a particular employee should be paid,
  based on that employee's contract, status, hours worked, etc.
* The `save` method stores the data managed by the Employee object onto the enterprise database.
* The `reportHours` method returns a string which is appended to a report that auditors use to ensure that employees are 
  working the appropriate number of hours and are being paid the appropriate compensation.

Now, which of those C-Level executives reporting to the CEO is responsible for specifying the behavior of the
`calculatePay` method? Which of them would be fired by the CEO if that method were catastrophically mis-specified?
Clearly the answer is the CFO. Specifying the pay of employees is a financial responsibility. If all the employees were 
paid double for a year because someone in the CFOs organization mis-specified the rules for calculating pay, the CFO
would likely be fired.

A different C-Level executive is responsible for specifying the format and content of the string returned from the 
`reportHours` method. That executive manages the auditors and reviewers, and that's an operations responsibility. So if 
there were a catastrophic mis-specification of that report, the COO would be fired.

Finally, it should be obvious which of the C-Level executives would be fired if there were a catastrophic
mis-specification of the `save` method. If the enterprise database were to be corrupted by such a horrific 
mis-specification, the CTO would likely be fired.

So it stands to reason that when changes are made to the algorithm within the `calculatePay` method, the request for
those changes will originate from the organization headed by the CFO. Similarly it will be the COO's organization that
will request changes to the `reportHours` method, and the CTOs organization that will request changes to the `save`
method.

And this gets to the crux of the Single Responsibility Principle. **This principle is about people**. _When you write a 
software module, you want to make sure that when changes are requested, those changes can only originate from a single 
person, or rather, a single tightly coupled group of people representing a single narrowly defined business function.
You want to isolate your modules from the complexities of the organization as a whole, and design your systems such that 
each module is responsible (responds to) the needs of just that one business function._

Why? Because we don't want to get the COO fired because we made a change requested by the CTO. Nothing terrifies our 
customers and managers more than discovering that a program malfunctioned in a way that was, from their point of view, 
completely unrelated to the changes they requested. If you change the `calculatePay` method, and inadvertently break the 
`reportHours` method; then the COO will start demanding that you never change the `calculatePay` method again.

> Imagine you took your car to a mechanic in order to fix a broken electric window. He calls you the next day saying
> it's all fixed. When you pick up your car, you find the window works fine; but the car won't start. It's not likely
> you will return to that mechanic because he's clearly an idiot.

That's how customers and managers feel when we break things they care about that they did not ask us to change.

This is the reason we do not put SQL in JSPs. This is the reason we do not generate HTML in the modules that compute 
results. This is the reason that business rules should not know the database schema. This is the reason we separate 
concerns.

Another wording for the Single Responsibility Principle is:

> Gather together the things that change for the same reasons. Separate those things that change for different reasons.

If you think about this you'll realize that this is just another way to define cohesion and coupling. We want to
increase the cohesion between things that change for the same reasons, and we want to decrease the coupling between
those things that change for different reasons.

However, as you think about this principle, remember that the reasons for change are people. It is people who request 
changes. And you don't want to confuse those people, or yourself, by mixing together the code that many different
people care about for different reasons.
