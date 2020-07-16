package com.horshers.puzzlehunt.ogm.config;

import org.springframework.context.annotation.Configuration;

@Configuration("ogm-ogm-config")
public class OgmSessionFactoryConfig {

/*
 This conflicts with the one in spring data.
 @Bean
  SessionFactory sessionFactory() {
    org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
        .uri("bolt://localhost:7687")
        .credentials("neo4j", "password")
        .build();

    return new SessionFactory(configuration, "com.horshers.puzzlehunt.ogm");
  }*/
}
