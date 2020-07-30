package com.horshers.puzzlehuntgraphql.controller;

import com.horshers.puzzlehuntgraphql.model.GraphQLRequest;
import graphql.GraphQL;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    // To enable its autocomplete feature, GraphiQL requests schema details from the server. It turns out that the
    // GraphQL object can fulfill this type of request via its execute method.
    if (request.getQuery().contains("__schema") || request.getQuery().contains("__type")) {
      return Map.of("data", graphQL.execute(request.getQuery()).getData());
    }

    // Amazing magic occurs here! Each GraphQL query (somewhat confusingly, the request.getQuery() string can represent
    // multiple queries to run in the same batch) is translated into a single Cypher query. This means that instead
    // of having to make a lot of database queries to fetch individual node properties, each top-level GraphQL query
    // only executes a one cypher query. Bow down before the mighty Translator.
    List<Cypher> queries = translator.translate(request.getQuery());

    return runCypherQueries(request, queries);
  }

  Map<String, Object> runCypherQueries(GraphQLRequest request, List<Cypher> queries) {
    Map<String, Object> dataMap = new LinkedHashMap<>();
    try (Session session = driver.session()) {
      for (Cypher query : queries) {
        Map<String, Object> queryVariables = mergeRequestAndCypherParams(request, query);

        String queryText = query.getQuery();
        Result result = session.run(queryText, queryVariables);

        dataMap.putAll(extractResultData(result, query));
      }
    }
    return Map.of("data", dataMap);
  }

  Map<String, Object> mergeRequestAndCypherParams(GraphQLRequest request, Cypher query) {
    // The generated cypher queries will use variables instead of literals even for values that are literal in the
    // graphQL query. When it does this the variables it generated are in the cypher object that comes back from the
    // translator. We need to make sure when we run the cypher query we have all the variables that the translator
    // defined AND all the ones that were specified in the graphQL request itself.
    Map<String, Object> queryVariables = new HashMap<>(query.getParams());
    if (request.getVariables() != null) {
      queryVariables.putAll(request.getVariables());
    }
    return queryVariables;
  }

  /**
   * This method is an attempt to get the resulting data format to look like it should based on the graphql response
   * spec here: http://spec.graphql.org/June2018/#sec-Response-Format
   *
   * We confirmed some of this functionality by comparing the behavior with the graphql-java-spring-webmvc library, but
   * our examples app for that library isn't built out enough to check everything.
   *
   * The result for each query gets nested under a single key for that query (or mutation). The key's value may be
   * specified in the request, but if not defaults to the name of the query root type. In addition, the type of the
   * query result is always the same as the query root type, so if the query root is a list type the result will
   * be a list even if there's only one element. e.g. the query:
   *
   *   puzzle {
   *     id
   *     name
   *   }
   *
   *   will produce a response with a "puzzle" key of type array because the root type in the schema of type [Puzzle]
   *
   *   "puzzle": [
   *     {
   *       ...properties
   *     }
   *   ]
   *
   * We think this code is conforming pretty well to the graphQL spec for its data return values (better than any
   * examples we've come across of a neo4j-graphql-java server endpoint, since we handle multiple queries in the same
   * request).
   *
   * But the graphql response spec also defines an "errors" return value for when something goes wrong, and we are not
   * doing that yet.
   */
  Map<String, Object> extractResultData(Result result, Cypher query) {
    Map<String, Object> resultMap = new HashMap<>();

    ResultType resultType = getResultType(query.getType());

    if (!result.hasNext()) {
      // It's possible that some queries won't have a result object (for example a query by ID for a nonexistent
      // entity). When that's the case we need to to a bit of extra work to get the correct key name to associate with
      // our empty result.
      // For non-empty results we simply count on the fact that the cypher translator is appending "AS <name>" to the
      // end of every generated query, which means when we turn the result into a map it's automatically keyed by the
      // correct name. When the result is empty, however, we can't ask the driver to turn the result into a map and have
      // to do it ourselves by parsing the appropriate name off of the end of the cypher query.
      Pattern pattern = Pattern.compile("^.*\\s+AS\\s+(.*)\\s*$");
      Matcher matcher = pattern.matcher(query.getQuery());
      if (matcher.matches()) {
        if (resultType == ResultType.SINGLE) {
          resultMap.put(matcher.group(1), new HashMap<>());
        }
        else if (resultType == ResultType.LIST) {
          resultMap.put(matcher.group(1), new ArrayList<>());
        }
      }
    }
    else if (resultType == ResultType.SINGLE) {
      resultMap.putAll(result.single().asMap());
    }
    else if (resultType == ResultType.LIST) {
      Map<String, Object> listResultMap = new HashMap<>();
      List<Object> listResult = new ArrayList<>();
      while (result.hasNext()) {
        Map<String, Object> currentMap = result.next().asMap();
        // We *think* all non-empty responses will be a map with a single key. When there are multiple results each call
        // to result.next().asMap() will produce a map with the same key name mapped to a different object. We need to
        // transform the result a bit by adding all of the result objects into a single list and then associating it
        // with the key name.
        String key = currentMap.keySet().stream().findFirst().orElseThrow();
        if (!listResultMap.containsKey(key)) {
          listResultMap.put(key, listResult);
        }
        listResult.add(currentMap.get(key));
      }
      resultMap.putAll(listResultMap);
    }
    return resultMap;
  }

  private ResultType getResultType(GraphQLType type) {
    // The type field in the Cypher object is nullable and defaults to null, which implies that there may be a case
    // where we'd expect to get a null type here. We aren't sure what that case actually is in practice though, so
    // although we defined this NONE value we don't really know how to deal with a NONE in the code above. Seems like
    // something we may be able to figure out if and when we stumble across an example.
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

  private enum ResultType {
    NONE,
    SINGLE,
    LIST
  }
}
