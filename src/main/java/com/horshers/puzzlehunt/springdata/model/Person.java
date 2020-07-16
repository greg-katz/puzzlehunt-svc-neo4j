package com.horshers.puzzlehunt.springdata.model;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Data
public class Person extends Entity {

  private String name;

  @Relationship("MEMBER_OF")
  private Team team;
}
