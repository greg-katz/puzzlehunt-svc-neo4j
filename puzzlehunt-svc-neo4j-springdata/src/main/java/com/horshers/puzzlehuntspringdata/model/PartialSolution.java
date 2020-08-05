package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class PartialSolution extends Entity {
  // TODO: A better name for the solution field is "guess"
  private String solution;
  private String response;
}