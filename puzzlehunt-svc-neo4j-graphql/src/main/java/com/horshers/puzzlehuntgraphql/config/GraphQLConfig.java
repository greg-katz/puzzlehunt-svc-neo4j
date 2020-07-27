package com.horshers.puzzlehuntgraphql.config;

import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.URL;

import static kotlin.text.Charsets.UTF_8;

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
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }
}
