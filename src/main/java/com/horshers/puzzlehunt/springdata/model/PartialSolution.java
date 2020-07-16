package com.horshers.puzzlehunt.springdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.horshers.puzzlehunt.driver.model.Puzzle;
import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.UUID;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@Data
public class PartialSolution {

  private UUID uuid;
  private String solution;
  private String response;

  @ToString.Exclude
  @JsonIgnore
  @Relationship(type = "HAS", direction = INCOMING)
  private Puzzle puzzle;
}