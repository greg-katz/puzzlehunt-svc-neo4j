package com.horshers.puzzlehuntspringdata.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration("spring-data-ogm-config")
@EnableNeo4jRepositories(basePackages = "com.horshers.puzzlehuntspringdata.repo")
public class OgmSessionFactoryConfig {

  @Bean
  SessionFactory sessionFactory() {
    org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
      .uri("bolt://localhost:7687")
      .credentials("neo4j", "password")
      .useNativeTypes()
      .build();

    return new SessionFactory(configuration, "com.horshers.puzzlehuntspringdata", "com.horshers.puzzlehuntogm");
  }
}
