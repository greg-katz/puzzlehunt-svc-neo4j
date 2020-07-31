package com.horshers.puzzlehuntgraphql.config;

import com.google.common.io.Resources;
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
      queryDataFetchers.put(queryType.getName(), cypherQueryDataFetcher());
    }

    Map<String, DataFetcher> mutationDataFetchers = new HashMap<>();
    for (GraphQLType mutationType : neoGeneratedGraphQLSchema.getMutationType().getChildren()) {
      mutationDataFetchers.put(mutationType.getName(), cypherQueryDataFetcher());
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
  DataFetcher cypherQueryDataFetcher() {
    return dataFetchingEnvironment -> {
      Map<String, Cypher> cypherMap = dataFetchingEnvironment.<Map<String, Cypher>>getContext();
      String cypherKey = dataFetchingEnvironment.getExecutionStepInfo().getPath().getSegmentName();
      Cypher cypher = cypherMap.get(cypherKey);
      Map<String, Object> paramMap = mergeRequestAndCypherParams(dataFetchingEnvironment.getArguments(), cypher);

      try (Session session = driver().session()) {
        Result result = session.run(cypher.getQuery(), paramMap);
        return extractResultData(result, cypher);
      }
    };
  }

  @Bean
  DataFetcher huntNameDataFetcher() {
    return dataFetchingEnvironment -> {
      String name = dataFetchingEnvironment.<Map<String, Object>>getSource().get("name").toString();
      return name.toLowerCase();
    };
  }

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

  Object extractResultData(Result result, Cypher query) {
    Map<String, Object> resultMap = new HashMap<>();

    GraphQLConfig.ResultType resultType = getResultType(query.getType());

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
        if (resultType == GraphQLConfig.ResultType.SINGLE) {
          resultMap.put(matcher.group(1), new HashMap<>());
        }
        else if (resultType == GraphQLConfig.ResultType.LIST) {
          resultMap.put(matcher.group(1), new ArrayList<>());
        }
      }
    }
    else if (resultType == GraphQLConfig.ResultType.SINGLE) {
      resultMap.putAll(result.single().asMap());
    }
    else if (resultType == GraphQLConfig.ResultType.LIST) {
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
    return resultMap.entrySet().stream().findFirst().orElseThrow().getValue();
  }

  private GraphQLConfig.ResultType getResultType(GraphQLType type) {
    // The type field in the Cypher object is nullable and defaults to null, which implies that there may be a case
    // where we'd expect to get a null type here. We aren't sure what that case actually is in practice though, so
    // although we defined this NONE value we don't really know how to deal with a NONE in the code above. Seems like
    // something we may be able to figure out if and when we stumble across an example.
    if (type == null) {
      return GraphQLConfig.ResultType.NONE;
    }
    else if (type instanceof GraphQLList) {
      return GraphQLConfig.ResultType.LIST;
    }
    else if (type instanceof GraphQLObjectType) {
      return GraphQLConfig.ResultType.SINGLE;
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
