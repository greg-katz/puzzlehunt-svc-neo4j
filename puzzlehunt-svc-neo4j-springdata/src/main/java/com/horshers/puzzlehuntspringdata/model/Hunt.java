package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class Hunt extends Entity {
  private UUID uuid;
  private String name;
  private Instant start;
  private Instant end;

  @Relationship("HAS")
  List<Puzzle> puzzles;

  @Relationship("PLAYED")
  List<Team> teams;
}
