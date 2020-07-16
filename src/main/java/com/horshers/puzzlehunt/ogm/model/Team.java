package com.horshers.puzzlehunt.ogm.model;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
@Data
public class Team extends Entity {

  String name;

  @Relationship(type = "MEMBER_OF", direction = INCOMING)
  List<Person> players;
}
