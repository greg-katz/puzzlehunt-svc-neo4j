# puzzlehunt-svc-neo4j

TODO: Describe what this repo was/is about


Outline:
- Project goals:
  - Get to "level 1" of experience with Neo4j, GraphQL, and Java's support for those two things
  - Get to "level 1" answers to the questions "what are graph databases good for, and when would you reach for one?"
  - Get to "level 1" answers to the questions "what is GraphQL good for, and when would use reach for it?"
- Non-goals:
  - Production readiness
- Baises:
  - We're Java devs, so we tried to make things work in Java
- Faux problem we solved: Write a service for a ClueKeeper-esque puzzlehunt app that stores its data in Neo4j

# A word about project structure
The puzzlehunt-svc-neo4j-app module contains the main runnable application which includes all the sub-modules where we tried experiments with different implementation approaches.

The puzzlehunt-svc-neo4j-app-graphql-java module is a second runnable application that you probably shouldn't be too interested in - the more substantial GraphQL experiments are still part of the puzzlehunt-svc-neo4j-app module. More details below.

# What we tried

The core of this project is a Neo4j database that was loaded with our puzzle-hunt schema represented by the load-data.cypher and add-constraints.cypher scripts. All the experiments used this database as their backend and demonstrate different ways to get data in and out of it.

##### 1st experiment: Spring MVC APIs with DAOs that use the Neo4j Java driver
This first and most basic experiment is in the puzzlehunt-svc-neo4j-driver module. This module contains Spring controllers prefixed with /driver, some simple POJO objects to define our entities, and DAO objects that run Cypher queries to perform queries and make updates. 

##### 2st experiment: Spring MVC APIs with Neo4j OGM
This experiment is in the puzzlehunt-svc-neo4j-ogm module and has endpoints prefixed with /ogm. The idea was to try Neo4j's OGM module as the implementation of our DAOs. We didn't do much with this experiment because Spring Data also uses OGM and seemed like it was going to add some functionality to make it more fun to work with, so we jumped ahead after writing just one proof-of-concept OGM example.

##### 3rd experiment: Spring MVC APIs with Neo4j OGM and Spring Data
This experiment is in the puzzlehunt-svc-neo4j-springdata module and has endpoints prefixed with /springdata. This experiment has a full OGM-annotated model for our database and a few example controllers. The TeamsController in particular got a lot of attention as an example of how REST APIs for this data model might really be built.

##### 4rd experiment: GraphQL API with autogenerated queries 
This experiment is in the puzzlehunt-svc-neo4j-graphql module and its only endpoint is the GraphQL API at /graphql. This started as an experiment where we used the neo4j-graphql-java library to transform GraphQL queries and mutations into Cypher queries, execute them, and return the result. It evolved into an integration with the graphql-java library where these autogenerated Cypher queries are the implementation of each top-level query/mutation in our GraphQL schema, but where custom DataFetchers can still be plugged in for specific properties when needed.

This experiment also includes support for the graphiql client, which can be accessed at /graphiql.

# What we concluded

TODO: GraphQL + Neo4j + autogeneration of a single Cipher query for each request + optional overriding of property fetching = seems like a powerful solution that covers a lot of bases

# Learnings about libraries

TODO: What each of the various neo-graphql-java-spring-nani libraries are useful for

# Musings on Neo4j

TODO

# Musings on GraphQL

TODO

# Helpful resources

TODO: Links to particularly useful docs/resources

# Things to do next

## Improvements to make

- TODO: Features to implement to learn how they could be made to work/what power they add to the solution
- TODO: Code to clean up
- TODO: Tests to add
- TODO: Docs to write

## Research avenues to explore

### GRANDstack

- TODO: GRANDstack (pure - no Java)
- TODO: Subscriptions
- TODO: Caching
- TODO: Large data set vs query performance
