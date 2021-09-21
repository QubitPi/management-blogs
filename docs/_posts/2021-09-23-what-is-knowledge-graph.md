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
* Interoperability: There is a range of specifications for data serialization, access (SPARQL Protocol for end-points),
  management (SPARQL Graph Store) and federation. The use of globally unique identifiers facilitates data integration
  and publishing.
* Standardization: All the above is standardized through the W3C community process, to make sure that the requirements
  of different actors are satisfied – all the way from logicians to enterprise data management professionals and system
  operations teams.

![Error loading Plain-KG_KG_KGI-Ok.svg!]({{ "/assets/img/Plain-KG_KG_KGI-Ok.svg" | relative_url}})

## Reference

### What is RDF?

### RDF*