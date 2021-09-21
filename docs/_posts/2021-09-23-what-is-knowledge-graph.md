---
layout: post
title: What is a Knowledge Graph?
tags: [Data, Graph]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/24-cover.png"
thumbnail: "assets/img/post-cover/24-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

The knowledge graph represents a collection of interlinked descriptions of entities - objects, events or concepts.
Knowledge graphs put data in context via linking and semantic metadata and this way provide a framework for data
integration, unification, analytics and sharing.

<!--more-->

![Error loading Ontotext-offshore-zone-case.svg!]({{ "/assets/img/Ontotext-offshore-zone-case.svg" | relative_url}})

The knowledge graph (KG) represents a collection of interlinked descriptions of entities - real-world objects and
events, or abstract concepts (e.g., documents) – where:

* Descriptions have formal semantics that allow **both people and computers** to process them in an efficient and
  unambiguous manner;
* Entity descriptions contribute to one another, forming a network, where each entity represents part of the description
  of the entities, related to it, and provides context for their interpretation.

* TOC
{:toc}

## Key Characteristics

Knowledge graphs combine characteristics of several data management paradigms:

* **Database**, because the data can be explored via structured queries;
* **Graph**, because they can be analyzed as any other network data structure;
* **Knowledge base**, because they bear formal semantics, which can be used to interpret the data and infer new facts.

Knowledge graphs, represented in [RDF](#what-is-rdf), provide the best framework for data integration, unification,
linking and reuse, because they combine:

* **Expressivity** The standards in the Semantic Web stack - RDF(S) and OWL - allow for a fluent representation of
  various types of data and content: data schema, taxonomies and vocabularies, all sorts of metadata, reference and
  master data. The [RDF*](#rdf) extension makes it easy to model provenance and other structured metadata.
* **Performance** All the specifications have been thought out, and proven in practice, to allow for efficient
  management of graphs of  billions of facts and properties.
* **Interoperability** There is a range of specifications for data serialization, access (SPARQL Protocol for
  end-points), management (SPARQL Graph Store) and federation. The use of globally unique identifiers facilitates data
  integration and publishing.
* **Standardization** All the above is standardized through the W3C community process, to make sure that the
  requirements of different actors are satisfied – all the way from logicians to enterprise data management
  professionals and system operations teams.

![Error loading Plain-KG_KG_KGI-Ok.svg!]({{ "/assets/img/Plain-KG_KG_KGI-Ok.svg" | relative_url}})

## Ontologies and Formal Semantics

**[Ontologies](#what-are-ontologies)** represent the backbone of the formal semantics of a knowledge graph. They can be
seen as the data schema of the graph. They serve as a formal contract between the developers of the knowledge graph and
its users regarding the meaning of the data in it. A user could be another human being or a software application that
wants to interpret the data in a reliable and precise way. Ontologies ensure a shared understanding of the data and its
meanings.

When formal semantics are used to express and interpret the data of a knowledge graph, there are a number of
representation and modeling instruments:

* **Classes** Most often an entity description contains a classification of the entity with respect to a class
  hierarchy. For instance, when dealing with business information there could be classes _Person_, _Organization_ and
  _Location_. Persons and organizations can have a common superclass _Agent_. Location usually has numerous sub-classes,
  e.g., _Country_, _Populated place_, _City_, etc. The notion of class is borrowed by the object-oriented design, where
  each entity usually belongs to exactly one class.
* **Relationship Types** The relationships between entities are usually tagged with types, which provide information
  about the nature of the relationship, e.g., _friend_, _relative_, _competitor_, etc. Relationship types can also have
  formal definitions, e.g., that _parent-of_ is inverse relation of _child-of_, they both are special cases of
  _relative-of_, which is a symmetric relationship. Or defining that sub-region and subsidiary are transitive
  relationships.
* **Categories** An entity can be associated with categories, which describe some aspect of its semantics, e.g.,
  "_Big four consultants_" or "_XIX century composers_". A book can belong simultaneously to all these categories: 
  "_Books about Africa_", "_Bestseller_", "_Books by Italian authors_", "_Books for kids_", etc. The categories are
  described and ordered into taxonomy.
* **Free Text Descriptions** Often a 'human-friendly text' description is provided to further clarify design intentions
  for the entity and improve search.

## What is NOT a Knowledge Graph?

### Not Every RDF Graph is a Knowledge Graph

For instance, a set of statistical data, e.g. the GDP data for countries, represented in RDF is not a KG. A graph
representation of data is often useful, but it might be unnecessary to capture the semantic knowledge of the data. It
might be sufficient for an application to just have a string "Italy" associated with the string "GDP" and a number
'1.95 trillion' without needing to define what countries are or what the 'Gross Domestic Product' of a country is.
_**It's the connections and the graph that make the KG, not the language used to represent the data**_.

### Not Every Knowledge Base is a Knowledge Graph

A key feature of a KG is that **entity descriptions should be interlinked to one another**. The definition of one entity
includes another entity. This linking is how the graph forms. (e.g. A is B. B is C. C has D. A has D). Knowledge bases
without formal structure and semantics, e.g. Q&A "knowledge base" about a software product, also do not represent a KG.
It is possible to have an expert system that has a collection of data organized in a format that is not a graph but uses
automated deductive processes such as a set of 'if-then' rules to facilitate analysis.

## Examples of Big Knowledge Graphs

### Google Knowledge Graph

Google made this term popular with the announcement of its knowledge graph in 2012. However, there are very few
technical details about its organization, coverage and size. There are also very limited means for using this knowledge
graph outside Google's own projects.

### [DBPedia](https://www.dbpedia.org/)

[This project](https://www.dbpedia.org/) leverages the structure inherent in the infoboxes of Wikipedia to create an
enormous dataset of 4.58 things (link https://wiki.dbpedia.org/about ) and an ontology that has encyclopedic coverage of
entities such as people, places, films, books, organizations, species, diseases, etc. This dataset is at the heart of
the Open Linked Data movement. It has been invaluable for organizations to bootstrap their internal knowledge graphs
with millions of crowdsourced entities.

### [Geonames](https://www.geonames.org/)

Under a creative commons, users of [Geonames](https://www.geonames.org/) dataset have access to 25 million geographical
entities and features.

### [Wordnet](https://wordnet.princeton.edu/)

One of the most well-known lexical databases for the English language, providing definitions and synonyms. Often used to
enhance the performance of NLP and search applications.

### [FactForge](https://www.ontotext.com/knowledgehub/demoservices/factforge-explore-linked-open-data/)

After years of developing expertise in the news publishing industry, Ontotext produced their knowledge graph of Linked
Open Data and news articles about people, organizations and locations. It incorporates the data from the KGs described
above as well as specialized ontologies such as the Financial Industry Business Ontology.

## Reference

### What is RDF?

### RDF*

### What are Ontologies?

![Error loading What-is-Ontology.png!]({{ "/assets/img/What-is-Ontology.png" | relative_url}})

An ontology is a formal description of knowledge as a set of concepts within a domain and the relationships that hold
between them. To enable such a description, we need to formally specify components such as individuals (instances of
objects), classes, attributes and relations as well as restrictions, rules and axioms. As a result, ontologies do not
only introduce a sharable and reusable knowledge representation but can also add new knowledge about the domain.

The ontology data model can be applied to a set of individual facts to create a knowledge graph - a collection of
entities, where the types and the relationships between them are expressed by nodes and edges between these nodes, By
describing the structure of the knowledge in a domain, the ontology sets the stage for the knowledge graph to capture
the data in it.

There are, of course, other methods that use formal specifications for knowledge representation such as vocabularies,
taxonomies, thesauri, topic maps and logical models. However, unlike taxonomies or relational database schemas, for
example, ontologies express relationships and enable users to link multiple concepts to other concepts in a variety of
ways.

As one of the building blocks of Semantic Technology, ontologies are part of the W3C standards stack for the Semantic
Web. They provide users with the necessary structure to link one piece of information to other pieces of information on
the Web of Linked Data. Because they are used to specify common modeling representations of data from distributed and
heterogeneous systems and databases, ontologies enable database interoperability, cross-database search and smooth
knowledge management.

![Error loading Semantic-Web-Technology-Stack_01-768x502-1.png!]({{ "/assets/img/Semantic-Web-Technology-Stack_01-768x502-1.png" | relative_url}})

#### Ontologies for Better Data Management

Some of the major characteristics of ontologies are that they ensure a common understanding of information and that they
make explicit domain assumptions. As a result, the interconnectedness and interoperability of the model make it
invaluable for addressing the challenges of accessing and querying data in large organizations. Also, by improving
metadata and provenance, and thus allowing organizations to make better sense of their data, ontologies enhance data
quality.

#### The OWL Standard and Ontology Modelling

In recent years, there has been an uptake of expressing ontologies using ontology languages such as the Web Ontology
Language (OWL). OWL is a semantic web computational logic-based language, designed to represent rich and complex
knowledge about things and the relations between them. It also provides detailed, consistent and meaningful distinctions
between classes, properties and relationships.

By specifying both object classes and relationship properties as well as their hierarchical order, OWL enriches ontology
modeling in semantic graph databases. OWL, used together with an OWL reasoner in such enables consistency checks (to
find any logical inconsistencies) and ensures satisfiability checks (to find whether there are classes that cannot have
instances).

Also, OWL comes equipped with means for defining equivalence and difference between instances, classes and properties.
**These relationships help users match concepts even if various data sources describe these concepts somewhat
differently**. They also ensure the disambiguation between different instances that share the same names or
descriptions.

#### The Benefits of Using Ontologies

![Error loading The-Benefits-of-Using-Ontologies.png!]({{ "/assets/img/The-Benefits-of-Using-Ontologies.png" | relative_url}})

One of the main features of ontologies is that, by having the essential relationships between concepts built into them,
they enable automated reasoning about data. Such reasoning is easy to implement in semantic graph databases that use
ontologies as their semantic schemata.

What's more, ontologies function like a 'brain'. They 'work and reason' with concepts and relationships in ways that are
close to the way humans perceive interlinked concepts.

In addition to the reasoning feature, ontologies provide more coherent and easy navigation as users move from one
concept to another in the ontology structure.

Another valuable feature is that ontologies are easy to extend as relationships and concept matching are easy to add to
existing ontologies. As a result, this model evolves with the growth of data without impacting dependent processes and
systems if something goes wrong or needs to be changed.

Ontologies also provide the means to represent any data formats, including unstructured, semi-structured or structured
data, enabling smoother data integration, easier concept and text mining, and data-driven analytics.

#### Limitations of Ontologies

While ontologies provide a rich set of tools for modeling data, their usability comes with certain limitations.

One such limitation is the available property constructs. For example, while providing powerful class constructs, the
most recent version of the Web Ontology Language – OWL2 has a somewhat limited set of property constructs.

Another limitation comes from the way OWL employs constraints. They serve to specify how data should be structured and
prevent adding data inconsistent with these constraints. This, however, is not always beneficial. Often, data imported
from a new source into the RDF triplestore would be structurally inconsistent with the constraints set using OWL.
Consequently, this new data would have to be modified before being integrated with what is already loaded in the
triplestore.

A novel alternative to using ontologies to model data is using the Shapes Constraint Language (SHACL) for validating RDF
graphs against a set of constraints. A shape specifies metadata about a type of resource - how it is used, how it should
be used and how it must be used. As such, similarly to OWL, SHACL can be applied to validate data. Unlike OWL, however,
SHACL can be applied to validate data that is already available in the triplestore.

#### Ontology Use Cases

##### Hypotheses Testing in Pharma

In the pharmaceutical research and discovery process, success is highly dependent on the availability and accessibility
of high-quality research data. The quality of the data can be assessed by its accuracy, correctness, completeness,
currency and relevance. The accuracy and the correctness of data are purely defined by the methods used to generate the
data. However, the latter three – completeness, currency and relevance – could be determined partially or completely by
an effective
[semantic data integration approach](https://www.ontotext.com/knowledgehub/fundamentals/semantic-data-integration/),
which:

* aggregates all relevant information;
* removes redundancy and ambiguities in the data;
* interlinks the related entities.

Researchers gather information from a broad range of biomedical data sources in an iterative way in order to generate or
expand a certain theory, to test hypotheses, to make informed assertions about which relationships are causal and about
exactly how they are causal. They need a mechanism that would allow them to mine all data scattered among different
relevant resources and to identify visible (direct) and invisible (distant) relations between biomedical entities
studied in the pharmaceutical research and discovery process.

###### The Solution: Linked Life Data Cloud

Semantic warehousing helps researchers get an overview of the existing relationships within scientific and clinical data
by utilizing causality data mining. [Linked Life Data](http://linkedlifedata.com/) is used as a platform for Interactive Relationship Discovery
between biomedical entities as it:

* integrates over 25 diverse data sources;
* aligns the data to more than 17 different biomedical objects (genes, proteins, molecular functions, biological processes/pathways, molecular interactions, cell localization, organisms, organs/tissues, cell lines, cell types, diseases, symptoms, drugs, drug side effects, small chemical compounds, clinical trials, scientific publications, etc.);
* identifies explicit relationships between entities locked in the original data sets and categorizes them to a causality relationship ontology;
* mines unstructured data in order to identify relationships hidden within the text (inclusion/exclusion criteria for clinical studies).

![Error loading LS_CAUSALITY-1.png!]({{ "/assets/img/LS_CAUSALITY-1.png" | relative_url}})

Since the entities in Linked Life Data are usually strongly interlinked, in most cases the approach for simply
crawling/querying the repository for relationships and listing them is not sufficient. That’s why Linked Life Data also
provides a user-centered process and interactive tools for assisting the discovery of even very large numbers of causal
relations.

![Error loading relfinder_0-2.png!]({{ "/assets/img/relfinder_0-2.png" | relative_url}})

### List of KG Products/Graph Databases

* [tiddlyroam](https://tiddlyroam.org/)
  
* [Ontotext GraphDB](https://www.ontotext.com/products/graphdb/)
* [Memgraph](https://memgraph.com/)
* [GDB by rankings](https://db-engines.com/en/ranking/graph+dbms)