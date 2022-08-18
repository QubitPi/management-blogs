---
layout: post
title: Documentation Guide
tags: [HTTP, HTTPS]
category: FINALIZED
color: rgb(0, 196, 0)
feature-img: "assets/img/post-cover/29-cover.png"
thumbnail: "assets/img/post-cover/29-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

"You will be using your code in 6 months"

<!--more-->

* TOC
{:toc}

This guide gathers the collective wisdom of the [Write the Docs](https://www.writethedocs.org/guide/) community around
best practices for creating software documentation.
  
## A Beginner's Guide to Writing Documentation

### Why Write Docs

#### You Will be Using Your Code in 6 Months

Code that you wrote 6 months ago is often indistinguishable from code that someone else has written. You will look upon
a file with a fond sense of remembrance. Then a sneaking feeling of foreboding, knowing that someone less experienced,
less wise, had written it.

As you go through this selfless act of untangling things that were obvious or clever months ago, you will start to
empathize with your users. If only I had written down why I had done this. Life would be so much simpler. Documentation
allows you to transfer the _why_ behind code. Much in the same way code comments explain the _why_, and not the _how_,
documentation serves the same purpose.

#### You Want People to Use Your Code

You have written a piece of code, and released it into the world. You have done this because you think that others might
find it useful. However, people need to understand why your code might be useful for them, before they decide to use it.
Documentation tells people that this project is for them.

* If people don't know why your project exists, they won't use it.
* If people can't figure out how to install your code, they won't use it.
* If people can't figure out how to use your code, they won't use it.

There are a small number of people who will source dive and use any code out there. That is a vanishingly small number
of people, compared to people who will use your code when properly documented. If you really love your project, document
it, and let other people use it.

> **Sidebar on open source**
> 
> There is a magical feeling that happens when you release your code. It comes in a variety of ways, but it always hits
> you the same. Someone is using my code?! A mix of terror and excitement.
> 
> * I made something of value! What if it breaks?!
> * I am a real open source developer!
> * Oh god, someone else is using my code..
> 
> Writing good documentation will help alleviate some of these fears. A lot of this fear comes from putting something
> into the world. My favorite quote about this is something along these lines:
> 
> "Fear is what happens when you're doing something important. If you are doing work that isn't scary, it isn't
> improving you or the world.
>
> Congrats on being afraid! It means you're doing something important.

#### You Want People to Help Out

Open source is this magical thing right? You release code, and the code gnomes come and make it better for you.

Not quite.

There are lots of ways that open source is amazing, but it doesn't exist outside the laws of physics. You have to put
work in, to get work out.

* You only get contributions after you have put in a lot of work.
* You only get contributions after you have users.
* You only get contributions after you have documentation.

Documentation also provides a platform for your first contributions. A lot of people have never contributed before, and
documentation changes are a lot less scary than code changes. If you don't have documentation, you will miss out on a
whole class of contributors.

#### You Want Your Code to be Better

It's really easy to have an idea in your head that sounds perfect, but the act of putting words to paper requires a
distillation of thought that may not be so easy.

Writing documentation improves the design of your code. Talking through your API and design decisions on paper allows
you to think about them in a more formalized way. A nice side effect is that it allows people to contribute code that
follows your original intentions as well.

#### You Want to be a Better Writer

Writing documentation is a different form of writing than most people have experience with. Technical writing is an art
that doesn't come naturally. Writing documentation will start you down the road to being a better technical writer,
which is a useful skill to have as a programmer.

Writing also becomes easier over time. If you don't write for many months, it is a lot harder to start writing again.
Keeping your projects documented will keep you writing at a reasonable cadence.

Starting simple is the best way to achieve actual results. I will present a well-paved path to walk down, and after you
have the basic idea, you can expand your scope. The tools should be powerful and easy to use. This removes obstacles to
actually putting words on the page.

### What to Write

Now we're getting down to the brass tacks. Making sure that you give your users all the information that they need, but
**not too much**.

First, you need to ask yourself who you're writing for. At first, you generally just need to appeal to two audiences:

1. **Users** - People who simply want to use your code, and don't care how it works
2. **Developers** - People who want to contribute back to your code

#### What Problem your Project Solves

A lot of people will come to your docs trying to figure out what exactly your project is. Someone will mention it, or
they'll google a phrase randomly. You should explain what your project does and why it exists

#### A Small Code Example

Show a telling example of what your project would normally be used for.

#### A Link to Your Code & Issue Tracker

People like to browse the code sometimes. They might be interested in filing bugs against the code for issues they've
found. Make it really easy for people who want to contribute back to the project in any way possible.

#### Frequently Asked Questions (FAQ)

A lot of people have the same problems. If things happen all the time, you should probably fix your documentation or the
code, so that the problems go away. However, there are always questions that get asked about your project, things that
can't be changed, etc. Document those, and keep it up to date. FAQs are generally out of date, but when done well, they
are a golden resource

#### How to Get Support

Mailing list? IRC Channel? Document how to get help and interact with the community around a project

People usually have standards for how they expect things to be done in their projects. You should document these so that
if people write code, they can do things in the norm of the project.
[Open Comparison](https://packaginator.readthedocs.io/en/latest/contributing.html) does a great job of this.

#### Installation Instructions

Once people figure out whether they want to use your code or not, they need to know how to actually get it and make it
run. Hopefully your install instructions should be a couple lines for the basic case. A page that gives more information
and caveats should be linked from here if necessary.

#### Your Project's License

BSD? MIT? GPL? This stuff might not matter to you, but the people who want to use your code will care about this a whole
lot. Think about what you want to accomplish with your license, and please only pick one of the standard licenses that
you see around the web.

## Building Documentation Mindshare in a Company

Having a culture of documentation inside of a company is a great thing. Building a culture around documentation however
is a hard thing to do.

### Start in Engineering

Starting in the engineering organization I think is the simplest way to affect change on documentation culture. There
are lots of other parts of an organization, but it's easier to get permission to work on just the internal stuff to
engineering. Think things like:

* Project documentation
* Operations documentation
* Playbooks
* Monitoring Information
* Best Practices
* Style Guides

### Build a Taxonomy

A big problem with a lot of documentation systems is that they have been organized organically, AKA have no
organization. If you build out a structure for people to start, and keep it consistent across projects, it will make
everyones lives easier.

> You need to have an answer to the "Where do I put it?" question.

### Make it Easy

Getting started should be as easy and straight-forward as possible. Most people don’t like change, and introducing new
tooling and things will already rouse a negative response in some. This is why you need to make it as simple as possible
to get started.

#### Build Templates

It should take about 5 seconds to get a basic outline of the documentation for a project started.

Also have a standard hierarchy for the docs, so that people know where to look for things in any project.

### Have Regular Meetings Throughout the Process

Having a weekly standing meeting where you keep track of the progress along all the stages is important. This gives
people a known place to go and discuss issues or ideas. It also provides a sense that the project is moving forward. At
the beginning these meetings will be used to plan and track implementation. After implementation, it will be a place to
drive adoption and gather feedback.

#### Make It Long Term

Something like a good documentation system also needs pretty constant care and feeding, reorganizing, and other
maintained work. If you view this project as getting the tools in place, without a long term commitment, it will fail
just like your last system.

## Documentation Principles

Software development benefits from
[philosophies](https://en.wikipedia.org/wiki/Category:Software_development_philosophies) and
[principles](https://en.wikipedia.org/wiki/Category:Programming_principles) such as
[DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself), [KISS](https://en.wikipedia.org/wiki/KISS_principle),
[code reuse](https://en.wikipedia.org/wiki/Code_reuse), and many more. With these commonly understood and accepted
standards, developers can hold themselves and each other accountable to producing high-quality code.

This set of principles seeks to define similar standards for software documentation that, when practiced, will foster
clean and intuitive content. The end goal is ultimately to delight and empower readers by making information easier for
them to acquire.

### In General, Documentation Should be ...

#### Precursory

> **Begin documenting before you begin developing**.

Before coding, write requirements and specifications that also serve as the first draft of documentation. These texts no
doubt will need a bit of clean up before publishing, but by front-loading the documentation, you lay a clear path
forwards. Early documentation also helps facilitate peer feedback and group decisions to guide your work. This model is
the sentiment behind [documentation driven design](#documentation-driven-design).

#### Participatory

> **In the documentation process, include everyone from developers to end users**.

Integrate documentation into the standard workflow of developers, and seek to reduce silos that solicit documentation
from only a subset of the software's contributors. Developers and engineers are the people with the best access to
in-demand information, and getting them to document it will help foster a culture of documentation.

As well, documentation readers (i.e., users) should have clear avenues towards involvement in documentation. A good
first step is to give readers the ability to **offer feedback in the form of comments or suggestions**. Allowing readers
to edit documentation directly (e.g., in a wiki) can also be effective but must be weighed against the need and capacity
for editorial oversight.

Encourage everyone to become a [documentarian](https://www.writethedocs.org/documentarians/)!

### Content Should Be ...

> "Content" is the conceptual information within documentation.

#### ARID

> **Accept (some) Repetition In Documentation**.
 
If you want to write good code, [Don't Repeat Yourself](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself). But if
you adhere strictly to this DRY principle when writing documentation, you won't get very far. **Some amount of business
logic described by your code will need to be described again in your documentation**.

In an ideal world, an automated system would generate documentation from the software's source code, and the system
would be smart enough to generate good documentation without any additional input. Unfortunately we do not (yet) live in
that world, and today the best documentation is hand-written, which means that just by writing any documentation, you
are repeating yourself. Sure,
[documentation generators](http://en.wikipedia.org/wiki/Comparison_of_documentation_generators) exist and are useful,
but it's important to acknowledge that they still require input from humans to function.

The pursuit of minimizing repetition remains valiant! ARID does not mean
[WET](https://en.wikipedia.org/wiki/Don't_repeat_yourself#WET), hence the word choice. It means: try to keep things as
DRY as possible but also recognize that you'll inevitably need some amount of "moisture" to produce documentation.

Cultivating an awareness of this inconvenient truth will hopefully be a helpful step toward reminding developers that a
need often exists to **update documentation along with code**.

#### Skimmable

> **Structure content to help readers identify and skip over concepts which they already understand or see are not
> relevant to their immediate questions**.

Burying concepts in prose and verbiage demands more time from readers seeking answers to specific questions. Save your
readers' time by writing like a newspaper instead of a novel. Specifically:

* **Headings** - should be descriptive and concise.
* **Hyperlinks** - should surround words which describe the link itself (and never phrases like "click here" or
  "this page").
* **Paragraphs and list items** - should begin with identifiable concepts as early as possible.

#### Exemplary

> **Include (some) examples and tutorials in content**.

Many readers look first towards examples for quick answers, so including them will help save these people time. Try to
write examples for the most common use cases, but not for everything. Too many examples can make the documentation less
[skimmable](#skimmable). Also, consider separating examples and tutorials from more dense reference information to
further help readers skim.

#### Consistent

> **Use consistent language and formatting in content**.

The more content editors you have, the more important a style guide becomes in facilitating consistency. Consistency
also helps make documentation skimmable and beautiful.

#### Current

> **Consider incorrect documentation to be worse than missing documentation**.

When software changes faster than its documentation, the users suffer. Keep it up to date.

Make every effort to write content that is version-agnostic and thus in less need of maintenance. For example,
generalize version numbers of software when they occur in tutorials (such as extracting a source code tarball with the
version number in the file name).

Be aware as well that some users will remain on older versions of your software, and thus require older versions of your
documentation. Proper documentation platforms will accommodate such needs gracefully.

### Sources Should Be ...

> A "source" refers to a system used to store and edit content. Examples of sources include: text files written using
> reStructuredText or Markdown, HTML content in a CMS database, help text stored within strings in application code,
> code comments to be assembled later into formalized documentation, and others too.

#### Nearby

> **Store sources as close as possible to the code which they document**.

Give developers systems which allow them to easily make documentation changes along with their code changes. One way is
to store documentation content in comment blocks within application source code. Another is to store it in separate text
files but within the same repository as the application's source code. Either way, the goal is **merge (as much as
possible) the workflows for development and documentation**.

#### Unique

> **Eliminate content overlap between separate sources**.

Storing content in different sources is okay, as long as the scope of each source is clearly defined and disjoint with
other sources. The goal here is to prevent any parallel maintenance (or worse - lack of maintenance) of the same
information across multiple sources.

### Each Publication Should Be ...

> A "publication" refers to a single, cohesive tool that readers use to consume documentation. It may be static or
> interactive - digital or paper. Multiple publications may be created from a single source (such as web and PDF
> versions of the same manual). Although rarer, multiple sources may be used to create a single publication. More
> examples of publications include: API reference, man page, command line ``-help`` output, in-application help tips,
> online tutorials, internal engineering manuals, and others too.

#### Discoverable

> **Funnel users intuitively towards publications through all likely pathways**.

Try to identify everywhere the user might go looking for documentation, and in all of those places, insert helpful
pointers for them to find it. Documentation need not exist in all of these places, just pointers to it.

If a user manual is published in the woods, and no one is around to read it, does it exist? Discoverability says "no".

#### Addressable

> **Provide addresses to readers which link directly to content at a granular level**.

The ability to reference specific sections deep within a body of documentation facilitates productive communication
about the documentation, even with one's self. These addresses can take the form of URLs, page numbers, or other forms
depending on the publication medium. Readers may wish to bookmark certain sections, share them with other users, or
provide feedback to the authors. The more granular this ability, and the easier it is to access, the better.

#### Cumulative

> **Content should be ordered to cover prerequisite concepts first**.

Can a reader follow your entire body of documentation, linearly, from start to finish without getting confused? If so,
the documentation is perfectly "cumulative", which is great, but not always possible. It's something to strive for,
especially in tutorials and examples. If you have separated your tutorials and examples from the reference
documentation, then put the tutorials and examples first. Then, content within the reference information section may be
ordered alphabetically or topically without regard to prerequisite needs.

The goal of cumulative ordering is not to encourage readers to consume your documentation linearly - rather it is to
help them narrow their search for information when filling in gaps in their knowledge. If a reader arrives with some
knowledge of the software and begins reading the documentation at the 25% mark, they are likely to "rewind" when
confused.

#### Complete

> **Within each publication, cover concepts in-full, or not at all**.

Picture some documentation of software like a map of a neighborhood. If the map displays roads, readers will expect it
to display all roads (which exist and are of the same type being displayed). Perhaps the map does not display railroads,
for example. Thus, a reader approaching the map to look for railroads will find zero and then seek a different map - but
the map is still "complete", even with this shortcoming. "Complete" does not mean that the map must describe all
characteristics of the land. It means simply that, for the characteristics it chooses to describe, it should describe
all of them. A map that displays fifty out of one hundred fire hydrants in a neighborhood is worse than a map which
displays none.

As a good example, `iconv` is a command line tool for working with character encodings. Its
[man page](http://man7.org/linux/man-pages/man1/iconv.1.html) covers all of its available options but none of the
possible character encodings accepted as values to these options. Instead, the man page instructs the user to run
`iconv -l` to produce a list of character encodings. In this example, the man page and the list are separate
publications, both of which are complete, which is good!

Publishing partially completed documentation must be done cautiously. To avoid misleading readers, make every effort to
clearly state, up front, that a particular concept is only covered partially.

#### Beautiful

> **Visual style should be intentional and aesthetically pleasing**.

Aesthetics don't matter to everyone - but (consciously or not) some readers will struggle to find comfort in
documentation that lacks attention to visual style. Even in text-only documentation such as `--help` output, visual
style is still present in the form of spacing and capitalization. If visual style is not important to you personally,
then consider soliciting stylistic improvements from others for whom it is.

### A Documentation Body Should Be ...

> A "body" refers to the collection of all the publications within a software project and any of its sub-projects

#### Comprehensive

> **Ensure that together, all the publications in the body of documentation can answer all questions the user is likely
> to have**.

We can never create enough documentation to satisfy all questions, however obscure, that might arise from users - but
satisfying the likely questions is certainly attainable and thus should be the goal of a body of documentation. "Likely"
is admittedly a blurry term, but it's also relative, which means that a body of documentation which answers very
unlikely questions while failing to answer likely ones is somewhat out of balance.

Answering some questions may require the user to read multiple publications, which is okay.

## Style Guides

### Developer Documentation and APIs

Clear, well-formatted, and detailed API documentation is the key for developers to quickly consume and implement your
API. It is also key to helping developers understand what happens when an API call is made, and in the case of failure,
understand what went wrong and how to fix it.

From the perspective of a user, if a feature is not documented, it does not exist. If a feature is documented
incorrectly, then it is broken. The best API documentation is often the result of a well designed API. Documentation
cannot fix a poorly designed API. It is best to work on developing the API and the documentation concurrently.

If your API already exists, automated reference documentation can be useful to document the API in its current state. If
your API is still being implemented, API documentation can perform a vital function in the design process.

#### Documentation-Driven Design

If your API isn't built yet, you can create API documentation to help with the design process. The documentation-driven
design philosophy comes down to this:

> Documentation changes are cheap. Code changes are expensive.

By designing your API through documentation, you can easily get feedback and iterate your design before development
begins.

Some API documentation formats have the added benefit of being machine-readable. These formats open the door to a
multitude of additional tools that can help during the entire lifecycle of your API:

* Create a mock server to help during the initial API design
* Test your API before deployment to ensure that the API and the documentation matches
* Create interactive documentation that allows developers to perform demo requests to your API

#### Test-Driven Documentation

Test-driven documentation aims to improve upon the typical approaches to automated documentation. It allows you to write
the bulk of the documentation by hand while also ensuring its accuracy by using your API’s tests to generate some
content.
