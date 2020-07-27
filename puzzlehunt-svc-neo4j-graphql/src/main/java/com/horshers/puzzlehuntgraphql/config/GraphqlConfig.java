package com.horshers.puzzlehuntgraphql.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import org.neo4j.graphql.SchemaBuilder;
import lombok.SneakyThrows;
import org.neo4j.driver.Record;

@Configuration
public class GraphqlConfig {

  String schema = """
                  type Query {
                    hunts: [Hunt]
                  }
                  type Hunt {
                    uuid: ID!
                    name: String!
                  }
                  """;

  String query = """
                 {
                   hunts {
                     uuid
                     name
                   }
                 }
                 """;

  @PostConstruct
  @SneakyThrows
  public void init() {
    Translator translator = new Translator(SchemaBuilder.buildSchema(schema));
    Cypher cypher = translator.translate(query).get(0);
    String cypherString = cypher.getQuery();

    Driver driver = driver();

    try (Session session = driver.session()) {

      Result result = session.run(cypherString);
      Record record = result.single();
      System.out.println(record.get("hunts"));
    }
  }

  @Bean("graphql-driver")
  Driver driver() {
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }
}
