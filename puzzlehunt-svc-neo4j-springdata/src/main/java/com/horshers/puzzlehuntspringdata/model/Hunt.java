package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
  private List<Puzzle> puzzles;

  @Relationship(value = "PLAYED", direction = INCOMING)
  private List<Team> teams;

  public void addTeam(Team team) {
    if (teams == null) teams = new ArrayList<>();
    teams.add(team);
  }

  public Optional<Team> findTeam(Team team) {
    if (teams == null || team == null) return Optional.empty();

    return teams.stream()
      .filter(t -> t.getUuid().equals(team.getUuid()))
      .findFirst();
  }
}