package com.horshers.puzzlehuntsvcneo4j.model.ogm;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.UuidStrategy;

import java.util.UUID;

public abstract class Entity {

  @Id @GeneratedValue(strategy = UuidStrategy.class)
  private UUID id;

  public UUID getId() {
    return id;
  }
}
