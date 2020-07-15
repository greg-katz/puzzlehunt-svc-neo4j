package com.horshers.puzzlehuntsvcneo4j.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Person {
  String name;

  @Relationship("MEMBER_OF")
  Team team;
}
