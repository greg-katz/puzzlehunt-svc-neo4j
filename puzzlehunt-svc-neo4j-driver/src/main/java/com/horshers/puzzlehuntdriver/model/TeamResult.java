package com.horshers.puzzlehuntdriver.model;

import lombok.Data;

@Data
public class TeamResult {
  private String name;
  private boolean finished;
  private int score;
  private String time;
}
