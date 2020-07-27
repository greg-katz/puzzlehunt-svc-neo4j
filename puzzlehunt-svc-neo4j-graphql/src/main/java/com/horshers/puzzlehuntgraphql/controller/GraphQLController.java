package com.horshers.puzzlehuntgraphql.controller;

import com.horshers.puzzlehuntgraphql.model.GraphQLRequest;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GraphQLController {

  @Autowired
  Translator translator;

  @Autowired
  @Qualifier("graphql-driver")
  Driver driver;

  @PostMapping("/graphql")
  @SneakyThrows
  public Map<String, Object> graphQLAPI(@RequestBody GraphQLRequest request) {

    Cypher cypher = translator.translate(request.getQuery()).get(0);
    String cypherString = cypher.getQuery();

    try (Session session = driver.session()) {

      Result result = session.run(cypherString, request.getVariables());
      Record record = result.single();
      return record.asMap();
    }
  }
}
