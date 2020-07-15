package com.horshers.puzzlehuntsvcneo4j.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.UuidStrategy;

import java.time.Duration;

@Data
public class TeamResult {
  @Id @GeneratedValue(strategy = UuidStrategy.class)
  long id;

  private String name;
  private boolean finished;
  private int score;
  private String time;
}
