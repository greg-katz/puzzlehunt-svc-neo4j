package com.horshers.puzzlehunt.ogm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Data
public class Person extends Entity {

  private String name;

  @ToString.Exclude
  @JsonIgnore
  @Relationship("MEMBER_OF")
  private Team team;
}
