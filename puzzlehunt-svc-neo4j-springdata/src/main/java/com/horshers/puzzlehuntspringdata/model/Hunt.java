package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZonedDateTime;
import java.util.List;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class Hunt extends Entity {
  private String name;
  // TODO: We originally designed the API to use Instant, but the default conversion uses ZonedDateTime. Which do we
  // want to use?
  private ZonedDateTime start;
  private ZonedDateTime end;

  @Relationship("HAS")
  List<Puzzle> puzzles;

  @Relationship("PLAYED")
  List<Team> teams;
}
