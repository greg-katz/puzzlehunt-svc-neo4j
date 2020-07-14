package com.horshers.puzzlehuntsvcneo4j.model;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class TeamResult {
  private String name;
  private boolean finished;
  private int score;
  private Duration duration;
}
