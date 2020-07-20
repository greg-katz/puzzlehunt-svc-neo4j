package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;

import java.time.LocalTime;

@RelationshipEntity(type = "SOLVED")
@Data
public class TeamSolvedPuzzle {

  LocalTime start;
  LocalTime end;

  @EndNode
  Puzzle puzzle;
}
