package com.horshers.puzzlehuntogm.model;

import lombok.Data;
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
}
