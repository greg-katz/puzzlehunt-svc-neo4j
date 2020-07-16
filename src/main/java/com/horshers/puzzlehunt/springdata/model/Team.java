package com.horshers.puzzlehunt.springdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = true)
public class Team extends Entity {

  String name;

  @Relationship(type = "MEMBER_OF", direction = INCOMING)
  List<Person> players;

  @ToString.Exclude
  @JsonIgnore
  @Relationship(type = "PLAYED", direction = INCOMING)
  Hunt hunt;

  @Relationship("SOLVED")
  List<TeamSolvedPuzzle> teamSolvedPuzzles;
}
