package com.horshers.puzzlehuntdriver.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NeoDriverConfig {

  @Bean
  Driver driver() {
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
  }
}
