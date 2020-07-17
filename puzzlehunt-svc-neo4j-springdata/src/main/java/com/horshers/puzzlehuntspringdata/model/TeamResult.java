package com.horshers.puzzlehuntspringdata.model;

import lombok.Data;
import org.neo4j.driver.types.IsoDuration;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@QueryResult
public class TeamResult {
  private String name;
  private boolean finished;
  private int score;
  // TODO: This is supposed to be a formatted duration
  private IsoDuration time;
}