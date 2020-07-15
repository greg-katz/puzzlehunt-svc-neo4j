package com.horshers.puzzlehunt.ogm.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OgmSessionFactoryConfig {

  @Bean
  SessionFactory sessionFactory() {
    org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
        .uri("bolt://localhost:7687")
        .credentials("neo4j", "password")
        .build();

    return new SessionFactory(configuration, "com.horshers.puzzlehuntsvcneo4j.model.ogm");
  }
}
