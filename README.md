# puzzlehunt-svc-neo4j

**Tl;dr:** Two Java devs set out to learn about Neo4j and GraphQL. This repo is the result. 

In the summer of 2020, we set out to get a bit of practical experience with Neo4j and GraphQL by writing an API for a fictional app that supports players competing in a [puzzlehunt](https://en.wikipedia.org/wiki/Puzzlehunt) (*à la* [DASH](http://playdash.org/), which uses the [ClueKeeper](https://www.cluekeeper.com/) app). 

The API's job is to provide CRUD operations for entities in the (simplified) puzzlehunt, the entities being:

- Hunts
- Leaderboards (ranked teams and their times)
- Teams
- Players
- Puzzles
- Hints
- Partial solutions (which let a team know whether they are on the right track to solving a puzzle)

We imagined the following data model for these entities:

```
Hunt
----
name: string
start: date
end: date
puzzles: Puzzle[] (ordered)
teams: Team[]

Leaderboard
-----------
teamResults: TeamResult[]

TeamResult
----------
name: string (team name)
finished: boolean
score: int
time: duration

Team
----
name: string
captain: Person
players: Person[]
progress: HuntProgress (spoiler: this ends up being stored on team->puzzle relationships)

Person
------
name: String

Puzzle
------
name: string
hints: Hint[] (ordered)
answer: string
partialSolution: PartialSolution[]
par: int
points: int

Hint
----
text: text (or string? Does Neo make a distinction?)
unlockMins: int
cost: int

PartialSolution
---------------
guess: string
response: string
```

We also imagined some operations to perform on these entities, including:

- Get the leaderboard for a hunt
- Get all teams including their captains and players
- Update a team's roster, including moving a player from one team to another

# Goals and non-goals

What we were trying to accomplish:

  - Gain "level 1"  experience with Neo4j, GraphQL, and Java's support for those two things.
  - Develop "level 1" answers to the questions "what are graph databases good for, and under what circumstances should you consider using one?"
  - Develop "level 1" answers to the questions "what is GraphQL good for, and under what circumstances should you consider using it?"
  - Side goal: Learn Java 14, including preview features
  - Side goal: Learn about the Java module system and its value in a multi-module project like this one.

Non-goals:

  - Production readiness. This was very much a learning exercise, with no pretense of shippability.

Biases:

  - We're Java devs, so we tried to make things work in Java.
  - We focused on solving an OLTP/realtime problem rather than an analytical/batch one.

# A word about project structure

The [puzzlehunt-svc-neo4j-app](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-app) module contains the main runnable application which includes all the sub-modules where we tried experiments with different implementation approaches.

The [puzzlehunt-svc-neo4j-app-graphql-java](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-app-graphql-java) module is a second runnable application that you probably shouldn't be too interested in - the more substantial GraphQL experiments are still part of the puzzlehunt-svc-neo4j-app module. More details below.

# What we tried

The core of this project is a Neo4j database that was loaded with our puzzlehunt schema represented by the load-data.cypher and add-constraints.cypher scripts. All the experiments used this database as their backend and demonstrate different ways to get data in and out of it.

##### 1st experiment: Spring MVC APIs with DAOs that use the Neo4j Java driver

This first and most basic experiment is in the [puzzlehunt-svc-neo4j-driver](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-driver) module. This module contains Spring controllers prefixed with /driver, some simple POJO objects to define our entities, and DAO objects that run Cypher queries to perform queries and make updates. 

##### 2nd experiment: Spring MVC APIs with Neo4j OGM

This experiment is in the [puzzlehunt-svc-neo4j-ogm](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-ogm) module and has endpoints prefixed with /ogm. The idea was to try Neo4j's OGM module as the implementation of our DAOs. We didn't do much with this experiment because Spring Data also uses OGM and seemed like it was going to add some functionality to make it more fun to work with, so we jumped ahead after writing just one proof-of-concept OGM example.

##### 3rd experiment: Spring MVC APIs with Neo4j OGM and Spring Data Neo4j

This experiment is in the [puzzlehunt-svc-neo4j-springdata](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-springdata) module and has endpoints prefixed with /springdata. This experiment has a full OGM-annotated model for our database and a few example controllers. The TeamsController in particular got a lot of attention as an example of how REST APIs for this data model might really be built.

##### 4th experiment: GraphQL API with autogenerated queries 

This experiment is in the [puzzlehunt-svc-neo4j-graphql](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-graphql) module and its only endpoint is the GraphQL API at /graphql. This started as an experiment where we used the neo4j-graphql-java library to transform GraphQL queries and mutations into Cypher queries, execute them, and return the result. It evolved into an integration with the graphql-java library where these autogenerated Cypher queries are the implementation of each top-level query/mutation in our GraphQL schema, but where custom DataFetchers can still be plugged in for specific properties when needed.

This experiment also includes support for the GraphiQL client, which can be accessed at /graphiql.

# What we concluded

TODO:
- REST API design is still a pain
- In practice, reference cycles are a PITA for Spring OGM/Spring Data Neo4j
- If you *don't* want to use GraphQL, Spring Data Neo4j is a good choice (you get CRUD for cheap)
- Neo4j OGM doesn't seem to have a use case on its own (just use Spring Data Neo4j - it uses OGM under the hood)
- GraphQL is cool! (cycles are solved; API design is fun)
- GraphQL + Neo4j and automagic Cypher query generation are a kind of "sweet spot" - by default, a GraphQL query is turned into a single Cypher query that resolves the entire requested object graph (meaning fewest possible trips to the database), while the option to plug in property resolvers is still available if the single query approach doesn't satisfy (when doing special processing of properties, or fetching properties from other data sources)
- Neo4j's Java driver is a perfectly fine choice for certain use cases (if your whole world is made up of leaderboard-style queries, for example - "I have a gnarly Cypher query and I want to expose its results via a URL")

TODO: GraphQL + Neo4j + autogeneration of a single Cipher query for each request + optional overriding of property fetching = seems like a powerful solution that covers a lot of bases

# Graphs with cycles: How OGM unintentionally brings the pain

When loading a node and its related nodes from Neo4j, Neo4j OGM provides a depth parameter to limit the amount of related nodes that come along for the ride. Pretty sensible - without some sort of limit, fetching a node could inadvertently fetch the entire graph. 

Neo4j OGM's heart is in the right place. When fetching Neo4j nodes and relationships, it goes above and beyond in two ways: *object deduplication* and *property filling*.

When Neo4j OGM fetches a node from Neo4j, it checks to see if it has already fetched that node before in the same session. If it has, rather than creating another copy of that node in memory, OGM instead refers to that previously fetched node. For example, if I'm fetching all of the teams for a given hunt and each team has a hunt property, each team object's hunt property will refer to the same hunt object. This is a nice savings of potential round trips to the database to fetch the same object, and it avoids creating duplicates of the same object in memory.

Even more helpfully, when Neo4j OGM creates an object, if that object has properties that reference objects OGM has already fetched during the same session, OGM will set the object's properties to refer to those already-fetched objects. For example, if a team has a captain property referring to a person and that person was previously fetched, OGM will set the captain property to point to that person object. Why not? Setting this property, even if it isn't needed to satisfy the depth parameter, is cheap, since the object is already in memory.

This seems like a good thing, but actually:

![It's a trap!](https://i.kym-cdn.com/entries/icons/facebook/000/000/157/itsatrap.jpg)

If I ask for a team and one more level of depth so that I also get its captain property filled in, what I end up with is a *cycle* - the team object refers to the captain object and the captain object refers back to the exact same team object in memory.

OGM's "kindness" causes trouble for naive algorithms that try to walk the object graph as though it is acyclic. We got bitten by this helpfulness during two kinds of serialization: JSON generation and Lombok's autogenerated toString() implementations. In both cases, the manifestations were StackOverflowErrors in library code (that we didn't have much control over). The bites were so painful that we ended up changing our Java data model to remove "back references" - we removed the team property from the captain object, for example. In other words, we were forced to impoverish our Java data model to use Neo4j OGM in conjunction with other libraries. 

# Compared to REST, GraphQL is a breath of fresh air

TODO

# Learnings about libraries

There are some different but similarly named libraries that are important to this project.

[graphql-java](https://github.com/graphql-java/graphql-java) is the primary Java implementation of GraphQL. It provides classes for defining a GraphQL schema by schema definition, then plugging in classes like DataFetchers to implement the operations, then executing a GraphQL query by calling the appropriate DataFetchers.

[neo4j-graphql-java](https://github.com/neo4j-graphql/neo4j-graphql-java) is a Java library for automatically transforming GraphQL queries into Cypher queries. Its primary class is the Translator, which gets built from a GraphQL schema (it uses graphql-java internally, so the schema object it creates is the same type as that library) and once set up allows you to translate any GraphQL query into Cypher.

Another important feature of this library is that when you invoke its SchemaBuilder, it automatically adds query and mutation types to the schema for CRUD operations of all of your data types. These CRUD operations are thus what the Translator knows how to turn directly into Cypher just by analyzing your GraphQL schema.

Although neo4j-graphql-java uses graphql-java internally it doesn't naturally plug into graphql-java so you can run DataFetchers that use Cypher translation. But this integration is exactly what we built in our puzzlehunt-svc-neo4j-graphql module.

[graphql-java-spring](https://github.com/graphql-java/graphql-java-spring) This is a Spring library providing controllers for implementing a GraphQL API on top of the graphql-java framework. We tried this as an experiment in our secondary puzzlehunt-svc-neo4j-app-graphql-java application but didn't use it in our primary GraphQL implementation in the puzzlehunt-svc-neo4j-graphql module.

Two reasons we weren't able to use this library:
1. As of this experiment there is an irreconcilable version mismatch where graphql-java-spring and neo4j-graphql-java both depend on different versions of graphql-java and neither will work with the version specified by the other. This is why the experiment we did with graphql-java-spring had to be is a separate application module - because it can't be on the classpath at the same time as neo4j-graphql-java.
2. The implementation we wound up with in the puzzlehunt-svc-neo4j-graphql module involved some custom work in the controller to generate Cypher queries that could be looked up by the CypherDataFetcher later. This meant we couldn't use the out-of-the-box controllers provided by graphql-java-spring even if they worked (well, maybe we could. If graphql-java-spring and neo4j-graphql-java didn't have their version incompatibility we could perhaps have done our extra work in a ControllerAdvice or Filter and still taken advantage of Spring's controller. Could be work looking into in the future).    

# Musings on Neo4j

- If your problem domain has a lot of many-to-many relationships, this can be a hint that it's better suited to a graph database. Join tables in a RDBMS are not only a pain but are inefficient to traverse.
  - On the same note, if your problem domain places a lot of importance on relationships in addition to the entities themselves, this can be a clue that you'll probably wind up needing to write queries that are better suited for a graph.
  - And the former is especially true at scale.
- One notion is the "whiteboard test", which essentially means draw your entities and relationships for your domain, then think about the queries you'll need to write. If they involve traversing through many relationships that's when you should probably be thinking about graph databases. 
- Graph databases have "index free adjacency", which means that nodes and their relationships are put on disk near each other to make traversals fast. Obviously the details here could differ greatly but a generality may be that this makes graphs that much better for queries that traverse relationships but also worse for queries that are shallow, since relational databases are more likely to lay out rows of a table adjacent to each other and thus be naturally better at looking up entities of the same type when you aren't doing joins.
- Speculative answer to the "when relational database" question: Perhaps relational databases aren't really inherently better at much, and it's just that they're good enough for a lot of cases and are more mature. If you aren't writing massively relationship-heavy queries or aren't at massive scale doing so, an RDBMS may be fine, and they still have much larger communities so it's going to be much easier to find people and technologies that know them/integrate with them, and they may have fewer bugs/quirks that will bite you. Perhaps in another 10 years graph databases will be the default answer when you need a flexible data store.
  - On the same note, it's easier to find people (both engineers and less technical folks) who know SQL and relational databases. At the moment, hiring experts in graph databases is probably hard, and offering graph data via Cypher queries is a relatively high barrier compared to offering relational data via SQL. In THE FUTURE, GQL might lower the barrier to entry a little bit by providing a standard graph query language analogous to SQL for RDBMSs. 
- Video that has some good info on "when to graph": A Practical Guide to Graph Databases - David Bechberger (info on this subject starts around 22:50)
  - Some takeaways: 
    - RDBMS is better for aggregation queries.
    - "Search and Selection queries (find people with name like x) are probably better in a RDBMS or search service like ElasticSearch or Solr.
    - Graphs are obviously better for queries that use relationships… Stuff like "how are x and y related", or "who is the most influential person in a social network".
    - "Pattern Matching" queries may or may not be best solved by graph - if the patterns involve finding similar clusters of nodes, then probably yes (good canonical use case of this is "does this transaction look like known fraudulent transactions"?).

# Musings on GraphQL

- GraphQL is strongly typed and allows its type system to be introspected, both by machines and humans. You can even introspect the mutation operations. The GraphiQL UI is easy to build right into your server so that humans have a tool to browse the schema and run queries and mutations.
- Our current impression of the "standard" GraphQL API is that it supports GET AND POST for queries and POST for mutations. This can make things awkward but not impossible if you want your requests to take advantage of HTTP verb semantics - as an example the [Apollo Server](https://www.apollographql.com/docs/apollo-server/) describes a scheme where you can set "cacheControl" hints on the types in your schema and the overall cache-control header for a GET request is set to the lowest value from all the types included in the query.
  - I haven't seen any mention of PUT or DELETE in GraphQL servers, and I can see why it might be hard for a client to know when to use them. If you happened to have a case where some of the semantics for these verbs were important (just as an example, maybe it's a big win that intermediate caches can add the PUT object automatically during that request) then GraphQL wouldn't make this easy.
- Is GraphQL for other content types a thing? I'd guess it may not be that bad to produce XML if somehow you really wanted to, but certainly seems like something like protobuf is off the table.
- One of the touted benefits is rapid iteration on the frontend, the use case being where you're in the habit of producing UI-specific endpoints to avoid problems of over and under-fetching and you need backend engineers to produce an API for your new prototype. With a GraphQL API you may just be able to request the data in a different shape and then be done.
  - This seems cool, but some potential counterpoints are that a new prototype that doesn't just require moving data around but also requires fetching something new or doing some new operation will still require a backend engineer. And to the extent that GraphQL makes it harder to do efficient queries on the backend there may be a price to pay here (though we have more speculations below on whether GraphQL actually does make it harder to be efficient on the backend - this is probably something we'd have to try for real to understand better).
      - Counter-counterpoint: [Schema stitching](https://www.apollographql.com/blog/graphql-schema-stitching-8af23354ac37/) could be a way to declaratively combine multiple GraphQL APIs together into a mashup API (in a way that doesn't require a back-end engineer). Another approach is Apollo Server's [federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) concept. In federation, Apollo Server creates a gateway to multiple GraphQL servers and allows a schema to declare that it is stitching together results from those servers.
- Speculation: GraphQL is not necessarily less performant but making it performant may require more up-front work and infrastructure (building out per-field distributed caching, for example) than a more traditional approach. If you have solutions to these problems already maybe there's little downside, but if you don't it may force you down that path to avoid the horror of running a database query per object resolved.
  - It does seem natural to think that a GraphQL server's resolver approach will lead you towards a "one query per object" approach, but it doesn't have to. Batching and caching aside there's nothing technically stopping you from filling in subobjects in a higher-level resolver so you can get more in one shot. Doing this intelligently may require knowing more about the overall graphql query than you get by default in a resolver, kind of like our Cypher translator implementation uses the entire GraphQL query to build a top-level Cypher query that doesn't overfetch. 
  - While it's neat to have the flexibility to use higher level resolvers to get sub-objects in one query, whether this is actually faster may depend on your database. It seems pretty natural to think that it will be in a graph database since getting a sub-object just means traversing the relationships of the objects you've already found. But in a relational database is it faster to do one query with a JOIN or to run two queries? It's going to depend, so it's harder to give a straightforward answer.
- Discoverability of the API seems like a great natural benefit. REST with HATEOS claims to do this, but still then requires multiple queries to pull things together.
- A potential benefit of GraphQL is as a standard for defining, publishing, and consuming APIs and schemas across an organization. REST doesn't provide the same first-class support for type system and query/mutation introspection or human-readable documentation.
- A potential downside: GraphQL is still young compared to REST, so the servers and libraries are still relatively immature and thin on the ground. The community is also smaller. For Java devs, a particular downside is that the community cares primarily about JavaScript.
- Maybe GraphQL in practice would actually be faster… Separate from GraphQL we've mused about the idea that it'd be good for a model-making server to fetch independent bits of its model in parallel. It kind of seems like the GraphQL resolver approach will set you up for this automatically. Perhaps there are still places in the tree where you have a complex linear process to do that involves parameter passing, but in the worst case you can still call a Maker-style method to fill in a chunk of the graph. But since implementations of GraphQL servers are encouraged to parallelize their resolver invocations as much as possible it seems like the resolver/DataFetcher approach might inherently be giving you the level of parallelization you're aiming for - parallelization between fetching peer data, but a guarantee that parent data gets fetched before its children.
- Final speculation: Maybe you should just do GraphQL in most cases? It seems like it has a lot of benefits:
  - Consistent API
  - Discoverable
  - Clients get everything in one query
  - Clients don't underfetch/overfetch
  - API gets a type system that's automatically enforced
  - Free parallelization?
  - Bidirectional object relationships are no problem (this was a big pain with our other experiments)
- So why not? Concerns/thoughts we've identified - some of this may be inherent, and some may be things we just haven't figured out how to do yet.
  - Complex parameter validation (the type system should give you much of this but maybe think of something more like validation of locales, or validation that a password and "confirm password" field have the same value). Maybe a directive thing?
- Java server world seems less mature than the Node server world. On the flipside, we still got pretty far with our Java server in a week. Still, you may need to enter the Node universe to use the most well supported already-existing servers/clients.
- The use case of a server that knows about and can release with its client still doesn't feel like a slam dunk exactly. These issues of underfetching/overfetching/discoverability don't actually seem that important in that case (but maybe free parallelization would still be a win).

# Musings on the Java module system

- It seems unfortunate that any package that has a Spring bean needs to be opened. Makes it seem like a big pain not to open the entire module if it's a Spring module.
- Not sure how big a deal this is really, but a theoretical case where the module system won't help is in a "runtime dependency" between two library modules. Like if one reflectively accesses a class from the other (maybe think something like autowiring a list of beans of a type where the other library contributes one. The "runtime" permissions that get applied are only those of the module of the running application, so you wouldn't really notice this kind of runtime dependency between two library modules.
- I am feeling like one of the main wins of the module system is the explicit declaration of your own module's public classes. Even without modules a conversation is provoked when one library depends on another for the first time (because you don't have the maven dependency) but the module system allows more discussions to be provoked when someone tries to use a class from your module that you thought of as internal-only.
- Modules add some additional pain during development because IDEA's tooling isn't good enough to prevent it. If you are in a Java class and you reference another class by adding an import statement for it, IDEA can give you a suggested action to also require the module of the imported class. Likewise, if the class you want to reference is already required but not imported then IDEA will offer its normal import suggestions for you. But if you need to reference a class that is neither imported nor required IDEA doesn't make it easy. You need to manually hunt down that class's package or module and add either the import or require statement by hand before IDEA will offer any help with the other.
- At more than one point in the course of this project the module system bit us because libraries we're using aren't playing nice with it.
    - We had to manually hack on the neo4j-ogm-bolt-native-types jar and put a new version in our local repositories because the version of it in Maven Central had an empty module name in its manifest and as a result would cause an error when included on the module path.
    - We also ran into an error with two modules (java.annotation and jsr305) contributing the same package (javax.annotation), which is not allowed. We never took the time to figure out how to solve this within the module system, and instead de-moduleified our application as a workaround (it is why the module-info.java file in [puzzlehunt-svc-neo4j-app](https://github.com/greg-katz/puzzlehunt-svc-neo4j/tree/master/puzzlehunt-svc-neo4j-app) was renamed to have a bogus suffix).
- Overall impression of the module system from these experiments is that it's still a pain. If you're a library author you should probably support it and make clear your public API - there is some benefit to this and at any rate you should want to play nice with your users whether they use modules or not. For applications that you own and run yourself I'm less sold on it - it seems like it made several aspects of development notably more difficult, which was not a worthy tradeoff in a small experimental project like this. But perhaps it's still worth it in a project with a larger and more established repository with a lot of parallel development.

# Musings on Java 14

The best thing about Java 14 for this project was text blocks (still a preview feature in Java 14). When Cypher queries were embedded in Java, we appreciated the readability of the triple double quote syntax for multi-line strings.

The worst thing is that IDEA apparently doesn't have preview enabled when using the "evaluate expression" command in the debugger, so if you used that command to execute code that contained a text block it would throw an exception.

# Musings on architecture
- Is the desire for a GraphQL API another good reason to have a "domain data service"? Previously I was pretty down on the concept unless you're really in a place where you need the layer of flexibility between your data model and your clients. But if you want a GraphQL API, where else would you put it?
  - I guess just having a GraphQL API for a data domain doesn't mean that internal services using that data domain need to use it... I could see some kind of compromise here where services that occupy that data domain do their own database calls to avoid issues of performance/scalability, and because they're more likely owned by the same team and thus have fewer issues with data model compatibility. But maybe "casual" users of the data domain get their data through the GraphQL API? Possibly a slippery slope here because what is a "casual" user, but I could at least see this as a potential compromise.    

# Getting started

TODO: How to get the service and database up and running

# Helpful resources

TODO: Links to particularly useful docs/resources

## Neo4j and service URLs

- http://localhost:7474/browser/ (Neo4j)
- TODO: Driver implementation URLs (curl commands?)
- http://localhost:8082/springdata/hunts (all hunts)
- http://localhost:8082/springdata/hunts/2c2b2203-ced6-452d-b4dc-9ee601d304e1 (DASH 11)
- http://localhost:8082/springdata/hunts/2c2b2203-ced6-452d-b4dc-9ee601d304e1/teams (DASH 11's teams)
- http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910 (DASH 11's Team 1)
- http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/captain (Team 1's captain
- http://localhost:8082/springdata/teams/66af7be3-7240-48a9-9e19-f2ff0e885910/players (Team 1's players)
- TODO: GraphQL implementation example queries and mutations as GraphiQL URLs

# Things to do next

## Improvements to make to the "GraphQL, mostly straight to Cypher" implementation

- Implement an example of a property in a GraphQL schema that isn't in the Neo4j database, but that is resolved completely from a custom DataFetcher.
- Try implementing a custom scalar (a date or currency formatter, say).
- How would you implement a not-so-simple business rule via a GraphQL query? For example, what if you wanted to implement this rule?: If a player is switched off of a team and that player was the captain, null out the captain property?
- Figure out how to create UUIDs automagically when doing create mutations.
- How do you support parameterized sorting/ordering via a GraphQL query?
- Figure out where security concerns like XSS handling (stripping on the way in or escaping on the way out) get implemented in GraphQL.
- Figure out how to add a complex validation rule for a mutation.
- TODO: Any other features to implement to learn how they could be made to work/what power they add to the solution?
- TODO: Code to clean up
- TODO: Tests to add
- TODO: Docs to write

## Other things to try

- Can you use an in-memory Neo4j database to enable build time integration tests of the GraphQL API?
- Add description comments to the GraphQL schema to document the types, properties, queries, and mutations (so that they show up in GraphiQL, for example). Descriptions support Markdown, [right](https://spec.graphql.org/June2018/#sec-Descriptions)?
- Does Neo4j support full text search indexes? [Looks like yes](https://neo4j.com/developer/kb/fulltext-search-in-neo4j/)?
- Does Neo4j support publishing a stream of change events? [Looks like yes](https://neo4j.com/docs/labs/neo4j-streams/current/overview/).
- Rather than turning on low-level Bolt chatter to log the Cypher queries, in the GraphQL controller we could log the query since we have to get it as a string there anyway. If we do this, we could add a Spring config property to turn this logging on and off.
- The neo4j-graphql-java dependency doesn't seem to provide a source attachment in the Maven central repo. Is there a way to fix this somehow?
  - Actually I think this isn't true. There's just some weirdness with IDEA attaching the Kotlin source in the debugger. The debugger shows the source files for some classes and decompiled code for others, but you can clearly find the source for everything if you browse the jars in the project window. 

## Research avenues to explore
[graphql-spqr](https://github.com/leangen/GraphQL-SPQR) may be worth investigating for those with an already-existing Java model that they want to turn into a GraphQL API. It aims to solve the problem of these two things getting out of sync.

### GRANDstack

- Try a pure GRANDstack implementation (meaning Apollo Server as the back end rather than Java))
  - Is it easier to do end-to-end rapid prototyping using a pure GRANDstack or one using Java as the service layer?
- TODO: Subscriptions
- TODO: Caching
- TODO: Large data set vs query performance
