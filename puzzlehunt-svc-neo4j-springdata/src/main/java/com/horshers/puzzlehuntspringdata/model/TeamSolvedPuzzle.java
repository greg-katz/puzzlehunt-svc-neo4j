package com.horshers.puzzlehuntspringdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalTime;

@RelationshipEntity(type = "SOLVED")
@Data
public class TeamSolvedPuzzle {

  @ToString.Exclude
  @JsonIgnore
  @StartNode
  Team team;

  @ToString.Exclude
  @JsonIgnore
  @EndNode
  Puzzle puzzle;

  LocalTime start;
  LocalTime end;
}
