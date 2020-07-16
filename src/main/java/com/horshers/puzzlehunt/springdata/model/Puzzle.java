package com.horshers.puzzlehunt.springdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity
@Data
public class Puzzle extends Entity {
  String name;
  String answer;
  int par;
  int points;

  @ToString.Exclude
  @JsonIgnore
  @Relationship("HAS")
  Hunt hunt;
  List<Hint> hints;

  @ToString.Exclude
  @JsonIgnore
  @Relationship("HAS")
  List<PartialSolution> partialSolutions;

  @ToString.Exclude
  @JsonIgnore
  @Relationship(value = "SOLVED", direction = Relationship.INCOMING)
  List<TeamSolvedPuzzle> teamSolvedPuzzles;
}
