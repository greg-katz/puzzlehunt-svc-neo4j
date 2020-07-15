package com.horshers.puzzlehunt.driver.model;

import lombok.Data;

import java.util.List;

@Data
public class Leaderboard {
  private List<TeamResult> teamResults;
}
