package com.horshers.puzzlehunt.springdata.model;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.Instant;
import java.util.List;

@NodeEntity
@Data
public class Hunt extends Entity {
  String name;
  Instant start;
  Instant end;

  List<Puzzle> puzzles;
  List<Team> teams;
}
