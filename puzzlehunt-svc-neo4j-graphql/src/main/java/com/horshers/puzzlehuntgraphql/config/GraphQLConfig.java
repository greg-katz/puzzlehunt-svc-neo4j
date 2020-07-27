package com.horshers.puzzlehuntgraphql.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

  String schema = """
                  type Query {
                    hunts: [Hunt]
                  }
                  type Hunt {
                    uuid: ID!
                    name: String!
                  }
                  """;

  @Bean
  Translator translator() {
    return new Translator(SchemaBuilder.buildSchema(schema));
  }

  @Bean("graphql-driver")
  Driver driver() {
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }
}
