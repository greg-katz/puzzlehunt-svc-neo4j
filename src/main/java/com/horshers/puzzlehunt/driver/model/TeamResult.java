package com.horshers.puzzlehunt.driver.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.UuidStrategy;

@Data
public class TeamResult {
  @Id @GeneratedValue(strategy = UuidStrategy.class)
  long id;

  private String name;
  private boolean finished;
  private int score;
  private String time;
}
