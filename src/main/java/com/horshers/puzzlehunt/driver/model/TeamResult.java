package com.horshers.puzzlehunt.driver.model;

import lombok.Data;

@Data
public class TeamResult {
  private String name;
  private boolean finished;
  private int score;
  private String time;
}
