package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZonedDateTime;
import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

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

  @Relationship(value = "PLAYED", direction = INCOMING)
  List<Team> teams;
}
