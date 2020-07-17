package com.horshers.puzzlehuntdriver.model;

import lombok.Data;

import java.util.List;

@Data
public class Leaderboard {
  private List<TeamResult> teamResults;
}
