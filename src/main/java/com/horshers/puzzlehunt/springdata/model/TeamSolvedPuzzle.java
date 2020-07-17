package com.horshers.puzzlehunt.springdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalTime;

@RelationshipEntity(type = "SOLVED")
@Data
public class TeamSolvedPuzzle {

  @JsonIgnore
  @StartNode
  Team team;

  @JsonIgnore
  @EndNode
  Puzzle puzzle;

  LocalTime start;
  LocalTime end;
}
