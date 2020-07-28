package com.horshers.puzzlehuntgraphql.controller;

import com.horshers.puzzlehuntgraphql.model.GraphQLRequest;
import graphql.GraphQL;
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

import java.util.HashMap;
import java.util.Map;

@RestController("neo-graphql-controller")
public class GraphQLController {

  @Autowired
  Translator translator;

  @Autowired
  GraphQL graphQL;

  @Autowired
  @Qualifier("graphql-driver")
  Driver driver;

  @PostMapping("/graphql")
  @SneakyThrows
  public Map<String, Object> graphQLAPI(@RequestBody GraphQLRequest request) {

    if (request.getQuery().contains("__schema")) {
      Map schemaResponse = new HashMap();
        schemaResponse.put("data", graphQL.execute(request.getQuery()).getData());
      return schemaResponse;
    }

    Cypher cypher = translator.translate(request.getQuery()).get(0);

    // The generated cypher queries will use variables instead of literals even for values that are literal in the
    // graphQL query. When it does this the variables it generated are in the cypher object that comes back from the
    // translator. We need to make sure when we run the cypher query we have all the variables that the translator
    // defined AND all the ones that were specified in the graphQL request itself.
    Map<String, Object> cypherVariables = new HashMap<>(cypher.getParams());
    if (request.getVariables() != null) {
      cypherVariables.putAll(request.getVariables());
    }

    String cypherString = cypher.getQuery();

    try (Session session = driver.session()) {

      Result result = session.run(cypherString, cypherVariables);

      Record record = result.single();
      return record.asMap();
    }
  }
}
