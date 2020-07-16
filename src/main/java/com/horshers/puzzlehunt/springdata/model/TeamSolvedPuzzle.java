package com.horshers.puzzlehunt.springdata.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.Instant;

@RelationshipEntity(type = "SOLVED")
public class TeamSolvedPuzzle {

  @StartNode
  Team team;

  @EndNode
  Puzzle puzzle;

  Instant start;
  Instant end;
}
