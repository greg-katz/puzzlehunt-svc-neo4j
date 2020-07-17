package com.horshers.puzzlehuntspringdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class PartialSolution extends Entity {

  private String solution;
  private String response;

  @ToString.Exclude
  @JsonIgnore
  @Relationship(type = "HAS", direction = INCOMING)
  private Puzzle puzzle;
}