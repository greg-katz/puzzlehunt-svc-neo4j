package com.horshers.puzzlehunt.springdata.model;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.Instant;
import java.util.List;

@NodeEntity
@Data
public class Hunt extends Entity {
  String name;
  Instant start;
  Instant end;

  @Relationship("HAS")
  List<Puzzle> puzzles;

  @Relationship("PLAYED")
  List<Team> teams;
}
