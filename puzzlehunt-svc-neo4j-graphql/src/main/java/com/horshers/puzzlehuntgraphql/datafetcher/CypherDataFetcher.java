package com.horshers.puzzlehuntgraphql.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphql.Cypher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data fetcher that resolves a property by generating a cypher query using the neo4j-graphql-java library.
 *
 * This is actually pretty cool but it takes some explaining. Let me tell you a story...
 *
 * Originally
 */
@Component
public class CypherDataFetcher implements DataFetcher<Object> {

  @Autowired
  @Qualifier("graphql-driver")
  Driver neoDriver;

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
    Map<String, Cypher> cypherMap = dataFetchingEnvironment.<Map<String, Cypher>>getContext();
    String cypherKey = dataFetchingEnvironment.getExecutionStepInfo().getPath().getSegmentName();
    Cypher cypher = cypherMap.get(cypherKey);
    Map<String, Object> paramMap = mergeRequestAndCypherParams(dataFetchingEnvironment.getArguments(), cypher);

    try (Session session = neoDriver.session()) {
      Result result = session.run(cypher.getQuery(), paramMap);
      return extractResultData(result, cypher);
    }
  };

  Map<String, Object> mergeRequestAndCypherParams(Map<String, Object> requestParams, Cypher query) {
    // The generated cypher queries will use variables instead of literals even for values that are literal in the
    // graphQL query. When it does this the variables it generated are in the cypher object that comes back from the
    // translator. We need to make sure when we run the cypher query we have all the variables that the translator
    // defined AND all the ones that were specified in the graphQL request itself.
    Map<String, Object> queryVariables = new HashMap<>(query.getParams());
    if (requestParams != null) {
      queryVariables.putAll(requestParams);
    }
    return queryVariables;
  }

  @SneakyThrows
  Object extractResultData(Result result, Cypher query) {
    ResultType resultType = getResultType(query.getType());

    if (!result.hasNext()) {
      if (resultType == ResultType.LIST) {
        return new ArrayList<>();
      }
      else {
        return new HashMap<>();
      }
    }
    else if (resultType == ResultType.SINGLE) {
      return result.single().asMap().entrySet().stream().findFirst().orElseThrow().getValue();
    }
    else if (resultType == ResultType.LIST) {
      List<Object> listResult = new ArrayList<>();
      while (result.hasNext()) {
        Map<String, Object> currentMap = result.next().asMap();
        // We *think* all non-empty responses will be a map with a single key. When there are multiple results each call
        // to result.next().asMap() will produce a map with the same key name mapped to a different object. We need to
        // transform the result a bit by adding all of the result objects into a single list and then associating it
        // with the key name.
        String key = currentMap.keySet().stream().findFirst().orElseThrow();

        listResult.add(currentMap.get(key));
      }
      return listResult;
    }
    else {
      return null;
    }
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
