package com.horshers.puzzlehuntgraphql.config;

import com.google.common.io.Resources;
import com.horshers.puzzlehuntgraphql.datafetcher.CypherDataFetcher;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import lombok.SneakyThrows;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  @Autowired
  CypherDataFetcher cypherQueryDataFetcher;

  //--------------------------------------

  @Bean
  GraphQL graphQL() {
    GraphQLSchema graphQLSchema = buildSchema();
    return GraphQL.newGraphQL(graphQLSchema).build();
  }

  private GraphQLSchema buildSchema() {
    GraphQLSchema neoGeneratedGraphQLSchema = SchemaBuilder.buildSchema(schema);

    Map<String, DataFetcher> queryDataFetchers = new HashMap<>();
    for (GraphQLType queryType : neoGeneratedGraphQLSchema.getQueryType().getChildren()) {
      queryDataFetchers.put(queryType.getName(), cypherQueryDataFetcher);
    }

    Map<String, DataFetcher> mutationDataFetchers = new HashMap<>();
    for (GraphQLType mutationType : neoGeneratedGraphQLSchema.getMutationType().getChildren()) {
      mutationDataFetchers.put(mutationType.getName(), cypherQueryDataFetcher);
    }

    GraphQLCodeRegistry customCodeRegistry =
        GraphQLCodeRegistry.newCodeRegistry(neoGeneratedGraphQLSchema.getCodeRegistry())
            .dataFetchers("Query", queryDataFetchers)
            .dataFetchers("Mutation", mutationDataFetchers)
            .dataFetcher(FieldCoordinates.coordinates("Hunt", "name"), huntNameDataFetcher())
            .build();

    return GraphQLSchema.newSchema(neoGeneratedGraphQLSchema).codeRegistry(customCodeRegistry).build();
  }

  @Bean
  DataFetcher huntNameDataFetcher() {
    return dataFetchingEnvironment -> {
      String name = dataFetchingEnvironment.<Map<String, Object>>getSource().get("name").toString();
      return name.toLowerCase();
    };
  }
}
