package com.horshers.puzzlehunt.graphqljava.config;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GraphQLConfig {

  @Bean("graphql-driver")
  Driver driver() {
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }

  String schemaString = """
                  type Query {
                    hunts: [Hunt]
                  }
                  type Hunt {
                    uuid: ID!
                    name: String!
                  }
                  """;

  @Bean
  GraphQL graphQL() {
    GraphQLSchema graphQLSchema = buildSchema();
    return GraphQL.newGraphQL(graphQLSchema).build();
  }

  @Bean
  DataFetcher huntsDataFetcher() {
    String query =
        """
          MATCH (hunt:Hunt)
          RETURN hunt.uuid as uuid, hunt.name as name
        """;

    return dataFetchingEnvironment -> {
      try (Session session = driver().session()) {
        Result result = session.run(query);
        List<Record> records = result.list();
        return records.stream().map(Record::asMap).collect(Collectors.toList());
      }
    };
  }

  private GraphQLSchema buildSchema() {
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaString);
    RuntimeWiring runtimeWiring = buildWiring();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
  }

  private RuntimeWiring buildWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .type(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher("hunts", huntsDataFetcher()))
        .build();
  }
}