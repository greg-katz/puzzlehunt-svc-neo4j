package com.horshers.puzzlehuntgraphql.controller;

import com.horshers.puzzlehuntgraphql.model.GraphQLRequest;
import graphql.ExecutionInput;
import graphql.GraphQL;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController("neo-graphql-controller-using-graphql-java")
public class GraphQLControllerUsingGraphQLJava {

  @Autowired
  GraphQL graphQL;

  @Autowired
  Translator translator;

  @Autowired
  @Qualifier("graphql-driver")
  Driver driver;

  @PostMapping("/graphql")
  @SneakyThrows
  public Map<String, Object> graphQLAPI(@RequestBody GraphQLRequest request) {

    // To enable its autocomplete feature, GraphiQL requests schema details from the server. It turns out that the
    // GraphQL object can fulfill this type of request via its execute method.
    if (request.getQuery().contains("__schema") || request.getQuery().contains("__type")) {
      return Map.of("data", graphQL.execute(request.getQuery()).getData());
    }

    List<Cypher> queries = translator.translate(request.getQuery());

    Map<String, Cypher> context = new HashMap<>();
    for (Cypher query : queries) {
      Pattern pattern = Pattern.compile("(?s)^.*\\s+AS\\s+(.*)\\s*$");
      Matcher matcher = pattern.matcher(query.getQuery());
      if (matcher.matches()) {
        context.put(matcher.group(1), query);
      }
    }

    return Map.of("data", graphQL.execute(ExecutionInput.newExecutionInput()
                                              .query(request.getQuery())
                                              .context(context))
                                              .getData());
  }
}
