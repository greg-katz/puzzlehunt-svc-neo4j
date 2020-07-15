package com.horshers.puzzlehuntsvcneo4j.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class Team {
  String name;

  @Relationship(type = "MEMBER_OF", direction = INCOMING)
  List<Person> players;
}
