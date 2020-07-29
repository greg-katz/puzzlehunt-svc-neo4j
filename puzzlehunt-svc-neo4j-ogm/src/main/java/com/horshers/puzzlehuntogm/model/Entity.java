package com.horshers.puzzlehuntogm.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.util.UUID;

@Data
public abstract class Entity {

  @Id
  @GeneratedValue(strategy = UuidStrategy.class)
  @Convert(UuidStringConverter.class)
  private UUID id;
}
