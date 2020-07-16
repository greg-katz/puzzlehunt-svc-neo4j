package com.horshers.puzzlehunt.springdata.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration("spring-data--ogm-config")
@EnableNeo4jRepositories(basePackages = "com.horshers.puzzlehunt.springdata.repo")
public class OgmSessionFactoryConfig {

  @Bean
  SessionFactory sessionFactory() {
    org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
        .uri("bolt://localhost:7687")
        .credentials("neo4j", "password")
        .build();

    return new SessionFactory(configuration, "com.horshers.puzzlehunt.springdata");
  }
}
