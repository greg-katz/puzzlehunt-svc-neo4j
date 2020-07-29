package com.horshers.puzzlehuntdriver.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NeoDriverConfig {

  @Bean("neo-driver")
  Driver driver() {
    Config config = Config.builder().withLogging(Logging.slf4j()).build();
    return GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"), config);
  }
}
