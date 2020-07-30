package com.horshers.puzzlehuntgraphql.controller;

import com.horshers.puzzlehuntgraphql.model.GraphQLRequest;
import graphql.GraphQL;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    // To enable its autocomplete feature, GraphiQL requests the schema definition from the server. It turns out that
    //  the GraphQL object can fulfill this request via its execute method.
    if (request.getQuery().contains("__schema") || request.getQuery().contains("__type")) {
      return Map.of("data", graphQL.execute(request.getQuery()).getData());
    }

    // Amazing magic occurs here! The GraphQL query is translated into a single Cypher query, which means that instead
    // of having to make a lot of database queries to fetch individual node properties, we can execute a single query
    // in a single round trip. Bow down before the mighty Translator.
    List<Cypher> queries = translator.translate(request.getQuery());

    Map<String, Object> dataMap = new HashMap<>();
    try (Session session = driver.session()) {
      for (Cypher cypher : queries) {
        // The generated cypher queries will use variables instead of literals even for values that are literal in the
        // graphQL query. When it does this the variables it generated are in the cypher object that comes back from the
        // translator. We need to make sure when we run the cypher query we have all the variables that the translator
        // defined AND all the ones that were specified in the graphQL request itself.
        ResultType resultType = getResultType(cypher.getType());
        Map<String, Object> cypherVariables = new HashMap<>(cypher.getParams());
        if (request.getVariables() != null) {
          cypherVariables.putAll(request.getVariables());
        }

        String cypherString = cypher.getQuery();

        Result result = session.run(cypherString, cypherVariables);
        if (!result.hasNext()) {
          Pattern pattern = Pattern.compile("^.*\\s+AS\\s+(.*)\\s*$");
          Matcher matcher = pattern.matcher(cypherString);
          if (matcher.matches()) {
            if (resultType == ResultType.SINGLE) {
              dataMap.put(matcher.group(1), new HashMap<>());
            }
            else if (resultType == ResultType.LIST) {
              dataMap.put(matcher.group(1), new ArrayList<>());
            }
          }
        }
        else if (resultType == ResultType.SINGLE) {
          dataMap.putAll(result.single().asMap());
        }
        else if (resultType == ResultType.LIST) {
          Map<String, Object> listResultMap = new HashMap<>();
          List<Object> listResult = new ArrayList<>();
          while (result.hasNext()) {
            Map<String, Object> currentMap = result.next().asMap();
            String key = currentMap.keySet().stream().findFirst().get();
            if (!listResultMap.containsKey(key)) {
              listResultMap.put(key, listResult);
            }
            listResult.add(currentMap.get(key));
          }
          dataMap.putAll(listResultMap);
        }
      }
    }
    /*
    If there are multiple queries in the same batch:
      * Generate names for each query based on their return value (unless they already have a name).
      * Add each query result to the data map under a key of its name

    For each query:
      * If its return type is a list, wrap it with a list even if it only returns a single match
      * If its return type is not a list, just add it to the result as-is.
   */

    return Map.of("data", dataMap);
  }

  ResultType getResultType(GraphQLType type) {
    if (type == null) {
      return ResultType.NONE;
    }
    else if (type instanceof GraphQLList) {
      return ResultType.LIST;
    }
    else if (type instanceof GraphQLObjectType) {
      return ResultType.SINGLE;
    }
    else {
      return getResultType(type.getChildren().get(0));
    }
  }

  enum ResultType {
    NONE,
    SINGLE,
    LIST
  }
}
