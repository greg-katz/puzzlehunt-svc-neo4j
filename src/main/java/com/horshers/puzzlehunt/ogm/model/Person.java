package com.horshers.puzzlehunt.ogm.model;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Person")
@Data
public class Person { // extends Entity {

  @Id
  private String uuid;

  private String name;

  @Relationship("MEMBER_OF")
  private Team team;
}
