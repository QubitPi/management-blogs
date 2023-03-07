---
layout: post
title: Dependency Grammar
tags: [Machine Learning]
color: rgb(157, 0, 0)
feature-img: "assets/img/post-cover/2-cover.png"
thumbnail: "assets/img/post-cover/2-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

Dependency grammar (DG) is a class of modern grammatical theories that are all based on the dependency relation (as
opposed to the constituency relation of phrase structure) and that can be traced back primarily to the work of Lucien 
Tesnière. Dependency is the notion that linguistic units, e.g. words, are connected to each other by directed links. The 
(finite) verb is taken to be the structural center of clause structure. All other syntactic units (words) are either 
directly or indirectly connected to the verb in terms of the directed links, which are called dependencies. Dependency 
grammar differs from phrase structure grammar in that while it can identify phrases it tends to overlook phrasal nodes.
A dependency structure is determined by the relation between a word (a head) and its dependents. Dependency structures
are flatter than phrase structures in part because they lack a finite verb phrase constituent, and they are thus well
suited for the analysis of languages with free word order, such as Czech or Warlpiri.

<!--more-->

* TOC
{:toc}

Dependency v.s. Phrase Structure
--------------------------------

Dependency is a one-to-one correspondence: for every element (e.g. word or morph) in the sentence, there is exactly one 
node in the structure of that sentence that corresponds to that element. The result of this one-to-one correspondence is 
that dependency grammars are word (or morph) grammars. All that exist are the elements and the dependencies that connect 
the elements into a structure. This situation should be compared with phrase structure. Phrase structure is a 
one-to-one-or-more correspondence, which means that, for every element in a sentence, there is one or more nodes in the 
structure that correspond to that element. The result of this difference is that dependency structures are minimal
compared to their phrase structure counterparts, since they tend to contain many fewer nodes.

![Error loading dependency-vs-phrase-structure.png]({{ "/assets/img/dependency-vs-phrase-structure.png" | relative_url}})

The distinction between dependency and phrase structure grammars derives in large part from the initial division of the 
clause. The phrase structure relation derives from an initial binary division, whereby the clause is split into a
subject noun phrase (NP) and a predicate verb phrase (VP). This division is certainly present in the basic analysis of
the clause that we find in the works of, for instance, Leonard Bloomfield and Noam Chomsky. Tesnière, however, argued 
vehemently against this binary division, preferring instead to position the verb as the root of all clause structure. 
Tesnière's stance was that the subject-predicate division stems from **term logic** and has no place in linguistics.
The importance of this distinction is that if one acknowledges the initial subject-predicate division in syntax is real, 
then one is likely to go down the path of phrase structure grammar, while if one rejects this division, then one must 
consider the verb as the root of all structure, and so go down the path of dependency grammar.

Types of Dependencies
---------------------

The dependency representations above (and further below) show **syntactic dependencies**. Indeed, most work in
dependency grammar focuses on syntactic dependencies. Syntactic dependencies are, however, just one of 3 or 4 types of 
dependencies. [Meaning–text theory](https://en.wikipedia.org/wiki/Meaning%E2%80%93text_theory), for instance, emphasizes
the role of **semantic** and **morphological dependencies** in addition to syntactic dependencies. A fourth type,
**prosodic dependencies**, can also be acknowledged. Distinguishing between these types of dependencies can be
important, in part because if one fails to do so, the likelihood that semantic, morphological, and/or prosodic
dependencies will be mistaken for syntactic dependencies is great. The following four subsections briefly sketch each of 
these dependency types. During the discussion, the existence of syntactic dependencies is taken for granted and used as
an orientation point for establishing the nature of the other three dependency types.

### Semantic Dependencies

Semantic dependencies are understood in terms of [predicates](https://en.wikipedia.org/wiki/Predicate_(grammar)) and
their [arguments](https://en.wikipedia.org/wiki/Argument_(linguistics)). The arguments of a predicate are semantically 
dependent on that predicate. Often, semantic dependencies overlap with and point in the same direction as syntactic 
dependencies. At times, however, semantic dependencies can point in the opposite direction of syntactic dependencies, or 
they can be entirely independent of syntactic dependencies. The hierarchy of words in the following examples show
standard syntactic dependencies, whereas the arrows indicate semantic dependencies:

![Error loading semantic-dependencies.png]({{ "/assets/img/semantic-dependencies.png" | relative_url}})

1. The two arguments _Sam_ and _Sally_ in tree (a) are dependent on the predicate `likes`, whereby these arguments are
   also syntactically dependent on likes. What this means is that the semantic and syntactic dependencies overlap and
   point in the same direction (down the tree).
2. Attributive adjectives, however, are predicates that take their head noun as their argument, hence `big` is a
   predicate in tree (b) that takes `bones` as its one argument; the semantic dependency points up the tree and therefore 
   runs counter to the syntactic dependency. 
3. A similar situation obtains in (c), where the preposition predicate `on` takes the two arguments the `picture` and
   the `wall`; one of these semantic dependencies points up the syntactic hierarchy, whereas the other points down it. 
4. Finally, the predicate to help in (d) takes the one argument `Jim` but is not directly connected to `Jim` in the 
   syntactic hierarchy, which means that semantic dependency is entirely independent of the syntactic dependencies.

### Morphological Dependencies

Morphological dependencies obtain between words or parts of words. When a given word or part of a word influences the
form of another word, then the latter is morphologically dependent on the former. Agreement and concord are therefore 
manifestations of morphological dependencies. Like semantic dependencies, morphological dependencies can overlap with
and point in the same direction as syntactic dependencies, overlap with and point in the opposite direction of syntactic 
dependencies, or be entirely independent of syntactic dependencies. The arrows are now used to indicate morphological 
dependencies.

![Error loading mophological-dependencies.png]({{ "/assets/img/mophological-dependencies.png" | relative_url}})

1. The plural _houses_ in (a) demands the plural of the demonstrative determiner, hence _these_ appears, not _this_,
   which means there is a morphological dependency that points down the hierarchy from _houses_ to _these_.
2. The situation is reversed in (b), where the singular subject _Sam_ demands the appearance of the agreement suffix
   _-s_ on the finite verb _works_, which means there is a morphological dependency pointing up the hierarchy from _Sam_
   to _works_.

Morphological dependencies play an important role in
[typological studies](https://en.wikipedia.org/wiki/Linguistic_typology). Languages are classified as mostly
[head-marking](https://en.wikipedia.org/wiki/Head-marking_language) (_Sam work-s_) or mostly
[dependent-marking](https://en.wikipedia.org/wiki/Dependent-marking_language) (`these houses`), whereby most if not all 
languages contain at least some minor measure of both head and dependent marking.

### Prosodic Dependencies

Prosodic dependencies are acknowledged in order to accommodate the behavior of clitics. A clitic is a syntactically 
autonomous element that is prosodically dependent on a host. A clitic is therefore integrated into the prosody of its
host, meaning that it forms a single word with its host. Prosodic dependencies exist entirely in the linear dimension 
(horizontal dimension), whereas standard syntactic dependencies exist in the hierarchical dimension (vertical
dimension). Classic examples of clitics in English are reduced auxiliaries (e.g. _-ll_, _-s_, _-ve_) and the possessive 
marker _-s_. The prosodic dependencies in the following examples are indicated with the hyphen and the lack of a
vertical projection line:

![Error loading prosodic-dependencies.png]({{ "/assets/img/prosodic-dependencies.png" | relative_url}})

The hyphens and lack of projection lines indicate prosodic dependencies. A hyphen that appears on the left of the clitic 
indicates that the clitic is prosodically dependent on the word immediately to its left (_He'll_, _There's_), whereas a 
hyphen that appears on the right side of the clitic (not shown here) indicates that the clitic is prosodically dependent
on the word that appears immediately to its right. A given clitic is often prosodically dependent on its syntactic 
dependent (_He'll_, _There's_) or on its head (_would've_). At other times, it can depend prosodically on a word that is 
neither its head nor its immediate dependent (_Florida's_).

### Syntactic Dependencies

Syntactic dependencies are the focus of most work in DG, as stated above. How the presence and the direction of
syntactic dependencies are determined is of course often open to debate. In this regard, it must be acknowledged that
the validity of syntactic dependencies in the trees throughout this article is being taken for granted. However, these 
hierarchies are such that many DGs can largely support them, although there will certainly be points of disagreement.
The basic question about how syntactic dependencies are discerned has proven difficult to answer definitively. One
should acknowledge in this area, however, that the basic task of identifying and discerning the presence and direction
of the syntactic dependencies of DGs is no easier or harder than determining the constituent groupings of phrase
structure grammars. A variety of heuristics are employed to this end, basic tests for constituents being useful tools;
the syntactic dependencies assumed in the trees in this article are grouping words together in a manner that most
closely matches the results of standard permutation, substitution, and ellipsis tests for constituents. Etymological 
considerations also provide helpful clues about the direction of dependencies. A promising principle upon which to base
the existence of syntactic dependencies is distribution. When one is striving to identify the root of a given phrase,
the word that is most responsible for determining the distribution of that phrase as a whole is its root.

Opensource Software
-------------------

Packages written in Java that support dependency parsing include:

- [MaltParser](http://maltparser.org/)
- [MSTParser](http://sourceforge.net/projects/mstparser/)
- [Stanford Parser](http://nlp.stanford.edu/software/lex-parser.shtml) ([demo](http://nlp.stanford.edu:8080/parser/),
  see typed dependencies section)
- [RelEx](http://wiki.opencog.org/w/RelEx_Dependency_Relationship_Extractor)

Of these, the Stanford Parser is the most accurate. However, some configurations of the MaltParser can be insanely fast 
([Cer et al. 2010](http://www.lrec-conf.org/proceedings/lrec2010/pdf/730_Paper.pdf)).
