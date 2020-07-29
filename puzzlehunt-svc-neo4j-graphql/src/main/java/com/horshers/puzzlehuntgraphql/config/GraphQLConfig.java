package com.horshers.puzzlehuntgraphql.config;

import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import lombok.SneakyThrows;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class GraphQLConfig {

  private String schema;

  @PostConstruct
  @SneakyThrows
  public void init() {
    URL url = Resources.getResource("schema.graphql");
    schema = Resources.toString(url, UTF_8);
  }

  @Bean
  Translator translator() {
    return new Translator(SchemaBuilder.buildSchema(schema));
  }

  @Bean("graphql-driver")
  Driver driver() {
    Config config = Config.builder().withLogging(Logging.slf4j()).build();
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"), config);
  }

  //--------------------------------------

  @Bean
  GraphQL graphQL() {
    GraphQLSchema graphQLSchema = SchemaBuilder.buildSchema(schema);
    return GraphQL.newGraphQL(graphQLSchema).build();
  }
}
