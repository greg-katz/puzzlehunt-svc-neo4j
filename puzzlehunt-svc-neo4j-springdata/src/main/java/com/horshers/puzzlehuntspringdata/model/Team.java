package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class Team extends Entity {

  private String name;

  @Relationship(type = "CAPTAIN_OF", direction = INCOMING)
  private Person captain;

  @Relationship(type = "MEMBER_OF", direction = INCOMING)
  private List<Person> players;

  @Relationship("SOLVED")
  private List<TeamSolvedPuzzle> teamSolvedPuzzles;

  public void addSolvedPuzzle(TeamSolvedPuzzle solvedPuzzle) {
    if (teamSolvedPuzzles == null) teamSolvedPuzzles = new ArrayList<>();
    teamSolvedPuzzles.add(solvedPuzzle);
  }

  public Optional<TeamSolvedPuzzle> findSolvedPuzzle(UUID puzzleId) {
    if (teamSolvedPuzzles == null) return Optional.empty();

    return teamSolvedPuzzles.stream()
      .filter(tsp -> tsp.getPuzzle().getUuid().equals(puzzleId))
      .findFirst();
  }
}