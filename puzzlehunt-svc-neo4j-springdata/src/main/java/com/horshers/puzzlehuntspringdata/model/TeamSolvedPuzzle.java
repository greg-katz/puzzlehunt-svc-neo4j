package com.horshers.puzzlehuntspringdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalTime;

@RelationshipEntity(type = "SOLVED")
@Data
public class TeamSolvedPuzzle extends Entity {

  LocalTime start;
  LocalTime end;

  // Relationship entities are required to have both @StartNode and @EndNode fields, but this causes a cycle in the
  // object graph because Team refers to TeamSolvedPuzzle objects. So, we have to break the cycle as far as Lombok
  // and Jackson are concerned.
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  @StartNode
  Team team;

  @EndNode
  Puzzle puzzle;
}
