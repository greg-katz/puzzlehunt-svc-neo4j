package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class Puzzle extends Entity {

  String name;
  String answer;
  int par;
  int points;

  @Relationship("HAS")
  List<Hint> hints;

  @Relationship("HAS")
  List<PartialSolution> partialSolutions;
}
